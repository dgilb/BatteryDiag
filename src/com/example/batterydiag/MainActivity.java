package com.example.batterydiag;

import java.text.NumberFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	static public final int BD_THREAD_BGCOLOR = 1;
	static public final int BD_THREAD_CURRENT_READER = 2;

	static public final int BD_MSG_UPDATE_BACKGROUND = 0;
	static public final int BD_MSG_UPDATE_CV_TEXT = 1;
	static public final int BD_MSG_STOP_SAMPLING = 2;

	static public final float MAX_PERTURBATION_FREQ = 2.000f;
	static public final float MIN_PERTURBATION_FREQ = 0.010f;

	public int bgcolor = 255;
	public int bgspeed = 156;

	private BackgroundShifter bs;
	private CurrentFileReader cr;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureBackgroundControl();
        updateUI.sendEmptyMessage(BD_MSG_UPDATE_BACKGROUND);
        updateUI.sendEmptyMessage(BD_MSG_UPDATE_CV_TEXT);

        try {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
        } catch (Throwable t) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onbsToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        
    	if (on) {
    		startSampling();
        } else {
        	stopSampling();
        }
    }

	@SuppressLint("HandlerLeak")
    Handler updateUI = new Handler() {
    	public void handleMessage(Message m) {
    		switch (m.what) {
    			case BD_MSG_UPDATE_BACKGROUND:
    				setBackgroundColor(bgcolor);

    				TextView textView = (TextView) findViewById(R.id.counterTextView);
        			textView.setText("Cycle Number: "+Integer.toString(m.arg1)+"/"+
        					Integer.toString(BackgroundShifter.NUM_CYCLES_PROCESSED)+
        					"\r\n"+"Background Color: "+Integer.toString(m.arg2));
    				break;
    			case BD_MSG_UPDATE_CV_TEXT:
    				TextView currentview=(TextView)findViewById(R.id.currentTextView);
    				currentview.setText("Current: "+Integer.toString(m.arg1/1000)+" mA");

    				TextView voltageview=(TextView)findViewById(R.id.voltagetextView);
    				voltageview.setText("Voltage: "+Integer.toString(m.arg2/1000)+" mV");
    				break;
    			case BD_MSG_STOP_SAMPLING:
    				setBackgroundColor(bgcolor);

    				ToggleButton bgToggleButton = (ToggleButton) findViewById(R.id.backgroundshiftToggleButton);
    				bgToggleButton.setChecked(false);

    				EditText bgEditText = (EditText) findViewById(R.id.bgspeedEditText);
    				bgEditText.setEnabled(true);
    				break;
    			default:
    				break;
    		}
    	}
    };

    public void setBackgroundColor(int col) {
    	RelativeLayout L = (RelativeLayout) this.findViewById(R.id.mainRelativeLayout);
    	L.setBackgroundColor(Color.argb(255, col, col, col));
    }
    
    void configureBackgroundControl() {
    	final EditText bgspeedTextBox = (EditText) findViewById(R.id.bgspeedEditText);

    	bgspeedTextBox.setEnabled(true);
    	
    	bgspeedTextBox.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	    			if (actionId == EditorInfo.IME_ACTION_DONE) {
	    				float freq = 0.0f;
	    				try {
	            			freq = Float.parseFloat(bgspeedTextBox.getText().toString());
	            			if ((freq > MAX_PERTURBATION_FREQ) || (freq < MIN_PERTURBATION_FREQ))
	            				throw new NumberFormatException();
	            		} catch (NumberFormatException err) {
	            			freq = (1000.0f/((float)(BackgroundShifter.NUM_STEPS_PER_CYCLE*bgspeed)));
	            			NumberFormat formatter = NumberFormat.getNumberInstance();
	            			formatter.setMinimumFractionDigits(2);
	            			formatter.setMaximumFractionDigits(2);
	            			bgspeedTextBox.setText(formatter.format(freq));
	            			return false;
	            		}

	    				bgspeed = (int)(1000.0f/((float)BackgroundShifter.NUM_STEPS_PER_CYCLE*freq));
	            		
	                	TextView bsSpeed = (TextView) findViewById(R.id.backgroundSpeedTextView);
	                	bsSpeed.setText(Integer.toString(bgspeed)+" ms "+Float.toString(freq)+" hz");
//	                	v.clearFocus();
	    			}
				return false;
			}
    	});

    	float freq = (1000.0f/((float)(BackgroundShifter.NUM_STEPS_PER_CYCLE*bgspeed)));
		NumberFormat formatter = NumberFormat.getNumberInstance();
		formatter.setMinimumFractionDigits(2);
		formatter.setMaximumFractionDigits(2);
		bgspeedTextBox.setText(formatter.format(freq));

    	TextView bsSpeed = (TextView) findViewById(R.id.backgroundSpeedTextView);
    	bsSpeed.setText(Integer.toString(bgspeed)+" ms "+formatter.format(freq)+" hz");

    	TextView bgFreqLabel = (TextView) findViewById(R.id.bgfreqlabelTextView);
    	bgFreqLabel.setText("Hz ("+Float.toString(MIN_PERTURBATION_FREQ)+"-"+
    					Float.toString(MAX_PERTURBATION_FREQ)+")");

    	ToggleButton bsButton = (ToggleButton) findViewById(R.id.backgroundshiftToggleButton);
    	bsButton.setChecked(false);
    }
    
    // Returns true if the indicated thread is running, otherwise false
    public boolean isRunning(int thread) {
    	switch (thread) {
    		case BD_THREAD_BGCOLOR:
    			if (bs == null)
    				return false;
    			if (bs.runloop == false)
    				return false;
    			break;
    		case BD_THREAD_CURRENT_READER:
    			if (cr == null)
    				return false;
    			if (cr.runloop == false)
    				return false;
    			break;
    		default:
    			return false;
    	}
    	return true;
    }

    public void startSampling() {
    	EditText bgEditText = (EditText) findViewById(R.id.bgspeedEditText);

    	bgEditText.setEnabled(false);
    	bs = new BackgroundShifter(this);
    	this.bs.runloop = true;
        this.bs.start();
        cr = new CurrentFileReader(this);
        this.cr.runloop=true;
        this.cr.start();            
    }

    // Should stop the running threads, whatever the current state is
    public void stopSampling() {
		bgcolor = BackgroundShifter.colarray_50ms[0];

		if (bs != null) {
    		bs.runloop = false;
    	}
    	if (cr != null) {
    		cr.runloop = false;
    	}

    	// Send a delayed message to be sure that the threads have time to stop.
    	updateUI.sendEmptyMessageDelayed(BD_MSG_STOP_SAMPLING, bgspeed*2);
    }
}
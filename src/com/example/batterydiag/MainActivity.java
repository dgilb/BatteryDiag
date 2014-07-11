package com.example.batterydiag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

	public int bgcolor;
	public int bgspeed;
	public int counter;
	
	private BackgroundShifter bs;
	private CurrentFileReader cr;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureBackgroundControl();
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
        
    	SeekBar B = (SeekBar) findViewById(R.id.backgroundspeedSeekBar);

    	if (on) {
        	B.setEnabled(true);
        	bs = new BackgroundShifter(this);
        	this.bs.runloop = true;
            this.bs.start();        	
            cr = new CurrentFileReader(this);
            this.cr.runloop=true;
            this.cr.start();
        } else {
        	B.setEnabled(false);
            this.bs.runloop = false;
            this.cr.runloop=false;
       }
    }
    
	@SuppressLint("HandlerLeak") 
    Handler updateUI = new Handler() {
    	public void handleMessage(Message m) {
			setBackgroundColor(bgcolor);
    		TextView textView = (TextView) findViewById(R.id.counterTextView);
    		textView.setText(Integer.toString(counter));
    		if ((m.arg1 != 0)&&(m.arg2!=0)) {
    			TextView currentview=(TextView)findViewById(R.id.currentTextView);
    			currentview.setText(Integer.toString(m.arg1));//+"  "+Integer.toString(m.arg2));
                TextView voltageview=(TextView)findViewById(R.id.voltagetextView);
                voltageview.setText(Integer.toString(m.arg2));

    		}
    	}
    };

    public void setBackgroundColor(int col) {
    	RelativeLayout L = (RelativeLayout) this.findViewById(R.id.mainRelativeLayout);
    	L.setBackgroundColor(Color.argb(255, col, col, col));
    }

    void configureBackgroundControl() {
    	final EditText bgspeedTextBox = (EditText) findViewById(R.id.bgspeedEditText);
        bgspeedTextBox.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
            	TextView bsspeedLabel = (TextView) findViewById(R.id.backgroundSpeedTextView);
            	if (hasFocus == false) {
            		try {
            			bgspeed = Integer.parseInt(bgspeedTextBox.getText().toString());
            		} catch (NumberFormatException err) { 
            			bgspeedTextBox.setText(Integer.toString(bgspeed));
            		} 
            		bsspeedLabel.setText(Integer.toString(bgspeed));
            	}
            }
        });

    	SeekBar B = (SeekBar) this.findViewById(R.id.backgroundspeedSeekBar);
    	B.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
    		@Override
    		public void onProgressChanged(SeekBar bgspeedSeekBar, int progress, boolean touch) {
    			bgspeed = progress+1;

    			TextView bsspeedLabel = (TextView) findViewById(R.id.backgroundSpeedTextView);
    			bsspeedLabel.setText((Integer.toString(progress))+" ms / "+Integer.toString(bgspeedSeekBar.getMax()));
    			
    		}

    		@Override
    		public void onStartTrackingTouch(SeekBar seekBar) {
    			// TODO Auto-generated method stub
    		}

    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar) {
    			// TODO Auto-generated method stub
    			}
    	});

    	B.setEnabled(false);
    	
    	
    	TextView bsSpeed = (TextView) findViewById(R.id.backgroundSpeedTextView);
    	bsSpeed.setText(Integer.toString((B.getProgress()+1))+" ms");
    	ToggleButton bsButton = (ToggleButton) findViewById(R.id.backgroundshiftToggleButton);
    	bsButton.setChecked(false);
    	
    	
    }
}
    
 

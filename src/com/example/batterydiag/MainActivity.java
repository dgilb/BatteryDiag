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
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity {

	public int bgcolor = 255;
	public int bgspeed = 15;
	public int counter = 0;
		
	private BackgroundShifter bs;
	private CurrentFileReader cr;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configureBackgroundControl();
        setBackgroundColor(bgcolor);
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
        
    	EditText bgEditText = (EditText) findViewById(R.id.bgspeedEditText);

    	if (on) {
        	bgEditText.setEnabled(true);
        	bs = new BackgroundShifter(this);
        	this.bs.runloop = true;
            this.bs.start();        	
            cr = new CurrentFileReader(this);
            this.cr.runloop=true;
            this.cr.start();
            
        } else {
        	bgEditText.setEnabled(false);
            this.bs.runloop = false;
            this.cr.runloop=false;
           
            counter=0;
       }
    }

	@SuppressLint("HandlerLeak") 
    Handler updateUI = new Handler() {
    	public void handleMessage(Message m) {
			setBackgroundColor(bgcolor);
    		TextView textView = (TextView) findViewById(R.id.counterTextView);
    		textView.setText(Integer.toString(counter));

    		if (m.what == 1) {
    			TextView currentview=(TextView)findViewById(R.id.currentTextView);
    			currentview.setText(Integer.toString(m.arg1));
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
            	if (hasFocus == true) {
            		bgspeedTextBox.setText("");
            	}
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

    	bgspeedTextBox.setText(Integer.toString(bgspeed));

    	TextView bsSpeed = (TextView) findViewById(R.id.backgroundSpeedTextView);
    	bsSpeed.setText(Integer.toString(bgspeed)+" ms");

    	ToggleButton bsButton = (ToggleButton) findViewById(R.id.backgroundshiftToggleButton);
    	bsButton.setChecked(false);
    }
}
    
 

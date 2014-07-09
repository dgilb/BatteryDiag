package com.example.batterydiag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

	public int color;
	public int counter;
	
	private BackgroundShifter bs = new BackgroundShifter(this);
	private MainLoop loop = new MainLoop(this);
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.loop.start();
        this.bs.start();
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

    @SuppressLint("HandlerLeak") 
    Handler updateUI = new Handler() {
    	public void handleMessage(Message m) {
			setBackgroundColor(color);
    		TextView textView = (TextView) findViewById(R.id.counterTextView);
    		textView.setText(Integer.toString(counter));
    	}
    };

    void setBackgroundColor(int col) {
    	RelativeLayout L = (RelativeLayout) this.findViewById(R.id.mainRelativeLayout);
    	L.setBackgroundColor(Color.argb(255, col, col, col));
    }

}

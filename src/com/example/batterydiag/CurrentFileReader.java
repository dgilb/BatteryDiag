package com.example.batterydiag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.os.Message;

public class CurrentFileReader extends Thread {
	MainActivity con;
	public boolean runloop = true;
	
	public CurrentFileReader(MainActivity a) {
		this.con = a;
	}
	
	public void run(){
		StringBuilder text= new StringBuilder();

		while (runloop) {
			File f = new File("/sys/class/power_supply/battery/current_now");
			File f2 = new File("/sys/class/power_supply/battery/voltage_now");

			if(f.exists() && f2.exists()) {
				try {
					BufferedReader br = new BufferedReader(new FileReader(f));
					BufferedReader br2 = new BufferedReader(new FileReader(f2));
					String currentline = br.readLine();
					String voltageline = br2.readLine();

					if ((currentline != null) && (voltageline != null)) {
						text.append(currentline+" "+voltageline);
						text.append('\n'); 
	
						Message m = con.updateUI.obtainMessage();
						m.what = 1;  // specify message 1 in handler to indicate updating text fields
						m.arg1 = Integer.parseInt(currentline);
						m.arg2 = Integer.parseInt(voltageline);
						con.updateUI.sendMessage(m);
					}

					br.close();
					br2.close();
				} catch (IOException e) {
					// error handling
				}
			}

			//if (con.counter<=1024){
			//	con.counter++;
			//
			//	}
			//else if (con.counter>1024){
			//	con.counter=0;
			//	Message m=con.updateUI.obtainMessage(1,1,1,text.toString());
			//	con.updateUI.sendMessage(m);
			//	text.setLength(0);}
			try {
				CurrentFileReader.sleep(100);
			}
			catch (InterruptedException err) {
				err.printStackTrace();
			}
		}
		return;
	}
}



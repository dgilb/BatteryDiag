package com.example.batterydiag;

public class BackgroundShifter extends Thread {
	
	MainActivity con;

	boolean up = true;

	public BackgroundShifter(MainActivity a) {
		this.con = a;
	}

	public void run() {
		for (;;) {
			if (up == true)
				con.color++;
			else
				con.color--;
			
			if (con.color > 255) {
				con.color = 254;
				up = false;
			}
			else if (con.color < 0) {
				con.color = 1;
				up = true;
			}

			con.updateUI.sendEmptyMessage(0);
			try {
   				BackgroundShifter.sleep(1);
   			} 
   			catch (InterruptedException err) {
   				err.printStackTrace();
   			}
   		}
   	}
    
}

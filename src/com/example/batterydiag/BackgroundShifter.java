package com.example.batterydiag;

public class BackgroundShifter extends Thread {
	
	MainActivity con;

	public boolean up = true;
	public boolean runloop = true;
	
	public BackgroundShifter(MainActivity a) {
		this.con = a;
	}

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_DISPLAY);

		while (runloop) {
			if (up == true)
				con.bgcolor++;
			else
				con.bgcolor--;
			
			if (con.bgcolor > 255) {
				con.bgcolor = 254;
				up = false;
			}
			else if (con.bgcolor < 0) {
				con.bgcolor = 1;
				up = true;
			}

			con.updateUI.sendEmptyMessage(0);
			try {
   				BackgroundShifter.sleep(con.bgspeed);
   			}
   			catch (InterruptedException err) {
   				err.printStackTrace();
   			}
   		}
		return;
   	}
}

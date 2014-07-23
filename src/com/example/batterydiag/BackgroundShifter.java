package com.example.batterydiag;

public class BackgroundShifter extends Thread {
	
	MainActivity con;

	public int colarray[] = {	238, 237, 235, 230, 224, 217, 207, 197, 185, 172,
								158, 144, 130, 117, 105,  97,  94,  97, 105, 117,
								130, 144, 158, 172, 185, 197, 207, 217, 224, 230,
								235, 237 };

	int index = 0;

	public boolean up = true;
	public boolean runloop = true;
	
	public BackgroundShifter(MainActivity a) {
		this.con = a;
	}

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		while (runloop) {
			con.bgcolor = colarray[index];
			index++;
			
			if (index > 31)
				index = 0;
/*
			if (up == true)
				con.bgcolor++;
			else
				con.bgcolor--;
			
			if (con.bgcolor > 255) {
				con.bgcolor = 254;
				up = false;
			}
			else if (con.bgcolor < 1) {
				con.bgcolor = 1;
				up = true;
			}
*/
			con.updateUI.sendEmptyMessage(0);
			try {
   				BackgroundShifter.sleep(con.bgspeed);
   			}
   			catch (InterruptedException err) {
   				err.printStackTrace();
   			}
   		}
		con.bgcolor = 255;
		con.updateUI.sendEmptyMessage(0);
		return;
   	}
}

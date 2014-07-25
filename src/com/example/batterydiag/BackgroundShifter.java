package com.example.batterydiag;

import android.os.Message;

public class BackgroundShifter extends Thread {
	static public final int colarray_50ms[] = {	238, 237, 235, 230, 224, 217, 207, 197, 185, 172,
									158, 144, 130, 117, 105,  97,  94,  97, 105, 117,
									130, 144, 158, 172, 185, 197, 207, 217, 224, 230,
									235, 237 };

	static public final int NUM_CYCLES_PROCESSED = 64;
	static public final int NUM_STEPS_PER_CYCLE = 32;

	MainActivity con;

	private int index = 0;
	private int period_counter = 1;
	
	public boolean runloop = true;
	
	public BackgroundShifter(MainActivity a) {
		this.con = a;
	}

	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		while (runloop && (period_counter <= NUM_CYCLES_PROCESSED)) {
			con.bgcolor = colarray_50ms[index];

			Message m = con.updateUI.obtainMessage(MainActivity.BD_MSG_UPDATE_BACKGROUND, period_counter, con.bgcolor);
			con.updateUI.sendMessage(m);

			index++;
			if (index > (NUM_STEPS_PER_CYCLE-1)) {
				index = 0;
				period_counter++;
			}

			try {
   				BackgroundShifter.sleep(con.bgspeed);
   			}
   			catch (InterruptedException err) {
   				err.printStackTrace();
   			}
   		}

		con.stopSampling();

		return;
   	}
}

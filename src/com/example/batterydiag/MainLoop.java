package com.example.batterydiag;

public class MainLoop extends Thread{

	MainActivity con;

	public MainLoop(MainActivity a) {
		this.con = a;
	}

	public void run() {
		for (;;) {

			con.counter++;
			
			con.updateUI.sendEmptyMessage(0);
			try {
				Thread.sleep(100);
			} 
			catch (InterruptedException err) {
				err.printStackTrace();
			}
		}
	}
}

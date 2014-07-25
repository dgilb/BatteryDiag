package com.example.batterydiag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Message;

public class CurrentFileReader extends Thread {
	static final public int CFR_SAMPLING_RATE = 50;
	
	static final public int CFR_CURRENT = 1;
	static final public int CFR_VOLTAGE = 2;

	private MainActivity con;

	public boolean runloop = true;

	private int current_now;
	private int voltage_now;

	public CurrentFileReader(MainActivity a) {
		this.con = a;
	}
	
	public void run() {
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);

		StringBuilder text= new StringBuilder();

		while (runloop && con.isRunning(MainActivity.BD_THREAD_BGCOLOR)) {

			current_now = readValueFromFile(CFR_CURRENT);
			voltage_now = readValueFromFile(CFR_VOLTAGE);

			text.append(Integer.toString(current_now)+" "+Integer.toString(voltage_now)+" "+Long.toString((System.nanoTime()/1000000)));
			text.append("\r\n");

			Message m = con.updateUI.obtainMessage(MainActivity.BD_MSG_UPDATE_CV_TEXT, current_now, voltage_now);
			con.updateUI.sendMessage(m);

		 	try {
		 		CurrentFileReader.sleep(CFR_SAMPLING_RATE);
		 	}
		 	catch (InterruptedException err){
				err.printStackTrace();
			}
		}

		con.stopSampling();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.US);
		String filename="CurrentDiag";
		filename += "-" + sdf.format(new Date()) + ".txt";
		File file=new File(con.getExternalFilesDir(null),filename);
		String content=text.toString();

		try {
			FileOutputStream fos=new FileOutputStream(file,false);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
			text.setLength(0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	private int readValueFromFile(int file_type) {
		File f = null;

		switch (file_type) {
			case CFR_CURRENT:
				f = new File("/sys/class/power_supply/battery/current_now");
				break;
			case CFR_VOLTAGE:
				f = new File("/sys/class/power_supply/battery/current_now");
				break;
			default:
				return 0;
		}

		int number = 0;

		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line = br.readLine();
			br.close();
	
			if (line == null) {
				return 0;
			}

			number = Integer.parseInt(line);
		}
		catch (IOException e) {
			// handle unknown value error
			return 0;
		}

		return number;
	}
}

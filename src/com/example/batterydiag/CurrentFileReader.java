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

public class CurrentFileReader extends Thread{
	MainActivity con;
	public boolean runloop=true;
	
	public CurrentFileReader(MainActivity a){
		this.con = a;
	}
public void run(){
	StringBuilder text= new StringBuilder();

	while (runloop) {
		File f= new File("/sys/class/power_supply/battery/current_now");
		File f2= new File("/sys/class/power_supply/battery/voltage_now");
		if(f.exists())
		{
			
			try{
				BufferedReader br= new BufferedReader(new FileReader(f));
				BufferedReader br2= new BufferedReader(new FileReader(f2));
				String currentline;
				String voltageline;
				currentline = br.readLine();
				voltageline=br2.readLine();
				if ((currentline != null) && (voltageline!=null)) {
					text.append(currentline+" "+voltageline+" "+Long.toString((System.nanoTime()/1000000)));
					text.append('\n'); 

					Message m=con.updateUI.obtainMessage();
					m.arg1=Integer.parseInt(currentline);
					m.arg2=Integer.parseInt(voltageline);
					con.updateUI.sendMessage(m);
				}
				
			} catch (IOException e) {
				// error handling
			}
	}
	 if (con.counter<1023){
		con.counter++;
		
			 }
	 else if (con.counter==1023){
		 con.counter=0;
	//Message m=con.updateUI.obtainMessage(1,1,1,text.toString());
	//con.updateUI.sendMessage(m);
//private void createExternalStorageFile(){
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssSSS", Locale.US);
		 String filename="CurrentDiag";
		 filename += "-" + sdf.format(new Date()) + ".txt";
			File file=new File(con.getExternalFilesDir(null),filename);
			String content=text.toString();
				try{
					FileOutputStream fos=new FileOutputStream(file,false);
					fos.write(content.getBytes());
					//FileOutputStream outputStream=openFileOutput(filename,Activity.MODE_APPEND);
							//outputStream.write(content.getBytes());
							//outputStream.flush();
							//outputStream.close();
					fos.flush();
					fos.close();
					text.setLength(0);
							//Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
						}catch(FileNotFoundException e){e.printStackTrace();}
				catch(IOException e){e.printStackTrace();}
				
				}

	try{
		CurrentFileReader.sleep(100);
	}
	catch (InterruptedException err){
		err.printStackTrace();
		}
	 }
	
	return;
}
}



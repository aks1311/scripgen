package com.airbusds.idea.job;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;

public abstract class LogFileReader extends TimerTask {

	private File logFile;
	private long filePointer = 0;
	
	public LogFileReader(File logFile){
		this.logFile = logFile;
	}
	
	@Override
	public void run() {
		
		if(stopReading())
			cancel();
		
		long fileLength = this.logFile.length();
		
		try{
			RandomAccessFile file = new RandomAccessFile(logFile, "r");
			
			if(fileLength < filePointer){
//				file = new RandomAccessFile(logFile, "r");
				filePointer = 0;
			}
			
			if(fileLength>filePointer){
				file.seek(filePointer);
				String line = file.readLine();
				while(line!=null){
					onNewLine(line);
					line = file.readLine();
				}
				filePointer = file.getFilePointer();
			}
			
			file.close();
		}catch(Exception e){
			
		}

	}
	
	public void start(){
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(this, 10, 100);
	}
	
	public abstract boolean stopReading();
	
	public abstract void onNewLine(String line);

}

package com.airbusds.idea.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IDEAJob extends InfoBase {
	
	private static final long serialVersionUID = -5043006666608887266L;
	private static Logger log = LogManager.getLogger(IDEAJob.class.getName());
	
	private Status status;
	private String label;
	private String screenLogFile;
	private List<String> command;
	private Process p;
	private String jobHome;
	private long startTime;
	private long endTime;
	
	private XYPlot result;
	
	public IDEAJob(String label) {
		 status = Status.NOT_STARTED;
		 this.label = label;
	}
	
	synchronized public void reset(){
		startTime = 0;
		endTime = 0;
		setStatus(Status.NOT_STARTED);
	}
	
	synchronized public void start(){
		File processOutput = new File(jobHome+File.separatorChar+"screen.out");
		if(processOutput.exists()){
			PrintWriter writer;
			try {
				writer = new PrintWriter(processOutput);
				writer.print("");
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}
		
		screenLogFile = processOutput.getAbsolutePath();
		
		final ProcessBuilder pb = new ProcessBuilder()
			.directory(new File(jobHome))
			.redirectErrorStream(true)
			.redirectOutput(Redirect.appendTo(processOutput))
			.command(command);
		
		endTime = 0;
		startTime = new Date().getTime();
		
		try {
			p = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		log.info("Started process "+getLabel());
		
		setStatus(Status.RUNNING);
		
		updateStatus();
		
	}
	
	private void updateStatus(){
		if(status!=Status.COMPLETED || status!=Status.CRASHED){
			Timer timer = new Timer();
			final IDEAJob _this = this;
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					
					synchronized (_this) {
						if(p!=null && p.isAlive())
							setStatus( Status.RUNNING );
						
						if(p!=null && !p.isAlive() && p.exitValue()==0){
							endTime = new Date().getTime();
							setStatus( Status.COMPLETED );
							p = null;
							cancel();
						}
						if(p!=null && !p.isAlive() && p.exitValue()!=0){
							log.info("Process \""+getLabel()+"\" exited with error code: "+p.exitValue());
							endTime = new Date().getTime();
							setStatus( Status.CRASHED );
							p = null;
							cancel();
						}
					} // synchronized block
					
				}
				
			}, 1, 10);
		}
	}
	
	synchronized public void setStatus(Status status){
		
		if(status.equals(this.status))
			return;
		
		Status oldValue = this.status;
		this.status = status;
		log.debug("Status changed to "+this.status+" "+label);
        this.pcs.firePropertyChange("value", oldValue, status);
	}
	
	synchronized public Status getStatus() {
		return status;
	}
	
	public long getDuration(){
		if(getStatus().equals(Status.COMPLETED) || getStatus().equals(Status.CRASHED))
			return endTime-startTime;
		else
			return 0;
	}
	
	public void stop(){
		log.info("Killing process "+getLabel());
		
		p.destroyForcibly();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getScreenLogFile() {
		return screenLogFile;
	}

	public void setScreenLogFile(String screenLogFile) {
		this.screenLogFile = screenLogFile;
	}

	public List<String> getCommand() {
		return command;
	}

	public void setCommand(List<String> command) {
		this.command = command;
	}

	public String getJobHome() {
		return jobHome;
	}

	public void setJobHome(String jobHome) {
		this.jobHome = jobHome;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public XYPlot getResult() {
		return result;
	}

	public void setResult(XYPlot result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return getLabel();
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof IDEAJob) && toString().equals(obj.toString());
	}

	public enum Status {
		NOT_STARTED,
		RUNNING,
		CRASHED,
		COMPLETED
	}
}

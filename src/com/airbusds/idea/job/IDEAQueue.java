package com.airbusds.idea.job;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.config.IDEAConfig;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionMethod;
import com.airbusds.idea.model.IDEABatch;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.IDEAJob.Status;


public class IDEAQueue {
	
	private static Logger log = LogManager.getLogger(IDEAQueue.class.getName());
	
	public static IDEAQueue _q;
	public static final int MAX_PROCESS = IDEAConfig.getMaxProcesses();
	
	LinkedList<IDEABatch> batches = new LinkedList<IDEABatch>();
	boolean isRunning;
	IDEABatch runningBatch;
	List<IDEABatch> completedBatches = new ArrayList<IDEABatch>();
	
	public boolean isRunning() {
		return isRunning;
	}

	private IDEAQueue(){
	}
	
	public static IDEAQueue getInstance(){
		
		synchronized (IDEAQueue.class) {
			if(_q == null){
				_q = new IDEAQueue();
			}
		}
		
		return _q;
		
	}

	public void addBatch(IDEABatch batch){
		batches.add(batch);
		if(!isRunning())
			start();
	}
	
	private void start(){
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				synchronized (IDEAQueue.class) {
				
					isRunning = true;
					
					if(runningBatch == null)
						runningBatch = batches.poll();
					
					if(runningBatch == null){
						isRunning = false;
						cancel();
						return;
					}
					
					int counter = 0;
					for (Iterator<IDEAJob> it = runningBatch.getJobs().iterator(); it
							.hasNext();) {
						IDEAJob job = it.next();
						if(job.getStatus().equals(Status.RUNNING)){
							counter++;
						}
					}
					
					Iterator<IDEAJob> it = runningBatch.getJobs().iterator();
					int maxProcess = getMaxProcesses();
					if(counter<maxProcess){
						while (it.hasNext() && counter<maxProcess) {
							IDEAJob job = it.next();
							if(job.getStatus().equals(Status.NOT_STARTED)){
								log.info("Starting process "+job.getLabel());
								job.start();
								counter++;
							}
						}
					}
					if(counter == 0){
						IDEABatchFile file = new IDEABatchFile();
						file.jobList = runningBatch.getJobs();
						file.sessId = runningBatch.getSessId();
						
						try{
							 
							FileOutputStream fout = new FileOutputStream(runningBatch.getBatchHome() + File.separatorChar + "idea.jobs");
							ObjectOutputStream oos = new ObjectOutputStream(fout);   
							oos.writeObject(file);
							oos.close();
					 
					   }catch(Exception ex){
						   ex.printStackTrace();
					   }
						
						runningBatch = null;
					}
					
					
				}
			}
			
		}, 10, 1000);
		
		
	}
	
	public List<IDEAJob> getJobs(long sessId, String jobsMD) throws FileNotFoundException, IOException, ClassNotFoundException{
		
		if(runningBatch!=null && runningBatch.getSessId() == sessId){
			return runningBatch.getJobs();
		}
		
		for (Iterator<IDEABatch> it = batches.iterator(); it.hasNext();) {
			IDEABatch batch = it.next();
			if(batch.getSessId() == sessId)
				return batch.getJobs();
		}
		
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(jobsMD));
		IDEABatchFile batch = (IDEABatchFile)ois.readObject();
		
		List<IDEAJob> result = batch.jobList;
		
		ois.close();
		return result;
	}
	
	public void restartJobs(AnalysisInfo info){
		long sessId = info.getIdeaSessionId();
		if(runningBatch!=null && runningBatch.getSessId() == sessId){
			return;
		}
		
		for (Iterator<IDEABatch> it = batches.iterator(); it.hasNext();) {
			IDEABatch batch = it.next();
			if(batch.getSessId() == sessId){
				return;
			}
		}
		
		IDEABatch batch = new IDEABatch();
        batch.setMethod(ExecutionMethod.Parallel);
		batch.setSessId(info.getIdeaSessionId());
        batch.setBatchHome(info.getAnalysisLocation());
        batch.setJobs(info.getJobs());
        addBatch(batch);
        
	}
	
	private int getMaxProcesses(){
		if(this.runningBatch.getMethod().equals(ExecutionMethod.Serial))
			return 1;
		else
			return MAX_PROCESS;
	}
	
	
}

package com.airbusds.idea.model;

import java.util.ArrayList;
import java.util.List;

public class IDEABatch {
	
	List<IDEAJob> jobs = new ArrayList<IDEAJob>();
	long sessId;
	String batchHome;
	ExecutionMethod method;
	
	public String getBatchHome() {
		return batchHome;
	}
	public void setBatchHome(String batchHome) {
		this.batchHome = batchHome;
	}
	public List<IDEAJob> getJobs() {
		return jobs;
	}
	public void setJobs(List<IDEAJob> jobs) {
		this.jobs = jobs;
	}
	public long getSessId() {
		return sessId;
	}
	public void setSessId(long sessId) {
		this.sessId = sessId;
	}
	public ExecutionMethod getMethod() {
		return method;
	}
	public void setMethod(ExecutionMethod method) {
		this.method = method;
	}
	
}

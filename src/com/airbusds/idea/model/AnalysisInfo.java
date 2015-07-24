package com.airbusds.idea.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AnalysisInfo extends InfoBase{
	
	private static final long serialVersionUID = -310018101448245003L;
	
	private String name;			// Name of analysis
	private String type;			// type of analysis (Flutter / Modal)
	private String analysisLocation;	// home directory for the analysis
	private String author;			// user name who created the analysis
	private Date createdDate; 		// analysis running start date
	private Date endDate; 			// analysis running end time/date
	private InputFile paramIn;		// input file (param.in)
	private InputFile modelIn;		// input file (model.in)
	private AnalysisStatus status = AnalysisStatus.DRAFT;
	
	private List<ExecutionPlan> executionPlans = new ArrayList<ExecutionPlan>();
	private boolean processCreated;
	
	private long ideaSessionId; // positive value indicates the Analysis was started. It may be either running or completed.
	private String jobsMD;
	private String executionPlanName;
	
	private String fileVersion = "0.0";
	
	@JsonIgnore
	private List<IDEAJob> jobs;
		
	@JsonIgnore
	public boolean isDirty;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getAnalysisLocation() {
		return analysisLocation;
	}
	public void setAnalysisLocation(String analysisLocation) {
		this.analysisLocation = analysisLocation;
	}
	public InputFile getParamIn() {
		return paramIn;
	}
	public void setParamIn(InputFile paramIn) {
		this.paramIn = paramIn;
	}
	public InputFile getModelIn() {
		return modelIn;
	}
	public void setModelIn(InputFile modelIn) {
		this.modelIn = modelIn;
	}
	public List<ExecutionPlan> getExecutionPlans() {
		return executionPlans;
	}
	public void setExecutionPlans(List<ExecutionPlan> executionPlans) {
		this.executionPlans = executionPlans;
	}
	public AnalysisStatus getStatus() {
		return status;
	}
	public void setStatus(AnalysisStatus status) {
		this.status = status;
	}
	public long getIdeaSessionId() {
		return ideaSessionId;
	}
	public void setIdeaSessionId(long ideaSessionId) {
		this.ideaSessionId = ideaSessionId;
	}
	public String getJobsMD() {
		return jobsMD;
	}
	public void setJobsMD(String jobsMD) {
		this.jobsMD = jobsMD;
	}
	public List<IDEAJob> getJobs() {
		return jobs;
	}
	public void setJobs(List<IDEAJob> jobs) {
		this.jobs = jobs;
	}
	public boolean isProcessCreated() {
		return processCreated;
	}
	public void setProcessCreated(boolean processCreated) {
		this.processCreated = processCreated;
	}
	public String getExecutionPlanName() {
		return executionPlanName;
	}
	public void setExecutionPlanName(String executionPlanName) {
		this.executionPlanName = executionPlanName;
	}
	public String getFileVersion() {
		return fileVersion;
	}
	public void setFileVersion(String fileVersion) {
		this.fileVersion = fileVersion;
	}
}

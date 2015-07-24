package com.airbusds.idea.job;

import java.io.Serializable;
import java.util.List;

import com.airbusds.idea.model.IDEAJob;

class IDEABatchFile implements Serializable{
	private static final long serialVersionUID = 3081587260443552836L;
	List<IDEAJob> jobList;
	long sessId;
}
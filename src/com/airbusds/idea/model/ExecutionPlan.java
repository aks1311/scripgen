package com.airbusds.idea.model;

import java.util.HashSet;
import java.util.Set;

public class ExecutionPlan {
	private String name;
	private ExecutionMethod method;
	
	private boolean useConstraints;
	private Set<PAConstraints> constraints = new HashSet<PAConstraints>();
	private Set<ParamValueCombination> valueCombinationList = new HashSet<ParamValueCombination>();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ExecutionMethod getMethod() {
		return method;
	}
	public void setMethod(ExecutionMethod method) {
		this.method = method;
	}
	public boolean isUseConstraints() {
		return useConstraints;
	}
	public void setUseConstraints(boolean useConstraints) {
		this.useConstraints = useConstraints;
	}
	public Set<PAConstraints> getConstraints() {
		return constraints;
	}
	public void setConstraints(Set<PAConstraints> constraints) {
		this.constraints = constraints;
	}
	public Set<ParamValueCombination> getValueCombinationList() {
		return valueCombinationList;
	}
	public void setValueCombinationList(Set<ParamValueCombination> valueList) {
		this.valueCombinationList = valueList;
	}
	
	@Override
	public String toString() {
		return getName();
	}
//	public boolean isProcessCreated() {
//		return processCreated;
//	}
//	public void setProcessCreated(boolean processCreated) {
//		this.processCreated = processCreated;
//	}
	
}

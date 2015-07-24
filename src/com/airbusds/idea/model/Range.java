package com.airbusds.idea.model;

public class Range {
	private Value minValue;
	private Value maxValue;
	private Value steps;
	
	public Range(){}
	
	public Range(Value minValue, Value maxValue, Value steps){
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.steps = steps;
	}

	public Value getMinValue() {
		return minValue;
	}

	public void setMinValue(Value minValue) {
		this.minValue = minValue;
	}

	public Value getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Value maxValue) {
		this.maxValue = maxValue;
	}

	public Value getSteps() {
		return steps;
	}

	public void setSteps(Value steps) {
		this.steps = steps;
	}
	
}

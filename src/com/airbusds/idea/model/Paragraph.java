package com.airbusds.idea.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

public class Paragraph {
	private int sequence;
	private boolean isBlankLine;
	private boolean isComment;
	private boolean isParameter;
	String comments; // used only if isComments is true
	
	// for parameter values
	Parameter parameter;
	boolean useParamDescAsComment;
	
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	@JsonGetter("isBlankLine")
	public boolean getIsBlankLine() {
		return isBlankLine;
	}
	@JsonSetter("isBlankLine")
	public void setBlankLine(boolean isBlankLine) {
		this.isBlankLine = isBlankLine;
	}
	@JsonGetter("isComment")
	public boolean getIsComment() {
		return isComment;
	}
	@JsonSetter("isComment")
	public void setComment(boolean isComment) {
		this.isComment = isComment;
	}
	@JsonGetter("isParameter")
	public boolean getIsParameter() {
		return isParameter;
	}
	@JsonSetter("isParameter")
	public void setIsParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	@JsonGetter("parameter")
	public Parameter getParameter() {
		return parameter;
	}
	@JsonSetter("parameter")
	public void setParameter(Parameter parameter) {
		this.parameter = parameter;
	}
	public boolean isUseParamDescAsComment() {
		return useParamDescAsComment;
	}
	public void setUseParamDescAsComment(boolean useParamDescAsComment) {
		this.useParamDescAsComment = useParamDescAsComment;
	}
	
}

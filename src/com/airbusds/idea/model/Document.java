package com.airbusds.idea.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Defines the formatted document which can be serialized to a file. Contains a list of ordered paragraphs.
 * 
 * @author amit.singh
 */
public class Document {
	String title;
	List<Paragraph> paragraphs = new ArrayList<Paragraph>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}
	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}
	
	public void addParagraph(Paragraph para){
		addParagraph(paragraphs.size(), para);
	}
	
	public void addParagraph(int location, Paragraph para){
		if(para.getIsParameter()){
			for (Iterator<Paragraph> it = paragraphs.iterator(); it.hasNext();) {
				Paragraph paragraph = it.next();
				if(paragraph.getIsParameter() && paragraph.getParameter().equals(para.getParameter())){
					return;
				}
			}
		}
		paragraphs.add(location, para);
		resetSequence();
	}
	
	public void deleteParagraph(int seq){
		paragraphs.remove(seq);
		resetSequence();
	}

	private void resetSequence(){
		int cnt = 0;
		for (Iterator<Paragraph> it = paragraphs.iterator(); it.hasNext();) {
			Paragraph tempPara = it.next();
			tempPara.setSequence(cnt++);
		}
	}
	
}

package com.airbusds.idea.gui;

import com.airbusds.idea.model.Paragraph;

public interface DocumentViewController{
	public void reloadPara(int seq);
	public void selectPara(int seq);
	public void deletePara(int seq);
	public void insertPara(Paragraph para);
}
package com.airbusds.idea.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.gui.editor.ParagraphEditor;


@SuppressWarnings("serial")
public class DocumentViewer extends BaseFormPane {

	private static Logger log = LogManager.getLogger(DocumentViewer.class.getName());
	
	private List<ParagraphView> paragraphs = new ArrayList<ParagraphView>();
	protected ParagraphEditor editor;
	
	public DocumentViewer() {
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, 1));
	}
	
	public void addParagraph(ParagraphView paraView, int position){
		paragraphs.add(position, paraView);
		resetParagraphNumbering();
	}
	
	public void deleteParagraph(int position){
		paragraphs.remove(position);
		resetParagraphNumbering();
	}
	
	private void resetParagraphNumbering(){
		getReaderPanel().removeAll();
		int seq = 0;
		for (Iterator<ParagraphView> it = paragraphs.iterator(); it.hasNext();) {
			ParagraphView para = it.next();
			para.getParagraph().setSequence(seq++);
			getReaderPanel().add(para);
		}
	}
	
	public void addParagraph(ParagraphView para){
		paragraphs.add(para);
		getReaderPanel().add(para);
	}
	
	public ParagraphView getParagraph(int position){
		if(position>-1 && position<paragraphs.size())
			return paragraphs.get(position);
		else
			return null;
	}
	
	public int getIndex(ParagraphView paragraph){
		return paragraphs.indexOf(paragraph);
	}
	
	public JPanel getReaderPanel(){
		return this.panel;
	}

	public ParagraphEditor getEditor() {
		return editor;
	}

	public void setEditor(ParagraphEditor editor) {
		this.editor = editor;
	}
	
}

package com.airbusds.idea.gui.editor;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.idea.model.Paragraph;

@SuppressWarnings("serial")
public class CommentsEditPanel extends EditorPanel {
	
	private JTextArea commentJTA;
	private Paragraph paragraph;
	
	public CommentsEditPanel(Paragraph paragraph){
		this.paragraph = paragraph;
		init();
	}
	
	public void update(){
		
		paragraph.setComments(commentJTA.getText());
		
	}
	
	private void init(){
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		commentJTA = new JTextArea(5,50);
		commentJTA.setText(paragraph.getComments());
		
		gbl.row().cell(new JLabel("Comments")).cell(commentJTA).fillX();
		
		gbl.doneAndPushEverythingToTop();
		
	}
	
}

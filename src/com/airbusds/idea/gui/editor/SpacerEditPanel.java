package com.airbusds.idea.gui.editor;

import javax.swing.JLabel;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.idea.model.Paragraph;

@SuppressWarnings("serial")
public class SpacerEditPanel extends EditorPanel {
	
	
	public SpacerEditPanel(Paragraph paragraph){
		init();
	}
	
	public void update(){
		
	}
	
	private void init(){
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		gbl.row().cell(new JLabel("<html><i>Delete the space using the delete button in the toolbar.</i>")).fillX();
		
		gbl.doneAndPushEverythingToTop();
		
	}
	
}

package com.airbusds.idea.gui;

import javax.swing.JLabel;

import com.airbusds.idea.model.Paragraph;


@SuppressWarnings("serial")
public class SpacerView extends ParagraphView {
	
	public SpacerView(Paragraph paragraph) {
		super(paragraph);
	}

	@Override
	public void init() {
		getBodyPanel().add(new JLabel(" "));
	}

	@Override
	public void resetData() {
		
	}
	
}

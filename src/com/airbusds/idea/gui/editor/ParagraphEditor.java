package com.airbusds.idea.gui.editor;

import com.airbusds.idea.model.Paragraph;

public interface ParagraphEditor {
	
	
	/**
	 * @param para
	 */
	public void editParagraph(Paragraph para);
	
	
	/**
	 * Invoked after the AnalysisInfo bean is updated with the values from the 
	 * editor form. Override to add call backs post updates.
	 * 
	 */
	public void onUpdate();
	
	/**
	 * @return
	 */
	public Paragraph getParagraph();
}

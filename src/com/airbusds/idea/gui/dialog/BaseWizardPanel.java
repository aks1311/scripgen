package com.airbusds.idea.gui.dialog;

import java.awt.Color;

import javax.swing.JTextField;

import com.airbusds.gui.wizard.JWizardPanel;
import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.gui.TextPrompt;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public abstract class BaseWizardPanel extends JWizardPanel {

	public BaseWizardPanel(WizardComponents wizardComponents, String title) {
		super(wizardComponents, title);
	}
	
	@Override
	public void next() {
		if(validateForm())
			super.next();
	}
	
	
	
	protected boolean validateForm(){
		return true;
	}
	
	protected boolean validateTextField(JTextField field){
		if( !StringUtils.hasText(field.getText()) ){
			TextPrompt tp = new TextPrompt("This field cannot be empty.", field);
	        tp.setForeground( Color.RED );
	        tp.changeAlpha(0.5f);
	        field.validate();
			return false;
		}
		return true;
	}
	
	/**
	 * Invoked before moving to the next wizard step.
	 * Invoked from the validateForm() or the calling method of the wizard. 
	 * 
	 */
	protected abstract void readForm();

}

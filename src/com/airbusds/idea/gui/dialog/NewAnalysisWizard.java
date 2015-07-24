package com.airbusds.idea.gui.dialog;

import java.awt.Frame;

import com.airbusds.Utilities;
import com.airbusds.gui.wizard.FinishAction;
import com.airbusds.gui.wizard.JWizardPanel;
import com.airbusds.gui.wizard.SimpleWizardDialog;
import com.airbusds.idea.model.AnalysisInfo;

@SuppressWarnings("serial")
public class NewAnalysisWizard extends SimpleWizardDialog {

	private AnalysisInfo info;
	public NewAnalysisWizard(Frame owner){
		super(owner, true);
		init();
	}
	
	private void init(){
		this.setTitle("New Analysis");
        
        JWizardPanel panel = null;
        setResizable(false);
        setSize(500, 310);
        
        info = new AnalysisInfo();
        
        panel = new AnalysisDetailsPanel(getWizardComponents(), info);
        getWizardComponents().addWizardPanel(0, panel);
        
        panel = new ImportFilesPanel(getWizardComponents(), info);
        getWizardComponents().addWizardPanel(1, panel);
        
        panel = new InputFieldsPanel(getWizardComponents(), info);
        getWizardComponents().addWizardPanel(2, panel);

        getWizardComponents().setFinishAction(new FinishAction(getWizardComponents()) {
			@Override public void performAction() {
				onFinish(info);
			}
		});
        
        Utilities.centerComponentOnScreen(this);
        
	}

	protected void onFinish(AnalysisInfo info){
		try {
			((BaseWizardPanel)getWizardComponents().getCurrentPanel()).readForm();
			dispose();
		} catch (Exception e) {
//			e.printStackTrace();
			// TODO 2 Not a good way to control flow. Refactor this.
			throw new RuntimeException(e.getMessage());
		}
	}
	
}

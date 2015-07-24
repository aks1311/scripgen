package com.airbusds.idea.gui.dialog;

import java.awt.Frame;

import com.airbusds.Utilities;
import com.airbusds.gui.wizard.FinishAction;
import com.airbusds.gui.wizard.SimpleWizardDialog;
import com.airbusds.idea.gui.editor.AnalysisEditor;
import com.airbusds.idea.job.IDEAQueue;
import com.airbusds.idea.manager.AnalysisManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.IDEABatch;

@SuppressWarnings("serial")
public class ExecutePlanDialog extends SimpleWizardDialog {

	private AnalysisInfo info;
	AnalysisEditor editor;
	ExecutionPlan plan;
	
	public ExecutePlanDialog(Frame owner, AnalysisEditor editor){
		super(owner, true);
		this.editor = editor;
		this.info = editor.getAnalysisInfo();
		init();
	}
	
	public ExecutePlanDialog(Frame owner, AnalysisEditor editor, ExecutionPlan plan){
		super(owner, true);
		this.editor = editor;
		this.info = editor.getAnalysisInfo();
		this.plan = plan;
		init();
	}
	
	private void init(){
		this.setTitle("Execute Plan");
		
		getWizardComponents().getFinishButton().setText("Start IDEA");
		
        setResizable(false);
        setSize(500, 310);
        
        if(plan == null){
        	PlanSelectionPanel panel1 = new PlanSelectionPanel(getWizardComponents(), info);
	        getWizardComponents().addWizardPanel(0, panel1);
	        
	        CreateFilesPanel panel2 = new CreateFilesPanel(getWizardComponents(), info);
	        getWizardComponents().addWizardPanel(1, panel2);
        }else{
        	CreateFilesPanel panel = new CreateFilesPanel(getWizardComponents(), info);
        	panel.setPlan(plan);
            getWizardComponents().addWizardPanel(0, panel);
        }
        
        
        getWizardComponents().setFinishAction(new FinishAction(getWizardComponents()) {
			@Override public void performAction() {
				IDEABatch batch = null;
				try {
					batch = ((CreateFilesPanel)getWizardComponents().getCurrentPanel()).batch;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				info.setIdeaSessionId(batch.getSessId());
				new AnalysisManager().saveAnalysis(info);
				
				IDEAQueue q = IDEAQueue.getInstance();
		    	batch.setMethod(plan.getMethod());
		    	q.addBatch(batch);
//				info.setJobs(batch.getJobs());
		    	
				info.setProcessCreated(true);
				AnalysisManager mgr = new AnalysisManager();
				mgr.saveAnalysis(info);
				
				editor.showProgress();
				editor.showResults();
				dispose();
			}
		});
        
        Utilities.centerComponentOnScreen(this);
        
	}

	public ExecutionPlan getPlan() {
		return plan;
	}

	public void setPlan(ExecutionPlan plan) {
		this.plan = plan;
	}

	
}

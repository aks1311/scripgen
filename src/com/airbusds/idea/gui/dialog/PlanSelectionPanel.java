package com.airbusds.idea.gui.dialog;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;

@SuppressWarnings("serial")
public class PlanSelectionPanel extends BaseWizardPanel {

	
	JComboBox<ExecutionPlan> combo;
	
	public PlanSelectionPanel(WizardComponents wizardComponents, AnalysisInfo info) {
		super(wizardComponents, "Select Plan");
		
		PainlessGridBag gb = new PainlessGridBag(this, false);
		combo = new JComboBox<ExecutionPlan>(info.getExecutionPlans().toArray(new ExecutionPlan[info.getExecutionPlans().size()]));
		gb.row().cell(new JLabel("Select Execution Plan")).cell(combo);
		gb.doneAndPushEverythingToTop();
		
	}

	@Override
	protected void readForm() {
		// create files and folders
		CreateFilesPanel panel =  (CreateFilesPanel)getWizardComponents().getWizardPanel(1);
		panel.setPlan((ExecutionPlan)combo.getSelectedItem());
	}
	
	@Override
	public void next() {
		readForm();
		super.next();
	}

}

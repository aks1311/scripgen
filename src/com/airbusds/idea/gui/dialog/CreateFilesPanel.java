package com.airbusds.idea.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.config.IDEAConfig;
import com.airbusds.idea.manager.AnalysisManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.IDEABatch;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.service.FileService;

@SuppressWarnings("serial")
public class CreateFilesPanel extends BaseWizardPanel implements PropertyChangeListener {

	private JTextArea taskOutput;
    private IDEAJobCreationTask task;
    private JProgressBar progressBar;
    
    private AnalysisInfo info;
    private ExecutionPlan plan;
    public IDEABatch batch; // TODO 2 make this private
	
	public CreateFilesPanel(WizardComponents wizardComponents, AnalysisInfo info) {
		super(wizardComponents, "Creating input files");
		
		this.info = info;
		
		setLayout(new BorderLayout());
		
		progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
 
        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);
 
        add(progressBar, BorderLayout.PAGE_START);
        add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
       
        
	}

	@Override
	public void update() {
		super.update();
		
		getWizardComponents().getBackButton().setEnabled(false);
		getWizardComponents().getFinishButton().setEnabled(false);
		
		info.setExecutionPlanName(plan.getName());
		task = new IDEAJobCreationTask();
        task.addPropertyChangeListener(this);
        task.execute();
	}
	
	@Override
	protected void readForm() {
		// Do nothing
	}
	
	class IDEAJobCreationTask extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            setProgress(0);
            float factor = 100/plan.getValueCombinationList().size();
			
            batch = new IDEABatch();
            batch.setSessId(new Date().getTime());
            batch.setBatchHome(info.getAnalysisLocation());
            
            int counter = 1;
            for (Iterator<ParamValueCombination> it = plan.getValueCombinationList().iterator(); it.hasNext();) {
				ParamValueCombination comb = it.next();
				progress += factor;
				
				String label = info.getName()+"_"+plan.getName()+"_"+counter++;
				comb.setLabel(label);
				
				AnalysisManager am = new AnalysisManager();
				am.setFileService(new FileService());
				am.createInputFiles(info, comb);
				
				taskOutput.append("Generated Input files in folder "+comb.getLabel()+".\n");
				setProgress(Math.min(progress, 100));
				
				IDEAJob job = new IDEAJob(label);
				job.setCommand(IDEAConfig.getIDEACommand());
				job.setJobHome(info.getAnalysisLocation()+File.separatorChar+label);
				batch.getJobs().add(job);
			}
            
            
            setProgress(100);
            
            return null;
        }
 
        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor
            taskOutput.append("Done!\n");
            
            getWizardComponents().getFinishButton().setEnabled(true);
        }
    }

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
            int progress = (Integer) evt.getNewValue();
            progressBar.setValue(progress);
        } 
		
	}

	public ExecutionPlan getPlan() {
		return plan;
	}

	public void setPlan(ExecutionPlan plan) {
		this.plan = plan;
	}

}

package com.airbusds.idea.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.job.IDEAQueue;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.IDEAJob.Status;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class JobSummaryPane extends JScrollPane implements ActionListener {
	
	private JPanel panel;
	private IDEAJob job;
	private JTextField jobTitle;
	private JLabel statusLbl;
	private JButton stopBtn;
	private JButton restartBtn;
	private JTextField startTime;
	private JTextField endTime;
	private JTextField duration;
	
	public JobSummaryPane(IDEAJob job) {
		super(new JPanel());
		panel = (JPanel)getViewport().getView();
		
		this.job = job;
		initUI();
		resetData();
	}
	
	private void initUI(){
//		panel.setBackground(Color.WHITE);
//		panel.setOpaque(true);
		
		stopBtn = new JButton("Stop");
		restartBtn = new JButton("Restart");
		stopBtn.setActionCommand("stop");
		restartBtn.setActionCommand("restart");
		stopBtn.addActionListener(this);
		restartBtn.addActionListener(this);
		
		jobTitle = new JTextField(this.job.getLabel());
		statusLbl = new JLabel();
		startTime = new JTextField();
		endTime = new JTextField();
		duration = new JTextField();
		jobTitle.setEditable(false);
		startTime.setEditable(false);
		endTime.setEditable(false);
		duration.setEditable(false);
		
		
		PainlessGridBag gbl = new PainlessGridBag(panel, false);
		gbl.row().cell(new JLabel("Job Name:")).cellXRemainder(jobTitle);
		gbl.row().cell(new JLabel("Status:")).cellXRemainder(statusLbl);
		gbl.row().cell().cell(stopBtn).cell(restartBtn);
		gbl.row().cell(new JLabel("Start Time:")).cellXRemainder(startTime);
		gbl.row().cell(new JLabel("End Time:")).cellXRemainder(endTime);
		gbl.row().cell(new JLabel("Duration:")).cellXRemainder(duration);
		
		
		JButton paramFileBtn = new JButton("View Param File");
		JButton modelFileBtn = new JButton("View Model File");
		JButton resultsBtn = new JButton("Show Results");
		
		paramFileBtn.setActionCommand("view.param");
		modelFileBtn.setActionCommand("view.model");
		resultsBtn.setActionCommand("goto.results");
		
		paramFileBtn.addActionListener(this);
		modelFileBtn.addActionListener(this);
		resultsBtn.addActionListener(this);
		
		gbl.row().cell().cell(paramFileBtn).cellXRemainder(modelFileBtn);
		gbl.doneAndPushEverythingToTop();
		
		this.job.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				resetData();
			}
		});
	}
	
	private void resetData(){
		
		if(job.getStartTime()!=0)
			startTime.setText( new Date(job.getStartTime()).toString());
		else
			startTime.setText( "-" );
		
		if(job.getEndTime()!=0){
			endTime.setText( new Date(job.getEndTime()).toString());
			duration.setText(  StringUtils.getDisplayTime(job.getDuration()) );
		}else{
			endTime.setText( "-" );
			duration.setText("-");
		}
		
		resetButtonIcons();
	}
	
	private void resetButtonIcons(){
		
		stopBtn.setEnabled(false);
		restartBtn.setEnabled(false);
		
		Status status = job.getStatus();
		switch(status){
			case NOT_STARTED:{
				statusLbl.setIcon(Utilities.createImageIcon("images/waiting14.png"));
				statusLbl.setText("Waiting");
				break;
			}
			case RUNNING:{
				statusLbl.setIcon(Utilities.createImageIcon("images/running14.png"));
				statusLbl.setText("Running");
				stopBtn.setEnabled(true);
				break;
			}
			case COMPLETED:{
				statusLbl.setIcon(Utilities.createImageIcon("images/success14.png"));
				statusLbl.setText("Completed");
				restartBtn.setEnabled(true);
				break;
			}
			case CRASHED:{
				statusLbl.setIcon(Utilities.createImageIcon("images/crashed14.png"));
				statusLbl.setText("Error Occured");
				restartBtn.setEnabled(true);
				break;
			}
			default:
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		switch(command){
			case "stop":{
				this.job.stop();
				break;
			}
			case "restart":{
				this.job.setStatus(Status.NOT_STARTED);
				
				AnalysisInfo info = IDEAContext.getInstance().getAnalysisInfo();
				IDEAQueue.getInstance().restartJobs(info);
				
				resetData();
				break;
			}
			case "view.param":{
				String filePath = this.job.getJobHome() + File.separatorChar + IDEAContext.getInstance().getAnalysisInfo().getParamIn().getName();
				FileViewer viewer = new FileViewer(filePath);
				viewer.setVisible(true);
				break;
			}
			case "view.model":{
				String filePath = this.job.getJobHome() + File.separatorChar + IDEAContext.getInstance().getAnalysisInfo().getModelIn().getName();
				FileViewer viewer = new FileViewer(filePath);
				viewer.setVisible(true);
				break;
			}
			case "goto.results":{
				
				break;
			}
			default:
		}
		
	}
	
}

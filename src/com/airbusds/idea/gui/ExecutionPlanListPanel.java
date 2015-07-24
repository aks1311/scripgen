package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.gui.editor.AnalysisEditor;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class ExecutionPlanListPanel extends JScrollPane implements ActionListener {

	private AnalysisEditor parent;
	private AnalysisInfo info;
	private JPanel panel;
	private JList<ExecutionPlan> planList;
	private DefaultListModel<ExecutionPlan> listModel;
	private JButton addButton;
	private JButton delButton;
	
	public ExecutionPlanListPanel(AnalysisInfo info) {
		super(new JPanel());
		
		this.panel = (JPanel)getViewport().getView();
		this.info = info;
		
		JLabel formHeader = new JLabel("  Execution Plans");
		setColumnHeaderView(formHeader);
		
		createUI();
		loadData();
	}

	public AnalysisEditor getParent() {
		return parent;
	}

	public void setParent(AnalysisEditor parent) {
		this.parent = parent;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		switch(command){
			case "ADD":{
				
				if(!info.getParamIn().isValidated() ){
					JOptionPane.showMessageDialog(this, "Param.in document is not validated. \n"
							+ "Plan cannot be created without validating all input files.","Execution Plan",JOptionPane.ERROR_MESSAGE);
					
					((AnalysisEditor)IDEAContext.getInstance().get(IDEAContext.KEY_ASYS_EDITOR)).selectNode(info.getParamIn());
					return;
				}
				
				if(!info.getModelIn().isValidated()){
					JOptionPane.showMessageDialog(this, "Model.in document is not validated. \n"
							+ "Plan cannot be created without validating all input files.","Execution Plan",JOptionPane.ERROR_MESSAGE);
					
					((AnalysisEditor)IDEAContext.getInstance().get(IDEAContext.KEY_ASYS_EDITOR)).selectNode(info.getModelIn());
					return;
				}
				
				boolean cancel = false;
				while(!cancel){
					String name = JOptionPane.showInputDialog(this, "Enter name of Plan.(15 chars max)","Execution Plan", JOptionPane.PLAIN_MESSAGE);
					
					if(name==null){
						break;
					}
						
					if(StringUtils.hasText(name) && name.length()<=15 && name.length()>=5){
						createExecutionPlan(name);
						break;
					}else{
						JOptionPane.showMessageDialog(this, "Invalid name entered. \n"
								+ "Enter name of Plan.(Min 5 and max 15 chars)","Execution Plan",JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			break;
			case "DEL":{
				int index = planList.getSelectedIndex();
				if(index<0)
					break;
				ExecutionPlan plan = planList.getSelectedValue();
				info.getExecutionPlans().remove(plan);
				parent.deleteExecutionPlan(plan);
				listModel.remove(index);
			}
			break;
			default:
		}
		
		
	}

	private void createUI() {
		
		panel.setBackground(Color.WHITE);
		
		addButton = new JButton("Create New Plan");
		addButton.setActionCommand("ADD");
		addButton.addActionListener(this);
		
		delButton = new JButton("Delete Plan");
		delButton.setActionCommand("DEL");
		delButton.addActionListener(this);
	
		planList = new JList<ExecutionPlan>();
		planList.setBorder(new LineBorder(new Color(15132390)));
		planList.setCellRenderer(new AlternateColorListCellRenderer());
		planList.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent evt) {
		        if (evt.getClickCount() > 1) {
		            parent.editExecutionPlan(planList.getSelectedValue());
		        }
		    }
		});
		
		PainlessGridBag gb = new PainlessGridBag(panel, false);
		gb.row().cellXRemainder(planList).fillX();
		gb.row().cell(addButton).cell(delButton);
		
		JPanel pnlPadding = new JPanel();
	    pnlPadding.setPreferredSize(new Dimension(1, 1));
	    pnlPadding.setMinimumSize(new Dimension(1, 1));
	    pnlPadding.setOpaque(false);
	    gb.row().cellXRemainder(pnlPadding).fillXY();
		
		gb.done();
	}

	private void loadData() {
		listModel = new DefaultListModel<ExecutionPlan>();
		
		for (Iterator<ExecutionPlan> it = info.getExecutionPlans().iterator(); it.hasNext();) {
			ExecutionPlan plan = it.next();
			listModel.addElement(plan);
		}
		
		planList.setModel(listModel);
	}

	private void createExecutionPlan(String name) {
		ExecutionPlan plan = new ExecutionPlan();
		plan.setName(name);
		info.getExecutionPlans().add(plan);
		parent.editExecutionPlan(plan);
	}


}

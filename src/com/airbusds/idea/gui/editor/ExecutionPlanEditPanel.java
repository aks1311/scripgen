package com.airbusds.idea.gui.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.PAValueMapper;
import com.airbusds.idea.gui.MainWindow;
import com.airbusds.idea.gui.PAValueMappingTable;
import com.airbusds.idea.gui.dialog.ExecutePlanDialog;
import com.airbusds.idea.manager.AnalysisManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.AnalysisStatus;
import com.airbusds.idea.model.ExecutionMethod;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;

@SuppressWarnings("serial")
public class ExecutionPlanEditPanel extends JScrollPane implements ActionListener {

	private ExecutionPlan plan;
	private AnalysisInfo info;
	
	private JPanel panel;
	private JRadioButton seqRB;
	private JRadioButton parallelRB;
	private PAValueMappingTable mappingTable;
	private JButton executeBtn = new JButton("Execute");
	
	public ExecutionPlanEditPanel(ExecutionPlan plan, AnalysisInfo info) {
		super(new JPanel());
		panel = (JPanel)getViewport().getView();
		this.plan = plan;
		this.info = info;
		
		createUI();
		loadData();
	}

	private void loadData() {
		if(plan.getMethod()==null){
			plan.setMethod(ExecutionMethod.Serial);
		}
		seqRB.setSelected(plan.getMethod().equals(ExecutionMethod.Serial));
		parallelRB.setSelected(plan.getMethod().equals(ExecutionMethod.Parallel));
		
		InputFile paramIn = info.getParamIn();

		List<Parameter> paParams = new ArrayList<Parameter>();
		for (Iterator<Parameter> it = paramIn.getParameters().iterator(); it.hasNext();) {
			Parameter param = it.next();
			if (param.isPAEnabled()) {
				paParams.add(param);
			}
		}
			
		List<ParamValueCombination> combinations = new PAValueMapper(paParams).createValueCombinations();
		if(combinations==null || combinations.isEmpty()){
			combinations = new ArrayList<ParamValueCombination>();
			ParamValueCombination comb = new ParamValueCombination();
			comb.setParams(new String[]{"analysis"});
			comb.setValues(new Value[][][]{new Value[][]{new Value[]{Value.valueFactory("flutter")}}});
			combinations.add(comb);
		}
		
		int counter = 1;
		for (ParamValueCombination valueCombination : combinations) {
			String label = info.getName()+"_"+plan.getName()+"_"+counter++;
			valueCombination.setLabel(label);
		}
		
		mappingTable.setData(plan, combinations);
		
	}

	private void createUI() {
		
		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
		toolbar.add(new JLabel("  Edit "+plan.getName()));
		setColumnHeaderView(toolbar);
		
		panel.setLayout(new BorderLayout());
		
		JPanel topPane = new JPanel();
		topPane.setOpaque(false);
		
		seqRB = new JRadioButton("Sequential");
		parallelRB = new JRadioButton("Parallel");
		
		seqRB.addActionListener(this);
		parallelRB.addActionListener(this);
		seqRB.setActionCommand("SEQ");
		parallelRB.setActionCommand("PARAL");
		
		ButtonGroup bg = new ButtonGroup();
		bg.add(parallelRB);
		bg.add(seqRB);
		
		executeBtn.setActionCommand("EXECUTE");
		executeBtn.addActionListener(this);
		
//		if(info.isProcessCreated()){
//			editBtn.setVisible(true);
//			executeBtn.setVisible(false);
//		}else{
//			editBtn.setVisible(false);
//			executeBtn.setVisible(true);
//		}
		
		PainlessGridBag gbl = new PainlessGridBag(topPane, false);
		gbl.row().cell(new JLabel("Execution Method: "))
				.cell(seqRB);
		gbl.row().cell()
				.cell(parallelRB);
		gbl.row().cellXRemainder(executeBtn);
		
		gbl.doneAndPushEverythingToTop();
		panel.add(topPane, BorderLayout.NORTH);
		
		mappingTable = new PAValueMappingTable();
		mappingTable.setBackground(Color.WHITE);
		panel.add(mappingTable, BorderLayout.CENTER);
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		if(command.equals("SEQ")){
			plan.setMethod(ExecutionMethod.Serial);
		}else if(command.equals("PARAL")){
			plan.setMethod(ExecutionMethod.Parallel);
		}else if(command.equals("EXECUTE")){
			
			if(plan.getValueCombinationList().isEmpty()){
				Utilities.showMessage("Select atleast one combination (row) to execute", "IDEA GUI");
				return;
			}
			
			if(info.isProcessCreated()){
				if(Utilities.promptYesNoQuestion("This action will delete the input files and the generated results. Do you want to proceed?")){
					
					AnalysisEditor editor = (AnalysisEditor)IDEAContext.getInstance().get(IDEAContext.KEY_ASYS_EDITOR);
					editor.clearJobsAndResults(plan);
					
					AnalysisManager asysMgr = new AnalysisManager();
					asysMgr.deleteAnalysisExecution(info);
					info.setIdeaSessionId(0);
					info.setStatus(AnalysisStatus.DOCS_VALIDATED);
					if(info.getJobs()!=null)
						info.getJobs().clear();
					info.setProcessCreated(false);
					executeBtn.setVisible(true);
					asysMgr.saveAnalysis(info);
				}else{
					return;
				}
			}
			
			AnalysisEditor editor = (AnalysisEditor)IDEAContext.getInstance().get(IDEAContext.KEY_ASYS_EDITOR);
			MainWindow main = (MainWindow)IDEAContext.getInstance().get(IDEAContext.KEY_WINDOW_MAIN);
			ExecutePlanDialog runWiz = new ExecutePlanDialog(main, editor, plan);
			runWiz.show();
		}
		
		
	}
	
}

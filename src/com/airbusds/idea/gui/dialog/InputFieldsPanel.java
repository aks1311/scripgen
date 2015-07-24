package com.airbusds.idea.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.Params;
import com.airbusds.idea.manager.ConfigManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Parameter;

@SuppressWarnings("serial")
public class InputFieldsPanel extends BaseWizardPanel implements ActionListener{
	private static Logger log = LogManager.getLogger(InputFieldsPanel.class.getName());
	
	private AnalysisInfo info;
	private JTable table;
	private InputFieldTableModel tableModel;
	private boolean tableDataInitialized = false;
	
	public InputFieldsPanel(WizardComponents wizardComponents, AnalysisInfo info) {
		super(wizardComponents, "Configure Input Parameters");
		init();
		this.info = info;
	}

	private void init() {
		// TODO 3 Internationalization
		
		BorderLayout bl = new BorderLayout();
		this.setLayout(bl);
		tableModel = new InputFieldTableModel();
		table = new JTable(tableModel);
		JScrollPane sp = new JScrollPane(table);
		table.setDragEnabled(false);
		
		TableColumn col = table.getColumnModel().getColumn(0);
//		col.sizeWidthToFit();
//		col.setResizable(false);
		col.setMinWidth(150);
		col = table.getColumnModel().getColumn(1);
		col.setResizable(false);
		col.setMaxWidth(100);
		col = table.getColumnModel().getColumn(2);
		col.setResizable(false);
		col.setMaxWidth(150);
		
		this.add(sp, BorderLayout.CENTER);
		
		JPanel headerPanel = new JPanel();
		headerPanel.add(new JLabel("Select the Parameters for the Analysis"));
		this.add(headerPanel, BorderLayout.NORTH);
		
		JButton moveUpBtn = new JButton("Up");
		moveUpBtn.setActionCommand("moverow.up");
		moveUpBtn.addActionListener(this);
		moveUpBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		JButton moveDownBtn = new JButton("Down");
		moveDownBtn.setActionCommand("moverow.down");
		moveDownBtn.addActionListener(this);
		moveDownBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPanel.add(Box.createHorizontalGlue());
		buttonPanel.add(moveUpBtn);
		buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPanel.add(moveDownBtn);
		this.add(buttonPanel, BorderLayout.EAST);
		
	}
	
	private static class InputFieldTableModel extends AbstractTableModel{
	    
		protected String[] columnNames = { "Parameter Name", "Select?","Parameter Study?"};
	    protected Params[] data = {};

		public InputFieldTableModel() {
			super();
		}
	    
	    @Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.length;
		}
		
		@Override
		public String getColumnName(int c) {
			return columnNames[c];
		}

		@Override
		public Object getValueAt(int row, int col) {
			Params param = data[row];
			Object res = null;
			
			switch(col){
			case 0:
				res = param.name;
				break;
			case 1:
				res = param.defaultSelected;
				break;
			case 2:
				res = param.isPAEnabled;
				break;
			default:
			}
			
			return res;
		}
		
		@Override
		public void setValueAt(Object val, int row, int col) {
			Params param = data[row];

			switch(col){
			case 0:
				param.name = (String)val;
				break;
			case 1:
				param.defaultSelected = (Boolean)val;
				break;
			case 2:
				param.isPAEnabled = (Boolean)val;
				break;
			default:
			}
			
			fireTableCellUpdated(row, col);
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
            if (col == 0) {
                return false;
            } else {
            	Params param = data[row];
            	if(col==2 && !param.isPAAllowed)
            		return false;
            	return true;
            }
        }
		
		public void moveRowUp(int row){
			int currOrder = (Integer)getValueAt(row, 3);
			if(row>0){
				data[row].order = currOrder-1;
				data[row-1].order = currOrder;
				
				Params back = data[row]; 
				data[row] = data[row-1];
				data[row-1] = back;
			}
			fireTableDataChanged();
		}
		
		public void moveRowDown(int row){
			int currOrder = (Integer)getValueAt(row, 3);
			int lastRow = data.length-1;
			if(row<lastRow){
				data[row].order = currOrder+1;
				data[row+1].order = currOrder;
				
				Params back = data[row]; 
				data[row] = data[row+1];
				data[row+1] = back;
			}
			fireTableDataChanged();
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String actionCommand = ae.getActionCommand();
		switch(actionCommand){
			case "moverow.up" : 
				{
					int selected = table.getSelectedRow();
					tableModel.moveRowUp(selected);
					if(selected>0)
						table.setRowSelectionInterval(selected-1, selected-1);
				}
				break;
			case "moverow.down" :
				{
					int selected = table.getSelectedRow();
					int lastRow = tableModel.getRowCount()-1;
					tableModel.moveRowDown(selected);
					if(selected<lastRow)
						table.setRowSelectionInterval(selected+1, selected+1);
				}
				break;
			default :
		}
	}
	
	
	@Override
	public void update() {
		super.update();
		
		// init table with data from config file (default list)
		if(!tableDataInitialized){
			initializeTableData();
			tableDataInitialized = true;
		}
		
		// mark the parameters selected which are present in the template file
		InputFile inFile = info.getParamIn();
		if(inFile == null){
			log.debug("No Param file found. Creating new file before marking the input fields");
			inFile = new InputFile();
			inFile.setName("param.in");
			info.setParamIn(inFile);
		}else{
			for (int i = 0; i < tableModel.data.length; i++) {
				Params row = tableModel.data[i];
				
				if(inFile.getParameter((String)row.name) != null){
					row.defaultSelected = true;
				}
			}
		}

		// mark the parameters selected which are present in the template file
		inFile = info.getModelIn();
		if(inFile == null){
			log.debug("No Model file found. Creating new file before marking the input fields");
			inFile = new InputFile();
			inFile.setName("model.in");
			info.setModelIn(inFile);
		}
//		else{
//			for (int i = 0; i < tableModel.data.length; i++) {
//				Params row = tableModel.data[i];
//				
//				if(inFile.getParameter((String)row.name) != null){
//					row.defaultSelected = true;
//				}
//			}
//		}

	
	}
	
	@Override
	public void readForm() {
		log.trace("Inside readForm >>" + this.getPanelTitle());
		
		if(!validateForm()){
			throw new RuntimeException("Incomplete form");
		}
		
		ConfigManager configMgr = new ConfigManager();
		
		// Read model config and create the Parameters for the model file
		InputFile inFile = info.getModelIn();
		InputFileConfig modelConfig = InputFileConfig.getModelInConfig();
		List<Params> params = modelConfig.paramList;
		Collections.sort(params, new Comparator<Params>() {
			@Override public int compare(Params arg0, Params arg1) {
				return arg0.order-arg1.order;
			}
		});
		
		for (Iterator<Params> it = params.iterator(); it.hasNext();) {
			Params paramConfig = it.next();
			if(paramConfig.defaultSelected){
				if(inFile.getParameter(paramConfig.name)!=null)
					inFile.addParameter(configMgr.createParameter(inFile.getParameter(paramConfig.name), paramConfig));
				else
					inFile.addParameter(configMgr.createParameter(paramConfig));
			}
				
		}
		
		// Read Param config from table and create the Parameters for the param.in file
		inFile = info.getParamIn();
		for (int i = 0; i < tableModel.data.length; i++) {
			Params row = tableModel.data[i];
			if(!(Boolean)row.defaultSelected){
				inFile.deleteParameter(row.name);
				continue;
			}
			Parameter param = inFile.getParameter((String)row.name);
			if(param == null){
				param = new Parameter();
				configMgr.createParameter(param, row);
				inFile.addParameter(param);
			}else{
				configMgr.createParameter(param, row);
			}
		}
		
	}
	
	private void initializeTableData() {
		
		log.trace("Initializing table data");
		InputFileConfig config = InputFileConfig.getParamInConfig();
		int rows = config.paramList.size();
		Collections.sort(config.paramList, new Comparator<Params>() {
			@Override public int compare(Params arg0, Params arg1) {
				return arg0.order - arg1.order;
			}
		});
		
		tableModel.data = new Params[rows];
		int cnt = 0;
		for (Iterator<Params> it = config.paramList.iterator(); it.hasNext();) {
			tableModel.data[cnt++] = it.next();
		}
		tableModel.fireTableDataChanged();
		
	}
	
	@Override
	protected boolean validateForm() {
		
		int paramCounter = 0;
		for (int i = 0; i < tableModel.data.length; i++) {
			Params row = tableModel.data[i];
			if(row.defaultSelected)
				paramCounter++;
		}
		if(paramCounter==0){
			JOptionPane.showMessageDialog(InputFieldsPanel.this.getParent(), "Select atleast one Parameter.");
			return false;
		}
			
		return true;
	}
	
}

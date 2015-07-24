package com.airbusds.idea.gui.editor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.IdValue;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;

@SuppressWarnings("serial")
public class ListPAEditor extends PAFieldEditor {

	JTextArea paramCommentJTF;
	JTable paTable;
	
	public ListPAEditor(Parameter param) {
		super(param);
		
		paramCommentJTF = new JTextArea();
		paramCommentJTF.setText(param.getDescription());
		paramCommentJTF.setLineWrap(true);
		paramCommentJTF.setWrapStyleWord(true);

		
		String paramTitle = Utilities.getParamDisplayName(param);

		
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		gbl.row().cell(new JLabel("Parameter Comments"))
					.cellXRemainder(paramCommentJTF).fillX();
		
		paTable = createListTable();
		
		gbl.row().cell(new JLabel(paramTitle))
					.cell(paTable).fillX();
		
		gbl.doneAndPushEverythingToTop();
		
	}
	
	@Override
	public void update() {
		
		if(paTable!=null){
			
			param.getParameterAnalysis().getPaValueList().clear();
			Object[] data = ((ListSelectionTableModel)paTable.getModel()).getSelectedObjects();
			
			for (int i = 0; i < data.length; i++) {
				param.getParameterAnalysis().getPaValueList().add( Value.valueFactory( ((IdValue)data[i]).id ) );
			}
			
		}
		
	}
	
	private JTable createListTable(){
		
		List<IdValue> list = InputFileConfig.getList(param.getListName());
		
		String paramTitle = Utilities.getParamDisplayName(param);
		ListSelectionTableModel model = new ListSelectionTableModel(new String[]{paramTitle, "Select"}, list.toArray());
		JTable table = new JTable(model);
		
		List<Value> values = param.getParameterAnalysis().getPaValueList();
		for (Iterator<Value> it = values.iterator(); it.hasNext();) {
			Value value = it.next();
			model.selectValue( new IdValue(value.getValue().toString(), "") );
		}
		
		return table;
	}
	
	
	private static class ListSelectionTableModel extends AbstractTableModel{
	    
		protected String[] columnNames;
	    protected Object[][] data = {};

		public ListSelectionTableModel(String[] colNames, Object[] rdata) {
			super();
			this.columnNames = colNames;
			
			
			this.data = new Object[rdata.length][2];
			for (int i = 0; i < rdata.length; i++) {
				data[i][0] = rdata[i];
				data[i][1] = false;
			}
			
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
			Object[] drow = data[row];
			return drow[col];
		}
		
		@Override
		public void setValueAt(Object val, int row, int col) {
			Object[] drow = data[row];
			drow[col] = val;
			
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
            	return true;
            }
        }
		
		public Object[] getSelectedObjects(){
			List<Object> res = new ArrayList<Object>();
			for (int i = 0; i < data.length; i++) {
				if((Boolean)data[i][1])
					res.add(data[i][0]);
			}
			return res.toArray();
		}
		
		public void selectValue(IdValue object){
			for (int i = 0; i < data.length; i++) {
				if(data[i][0].equals(object)){
					data[i][1] = true;
					break;
				}
			}
		}
		
	}


}

package com.airbusds.idea.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.airbusds.idea.config.InputFileConfig.IdValue;

@SuppressWarnings("serial")
public class InteractiveTable extends JTable {
	
	
	private InteractiveTableModel tableModel;

	public InteractiveTable(InteractiveTableModel model){
		super(model);
		this.tableModel = model;
		final String delete = "Delete";
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, delete);
		getActionMap().put(delete, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent ae) {
				deleteSelectedRow();
			}
		});
		
		tableModel.addTableModelListener(new InteractiveTableModelListener());
		TableColumn hidden = getColumnModel().getColumn(tableModel.HIDDEN_INDEX);
		hidden.setMinWidth(2);
		hidden.setPreferredWidth(2);
		hidden.setMaxWidth(2);
		hidden.setCellRenderer(new InteractiveRenderer(tableModel.HIDDEN_INDEX));
		
	}
	
	public static class InteractiveTableModel extends AbstractTableModel {
	    public int HIDDEN_INDEX;
	
	    private String[] columnNames;
	    private String[] columnTyps;
	    protected List<Object[]> dataList;
	
	    public InteractiveTableModel(String[] columnNames, String[] colTypes) {
	        this.columnNames = columnNames;
	        this.columnTyps = colTypes;
	        dataList = new ArrayList<Object[]>();
	        HIDDEN_INDEX = columnNames.length;
	    }
	
	    public String getColumnName(int column) {
	        if(column==HIDDEN_INDEX)
	        	return "";
	   	 	return columnNames[column];
	    }
	
	    public boolean isCellEditable(int row, int column) {
	        if (column == HIDDEN_INDEX) 
	       	 return false;
	        else 
	       	 return true;
	    }
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int column) {
	   	 	return String.class;
	    }
	
	    public Object getValueAt(int row, int column) {
	    	if (column == HIDDEN_INDEX)
	    		return "";
	    	else
	    		return this.dataList.get(row)[column];
	    }
	
	    public void setValueAt(Object value, int row, int column) {
		   	 Object[] rec = this.dataList.get(row);
		   	 String dataType = columnTyps[column];
		   	 switch(dataType){
			   	 case "int":{
			   		 rec[column] = Integer.parseInt(value.toString());
			   		 break;
			   	 }
			   	 case "float":{
			   		rec[column] = Float.parseFloat(value.toString());
			   		break;
			   	 }
			   	 case "string":{
			   		rec[column] = value;
			   		break;
			   	 }
			   	 
			   	 default:
			   		rec[column] = convertToCustomObject(value);
		   	 }
		   	 
		   	 fireTableCellUpdated(row, column);
	    }
	    
	    public Object convertToCustomObject(Object obj){
	    	return obj;
	    }
	
	    public int getRowCount() {
	        return dataList.size();
	    }
	
	    public int getColumnCount() {
	        return columnNames.length+1;
	    }
	
	    public boolean hasEmptyRow() {
	        if (dataList.size() == 0) return false;
	        Object[] record = dataList.get(dataList.size() - 1);
	        
	        boolean isEmpty = true;
	        for (int i = 0; i < record.length; i++) {
				Object object = record[i];
				if(object!=null){
					isEmpty = false;
					break;
				}
			}
	        
	       return isEmpty;
	    }
	
	    
	    /**
	     * This should be abstract.
	     */
	    public void addEmptyRow() {
	        dataList.add(new Object[this.columnNames.length+1]);
	        fireTableRowsInserted(
	           dataList.size() - 1,
	           dataList.size() - 1);
	    }
	    
	    public void deleteRow(int rowIndex){
	    	dataList.remove(rowIndex);
	    	fireTableRowsDeleted(rowIndex, rowIndex);	    
	    }

	}
	
	public class InteractiveTableModelListener implements TableModelListener {
	   public void tableChanged(TableModelEvent evt) {
	       if (evt.getType() == TableModelEvent.UPDATE) {
	           int column = evt.getColumn();
	           int row = evt.getFirstRow();
	           setColumnSelectionInterval(column + 1, column + 1);
	           setRowSelectionInterval(row, row);
	       }
	   }
	}
	
	static class InteractiveRenderer extends DefaultTableCellRenderer {
	   protected int interactiveColumn;
	
	   public InteractiveRenderer(int interactiveColumn) {
	       this.interactiveColumn = interactiveColumn;
	   }
	
	   public Component getTableCellRendererComponent(JTable table,
	      Object value, boolean isSelected, boolean hasFocus, int row,
	      int column)
	   {
		   InteractiveTableModel model = (InteractiveTableModel)table.getModel();
	       Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	       if (column == interactiveColumn && hasFocus) {
	           if ((model.getRowCount() - 1) == row &&
	              !model.hasEmptyRow())
	           {
	               model.addEmptyRow();
	           }
	
	           ((InteractiveTable)table).highlightLastRow(row);
	       }
	
	       return c;
	   }
	}
	
	public void deleteSelectedRow() {
		if (tableModel.getRowCount() < 2)
			return;
		if (getSelectedRow() > -1)
			tableModel.deleteRow(getSelectedRow());
	}
	
	public void highlightLastRow(int row) {
	   int lastrow = tableModel.getRowCount();
	   if (row == lastrow - 1) {
	       setRowSelectionInterval(lastrow - 1, lastrow - 1);
	   } else {
	       setRowSelectionInterval(row + 1, row + 1);
	   }
	
	   setColumnSelectionInterval(0, 0);
	}
	
	
	public Object[][] getData() {
		int rowC = tableModel.getRowCount();
		int colC = tableModel.getColumnCount()-1;
		
		if(tableModel.hasEmptyRow()){
			rowC--;
		}
		
		Object[][] data = new Object[rowC][colC];
		
		for(int i=0; i<rowC; i++){
			for(int j=0; j<colC; j++){
				data[i][j] = tableModel.getValueAt(i, j);
			}			
		}
		return data;
	}
	
	public void setData(Object[][] data){
		tableModel.dataList.clear();
		if(data==null || data.length==0){
			tableModel.addEmptyRow();
			return;
		}
		for (int i = 0; i < data.length; i++) {
			Object[] rec = data[i];
			tableModel.addEmptyRow();
			for (int j = 0; j < rec.length; j++) {
				if(rec[j]!=null){
					if(getCellEditor(i, j).getTableCellEditorComponent(this, null, false, i, j) instanceof JComboBox){
						tableModel.setValueAt(new IdValue( rec[j].toString(), null ), i, j);
					}else{
						tableModel.setValueAt(rec[j], i, j);
					}
				}
			}
		}
		createEmptyRow();
	}
	
	public void createEmptyRow(){
//		int rowC = tableModel.getRowCount();
//		if(isSingleRow && rowC>0)
//			return;
		if(!tableModel.hasEmptyRow()){
			tableModel.addEmptyRow();
		}
	}

}
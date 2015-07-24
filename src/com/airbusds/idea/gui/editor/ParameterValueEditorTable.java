package com.airbusds.idea.gui.editor;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.IdValue;
import com.airbusds.idea.gui.NumberCellEditor;
import com.airbusds.idea.gui.TableValidator;
import com.airbusds.idea.gui.TableValidator.ValidationResult;

@SuppressWarnings("serial")
public class ParameterValueEditorTable extends JScrollPane {
	
	public String[] columnNames;
	protected JTable table;
	protected InteractiveTableModel tableModel;
	private boolean isSingleRow;
	
	private TableValidator validator;
	
	public ParameterValueEditorTable(String[] colNames, String[] colTypes, boolean singleRow ){
		super(new JTable());
		
		columnNames = colNames;
		table = (JTable)getViewport().getView();
		
		// single row stuff
		this.isSingleRow = singleRow;
		
		final String delete = "Delete";
		KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
		table.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, delete);
		table.getActionMap().put(delete, new AbstractAction() {
			@Override public void actionPerformed(ActionEvent ae) {
				tableModel.deleteSelectedRow();
			}
		});
		
		
		tableModel = new InteractiveTableModel(columnNames, colTypes);
        tableModel.addTableModelListener(new InteractiveTableModelListener());
        table.setModel(tableModel);
        table.setSurrendersFocusOnKeystroke(true);
        createEmptyRow();
        
        TableColumn hidden = table.getColumnModel().getColumn(tableModel.HIDDEN_INDEX);
        hidden.setMinWidth(2);
        hidden.setPreferredWidth(2);
        hidden.setMaxWidth(2);
        
        for (int i = 0; i < colTypes.length; i++) {
			String type = colTypes[i];
			
			if(type.equals("int")){
				table.getColumnModel().getColumn(i).setCellEditor(new NumberCellEditor(NumberCellEditor.INT));
			}else if(type.equals("float")){
				table.getColumnModel().getColumn(i).setCellEditor(new NumberCellEditor(NumberCellEditor.FLOAT));
			}else if(type.equals("string") || type.equals("file")){
				
			}else if(type.indexOf("list:")==0){
				// its a list
				String listName = type.substring(type.indexOf(':')+1, type.length());
				List<IdValue> list = InputFileConfig.getList(listName);
				TableColumn comboColumn = table.getColumnModel().getColumn(i);
				
				JComboBox<IdValue> combo = new JComboBox<IdValue>(list.toArray(new IdValue[list.size()]));
		        comboColumn.setCellEditor(new DefaultCellEditor(combo));
			}
		}

        // make table with single row
        if(!singleRow){
        	hidden.setCellRenderer(new InteractiveRenderer(tableModel.HIDDEN_INDEX));
        	table.setPreferredScrollableViewportSize(
				    new Dimension(table.getWidth(), table.getRowHeight()*3 + table.getTableHeader().getPreferredSize().height));
        }else{
			table.setPreferredScrollableViewportSize(
			    new Dimension(table.getWidth(), table.getRowHeight() + table.getTableHeader().getPreferredSize().height));
		}
        revalidate();        
	}

	public class InteractiveTableModel extends AbstractTableModel {
	     public int HIDDEN_INDEX;

	     protected String[] columnNames;
	     protected String[] columnTyps;
	     protected Vector<Object[]> dataVector;

	     public InteractiveTableModel(String[] columnNames, String[] colTypes) {
	         this.columnNames = columnNames;
	         this.columnTyps = colTypes;
	         dataVector = new Vector<Object[]>();
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
//             String type = columnTyps[column];
//	    	 if(type.equals("int"))
//	    		 return Integer.class;
//	    	 else if(type.equals("float"))
//	    		 return Float.class;
	    	 
	    	 return String.class;
	     }

	     public Object getValueAt(int row, int column) {
	    	 Object res = this.dataVector.get(row)[column];
	    	 return res;
	     }

	     public void setValueAt(Object value, int row, int column) {
	    	 Object[] rec = this.dataVector.get(row);
	    	 rec[column] = value;
	         fireTableCellUpdated(row, column);
	     }

	     public int getRowCount() {
	         return dataVector.size();
	     }

	     public int getColumnCount() {
	         return columnNames.length+1;
	     }

	     public boolean hasEmptyRow() {
	         if (dataVector.size() == 0) return false;
	         Object[] record = dataVector.get(dataVector.size() - 1);
	         
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

	     public void addEmptyRow() {
	         dataVector.add(new Object[this.columnNames.length+1]);
	         fireTableRowsInserted(
	            dataVector.size() - 1,
	            dataVector.size() - 1);
	     }
	     
	     public void deleteSelectedRow(){
	    	 if(dataVector.size()<2)
	    		 return;
	    	 if(table.getSelectedRow()>-1)
	    		 dataVector.remove(table.getSelectedRow());
	    	 fireTableRowsDeleted(dataVector.size() - 1,
	            dataVector.size() - 1);
	     }
	 }
	
    public class InteractiveTableModelListener implements TableModelListener {
        public void tableChanged(TableModelEvent evt) {
            if (evt.getType() == TableModelEvent.UPDATE) {
                int column = evt.getColumn();
                int row = evt.getFirstRow();
                table.setColumnSelectionInterval(column + 1, column + 1);
                table.setRowSelectionInterval(row, row);
            }
        }
    }
    
    class InteractiveRenderer extends DefaultTableCellRenderer {
        protected int interactiveColumn;

        public InteractiveRenderer(int interactiveColumn) {
            this.interactiveColumn = interactiveColumn;
        }

        public Component getTableCellRendererComponent(JTable table,
           Object value, boolean isSelected, boolean hasFocus, int row,
           int column)
        {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == interactiveColumn && hasFocus) {
                if ((tableModel.getRowCount() - 1) == row &&
                   !tableModel.hasEmptyRow())
                {
                    tableModel.addEmptyRow();
                }

                highlightLastRow(row);
            }

            return c;
        }
    }
    
    public void highlightLastRow(int row) {
        int lastrow = tableModel.getRowCount();
        if (row == lastrow - 1) {
            table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
        } else {
            table.setRowSelectionInterval(row + 1, row + 1);
        }

        table.setColumnSelectionInterval(0, 0);
    }


	public Object[][] getData() {
		int rowC = tableModel.getRowCount();
		int colC = columnNames.length;
		
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
		tableModel.dataVector.clear();
		if(data==null || data.length==0){
			tableModel.addEmptyRow();
			return;
		}
		for (int i = 0; i < data.length; i++) {
			Object[] rec = data[i];
			tableModel.addEmptyRow();
			for (int j = 0; j < rec.length; j++) {
				if(rec[j]!=null){
					if(table.getCellEditor(i, j).getTableCellEditorComponent(table, null, false, i, j) instanceof JComboBox){
						tableModel.setValueAt(new IdValue( rec[j].toString(), null ), i, j);
					}else{
						tableModel.setValueAt(rec[j], i, j);
					}
				}
			}
		}
		createEmptyRow();
	}
	
	public TableValidator getValidator() {
		return validator;
	}


	public void setValidator(TableValidator validator) {
		this.validator = validator;
	}


	public ValidationResult validateData(){
		
		TableCellEditor tce = table.getCellEditor();
		if(tce!=null)
			tce.stopCellEditing();
		
		if(validator!=null){
			ValidationResult result = validator.validateTableData(getData(), tableModel.columnNames);
			if(result.isError){
				JOptionPane.showMessageDialog(this.getParent(), result.message);
				table.editCellAt(result.errRow, result.errCol);
				return result;
			}
		}
		ValidationResult result = new ValidationResult();
		result.isError = false;
		return result;
	}
	
	private void createEmptyRow(){
		int rowC = tableModel.getRowCount();
		if(isSingleRow && rowC>0)
			return;
		if(!tableModel.hasEmptyRow()){
			tableModel.addEmptyRow();
		}
	}

	
}

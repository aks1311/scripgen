package com.airbusds.idea.gui;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import net.coderazzi.filters.examples.TableSorter;
import net.coderazzi.filters.gui.TableFilterHeader;
import net.coderazzi.filters.gui.editor.FilterEditor;

import com.airbusds.Utilities;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.model.Value;

@SuppressWarnings("serial")
public class PAValueMappingTable extends JScrollPane {

	protected JTable table;
	PAValueTableModel tableModel;
	
	TableFilterHeader filterHeader;
	TableSorter tableSorter;
	
	public PAValueMappingTable() {
		super(new JTable());
		table = (JTable) getViewport().getView();
	}
	
	public void setData(ExecutionPlan plan, List<ParamValueCombination> data){
		tableModel = new PAValueTableModel(plan, data);
		tableSorter = new TableSorter(tableModel, table.getTableHeader());
		table.setModel(tableSorter);
		
		filterHeader = new TableFilterHeader(table){
    		@Override
    		protected void customizeEditor(FilterEditor editor) { // TODO 2 Filter does not show mouse events
    			super.customizeEditor(editor);
    			
    			editor.setListCellRenderer(new ListCellRenderer() {

					@Override
					public Component getListCellRendererComponent(
							JList list, Object value, int index,
							boolean isSelected, boolean cellHasFocus) {
						JLabel text = new JLabel();
						
						if (value instanceof Boolean){
							text.setText(((Boolean)value).toString());
						} else if (value.getClass().isEnum()){
							text.setText(value.toString());
						} else {
		                    text.setText( PAValueUtility.getPAValueString( (Value[][])value ) );
		                }
						
						return text;
					}
    				
				});
//    			editor.unsetAutoOptions();
//                
//                Class<?> c = table.getModel().getColumnClass(editor.getFilterPosition());
//                List<Object> options;
//                if (c.equals(Boolean.class)){
//                	options = new ArrayList<Object>(3);
//                	options.add(true);
//                	options.add(false);
//                } else if (c.isEnum()){
//                	Object[] enums = c.getEnumConstants();
//                	options = new ArrayList<Object>(enums.length+1);
//                	for (Object each : enums){
//                		options.add(each);
//                	}
//                } else {
//                	int column = editor.getFilterPosition();
//                	options = new ArrayList<Object>(tableModel.getRowCount() + 1);
//                	int firstRow = 0;
//    				int lastRow = tableModel.getRowCount() - 1;
//                    lastRow = Math.min(tableModel.getRowCount() - 1, lastRow);
//                    while (lastRow >= firstRow) {
//                    	options.add( PAValueUtility.getPAValueString( (Value[][])tableModel.getValueAt(firstRow++, column) ));
//                    }
//                }
//    			
//                editor.setOptions(options);
//    			editor.setAutoOptions(tableModel);
    		}
    		
    		
    	};
    	
    	filterHeader.setAutoOptions(true);
    	
    	TableColumn hidden = table.getColumnModel().getColumn(tableModel.SELECT_INDEX);
        hidden.setMinWidth(50);
        hidden.setPreferredWidth(50);
        hidden.setMaxWidth(50);
        
//        table.getColumnModel().getColumn(0).setCellRenderer(new VariableRowHeightRenderer());
        table.setDefaultRenderer(String.class, new VariableRowHeightRenderer());
	
	}
	
	public List<ParamValueCombination> getSelected(){
		return ((PAValueTableModel)table.getModel()).getSelected();
	}
	
	public class PAValueTableModel extends AbstractTableModel {

	     protected List<ParamValueCombination> dataList;
	     protected Boolean[] selected;
	     protected String[] colNames;
	     int SELECT_INDEX;
	     int LABEL_INDEX = 0;
	     ExecutionPlan plan;

	     public PAValueTableModel(ExecutionPlan plan, List<ParamValueCombination> data) {
	    	 this.plan = plan;
	         dataList = data;
	         selected = new Boolean[dataList.size()];
	         
	         {	// Set the column Names
	        	 String[] params = dataList.get(0).getParams();
		         SELECT_INDEX = params.length + 1;
		         colNames = new String[params.length + 1];
		         
		         for (int i = 0; i < params.length; i++) {
					colNames[i] = params[i]; //Utilities.getParamDisplayName(params[i]);
		         }
		         colNames[SELECT_INDEX-1] = "Select";
	         }
	         
	         for (int i = 0; i < dataList.size(); i++) {
	        	 if(plan.getValueCombinationList().contains(dataList.get(i)))
	        		 selected[i] = true;
	        	 else
	        		 selected[i] = false;
	         }
	         for (Iterator<ParamValueCombination> it = plan.getValueCombinationList().iterator(); it.hasNext();) {
				ParamValueCombination comb = it.next();
				if(!dataList.contains(comb))
					it.remove();
	         }
	         
	     }

	     public String getColumnName(int column) {
	         if (column == SELECT_INDEX) 
	        	 return "";
	         else if(column == 0)
	        	 return "Label";
	         else
		    	 return colNames[column-1];
	     }

	     public boolean isCellEditable(int row, int column) {
	         if (column == SELECT_INDEX || column == LABEL_INDEX) 
	        	 return true;
	         else 
	        	 return false;
	     }

	    @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int column) {
	         if (column == SELECT_INDEX) 
	        	 return Boolean.class;
	         else if(column == 0)
	        	 return String.class;
	         else
	        	 return String.class;
	     }

	     public Object getValueAt(int row, int column) {
	         if (column == SELECT_INDEX){
	        	 return selected[row];
	         }else if(column == 0){
	        	 return this.dataList.get(row).getLabel();
	         }else {
	        	 return this.dataList.get(row).getValues()[column-1];
	         }
	     }

	     public void setValueAt(Object value, int row, int column) {
	         if (column == SELECT_INDEX){
	        	 selected[row] = (Boolean)value;
	        	 if(selected[row]){ // adding
	        		 plan.getValueCombinationList().add(dataList.get(row));
	        	}else{ // deleting
	        		plan.getValueCombinationList().remove(dataList.get(row));
	        	 }
	         }else if (column == LABEL_INDEX){
	        	 if(isLabelUnique((String)value, row, column)){
	        		 this.dataList.get(row).setLabel((String)value);
	        	 }else{
	        		 Utilities.showMessage("Not a unique label! Try again.", "Error");
	        	 }
	         }
	         fireTableCellUpdated(row, column);
	     }
	     
		private boolean isLabelUnique(String newVal, int row, int column) {
			int rowCnt = 0;
			for (ParamValueCombination cell : this.dataList) {
				if (rowCnt++ == row)
					continue;
				if (cell.getLabel().equalsIgnoreCase(newVal))
					return false;
			}
			return true;
		}

	     public int getRowCount() {
	         return dataList.size();
	     }

	     public int getColumnCount() {
	         return colNames.length + 1;
	     }
	     
	     public List<ParamValueCombination> getSelected(){
	    	 List<ParamValueCombination> result = new ArrayList<ParamValueCombination>(); 
	    	 TableModel model = table.getModel();
	 		 int rc = model.getRowCount();
	 		 for (int i = 0; i < rc; i++) {
	 			 if(selected[i])
	 				 result.add(this.dataList.get(i));
	 		 }
	 		 return result;
	     }

	 }

}

@SuppressWarnings("serial")
class VariableRowHeightRenderer extends DefaultTableCellRenderer {
	private static String HTML_LINE_BREAK = "<br/>";
	private static String RANGE_SEPERATOR = " : ";
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(column!=0){
			Value[][] vData = (Value[][])value;
			
			StringBuffer sbTableText = new StringBuffer();
			sbTableText.append("<html>");
			for (int i = 0; i < vData.length; i++) {
				Value[] vRow = vData[i];
				if(vRow==null) continue;
				for (int j = 0; j < (vRow.length); j++) {
					if(vRow[j]==null)
						continue;
					if(j>0)
						sbTableText.append(RANGE_SEPERATOR);
					sbTableText.append(vRow[j]);
				}
				sbTableText.append(HTML_LINE_BREAK);
			}
			setText(sbTableText.toString());
		}else{
			setText((String)value);
		}
		
		if(table.getRowHeight(row) <= (getPreferredSize().height+5))
			table.setRowHeight(row, getPreferredSize().height+5);
		return c;
	}
}

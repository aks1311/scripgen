package com.airbusds.idea.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import com.airbusds.Utilities;
import com.airbusds.idea.gui.InteractiveTable;
import com.airbusds.idea.gui.InteractiveTable.InteractiveTableModel;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class TableListEditorTable extends JScrollPane {
	
	InteractiveTable viewTable;
	TableListTableModel tableModel;
	Parameter param;
	
	public TableListEditorTable(final Parameter param){
		super(new InteractiveTable( new TableListTableModel(param.getParameterAnalysis().getPaTableList(), param.getColumnNames())));
		if( !param.getIsTabularData() || param.isSingleRow() ){
			throw new RuntimeException("Invalid State: Parameter not a multirow table.");
		}
		
		this.param = param;
		
		viewTable = (InteractiveTable)getViewport().getView();
		viewTable.setPreferredScrollableViewportSize(
			    new Dimension(viewTable.getWidth(), viewTable.getRowHeight()*3 + viewTable.getTableHeader().getPreferredSize().height));
		
		tableModel = (TableListTableModel)viewTable.getModel();
		if(tableModel.getRowCount()==0)
			viewTable.createEmptyRow();
		
		viewTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( e.getClickCount() == 2 ) 
                { 
					InteractiveTable table = (InteractiveTable)e.getSource(); 
					final int index = table.getSelectedRow();
					
					JPanel pan = new JPanel();
					pan.setLayout(new BorderLayout());
					InteractiveTableModel dialogModel = new InteractiveTableModel(param.getColumnNames(), param.getColumnTypes());
					final InteractiveTable tableEditor = new InteractiveTable(dialogModel);
					if(tableModel.getRowCount()>index)
						tableEditor.setData((Value[][])tableModel.getValueAt(index, 0));
					else
						tableEditor.createEmptyRow();
					pan.add(new JScrollPane(tableEditor), BorderLayout.CENTER);
					
					JPanel buttonPanel = new JPanel();
					pan.add(buttonPanel, BorderLayout.SOUTH);
					JButton okBtn = new JButton("OK");
					buttonPanel.add(okBtn);
					
					final JDialog jd = new JDialog();
					jd.add(pan);
					
					// TODO 1 Fix modality of dialog
					//jd.setModalityType(ModalityType.APPLICATION_MODAL);
					jd.setSize(500, 300);
					Utilities.centerComponentOnScreen(jd);
					jd.setVisible(true);
					
					okBtn.addActionListener(new ActionListener() {
						@Override public void actionPerformed(ActionEvent e) {
							tableModel.setValueAt(tableEditor.getData(), index, 0);
							jd.dispose();
						}
					});
                } 
				
			}
		});
		
		viewTable.getColumnModel().getColumn(0).setCellRenderer(new VariableRowHeightRenderer());
		
	}
	
	public List<Value[][]> getData() {
		List<Value[][]> result = new ArrayList<Value[][]>();
		Object[][] data = viewTable.getData();
		for (int i = 0; i < data.length; i++) {
			Object[] row = data[i];
			for (int j = 0; j < row.length; j++) {
				Object cell = row[j];
				result.add((Value[][])cell);
			}
		}
		return result;
	}

}

@SuppressWarnings("serial")
class TableListTableModel extends InteractiveTableModel {

	private static String DELIM = " : ";
	String[] colNames;
	
	TableListTableModel(List<Value[][]> tableList, String[] colNames){
		super(new String[]{StringUtils.arrayToString(colNames, DELIM)}, new String[]{"custom"});
		this.colNames = colNames;
		
		for (Iterator<Value[][]> it = tableList.iterator(); it.hasNext();) {
			dataList.add(new Value[][][]{it.next()});
		}
		
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
//	@Override
//	public Object getValueAt(int rowIndex, int columnIndex) {
//		if(columnIndex==0){
//			Value[][] table = ((Value[][][])dataList.get(rowIndex)[0])[0];
//			
//			StringBuffer sbTableText = new StringBuffer();
//			sbTableText.append("<html>");
//			for (int i = 0; i < table.length; i++) {
//				Value[] row = table[i];
//				if(row==null) continue;
//				for (int j = 0; j < (row.length-1); j++) {
//					if(j>0)
//						sbTableText.append(DELIM);
//					sbTableText.append(row[j]);
//				}
//				sbTableText.append(LINE_BREAK);
//			}
//			return sbTableText.toString();
//		}else{
//			return rowIndex;
//		}
//	}
	
	@Override
	public Object convertToCustomObject(Object obj) {
		Object[][] table = (Object[][])obj;
		Value[][] newTable = new Value[table.length][];
		
		for (int i = 0; i < table.length; i++) {
			Object[] row = table[i];
			Value[] newRow = new Value[row.length];
			newTable[i] = newRow;
			
			for (int j = 0; j < row.length; j++) {
				Object cell = row[j];
				if(cell instanceof Integer || cell instanceof Float || cell instanceof String){
					newRow[j] = Value.valueFactory(cell);
				}else{
//					System.err.println("Something wrong >> "+cell.getClass()+" at "+i+", "+j);
				}
			}
			
		}
		return newTable;
	}
	
    public boolean hasEmptyRow() {
        if (dataList.size() == 0) return false;
        Value[][][] record = (Value[][][])dataList.get(dataList.size() - 1);
        
        for (int i = 0; i < record.length; i++) {
        	Value[][] table = record[i];
			if(table!=null && table.length>0){
				for (int j = 0; j < table[0].length; j++) {
					if(table[0][j]!=null){
						return false;
					}
				}
			}
		}
        
       return true;
    }
	
	@Override
	public void addEmptyRow() {
        dataList.add(new Value[1][1][this.colNames.length+1]);
        fireTableRowsInserted(
           dataList.size() - 1,
           dataList.size() - 1);
	}
	
}

@SuppressWarnings("serial")
class VariableRowHeightRenderer extends DefaultTableCellRenderer {
	private static String HTML_LINE_BREAK = "<br/>";
	private static String RANGE_SEPERATOR = " : ";
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		if(column==0){
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
			setText(row+"");
		}
		
		table.setRowHeight(row, getPreferredSize().height + 5);
		return c;
	}
}

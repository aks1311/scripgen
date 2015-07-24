package com.airbusds.idea.gui;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class IterationsTableModel extends AbstractTableModel implements
		TableModel {

	private Double[][] data;
	private Boolean[] selectColumn;
	private int selectColumnIndex;
	private int curveCount;
	private String flutterVariable;
	
	XYSeriesCollection dataSet;
	
	public IterationsTableModel(Double[][] data, String flutterVariable) {
		super();
		this.data = data;
		selectColumn = new Boolean[data.length];
		curveCount = data[0].length - 1;
		selectColumnIndex = curveCount + 1;
		this.flutterVariable = flutterVariable;
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return curveCount + 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		if(columnIndex == selectColumnIndex){
			if(selectColumn[rowIndex] == null)
				selectColumn[rowIndex] = true;
			result = selectColumn[rowIndex];
		}else
			result = data[rowIndex][columnIndex];
		
		return result;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		selectColumn[rowIndex] = (Boolean)aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
		dataSet.removeAllSeries();
		fillDataSetWithSeries(dataSet, data);
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == selectColumnIndex){
			return "Select";
		}else if(column == 0){
			return "Flutter Variable ("+this.flutterVariable+")";
		}else
			return "Curve "+column;

	}
	
	public XYSeriesCollection getDataSet(){
		if(dataSet == null){
			dataSet = new XYSeriesCollection();
			fillDataSetWithSeries(dataSet, data);
		}
		
		return dataSet;
	}
	
	@Override
	public Class getColumnClass(int c) {
		if(c == selectColumnIndex)
			return Boolean.class;
		else
			return Double.class;
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		if(col == selectColumnIndex){
			return true;
		}else
			return false;
    }
	
	private void fillDataSetWithSeries(XYSeriesCollection dataset, Double[][] data){
		for (int i = 0; i < curveCount; i++) {
			XYSeries series = new XYSeries("Curve "+(i+1), false);
			for (int j = 0; j < data.length; j++) {
				if((Boolean)getValueAt(j, selectColumnIndex) && data[j][i+1]!=null && data[j][0]!=null){
					series.add(data[j][i+1], data[j][0]);
				}
			}
			dataset.addSeries(series);
		}
	}

}

package com.airbusds.idea.gui;

import javax.swing.table.AbstractTableModel;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class EigenValueTableModel extends AbstractTableModel {

	private Double[][] reData;
	private Double[][] imData;
	private Boolean[] selectColumn;
	private int curveCount;
	private int selectColumnIndex;
	private XYSeriesCollection dataSet;
	private String flutterVariable;

	// TODO 1 Variable names not correct Re/Im
	public EigenValueTableModel(Double[][] reData, Double[][] imData, String flutterVariable) {
		super();
		this.reData = reData;
		this.imData = imData;
		curveCount = reData[0].length-1;
		selectColumn = new Boolean[reData.length];
		selectColumnIndex = curveCount*2 + 1;
		this.flutterVariable = flutterVariable;
	}
	
	
	@Override
	public int getRowCount() {
		return reData.length;
	}

	@Override
	public int getColumnCount() {
		return curveCount*2 + 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		if(columnIndex == selectColumnIndex){
			if(selectColumn[rowIndex] == null)
				selectColumn[rowIndex] = true;
			result = selectColumn[rowIndex];
		}else if(columnIndex == 0){
			result = reData[rowIndex][0];
		}else if(columnIndex%2 == 0){
			result = imData[rowIndex][(1+columnIndex)/2];
		}else{
			result = reData[rowIndex][(1+columnIndex)/2];
		}
		return result;
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		selectColumn[rowIndex] = (Boolean)aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
		dataSet.removeAllSeries();
		reloadDataSet();
	}

	public XYDataset getDataSet() {
		if(dataSet == null){
			dataSet = new XYSeriesCollection();
			reloadDataSet();
		}
		
		return dataSet;
	}
	
	private void reloadDataSet(){
		for (int i = 1; i <= curveCount; i++) {
			XYSeries series = new XYSeries("Curve "+i, false);
			for (int j = 0; j < reData.length; j++) {
				if((Boolean)getValueAt(j, selectColumnIndex) && reData[j][i]!=null && imData[j][i]!=null){
					if(Double.isInfinite(imData[j][i]) || Double.isInfinite(reData[j][i]))
						continue;
					series.add(imData[j][i], reData[j][i]);
				}
			}
			dataSet.addSeries(series);
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		if(col == selectColumnIndex){
			return true;
		}else
			return false;
    }
	
	@Override
	public Class getColumnClass(int c) {
		if(c == selectColumnIndex)
			return Boolean.class;
		else
			return Double.class;
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == selectColumnIndex){
			return "Select";
		}else if(column == 0){
			return "Flutter Variable ("+this.flutterVariable+")";
		}else if(column%2 == 0)
			return "Curve "+((1 + column)/2)+" (Re)";
		else
			return "Curve "+((1 + column)/2)+" (Im)";

	}

}

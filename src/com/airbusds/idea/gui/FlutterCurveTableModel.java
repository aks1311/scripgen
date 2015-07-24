package com.airbusds.idea.gui;

import javax.swing.table.AbstractTableModel;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

@SuppressWarnings("serial")
public class FlutterCurveTableModel extends AbstractTableModel {

	private Double[][] vgData;
	private Double[][] vfData;
	private Boolean[] selectColumn;
	private int selectColumnIndex;
	private int curveCount;
	private String flutterVariable;
	
	XYSeriesCollection vgDataSet;
	XYSeriesCollection vfDataSet;
	
	public FlutterCurveTableModel(Double[][] vgData, Double[][] vfData, String flutterVariable) {
		super();
		this.vgData = vgData;
		this.vfData = vfData;
		selectColumn = new Boolean[vgData.length];
		curveCount = vgData[0].length - 1;
		selectColumnIndex = curveCount*2 + 1;
		this.flutterVariable = flutterVariable;
	}
	
	@Override
	public int getRowCount() {
		return vgData.length;
	}

	@Override
	public int getColumnCount() {
		return vgData[0].length * 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Object result = null;
		if(columnIndex == selectColumnIndex){
			if(selectColumn[rowIndex] == null)
				selectColumn[rowIndex] = true;
			result = selectColumn[rowIndex];
		}else if(columnIndex > curveCount)
			result = vfData[rowIndex][columnIndex - curveCount];
		else
			result = vgData[rowIndex][columnIndex];
		
		return result;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		selectColumn[rowIndex] = (Boolean)aValue;
		fireTableCellUpdated(rowIndex, columnIndex);
		vgDataSet.removeAllSeries();
		vfDataSet.removeAllSeries();
		fillDataSetWithSeries(vgDataSet, vgData);
		fillDataSetWithSeries(vfDataSet, vfData);
	}
	
	@Override
	public String getColumnName(int column) {
		if(column == selectColumnIndex){
			return "Select";
		}else if(column == 0){
			return "Flutter Variable ("+this.flutterVariable+")";
		}else if(column > curveCount)
			return "Vf Curve "+(column-curveCount);
		else
			return "Vg Curve "+column;

	}
	
	public XYSeriesCollection getVfDataSet(){
		if(vfDataSet == null){
			vfDataSet = new XYSeriesCollection();
			fillDataSetWithSeries(vfDataSet, vfData);
		}
		
		return vfDataSet;
	}
	
	public XYSeriesCollection getVgDataSet(){
		if(vgDataSet == null){
			vgDataSet = new XYSeriesCollection();
			fillDataSetWithSeries(vgDataSet, vgData);
		}
		
		return vgDataSet;
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
					if(Double.isInfinite(data[j][i+1]) || Double.isInfinite(data[j][0]))
						continue;
					series.add(data[j][i+1], data[j][0]);
				}
			}
			dataset.addSeries(series);
		}
	}
	
	
}

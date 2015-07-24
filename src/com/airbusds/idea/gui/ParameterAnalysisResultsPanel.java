package com.airbusds.idea.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.InputFileSerializer;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.model.XYPlot;
import com.airbusds.idea.utilities.StringUtils;
import com.airbusds.idea.utilities.TableDataHelper;

public class ParameterAnalysisResultsPanel extends XYPlot implements ItemListener{
	
	// TODO 1 Mouse over tool tip for curves
	
	private AnalysisInfo info;
	private ExecutionPlan currentPlan;
	private XYLineAndShapeRenderer lineRenderer;
	private XYSeriesCollection dataSet = new XYSeriesCollection();
	private Parameter refParam;
	private boolean isResultSpeed = true;
	private boolean isBoundaryMax = true;
	private LegendTitle legend;
	private NumberAxis xAxis;
	private NumberAxis yAxis;
	
	public ParameterAnalysisResultsPanel(AnalysisInfo info){
		this.info = info;
		createControlsPanel();
		createChart();
	}
	
	@Override
	public JComponent createChart() {
		
		yAxis = new NumberAxis("Y axis");  // y axis
		
		if(refParam.getIsTabularData()){
			xAxis = new SymbolAxis("X Axis",
				    new String[]{"Values 1","Values 2","Values 3","Values 4","Values 5","Values 6","Values 7"});
		}else{
			xAxis = new NumberAxis("X Axis"); // x axis
		}
		
		lineRenderer = new XYLineAndShapeRenderer();
		lineRenderer.setSeriesShapesVisible(0, false);
		
		chart = ChartFactory.createXYLineChart("Parameter Studies", xAxis.getLabel(), yAxis.getLabel(), dataSet, PlotOrientation.VERTICAL, true, true, false);
		chart.getXYPlot().setRenderer(lineRenderer);
	    chart.setBackgroundPaint(Color.white);
	    chart.getXYPlot().getRenderer().setSeriesStroke(0, new BasicStroke(
                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[] {6.0f, 6.0f}, 0.0f
            ));
	    
		chartPanel = new ChartPanel(chart, false, true, false, false, true);
		chartPanel.setPopupMenu(createPopUpMenu());
	    
		reloadDataSet();

		showTitle();
		legend = chart.getLegend();
		
		JPanel blPanel = new JPanel();
		blPanel.setLayout(new BorderLayout());
		blPanel.add(chartPanel, BorderLayout.CENTER);
		blPanel.add(createControlsPanel(), BorderLayout.SOUTH);
		blPanel.setVisible(false);
		
		return blPanel;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		
		JCheckBox cb = (JCheckBox)e.getSource();
		String action = cb.getActionCommand();
		
		switch(action){
			case "SHOW.HEADINGS":{
				if(cb.isSelected()){
					showTitle();
				}else{
					hideTitle();
				}
				break;
			}
			case "SHOW.LEGENDS":{
				if(cb.isSelected()){
					showLegends();
				}else{
					hideLegend();
				}
				break;
			}
		}
		
	}


	private void reloadDataSet() {
		lineRenderer.setBaseShapesVisible(true);
		lineRenderer.setBaseLinesVisible(true);
		
		dataSet.removeAllSeries();
		ExecutionPlan plan = getCurrentPlan();
		
		if(plan != null && refParam!= null){
			if(refParam.getIsTabularData()){
				xAxis = new SymbolAxis(Utilities.getParamDisplayName(refParam),
					    new String[]{"Values 1","Values 2","Values 3","Values 4","Values 5","Values 6","Values 7"});
			}else{
				xAxis = new NumberAxis(Utilities.getParamDisplayName(refParam)); // x axis
			}
			yAxis.setLabel(isResultSpeed?"Flutter Speed":"Flutter Frequency");
			chart.getXYPlot().setDomainAxis(xAxis);
			chart.getXYPlot().setRangeAxis(yAxis);
			
			Set<ParamValueCombination> values = plan.getValueCombinationList();
			
			XYSeries boundarySeries = new XYSeries("boundary");
			dataSet.addSeries(boundarySeries);
			
			Map<String, XYSeries> seriesMap = new HashMap<String, XYSeries>();
			Map<Number, Double> boundaryValueMap = new HashMap<Number, Double>();
			Map<String, Integer> refParamTicks = new HashMap<String, Integer>();
			
			for (Iterator<ParamValueCombination> it = values.iterator(); it.hasNext();) {
				ParamValueCombination valueCombination = it.next();
				String key = generateKey(valueCombination);
				XYSeries series = seriesMap.get(key);
				if(series == null){
					series = new XYSeries(key);
					seriesMap.put(key, series);
					dataSet.addSeries(series);
				}
				Number refParamValue = null;
				
				if(!refParam.getIsTabularData()){
					refParamValue = getSimpleParamValue(valueCombination);
				}else{
					String refKey = getTabularParamValue(valueCombination);
					if(refParamTicks.containsKey(refKey))
						refParamValue = refParamTicks.get(refKey);
					else{
						refParamValue = Integer.valueOf( refParamTicks.keySet().size()+1 );
						refParamTicks.put(refKey, refParamValue.intValue());
					}
				}
				
				getSimpleParamValue(valueCombination);
				
				;
				double tempBoundary = getBoundaryValue(valueCombination);
				if(isBoundaryMax){
					double boundaryValue = Double.MIN_VALUE;
					if(boundaryValueMap.containsKey(refParamValue))
						boundaryValue = boundaryValueMap.get(refParamValue);
					if(tempBoundary > boundaryValue)
						boundaryValueMap.put(refParamValue, tempBoundary);
				}else{
					double boundaryValue = Double.MAX_VALUE;
					if(boundaryValueMap.containsKey(refParamValue))
						boundaryValue = boundaryValueMap.get(refParamValue);
					if(tempBoundary < boundaryValue)
						boundaryValueMap.put(refParamValue, tempBoundary);
				}
				
				
				Number resultValue = getResultValue(valueCombination);
				if(refParamValue!=null){
					series.add(refParamValue, resultValue);
				}
			}
			
			for (Iterator<Number> it = boundaryValueMap.keySet().iterator(); it.hasNext();) {
				Number refParamValue = it.next();
				double boundaryValue = boundaryValueMap.get(refParamValue);
				boundarySeries.add(refParamValue, boundaryValue);
			}
		}
		
		
	}

	private String getTabularParamValue(ParamValueCombination valueCombination) {
		String path = info.getAnalysisLocation() + File.separatorChar + valueCombination.getLabel() + File.separatorChar + "param.in";
		HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(path);
		String[][] paramValues = parameterValueMap.get(refParam.getName());
		
		return Arrays.deepToString(paramValues);
	}

	private Number getResultValue(ParamValueCombination valueCombination) {
		String path = info.getAnalysisLocation() + File.separatorChar + valueCombination.getLabel() + File.separatorChar + "param.in";
		HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(path);
		
		String[][] paramValues = parameterValueMap.get("flutter_points");
		double criticalSpeed = Double.MAX_VALUE;
		double criticalFreq = 0;
		
		if(paramValues!=null){
			String flutterPointsResult = paramValues[0][0];
			flutterPointsResult = StringUtils.removeQuotesFromFileName(flutterPointsResult);
			String resultsFile = info.getAnalysisLocation() + File.separatorChar + valueCombination.getLabel() + File.separatorChar + flutterPointsResult;
			Double[][] result = TableDataHelper.readVfFile(resultsFile);
			for (int i = 0; i < result.length; i++) {
				if(criticalSpeed>result[i][1]){
					criticalSpeed = result[i][1];
					criticalFreq = result[i][2];
				}
			}
		}
		if(isResultSpeed){
			return criticalSpeed;
		}else{
			return criticalFreq;
		}
	}
	
	private double getBoundaryValue(ParamValueCombination valueCombination) {
		String path = info.getAnalysisLocation() + File.separatorChar + valueCombination.getLabel() + File.separatorChar + "param.in";
		HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(path);
		
		String[][] paramValues = parameterValueMap.get("flutter_points");
		double criticalSpeed = 0;
		double criticalFreq = 0;
		
		if(paramValues!=null){
			String flutterPointsResult = paramValues[0][0];
			flutterPointsResult = StringUtils.removeQuotesFromFileName(flutterPointsResult);
			String resultsFile = info.getAnalysisLocation() + File.separatorChar + valueCombination.getLabel() + File.separatorChar + flutterPointsResult;
			Double[][] result = TableDataHelper.readVfFile(resultsFile);
			for (int i = 0; i < result.length; i++) {
				
				if(isBoundaryMax){
					if(criticalSpeed<result[i][1]){
						criticalSpeed = result[i][1];
					}
					if(criticalFreq<result[i][2]){
						criticalFreq = result[i][2];
					}
				}else{
					if(criticalSpeed>result[i][1]){
						criticalSpeed = result[i][1];
					}
					if(criticalFreq>result[i][2]){
						criticalFreq = result[i][2];
					}
				}
				
			}
		}
		if(isResultSpeed){
			return criticalSpeed;
		}else{
			return criticalFreq;
		}
	}

	private JPanel createControlsPanel() {
			
		// Chart Inputs panel
		JPanel chartInputsPanel = new JPanel();
		chartInputsPanel.setBorder(new TitledBorder("Plot Inputs"));
		{
			ExecutionPlan plan = getCurrentPlan();
			ParamValueCombination comb = plan.getValueCombinationList().iterator().next();
			comb.getParams();
			JComboBox<String> studyParam = new JComboBox<String>(comb.getParams());
			JComboBox<String> resultsParam = new JComboBox<String>(new String[]{"Flutter Speed", "Flutter Frequency"});
			
			studyParam.addItemListener(new ItemListener(){
				@Override public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.SELECTED){
						refParam = info.getParamIn().getParameter((String)e.getItem());
						reloadDataSet();
					}
				}
			});
			
			resultsParam.addItemListener(new ItemListener() {
				@Override public void itemStateChanged(ItemEvent e) {
					if(e.getStateChange()==ItemEvent.SELECTED){
						isResultSpeed = e.getItem().equals("Flutter Speed");
						reloadDataSet();
					}
				}
			});
			
			
			studyParam.setSelectedIndex(0);
			resultsParam.setSelectedIndex(isResultSpeed?0:1);
			
			PainlessGridBag innerGB = new PainlessGridBag(chartInputsPanel, false);
			innerGB.row().cell(new JLabel("Study Parameter")).cell(studyParam);
			innerGB.row().cell(new JLabel("Results Parameter")).cell(resultsParam);
			innerGB.doneAndPushEverythingToTop();
			refParam = info.getParamIn().getParameter(comb.getParams()[0]);
		}
		
		// Display options panel
		JPanel displayOptionPanel = new JPanel();
		displayOptionPanel.setLayout(new BoxLayout(displayOptionPanel, BoxLayout.Y_AXIS));
			
		JCheckBox cbHeadings = new JCheckBox("Show Title", true);
		JCheckBox cbLegends = new JCheckBox("Show Legend", true);
		
		cbHeadings.setActionCommand("SHOW.HEADINGS");
		cbLegends.setActionCommand("SHOW.LEGENDS");
		
		cbHeadings.addItemListener(this);
		cbLegends.addItemListener(this);
		
		displayOptionPanel.add(cbHeadings);
		displayOptionPanel.add(cbLegends);
		displayOptionPanel.setBorder(new TitledBorder("Display Options"));
		
			
			
	//		JPanel curveListPanel = new JPanel();
	//		curveListPanel.setLayout(new BoxLayout(curveListPanel, BoxLayout.Y_AXIS));
	//		curveListPanel.setBorder(new TitledBorder("Show Curves"));
	//		
	//		for (int i = 0; i < curveCount; i++) {
	//			JCheckBox cb = new JCheckBox("Curve "+(i+1), true);
	//			cb.setActionCommand(i+"");
	//			cb.addItemListener(this);
	//			curveListPanel.add(cb);
	//		}
			
			
			JPanel panel = new JPanel();
			PainlessGridBag gbl = new PainlessGridBag(panel, false);		
			gbl.row().cell(chartInputsPanel).cell(displayOptionPanel);
			gbl.constraints(displayOptionPanel).anchor = GridBagConstraints.NORTHWEST;
			gbl.doneAndPushEverythingToTop();
			
			return panel;
	}
	
	
	private String generateKey(ParamValueCombination comb){
		List<String> values = new ArrayList<String>();
		String[] params = comb.getParams();
		for (int i = 0; i < params.length; i++) {
			String paramName = params[i];
			if(paramName.equals(refParam.getName()))
				continue;
			values.add( paramName + ":" + PAValueUtility.getPAValueString(comb.getValues()[i], " ") );
		}
		
		java.util.Collections.sort(values);
		
		return StringUtils.arrayToString(values.toArray(), "");
	}
	
	private Number getSimpleParamValue(ParamValueCombination comb){
		String[] params = comb.getParams();
		for (int i = 0; i < params.length; i++) {
			String paramName = params[i];
			if(paramName.equals(refParam.getName())){
				Value[][] val = comb.getValues()[i];
				if(val.length > 0){
					Value[] valueRow = val[0];
					if(valueRow.length > 0){
						return Float.parseFloat(valueRow[0].toString());
					}
				}
			}
		}
		return null;
	}
	
//	private String getTableParamValue()
	
	private ExecutionPlan getCurrentPlan(){
		if(currentPlan == null){
			String planName = info.getExecutionPlanName();
			List<ExecutionPlan> plans = info.getExecutionPlans();
			for (Iterator<ExecutionPlan> it = plans.iterator(); it.hasNext();) {
				ExecutionPlan executionPlan = it.next();
				if(executionPlan.getName().equals(planName)){
					currentPlan = executionPlan;
					break;
				}
			}
		}
		return currentPlan;
	}

	@Override
	public LegendTitle getLegend() {
		if(legend == null){
			legend = chart.getLegend();
		}
		
		return legend;
	}

	@Override
	public String getTitle() {
		return "Parameter Studies";
	}

}

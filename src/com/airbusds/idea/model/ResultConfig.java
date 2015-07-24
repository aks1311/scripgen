package com.airbusds.idea.model;

public class ResultConfig {

	public boolean showTitle = true;
	public boolean showSubTitle = true;
	public boolean showLegends = true;

	public String flutterPlotTitle = "Flutter Plots";
	public String flutterPlotSubtitle = "";
	public String flutterDampingAxisLabel = "Damping (-)";
	public String flutterFreqAxisLabel = "Frequency (Hz)";
	public String flutterVarAxisLabel = "";
	public int[] showFlutterCurves;
	
	public String evPlotTitle = "Eigen Value Plots";
	public String evPlotSubTitle = "";
	public String evImPartText = "Im(s)[Rad/s]";
	public String evRePartText = "Re(s)[rad/s]";
	public int[] showEvCurves;
	
	public String itPlotTitle = "Iterations Plot";
	public String itPlotSubTitle = "";
	public String itFlutterAxisText = "";
	public String iterationAxisText = "Iterations[-]";
	public int[] showItCurves;

	
	private static ResultConfig instance;
	
	public static ResultConfig createInstance(){
		synchronized (ResultConfig.class) {
			if(instance == null){
				instance = new ResultConfig();
			}
		}
		return instance;
	}
	
}

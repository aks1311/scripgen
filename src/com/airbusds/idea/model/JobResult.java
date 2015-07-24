package com.airbusds.idea.model;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;

import org.jfree.chart.title.TextTitle;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.VerticalAlignment;

import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.InputFileSerializer;
import com.airbusds.idea.utilities.StringUtils;
import com.airbusds.idea.utilities.TableDataHelper;

public abstract class JobResult extends XYPlot {
	
	// TODO 2 Re-factor JobResult. JobResult should be only model and create a separate base class for ResultUI.
	private IDEAJob job;
	protected ResultConfig config = ResultConfig.createInstance();
	private TextTitle[] subTitles;
	public JobResult(IDEAJob job){
		this.job = job;
	}
	
	public IDEAJob getJob(){
		return job;
	}
	
	public String getInfoString(){
		// TODO 2 Create the subtitle based on config
		
		String path = job.getJobHome() + File.separatorChar + "param.in";
		HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(path);
		StringBuffer sbSubTitle = new StringBuffer();
		
		String[][] paramValues = parameterValueMap.get("flutter_Mach");
		if(paramValues!=null){
			String machanalysis = paramValues[0][0];
			sbSubTitle.append("Mach Analysis:"+machanalysis);
			String mach = machanalysis.equals("fixed")?paramValues[0][1]:"";
			if(StringUtils.hasText(mach))
				sbSubTitle.append(", Mach Number:"+mach);
		}
		
		paramValues = parameterValueMap.get("flutter_altitude");
		if(paramValues!=null){
			String altitudes = paramValues[0][1];
			if(StringUtils.hasText(altitudes))
				sbSubTitle.append(", Altitude:"+altitudes);
		}
		
		
		paramValues = parameterValueMap.get("flutter_V_sound");
		if(paramValues!=null){
			String vsounds = paramValues[0][0];
			if(StringUtils.hasText(vsounds))
				sbSubTitle.append(", Speed of sound:"+vsounds);
		}
		
		paramValues = parameterValueMap.get("flutter_density");
		if(paramValues!=null){
			String density = paramValues[0][0];
			if(StringUtils.hasText(density))
				sbSubTitle.append(", Density:"+density);
		}
		
		paramValues = parameterValueMap.get("flutter_plot_variable");
		if(paramValues!=null){
			String fVar = paramValues[0][0];
			if(StringUtils.hasText(fVar))
				sbSubTitle.append(", Flutter Variable:"+fVar);
		}
		
		return sbSubTitle.toString();
		
		
	}

	@Override
	public String toString() {
		return job.toString();
	}

	protected void showSubtitle(){
		
//		if(subTitles != null && subTitles.length>0 && subTitles[0]!=null)
//			getChart().removeSubtitle(subTitles[0]);
		
		List<TextTitle> titles = new ArrayList<TextTitle>();
		
		
		TextTitle subTitle = new TextTitle(getSubTitle(),
			    new Font("Dialog", Font.ITALIC, 14), Color.black,
			    RectangleEdge.TOP, HorizontalAlignment.CENTER,
			    VerticalAlignment.BOTTOM, RectangleInsets.ZERO_INSETS);
		getChart().addSubtitle(subTitle);
		titles.add(subTitle);
		
		
		// for flutter points table
		subTitle = new TextTitle("Flutter Points(Mode/Speed/Frequency)",
			    new Font("Dialog", Font.PLAIN, 14), Color.black,
			    RectangleEdge.TOP, HorizontalAlignment.CENTER,
			    VerticalAlignment.BOTTOM, RectangleInsets.ZERO_INSETS);
		getChart().addSubtitle(subTitle);
		titles.add(subTitle);
		
		
		String path = job.getJobHome() + File.separatorChar + "param.in";
		HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(path);
		
		String[][] paramValues = parameterValueMap.get("flutter_points");
		if(paramValues!=null){
			String flutter_points_file_name = paramValues[0][0];
			flutter_points_file_name = flutter_points_file_name.substring(2, flutter_points_file_name.length()-2);
			String flutter_points_file_path = job.getJobHome() + File.separatorChar + flutter_points_file_name;
			Double[][] reData = TableDataHelper.readVgFile(flutter_points_file_path);
			
			for (int i = 0; i < reData.length; i++) {
				Double[] row = reData[i];
				subTitle = new TextTitle(StringUtils.arrayToString(row, "  "),
					    new Font("Dialog", Font.PLAIN, 14), Color.black,
					    RectangleEdge.TOP, HorizontalAlignment.CENTER,
					    VerticalAlignment.BOTTOM, RectangleInsets.ZERO_INSETS);
				getChart().addSubtitle(subTitle);
				titles.add(subTitle);
			}
		}
		
		subTitles = titles.toArray(new TextTitle[titles.size()]);
		
	}
	
	protected void hideSubTitle(){
		if(subTitles != null && subTitles.length>0){
			for (int i = 0; i < subTitles.length; i++) {
				TextTitle title = subTitles[i];
				getChart().removeSubtitle(title);
			}
		}
	}
	
	protected String getResultFilePath(String paramName){
		InputFile paramIn = IDEAContext.getInstance().getAnalysisInfo().getParamIn();
		Parameter param = paramIn.getParameter(paramName);
		return  getJob().getJobHome() + File.separatorChar + param.getValue().getValue().toString();
	}
	
	public abstract JComponent createTable();
	public abstract String getSubTitle();
	
}

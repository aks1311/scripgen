package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;

import com.airbusds.Utilities;
import com.airbusds.idea.gui.PAValueUtility.PAValueStatus;
import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class ParameterView extends ParagraphView {

	public static final int LARGE_TEXT = 50;
	public static final int SMALL_TEXT = 20;
	
	Parameter param;

	JLabel commentJL;
	JLabel paramNameJL;
	JLabel paramValueJL;
	
	public ParameterView(Paragraph paragraph){
		super(paragraph);
	}
	
	public void init() {
		this.param = getParagraph().getParameter();	

        PainlessGridbagConfiguration config = new PainlessGridbagConfiguration();
        config.setVirticalSpacing(0);
        config.setFirstRowTopSpacing(0);
        config.setLastRowBottomSpacing(0);
		PainlessGridBag gb = new PainlessGridBag(getBodyPanel(), config, false);
		
		commentJL = createCommentLabel();
		gb.row().cellXRemainder(commentJL);
		paramNameJL = createParamName();
		paramValueJL = createParamValue();
		gb.row().cell(paramNameJL)
				.cell(paramValueJL);
		
		JPanel pnlPadding = new JPanel();
        pnlPadding.setPreferredSize(new Dimension(1, 1));
        pnlPadding.setMinimumSize(new Dimension(1, 1));
        pnlPadding.setOpaque(false);
        gb.row().cellXRemainder(pnlPadding).fillXY();
		gb.done();
	}
	
	public void resetData(){
		
		if(StringUtils.hasText(param.getDescription())){
			commentJL.setText(StringUtils.getHTMLFormattedComment(param.getDescription(), null, false));
		}else{
			commentJL.setText("");
		}
		commentJL.updateUI();
		
		String paramName = Utilities.getParamDisplayName(param);
		if(param.getIsTabularData()){
			StringBuffer html = new StringBuffer();
			int rc = 1;
			
			if(param.isPAEnabled() && !param.isSingleRow() && param.getParameterAnalysis().getPaTableList()!=null && !param.getParameterAnalysis().getPaTableList().isEmpty()){
				rc =  param.getParameterAnalysis().getPaTableList().iterator().next().length;
			}else if(param.getTabularData()!=null && param.getTabularData().length>0){
				rc = param.getTabularData().length;
			}
			
			html.append("<html><body><table border=0 cellspacing=0 cellpadding=0><tr><td>"+paramName+"</td></tr>");
			while(--rc!=0){
				html.append("<tr><td><font color='orange'>&</font></td></tr>");
			}
			html.append("</table></body></html>");
			paramName = html.toString();
		}
		
		paramNameJL.setText(paramName);
		
		if(param.isPAEnabled()){
			PAValueStatus stat = new PAValueStatus();
			paramValueJL.setText(getPAValueText(stat));
			
			if(stat.isError){
				setMarkerMessage("Following values are generated in multiple ranges. ["+stat.message+"]", Color.RED);
			}else{
				setMarkerMessage("This Parameter is configured for Parameter Studies. The values displayed here are only for suggestion.", Color.BLUE);
			}

		}else{
			paramValueJL.setText(getParamValueText());
			clearMarker();
		}
		
		paramNameJL.updateUI();
		paramValueJL.updateUI();
	}
	
	
	// TODO 2 Re-factor to merge the PA and non PA method
	private String getPAValueText(PAValueStatus stat){
		StringBuffer sb = new StringBuffer();
		if(!param.getIsTabularData()){
			
			Value val = Value.valueFactory("");
			if(param.getDataType().equals("int") || param.getDataType().equals("float")){
				Value[] values = PAValueUtility.createValuesForRanges(param, stat);
				if(values.length>0)
					val = values[0];
			}else if(param.getDataType().equals("string") || param.getDataType().equals("list")){
				List<Value> values = param.getParameterAnalysis().getPaValueList();
				if(values.size()>0)
					val = values.get(0);
			}
			sb.append(val.getValue().toString());
			
		}else{
			Value[][] data = null;
			String[] types = param.getColumnTypes();
			
			if(param.isSingleRow()){
				data = param.getParameterAnalysis().getPaRowList();
				if(data!=null)
					data = new Value[][]{data[0]};
			}else if(!param.getParameterAnalysis().getPaTableList().isEmpty()){
				data = param.getParameterAnalysis().getPaTableList().iterator().next();
			}
			
			if(data!=null && data.length>0){
				sb.append("<html><table border=0 cellspacing=0 cellpadding=0>");
				for (int i = 0; i < data.length; i++) {
					Value[] rec = data[i];
					sb.append("<tr>");
					for (int j = 0; j < rec.length; j++) {
						Value value = rec[j];
						sb.append("<td style='padding-right:5px;'>");
						if(value!=null){
							if(types[j].equals("file"))
								sb.append( StringUtils.createFileName( value.getValue().toString() ));
							else
								sb.append(value.getValue().toString());
						}
						sb.append("</td>");
					}
					if(i<data.length-1){
						sb.append("<td><font color='orange'>&</font></td>");
					}else{
						sb.append("<td></td>");
					}
					sb.append("</tr>");
				}
				sb.append("</table></html>");
			}
		}
		return sb.toString();
	}

	private String getParamValueText(){
		StringBuffer sb = new StringBuffer();
		if(!param.getIsTabularData()){
			if(param.getValue()!=null){
				if(param.getDataType().equals("file"))
					sb.append(StringUtils.createFileName( param.getValue().getValue().toString() ));
				else
					sb.append(param.getValue().getValue().toString());
				
			}
		}else{
			String[] types = param.getColumnTypes();
			
			Value[][] data = param.getTabularData();
			if(data!=null && data.length>0){
				sb.append("<html><table border=0 cellspacing=0 cellpadding=0>");
				for (int i = 0; i < data.length; i++) {
					Value[] rec = data[i];
					sb.append("<tr>");
					for (int j = 0; j < rec.length; j++) {
						Value value = rec[j];
						sb.append("<td style='padding-right:5px;'>");
						if(value!=null){
							if(types[j].equals("file"))
								sb.append( StringUtils.createFileName( value.getValue().toString() ));
							else
								sb.append(value.getValue().toString());
						}
						sb.append("</td>");
					}
					if(i<data.length-1){
						sb.append("<td><font color='orange'>&</font></td>");
					}else{
						sb.append("<td></td>");
					}
					sb.append("</tr>");
				}
				sb.append("</table></html>");
			}
		}
		
		return sb.toString();
	}
	
	private JLabel createParamName(){
		JLabel label = new JLabel();
		label.setForeground(new Color(2640855));
		return label;
	}
	
	private JLabel createParamValue(){
		JLabel text = new JLabel();
		text.setAlignmentY(TOP_ALIGNMENT);
		return text;
	}

	private JLabel createCommentLabel(){
		JLabel label = new JLabel("<NO COMMENTS>");
		label.setForeground(new Color(706600));
		label.setFont(new Font(label.getFont().getName(), Font.ITALIC, label.getFont().getSize()));
		return label;
	}

}

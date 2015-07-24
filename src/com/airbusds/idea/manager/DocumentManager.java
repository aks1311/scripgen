package com.airbusds.idea.manager;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.Document;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

public class DocumentManager {
	
	/**
	 * @param inFile
	 * @return
	 */
	public Document createDocument(InputFile inFile){
		
		Document doc = inFile.getDocument();
		doc.setTitle("Input file ("+inFile.getName()+")");
		List<Parameter> paramList = inFile.getParameters();
		
		Collections.sort(paramList, new Comparator<Parameter>() {
			public int compare(Parameter p1, Parameter p2) {
				return p1.getOrder()-p2.getOrder();
			};
		});
		
		for (Iterator<Parameter> it = paramList.iterator(); it.hasNext();) {
			Parameter parameter = it.next();
			Paragraph para = new Paragraph();
			
			para.setSequence(parameter.getOrder());
			para.setIsParameter(true);
			para.setParameter(parameter);
			para.setUseParamDescAsComment(true);
			
			doc.addParagraph(para);
		}
		
		return inFile.getDocument();
	}
	
	/**
	 * @param info
	 */
	public void fixParameterReferences(AnalysisInfo info){

		InputFile inputFile = info.getParamIn();
		for (Iterator<Paragraph> it = inputFile.getDocument().getParagraphs().iterator(); it.hasNext();) {
			Paragraph para = it.next();
			if(!para.getIsParameter())
				continue;
			Parameter param = inputFile.getParameter(para.getParameter().getName());
			para.setParameter(param);
		}
		
		inputFile = info.getModelIn();
		for (Iterator<Paragraph> it = inputFile.getDocument().getParagraphs().iterator(); it.hasNext();) {
			Paragraph para = it.next();
			if(!para.getIsParameter())
				continue;
			Parameter param = inputFile.getParameter(para.getParameter().getName());
			para.setParameter(param);
		}
		
	}
	
	/**
	 * 
	 * 
	 * @param inFile
	 * @param parameterValueMap
	 */
	public void updateValuesInInputFiles(InputFile inFile, HashMap<String, String[][]> parameterValueMap){
		
		List<Parameter> params = inFile.getParameters();
		for (Iterator<Parameter> it = params.iterator(); it.hasNext();) {
			Parameter parameter = it.next();
			if(parameterValueMap.containsKey(parameter.getName())){
				
				if(parameter.getIsTabularData()){ // if the data type is tabular 
					String[] colTypes = parameter.getColumnTypes();
					String[][] values = parameterValueMap.get(parameter.getName());
					Value[][] objValues = new Value[values.length][];
					for (int i = 0; i < values.length; i++) {
						String[] row = values[i];
						objValues[i] = new Value[row.length];
						for (int j = 0; j < row.length; j++) {
							objValues[i][j] = createValue(row[j], colTypes[j]);
						}
					}
					parameter.setTabularData(objValues);
				}else{ // for non tabular data
					parameter.setValue( createValue(parameterValueMap.get(parameter.getName())[0][0], parameter.getDataType()) );
				}
			}
		}
	}
	
	private Value createValue(String data, String dataType){
		Object objData = data;
		
		switch (dataType) {
		case "file":{
				if(StringUtils.hasText(data) && data.length()>4){
					objData = data.substring(2, data.length()-2);
				}
			}
			break;
		case "int":{
				objData = Integer.parseInt(data);
			}
			break;
		case "float":{
				objData = Float.parseFloat(data);
			}
			break;

		default:
			break;
		}		
		return Value.valueFactory(objData);
	}
	
	
}

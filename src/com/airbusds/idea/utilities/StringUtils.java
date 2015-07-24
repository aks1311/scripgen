package com.airbusds.idea.utilities;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;

public class StringUtils {
	
	public static final int COMMENT_MAX_LINE_LENGTH = 120;
	public static final String COMMENT_HASH_TEXT = "# ";
	

	public static boolean hasText(String text){
		if(text == null || text.trim().length()==0)
			return false;
		else
			return true;
	}
	
	public static String collectionToString(Collection<String> coll, String delim){
		StringBuffer sb = new StringBuffer();
		Iterator<String> it = coll.iterator();
		while(it.hasNext()){
			sb.append(it.next());
			if(it.hasNext())
				sb.append(delim);
		}
		
		return sb.toString();
	}
	
	public static String arrayToString(Object[] arr, String delim){
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0; i < arr.length; i++) {
			if(i != 0)
				sb.append(delim);
			sb.append(arr[i]!=null?arr[i]:"");
		}
		
		return sb.toString();
	}
	
	public static List<String> arrayToList(String[][] arr){
		List<String> list = new LinkedList<String>();
		for (int i = 0; i < arr.length; i++) {
			String[] row = arr[i];
			for (int j = 0; j < row.length; j++) {
				list.add(row[j]);
			}
		}
		return list;
	}
	
	public static String getHTMLFormattedComment(String comment, String hashString, boolean endWithHash) {
		StringBuffer sb = new StringBuffer();
		String lineBreakers = ".,; ";
		sb.append("<html>");
		
		if(!hasText(comment))
			return "";
		
		if(!hasText(hashString))
			hashString = COMMENT_HASH_TEXT;
		
		String[] lines = comment.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			
			if( !hasText(line))
				continue;
			
			if(i>0)
				sb.append("<br/>");
			
			while(line.length()>COMMENT_MAX_LINE_LENGTH){
				int pointer = COMMENT_MAX_LINE_LENGTH;
				while(--pointer>0){
					if( lineBreakers.contains(line.charAt(pointer)+"") ){
						sb.append(hashString + line.substring(0, pointer)+"<br/>");
						line = line.substring(pointer);
						break;
					}
				}
			}
			if(endWithHash)
				sb.append(hashString +" "+ line + " " + hashString);
			else
				sb.append(hashString +" "+ line);
		}
		
		return sb.toString();
	}

	public static List<String> getFormattedComment(String comment, String hashString, boolean endWithHash) {
		List<String> sb = new ArrayList<String>();
		String lineBreakers = ".,; ";
		
		if(!hasText(comment))
			return sb;
		
		if(!hasText(hashString))
			hashString = COMMENT_HASH_TEXT;
		
		String[] lines = comment.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i].trim();
			
			if( !hasText(line))
				continue;
			
			while(line.length()>COMMENT_MAX_LINE_LENGTH){
				int pointer = COMMENT_MAX_LINE_LENGTH;
				while(--pointer>0){
					if( lineBreakers.contains(line.charAt(pointer)+"") ){
						sb.add(hashString +" "+ line.substring(0, pointer));
						line = line.substring(pointer);
						break;
					}
				}
			}
			if(endWithHash)
				sb.add(hashString + " " + line + " " + hashString);
			else
				sb.add(hashString + " " + line);
		}
		
		return sb;
	}
	
	public static List<String> getFormattedParameterText(Parameter param, Value[][] vals){
		List<String> sb = new ArrayList<String>();
		
		if(param.getIsTabularData()){
			Value[][] data = null;
			
			if(vals!=null){
				data = vals;
			}else{
				data = fixParamValueArray(param, param.getTabularData());
			}
				
			if(data!=null && data.length>0){
				for (int i = 0; i < data.length; i++) {
					String line = "";
					
					if(i==0)
						line += param.getName()+"  ";
					else
						line += "&       ";
					
					line += arrayToString(data[i], " ");
					if(i<data.length-1){
						line += " &";
					}
					sb.add(line);
				}
			}
				
			
		}else{
			if(vals!=null){
				sb.add(param.getName()+" "+ arrayToString(vals[0], " "));
			}else{
				if(param.getDataType().equals("file"))
					sb.add(param.getName()+"  "+createFileName(param.getValue().toString()));
				else
					sb.add(param.getName()+"  "+param.getValue());
			}
		}
		
		return sb;
	}
	
	
	/**
	 * Fix the appearance of null in the generated files. Remove the null objects.
	 * 
	 * @param param
	 * @param vals
	 * @return
	 */
	public static Value[][] fixParamValueArray(Parameter param, Value[][] vals){
		Value[][] result = null;
		if(vals!=null){
			result = new Value[vals.length][];
			String[] colTypes = param.getColumnTypes();
			for (int i = 0; i < vals.length; i++) {
				Value[] row = vals[i];
				result[i] = new Value[row.length];
				for (int j = 0; j < row.length; j++) {
					Value value = row[j];
					if(value == null) continue;
					if(colTypes[j].equals("file"))
						result[i][j] = Value.valueFactory(createFileName(value.toString()));
					else
						result[i][j] = Value.valueFactory(value.toString());
				}
			}
		}
		return result;
	}
	
	public static String createFileName(String rawFileName){
		if(hasText(rawFileName))
			return "'\""+rawFileName+"\"'";
		else
			return "";
	}
	
	public static String getDisplayTime(long millis){
		DecimalFormat df = new DecimalFormat("#.##");
		
		if(millis < 1000){
			return millis +" ms";
		}else if(millis >= 1000 && millis < 60000){
			return df.format(millis/1000d) +" secs";
		}else{
			return df.format((millis/1000d)/60) + " mins";
		}
	}
	
	public static String removeQuotesFromFileName(String fileName){
		return fileName.substring(2, fileName.length()-2);
	}
	
}

package com.airbusds.idea;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.utilities.StringUtils;

public class InputFileSerializer {
	
	private static Logger log = LogManager.getLogger(InputFileSerializer.class.getName());
	
	public static HashMap<String, String[][]> readFile(String path){
		BufferedReader br = null;
		HashMap<String, String[][]> map = new HashMap<String, String[][]>();
		String currentParam = "";
		List<String[]> paramvalues = new ArrayList<String[]>();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path));
			boolean continuationFound = false;
 
			while ((sCurrentLine = br.readLine()) != null) {
				if(StringUtils.hasText(sCurrentLine)){
					sCurrentLine = sCurrentLine.trim();
					
					boolean parameterDone = false;
					
					// ignore if comment found
					if(sCurrentLine.charAt(0)=='#'){
						continue; // TODO 3 Comments have to be loaded and edited
					}
					
					StringTokenizer st = new StringTokenizer(sCurrentLine);
					
					// if continuation use the previous parameter and append values
					if(sCurrentLine.charAt(0)=='&' && continuationFound){
						parameterDone = true;
						st.nextElement();
					}
					
					List<String> strRow = new ArrayList<String>();
					while (st.hasMoreElements()) {
						String text = (String)st.nextElement();
						if(!parameterDone){
							if(StringUtils.hasText(currentParam)){
								map.put(currentParam, paramvalues.toArray(new String[paramvalues.size()][]));
								paramvalues = new ArrayList<String[]>();
							}
							currentParam = text;
							parameterDone = true;
						}else{
							if(text.equals("&")){
								if(!strRow.isEmpty())
									paramvalues.add(strRow.toArray(new String[strRow.size()]));
								strRow = new ArrayList<String>();
								continuationFound = true;
							}else{
								strRow.add(text);
							}
						}
					}
					if(!strRow.isEmpty())
						paramvalues.add(strRow.toArray(new String[strRow.size()]));
					
				}
			}
			map.put(currentParam, paramvalues.toArray(new String[paramvalues.size()][]));
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return map;
	}
	
//	public static void main(String[] args){
//		Map<String, String[][]> map = readFile("E:/DeleteLater/First_analysis_OOOOPPPSS_1/test.in");
//		System.err.println(map.get("amit").length);
//		System.err.println(map.get("kumar").length);
//		System.err.println(map.get("singh").length);
//	}
	
	public void saveFile(HashMap<String, List<String>> map, String path, Map<String, String> commentMap){
		Iterator<String> it = map.keySet().iterator();
		
		File file = new File(path);
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try{
			// if file doesnt exists, then create it
			if (!file.exists()) {
					file.createNewFile();
			}
			
			fw = new FileWriter(file.getAbsoluteFile());
			bw = new BufferedWriter(fw);
			
			while(it.hasNext()){
				String parameter = it.next();
				List<String> values = map.get(parameter);
				String comments = commentMap.get(parameter);
				if(StringUtils.hasText(comments)){
					bw.newLine();
					bw.write("#"+comments);
					bw.newLine();
				}
				String valueTxt = StringUtils.collectionToString(values, " ");
				bw.write(parameter+" "+valueTxt);
				bw.newLine();
			}
		}catch(IOException ie){
			log.error("Error creating/saving file");
			ie.printStackTrace();
		}finally{
			
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		log.debug("File saved!");
		
	}
	
}

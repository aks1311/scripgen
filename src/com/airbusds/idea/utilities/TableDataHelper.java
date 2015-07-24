package com.airbusds.idea.utilities;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class TableDataHelper {
	
	public static Double[][] readVfFile(String path){
		
		int rowSize = 0;
		List<Double[]> result = new ArrayList<Double[]>();
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(path));
			String currentLine;
			while ((currentLine = br.readLine()) != null) {
				if(StringUtils.hasText(currentLine))
					currentLine = currentLine.trim();
				
				StringTokenizer st = new StringTokenizer(currentLine);
				
				if(rowSize == 0){
					List<Double> strRow = new ArrayList<Double>();
					while (st.hasMoreElements()) {
						String sValue = (String)st.nextElement();
						Double value = Double.parseDouble(sValue);
//						if(!Double.isFinite(value))
//							value = null;
						strRow.add(value);
					}
					result.add(strRow.toArray(new Double[strRow.size()]));
				}else{
					Double[] row = new Double[rowSize];
					int cntr = 0;
					while (st.hasMoreElements()) {
						String sValue = (String)st.nextElement();
						Double value = Double.parseDouble(sValue);
						if(Double.isFinite(value))
							value = null;
						row[cntr++] = value;
					}
					result.add(row);
				}
				
			}
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				
		}
		
		
		return result.toArray(new Double[result.size()][]);
	}
	
	public static Double[][] readVgFile(String path){
		
		return readVfFile(path);
	}
	
	
	
}

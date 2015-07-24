package com.airbusds.idea;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.airbusds.idea.gui.PAValueUtility;
import com.airbusds.idea.gui.PAValueUtility.PAValueStatus;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.ParameterAnalysis;
import com.airbusds.idea.model.Value;

public class PAValueMapper {
	
	private Map<Parameter, Value[][]> pointer;
	private List<ParamValueCombination> result;
	private Map<Parameter, Value[][][]> paramValueMap;
	private ListIterator<Parameter> paramIterator;

	public PAValueMapper(List<Parameter> params){
		Collections.sort(params, new Comparator<Parameter>() {

			@Override
			public int compare(Parameter p1, Parameter p2) {
				return p1.getName().compareTo(p2.getName());
			}
			
		});
		
		paramIterator = params.listIterator();
		pointer = new HashMap<Parameter, Value[][]>();
		
		// create param value map
		paramValueMap = createParamValueMap(params);
	}

	public List<ParamValueCombination> createValueCombinations(){
		Parameter nextParam = getNextParameter();
		if(nextParam!=null){
			result = new ArrayList<ParamValueCombination>();
			createParameterValueCombinations(nextParam);
		}
		return result;
	}
	
	private void createParameterValueCombinations(Parameter param) {
		
		Value[][][] values = paramValueMap.get(param);
		Parameter paramNext = getNextParameter();
		boolean isLeaf = false;
		for (int i = 0; i < values.length; i++) {
			Value[][] paramValue = values[i];
			pointer.put(param, paramValue);
			if(paramNext==null){
				// break condition
				ParamValueCombination comb = createCombinationFromPointerMap();
				result.add(comb);
				isLeaf = true;
			}else{
				createParameterValueCombinations(paramNext);
			}
		}
		if(!isLeaf)
			getPrevParameter();
	
	}
	
	private ParamValueCombination createCombinationFromPointerMap() {
		
		String[] params = new String[pointer.keySet().size()];
		Value[][][] values = new Value[params.length][][];
		
		int counter = 0;
		List<Parameter> list = new ArrayList<Parameter>(pointer.keySet());
		Collections.sort(list, new Comparator<Parameter>() {

			@Override
			public int compare(Parameter p1, Parameter p2) {
				return p1.getName().compareTo(p2.getName());
			}
			
		});
		
		for (Iterator<Parameter> it = list.iterator(); it
				.hasNext();) {
			Parameter param = it.next();
			params[counter] = param.getName();
			values[counter] = pointer.get(param);
			counter++;
		}
		ParamValueCombination comb = new ParamValueCombination();
		comb.setParams(params);
		comb.setValues(values);

		return comb;
	}

	private HashMap<Parameter, Value[][][]> createParamValueMap(List<Parameter> params){
		HashMap<Parameter, Value[][][]> map = new HashMap<Parameter, Value[][][]>();
		
		for (Iterator<Parameter> it = params.iterator(); it.hasNext();) {
			Parameter param = it.next();
			ParameterAnalysis pa = param.getParameterAnalysis();
			
			String dataType = param.getDataType();
			
			if( dataType.equals("int") || dataType.equals("float")){
				// read ranges of numbers and create number lists
				
				PAValueStatus status = new PAValueStatus();
				Value[] list = PAValueUtility.createValuesForRanges(param, status);
				Value[][][] values = new Value[list.length][][];
				for (int i = 0; i < values.length; i++) {
					values[i] = new Value[][]{  new Value[]{list[i]} };
				}
				map.put(param, values);
				
			}else if(dataType.equals("string") || dataType.equals("list")){
				// read lists of texts
				
				List<Value> list = pa.getPaValueList();
				Iterator<Value> itr = list.iterator();
				Value[][][] values = new Value[list.size()][][];
				for (int i = 0; i < values.length; i++) {
					values[i] = new Value[][]{ new Value[]{itr.next()} };
				}
				map.put(param, values);
				
				
			}else if(param.getIsTabularData() && param.isSingleRow()){
				if(pa.getPaRowList()!=null && pa.getPaRowList().length>0){
					
					for (int i = 0; i < pa.getPaRowList().length; i++) {
						map.put(param, new Value[][][]{pa.getPaRowList()});
					}
				
				}
			}else if(param.getIsTabularData() && !param.isSingleRow()){
				if(param.getTabularData()!=null && param.getTabularData().length>0)
					map.put(param, pa.getPaTableList().toArray(new Value[pa.getPaTableList().size()][][]));
				
			}
			
			
		}
		
		return map;
	}
	
	private Parameter getPrevParameter(){
		return paramIterator.hasPrevious()?paramIterator.previous():null;
	}
	
	private Parameter getNextParameter(){
		return paramIterator.hasNext()?paramIterator.next():null;
	}
	
}

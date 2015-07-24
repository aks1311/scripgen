package com.airbusds.idea.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.JSONSerializer;
import com.airbusds.idea.ObjectSerializer;
import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.Params;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.ExecutionPlan;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.model.ParamValueCombination;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.service.FileService;
import com.airbusds.idea.utilities.StringUtils;

public class AnalysisManager {
	
	private static Logger log = LogManager.getLogger(AnalysisManager.class.getName());
	
	FileService fileService;
	ObjectSerializer serializer;
	
	
	public void saveAnalysis(AnalysisInfo info){
		
		String filePath = info.getAnalysisLocation() 
				+ File.separatorChar
				+ info.getName() + ".iac";
		
		JSONSerializer serializer = new JSONSerializer();
		serializer.serializeToFile(filePath, info);
		
		log.info("Analysis saved as " + filePath);
	}
	
	public AnalysisInfo readAnalysis(File file){
        JSONSerializer serializer = new JSONSerializer();
        
        // TODO 2 Read matching versions only
        AnalysisInfo info = serializer.getAsObject(file, AnalysisInfo.class);
        
        // fix the parameter config based on latest config
		InputFileConfig config = InputFileConfig.getParamInConfig();
		List<Params> list = config.paramList;
        for (Iterator<Params> it = list.iterator(); it.hasNext();) {
			Params params = it.next();
			Parameter param = info.getParamIn().getParameter(params.name);
			param.setPAAllowed(params.isPAAllowed);
			param.setReadOnly(param.isReadOnly());
			param.setSingleRow(params.isSingleRow);
		}
        
		return info;
	}
	
	
	public void createInputFiles(AnalysisInfo info, ParamValueCombination comb){

		String parentDir = info.getAnalysisLocation()+File.separatorChar+comb.getLabel();
		
		// create folder
		fileService.createFolder(parentDir);
		
		try {
			
			// create param.in file stream
			createInputFile(info.getParamIn(), parentDir+File.separatorChar+"param.in", comb);
			
			// create model.in
			createInputFile(info.getModelIn(), parentDir+File.separatorChar+"model.in", null);

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Error creating input files.");
		}
		
	}
	
	private void createInputFile(InputFile inFile, String path, ParamValueCombination comb) throws IOException{
		
		PrintWriter pw = new PrintWriter(new FileOutputStream(path));

		Map<String, Value[][]> combMap = new HashMap<String, Value[][]>();
		if(comb != null){
			for (int i = 0; i < comb.getParams().length; i++) {
				String paramName = comb.getParams()[i];
				combMap.put(paramName, comb.getValues()[i]);
			}
		}

		// read paragraphs
		List<Paragraph> paras = inFile.getDocument().getParagraphs();
		for (Iterator<Paragraph> it = paras.iterator(); it.hasNext();) {
			Paragraph paragraph = it.next();
			
			// output comments and blank lines
			if(paragraph.getIsBlankLine()){
				pw.println();
			}else if(paragraph.getIsComment()){
				
				List<String> lines = StringUtils.getFormattedComment(paragraph.getComments(), "###", true);
				for (Iterator<String> lineIt = lines.iterator(); lineIt.hasNext();) {
					String line = lineIt.next();
					pw.println(line);
				}
				
			}else if(paragraph.getIsParameter()){
				
				Parameter param = paragraph.getParameter();
				List<String> paramLines = StringUtils.getFormattedParameterText(param, combMap.get(param.getName()));
				
				if(!paramLines.isEmpty() && StringUtils.hasText(param.getDescription())){
					List<String> lines = StringUtils.getFormattedComment(param.getDescription(), "#", false);
					for (Iterator<String> lineIt = lines.iterator(); lineIt.hasNext();) {
						String line = lineIt.next();
						pw.println(line);
					}
				}
				
				for (Iterator<String> lineIt = paramLines.iterator(); lineIt.hasNext();) {
					String line = lineIt.next();
					pw.println(line);
				}
				
			}
			
		}
		
		pw.flush();
		pw.close();
		
		
	}
	
	public void deleteAnalysisExecution(AnalysisInfo info){
		log.info("Deleting input files and results for Analysis: "+info.getName());
		List<ExecutionPlan> plans = info.getExecutionPlans();
		
		for (Iterator<ExecutionPlan> itr = plans.iterator(); itr.hasNext();) {
			ExecutionPlan plan = itr.next();
			Set<ParamValueCombination> combs = plan.getValueCombinationList();
			for (Iterator<ParamValueCombination> it = combs.iterator(); it.hasNext();) {
				ParamValueCombination comb = it.next();
				String parentDir = info.getAnalysisLocation()+File.separatorChar+comb.getLabel();
				FileUtils.deleteQuietly(new File(parentDir));
			}
		}
		
	}
	
	
	///  Private and Getter/Setter methods below  ///
	
	public FileService getFileService() {
		return fileService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	public ObjectSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(ObjectSerializer serializer) {
		this.serializer = serializer;
	}
	
}

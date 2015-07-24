package com.airbusds.idea.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.commons.io.FileUtils;

public class FileService {
	
	public File readFile(String path){
		return null;
	}
	
	public File createFolder(String path){
		
		File newFolder = new File(path);
		try {
			FileUtils.forceMkdir(newFolder);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return newFolder;
	}

	public File copyFile(String srcPath, String destPath) {
		File destFile = new File(destPath); 
		try {
//			FileUtils.copyFile(new File(srcPath), destFile);
			Files.copy(new File(srcPath).toPath(), destFile.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return destFile;
	}
	
	
}

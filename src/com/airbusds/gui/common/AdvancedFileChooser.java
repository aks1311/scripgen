package com.airbusds.gui.common;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Properties;

import javax.swing.JFileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A file chooser which remembers the last directory selected by the user.
 * The details about the last opened directory are saved in the user home in a file <code>.idea-gui</code>
 * 
 * @author amit.singh
 *
 */
public class AdvancedFileChooser extends JFileChooser {
	
	private static final long serialVersionUID = -2620332781701993784L;
	private static Logger log = LogManager.getLogger(AdvancedFileChooser.class.getName());
	
	public AdvancedFileChooser(){
		super();
		String userHome = System.getProperty("user.home");
		Path config_file_path = FileSystems.getDefault().getPath(userHome, ".idea-gui");
		File config_file = config_file_path.toFile();
		Properties prop = new Properties();
		
		if(!Files.exists(config_file_path, LinkOption.NOFOLLOW_LINKS)){
			log.debug("IDEA-GUI config file not found");
			try {
				config_file = Files.createFile(config_file_path).toFile();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		InputStream is;
		try {
			is = new FileInputStream(config_file);
			prop.load(is);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		
		String lastLoc = prop.getProperty("last.location");
		if(lastLoc!=null && lastLoc.length()>0){
			this.setCurrentDirectory(new File(lastLoc));
		}

	}
	
	@Override
	public int showOpenDialog(Component cmp) throws HeadlessException {
		int returnVal = super.showOpenDialog(cmp);
		
		if(returnVal == JFileChooser.APPROVE_OPTION){
			String userHome = System.getProperty("user.home");
			Path config_file_path = FileSystems.getDefault().getPath(userHome, ".idea-gui");
			File config_file = config_file_path.toFile();
			Properties prop = new Properties();
			OutputStream output = null;
			
			try {
				InputStream is = new FileInputStream(config_file);
				prop.load(is);
				prop.setProperty("last.location", getSelectedFile().getPath());
				
				output = new FileOutputStream(config_file);
				prop.store(output, null);
				
			} catch (IOException e) {
				log.error(e.getMessage());
			}finally{
				if (output != null) {
					try {
						output.close();
					} catch (IOException e) {
						log.error(e.getMessage());
					}
				}
			}
		}
		
		return returnVal;
	}
	
}

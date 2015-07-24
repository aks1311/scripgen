package com.airbusds.idea.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.painlessgridbag.PainlessGridBag;

import com.airbusds.gui.common.AdvancedFileChooser;
import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.InputFileSerializer;
import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.Params;
import com.airbusds.idea.manager.ConfigManager;
import com.airbusds.idea.manager.DocumentManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class ImportFilesPanel extends BaseWizardPanel {
	
	private static Logger log = LogManager.getLogger(ImportFilesPanel.class.getName());
	
	private AnalysisInfo info;
	
    private JTextField paramFileTemplateLocationTxt;
    private JButton paramFileTemplateBrowseBtn;
    
    private JTextField modelFileTemplateLocationTxt;
    private JButton modelFileTemplateBrowseBtn;

	public ImportFilesPanel(WizardComponents wizardComponents, AnalysisInfo info) {
		super(wizardComponents, "Import Input Files");
		init();
		this.info = info;
	}

	private void init() {
		paramFileTemplateLocationTxt = new JTextField();
		paramFileTemplateBrowseBtn = new JButton("Browse..");
		modelFileTemplateLocationTxt = new JTextField();
		modelFileTemplateBrowseBtn = new JButton("Browse..");

		PainlessGridBag gbl = new PainlessGridBag(this, false);
		gbl.row().cell(new JLabel("Param File Template"))
				.cell(paramFileTemplateLocationTxt).fillX()
				.cell(paramFileTemplateBrowseBtn);
		gbl.row().cell(new JLabel("Model File Template"))
				.cell(modelFileTemplateLocationTxt).fillX()
				.cell(modelFileTemplateBrowseBtn);
		gbl.doneAndPushEverythingToTop();
		
		paramFileTemplateBrowseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AdvancedFileChooser fc = new AdvancedFileChooser();
				fc.setFileSelectionMode(AdvancedFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(ImportFilesPanel.this
						.getParent());

				if (returnVal == AdvancedFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					paramFileTemplateLocationTxt.setText(file.getAbsolutePath());
				}
			}
		});

		modelFileTemplateBrowseBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AdvancedFileChooser fc = new AdvancedFileChooser();
				fc.setFileSelectionMode(AdvancedFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(ImportFilesPanel.this
						.getParent());

				if (returnVal == AdvancedFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					modelFileTemplateLocationTxt.setText(file.getAbsolutePath());
				}
			}
		});


	}
	
	@Override
	public void readForm() {
		log.debug("Inside readForm >>" + this.getPanelTitle());

		paramFileTemplateLocationTxt.setText(paramFileTemplateLocationTxt
				.getText().trim());
		modelFileTemplateLocationTxt.setText(modelFileTemplateLocationTxt
				.getText().trim());

		DocumentManager docMgr = new DocumentManager();
		ConfigManager configMgr = new ConfigManager();
		InputFileConfig paramConfig = InputFileConfig.getParamInConfig();
		InputFileConfig modelConfig = InputFileConfig.getModelInConfig();
		
		if (StringUtils.hasText(paramFileTemplateLocationTxt.getText())) {
			File file = new File(paramFileTemplateLocationTxt.getText());
			HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(file.getAbsolutePath());
			
			InputFile inFile = info.getParamIn();
			if(inFile == null){
				inFile = new InputFile();
				inFile.setName("param.in");
				info.setParamIn(inFile);
				
				for (Params param : paramConfig.paramList) {
					inFile.addParameter(configMgr.createParameter(param));
				}
				
			}
			
			docMgr.updateValuesInInputFiles(info.getParamIn(), parameterValueMap);;
		}

		if (StringUtils.hasText(modelFileTemplateLocationTxt.getText())) {
			File file = new File(modelFileTemplateLocationTxt.getText());
			HashMap<String, String[][]> parameterValueMap = InputFileSerializer.readFile(file.getAbsolutePath());
			
			InputFile inFile = info.getModelIn();
			if(inFile == null){
				inFile = new InputFile();
				inFile.setName("model.in");
				info.setModelIn(inFile);
				
				for (Params param : modelConfig.paramList) {
					inFile.addParameter(configMgr.createParameter(param));
				}
			}

			docMgr.updateValuesInInputFiles(info.getModelIn(), parameterValueMap);;
		}

	}
	
	@Override
	public void next() {
		readForm();
		super.next();
	}
	

}

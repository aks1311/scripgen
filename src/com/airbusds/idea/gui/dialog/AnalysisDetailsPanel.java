package com.airbusds.idea.gui.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.painlessgridbag.PainlessGridBag;

import com.airbusds.gui.common.AdvancedFileChooser;
import com.airbusds.gui.wizard.WizardComponents;
import com.airbusds.idea.JSONSerializer;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class AnalysisDetailsPanel extends BaseWizardPanel {
	
	private static Logger log = LogManager.getLogger(AnalysisDetailsPanel.class.getName());

	JTextField analysisNameText;
	JComboBox<String> analysisType;
	JTextField analysisLocationFile;
	JButton analysisLocationChooserBtn;
	JTextField analysisTemplateLocationFile;
	JButton analysisTemplateChooserBtn;
	AnalysisInfo info;
			
	public AnalysisDetailsPanel(WizardComponents wizardComponents, AnalysisInfo info) {
        super(wizardComponents, "Enter Analysis details");
        init();
        this.info = info;
	}

	private void init() {
		//TODO 3 Title section in the wizard panels
		
		analysisNameText = new JTextField();
		String[] analysisTypes = { "Flutter", "Modal" };
		analysisType = new JComboBox<String>(analysisTypes);
		analysisLocationFile = new JTextField();
		analysisLocationFile.setEditable(false);
		analysisLocationChooserBtn = new JButton("Browse..");
		analysisTemplateLocationFile = new JTextField();
		analysisTemplateLocationFile.setEditable(false);
		analysisTemplateChooserBtn = new JButton("Browse..");

		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
//		JLabel titleLabel = new JLabel("Create New Analysis");
//		Font font = titleLabel.getFont();
//		titleLabel.setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
//
//		JLabel messageLabel = new JLabel("Enter Analysis Name and Location");
//		font = messageLabel.getFont();
//		messageLabel.setFont(new Font(font.getName(), Font.ITALIC, font.getSize()));
//		
//		gbl.row().cell(titleLabel);
//		gbl.row().cell(messageLabel);
//		gbl.row().separator();
		
		gbl.row().cell(new JLabel("Analysis Name"))
					.cell(analysisNameText)
					.fillX();
		gbl.row().cell(new JLabel("Analysis Type"))
					.cell(analysisType)
					.fillX();
		gbl.row().cell(new JLabel("Analysis Location"))
					.cell(analysisLocationFile).fillX()
					.cell(analysisLocationChooserBtn);
		gbl.row().cell(new JLabel("Analysis Template"))
					.cell(analysisTemplateLocationFile).fillX()
					.cell(analysisTemplateChooserBtn);
		gbl.doneAndPushEverythingToTop();

		analysisLocationChooserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AdvancedFileChooser fc = new AdvancedFileChooser();
				fc.setFileSelectionMode(AdvancedFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(AnalysisDetailsPanel.this
						.getParent());

				if (returnVal == AdvancedFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					analysisLocationFile.setText(file.getAbsolutePath());
				}
			}
		});

		analysisTemplateChooserBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AdvancedFileChooser fc = new AdvancedFileChooser();
				fc.setFileSelectionMode(AdvancedFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(AnalysisDetailsPanel.this
						.getParent());

				if (returnVal == AdvancedFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					analysisTemplateLocationFile.setText(file.getAbsolutePath());
				}
			}
		});

//		validateForm();
	}
	
	@Override
	public void readForm() {
		log.debug("Inside readForm >>"+this.getPanelTitle());
		
		analysisNameText.setText(analysisNameText.getText().trim());
		analysisLocationFile.setText(analysisLocationFile.getText().trim());
		analysisTemplateLocationFile.setText(analysisTemplateLocationFile.getText().trim());
		
		info.setName(analysisNameText.getText());
		info.setAnalysisLocation(analysisLocationFile.getText());
		info.setType(analysisType.getSelectedItem().toString());
		
		// Import analysis if required
		if(StringUtils.hasText(analysisTemplateLocationFile.getText())){
			File templateIac = new File(analysisTemplateLocationFile.getText());
			log.debug("Template found. Trying import.."+templateIac.getAbsoluteFile());
			JSONSerializer serializer = new JSONSerializer();
			AnalysisInfo template = serializer.getAsObject(templateIac.getAbsoluteFile(), AnalysisInfo.class);
			log.debug("Analysis Template File is "+template);
			
			if(template.getType().equals(info.getType())){
				template.setName(info.getName());
				template.setAnalysisLocation(info.getAnalysisLocation());
				
				try {
					BeanUtils.copyProperties(info, template);
				} catch (IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}
				
				info.getExecutionPlans().clear();
//				info.setJobs(null);
				info.setIdeaSessionId(0);
				
				
			}else{
				log.info("Template Analysis is of not same type. Import skipped.");
			}
		}
		
	}
	
	@Override
	public void next() {
		readForm();
		super.next();
	}
	
//	@Override
//	public void back() {
//		readForm();
//		super.back();
//	}
	
	@Override
	protected boolean validateForm() {
		boolean result = true;
		result = validateTextField(analysisNameText) && result;
		result = validateTextField(analysisLocationFile) && result;
		
		return result;
	}
	
}

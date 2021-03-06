package com.airbusds.gui.wizard;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;


@SuppressWarnings("serial")
public class SimpleWizardDialog extends JDialog {
	
	WizardComponents wizardComponents;
	
	JPanel buttonPanel;
	JLabel statusLabel = new JLabel();
	JPanel bottomPanel = new JPanel();
	
	public SimpleWizardDialog() {
		wizardComponents = new DefaultJWizardComponents();
		init();
	}
	
	public SimpleWizardDialog(Frame owner, boolean modal) {
		super(owner, modal);
		wizardComponents = new DefaultJWizardComponents();
		init();
	}
	
	private void init() {
		
		this.getContentPane().setLayout(new GridBagLayout());
		this.getContentPane().add(wizardComponents.getWizardPanelsContainer(),
				new GridBagConstraints(0, 0, 1, 1, 1.0, 0.9
						, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		
		this.getContentPane().add(new JSeparator(),
				new GridBagConstraints(0, 1, 1, 0, 1.0, 0.01
						,GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(1, 1, 1, 1), 0, 0));
		
		buttonPanel = new SimpleButtonPanel(wizardComponents);
		this.getContentPane().add(buttonPanel,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 0.09
						,GridBagConstraints.WEST, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
		
		wizardComponents.setFinishAction(new FinishAction(wizardComponents) {
			public void performAction() {
				dispose();
			}
		});
		wizardComponents.setCancelAction(new CancelAction(wizardComponents) {
			public void performAction() {
				dispose();
			}
		});
		
	}
	
	public WizardComponents getWizardComponents(){
		return wizardComponents;
	}
	
	public void setWizardComponents(WizardComponents aWizardComponents){
		wizardComponents = aWizardComponents;
	}
	
	public void show() {
		wizardComponents.updateComponents();
		super.show();
	}
	
}
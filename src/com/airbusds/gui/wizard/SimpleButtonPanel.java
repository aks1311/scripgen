package com.airbusds.gui.wizard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SimpleButtonPanel extends JPanel {

  JLabel statusLabel = new JLabel();

  public SimpleButtonPanel(WizardComponents wizardComponents) {
    this.setLayout(new GridBagLayout());
    this.add(statusLabel,  new GridBagConstraints(0, 0, 1, 1, 0.7, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
    this.add(wizardComponents.getBackButton(), new GridBagConstraints(1, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
    this.add(wizardComponents.getNextButton(), new GridBagConstraints(2, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));
	this.add(wizardComponents.getFinishButton(), new GridBagConstraints(3, 0, 1, 1, 0.1, 0.0
			,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 0, 2, 0), 0, 0));    
    this.add(wizardComponents.getCancelButton(), new GridBagConstraints(4, 0, 1, 1, 0.1, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(2, 3, 2, 2), 0, 0));

  }

}
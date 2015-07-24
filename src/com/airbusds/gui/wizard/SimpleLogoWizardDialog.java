package com.airbusds.gui.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
@Deprecated
public class SimpleLogoWizardDialog extends JDialog {
    
    DefaultJWizardComponents wizardComponents;
    
    JPanel buttonPanel;
    JLabel statusLabel = new JLabel();
    
    ImageIcon logo;
    
    /** Creates a new SimpleLogoJWizardDialog without parent.
     * @param logo an ImageIcon object to be shown within the wizard
     */    
    public SimpleLogoWizardDialog(ImageIcon logo) {
        this.logo = logo;
        wizardComponents = new DefaultJWizardComponents();
        init();
    }
    
    /** Creates a new SimpleLogoJWizardDialog with a frame as owner.
     * @param owner Owner of this wizard dialog
     * @param logo an ImageIcon object to be shown within the wizard
     * @param modal true, if the wizard should be shown modal; false otherwise
     */    
    public SimpleLogoWizardDialog(Frame owner, ImageIcon logo, boolean modal) {
        super(owner, modal);
        this.logo = logo;
        wizardComponents = new DefaultJWizardComponents();
        init();
    }
    
    private void init() {
        this.getContentPane().setLayout(new GridBagLayout());
        
        JPanel logoPanel = new JPanel();
        
        String fileString;
        if (logo.toString().indexOf("file:") < 0 &&
        logo.toString().indexOf("http:") < 0) {
            fileString = "file:///" +System.getProperty("user.dir") +"/"
            +logo.toString();
            fileString = fileString.replaceAll("\\\\", "/");
        } else {
            fileString = logo.toString();
        }
        logoPanel.add(new JLabel("<html><img src='" +fileString +"'></html>"),
        BorderLayout.CENTER);
        logoPanel.setBackground(Color.WHITE);
        this.getContentPane().add(logoPanel,
        new GridBagConstraints(0, 0, 1, 1, 0.3, 0.9
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(wizardComponents.getWizardPanelsContainer(),
        new GridBagConstraints(1, 0, 1, 1, 0.7, 0.9
        , GridBagConstraints.CENTER, GridBagConstraints.BOTH,
        new Insets(0, 0, 0, 0), 0, 0));
        
        this.getContentPane().add(new JSeparator(),
        new GridBagConstraints(0, 1, 2, 0, 1.0, 0.01
        ,GridBagConstraints.WEST, GridBagConstraints.BOTH,
        new Insets(1, 1, 1, 1), 0, 0));
        
        buttonPanel = new SimpleButtonPanel(wizardComponents);
        this.getContentPane().add(buttonPanel,
        new GridBagConstraints(0, 2, 2, 1, 1.0, 0.09
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
    
    /** Returns this wizards components.
     * @return DefaultJWizardComponents
     */    
    public DefaultJWizardComponents getWizardComponents(){
        return wizardComponents;
    }
    
    /** Sets this wizards components.
     * @param aWizardComponents DefaultJWizardComponents
     */    
    public void setWizardComponents(DefaultJWizardComponents aWizardComponents){
        wizardComponents = aWizardComponents;
    }
    
    /** Shows this dialog. */    
    public void show() {
        wizardComponents.updateComponents();
        super.show();
    }
    
}
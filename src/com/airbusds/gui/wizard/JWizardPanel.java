package com.airbusds.gui.wizard;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public abstract class JWizardPanel extends JPanel {
    
    private WizardComponents wizardComponents;
    private String panelTitle;
    
    public JWizardPanel(WizardComponents wizardComponents) {
        this(wizardComponents, null);
    }
    
    public JWizardPanel(WizardComponents wizardComponents, String title) {
        this.wizardComponents = wizardComponents;
        this.panelTitle = title;
    }
    
    public void update() {
    }
    
    public void next() {
        goNext();
    }
    
    public void back() {
        goBack();
    }
    
    public WizardComponents getWizardComponents(){
        return wizardComponents;
    }
    
    public void setWizardComponents(WizardComponents awizardComponents){
        wizardComponents = awizardComponents;
    }
    
    public String getPanelTitle() {
        return panelTitle;
    }
    
    public void setPanelTitle(String title) {
        panelTitle = title;
    }
    
    protected boolean goNext() {
        if (wizardComponents.getWizardPanelList().size() > wizardComponents.getCurrentIndex()+1 ) {
            wizardComponents.setCurrentIndex(wizardComponents.getCurrentIndex()+1);
            wizardComponents.updateComponents();
            return true;
        } else {
            return false;
        }
    }
    
    protected boolean goBack() {
        if (wizardComponents.getCurrentIndex()-1 >= 0) {
            wizardComponents.setCurrentIndex(wizardComponents.getCurrentIndex()-1);
            wizardComponents.updateComponents();
            return true;
        } else {
            return false;
        }
    }
    
    protected void switchPanel(int panelIndex) {
        getWizardComponents().setCurrentIndex(panelIndex);
        getWizardComponents().updateComponents();
    }
    
    protected void setBackButtonEnabled(boolean set) {
        wizardComponents.getBackButton().setEnabled(set);
    }
    
    protected void setNextButtonEnabled(boolean set) {
        wizardComponents.getNextButton().setEnabled(set);
    }
    
    protected void setFinishButtonEnabled(boolean set) {
        wizardComponents.getFinishButton().setEnabled(set);
    }
}
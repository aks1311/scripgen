package com.airbusds.gui.wizard;

import java.util.List;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public interface WizardComponents extends Wizard {

	public void addWizardPanel(JWizardPanel panel);
	
	public void addWizardPanel(int index, JWizardPanel panel);

	public void addWizardPanelAfter(
		JWizardPanel panelToBePlacedAfter,
		JWizardPanel panel);

	public void addWizardPanelBefore(
		JWizardPanel panelToBePlacedBefore,
		JWizardPanel panel);
		
	public void addWizardPanelAfterCurrent(JWizardPanel panel);
	
	public JWizardPanel removeWizardPanel(JWizardPanel panel);

	public JWizardPanel removeWizardPanel(int index);

	public JWizardPanel removeWizardPanelAfter(JWizardPanel panel);

	public JWizardPanel removeWizardPanelBefore(JWizardPanel panel);

	public JWizardPanel getWizardPanel(int index);

	public int getIndexOfPanel(JWizardPanel panel);	

	public void updateComponents();

	public JWizardPanel getCurrentPanel() throws Exception;

	public FinishAction getFinishAction();

	public void setFinishAction(FinishAction aFinishAction);

	public CancelAction getCancelAction();

	public void setCancelAction(CancelAction aCancelAction);

	public int getCurrentIndex();

	public void setCurrentIndex(int aCurrentIndex);

	public JPanel getWizardPanelsContainer();

	public void setWizardPanelsContainer(JPanel aWizardPanelsContainer);

	public JButton getBackButton();

	public void setBackButton(JButton aBackButton);

	public JButton getNextButton();

	public void setNextButton(JButton aNextButton);

	public JButton getCancelButton();

	public void setCancelButton(JButton aCancelButton);

	public JButton getFinishButton();

	public void setFinishButton(JButton button);
	
	@SuppressWarnings("rawtypes")
	public List getWizardPanelList();

	@SuppressWarnings("rawtypes")
	public void setWizardPanelList(List panelList);
	
	public boolean onLastPanel();		

        public final static String CURRENT_PANEL_PROPERTY = "currentPanel";
        
        public void addPropertyChangeListener(PropertyChangeListener listener);
        
        public void removePropertyChangeListener(PropertyChangeListener listener);
        
}

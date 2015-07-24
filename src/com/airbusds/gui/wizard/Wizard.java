package com.airbusds.gui.wizard;

import java.util.List;


public interface Wizard {

  @SuppressWarnings("rawtypes")
public List getWizardPanelList();

  @SuppressWarnings("rawtypes")
public void setWizardPanelList(List panelList);

  public void addWizardPanel(JWizardPanel panel);

  public void addWizardPanel(int index, JWizardPanel panel);

  public JWizardPanel removeWizardPanel(JWizardPanel panel);

  public JWizardPanel removeWizardPanel(int index);

  public JWizardPanel getWizardPanel(int index);

}
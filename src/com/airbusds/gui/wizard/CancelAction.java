package com.airbusds.gui.wizard;



public abstract class CancelAction implements Action {

  WizardComponents wizardComponents;

  public CancelAction(WizardComponents wizardComponents) {
    this.wizardComponents = wizardComponents;
  }

}
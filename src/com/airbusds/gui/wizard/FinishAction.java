package com.airbusds.gui.wizard;




public abstract class FinishAction implements Action {

  WizardComponents wizardComponents;

  public FinishAction(WizardComponents wizardComponents) {
    this.wizardComponents = wizardComponents;
  }

}
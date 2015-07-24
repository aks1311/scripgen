package com.airbusds.idea.model;

import java.beans.PropertyChangeListener;

public interface PropertySupport {

  public void addPropertyChangeListener(PropertyChangeListener listener);

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

  public void removePropertyChangeListener(PropertyChangeListener listener);

  public boolean hasListeners(String propertyName);

  public void firePropertyChange(String property, Object oldval, Object newval);

}
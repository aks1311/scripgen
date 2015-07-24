package com.airbusds.idea.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

public class InfoBase implements Serializable{
	
	private static final long serialVersionUID = -7642416518873827397L;
	
	protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.removePropertyChangeListener(listener);
    }
	
	@Override
	public String toString() {
		try {
			java.util.Map<String, String> map = BeanUtils.describe(this);
			return map.toString();
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
//			e.printStackTrace();
		}
		return "ERROR_OCCURED";
	}
	
}

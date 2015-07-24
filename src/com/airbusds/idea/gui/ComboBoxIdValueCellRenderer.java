package com.airbusds.idea.gui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.airbusds.idea.config.InputFileConfig.IdValue;

@SuppressWarnings("serial")
public class ComboBoxIdValueCellRenderer extends JLabel implements
		ListCellRenderer {

	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		IdValue val = (IdValue)value;
		
		return null;
	}

}

package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

@SuppressWarnings("serial")
public class AlternateColorListCellRenderer extends JLabel implements ListCellRenderer {

    public AlternateColorListCellRenderer() {
        setOpaque(true);
    }
    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Assumes the stuff in the list has a pretty toString
        setText(value.toString());

        // based on the index you set the color.  This produces the every other effect.
        if(isSelected)
        	setBackground(new Color(11200250));
        else if (index % 2 == 0)
        	setBackground(Color.WHITE);
        else 
        	setBackground(new Color(15132390));

        return this;
    }
}
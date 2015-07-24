package com.airbusds.idea.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.airbusds.idea.model.Paragraph;

@SuppressWarnings("serial")
public abstract class ParagraphView extends JPanel {
	
	private Paragraph paragraph;
	private boolean isSelected;
	List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	
	JPanel markerPanel = new JPanel();
	JLabel marker = new JLabel(".");
	JPanel bodyPanel = new JPanel();
	
	public ParagraphView(Paragraph paragraph){
		this.paragraph = paragraph;
		
		_init();
		init();
		
		resetUI();
		resetData();

	}
	
	public abstract void init();
	public abstract void resetData();
	
	public Paragraph getParagraph() {
		return paragraph;
	}

	public void select(){
		if(!isSelected()){
			onSelect();
			this.isSelected = true;
			resetUI();
		}
	}
	
	public void unSelect(){
		this.isSelected = false;
		resetUI();
		resetData();
	}
	
	public boolean isSelected(){
		return isSelected;
	}

	public void addSelectionListener(SelectionListener listener){
		selectionListeners.add(listener);
	}
	
	protected void checkPanelSelection(){
		if(isSelected()){
			bodyPanel.setBackground(new Color(11200250)); // TODO 3 Selected color from config
		}
	}
	
	protected void resetUI(){

		// TODO 3 configurable alternate colors
		bodyPanel.setBackground(Color.WHITE);
		
//		if( paragraph.getSequence()%2 == 0){
//			this.setBackground(Color.WHITE);
//		}else{
//			this.setBackground(new Color(15132390));
//		}
		checkPanelSelection();
	}
	
	private void onSelect(){
		if(selectionListeners!=null){
			for (SelectionListener listener : selectionListeners) {
				listener.onSelect(this);
			}
		}
	}
	
	private void _init(){
		
		BorderLayout bl = new BorderLayout();
		this.setLayout(bl);
		this.add(markerPanel, BorderLayout.WEST);
		this.add(bodyPanel, BorderLayout.CENTER);
		
		markerPanel.setPreferredSize(new Dimension(4, 8));
		markerPanel.setMinimumSize(new Dimension(4, 8));
		
		bodyPanel.setBackground(Color.WHITE);
		bodyPanel.addMouseListener(new MouseListener() {
			
			@Override public void mouseReleased(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseClicked(MouseEvent me) {
				select();
			}
			@Override public void mouseExited(MouseEvent me) {
				resetUI();
			}
			@Override public void mouseEntered(MouseEvent me) {
				JPanel currentPanel = (JPanel)me.getSource();
				currentPanel.setBackground(new Color(11532970)); // TODO 3 Color form properties
			}
		});
		
//		markerPanel.add(marker);
		markerPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		clearMarker();
	}
	
	public void setMarkerMessage(String message, Color color){
		markerPanel.setForeground(color);
		markerPanel.setBackground(color);
		markerPanel.setBorder(BorderFactory.createLineBorder(color));
		markerPanel.setToolTipText(message);
	}
	
	public void clearMarker(){
		markerPanel.setForeground(Color.WHITE);
		markerPanel.setBackground(Color.WHITE);
		markerPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE));
		markerPanel.setToolTipText(null);
	}
	
	public JPanel getBodyPanel(){
		return bodyPanel;
	}
	
}

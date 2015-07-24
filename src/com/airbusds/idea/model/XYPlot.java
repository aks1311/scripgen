package com.airbusds.idea.model;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.title.LegendTitle;

import com.airbusds.gui.common.AdvancedFileChooser;

public abstract class XYPlot {

	protected ChartPanel chartPanel;
	protected JFreeChart chart;
	
	protected Color[] colorseries = new Color[]{
				Color.RED, Color.BLUE, 
				Color.GREEN, Color.ORANGE,
				Color.BLACK, Color.CYAN, 
				Color.YELLOW, Color.GRAY, 
				Color.MAGENTA, Color.PINK 
			};

	protected JPopupMenu createPopUpMenu() {
		JPopupMenu menu = new JPopupMenu();
		JMenu saveMenu = new JMenu("Save As..");
		menu.add(saveMenu);
		
		JMenuItem item = new JMenuItem("PNG");
		item.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				saveImage("PNG");
			}
		});
		saveMenu.add(item);
		
		item = new JMenuItem("JPEG");
		item.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				saveImage("JPEG");
			}
		});
		saveMenu.add(item);
		
		JMenu[] menus = createMenus();
		if(menus!=null){
			for (int i = 0; i < menus.length; i++) {
				menu.add(menus[i]);
			}
		}
		
		return menu;
	}

	protected void saveImage(String type) {
		
		// TODO 3 Image size should be dependent on the system graphics or configurable
		int height = 793;
		int width = 1120; 
		
		AdvancedFileChooser fileChooser = new AdvancedFileChooser();
		
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnVal = fileChooser.showSaveDialog(chartPanel);
	
		File filePath = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			filePath = fileChooser.getSelectedFile();
		}
		
		String tempPath = filePath.getAbsolutePath().toUpperCase();
		
		try{
			if(type.equals("PNG")){
				if(!tempPath.endsWith(".PNG"))
					filePath = new File(tempPath+".PNG");
				ChartUtilities.saveChartAsPNG(filePath, getChart(), width, height);
			}else if(type.equals("JPEG")){
				if(!tempPath.endsWith(".JPEG"))
					filePath = new File(tempPath+".JPEG");
				ChartUtilities.saveChartAsJPEG(filePath, getChart(), width, height);
			}
		}catch(IOException ie){
			
		}
	
	}

	public JFreeChart getChart(){
		return chart;
	}

	public JMenu[] createMenus(){
		return null;
	}

	public abstract JComponent createChart();

	public abstract LegendTitle getLegend();
	
	public abstract String getTitle();

	protected void showLegends() {
		getChart().addLegend(getLegend());
	}

	protected void hideLegend() {
		getChart().removeLegend();
	}

	protected void showTitle() {
		getChart().setTitle(getTitle());
	}

	protected void hideTitle() {
		getChart().setTitle((String)null);
	}

}

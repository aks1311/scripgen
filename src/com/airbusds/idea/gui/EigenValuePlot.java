package com.airbusds.idea.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.LegendItemSource;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;

import com.airbusds.Utilities;
import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.model.IDEAJob;
import com.airbusds.idea.model.JobResult;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.utilities.StringUtils;
import com.airbusds.idea.utilities.TableDataHelper;

public class EigenValuePlot extends JobResult implements ActionListener, ItemListener{
	
	private int curveCount;
//	private JFreeChart chart;
	private LegendTitle legendTitle;
	private EigenValueTableModel tableModel;
	private XYLineAndShapeRenderer lineRenderer;

	public EigenValuePlot(IDEAJob job){
		super(job);
	}
	
	@Override
	public String toString() {
		return "EigenValue-Plot";
	}

	@Override
	public JComponent createChart() {
		
		if(!StringUtils.hasText(config.evPlotSubTitle)){
			config.evPlotSubTitle = getInfoString();
		}
		
		NumberAxis domainAxis = new NumberAxis(config.evRePartText); // x axis
		NumberAxis rangeAxis = new NumberAxis(config.evImPartText);  // y axis
		
		lineRenderer = new XYLineAndShapeRenderer();
	    lineRenderer.setUseFillPaint(true);
	    lineRenderer.setBaseShapesVisible(false);
	    lineRenderer.setAutoPopulateSeriesShape(false);
		
		XYPlot xyPlot = new XYPlot(getDataSet(), domainAxis, rangeAxis, lineRenderer);
		
		for (int i = 0; i < curveCount; i++) {
			lineRenderer.setSeriesPaint(i, colorseries[i]);
		}
		
		curveCount = xyPlot.getSeriesCount();
		
		chart = new JFreeChart(null, null, xyPlot, false);
	    chart.setBackgroundPaint(Color.white);
	    
		chartPanel = new ChartPanel(chart, false, true, false, false, false);
		chartPanel.setPopupMenu(createPopUpMenu());
	    
		showTitle();
	    showLegends();
	    showSubtitle();
	    
		JPanel blPanel = new JPanel();
		blPanel.setLayout(new BorderLayout());
		blPanel.add(chartPanel, BorderLayout.CENTER);
		blPanel.add(createControlsPanel(), BorderLayout.SOUTH);
		blPanel.setVisible(false);
		
		return blPanel;
	}

	@Override
	public JComponent createTable() {
		
		if(tableModel==null){
			Double[][] reData = TableDataHelper.readVgFile(getResultFilePath("flutter_plot_Re_eigenvalue"));
			Double[][] imData = TableDataHelper.readVfFile(getResultFilePath("flutter_plot_Im_eigenvalue"));
			
			if(reData==null || imData==null || reData.length==0 || imData.length==0)
				return new JPanel();
			
			tableModel = new EigenValueTableModel(imData, reData, getFlutterVariable());
		}
		
		return new JScrollPane(new JTable(tableModel));
	}
	
	public boolean areResultFilesPresent(){
		if(!new File(getResultFilePath("flutter_plot_Re_eigenvalue")).exists() || !new File(getResultFilePath("flutter_plot_Im_eigenvalue")).exists())
			return false;
		return true;
	}

	@Override
	public JMenu[] createMenus() {
		JMenu edit = new JMenu("Edit"); 
		JMenuItem menuItem = new JMenuItem("Title Text");
		menuItem.setActionCommand("MENU.EDIT.TITLE");
		menuItem.addActionListener(this);
		edit.add(menuItem);
		
		menuItem = new JMenuItem("Subtitle Text");
		menuItem.setActionCommand("MENU.EDIT.SUBTITLE");
		menuItem.addActionListener(this);
		edit.add(menuItem);
		
		edit.add(new JSeparator());
		
		menuItem = new JMenuItem("Real Value Axis Label");
		menuItem.setActionCommand("MENU.EDIT.REAXIS");
		menuItem.addActionListener(this);
		edit.add(menuItem);

		menuItem = new JMenuItem("Imaginary Value Axis Label");
		menuItem.setActionCommand("MENU.EDIT.IMAXIS");
		menuItem.addActionListener(this);
		edit.add(menuItem);

		return new JMenu[]{edit};
	}

	@Override
	public LegendTitle getLegend() {
		if(legendTitle == null){
			final LegendItemCollection chartLegend = new LegendItemCollection();
			Shape shape = new Rectangle(10, 10);
			for (int i = 0; i < curveCount; i++) {
				lineRenderer.setSeriesPaint(i, colorseries[i]);
				chartLegend.add(new LegendItem("Curve "+(i+1), null, null, null, shape, colorseries[i]));
			}
			LegendItemSource chartLegendSource = new LegendItemSource() {
				@Override public LegendItemCollection getLegendItems() {
					return chartLegend;
				}
			};
			legendTitle = new LegendTitle(chartLegendSource);
			legendTitle.setPosition(RectangleEdge.BOTTOM);
		}
		return legendTitle;
	}
	
	@Override
	public String getTitle() {
		return config.evPlotTitle;
	}

	@Override
	public String getSubTitle() {
		return config.evPlotSubTitle;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		JCheckBox cb = (JCheckBox)e.getSource();
		String action = cb.getActionCommand();
		
		switch(action){
		
		case("SHOW.LEGENDS"):
			if(cb.isSelected()){
				showLegends();
			}else{
				hideLegend();
			}
			break;
		
		case("SHOW.HEADINGS"):
			if(cb.isSelected()){
				showTitle();
			}else{
				hideTitle();
			}
			break;
		
		case("SHOW.INFO"):
			if(cb.isSelected()){
				showSubtitle();
			}else{
				hideSubTitle();
			}
			break;
		
		default:
			int curve = Integer.parseInt(action);
			lineRenderer.setSeriesVisible(curve, cb.isSelected());
		}
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
		String command = e.getActionCommand();
		
		switch(command){
			
			case("MENU.EDIT.TITLE"):
			{
				config.evPlotTitle = Utilities.promptQuestion("Edit Title Text", config.evPlotTitle); 
				if(StringUtils.hasText(config.evPlotTitle)){
					showTitle();
				}
			}
				break;
			case("MENU.EDIT.SUBTITLE"):
			{
				config.evPlotSubTitle = Utilities.promptQuestion("Edit Subtitle Text", config.evPlotSubTitle);
				if(StringUtils.hasText(config.evPlotSubTitle)){
					showSubtitle();
				}
			}
				break;
			case("MENU.EDIT.REAXIS"):
			{
				config.evRePartText = Utilities.promptQuestion("Edit Real Value Axis Text", config.evRePartText);
				if(StringUtils.hasText(config.evRePartText))
					((XYPlot)getChart().getPlot()).getDomainAxis().setLabel(config.evRePartText);
			}	
				break;
			case("MENU.EDIT.IMAXIS"):
			{	
				config.evImPartText = Utilities.promptQuestion("Edit Imaginary Value Axis Text", config.evImPartText);
				if(StringUtils.hasText(config.evImPartText))
					((XYPlot)getChart().getPlot()).getRangeAxis().setLabel(config.evImPartText);
			}	
				break;
			
			default:
		
		}
		
	}

	private Component createControlsPanel() {
		JPanel displayOptionPanel = new JPanel();
		displayOptionPanel.setLayout(new BoxLayout(displayOptionPanel, BoxLayout.Y_AXIS));
		
		JCheckBox cbHeadings = new JCheckBox("Show Title", true);
		JCheckBox cbInfo = new JCheckBox("Show Subtitle", true);
		JCheckBox cbLegends = new JCheckBox("Show Legend", true);
		
		cbHeadings.setActionCommand("SHOW.HEADINGS");
		cbLegends.setActionCommand("SHOW.LEGENDS");
		cbInfo.setActionCommand("SHOW.INFO");
		
		cbHeadings.addItemListener(this);
		cbLegends.addItemListener(this);
		cbInfo.addItemListener(this);
		
		displayOptionPanel.add(cbHeadings);
		displayOptionPanel.add(cbInfo);
		displayOptionPanel.add(cbLegends);
		displayOptionPanel.setBorder(new TitledBorder("Display Options"));
		
		JPanel curveListPanel = new JPanel();
		curveListPanel.setLayout(new BoxLayout(curveListPanel, BoxLayout.Y_AXIS));
		curveListPanel.setBorder(new TitledBorder("Show Curves"));
		
		for (int i = 0; i < curveCount; i++) {
			JCheckBox cb = new JCheckBox("Curve "+(i+1), true);
			cb.setActionCommand(i+"");
			cb.addItemListener(this);
			curveListPanel.add(cb);
		}
		
		JPanel rangePanel = new JPanel();
		PainlessGridbagConfiguration innerConfig = new PainlessGridbagConfiguration();
		innerConfig.setVirticalSpacing(0);
		innerConfig.setFirstRowTopSpacing(0);
		innerConfig.setFirstColumnLeftSpacing(0);
		innerConfig.setLastRowBottomSpacing(0);
		
		PainlessGridBag innerGB = new PainlessGridBag(rangePanel, innerConfig, false);
		
		final JTextField imValueMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField imValueMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField reValueMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField reValueMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		
		final XYPlot plot = chart.getXYPlot();
		
		imValueMin.setText(plot.getRangeAxis().getRange().getLowerBound()+"");
		imValueMax.setText(plot.getRangeAxis().getRange().getUpperBound()+"");
		reValueMin.setText(plot.getDomainAxis().getRange().getLowerBound()+"");
		reValueMax.setText(plot.getDomainAxis().getRange().getUpperBound()+"");
		
		JButton replotBtn = new JButton("Replot..");
		replotBtn.setActionCommand("REPLOT");
		replotBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				float imMinVal = Float.parseFloat(imValueMin.getText());
				float imMaxVal = Float.parseFloat(imValueMax.getText());
				float reMinVal = Float.parseFloat(reValueMin.getText());
				float reMaxVal = Float.parseFloat(reValueMax.getText());
				
				if( imMinVal>imMaxVal || reMinVal>reMaxVal ){
					return;
				}
				
				plot.getRangeAxis().setRange(imMinVal, imMaxVal);
				plot.getDomainAxis().setRange(reMinVal, reMaxVal);
				
			}
		});
		
		plot.addChangeListener(new PlotChangeListener() {
			@Override public void plotChanged(PlotChangeEvent pce) {
				reValueMin.setText( plot.getDomainAxis().getLowerBound() +"");
				reValueMax.setText( plot.getDomainAxis().getUpperBound() +"");
				imValueMin.setText( plot.getRangeAxis().getLowerBound() +"");
				imValueMax.setText( plot.getRangeAxis().getUpperBound() +"");
			}
		});
		
		innerGB.row().cell(new JLabel("Imaginary Part")).cell(imValueMin).fillX().cell(imValueMax).fillX();
		innerGB.row().cell(new JLabel("Real Part")).cell(reValueMin).fillX().cell(reValueMax).fillX().cell(replotBtn);
		innerGB.doneAndPushEverythingToTop();
		rangePanel.setBorder(new TitledBorder("Select Ranges"));
		
		
		JPanel panel = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(panel, false);
		gbl.row().cell(displayOptionPanel).cell(curveListPanel).cell(rangePanel);
		gbl.constraints(displayOptionPanel).anchor = GridBagConstraints.NORTHWEST;
		gbl.constraints(curveListPanel).anchor = GridBagConstraints.NORTHWEST;
		gbl.constraints(rangePanel).anchor = GridBagConstraints.NORTHWEST;
		gbl.doneAndPushEverythingToTop();
		
		return panel;
	}

	private String getFlutterVariable(){
		Parameter param = IDEAContext.getInstance().getAnalysisInfo().getParamIn().getParameter("flutter_plot_variable");
		String flutter_plot_variable = (String)param.getValue().getValue();
		return flutter_plot_variable;
	}

	private XYDataset getDataSet(){
		return tableModel.getDataSet();
	}

}

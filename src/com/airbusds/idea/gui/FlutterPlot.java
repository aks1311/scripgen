package com.airbusds.idea.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.Iterator;

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
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
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

public class FlutterPlot extends JobResult implements ActionListener, ItemListener{
	
	private int curveCount;
	private ChartPanel chartPanel;
	private FlutterCurveTableModel tableModel;
	
//	private JFreeChart chart;
	private CombinedRangeXYPlot combinedPlot;
	private LegendTitle legend;
	private XYItemRenderer vgRenderer;
	private XYItemRenderer vfRenderer;
//	private TextTitle info;
	
	public FlutterPlot(IDEAJob job) {
		super(job);
	}
	
	@Override
	public String toString() {
		return "Flutter Plot";
	}
	
	@Override
	public JComponent createChart() {
		
		if(!StringUtils.hasText(config.flutterPlotSubtitle)){
			config.flutterPlotSubtitle = getInfoString();
		}
		
		if(!StringUtils.hasText(config.flutterVarAxisLabel))
			config.flutterVarAxisLabel = getFlutterVariable();
		
		XYPlot vgPlot = createVgPlot();
		XYPlot vfPlot = createVfPlot();
		
		NumberAxis flutterVariableAxis = new NumberAxis(config.flutterVarAxisLabel);
		combinedPlot = new CombinedRangeXYPlot(flutterVariableAxis);
		combinedPlot.add(vgPlot);
		combinedPlot.add(vfPlot);
		
		vgRenderer = vgPlot.getRenderer();
		vfRenderer = vfPlot.getRenderer();
		
		curveCount = vgPlot.getSeriesCount();
		
		chart = new JFreeChart(null, null, combinedPlot, false);
	    chart.setBackgroundPaint(Color.white);

		for (int i = 0; i < curveCount; i++) {
			vgRenderer.setSeriesPaint(i, colorseries[i]);
			vfRenderer.setSeriesPaint(i, colorseries[i]);
		}
	    
		showTitle();
	    showSubtitle();
	    showLegends();
	    
		JButton printBtn = new JButton("Print");
		printBtn.setActionCommand("PRINT_CHART");
		printBtn.addActionListener(this);
		
		chartPanel = new ChartPanel(chart, false, true, false, false, false);
		chartPanel.setPopupMenu(createPopUpMenu());
		
		JPanel blPanel = new JPanel();
		blPanel.setLayout(new BorderLayout());
		blPanel.add(chartPanel, BorderLayout.CENTER);
		blPanel.add(createControlsPanel(), BorderLayout.SOUTH);
		blPanel.setVisible(false);
		
		return blPanel;
	}
	
	@Override
	public JComponent createTable() {
		Double[][] vgData = TableDataHelper.readVgFile(getResultFilePath("flutter_plot_g"));
		Double[][] vfData = TableDataHelper.readVfFile(getResultFilePath("flutter_plot_f"));
		
		if(vgData==null || vfData==null || vgData.length==0 || vfData.length==0)
			return new JPanel();
		
		tableModel = new FlutterCurveTableModel(vgData, vfData, getFlutterVariable());
		JTable table = new JTable(tableModel);
		JScrollPane sp = new JScrollPane(table);
		
		return sp;
	}
	
	public boolean areResultFilesPresent(){
		if(!new File(getResultFilePath("flutter_plot_g")).exists() || !new File(getResultFilePath("flutter_plot_f")).exists())
			return false;
		else
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
		
		menuItem = new JMenuItem("Vg Plot Axis Label");
		menuItem.setActionCommand("MENU.EDIT.VGAXIS");
		menuItem.addActionListener(this);
		edit.add(menuItem);

		menuItem = new JMenuItem("Vf Plot Axis Label");
		menuItem.setActionCommand("MENU.EDIT.VFAXIS");
		menuItem.addActionListener(this);
		edit.add(menuItem);
		
		menuItem = new JMenuItem("Flutter Variable Axis Label");
		menuItem.setActionCommand("MENU.EDIT.FLUTTERAXIS");
		menuItem.addActionListener(this);
		edit.add(menuItem);
		
		return new JMenu[]{edit};
	}
	
	@Override
	public LegendTitle getLegend() {
		if(legend == null){
			final LegendItemCollection chartLegend = new LegendItemCollection();
			Shape shape = new Rectangle(10, 10);
			for (int i = 0; i < curveCount; i++) {
				chartLegend.add(new LegendItem("Curve "+(i+1), null, null, null, shape, colorseries[i]));
			}
			
			LegendItemSource chartLegendSource = new LegendItemSource() {
				@Override public LegendItemCollection getLegendItems() {
					return chartLegend;
				}
			};
			legend = new LegendTitle(chartLegendSource);
			legend.setPosition(RectangleEdge.BOTTOM);
		}
		return legend;
	}

	@Override
	public String getTitle() {
		return config.flutterPlotTitle;
	}

	@Override
	public String getSubTitle() {
		return config.flutterPlotSubtitle;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	
		String command = e.getActionCommand();
		
		Iterator plotsIterator = combinedPlot.getSubplots().iterator();
		final XYPlot plot1 = (XYPlot)plotsIterator.next();
		final XYPlot plot2 = (XYPlot)plotsIterator.next();
		
		switch(command){
			
			case("MENU.EDIT.TITLE"):
			{
				config.flutterPlotTitle = Utilities.promptQuestion("Edit Title Text", config.flutterPlotTitle); 
				if(StringUtils.hasText(config.flutterPlotTitle)){
					showTitle();
				}
			}
				break;
			case("MENU.EDIT.SUBTITLE"):
			{
				config.flutterPlotSubtitle = Utilities.promptQuestion("Edit Subtitle Text", config.flutterPlotSubtitle);
				if(StringUtils.hasText(config.flutterPlotSubtitle)){
					showSubtitle();
				}
			}
				break;
			case("MENU.EDIT.VGAXIS"):
			{
				config.flutterDampingAxisLabel = Utilities.promptQuestion("Edit VgPlot Axis Text", config.flutterDampingAxisLabel);
				if(StringUtils.hasText(config.flutterDampingAxisLabel))
					plot1.getDomainAxis().setLabel(config.flutterDampingAxisLabel);
			}	
				break;
			case("MENU.EDIT.VFAXIS"):
			{	
				config.flutterFreqAxisLabel = Utilities.promptQuestion("Edit VfPlot Axis Text", config.flutterFreqAxisLabel);
				if(StringUtils.hasText(config.flutterFreqAxisLabel))
					plot2.getDomainAxis().setLabel(config.flutterFreqAxisLabel);
			}	
				break;
			case("MENU.EDIT.FLUTTERAXIS"):
			{
				config.flutterVarAxisLabel = Utilities.promptQuestion("Edit Flutter Variable Axis Text", config.flutterVarAxisLabel);
				if(StringUtils.hasText(config.flutterVarAxisLabel))
					combinedPlot.getRangeAxis().setLabel(config.flutterVarAxisLabel);
			}
				break;
			
			default:
		
		}
		
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
			vgRenderer.setSeriesVisible(curve, cb.isSelected());
			vfRenderer.setSeriesVisible(curve, cb.isSelected());
		}
		
	}

	private String getFlutterVariable(){
		Parameter param = IDEAContext.getInstance().getAnalysisInfo().getParamIn().getParameter("flutter_plot_variable");
		String flutter_plot_variable = (String)param.getValue().getValue();
		return flutter_plot_variable;
	}

	private XYPlot createVfPlot(){
		
		NumberAxis plotAxis = new NumberAxis(config.flutterFreqAxisLabel);
		
		XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
	    lineRenderer.setUseFillPaint(true);
	    lineRenderer.setBaseShapesVisible(false);
	    lineRenderer.setAutoPopulateSeriesShape(true);
		
		XYPlot xyPlot = new XYPlot(getVfDataSet(), plotAxis, null, lineRenderer);
		
		return xyPlot;
	}

	private XYPlot createVgPlot(){
			NumberAxis plotAxis = new NumberAxis(config.flutterDampingAxisLabel);
			plotAxis.setInverted(true);
			
			XYSeriesCollection series = (XYSeriesCollection)getVgDataSet();
			
			XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer();
		    lineRenderer.setUseFillPaint(true);
		    lineRenderer.setBaseShapesVisible(false);
		    lineRenderer.setAutoPopulateSeriesShape(false);
			
			XYPlot xyPlot = new XYPlot(series, plotAxis, null, lineRenderer);
			xyPlot.setDomainZeroBaselineVisible(true);
			return xyPlot;
		}

	private XYDataset getVfDataSet(){
	    return tableModel.getVfDataSet();
	}

	private XYDataset getVgDataSet(){
		return tableModel.getVgDataSet();
	}

	private JPanel createControlsPanel(){
		
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
		
		final JTextField flutterMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField flutterMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField dampingMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField dampingMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField freqMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField freqMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		
		Iterator plotsIterator = combinedPlot.getSubplots().iterator();
		final XYPlot plot1 = (XYPlot)plotsIterator.next();
		final XYPlot plot2 = (XYPlot)plotsIterator.next();
		
		flutterMin.setText(combinedPlot.getRangeAxis().getRange().getLowerBound()+"");
		flutterMax.setText(combinedPlot.getRangeAxis().getRange().getUpperBound()+"");
		dampingMin.setText(plot1.getDomainAxis().getRange().getLowerBound()+"");
		dampingMax.setText(plot1.getDomainAxis().getRange().getUpperBound()+"");
		freqMin.setText(plot2.getDomainAxis().getRange().getLowerBound()+"");
		freqMax.setText(plot2.getDomainAxis().getRange().getUpperBound()+"");
		
		JButton replotBtn = new JButton("Replot..");
		replotBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				float flutterMinVal = Float.parseFloat(flutterMin.getText());
				float flutterMaxVal = Float.parseFloat(flutterMax.getText());
				float dampingMinVal = Float.parseFloat(dampingMin.getText());
				float dampingMaxVal = Float.parseFloat(dampingMax.getText());
				float freqMinVal = Float.parseFloat(freqMin.getText());
				float freqMaxVal = Float.parseFloat(freqMax.getText());
				
				if( flutterMinVal>flutterMaxVal || dampingMinVal>dampingMaxVal || freqMinVal>freqMaxVal){
					
					return;
				}
				
				combinedPlot.getRangeAxis().setRange(flutterMinVal, flutterMaxVal);
				plot1.getDomainAxis().setRange(dampingMinVal, dampingMaxVal);
				plot2.getDomainAxis().setRange(freqMinVal, freqMaxVal);
				
			}
		});
		
		plot1.addChangeListener(new PlotChangeListener() {
			@Override public void plotChanged(PlotChangeEvent pce) {
				dampingMin.setText( plot1.getDomainAxis().getLowerBound() +"");
				dampingMax.setText( plot1.getDomainAxis().getUpperBound() +"");
				flutterMin.setText( plot1.getRangeAxis().getLowerBound() +"");
				flutterMax.setText( plot1.getRangeAxis().getUpperBound() +"");
			}
		});
		plot2.addChangeListener(new PlotChangeListener() {
			@Override public void plotChanged(PlotChangeEvent pce) {
				freqMin.setText( plot2.getDomainAxis().getLowerBound() +"");
				freqMax.setText( plot2.getDomainAxis().getUpperBound() +"");
				flutterMin.setText( plot2.getRangeAxis().getLowerBound() +"");
				flutterMax.setText( plot2.getRangeAxis().getUpperBound() +"");
			}
		});
		
		innerGB.row().cell(new JLabel("Flutter Variable")).cell(flutterMin).fillX().cell(flutterMax).fillX();
		innerGB.row().cell(new JLabel("Damping Value")).cell(dampingMin).fillX().cell(dampingMax).fillX();
		innerGB.row().cell(new JLabel("Frequency")).cell(freqMin).fillX().cell(freqMax).fillX().cell(replotBtn);
		innerGB.doneAndPushEverythingToTop();
		rangePanel.setBorder(new TitledBorder("Select Ranges"));
		
		JPanel panel = new JPanel();
		PainlessGridBag gbl = new PainlessGridBag(panel, false);		
		gbl.row().cell(displayOptionPanel).cell(curveListPanel).cell(rangePanel);
		gbl.constraints(displayOptionPanel).anchor = GridBagConstraints.NORTHWEST;
		gbl.constraints(curveListPanel).anchor = GridBagConstraints.NORTHWEST;
		gbl.doneAndPushEverythingToTop();
		
		return panel;
	}
	
}

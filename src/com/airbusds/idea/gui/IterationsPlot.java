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
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.JobResult;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.utilities.StringUtils;
import com.airbusds.idea.utilities.TableDataHelper;

public class IterationsPlot extends JobResult implements ItemListener, ActionListener{
	
	private IterationsTableModel tableModel;
	private int curveCount;
//	private JFreeChart chart;
	private ChartPanel chartPanel;
	private LegendTitle legendTitle;
	private XYLineAndShapeRenderer lineRenderer;

	public IterationsPlot(IDEAJob job){
		super(job);
	}
	

	@Override
	public String toString() {
		return "Iterations-Plot";
	}


	@Override
	public JComponent createChart() {
		
		if(!StringUtils.hasText(config.itPlotSubTitle)){
			config.itPlotSubTitle = getInfoString();
		}
		
		if(!StringUtils.hasText(config.itFlutterAxisText))
			config.itFlutterAxisText = getFlutterVariable();
		
		NumberAxis flutterVariableAxis = new NumberAxis(config.itFlutterAxisText);
		NumberAxis plotAxis = new NumberAxis(config.iterationAxisText);
		
		lineRenderer = new XYLineAndShapeRenderer();
	    lineRenderer.setUseFillPaint(true);
	    lineRenderer.setBaseShapesVisible(false);
	    lineRenderer.setAutoPopulateSeriesShape(true);
		
		XYPlot plot = new XYPlot(getDataSet(), plotAxis, flutterVariableAxis, lineRenderer);
		
		curveCount = plot.getSeriesCount();
		
		for (int i = 0; i < curveCount; i++) {
			lineRenderer.setSeriesPaint(i, colorseries[i]);
		}
		
		chart = new JFreeChart(null, null, plot, false);
	    chart.setBackgroundPaint(Color.white);
	    
		showTitle();
		showSubtitle();
	    showLegends();
	    
		chartPanel = new ChartPanel(chart, false, true, false, false, false);
//			@Override public void mouseDragged(MouseEvent a) {}
//			@Override public void restoreAutoBounds() {}
//		};
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
		InputFile paramIn = IDEAContext.getInstance().getAnalysisInfo().getParamIn();
		Parameter flutter_plot_Iterations = paramIn.getParameter("flutter_plot_Iterations");
		
		String plotFilePath = getJob().getJobHome() + File.separatorChar + flutter_plot_Iterations.getValue().getValue().toString();
		Double[][] data = TableDataHelper.readVgFile(plotFilePath);
		
		if(data==null || data.length==0 )
			return new JPanel();
		
		tableModel = new IterationsTableModel(data, getFlutterVariable());
		JTable table = new JTable(tableModel);
		JScrollPane sp = new JScrollPane(table);
		
		return sp;
	}
	
	public boolean areResultFilesPresent(){
		if(!new File(getResultFilePath("flutter_plot_Iterations")).exists())
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
		
		menuItem = new JMenuItem("Flutter Variable Axis Label");
		menuItem.setActionCommand("MENU.EDIT.FLUTTER");
		menuItem.addActionListener(this);
		edit.add(menuItem);

		menuItem = new JMenuItem("Iterations Value Axis Label");
		menuItem.setActionCommand("MENU.EDIT.ITERATIONS");
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
		return config.itPlotTitle;
	}


	@Override
	public String getSubTitle() {
		return config.itPlotSubTitle;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		
		switch(command){
			
			case("MENU.EDIT.TITLE"):
			{
				config.itPlotTitle = Utilities.promptQuestion("Edit Title Text", config.itPlotTitle); 
				if(StringUtils.hasText(config.itPlotTitle)){
					showTitle();
				}
			}
				break;
			case("MENU.EDIT.SUBTITLE"):
			{
				config.itPlotSubTitle = Utilities.promptQuestion("Edit Subtitle Text", config.itPlotSubTitle);
				if(StringUtils.hasText(config.itPlotSubTitle)){
					showSubtitle();
				}
			}
				break;
			case("MENU.EDIT.FLUTTER"):
			{
				config.itFlutterAxisText = Utilities.promptQuestion("Edit Flutter Value Axis Text", config.itFlutterAxisText);
				if(StringUtils.hasText(config.itFlutterAxisText))
					((XYPlot)getChart().getPlot()).getRangeAxis().setLabel(config.itFlutterAxisText);
			}	
				break;
			case("MENU.EDIT.ITERATIONS"):
			{	
				config.iterationAxisText = Utilities.promptQuestion("Edit Itarations Value Axis Text", config.iterationAxisText);
				if(StringUtils.hasText(config.iterationAxisText))
					((XYPlot)getChart().getPlot()).getDomainAxis().setLabel(config.iterationAxisText);
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
			lineRenderer.setSeriesVisible(curve, cb.isSelected());
		}
		
	}


	private String getFlutterVariable(){
		Parameter param = IDEAContext.getInstance().getAnalysisInfo().getParamIn().getParameter("flutter_plot_variable");
		String flutter_plot_variable = (String)param.getValue().getValue();
		return flutter_plot_variable;
	}


	private XYDataset getDataSet(){
	    return tableModel.getDataSet();
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
		final JTextField iterationsMin = new FormattedNumberField(FormattedNumberField.FLOAT);
		final JTextField iterationsMax = new FormattedNumberField(FormattedNumberField.FLOAT);
		
		final XYPlot plot = chart.getXYPlot();
		
		flutterMin.setText(plot.getRangeAxis().getRange().getLowerBound()+"");
		flutterMax.setText(plot.getRangeAxis().getRange().getUpperBound()+"");
		iterationsMin.setText(plot.getDomainAxis().getRange().getLowerBound()+"");
		iterationsMax.setText(plot.getDomainAxis().getRange().getUpperBound()+"");
		
		JButton replotBtn = new JButton("Replot..");
		replotBtn.setActionCommand("REPLOT");
		replotBtn.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				float flutterMinVal = Float.parseFloat(flutterMin.getText());
				float flutterMaxVal = Float.parseFloat(flutterMax.getText());
				float iterationsMinVal = Float.parseFloat(iterationsMin.getText());
				float iterationsMaxVal = Float.parseFloat(iterationsMax.getText());
				
				if( flutterMinVal>flutterMaxVal || iterationsMinVal>iterationsMaxVal ){
					
					return;
				}
				
				plot.getRangeAxis().setRange(flutterMinVal, flutterMaxVal);
				plot.getDomainAxis().setRange(iterationsMinVal, iterationsMaxVal);
				
			}
		});
		
		
		plot.addChangeListener(new PlotChangeListener() {
			@Override public void plotChanged(PlotChangeEvent pce) {
				iterationsMin.setText( plot.getDomainAxis().getLowerBound() +"");
				iterationsMax.setText( plot.getDomainAxis().getUpperBound() +"");
				flutterMin.setText( plot.getRangeAxis().getLowerBound() +"");
				flutterMax.setText( plot.getRangeAxis().getUpperBound() +"");
			}
		});
		
		innerGB.row().cell(new JLabel("Flutter Variable")).cell(flutterMin).fillX().cell(flutterMax).fillX();
		innerGB.row().cell(new JLabel("Damping Value")).cell(iterationsMin).fillX().cell(iterationsMax).fillX().cell(replotBtn);
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

}

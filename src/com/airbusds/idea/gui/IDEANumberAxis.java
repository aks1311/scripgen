package com.airbusds.idea.gui;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.data.Range;
import org.jfree.ui.RectangleEdge;

@SuppressWarnings("serial")
public class IDEANumberAxis extends NumberAxis {
	private static Logger log = LogManager.getLogger(IDEANumberAxis.class.getName());
	
	public IDEANumberAxis(String name) {
		super(name);
	}
	
	
	protected void selectHorizontalAutoTickUnit(Graphics2D g2,
			Rectangle2D dataArea, RectangleEdge edge) {

		double tickLabelWidth = estimateMaximumTickLabelWidth(g2, getTickUnit());

		// start with the current tick unit...
		TickUnitSource tickUnits = getStandardTickUnits();
		TickUnit unit1 = tickUnits.getCeilingTickUnit(getTickUnit());
		double unit1Width = lengthToJava2D(unit1.getSize(), dataArea, edge);

		// then extrapolate...
		double guess = (tickLabelWidth / unit1Width) * unit1.getSize();

		NumberTickUnit unit2 = (NumberTickUnit) tickUnits
				.getCeilingTickUnit(guess);
		double unit2Width = lengthToJava2D(unit2.getSize(), dataArea, edge);

		tickLabelWidth = estimateMaximumTickLabelWidth(g2, unit2);
		if (tickLabelWidth > unit2Width) {
			unit2 = (NumberTickUnit) tickUnits.getLargerTickUnit(unit2);
		}

		setTickUnit(unit2, false, false);

	}
	
	@Override
	public double lengthToJava2D(double length, Rectangle2D area, RectangleEdge edge) {
		
//		BigDecimal zero = valueToJava2D1(0.0, area, edge);
//		BigDecimal l = valueToJava2D1(length, area, edge);
//		double res = Math.abs(l.subtract(zero).doubleValue());
		
		double zero = valueToJava2D(0.0, area, edge);
		double l = valueToJava2D(length, area, edge);
		double res = Math.abs(l - zero);
		
		if(length!=0d && res==0){
			//res = 1;
			log.info("lengthToJava2D() override occured. Input was "+length);
		}
		System.err.println("IsInverted = "+isInverted()+"  -- Bottom? "+RectangleEdge.isTopOrBottom(edge)+"  -- "+area.getMaxX());
		System.err.println(getRange().getLowerBound()+" -- "+getRange().getUpperBound() + " -- " + length + " -- "+ area + " -- "+ edge+" -- "+res);
		return res;
	}
	
	@Override
	public double valueToJava2D(double value, Rectangle2D area,
			RectangleEdge edge) {

		Range range = getRange();
		double axisMin = range.getLowerBound();
		double axisMax = range.getUpperBound();

		double min = 0.0;
		double max = 0.0;
		if (RectangleEdge.isTopOrBottom(edge)) {
			min = area.getX();
			max = area.getMaxX();
		} else if (RectangleEdge.isLeftOrRight(edge)) {
			max = area.getMinY();
			min = area.getMaxY();
		}
		double res = 0;
		if (isInverted()) {
			res = max - ((value - axisMin) / (axisMax - axisMin))
					* (max - min);
		} else {
			res = min + ((value - axisMin) / (axisMax - axisMin))
					* (max - min);
		}
		
//		System.err.println("Result is >> "+value+" -- "+res);
		return res;

	}


	public BigDecimal valueToJava2D1(double value, Rectangle2D area,
			RectangleEdge edge) {
	
		BigDecimal val = new BigDecimal(value, MathContext.DECIMAL64);
		
		Range range = getRange();
		BigDecimal axisMin = new BigDecimal(range.getLowerBound(), MathContext.DECIMAL64);
		BigDecimal axisMax = new BigDecimal(range.getUpperBound(), MathContext.DECIMAL64);
	
		BigDecimal min = new BigDecimal(0.0, MathContext.DECIMAL64);
		BigDecimal max = new BigDecimal(0.0, MathContext.DECIMAL64);
		if (RectangleEdge.isTopOrBottom(edge)) {
			min = new BigDecimal(area.getX(), MathContext.DECIMAL64);
			max = new BigDecimal(area.getMaxX(), MathContext.DECIMAL64);
		} else if (RectangleEdge.isLeftOrRight(edge)) {
			max = new BigDecimal(area.getMinY(), MathContext.DECIMAL64);
			min = new BigDecimal(area.getMaxY(), MathContext.DECIMAL64);
		}
		BigDecimal res = new BigDecimal(0, MathContext.DECIMAL64);
		if (isInverted()) {
			res = max.subtract( val.subtract(axisMin).divide(axisMax.subtract(axisMin)).multiply(max.subtract(min)) );
		} else {
			res = min.add( val.subtract(axisMin).divide(axisMax.subtract(axisMin)).multiply(max.subtract(min)) );
		}
		
		System.err.println("Result is >> "+value+" -- "+res);
		return res;
	}
}

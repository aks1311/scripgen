package com.airbusds.idea.gui;

import org.jfree.chart.axis.NumberTickUnitSource;
import org.jfree.chart.axis.TickUnit;

@SuppressWarnings("serial")
public class VgTickUnitSource extends NumberTickUnitSource {
	@Override
	public TickUnit getCeilingTickUnit(double size) {
		System.err.println(" >>> "+size);
		return super.getCeilingTickUnit(size);
	}
}

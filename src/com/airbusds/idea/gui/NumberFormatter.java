package com.airbusds.idea.gui;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NumberFormatter {
	
	List<DecimalFormat> intFormats = new ArrayList<DecimalFormat>();
	List<DecimalFormat> floatFormats = new ArrayList<DecimalFormat>();
	
	public NumberFormatter() {
		initIntFormats(new String[]{"######"});
		initFloatFormats(new String[]{"#####0.00", "00.###E00"});
	}
	
	
    private void initIntFormats(String[] formats){
    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setInfinity("Inf");
		
		for (String format : formats) {
			DecimalFormat decFormat = new DecimalFormat(format, symbols);
			decFormat.setParseIntegerOnly(true);
			decFormat.setDecimalSeparatorAlwaysShown(false);
			this.intFormats.add(decFormat);
		}
		
    }
    
    private void initFloatFormats(String[] formats){
    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		symbols.setInfinity("Inf");
		
		for (String format : formats) {
			DecimalFormat decFormat = new DecimalFormat(format, symbols);
			decFormat.setMaximumFractionDigits(0);
			decFormat.setMinimumIntegerDigits(2);
			this.floatFormats.add(decFormat);
		}
		
    }
	
	public Integer formatToInt(String text){
		
		Number result = null;
		ParsePosition pos = new ParsePosition(0);
		int maxPos = pos.getErrorIndex();
		for (DecimalFormat format : intFormats) {
			Number temp = format.parse(text, pos);
			if (maxPos < pos.getIndex() && pos.getErrorIndex()<0){
				maxPos = pos.getIndex();
				result = temp;
			}
		}

		if (maxPos != text.length()) {
			return null;
		}

		return result.intValue();
	
	}
	
	public Float formatToFloat(String text){
		Number result = null;
		ParsePosition pos = new ParsePosition(0);
		int maxPos = pos.getErrorIndex();
		for (DecimalFormat format : floatFormats) {
			Number temp = format.parse(text, pos);
			if (maxPos < pos.getIndex() && pos.getErrorIndex()<0){
				maxPos = pos.getIndex();
				result = temp;
			}
		}

		if (maxPos != text.length()) {
			return null;
		}

		return result.floatValue();
	}
	

	
}

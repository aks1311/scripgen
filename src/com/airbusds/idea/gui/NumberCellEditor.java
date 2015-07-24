package com.airbusds.idea.gui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class NumberCellEditor extends DefaultCellEditor {
	
	private static Logger log = LogManager.getLogger(NumberCellEditor.class.getName());
	
	public static int INT = 0;
	public static int FLOAT = 1;
	
	FormattedNumberField editor;
	
//	NumberConfig numberConfig;
//	String[] formats;
	
//	public NumberCellEditor() {
//		super(new JFormattedTextField());
//		
//		numberConfig = new NumberConfig();
//		numberConfig.maxFractionDigits = 2;
//		numberConfig.minFractionDigits = 2;
//		numberConfig.minIntDigits = 1;
//	}
//
//	public NumberCellEditor(NumberConfig numberConfig) {
//		super(new JFormattedTextField());
//		this.numberConfig = numberConfig;
//	}

//	public NumberCellEditor(String[] formats) {
//		super(new FormattedNumberField(formats));
////		this.formats = formats;
//	}

	
	public NumberCellEditor(int format) {
		super(new FormattedNumberField((format==INT?FormattedNumberField.INTEGER:FormattedNumberField.FLOAT)));
//		this.formats = formats;
	}

	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		editor = (FormattedNumberField) super
				.getTableCellEditorComponent(table, value, isSelected, row,
						column);

		editor.setBorder(null);
		
//		if (value instanceof Number) {
//			Locale myLocale = Locale.getDefault();
//
//			DecimalFormatSymbols symbols = new DecimalFormatSymbols(myLocale);
//	    	symbols.setInfinity("Inf");
//			
//			NumberFormat numberFormatB = NumberFormat.getInstance(myLocale);
//			
//			if(numberFormatB instanceof DecimalFormat)
//				((DecimalFormat)numberFormatB).setDecimalFormatSymbols(symbols);
//			
//			numberFormatB.setMaximumFractionDigits(numberConfig.maxFractionDigits);
//			numberFormatB.setMinimumFractionDigits(numberConfig.minFractionDigits);
//			numberFormatB.setMinimumIntegerDigits(numberConfig.minIntDigits);
//
//			editor.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
//					new NumberFormatter(numberFormatB)));

			editor.setHorizontalAlignment(SwingConstants.LEFT);
			if(StringUtils.hasText((String)value))
				editor.setText((String)value);
//		}
//		log.trace("Editor is >>"+editor.hashCode());
		return editor;
	}

	@Override
	public boolean stopCellEditing() {
		
//		log.trace("Editor is <<"+editor.hashCode());
		if(editor.getInputVerifier().verify(editor))
			return super.stopCellEditing();
		else
			return false;
		
//		try {
//			// try to get the value
//			this.getCellEditorValue();
//			return super.stopCellEditing();
//		} catch (Exception ex) {
//			return false;
//		}

	}

	@Override
	public Object getCellEditorValue() {
		// get content of textField
		String str = (String) super.getCellEditorValue();
		if (str == null) {
			return null;
		}

		if (str.length() == 0) {
			return null;
		}

//		if(!editor.getInputVerifier().verify(editor))
//			throw new RuntimeException("Parsing error");
		
		return str;
		
		// try to parse a number
//		try {
//			ParsePosition pos = new ParsePosition(0);
//			Number n = NumberFormat.getInstance().parse(str, pos);
//			if (pos.getIndex() != str.length()) {
//				throw new ParseException("parsing incomplete", pos.getIndex());
//			}
//
//			// return an instance of column class
//			if(numberConfig.minFractionDigits < 1)
//				return new Integer(n.intValue());
//			else
//				return new Float(n.floatValue());
//
//		} catch (ParseException pex) {
//			throw new RuntimeException(pex);
//		}
	}

	
	public static class NumberConfig{
		
		NumberConfig(){}
		NumberConfig(int minInt, int minFract, int maxFract){
			minIntDigits = minInt;
			minFractionDigits = minFract;
			maxFractionDigits = maxFract;
		}
		public int minIntDigits;
		public int minFractionDigits;
		public int maxFractionDigits;
	}
	

}

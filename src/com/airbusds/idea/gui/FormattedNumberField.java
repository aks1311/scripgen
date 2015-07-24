package com.airbusds.idea.gui;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.PopupFactory;

import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class FormattedNumberField extends JTextField {

	public static final int INTEGER = 1;
	public static final int FLOAT = 2;
	
	private String value;
	private int thisFormat;
	
//	ArrayList<DecimalFormat> formats = new ArrayList<DecimalFormat>();
	
	public FormattedNumberField( int format )
    {
		thisFormat = format;
		 setInputVerifier(new TextFieldVerifier());
//		if(format==INTEGER){
//			initIntFormats(new String[]{"######"});
//		}
//		
//		if(format==FLOAT){
//			initFloatFormats(new String[]{"#####0.00", "00.###E00"});
//		}
    }
	
	public FormattedNumberField( String[] formats, int type ){
		 setInputVerifier(new TextFieldVerifier());
//		if(type==FLOAT)
//			initFloatFormats(formats);
//		else
//			initIntFormats(formats);
	}
	
//    private void initIntFormats(String[] formats){
//    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
//		symbols.setInfinity("Inf");
//		
//		for (String format : formats) {
//			DecimalFormat decFormat = new DecimalFormat(format, symbols);
//			decFormat.setParseIntegerOnly(true);
//			decFormat.setDecimalSeparatorAlwaysShown(false);
//			this.formats.add(decFormat);
//		}
//		
//        setInputVerifier(new TextFieldVerifier());
//    }
//    
//    private void initFloatFormats(String[] formats){
//    	DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
//		symbols.setInfinity("Inf");
//		
//		for (String format : formats) {
//			DecimalFormat decFormat = new DecimalFormat(format, symbols);
//			decFormat.setMaximumFractionDigits(0);
//			decFormat.setMinimumIntegerDigits(2);
//			this.formats.add(decFormat);
//		}
//		
//        setInputVerifier(new TextFieldVerifier());
//    }

    public void showMessage(String message){
    	
    	if(!isShowing())
    		return;
    	
    	final PopupFactory popupFactory = PopupFactory.getSharedInstance();
        final JToolTip toolTip = new JToolTip();
        toolTip.setTipText(message);
        final Popup tooltipPopup = popupFactory.getPopup(this, toolTip,
                             getLocationOnScreen().x + getWidth()-10,
                             getLocationOnScreen().y-5);
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				tooltipPopup.hide();
			}
		}, 3000);
        tooltipPopup.show();
    }
    
//    public List<DecimalFormat> getFormats(){
//    	return formats;
//    }

    @Override
    public void setText(String t) {
    	if(verify(t)){
    		super.setText(t);
    		setValue(t);
    	}
    }
    
	public String getValue() {
		return value;
	}

	void setValue(String value) {
		this.value = value;
	}
	
	boolean verify(String text){
		if(!StringUtils.hasText(text))
			return true;
		
		Number number = null;
		if(thisFormat == INTEGER)
			number = new NumberFormatter().formatToInt(text);
		else
			number = new NumberFormatter().formatToFloat(text);
		
		if(number == null)
			return false;

		return true;
	}
    
    
}

class TextFieldVerifier extends InputVerifier {
	
	FormattedNumberField jtf;
	
	public boolean verify(JComponent input) {

		jtf = (FormattedNumberField) input;
//		List<DecimalFormat> formats = jtf.getFormats();

		
		String text = jtf.getText();
		return jtf.verify(text);
//		ParsePosition pos = new ParsePosition(0);
//		int maxPos = pos.getErrorIndex();
//		for (DecimalFormat format : formats) {
//			format.parse(text, pos);
//			if (maxPos < pos.getIndex())
//				maxPos = pos.getIndex();
//		}
//
//		if (maxPos != text.length()) {
//			return false;
//		}
//		Number number = new NumberFormatter().formatToInt(text);
//		if(number == null)
//			return false;
//
//		return true;
	}

	public boolean shouldYieldFocus(JComponent input) {
		if(verify(input)){
			jtf.setValue(jtf.getText());
			return true;
		}else{
			jtf.showMessage("Invalid value entered. Changes will be ignored.");
			return false;
		}
	}
}
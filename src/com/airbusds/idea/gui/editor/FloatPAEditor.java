package com.airbusds.idea.gui.editor;

import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.gui.JSTableValidator;
import com.airbusds.idea.gui.TableValidator;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Range;
import com.airbusds.idea.model.Value;

@SuppressWarnings("serial")
public class FloatPAEditor extends PAFieldEditor {

	JTextArea paramCommentJTF;
	JTextField minField;
	JTextField stepsField;
	JTextField maxField;
//	String formatString = "########";
	private ParameterValueEditorTable tableEditor;	
	
	public FloatPAEditor(Parameter param) {
		super(param);
		
		paramCommentJTF = new JTextArea();
		paramCommentJTF.setText(param.getDescription());
		paramCommentJTF.setLineWrap(true);
		paramCommentJTF.setWrapStyleWord(true);
		
		tableEditor = new ParameterValueEditorTable(new String[]{"Min","Steps","Max"}, new String[]{"float","float","float"}, false);
		
		TableValidator validator = new JSTableValidator("parseFloat({1})<parseFloat({3})#{1} should be less than {3};{1}!=null#{1} is mandatory;{2}!=null#{2} is mandatory;{3}!=null#{3} is mandatory;");
		tableEditor.setValidator(validator);
		
		List<Range> ranges = param.getParameterAnalysis().getPaValueRange();
		if(ranges!=null && !ranges.isEmpty()){
			String[][] data = new String[ranges.size()][3];
			
			Iterator<Range> it = ranges.iterator();
			int cnt = 0;
			while (it.hasNext()) {
				Range range = it.next();
				data[cnt][0] = range.getMinValue()!=null?(String)range.getMinValue().getValue():"";
				data[cnt][1] = range.getSteps()!=null?(String)range.getSteps().getValue():"";
				data[cnt][2] = range.getMaxValue()!=null?(String)range.getMaxValue().getValue():"";
				cnt++;
			}
			tableEditor.setData(data);
		}
		
		String paramTitle = Utilities.getParamDisplayName(param);
		
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		gbl.row().cell(new JLabel("Parameter Comments"))
					.cellXRemainder(paramCommentJTF).fillX();
		
		gbl.row().cell(new JLabel(paramTitle))
					.cell(tableEditor).fillX();
		
		gbl.doneAndPushEverythingToTop();
	}

	@Override
	public void update() {
		
		if(tableEditor.validateData().isError)
			throw new RuntimeException("Table data validation error.");
		
		param.setDescription( paramCommentJTF.getText() );
		
		Object[][] data = tableEditor.getData();
		
		param.getParameterAnalysis().getPaValueRange().clear();
		for (int i = 0; i < data.length; i++) {
			Object[] range = data[i];
			param.getParameterAnalysis()
					.getPaValueRange()
					.add(
							new Range(Value.valueFactory(range[0]), Value.valueFactory(range[2]), Value.valueFactory(range[1]))
						);	
		}
		
	}

//	private MaskFormatter createFormatter(String s) {
//	    MaskFormatter formatter = null;
//	    try {
//	        formatter = new MaskFormatter(s);
//	    } catch (java.text.ParseException exc) {
//	        System.err.println("formatter is bad: " + exc.getMessage());
//	        System.exit(-1);
//	    }
//	    return formatter;
//	}
	
}

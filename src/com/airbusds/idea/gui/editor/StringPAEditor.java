package com.airbusds.idea.gui.editor;

import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;

@SuppressWarnings("serial")
public class StringPAEditor extends PAFieldEditor {

	JTextArea paramCommentJTF;
	ParameterValueEditorTable tableEditor;

	
	public StringPAEditor(Parameter param) {
		super(param);
		
		
		paramCommentJTF = new JTextArea();
		paramCommentJTF.setText(param.getDescription());
		paramCommentJTF.setLineWrap(true);
		paramCommentJTF.setWrapStyleWord(true);

		String paramTitle = Utilities.getParamDisplayName(param);
		
		tableEditor = new ParameterValueEditorTable(new String[]{paramTitle}, new String[]{"string"}, false);
		List<Value> values = param.getParameterAnalysis().getPaValueList();
		if(values!=null && !values.isEmpty()){
			String[][] data = new String[values.size()][];
			
			Iterator<Value> it = values.iterator();
			int cnt = 0;
			while (it.hasNext()) {
				Value value = it.next();
				data[cnt] = new String[1];
				data[cnt][0] = (String)value.getValue();
				cnt++;
			}
			tableEditor.setData(data);
		}
		
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		gbl.row().cell(new JLabel("Parameter Comments"))
					.cellXRemainder(paramCommentJTF).fillX();
		
		gbl.row().cell(new JLabel(paramTitle))
					.cell(tableEditor).fillX();
		
		gbl.doneAndPushEverythingToTop();	

	}

	@Override
	public void update() {
		
		param.setDescription( paramCommentJTF.getText() );
		
		Object[][] data = tableEditor.getData();
		
		param.getParameterAnalysis().getPaValueList().clear();
		for (int i = 0; i < data.length; i++) {
			Object[] value = data[i];
			if(value[0]!=null){
				param.getParameterAnalysis()
					.getPaValueList().add(Value.valueFactory(value[0]));
			}
		}

	}

}

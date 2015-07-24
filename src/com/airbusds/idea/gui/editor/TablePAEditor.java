package com.airbusds.idea.gui.editor;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.config.InputFileConfig.IdValue;
import com.airbusds.idea.gui.JSTableValidator;
import com.airbusds.idea.gui.TableValidator;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class TablePAEditor extends PAFieldEditor {

	private JTextArea paramCommentJTF;
	private ParameterValueEditorTable tableEditor;
	private TableListEditorTable tableOfTables;
	
	public TablePAEditor(Parameter param) {
		super(param);
		
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		paramCommentJTF = new JTextArea(1,10);
		paramCommentJTF.setLineWrap(true);
		paramCommentJTF.setWrapStyleWord(true);
		paramCommentJTF.setText(param.getDescription());
		
		gbl.row().cell(new JLabel("Parameter Comments"))
					.cell(new JScrollPane(paramCommentJTF)).fillXY();
		String paramTitle = Utilities.getParamDisplayName(param);

		if(param.isSingleRow()){
			
			tableEditor = new ParameterValueEditorTable(param.getColumnNames(), param.getColumnTypes(), false);
			if( StringUtils.hasText(param.getValidationRules()) ){
				TableValidator validator = new JSTableValidator(param.getValidationRules());
				tableEditor.setValidator(validator);
			}
			
			Value[][] valueData = param.getParameterAnalysis().getPaRowList();
			if(valueData==null)
				valueData = new Value[][]{};
			
			Object[][] tableData = new Object[valueData.length][];
			
			for (int i = 0; i < valueData.length; i++) {
				Value[] row = valueData[i];
				tableData[i] = new Object[row.length];
				
				for (int j = 0; j < row.length; j++) {
					Value value = row[j];
					if(value!=null)
						tableData[i][j] = value.getValue();
				}
			}
			tableEditor.setData(tableData);			
			
			gbl.row().cell(new JLabel(paramTitle))
						.cell(tableEditor).fillX();
		}else{
			
			tableOfTables = new TableListEditorTable(param);
			gbl.row().cell(new JLabel(paramTitle)).cell(tableOfTables).fillX();
				
		}
		
		gbl.doneAndPushEverythingToTop();
		
	}


	@Override
	public void update() {
		
		param.setDescription( paramCommentJTF.getText() );
		
		if(param.isSingleRow()){
			if(tableEditor.validateData().isError)
				throw new RuntimeException("Table data validation error.");
			
			Object[][] data = tableEditor.getData();
			String[] colTypes = param.getColumnTypes();
	        Value[][] newData = new Value[data.length][];
			param.getParameterAnalysis().setPaRowList(newData);
			
			for (int i = 0; i < data.length; i++) {
				Object[] rec = data[i];
				Value[] newRec = new Value[rec.length];
				newData[i] = newRec;
				
				for (int j = 0; j < rec.length; j++) {
					Object object = rec[j];
					if(object==null)
						continue;
					String type = colTypes[j];
					
					if(type.equals("int")){
						newRec[j] = Value.valueFactory(object);
					}else if(type.equals("float")){
						newRec[j] = Value.valueFactory(object);
					}else{
						if(object instanceof IdValue)
							object = ((IdValue)object).id;
						newRec[j] = Value.valueFactory(object);
					}
				}
			}		
		}else{
			param.getParameterAnalysis().getPaTableList().clear();
			param.getParameterAnalysis().getPaTableList().addAll( tableOfTables.getData() );
		}
		
	}
	
}

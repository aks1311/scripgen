package com.airbusds.idea.gui.editor;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.painlessgridbag.PainlessGridBag;

import com.airbusds.Utilities;
import com.airbusds.idea.config.InputFileConfig;
import com.airbusds.idea.config.InputFileConfig.IdValue;
import com.airbusds.idea.gui.FormattedNumberField;
import com.airbusds.idea.gui.JSTableValidator;
import com.airbusds.idea.gui.TableValidator;
import com.airbusds.idea.gui.TableValidator.ValidationResult;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.model.Value;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class ParameterEditPanel extends EditorPanel {
	
	private Parameter _param;
	private JTextArea paramCommentJTF;
	private JTextField value;
	private JComboBox<IdValue> valueCB;
	private ParameterValueEditorTable tableEditor;
	
	public ParameterEditPanel(Parameter param){
		_param = param;
		init();
	}
	
	public void update(){
		_param.setDescription( paramCommentJTF.getText() );
		
		if(_param.getIsTabularData()){ // tabular data
			
			if(tableEditor.validateData().isError)
				throw new RuntimeException("Table data validation error.");
			
			Object[][] data = tableEditor.getData();
			String[] colTypes = _param.getColumnTypes();
	        Value[][] newData = new Value[data.length][];
			_param.setTabularData(newData);
			
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
			
			String paramType = _param.getDataType();
			if(paramType.equals("list")){
				IdValue val = (IdValue)valueCB.getSelectedItem();
				if(val!=null)
					_param.setValue(Value.valueFactory(val.id));
			}else{
				if(!_param.isReadOnly()){
					
					if( StringUtils.hasText(_param.getValidationRules()) ){
						TableValidator validator = new JSTableValidator(_param.getValidationRules());
						ValidationResult result= validator.validateTableData(new Object[][]{new Object[]{value.getText()}}, new String[]{Utilities.getParamDisplayName(_param)});
						if(result.isError){
							JOptionPane.showMessageDialog(this.getParent(), result.message);
							throw new RuntimeException("Table data validation error.");
						}
					}
					
					_param.setValue(Value.valueFactory(value.getText()));
					
				}
			}
			
		}
	}
	
	private void init(){
		PainlessGridBag gbl = new PainlessGridBag(this, false);
		
		paramCommentJTF = new JTextArea(1,10);
		paramCommentJTF.setLineWrap(true);
		paramCommentJTF.setWrapStyleWord(true);
		paramCommentJTF.setText(_param.getDescription());
		
		gbl.row().cell(new JLabel("Parameter Comments"))
					.cell(new JScrollPane(paramCommentJTF)).fillXY();
		String paramTitle = Utilities.getParamDisplayName(_param);
		
		if(!_param.getIsTabularData()){
			
			String paramType = _param.getDataType();
			if(paramType.equals("list")){
				
				List<IdValue> list = InputFileConfig.getList(_param.getListName());
				valueCB = new JComboBox(list.toArray());
				if(_param.getValue()!=null)
					valueCB.setSelectedItem(new IdValue(_param.getValue().getValue().toString(), ""));
				gbl.row().cell(new JLabel(paramTitle))
							.cell(valueCB);
			}else{
				if(paramType.equals("int")){
					value = new FormattedNumberField(FormattedNumberField.INTEGER);
				}else if(paramType.equals("float")){
					value = new FormattedNumberField(FormattedNumberField.FLOAT);
				}else{ // string and file
					value = new JTextField();
				}
				value.setEnabled(!_param.isReadOnly());
				value.setEditable(!_param.isReadOnly());
				
				if(_param.getValue()!=null)
					value.setText(_param.getValue().getValue().toString());
				
				gbl.row().cell(new JLabel(paramTitle))
							.cell(value).fillX();
				
			}
			
		}else{
			tableEditor = new ParameterValueEditorTable(_param.getColumnNames(), _param.getColumnTypes(), _param.isSingleRow());
			if( StringUtils.hasText(_param.getValidationRules()) ){
				TableValidator validator = new JSTableValidator(_param.getValidationRules());
				tableEditor.setValidator(validator);
			}
			
			Value[][] valueData = _param.getTabularData();
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
			
		}
		gbl.doneAndPushEverythingToTop();
		
	}
	
//	private int getSubParamCount(){
//		if(_param.getSubParams()==null)
//			return 0;
//		return _param.getSubParams().length;
//	}
	
}

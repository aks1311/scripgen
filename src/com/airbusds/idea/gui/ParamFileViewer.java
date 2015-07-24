package com.airbusds.idea.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import com.airbusds.Utilities;
import com.airbusds.idea.model.Document;
import com.airbusds.idea.model.InputFile;
import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.model.validation.DocumentValidator;
import com.airbusds.idea.model.validation.ModelInValidator;
import com.airbusds.idea.model.validation.ParamInValidator;
import com.airbusds.idea.model.validation.ValidationResults;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class ParamFileViewer extends DocumentViewer implements ActionListener {
	
	private ParagraphView selectedParagraphView;
	private InputFile _input;
	private Document _doc;
	
	public ParamFileViewer(InputFile input) {
		_input = input;
		_doc = input.getDocument();
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.add(new JLabel("  "+_doc.getTitle()+"  "));
        toolBar.add(Box.createHorizontalGlue());
        
        JButton button = new JButton();
        button.setIcon(Utilities.createImageIcon("images/valid24.png"));
        button.setToolTipText("Validate Document");
        button.setActionCommand("VALIDATE");
        button.addActionListener(this);
        toolBar.add(button);
		
        setColumnHeaderView(toolBar);
		
		List<Paragraph> paras = _doc.getParagraphs();
		Collections.sort(paras, new Comparator<Paragraph>() {
			@Override public int compare(Paragraph p1, Paragraph p2) {
				return p1.getSequence()-p2.getSequence();
			}
		});
		
		for (Iterator<Paragraph> it = paras.iterator(); it.hasNext();) {
			Paragraph para = it.next();
			ParagraphView panel = createView(para);
			addParagraph(panel);
		}
		
	}
	
	
	public void insertParagraph(Paragraph para){
		int currentLocation = 0;
		if(getSelectedParagraphView()!=null){
			currentLocation = getSelectedParagraphView().getParagraph().getSequence();
		}
		_doc.addParagraph(currentLocation, para);
		ParagraphView newParagraphView = createView(para);
		addParagraph(newParagraphView, currentLocation);
		newParagraphView.select();
	}
	
	public ParagraphView getSelectedParagraphView() {
		return selectedParagraphView;
	}


	public void setSelectedParagraphView(ParagraphView selectedParagraphView) {
		this.selectedParagraphView = selectedParagraphView;
	}


	@Override
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		
		switch(action){
			case "VALIDATE":
				DocumentValidator validator = _input.getName().equals("param.in")?new ParamInValidator():new ModelInValidator();
				ValidationResults results = validator.validate(_input);
				if(!results.getErrors().isEmpty()){
					String message = StringUtils.collectionToString(results.getErrors(), "\n");
					JOptionPane.showMessageDialog(this, message,"Input File",JOptionPane.ERROR_MESSAGE);
					_input.setValidated(false);
				}else if(!results.getWarnings().isEmpty()){
					String message = StringUtils.collectionToString(results.getWarnings(), "\n");
					JOptionPane.showMessageDialog(this, message,"Input File",JOptionPane.INFORMATION_MESSAGE);
					_input.setValidated(true);
				}else{
					JOptionPane.showMessageDialog(this, "Validation completed","Input File",JOptionPane.INFORMATION_MESSAGE);
					_input.setValidated(true);
				}
				break;
			default:
		}
		
	}


	private ParagraphView createView(final Paragraph para){
		ParagraphView view = null;
		
		SelectionListener listener = new SelectionListener() {
			
			@Override
			public void onSelect(ParagraphView newView) {
				if(editor!=null){
					editor.editParagraph(para);
				}
				ParagraphView prevPara = selectedParagraphView;
				selectedParagraphView = newView;
				if(prevPara!=null){
					prevPara.unSelect();
				}
			}
			
		};
		
		if(para.getIsComment()){
			view = new CommentsView(para);
		}else if(para.getIsBlankLine()){
			view = new SpacerView(para);
		}else if(para.getIsParameter()){
			view = new ParameterView(para);
		}
		
		view.addSelectionListener(listener);
		
		return view;
	}
	
}

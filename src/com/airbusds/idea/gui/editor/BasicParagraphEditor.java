package com.airbusds.idea.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import com.airbusds.Utilities;
import com.airbusds.idea.gui.DocumentViewController;
import com.airbusds.idea.model.Paragraph;

@SuppressWarnings("serial")
public class BasicParagraphEditor extends JPanel implements ParagraphEditor, ActionListener {

	Paragraph para;
	DocumentViewController viewController;
	BorderLayout bl = new BorderLayout();
	EditorPanel editorPanel;
	
	JButton deleteButton; 
	JButton commentButton; 
	JButton lineButton; 
	JToggleButton paButton;
	JButton resetButton;
	JButton submitButton;
	
	public BasicParagraphEditor(DocumentViewController viewController) {
		setLayout(bl);
		
		this.viewController = viewController;
		
		JToolBar toolBar = new JToolBar();
        addButtons(toolBar);
        toolBar.setFloatable(false);
        toolBar.setRollover(true);
		
        // Form will replace this
        JTextArea textArea = new JTextArea(5, 30);
        textArea.setText("Select a parameter to edit");
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
		
        add(toolBar, BorderLayout.PAGE_START);
        add(scrollPane, BorderLayout.CENTER);
	}
	
	
	@Override
	public void editParagraph(Paragraph para) {
		if(this.para!=null){
			editorPanel.update();
			onUpdate();
		}
		this.para = para;
		createGUI();
	}
	
	
	/* (non-Javadoc)
	 * @see com.airbusds.idea.gui.ParagraphEditor#onUpdate()
	 */
	public void onUpdate() {
		
	}


	public Paragraph getParagraph(){
		return para;
	}


	@Override
	public void actionPerformed(ActionEvent ae) {
		String command = ae.getActionCommand();
		switch(command){
			case "PREVIOUS":
				if(para!=null && para.getSequence()>0){
					viewController.selectPara(para.getSequence()-1);
				}
				break;
			case "NEXT":
				if(para!=null)
					viewController.selectPara(para.getSequence()+1);
				else
					viewController.selectPara(0);
				break;
			case "COMMENT":
				Paragraph comment = new Paragraph();
				comment.setComment(true);
				viewController.insertPara(comment);
				break;
			case "LINE":
				Paragraph newLine = new Paragraph();
				newLine.setBlankLine(true);
				viewController.insertPara(newLine);
				break;
			case "DELETE":
				viewController.deletePara(para.getSequence());
				break;
			case "PA":
				JToggleButton bt = (JToggleButton)ae.getSource();
				para.getParameter().setPAEnabled(bt.isSelected());
				editParagraph(para);
				break;
			case "UPDATE":
				editParagraph(para);
				viewController.reloadPara(para.getSequence());
				break;
			case "RESET":
				createGUI();
				break;
			default:
		
		}
		
	}


	/**
	 * Editor factory and basic UI generator.
	 * 
	 * */
	private void createGUI(){
		Component cmp = bl.getLayoutComponent(BorderLayout.CENTER);
		if(cmp!=null){
			bl.removeLayoutComponent(cmp);
			remove(cmp); // TODO 3 Re-factor this
		}

		deleteButton.setEnabled(true);
		paButton.setEnabled(false);
		resetButton.setEnabled(false);
		submitButton.setEnabled(false);
		
		if(para.getIsParameter()){
			
			deleteButton.setEnabled(false);
			resetButton.setEnabled(true);
			submitButton.setEnabled(true);
			if( !para.getParameter().isReadOnly() && para.getParameter().isPAAllowed())
				paButton.setEnabled(true);
			paButton.setSelected(para.getParameter().isPAEnabled());
			
			if(para.getParameter().isPAEnabled()){
				editorPanel = new ParameterAnalysisEditPanel(para.getParameter());
			}else{
				editorPanel = new ParameterEditPanel(para.getParameter());
			}
			
			
		}else if(para.getIsComment()){
			resetButton.setEnabled(true);
			submitButton.setEnabled(true);
			editorPanel = new CommentsEditPanel(para);
		}else{
			editorPanel = new SpacerEditPanel(para);
		}
		
		add(new JScrollPane((JPanel)editorPanel), BorderLayout.CENTER);
		validate();
	}
	
	private void addButtons(JToolBar toolBar) {
        JButton button = null;
        
        button = new JButton(Utilities.createImageIcon("images/Back24.gif"));
        button.setActionCommand("PREVIOUS");
        button.addActionListener(this);
        button.setToolTipText("Edit Previous Paragraph");
        toolBar.add(button);

        button = new JButton(Utilities.createImageIcon("images/Forward24.gif"));
        button.setActionCommand("NEXT");
        button.addActionListener(this);
        button.setToolTipText("Edit Next Paragraph");
        toolBar.add(button);

        toolBar.addSeparator();
        
        deleteButton = new JButton(Utilities.createImageIcon("images/trash-24.png"));
        deleteButton.setActionCommand("DELETE");
        deleteButton.addActionListener(this);
        deleteButton.setToolTipText("Delete Paragraph");
        deleteButton.setEnabled(false);
        toolBar.add(deleteButton);
        
        commentButton = new JButton(Utilities.createImageIcon("images/comment-24.png"));
        commentButton.setActionCommand("COMMENT");
        commentButton.addActionListener(this);
        commentButton.setToolTipText("Insert Comment");
        commentButton.setEnabled(true);
        toolBar.add(commentButton);
        
        lineButton = new JButton(Utilities.createImageIcon("images/newline-24.png"));
        lineButton.setActionCommand("LINE");
        lineButton.addActionListener(this);
        lineButton.setToolTipText("Insert blank line");
        lineButton.setEnabled(true);
        toolBar.add(lineButton);
        
        toolBar.addSeparator();
        
        submitButton = new JButton(Utilities.createImageIcon("images/submit-24.png"));
        submitButton.setActionCommand("UPDATE");
        submitButton.addActionListener(this);
        submitButton.setToolTipText("Check and update");
        submitButton.setEnabled(false);
        toolBar.add(submitButton);

        resetButton = new JButton(Utilities.createImageIcon("images/reset-24.png"));
        resetButton.setActionCommand("RESET");
        resetButton.addActionListener(this);
        resetButton.setToolTipText("Reset Values");
        resetButton.setEnabled(false);
        toolBar.add(resetButton);
        
        toolBar.addSeparator();
        
        paButton = new JToggleButton(Utilities.createImageIcon("images/chart-24.png"));
        paButton.setActionCommand("PA");
        paButton.addActionListener(this);
        paButton.setToolTipText("Enable Parameter Analysis");
        paButton.setEnabled(false);
        toolBar.add(paButton);
        
	}

}

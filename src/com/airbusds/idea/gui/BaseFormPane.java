package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.airbusds.Utilities;
import com.airbusds.idea.utilities.StringUtils;

public class BaseFormPane extends JScrollPane {

	private static final long serialVersionUID = 2474934660099914316L;
	protected final static int TEXT_FIELD_SMALL = 10;
	protected final static int TEXT_FIELD_MEDIUM = 20;
	protected final static int TEXT_FIELD_LARGE = 30;

	protected final Map<String, String> commentMap = new HashMap<String, String>();
	protected JPanel panel;
	
	public BaseFormPane() {
		super(new JPanel());
		panel = (JPanel)getViewport().getView();
//		panel.setBackground(Color.WHITE);
		getVerticalScrollBar().setUnitIncrement(16);
		getHorizontalScrollBar().setUnitIncrement(16);
	}
	
	public JPanel getPanel(){
		return panel;
	}
	
	protected void createHelpTexts(JTextField textField, String in_message, String toolTip){
        if( StringUtils.hasText(in_message)){
			TextPrompt tp = new TextPrompt(in_message, textField);
	        tp.setForeground( Color.BLUE );
	        tp.changeAlpha(0.5f);
        }
        if(StringUtils.hasText(toolTip)){
        	textField.setToolTipText(toolTip);
        }
	}
	
	
	protected void createCommentButton(final JPanel panel, int trow, final String src){
		JTextField comment = new JTextField();
		comment.setVisible(false);
		
		final ImageIcon commentIcon = Utilities.createImageIcon("images/comment-icon.gif");
		final ImageIcon commentIconFilled = Utilities.createImageIcon("images/comment-icon-filled.gif");
        final JButton commentButton = new JButton(commentIcon);
        panel.add(commentButton
		        , new GridBagConstraints(4, trow, 1, 1, 0.0, 0.0
		        , GridBagConstraints.WEST, GridBagConstraints.NONE
		        , new Insets(0, 0, 0, 0), 0, 0));
        panel.add(comment
		        , new GridBagConstraints(4, trow, 1, 1, 0.0, 0.0
		        , GridBagConstraints.WEST, GridBagConstraints.NONE
		        , new Insets(0, 0, 0, 0), 0, 0));

        commentButton.setActionCommand(src);
        commentButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String s = (String)JOptionPane.showInputDialog(panel, 
												"Enter Comments", 
												"Comments", 
												JOptionPane.PLAIN_MESSAGE, 
												null, null,
												commentButton.getToolTipText()
										);
				
				commentButton.setIcon(commentIcon);
			    if ((s != null) && (s.length() > 0)) {
			        commentButton.setToolTipText(s);
			        commentMap.put(src, s);
			        commentButton.setIcon(commentIconFilled);
			        return;
			    }
			}
		});
	}
	
	protected String getDisplayStringFromList(List<String> list, int i){
		String strA = "";
		if(list!=null && !list.isEmpty()){
			strA = list.get(i);
		}
		return strA;
	}
	
	protected String createStringForArray(String[][] data){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			if(i!=0)
				sb.append("&&");
			sb.append( StringUtils.arrayToString(data[i], " "));
		}
		return sb.toString();
	}
	

}

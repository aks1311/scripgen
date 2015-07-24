package com.airbusds.idea.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.painlessgridbag.PainlessGridBag;
import org.painlessgridbag.PainlessGridbagConfiguration;

import com.airbusds.idea.model.Paragraph;
import com.airbusds.idea.utilities.StringUtils;


@SuppressWarnings("serial")
public class CommentsView extends ParagraphView {
	
	String comments;
	JLabel commentsJL;
	
	public CommentsView(Paragraph paragraph){
		super(paragraph);
	}
	
	public void setComment(String comment){
		this.comments = comment;
	}
	
	public String getComments(){
		return comments;
	}
	
	public void init(){
		if(getParagraph().getIsComment())
			this.comments = getParagraph().getComments();
		
        PainlessGridbagConfiguration config = new PainlessGridbagConfiguration();
        config.setVirticalSpacing(0);
        config.setFirstRowTopSpacing(0);
        config.setLastRowBottomSpacing(1);
		PainlessGridBag gb = new PainlessGridBag(getBodyPanel(), config, false);
		
		commentsJL = new JLabel("###");
		commentsJL.setForeground(new Color(706600));
		commentsJL.setFont(new Font(commentsJL.getFont().getName(), Font.ITALIC, commentsJL.getFont().getSize()));
		
		gb.row().cellXRemainder(commentsJL);
		
		JPanel pnlPadding = new JPanel();
        pnlPadding.setPreferredSize(new Dimension(1, 1));
        pnlPadding.setMinimumSize(new Dimension(1, 1));
        pnlPadding.setOpaque(false);
        gb.row().cellXRemainder(pnlPadding).fillXY();
		gb.done();
	}

	public void resetData() {
		commentsJL.setText(StringUtils.getHTMLFormattedComment(getParagraph().getComments(), "###", true));
		commentsJL.updateUI();
	}
	
}

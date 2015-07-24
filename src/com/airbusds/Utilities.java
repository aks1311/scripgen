package com.airbusds;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.model.Parameter;
import com.airbusds.idea.utilities.StringUtils;

/**
 * Contains static utility methods for the IDEA GUI.
 * 
 * @author amit.singh
 *
 */
public class Utilities {
	private static Logger log = LogManager.getLogger(Utilities.class.getName());

	/**
	 * Draws the the component in the center of the screen. 
	 * 
	 * @param component UI component to be drawn
	 */
	public static void centerComponentOnScreen(Component component) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension d = toolkit.getScreenSize();

		Point p = new Point();
		p.x += ((d.width - component.getWidth()) / 2);
		p.y += ((d.height - component.getHeight()) / 2);

		if (p.x < 0) {
			p.x = 0;
		}

		if (p.y < 0) {
			p.y = 0;
		}

		component.setLocation(p);
	}

	/**
	 * Create and returns <code>ImageIcon</code> object for the image at the location <code>path</code>
	 * @param path path of the Image
	 * @return ImageIcon object
	 */
	public static ImageIcon createImageIcon(String path) {
		java.net.URL imgURL = ClassLoader.getSystemResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL);
		} else {
			log.error("Couldn't find file: " + path);
			return null;
		}
	}
	
	public static String getParamDisplayName(Parameter param){
		return (StringUtils.hasText(param.getTitle()))?param.getTitle():param.getName();
	}
	
	public static void scrollToBottom(final JComponent component) {
        
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                		Rectangle visibleRect = component.getVisibleRect();
                        visibleRect.y = component.getHeight() - visibleRect.height;
                        component.scrollRectToVisible(visibleRect);
                    }
                });
            }
        });
	}
	
	public static BufferedImage createImage(JPanel panel) {

	    int w = panel.getWidth();
	    int h = panel.getHeight();
	    BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	    Graphics2D g = bi.createGraphics();
	    panel.paint(g);
	    return bi;
	}
	
	public static boolean promptYesNoQuestion(String ques){
		int res = JOptionPane.showConfirmDialog((JFrame)IDEAContext.getInstance().get(IDEAContext.KEY_WINDOW_MAIN),
			    ques,
			    "IDEA GUI",
			    JOptionPane.YES_NO_OPTION);
		
		return JOptionPane.YES_OPTION==res?true:false;
	}
	
	public static void showMessage(String message, String title){
		JOptionPane.showMessageDialog((JFrame)IDEAContext.getInstance().get(IDEAContext.KEY_WINDOW_MAIN), message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Prompts the question to the user with the initial value. Blanks are allowed. 
	 * 
	 * @param question Message describing the user input
	 * @param value Initial value shown in the input
	 * @return text
	 */
	public static String promptQuestion(String question, String value){
		String response = JOptionPane.showInputDialog( (Component)IDEAContext.getInstance().get(IDEAContext.KEY_WINDOW_MAIN), question, value);
		return response!=null?response:value;
	}

}
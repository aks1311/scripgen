package com.airbusds.idea;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.Utilities;
import com.airbusds.idea.config.IDEAConfig;
import com.airbusds.idea.gui.MainWindow;
import com.airbusds.idea.job.IDEAQueue;
import com.airbusds.idea.utilities.StringUtils;

public class IDEAMain {

	private static Logger log = LogManager.getLogger(IDEAMain.class.getName());

	// TODO 3 Symbolic links for external input files
	// TODO 3 Replace JSON with XML
	// TODO 2 Dirty field alerts
	// TODO 3 Re-sizable logger
	// TODO 3 File as relative paths
	// TODO 1 Input Files to become read-only once plan is created. If edited. Plans are all recreated.
	// TODO 2 JavaDocs
	// TODO 1 User manual
	
	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				
				if (!ApplicationInstanceManager.registerInstance()) {
					// instance already running.
					log.info("Another instance of this application is already running.  Exiting.");
					System.exit(0);
				}

				IDEAConfig config = IDEAConfig.getInstance();
				if( args.length>0 && StringUtils.hasText(args[0]) ){
					config.setUpConfig(args[0]);
				}
				
				final MainWindow mainWindow = createAndShowGUI();
				
				ApplicationInstanceManager.setApplicationInstanceListener(new ApplicationInstanceListener() {
							public void newInstanceCreated() {
								mainWindow.setVisible(true);
								mainWindow.setExtendedState(JFrame.NORMAL);
							}
						});
				
			}
		});
	}

	private static MainWindow createAndShowGUI() {
		
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
		    public void uncaughtException(Thread t, Throwable e) {
		        log.debug("ERROR::",e);
//		        log.error("An Error has occured: "+e.getMessage());
		    }
		});
		
		final MainWindow main = new MainWindow();
		IDEAContext.getInstance().set(IDEAContext.KEY_WINDOW_MAIN, main);
		
		final Image image = Utilities.createImageIcon("images/idea.png").getImage();
		
		main.setVisible(true);
		
		main.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		main.addWindowListener(new WindowAdapter() {
		    
			@Override
			public void windowOpened(WindowEvent e) {
				log.info("IDEA GUI started.");
				super.windowOpened(e);
			}
			
			public void windowClosing(WindowEvent e) {
				
		    	IDEAQueue q = IDEAQueue.getInstance();
				if(q.isRunning()){
					log.info("IDEA Processes are still running, so IDEA GUI will minimize and try to run in background. "
							+ "IDEA GUI can be opened to track them later.");
					if (SystemTray.isSupported()) {
						
						final SystemTray tray = SystemTray.getSystemTray();
						PopupMenu popup = new PopupMenu();
						final TrayIcon trayIcon = new TrayIcon(image, "IDEA", popup);
						trayIcon.setImageAutoSize(true);
						
						
						MenuItem defaultItem = new MenuItem("Open");
						defaultItem.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								main.setVisible(true);
								main.setExtendedState(JFrame.NORMAL);
								tray.remove(trayIcon);
							}
						});
						popup.add(defaultItem);
						try {
							tray.add(trayIcon);
							main.setVisible(false);
							log.debug("Minimized to SystemTray");
						} catch (AWTException e1) {
							e1.printStackTrace();
						}
						
					}else{
						log.info("There are IDEA processes in the queue. They will keep running in the background but the "
								+ "IDEA GUI cannot track them.");
					}
				}else{
					main.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					WindowEvent wev = new WindowEvent(main, WindowEvent.WINDOW_CLOSING);
	                Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
	                log.info("Closing the IDEA GUI");
				}
			
		    }
			
		});
		
		return main;
	}

}

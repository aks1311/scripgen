package com.airbusds.idea.gui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.airbusds.Utilities;
import com.airbusds.gui.common.AdvancedFileChooser;
import com.airbusds.idea.IDEAContext;
import com.airbusds.idea.JSONSerializer;
import com.airbusds.idea.gui.dialog.NewAnalysisWizard;
import com.airbusds.idea.gui.editor.AnalysisEditor;
import com.airbusds.idea.job.LogFileReader;
import com.airbusds.idea.manager.AnalysisManager;
import com.airbusds.idea.manager.ConfigManager;
import com.airbusds.idea.manager.DocumentManager;
import com.airbusds.idea.model.AnalysisInfo;
import com.airbusds.idea.utilities.StringUtils;

@SuppressWarnings("serial")
public class MainWindow extends JFrame implements ActionListener, ItemListener {

	private static Logger log = LogManager.getLogger(MainWindow.class.getName());
	
	private JPanel blPanel;
	private JScrollPane statusBar;
	private AnalysisInfo _info;
	private AnalysisEditor editor;
	
	public MainWindow() {
		super("IDEA GUI"); // TODO 1 Names should come from configuration
		
//		log.trace("Trace working");
//		log.debug("Debug working");
//		log.info("Info working");
//		log.warn("Warn working");
//		log.error("Error working");
		
		setUpLookAndFeel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		setJMenuBar(createMenuBar());

		// Display the window.
		setSize(1024, 768);
		setVisible(true);
		
		blPanel = new JPanel();
		setContentPane(blPanel);
		blPanel.setLayout(new BorderLayout());
		
		Utilities.centerComponentOnScreen(this);
		
		Image image = Utilities.createImageIcon("images/idea.png").getImage();
		setIconImage(image);
		
	}
	
	public void showLogger(){
		final JTextArea logs = new JTextArea(5,1);
		logs.setEditable(false);
		
		statusBar = new JScrollPane(logs);
		JToolBar toolbar = new JToolBar();
		toolbar.add(new JLabel("Console"));
		toolbar.add(new JSeparator());
		toolbar.setFloatable(false);
		
		toolbar.add(Box.createHorizontalGlue());

//		JButton closeBtn = new JButton("X");
//		closeBtn.addActionListener(new ActionListener() {
//			@Override public void actionPerformed(ActionEvent e) {
//				hideLogger();
//			}
//		});
//		toolbar.add(closeBtn);
		
		statusBar.setColumnHeaderView(toolbar);
		
		blPanel.add(statusBar, BorderLayout.SOUTH);
		blPanel.updateUI();
		
		File file = new File("idea-log/idea-gui.log");
		LogFileReader reader = new LogFileReader(file) {
			
			@Override
			public void onNewLine(String line) {
				logs.append(line+"\n");
				Utilities.scrollToBottom(logs);
			}
			
			@Override
			public boolean stopReading() {
				if (statusBar==null) {
					return true;
				}else{
					return false;
				}
			}
			
		};
		reader.start();
	}
	
	public void hideLogger(){
		blPanel.remove(statusBar);
		statusBar = null;
		blPanel.updateUI();
	}
	
//	public void showMessage(String message) {
//		statusBar.setForeground(Color.BLUE);
//		statusBar.append(message);
////		resetMessage(5000);
//	}
//
//	public void showError(String message) {
//		statusBar.setForeground(Color.RED);
//		statusBar.append(message);
////		resetMessage(5000);
//	}
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
	
		switch (action) {
	
		case "new.analysis":
			NewAnalysisWizard wizard = new NewAnalysisWizard(this) {
				@Override
				public void onFinish(AnalysisInfo info) {
					super.onFinish(info);
					_info = info;
					DocumentManager docMgr = new DocumentManager();
					docMgr.createDocument(info.getParamIn());
					docMgr.createDocument(info.getModelIn());
					
					IDEAContext.getInstance().setAnalysisInfo(_info);
					
					// Load Forms for editing the files and configuration for
					// the Analysis
					showEditPanels(_info);
				}
			};
	
			wizard.show();
			break;
	
		case "open.analysis": {
			JFileChooser fc = new AdvancedFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileNameExtensionFilter(
					"Analysis metadata files", "iac"));
			int returnVal = fc.showOpenDialog(this);
	
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				AnalysisManager asysMgr = new AnalysisManager();
				_info = asysMgr.readAnalysis(file);
				_info.setAnalysisLocation(file.getParentFile().getPath());
				
				IDEAContext.getInstance().setAnalysisInfo(_info);
				
				DocumentManager docMgr = new DocumentManager();
				docMgr.fixParameterReferences(_info);
	
				ConfigManager confMgr = new ConfigManager();
				confMgr.updateConfigInInputFile(_info.getParamIn(),
						ConfigManager.PARAM_IN);
				confMgr.updateConfigInInputFile(_info.getModelIn(),
						ConfigManager.MODEL_IN);
	
				showEditPanels(_info);
			}
		}
			break;
	
		case "save": {
			File filePath = null;
			if (!StringUtils.hasText(_info.getName())
					|| !StringUtils.hasText(_info.getAnalysisLocation())) {
				JFileChooser fc = new AdvancedFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new FileNameExtensionFilter(
						"Analysis metadata files", "iac"));
				int returnVal = fc.showSaveDialog(this);
	
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					filePath = fc.getSelectedFile();
					_info.setAnalysisLocation(filePath.getParent());
					_info.setName(filePath.getName());
					// showEditPanels(info);
				}
			} 
//			else {
//				filePath = new File(_info.getAnalysisLocation() 
//						+ File.separatorChar
//						+ _info.getName() + ".iac");
//			}
	
			new AnalysisManager().saveAnalysis(_info);
			log.info("Analysis saved successfully");
			
		}
			break;
	
		case "saveas": {
			File filePath = null;
			JFileChooser fc = new AdvancedFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new FileNameExtensionFilter(
					"Analysis metadata files", "iac"));
			int returnVal = fc.showSaveDialog(this);
	
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				filePath = fc.getSelectedFile();
	
				String analysisName = filePath.getName();
				if (analysisName.lastIndexOf(".iac") > -1) {
					analysisName = analysisName.substring(0,
							analysisName.lastIndexOf(".iac"));
				}
				_info.setName(analysisName);
				_info.setAnalysisLocation(filePath.getParent());
	
				filePath = new File(filePath.getParent() + File.separatorChar + analysisName
						+ ".iac");
	
				JSONSerializer serializer = new JSONSerializer();
				serializer.serializeToFile(filePath, _info);
				
				log.info("Analysis saved as " + filePath);
	
				closeAction();
				showEditPanels(_info);
			}
		}
			break;
	
		case "close":
			log.info("Closing Analysis: "+_info.getName());
			closeAction();
			break;
	
		case "exit":
			WindowEvent wev = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
            Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
			break;
			
		case "exit.force":
			System.exit(1);
			break;
	
//		case "help":
//			Utilities.showMessage("IDEA GUI user manual shared sepera", "IDEA Help");
//			break;
	
		case "about":
			Utilities.showMessage("IDEA GUI\nVersion 0.9", "About IDEA");
			break;
	
		default:
		}
	
	}

	public void itemStateChanged(ItemEvent e) {
		JCheckBoxMenuItem cbItem = (JCheckBoxMenuItem)e.getItem();
		String action = cbItem.getActionCommand();
		
		switch(action){
		case "view.console":{
			if(e.getStateChange() == ItemEvent.SELECTED){
				showLogger();
			}else{
				hideLogger();
			}
			break;
		}
		
		default:
		}
		
    }
	
	private void setUpLookAndFeel(){
		try {
			for (LookAndFeelInfo info : UIManager
					.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
			
		} catch (Exception e) {
			// If Nimbus is not available, you can set the GUI to
			// another look and feel.
			 try {
				UIManager.setLookAndFeel("com.seaglasslookandfeel.SeaGlassLookAndFeel");
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException
					| UnsupportedLookAndFeelException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	/**
	 * Creates and returns the menu bar with menu buttons added
	 * 
	 * @return menubar
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(menu);

		// Submenu for New
		submenu = new JMenu("New");
		submenu.setMnemonic(KeyEvent.VK_N);

		menuItem = new JMenuItem("Analysis");
		submenu.add(menuItem);
		menuItem.setActionCommand("new.analysis");
		menuItem.addActionListener(this);

		menu.add(submenu);

		menuItem = new JMenuItem("Open", KeyEvent.VK_O);
		menuItem.setMnemonic(KeyEvent.VK_O);
		menuItem.setActionCommand("open.analysis");
		menu.add(menuItem);
		menuItem.addActionListener(this);

		menu.add(new JSeparator());
		menuItem = new JMenuItem("Close", KeyEvent.VK_C);
		menuItem.setActionCommand("close");
		menuItem.addActionListener(this);
		menuItem.setMnemonic(KeyEvent.VK_C);
		menu.add(menuItem);

		menu.add(new JSeparator());
		menuItem = new JMenuItem("Save", KeyEvent.VK_S);
		menuItem.setActionCommand("save");
		menuItem.addActionListener(this);
		menuItem.setMnemonic(KeyEvent.VK_S);
		menu.add(menuItem);

		menuItem = new JMenuItem("Save As..", KeyEvent.VK_S);
		menuItem.setActionCommand("saveas");
		menuItem.addActionListener(this);
		menuItem.setMnemonic(KeyEvent.VK_S);
		menu.add(menuItem);

		menu.addSeparator();

		menuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		menuItem.setMnemonic(KeyEvent.VK_X);
		menuItem.setActionCommand("exit");
		menuItem.addActionListener(this);
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Forced Exit", KeyEvent.VK_T);
		menuItem.setMnemonic(KeyEvent.VK_T);
		menuItem.setActionCommand("exit.force");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menu = new JMenu("View");
		menu.setMnemonic(KeyEvent.VK_V);
		menuItem = new JCheckBoxMenuItem("Console");
		menuItem.setActionCommand("view.console");
		menuItem.addItemListener(this);
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_H);

//		menuItem = new JMenuItem("Help Contents", KeyEvent.VK_C);
//		menuItem.setActionCommand("help");
//		menuItem.addActionListener(this);
//		menu.add(menuItem);
//		menu.addSeparator();

		menuItem = new JMenuItem("About", KeyEvent.VK_A);
		menuItem.setActionCommand("about");
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuBar.add(menu);

		return menuBar;
	}

	private void showEditPanels(AnalysisInfo info) {
		log.info("Editing the Analysis: "+info.getName());
		editor = new AnalysisEditor(info);
		IDEAContext.getInstance().set(IDEAContext.KEY_ASYS_EDITOR, editor);
		blPanel.add(editor, BorderLayout.CENTER);

		validate();
	}
	
//	public void resetMessage(long ... delay) {
//		long dely = delay.length>0?delay[0]:10;
//		
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				statusBar.setForeground(Color.DARK_GRAY);
//				statusBar.setText("Welcome to IDEA GUI!");
//			}
//		}, dely);
//	}

	private void closeAction() {
		BorderLayout bl = (BorderLayout) blPanel.getLayout();
		if (bl.getLayoutComponent(BorderLayout.CENTER) != null) {
			blPanel.remove(bl.getLayoutComponent(BorderLayout.CENTER));
			repaint();
		}
	}

	
}

package com.airbusds.idea.gui;

import java.awt.Font;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class creates and displays a window containing a TextArea, in which the
 * contents of a text file are displayed.
 **/
@SuppressWarnings("serial")
public class FileViewer extends JFrame {
	String directory; // The default directory to display in the FileDialog
	JTextArea textarea; // The area to display the file contents into

	public FileViewer() {
		this(null, null);
	}

	public FileViewer(String filename) {
		this(null, filename);
	}

	/**
	 * The real constructor. Create a FileViewer object to display the specified
	 * file from the specified directory
	 **/
	public FileViewer(String directory, String filename) {
		super();

		// Create a TextArea to display the contents of the file in
		textarea = new JTextArea("", 24, 80);
		textarea.setFont(new Font("MonoSpaced", Font.PLAIN, 12));
		textarea.setEditable(false);
		this.add("Center", new JScrollPane(textarea));

		this.pack();

		// Figure out the directory, from filename or current dir, if necessary
		if (directory == null) {
			File f;
			if ((filename != null) && (f = new File(filename)).isAbsolute()) {
				directory = f.getParent();
				filename = f.getName();
			} else
				directory = System.getProperty("user.dir");
		}

		this.directory = directory; // Remember the directory, for FileDialog
		setFile(directory, filename); // Now load and display the file
	}

	/**
	 * Load and display the specified file (if any) from the specified directory
	 **/
	public void setFile(String directory, String filename) {
		if ((filename == null) || (filename.length() == 0))
			return;
		File f;
		FileReader in = null;
		// Read and display the file contents. Since we're reading text, we
		// use a FileReader instead of a FileInputStream.
		try {
			f = new File(directory, filename); // Create a file object
			in = new FileReader(f); // Create a char stream to read it
			int size = (int) f.length(); // Check file size
			char[] data = new char[size]; // Allocate an array big enough for it
			int chars_read = 0; // How many chars read so far?
			while (chars_read < size)
				// Loop until we've read it all
				chars_read += in.read(data, chars_read, size - chars_read);
			textarea.setText(new String(data)); // Display chars in TextArea
			this.setTitle("FileViewer: " + filename); // Set the window title
		}
		// Display messages if something goes wrong
		catch (IOException e) {
			textarea.setText(e.getClass().getName() + ": " + e.getMessage());
			this.setTitle("FileViewer: " + filename + ": I/O Exception");
		}
		// Always be sure to close the input stream!
		finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
		}
	}


	/**
	 * The FileViewer can be used by other classes, or it can be used standalone
	 * with this main() method.
	 **/
//	static public void main(String[] args) throws IOException {
//		JFrame f = new FileViewer((args.length == 1) ? args[0] : null);
//		f.addWindowListener(new WindowAdapter() {
//			public void windowClosed(WindowEvent e) {
//				System.exit(0);
//			}
//		});
//		f.setVisible(true);
//	}
}
package com.vint.pdf2ppt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UserInterface {

	JFrame frame;
	private JTextField pdfFile;
	private JTextField pptFile;
	private final JProgressBar progressBar = new JProgressBar();
	private final JButton selectPDF = new JButton("Select PDF File");
	private final JButton convertToPPT = new JButton("Convert To PPT");

	private final AtomicBoolean preparing = new AtomicBoolean(false);
	private 

	/**
	 * Create the application.
	 */
	public UserInterface() {
		initialize();
		setListeners();
	}

	private void setListeners() {
		selectPDF.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				if (chooser.showDialog(frame, "Select") == JFileChooser.APPROVE_OPTION) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							pdfFile.setText(chooser.getSelectedFile().getAbsolutePath());
							pptFile.setText(chooser.getSelectedFile().getAbsolutePath().replace(".pdf", ".ppt"));
						}
					});
					if (preparing.compareAndSet(false, true)) {

					}
				}
			}
		});
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 637, 413);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 601, 353);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblPdfFile = new JLabel("PDF File");
		lblPdfFile.setBounds(56, 130, 54, 14);
		panel.add(lblPdfFile);

		pdfFile = new JTextField();
		pdfFile.setBounds(179, 127, 189, 20);
		panel.add(pdfFile);
		pdfFile.setColumns(10);

		progressBar.setBounds(107, 281, 317, 20);
		panel.add(progressBar);

		selectPDF.setBounds(405, 126, 129, 23);
		panel.add(selectPDF);

		JLabel label = new JLabel("PPT File Output");
		label.setBounds(56, 174, 113, 14);
		panel.add(label);

		pptFile = new JTextField();
		pptFile.setColumns(10);
		pptFile.setBounds(179, 171, 189, 20);
		panel.add(pptFile);

		convertToPPT.setBounds(338, 232, 129, 23);
		panel.add(convertToPPT);

		JLabel lblSelectThePdf = new JLabel("Select the PDF File to covert then specify the output file.");
		lblSelectThePdf.setBounds(62, 11, 347, 36);
		panel.add(lblSelectThePdf);

		JLabel lblWhenDonePress = new JLabel(" When done press Convert to PDF.");
		lblWhenDonePress.setBounds(104, 46, 200, 14);
		panel.add(lblWhenDonePress);

		progressBar.setVisible(false);
	}
}

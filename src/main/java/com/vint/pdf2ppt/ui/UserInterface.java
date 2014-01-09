package com.vint.pdf2ppt.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.vint.pdf2ppt.core.ConversionCallback;
import com.vint.pdf2ppt.core.ConversionProcessor;
import com.vint.pdf2ppt.core.PdfToPpt;
import com.vint.pdf2ppt.core.ProcessorFactory;

public class UserInterface {

	JFrame frame;
	private JTextField pdfFile;
	private JTextField pptFile;
	private final JProgressBar progressBar = new JProgressBar();
	private final JButton selectPDF = new JButton("Select PDF File");
	private final JButton convertToPPT = new JButton("Convert To PPT");
	private final JLabel lblThereAre = new JLabel();
	private final String infoLabelText = "There are # pages to convert.  \"Press Convert To PPT\" when ready";

	private final ProcessorFactory factory = new PdfToPpt();

	private final AtomicBoolean running = new AtomicBoolean(false);
	private volatile ConversionProcessor processor;

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
					final String pdf = chooser.getSelectedFile().getAbsolutePath();
					final String ppt = chooser.getSelectedFile().getAbsolutePath().replace(".pdf", ".ppt");
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							pdfFile.setText(pdf);
							pptFile.setText(ppt);
						}
					});
					try {
						processor = factory.prepare(new File(pdf), new File(ppt));
					} catch (IOException e1) {
						e1.printStackTrace();
						throw new RuntimeException(e1);
					}
					configureProcessor();
				}
			}
		});
	}

	private void configureProcessor() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressBar.setString("0 of " + processor.getPageCount() + " pages converted");
				progressBar.setStringPainted(true);
				progressBar.setVisible(true);
				progressBar.setMinimum(1);
				progressBar.setMaximum(processor.getPageCount());
				lblThereAre.setText(infoLabelText.replace("#", "" + processor.getPageCount()));
				lblThereAre.setVisible(true);
			}
		});
		progressBar.setStringPainted(true);
		processor.setConversionCallback(new ConversionCallback() {

			@Override
			public void onStart() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						convertToPPT.setEnabled(false);
						lblThereAre.setText("The PPT is being built");
					}
				});
			}

			@Override
			public void onPageProcessed(final int page) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						progressBar.setValue(page);
						progressBar.setString(page + " of " + processor.getPageCount() + " pages converted");

					}
				});
			}

			@Override
			public void onPagesProcessed() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						lblThereAre.setText("All pages have been converted.  Now building the PPT");
						progressBar.setValue(processor.getPageCount());
						progressBar.setVisible(false);
					}
				});
			}

			@Override
			public void onCompletion() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						lblThereAre.setText("The PPT has been created");
						convertToPPT.setEnabled(true);
					}
				});
				processor = null;
				running.set(false);
			}
		});
		convertToPPT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (running.compareAndSet(false, true)) {
					processor.execute();
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

		progressBar.setBounds(106, 310, 361, 20);
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
		lblSelectThePdf.setBounds(132, 34, 347, 36);
		panel.add(lblSelectThePdf);

		lblThereAre.setBounds(106, 284, 405, 14);
		panel.add(lblThereAre);

		progressBar.setVisible(false);
	}
}

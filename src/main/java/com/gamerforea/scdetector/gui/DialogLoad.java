package com.gamerforea.scdetector.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.common.base.Strings;

public class DialogLoad extends JDialog
{
	private static final long serialVersionUID = -7036342179299257807L;
	private final JPanel contentPanel = new JPanel();
	private JTextField input1;
	private JTextField input2;
	private JTextField sources1;
	private JTextField sources2;

	public static void initGui()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			DialogLoad dialog = new DialogLoad();
			dialog.setResizable(false);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DialogLoad()
	{
		setBounds(100, 100, 450, 330);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		JLabel label1 = new JLabel("Input 1:");
		label1.setBounds(15, 20, 39, 14);

		input1 = new JTextField();
		input1.setBounds(58, 17, 288, 20);
		input1.setColumns(10);

		JButton button1 = new JButton("Browse");
		button1.setBounds(352, 16, 67, 23);
		button1.addActionListener((event) ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("JAVA program (*.jar, *.zip)", "jar", "zip"));
			chooser.showOpenDialog(this);
			if (chooser.getSelectedFile() != null) this.input1.setText(chooser.getSelectedFile().getAbsolutePath());
		});

		JLabel label2 = new JLabel("Input 2:");
		label2.setBounds(15, 73, 39, 14);

		input2 = new JTextField();
		input2.setBounds(58, 70, 288, 20);
		input2.setColumns(10);

		JButton button2 = new JButton("Browse");
		button2.setBounds(352, 69, 67, 23);
		button2.addActionListener((event) ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("JAVA program (*.jar, *.zip)", "jar", "zip"));
			chooser.showOpenDialog(this);
			if (chooser.getSelectedFile() != null) this.input2.setText(chooser.getSelectedFile().getAbsolutePath());
		});
		contentPanel.setLayout(null);
		contentPanel.add(label1);
		contentPanel.add(input1);
		contentPanel.add(button1);
		contentPanel.add(label2);
		contentPanel.add(input2);
		contentPanel.add(button2);

		JLabel label3 = new JLabel("Src 1:");
		label3.setBounds(15, 121, 39, 14);
		contentPanel.add(label3);

		sources1 = new JTextField();
		sources1.setColumns(10);
		sources1.setBounds(58, 118, 288, 20);
		contentPanel.add(sources1);

		JButton button3 = new JButton("Browse");
		button3.setBounds(352, 117, 67, 23);
		button3.addActionListener((event) ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("JAVA sources (*.jar, *.zip)", "jar", "zip"));
			chooser.showOpenDialog(this);
			if (chooser.getSelectedFile() != null) this.sources1.setText(chooser.getSelectedFile().getAbsolutePath());
		});
		contentPanel.add(button3);

		JLabel label4 = new JLabel("Src 2:");
		label4.setBounds(15, 174, 39, 14);
		contentPanel.add(label4);

		sources2 = new JTextField();
		sources2.setColumns(10);
		sources2.setBounds(58, 171, 288, 20);
		contentPanel.add(sources2);

		JButton button4 = new JButton("Browse");
		button4.setBounds(352, 170, 67, 23);
		button4.addActionListener((event) ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("JAVA sources (*.jar, *.zip)", "jar", "zip"));
			chooser.showOpenDialog(this);
			if (chooser.getSelectedFile() != null) this.sources2.setText(chooser.getSelectedFile().getAbsolutePath());
		});
		contentPanel.add(button4);

		JLabel label5 = new JLabel("Minimal similar (%):");
		label5.setBounds(15, 233, 92, 14);
		contentPanel.add(label5);

		JSpinner minSimilar = new JSpinner();
		minSimilar.setModel(new SpinnerNumberModel(90, 1, 100, 1));
		minSimilar.setBounds(117, 230, 49, 20);
		contentPanel.add(minSimilar);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Load");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
				okButton.addActionListener((event) ->
				{
					String s1 = this.input1.getText();
					String s2 = this.input2.getText();
					String s3 = this.sources1.getText();
					String s4 = this.sources2.getText();
					int i = (Integer) minSimilar.getModel().getValue();
					if (!isNullOrEmpty(s1, s2, s3, s4) && i >= 1 && i <= 100)
					{
						this.dispose();
						MainGUI.initGui(s1, s2, s3, s4, (float) i / 100);
					}
				});
			}
			{
				JButton cancelButton = new JButton("Exit");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				cancelButton.addActionListener((event) -> this.dispose());
			}
		}
	}

	private static boolean isNullOrEmpty(String... strings)
	{
		for (String s : strings)
			if (Strings.isNullOrEmpty(s)) return true;
		return false;
	}
}
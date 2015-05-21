package com.gamerforea.scdetector.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.gamerforea.scdetector.similar.ClassContainer;
import com.gamerforea.scdetector.similar.SimilarContainer;
import com.gamerforea.scdetector.similar.SimilarContainer.GsonSimilarContainer;
import com.gamerforea.scdetector.similar.SimilarData;
import com.gamerforea.scdetector.util.SimilarUtils;
import com.gamerforea.scdetector.util.Utils;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainGUI
{
	private JFrame frame;
	private JTree classes;
	private DefaultMutableTreeNode classesNode = new DefaultMutableTreeNode("Classes");
	private JTextPane sources1;
	private JTextPane sources2;

	private List<SimilarContainer> similars;
	private DefaultMutableTreeNode selectedNode;
	private SimilarContainer selectedClass;
	private SimilarData selectedData;
	private Map<String, String> sourceMap1;
	private Map<String, String> sourceMap2;
	public float minSimilar;

	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		if (args.length == 5)
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			initGui(args[0], args[1], args[2], args[3], (float) Integer.valueOf(args[4]) / 100);
		}
		else DialogLoad.initGui();
	}

	public static void initGui(String input1, String input2, String sources1, String sources2, float minSimilar)
	{
		EventQueue.invokeLater(() ->
		{
			try
			{
				MainGUI window = new MainGUI();
				window.minSimilar = minSimilar;
				window.loadClasses(input1, input2);
				window.loadSources(sources1, sources2);
				window.frame.setVisible(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainGUI()
	{
		initialize();
	}

	private void loadClasses(String input1, String input2)
	{
		try
		{
			List<ClassContainer> list1 = Utils.getClassContainers(Utils.getClasses(input1));
			List<ClassContainer> list2 = Utils.getClassContainers(Utils.getClasses(input2));
			this.similars = SimilarUtils.getSimilars(list1, list2);
			this.similars = SimilarUtils.diffStrings(this.similars, this.minSimilar);
			this.similars = SimilarUtils.diffFieldsCount(this.similars, this.minSimilar);
			this.similars = SimilarUtils.diffMethodsCount(this.similars, this.minSimilar);

			this.similars = SimilarUtils.validateSimilars(this.similars);

			List<DefaultMutableTreeNode> nodes = new ArrayList<>(this.similars.size());
			this.similars.parallelStream().forEach((similar) ->
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(similar);
				similar.similarClasses.stream().sorted().forEach((data) -> node.add(new DefaultMutableTreeNode(data)));
				synchronized (nodes)
				{
					nodes.add(node);
				}
			});
			nodes.stream().sorted((n1, n2) -> n1.getUserObject().toString().compareTo(n2.getUserObject().toString())).forEach((node) -> this.classesNode.add(node));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadSources(String sources1, String sources2)
	{
		try
		{
			this.sourceMap1 = Utils.getSources(sources1);
			this.sourceMap2 = Utils.getSources(sources2);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 * @wbp.parser.entryPoint
	 */
	private void initialize()
	{
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		frame.getContentPane().add(splitPane);

		JPanel panel = new JPanel();
		splitPane.setRightComponent(panel);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel validator = new JPanel();
		panel.add(validator, BorderLayout.SOUTH);

		JButton validate = new JButton("Validate");
		validate.addActionListener((event) ->
		{
			if (this.selectedNode != null && this.selectedClass != null && !this.selectedClass.validated && this.selectedData != null)
			{
				TreePath path = this.classes.getSelectionPath();
				this.selectedClass.validated = true;
				this.selectedClass.similarClasses = new ArrayList<>(1);
				this.selectedClass.similarClasses.add(this.selectedData);
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode) this.selectedNode.getParent();
				parent.removeAllChildren();
				parent.add(this.selectedNode);
				@SuppressWarnings("unchecked")
				Enumeration<DefaultMutableTreeNode> iter = this.classesNode.children();
				while (iter.hasMoreElements()) // Remove selected class from other nodes
				{
					DefaultMutableTreeNode node = iter.nextElement();
					if (node != parent)
					{
						DefaultMutableTreeNode removeNode = null;
						@SuppressWarnings("unchecked")
						Enumeration<DefaultMutableTreeNode> iter1 = node.children();
						while (iter1.hasMoreElements())
						{
							DefaultMutableTreeNode node1 = iter1.nextElement();
							if (node1.getUserObject().toString().equals(this.selectedData.toString()))
							{
								removeNode = node1;
								break;
							}
						}
						if (removeNode != null) node.remove(removeNode);
					}
				}
				this.similars = SimilarUtils.validateSimilars(this.similars);
				DefaultTreeModel model = (DefaultTreeModel) this.classes.getModel();
				model.reload(); // Redraw classes tree
				this.classes.setSelectionPath(path);
			}
		});
		validator.add(validate);

		JButton save = new JButton("Save");
		save.addActionListener((event) ->
		{
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new FileNameExtensionFilter("JSON File (*.json)", "json"));
			chooser.showSaveDialog(this.frame);
			if (chooser.getSelectedFile() != null)
			{
				List<GsonSimilarContainer> list = new ArrayList<>();
				this.similars.parallelStream().forEach((s) ->
				{
					synchronized (list)
					{
						list.add(s.toGson());
					}
				});

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				try
				{
					Files.write(gson.toJson(list.stream().sorted((e1, e2) -> e1.name.compareTo(e2.name)).collect(Collectors.toList())).getBytes(), chooser.getSelectedFile());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		validator.add(save);

		JSplitPane sources = new JSplitPane();
		sources.setResizeWeight(0.5);
		panel.add(sources, BorderLayout.CENTER);

		sources1 = new JTextPane();
		sources1.setEditable(false);
		JScrollPane sources1Scroll = new JScrollPane();
		sources1Scroll.setViewportView(sources1);
		sources.setLeftComponent(sources1Scroll);

		JScrollPane sources2Scroll = new JScrollPane();
		sources2 = new JTextPane();
		sources2.setEditable(false);
		sources2Scroll.setViewportView(sources2);
		sources.setRightComponent(sources2Scroll);

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setLeftComponent(scrollPane);

		classes = new JTree(classesNode);
		classes.addTreeSelectionListener((e) ->
		{
			if (e.getPath().getLastPathComponent() instanceof DefaultMutableTreeNode)
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				if (node.getUserObject() instanceof SimilarData)
				{
					this.selectedNode = node;
					this.selectedClass = (SimilarContainer) ((DefaultMutableTreeNode) node.getParent()).getUserObject();
					this.selectedData = (SimilarData) node.getUserObject();
					this.sources1.setText(Utils.getSource(this.sourceMap1, this.selectedClass.classContainer.name));
					this.sources2.setText(Utils.getSource(this.sourceMap2, this.selectedData.classContainer.name));
				}
				else
				{
					this.selectedNode = null;
					this.selectedClass = null;
					this.selectedData = null;
					this.sources1.setText("");
					this.sources2.setText("");
				}
				this.sources1.setCaretPosition(0);
				this.sources2.setCaretPosition(0);
			}
		});
		scrollPane.setViewportView(classes);
	}
}
package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer;

/*
 * @author Hermann Wöhrmann
 *
 * Description:
 *
 * Showing the contents of an XML file based on a speed.jg.XMLDocumentTree
 *
 * Version Date       Comments
 * 1.01.01 03.11.2004 created
 *
 */

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import speed.jg.*;

import speed.util.*;

import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.event.*;
import java.io.*;

class XmlTreeViewGUI extends GUIObject {
	@SuppressWarnings("serial")
	
	protected XmlTreeViewGUI() {
		JFrame xmlTreeView = new JFrame();
		xmlTreeView.setBounds(80, 60, 640, 480);
		xmlTreeView.getContentPane().setLayout(new BorderLayout());
		//xmlTreeView.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  //removed by xfwang 10/4/2012
		xmlTreeView.setTitle("Viewing XML");
		xmlTreeView.setResizable(true);
		{
			JMenuBar menuBar = new JMenuBar();
			{
				JMenu fileMenu = new JMenu();
				fileMenu.setText("File");
				{
					JMenuItem openMenuItem = new JMenuItem();
					openMenuItem.setText("Open");
					openMenuItem.setName("openMenuItem");
					super.add(openMenuItem);
					fileMenu.add(openMenuItem);
				}
				{
					JSeparator separator1 = new JSeparator();
					separator1.setName("separator1");
					super.add(separator1);
					fileMenu.add(separator1);
				}
				{
					JMenuItem exitMenuItem = new JMenuItem();
					exitMenuItem.setText("Exit");
					exitMenuItem.setName("exitMenuItem");
					super.add(exitMenuItem);
					fileMenu.add(exitMenuItem);
				}
				fileMenu.setName("fileMenu");
				super.add(fileMenu);
				menuBar.add(fileMenu);
			}
			menuBar.setName("menuBar");
			super.add(menuBar);
			xmlTreeView.setJMenuBar(menuBar);
		}
		{
			JSplitPane splitPane = new JSplitPane();
			splitPane.setOneTouchExpandable(true);
			{
				JPanel selectionPanel = new JPanel() {
					public Dimension getPreferredSize() {
						Dimension preferred = super.getPreferredSize();
						return GUIObject.getPreferredSize(preferred,
								getParent(), "4", null, null, null, null, null);
					}
				};
				selectionPanel.setLayout(new BoxLayout(selectionPanel,
						BoxLayout.PAGE_AXIS));
				selectionPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
								Color.WHITE, Color.BLUE), "XML Tree View",
						TitledBorder.RIGHT, TitledBorder.ABOVE_BOTTOM,
						new Font("Dialog", Font.PLAIN, 12), Color.BLUE));
				{
					JTree selectionTree = new JTree();
					selectionTree.setName("selectionTree");
					super.add(selectionTree);
					JScrollPane treeScrollPane = new JScrollPane(selectionTree);
					treeScrollPane.setName("treeScrollPane");
					super.add(treeScrollPane);
					selectionPanel.add(treeScrollPane);
				}
				selectionPanel.setName("selectionPanel");
				super.add(selectionPanel);
				splitPane.setLeftComponent(selectionPanel);
			}
			{
				JPanel presentationPanel = new JPanel() {
					public Dimension getPreferredSize() {
						Dimension preferred = super.getPreferredSize();
						return GUIObject.getPreferredSize(preferred,
								getParent(), "4", null, "3", null, null, null);
					}
				};
				presentationPanel.setLayout(new BoxLayout(presentationPanel,
						BoxLayout.Y_AXIS));
				presentationPanel.setBorder(BorderFactory.createTitledBorder(
						BorderFactory.createEtchedBorder(EtchedBorder.LOWERED,
								Color.WHITE, Color.BLUE), "XML Text View",
						TitledBorder.RIGHT, TitledBorder.ABOVE_BOTTOM,
						new Font("Dialog", Font.PLAIN, 12), Color.BLUE));
				{
					JTextArea xmlTextArea = new JTextArea();
					{
						JPopupMenu popupMenu = new JPopupMenu();
						popupMenu.setBackground(Color.CYAN);
						popupMenu.setBorder(BorderFactory.createCompoundBorder(
								BorderFactory.createEtchedBorder(
										EtchedBorder.LOWERED, Color.WHITE,
										Color.BLUE), BorderFactory
										.createEmptyBorder(2, 2, 2, 2)));
						popupMenu.setBorderPainted(true);
						{
							JPanel panel = new JPanel();
							panel.setLayout(new FlowLayout(FlowLayout.CENTER,
									5, 5));
							panel.setBorder(BorderFactory
									.createCompoundBorder(BorderFactory
											.createEmptyBorder(2, 2, 2, 2),
											BorderFactory.createEtchedBorder(
													EtchedBorder.LOWERED,
													Color.WHITE, Color.BLUE)));
							{
								JLabel label = new JLabel();
								label.setForeground(Color.BLUE);
								label.setText("System Clipboard");
								label.setName("label");
								super.add(label);
								panel.add(label);
							}
							panel.setName("panel");
							super.add(panel);
							popupMenu.add(panel);
						}
						{
							JSeparator separatorP = new JSeparator();
							separatorP.setName("separatorP");
							super.add(separatorP);
							popupMenu.add(separatorP);
						}
						{
							JMenuItem copyMenuItem = new JMenuItem();
							copyMenuItem.setText("Copy");
							copyMenuItem.setName("copyMenuItem");
							super.add(copyMenuItem);
							popupMenu.add(copyMenuItem);
						}
						popupMenu.setName("popupMenu");
						super.add(popupMenu);
						new PopupMenuController(popupMenu, xmlTextArea, -60,
								-50);
					}
					xmlTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
					xmlTextArea.setEditable(false);
					xmlTextArea.setColumns(1);
					xmlTextArea.setRows(1);
					xmlTextArea.setName("xmlTextArea");
					super.add(xmlTextArea);
					JScrollPane scrollPane = new JScrollPane(xmlTextArea);
					scrollPane.setName("scrollPane");
					super.add(scrollPane);
					presentationPanel.add(scrollPane);
				}
				presentationPanel.setName("presentationPanel");
				super.add(presentationPanel);
				splitPane.setRightComponent(presentationPanel);
			}
			splitPane.setDividerLocation(200);
			splitPane.setResizeWeight(0.33);
			splitPane.setName("splitPane");
			super.add(splitPane);
			xmlTreeView.getContentPane().add(splitPane, BorderLayout.CENTER);
		}
		xmlTreeView.setName("xmlTreeView");
		super.add(xmlTreeView);
	}
}

public class XmlTreeView extends XmlTreeViewController {
	@SuppressWarnings("deprecation")
	private File in;
	public XmlTreeView(File input) throws ServiceException {
		super(new XmlTreeViewGUI(), input);
		this.in = input;
		xmlTreeView.show();
	}

	// ==============================================================================
	// XML File Chooser ...
	// ------------------------------------------------------------------------------

	private XMLDocumentTree openXMLDocument() throws Exception {
		File file = getXMLFile();
		if (file == null)
			return null;
		return new XMLDocumentTree(XMLDocument.fromFile(file.getPath())
				.getContent());
	}

	private class XMLFileFilter extends javax.swing.filechooser.FileFilter {
		public boolean accept(File file) {
			return file.getName().toLowerCase().endsWith(".xml")
					|| file.isDirectory();
		}

		public String getDescription() {
			return "XML Files ( *.xml )";
		}
	}

	private File getXMLFile() {
		JFileChooser jfc = new JFileChooser();
		jfc.setCurrentDirectory(new File("."));
		jfc.setFileFilter(new XMLFileFilter());
		jfc.removeChoosableFileFilter(jfc.getAcceptAllFileFilter());
		int result = jfc.showOpenDialog(null);
		if (result != JFileChooser.APPROVE_OPTION) {
			return null;
		} else {
			return jfc.getSelectedFile();
		}
	}

	// ==============================================================================
	// Overwritten XmlTreeViewController methods ...
	// ------------------------------------------------------------------------------

	void initialize(File in) throws ServiceException {
//		selectionTree.setModel(null);
		selectionTree.setModel(new XMLDocumentTree(XMLDocument.fromFile(in.getAbsolutePath())
				.getContent()));
		
	}

	// ==============================================================================
	// ActionListener event handling ...
	// ------------------------------------------------------------------------------

	void handleCopyMenuItemActionPerformedEvent(ActionEvent e) throws Exception {
		Clipboard clipBoard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipBoard.setContents(new StringSelection(xmlTextArea.getText()), null);
	}

	void handleExitMenuItemActionPerformedEvent(ActionEvent e) throws Exception {
		xmlTreeView.dispose(); //added by xfwang 10/4/2012
		//System.exit(0);
	}

	void handleOpenMenuItemActionPerformedEvent(ActionEvent e) throws Exception {
		selectionTree.setModel(openXMLDocument());
		xmlTextArea.setText(null);
	}

	// ==============================================================================
	// TreeSelectionListener event handling ...
	// ------------------------------------------------------------------------------

	void handleSelectionTreeValueChangedEvent(TreeSelectionEvent e)
			throws Exception {
		Object[] path = e.getPath().getPath();
		XMLElement nodeElement = (XMLElement) path[path.length - 1];
		xmlTextArea.setText(nodeElement.asXMLString());
		xmlTextArea.setCaretPosition(0);
	}

	// ==============================================================================
	// Execute this XmlTreeView Program ...
	// ------------------------------------------------------------------------------

//	public static void main(String[] args) throws ServiceException {
//		@SuppressWarnings("unused")
//		XmlTreeView xmlTreeView = new XmlTreeView(null);
//	}

}

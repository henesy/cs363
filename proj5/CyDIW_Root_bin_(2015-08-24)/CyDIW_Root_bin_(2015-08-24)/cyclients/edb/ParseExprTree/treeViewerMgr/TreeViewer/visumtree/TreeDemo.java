/*
	LICENSE
	Copyright (c) 1998 Matthias T. Kromann.

	The code in this package is licensed for commercial use and
	distribution in the VISUM project coordinated by the University of
	Copenhagen and the Copenhagen Business School. There are no
	restrictions on its use within this project and its descendants. 

	With this exception, this package is free software: You can
	redistribute it and/or modify it under the terms of the GNU
	General Public License as published by the Free Software
	Foundation. This package is distributed in the hope that it will
	be useful, but WITHOUT ANY WARRANTY or any implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
	General Public License for more details. 

	Matthias T. Kromann 
	mtkromann@ieee.org
*/

// This file was generated automatically from the corresponding
// noweb file. Please edit the noweb file, not this file. 

package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TreeDemo extends JFrame implements ActionListener, TreeListener {
	TreePanel treePanel;
	Tree tree;
	Node rootclone;
	TreeInfo infoclone; 
	JPanel buttons;
	JTextField path, value;
	int layoutStyle = 1;
	Font[] font; 
	int currentFont = 0;
	int currentTree = 0;
	TreeDemo(String title) {
		super(title);
	}
	public static void main(String[] args) {
		TreeDemo treeDemo = new TreeDemo("Tree Drawing");
		treeDemo.setSize(600,420);
		treeDemo.addTreeWindowAdapter();
		treeDemo.init();
		treeDemo.show();
	}
	void init() {
		treePanel = new TreePanel();
		tree = treePanel.tree();
		buttons = setupButtons();
		treePanel.setBorder(null);

		// Set tree listener
		tree.addTreeListener(this);

		// Set font for buttons
		buttons.setFont(new Font("Helvetica", Font.BOLD, 10));

		// Tree
		treePanel.getHorizontalScrollBar().setUnitIncrement(20);
		treePanel.getHorizontalScrollBar().setBlockIncrement(20);
		treePanel.getVerticalScrollBar().setUnitIncrement(20);
		treePanel.getVerticalScrollBar().setBlockIncrement(20);

		// Setup Tree and TreeInfo
		setupTree();

		// Component layout
		getContentPane().add(treePanel, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.WEST);

		// Fonts
		font = new Font[5];
		font[0] = new Font("Helvetica", Font.PLAIN, 10);
		font[1] = new Font("Helvetica", Font.PLAIN, 18);
		font[2] = new Font("Helvetica", Font.PLAIN, 34);
		font[3] = new Font("Helvetica", Font.PLAIN, 2);
		font[4] = new Font("Helvetica", Font.PLAIN, 8);

		// Creating clone of tree
		System.out.println("Making clone of tree");
		System.out.println("Clone-button switches between two trees");
		rootclone = (Node) tree.root().clone();
		infoclone = (TreeInfo) tree.info().clone();
	}
	JButton makeButton(JPanel panel, String name, GridBagLayout gridbag,
			GridBagConstraints c) {
		JButton button = new JButton(name);
		button.addActionListener(this);
		gridbag.setConstraints(button, c);
		panel.add(button);
		return button;
	}
	JLabel makeLabel(JPanel panel, String name, GridBagLayout gridbag,
			GridBagConstraints c) {
		JLabel label = new JLabel(name, JLabel.CENTER);
		label.setBackground(Color.blue);
		label.setForeground(Color.white);
		gridbag.setConstraints(label, c);
		panel.add(label);
		return label;
	}
	JTextField makeTextField(JPanel panel, String text, int size, GridBagLayout
			gridbag, GridBagConstraints c) {
		JTextField tfield = new JTextField(text, size);
		gridbag.setConstraints(tfield, c);
		panel.add(tfield);
		return tfield;
	}
	class TreeWindowAdapter extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	} 
	void addTreeWindowAdapter() {
		addWindowListener(new TreeWindowAdapter());
	}
	JPanel setupButtons() {
		JPanel buttons = new JPanel();
		JPanel buttons1 = new JPanel();
		JPanel buttons2 = new JPanel();

		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		buttons.setLayout(gridbag);
		buttons1.setLayout(gridbag);
		buttons2.setLayout(gridbag);

		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.REMAINDER;

		// Node buttons
		makeLabel(buttons1, "General", gridbag, c);
		makeButton(buttons1, "TreeType", gridbag, c);
		makeButton(buttons1, "Print", gridbag, c);
		makeButton(buttons1, "Quit", gridbag, c);

		makeLabel(buttons1, "Node ops", gridbag, c);
		makeButton(buttons1, "New mother", gridbag, c);
		makeButton(buttons1, "New daughter", gridbag, c);
		makeButton(buttons1, "Remove", gridbag, c);
		makeButton(buttons1, "Abbreviate", gridbag, c);
		makeButton(buttons1, "Bonsai", gridbag, c);

		makeLabel(buttons1, "Selecting", gridbag, c);
		makeButton(buttons1, "SelectAll", gridbag, c);
		makeButton(buttons1, "SelectSub", gridbag, c);
		makeButton(buttons1, "Maximals", gridbag, c);
		makeButton(buttons1, "Terminals", gridbag, c);
		makeButton(buttons1, "Minroot", gridbag, c);
		makeButton(buttons1, "PrevNode", gridbag, c);
		makeButton(buttons1, "NextNode", gridbag, c);

		// AVM buttons
		makeLabel(buttons2, "AVMs/options", gridbag, c);
		path  = makeTextField(buttons2, "path", 10, gridbag, c);
		value = makeTextField(buttons2, "value", 10, gridbag, c);
		makeButton(buttons2, "Set AV", gridbag, c);
		makeButton(buttons2, "Set Option", gridbag, c);
		makeButton(buttons2, "Set Noption", gridbag, c);
		makeButton(buttons2, "Get AV", gridbag, c);
		makeButton(buttons2, "Remove AV", gridbag, c);
		makeButton(buttons2, "Get AVM", gridbag, c);
		makeButton(buttons2, "To string", gridbag, c);

		makeLabel(buttons2, "Layout", gridbag, c);
		makeButton(buttons2, "Layout style", gridbag, c);
		makeButton(buttons2, "Font", gridbag, c);
		makeButton(buttons2, "Draw bbox", gridbag, c);
		makeButton(buttons2, "Position", gridbag, c);
		makeButton(buttons2, "Clone", gridbag, c);

		// Empty label
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1.0;
		makeLabel(buttons1, "", gridbag, c).setBackground(null);
		makeLabel(buttons2, "", gridbag, c).setBackground(null);

		// Component layout
		buttons.add(buttons1);
		buttons.add(buttons2);

		// Return buttons
		return buttons;
	}
	public void treeActionPerformed(TreeEvent e) {
		if (e.type() == TreeEvent.SELECT) {
			path.setText(tree.selectedPath());
			Object obj = tree.getAV(tree.selectedPath()); 

			if (obj instanceof String) {
				value.setText((String) obj);
			} else {
				value.setText("");
			}
		}
	}
	public void actionPerformed(ActionEvent e) {
		String action = e.getActionCommand();

		// General
		if (action == "TreeType") {
			++currentTree;
			setupTree();
			tree.deselectAll();
		} else if (action == "Print") {
			PrintJob pjob = getToolkit().getPrintJob(this, "Printing Test", null);
			if (pjob != null) {
				Graphics pg = pjob.getGraphics();
				if (pg != null) {
					// Positioning parameters
					double marginx = 0.1;	// Right/left margin (relative to paper width)
					double marginy = 0.1;	// Top/bottom margin (relative to paper width)
					double fillx = 0.5;		// Left horisontal fill (rel. to extra space)
					double filly = 0;		// Top vertical fill (rel. to extra space)

					// Get Tree page dimension
					Dimension size = pjob.getPageDimension();

					// Save old font and setup new font
					int fontsize = 10;
					Font oldfont = (Font) tree.getOption("tree.font");
					tree.setOption("tree.font", new Font("Helvetica", Font.PLAIN, fontsize));

					// Layout tree and get BBox
					tree.printlayout(pg, 0, 0);
					Rectangle bbox = tree.getBBox();

					// Scale so tree fits within margins
					double scalew = ((double) 1 - 2 * marginx) * size.width / bbox.width;
					double scaleh = ((double) 1 - 2 * marginy) * size.height / bbox.height;
					double scale = min(1, min(scalew, scaleh));

					// Calculate new fontsize and set font
					fontsize = (int) (scale * fontsize);
					if (fontsize < 1) 
						fontsize = 1;
					tree.setOption("tree.font", new Font("Helvetica", Font.PLAIN, fontsize));

					// Recalculate layout and bounding box for new font size
					tree.printlayout(pg, 0, 0);
					bbox = tree.getBBox();

					// Calculate reference point
					int xref = (int) (marginx * size.width 
						+ fillx * ((1 - 2 * marginx) * size.width - bbox.width));
					int yref = (int) (marginy * size.height
						+ filly * ((1 - 2 * marginy) * size.height - bbox.height)); 

					// Print tree
					tree.print(pg, xref, yref);
					pg.dispose();

					// Reset original font in tree and update screen 
					tree.setOption("tree.font", oldfont);
					tree.requestRepaintAll();
				}
				pjob.end();
			}
		} else if (action == "Quit") {
			System.exit(0);
		} else

		// Node operations
		if (action == "New mother") {
			tree.newMother(new Node()); 
		} else if (action == "New daughter") {
			tree.newDaughter(new Node()); 
		} else if (action == "Remove") {
			tree.remove(); 
		} else if (action == "Abbreviate") {
			tree.abbreviate();
		} else if (action == "Bonsai") {
			tree.bonsai();
		} 

		// Selecting nodes
		if (action == "SelectAll") {
			tree.selectAll();
		} else if (action == "SelectSub") {
			tree.select(tree.subnodes());
		} else if (action == "Maximals") {
			tree.select(tree.maximals());
		} else if (action == "Terminals") {
			tree.select(tree.terminals());
		} else if (action == "Minroot") {
			Node minroot = tree.minroot();
			tree.select(minroot, null);
			if (minroot == tree.root())
				System.out.println("minroot returned root node");
		} else if (action == "PrevNode") {
			Node sel = tree.selectedAt(0);
			if (sel != null) 
				tree.select(sel.prev(), null);
		} else if (action == "NextNode") {
			Node sel = tree.selectedAt(0);
			if (sel != null) 
				tree.select(sel.next(), null);
		} 

			// AVMs and options
		if (action == "Set AV") {
			tree.setAV(path.getText(), value.getText()); 
		} else if (action == "Set Option" || action == "Set Noption") { 
			Object v = value.getText();
			if (((String) v).equals("TRUE")) 
				v = Boolean.TRUE;
			else if (((String) v).equals("FALSE")) 
				v = Boolean.FALSE;
				
			System.out.print("Set option " + path.getText() + " = " 
				+ v + " (" + v.getClass().getName() + ")");

			// Set option
			if (action == "Set Option") {
				tree.setOption(path.getText(), v);
				System.out.println("");
			} else {
				Node[] nodes = tree.selected();
				for (int i = 0; i < nodes.length; ++i) 
					nodes[i].setOption("@@node." + path.getText(), v);
				System.out.println(" in selected nodes");
			}
		} else if (action == "Get AV") {
			System.out.println(path.getText() + "=" + tree.getAV(path.getText())); 
		} else if (action == "Remove AV") {
			tree.removeAV(path.getText());
		} else if (action == "Get AVM") {
			String[][] AVMstring = tree.getAVM();
			if (AVMstring != null) 
				for (int i = 0; i < AVMstring.length; ++i) 
					System.out.println(AVMstring[i][0] + "=" + AVMstring[i][1]);
		} else if (action == "To string") {
			Node[] nodes = tree.selected();
			for (int i = 0; i < nodes.length; ++i)
				System.out.println("" + nodes[i].number + ": " 
					+ nodes[i].avm().toString());
		} 

			// Layout
		if (action == "Layout style") {
			layoutStyle = (layoutStyle + 1) % 2;
			tree.setOption("tree.layoutStyle", new Integer(layoutStyle));
		} else if (action == "Font") {
			currentFont = (currentFont + 1) % font.length;
			tree.setOption("tree.font", font[currentFont]);
		} else if (action == "Draw bbox") {
			Rectangle b = tree.getBBox();
			tree.getGraphics().drawRect(b.x, b.y, b.width, b.height);
		} else if (action == "Position") {
			treePanel.scrollSelectedToVisible();
		} else if (action == "Clone") {
			Node tmproot = tree.root();
			TreeInfo tmpinfo = tree.info();
			tree.setRoot(rootclone);
			tree.setInfo(infoclone);
			rootclone = tmproot;
			infoclone = tmpinfo;
		}
	}

	double min(double x, double y) {
		return (x < y) ? x : y;
	}
	void setupTree() {
		// Remove all nodes from tree
		tree.resetNodes();
		tree.resetInfo();

		// Common TreeInfo settings
		tree.setOption("tree.layoutStyle", new Integer(layoutStyle));
		tree.setOption("avm.tooltip", "${} is ${${}}");
		tree.setOption("macro.selectAV.attrBG", Color.yellow);
		tree.setOption("macro.selectAV.valBG", Color.yellow);
		tree.setOption("macro.selectNode.nodeBG", Color.cyan);

		// Initialize new tree
		switch ((currentTree = currentTree % 5)) {
			// Free tree
			case 0: 
				FREEtree();
				setTitle("TreeDemo: free tree");
				break;

			// HPSG style tree
			case 1: 
				HPSGtree();
				setTitle("TreeDemo: HPSG tree");
				break;

			// IESA style tree
			case 2:
				IESAtree();
				setTitle("TreeDemo: IESA tree");
				break;

			// Chomsky style tree
			case 3:
				CHOMtree();
				setTitle("TreeDemo: Chomsky-like tree");
				break;

			// Dependency tree
			case 4:
				DEPtree();
				setTitle("TreeDemo: dependency tree");
				break;
		}
	}
	void FREEtree() {
		// TreeInfo settings
		tree.setOption("node.abbrMacro", "${lex}");

		// Nonsense nested AVMs with many values
		String avm1 = "cat=np | syn.gen=neut | syn.case=akk | syn.func=s | " + 
			"sem.op=lege1a | sem.arg1=matthias | sem.arg2=matthias| ref=2200";
		String avm2 = "cat=np | ref=2200 | tooltip=point here | " + 
			"@tooltip.tooltip=Surprise! Special tooltip for tooltip";
		String avm3 = "syn.gen=neut | syn.case=akk | syn.func=s | " + 
			"sem.op=lege1a | sem.arg1=matthias";

		// Insert nodes in tree
		Node rootNode = tree.root(), daughterNode;
		rootNode.newDaughter(new Node("lex=1|" + avm1));
		rootNode.newDaughter(daughterNode = new Node("lex=2|" + avm2));
			daughterNode.newDaughter(new Node("lex=3|" + avm2));
			daughterNode.newDaughter(new Node("lex=4|" + avm1));
			daughterNode.newDaughter(new Node("lex=5|" + avm3));
		rootNode.newDaughter(new Node("lex=6|" + avm3));
	}
	void HPSGtree() {
		// TreeInfo settings
		tree.setOption("node.abbrMacro", "${cat}");
		tree.setOption("node.PERMndaughter", Boolean.FALSE);
		tree.setOption("node.PERMterminal", Boolean.FALSE);

		// TreeInfo macro for terminal nodes, overriding default
		tree.setOption("macro.terminal.abbrMacro", "${lex}");
		tree.setOption("macro.terminal.PERMchange", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMremove", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreceive", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreorder", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMterminal", Boolean.TRUE);
		tree.setOption("macro.terminal.PERMabbreviate", Boolean.FALSE);
		tree.setOption("macro.terminal.abbreviate", Boolean.TRUE);

		// Insert nodes in tree
		Node Snode, NPnode, ARTnode, Nnode, VPnode, Vnode, APnode, Anode;
		String term = "@@node.:terminal=true";
		String agr = "agr.num=sg|agr.gen=com";

		tree.root().newDaughter(Snode = new Node("cat=s"));
		Snode.newDaughter(NPnode = new Node("cat=np|" + agr));
			NPnode.newDaughter(ARTnode = new Node("cat=art|" + agr));
				ARTnode.newDaughter(new Node("lex=en|" + term));
			NPnode.newDaughter(Nnode = new Node("cat=n|" + agr));
				Nnode.newDaughter(new Node("lex=skovtur|" + term));
		Snode.newDaughter(VPnode = new Node("cat=vp|" + agr));
			VPnode.newDaughter(Vnode = new Node("cat=v|subcat=ap"));
				Vnode.newDaughter(new Node("lex=bliver|" + term));
			VPnode.newDaughter(APnode = new Node("cat=ap|" + agr));
				APnode.newDaughter(Anode = new Node("cat=a|" + agr));
					Anode.newDaughter(new Node("lex=dyr|" + term));
	}
	void IESAtree() {
		// TreeInfo settings
		tree.setOption("node.abbrMacro", "${cat}:${func}");
		tree.setOption("node.abbreviate", Boolean.TRUE);
		tree.setOption("node.PERMabbreviate", Boolean.FALSE);
		tree.setOption("node.PERMndaughter", Boolean.FALSE);
		tree.setOption("node.PERMterminal", Boolean.FALSE);

		// TreeInfo macro for terminal nodes, overriding default
		tree.setOption("macro.terminal.abbrMacro", "${lex}");
		tree.setOption("macro.terminal.PERMchange", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMremove", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreceive", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreorder", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMterminal", Boolean.TRUE);

		// Insert nodes in tree
		Node Sent, Sn, Pv, Aadv, Cg, DEPadv, Hadj, DEP0;
		String term = "|@@node.:terminal=true";

		tree.root().newDaughter(Sent = new Node("cat=Sent"));
			Sent.newDaughter(Sn = new Node("cat=S|func=n"));
				Sn.newDaughter(new Node("lex=Mary" + term));
			Sent.newDaughter(Pv = new Node("cat=P|func=v"));
				Pv.newDaughter(new Node("lex=is" + term));
			Sent.newDaughter(Aadv = new Node("cat=A|func=adv"));
				Aadv.newDaughter(new Node("lex=also" + term));
			Sent.newDaughter(Cg = new Node("cat=C|func=g"));
				Cg.newDaughter(DEPadv = new Node("cat=DEP|func=adv"));
					DEPadv.newDaughter(new Node("lex=more" + term));
				Cg.newDaughter(Hadj = new Node("cat=H|func=adj"));
					Hadj.newDaughter(new Node("lex=beautiful" + term));
				Cg.newDaughter(DEP0 = new Node("cat=DEP|func=?"));
					DEP0.newDaughter(new Node("lex=than Alice" + term));
	}
	void CHOMtree() {
		// Reuse HPSG tree
		HPSGtree();

		// Now force abbreviation of all nodes
		tree.setOption("node.PERMabbreviate", Boolean.FALSE);
		tree.setOption("node.abbreviate", Boolean.TRUE);
	}
	void DEPtree() {
		// TreeInfo settings
		tree.setOption("node.abbrMacro", "${lex}");
		tree.setOption("node.abbreviate", Boolean.TRUE);
		tree.setOption("node.PERMabbreviate", Boolean.FALSE);

		tree.setOption("node.PERMchange", Boolean.FALSE);
		tree.setOption("node.PERMremove", Boolean.FALSE);
		tree.setOption("node.PERMnmother", Boolean.FALSE);
		tree.setOption("node.PERMndaughter", Boolean.FALSE);

		// Insert nodes in tree
		Node chante, ami, chanson, jolie, souvent;
		tree.root().newDaughter(chante = new Node("lex=chante"));
			chante.newDaughter(ami = new Node("lex=ami"));
				ami.newDaughter(new Node("lex=mon"));
				ami.newDaughter(new Node("lex=vieil"));
			chante.newDaughter(chanson = new Node("lex=chanson"));
				chanson.newDaughter(new Node("lex=cette"));
				chanson.newDaughter(jolie = new Node("lex=jolie"));
					jolie.newDaughter(new Node("lex=fort"));
			chante.newDaughter(souvent = new Node("lex=souvent"));
				souvent.newDaughter(new Node("lex=tr?s"));
	}
}

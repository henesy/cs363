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
import java.util.*;
import javax.swing.*;

public class Tree extends JPanel implements Cloneable {
	// Tree and option variables
	Node root;							// Root node of tree.
	TreeInfo info;						// Global tree options

	// Selection variables
	transient Vector selectedNodes;		// Selected nodes 
	transient String selectedPath;		// Selected path of 0th selected.

	// Tree listeners
	transient TreeListener[] listeners;	// Registered Tree listeners
	transient boolean fireEvents;		// Fire tree events?

	// Layout variables 
	transient Dimension treeSize;		// Actual tree size
	transient Dimension minimumSize;	// Minimum canvas size
	transient int xref, yref; 			// Reference point coordinates
	public Tree() {
		// Start super constructor
		super();

		// Add mouse listeners
		addMouseListener(new TreeMouseAdapter());
		addMouseMotionListener(new TreeMouseMotionAdapter());

		// Initialize
		init();
	}
	public void init() {
		// Initialize tree and options
		root = new Node();
		info = new TreeInfo();

		// Initialize selection variables
		selectedNodes = new Vector();
		selectedPath = null;

		// Initialize tree listeners
		listeners = new TreeListener[0];
		fireEvents = true;

		// Initialize layout variables
		minimumSize = new Dimension(600,400);
		treeSize = new Dimension(600,400);
		setOpaque(false);
	}
	public void resetNodes() {
		deselectAll(false);
		root = new Node();
	}
	public void resetInfo() {
		info = new TreeInfo();
	}
	public Object clone() {
		Tree clone;
		
		try { 
			clone = (Tree) super.clone();

			// Clone root and info
			clone.root = (Node) root.clone();
			clone.info = (TreeInfo) info.clone();

			// Clone selectedNodes and listeners
			clone.selectedNodes = new Vector();
			clone.listeners = null;

			// Clone treeSize and minimumSize
			clone.treeSize = new Dimension(treeSize);
			clone.minimumSize = new Dimension(minimumSize);
		} catch (CloneNotSupportedException cnse) {
	        System.err.println(cnse); 
	        return null;
	    }
	 
		// Return clone
		return clone;
	}
	public TreeInfo info() {
		return info;
	}
	public void setInfo(TreeInfo info) {
		if (info != null)
			this.info = info;
		deselectAll(false);
		requestRepaintAll();
	}
	public void setRoot(Node root) {
		if (root != null)
			this.root = root;
		deselectAll(false);
		requestRepaintAll();
	} 
	public void addTreeListener(TreeListener listener) {
		// Create new TreeListener list
		TreeListener[] newlisteners = new TreeListener[listeners.length + 1];

		// Move listeners to new list 
		for (int i = 0; i < listeners.length; ++i) {
			if (listener == listeners[i]) 
				// return; listener is on old list
				return;
			else 
				// add old listener to new list
				newlisteners[i] = listeners[i];
		}
		newlisteners[listeners.length] = listener;

		// Set listeners to new list
		listeners = newlisteners;
	}
	public void removeTreeListener(TreeListener listener) {
		// Find instances of listener on listeners list
		int i, n;
		for (i = 0, n = 0; i < listeners.length; ++i)
			if (listeners[i] == listener) 
				++n;

		// Create new TreeListener list
		TreeListener[] newlisteners = new TreeListener[listeners.length - n];

		// Move listeners to newlisteners
		for (i = 0, n = 0; i < listeners.length; ++i) 
			if (listeners[i] != listener) 
				newlisteners[n++] = listeners[i];

		// Set listeners to new list
		listeners = newlisteners;
	}
	private void fireTreeEvent(TreeEvent e) {
		if (fireEvents) {
			// Inform each listener
			for (int i = 0; i < listeners.length; ++i) 
				listeners[i].treeActionPerformed(e);

			// Repaint super
			requestRepaint();
		}
	}
	// Selection manipulation (public)
	public void select(Node node, String path) {
		select(node, path, true);
	}

	public void select(Node node, String path, boolean fireEvents) {
		// Deselect old selected nodes (don't fire events), select new
		deselectAll(false);
		addSelect(node, path, fireEvents);
	}

	public void select(Node[] nodes) {
		select(nodes, true);
	}

	public void select(Node[] nodes, boolean fireEvents) {
		// Deselect old selected nodes (don't fire events)
		deselectAll(false);
		if (nodes == null)
			return;

		// Select new nodes
		for (int i = nodes.length - 1; i >= 0; --i) 
			if (selectedNodes.indexOf(nodes[i]) == -1 && nodes[i] != null
					&& nodes[i].mother != null) {
				selectedNodes.insertElementAt(nodes[i], 0);
				nodes[i].setOption("@@node.:selectNode", Boolean.TRUE);
			}

		// Fire Tree event
		fireTreeEvent(new TreeEvent(TreeEvent.SELECT));
	}
	public void selectAll() {
		Node[] nodes = new Node[1];
		nodes[0] = root;
		select(subnodes(nodes));
	}
	public void addSelect(Node node, String path) {
		addSelect(node, path, true);
	}

	public void addSelect(Node node, String path, boolean fireEvents) {
		// Return if node is null
		if (node != null && node.mother != null) {
			// Unmark path in first selected node
			if (selectedNodes.size() > 0 && selectedPath != null) 
				selectedAt(0).removeOption(AVM.optionPath(selectedPath) + ".:selectAV");
				
			// Insert node as selected node (removing it first, if necessary)
			selectedNodes.removeElement(node);
			selectedNodes.insertElementAt(node, 0);
			selectedPath = path;

			// Mark new selected node
			node.setOption("@@node.:selectNode", Boolean.TRUE);
			if (path != "" && path != null)
				node.setOption(AVM.optionPath(path) + ".:selectAV", Boolean.TRUE);
		}

		// Fire Tree Event
		fireTreeEvent(new TreeEvent(TreeEvent.SELECT));
	}
	public void toggleSelect(Node node, String path) {
		if (selectedNodes.indexOf(node) == -1)
			addSelect(node, path);
		else
			deselect(node);
	}
	public void deselect(Node node) {
		deselect(node, true);
	}

	public void deselect(Node node, boolean fireEvents) {
		// Return if node is null
		if (node == null)
			return;

		// Unmark deselected node
		node.removeOption("@@node.:selectNode");
		if (selectedNodes.indexOf(node) == 0 && selectedPath != null)
			node.removeOption(AVM.optionPath(selectedPath) + ".:selectAV");

		// Remove deselected node from selectedNodes
		selectedNodes.removeElement(node);

		// Fire Tree event
		fireTreeEvent(new TreeEvent(TreeEvent.SELECT));
	}
	public void deselectAll() {
		deselectAll(true);
	}

	public void deselectAll(boolean fireEvents) {
		// Deselect selected nodes
		for (int i = selectedNodes.size(); i > 0; --i) 
			deselect(selectedAt(0), false);
		selectedPath = null;

		// Fire events
		fireTreeEvent(new TreeEvent(TreeEvent.SELECT));
	}

	// Returning selected nodes
	public Node root() {
		return root;
	}
	Node minroot() {
		return minroot(selected());
	}

	Node minroot(Node[] nodes) {
		// The root node is the maximal root, so is a root of minroot
		Node minroot = root, subroot;

		// Descend the tree until no subnode of node is a root
		boolean nosubroots;
		do {
			// A daughter is a candidate as subroot if it contains one of
			// the nodes; two candidates means no subroots; start with
			// no subroot. 
			subroot = null;
			nosubroots = false;

			// Find subroot candidates among daughters
			for (int i = 0; i < minroot.daughters.size() && (!nosubroots); ++i) {
				// Check whether daughter(i) is a candidate
				Node daughter = minroot.daughterAt(i);

				// Daughter is candidate if supernode of any of nodes
				for (int j = 0; j < nodes.length && (!nosubroots); ++j) {
					// Test that nodes[j] has daughter as root
					if (nodes[j] != null && nodes[j].supernode(daughter)) {
						// Daughter is a candidate as subroot
						if (subroot == null || subroot == daughter) 
							// Only one candidate as subroot
							subroot = daughter;
						else 
							// Two candidates as subroot, so no subroot
							nosubroots = true;
					} else if (subroot == daughter) 
						// Daughter is both a candidate and not a candidate
						// so no daughter can be a subroot.
						nosubroots = true;
				}
			}

			// Replace minroot with new subroot
			if (subroot != null && !nosubroots) {
				minroot = subroot;
				nosubroots = false;
			} else 
				nosubroots = true;
		} while (!nosubroots);

		// Now minroot contains the minimal subroot
		return minroot;
	}
	public Node[] selected() {
		Node[] result = new Node[selectedNodes.size()];
		for (int i = 0; i < selectedNodes.size(); ++i) 
			result[i] = selectedAt(i);
		return result;
	}
	public Node[] getSelectedNodes() {
		return selected();
	}
	public Node[] selectedNodes() {
		return selected();
	}
	public Node[] subnodes() {
		return subnodes(selected());
	}

	public Node[] subnodes(Node[] roots) {
		// Create new vector to hold subnodes
		Vector subs = new Vector();
		Node[] subtree;

		// Run through nodes, selecting subtrees
		for (int i = 0; i < roots.length; ++i) {
			subtree = roots[i].subnodes();
			for (int j = 0; j < subtree.length; ++j)
				insertOrderedNode(subs, subtree[j]);
		}
		
		// Create new array
		Node[] subnodes = new Node[subs.size()];
		subs.copyInto(subnodes);

		// Return subnodes
		return subnodes;
	}
	public Node[] terminals() {
		Node[] roots = new Node[selectedNodes.size()];
		for (int i = 0; i < selectedNodes.size(); ++i)
			roots[i] = (Node) selectedNodes.elementAt(i);
		return terminals(roots);
	}

	public Node[] terminals(Node[] roots) {
		Vector nodes = new Vector();

		// Transfer roots to nodes vector in order
		for (int i = 0; i < roots.length; ++i) 
			insertOrderedNode(nodes, roots[i]);

		// Replace nodes with daughters until only terminals left
		for (int i = 0; i < nodes.size(); ++i) {
			// Process daughters in ith node
			Node node = (Node) nodes.elementAt(i);
			int daughterSize = node.daughters.size();

			if (daughterSize != 0) {
				// node is not terminal; replace with its daughters
				nodes.removeElementAt(i);
				for (int j = 0; j < daughterSize; ++j) 
					insertOrderedNode(nodes, node.daughterAt(j));
				
				// Go trhough list once more
				i = -1;
			}
		}
			
		// Create and initialize new array of terminals
		Node[] terminals = new Node[nodes.size()];
		nodes.copyInto(terminals);

		return terminals;
	}
	public Node[] getTerminalNodes(Node[] nodes) {
		return terminals(nodes);
	}
	public Node[] allTerminals() {
		Node[] roots = new Node[1];
		roots[0] = root;
		return terminals(roots);
	}
	public Node[] maximals() {
		Node[] nodes = selected();
		return maximals(nodes);
	}

	public Node[] maximals(Node[] nodes) {
		Vector M = new Vector();

		// Let M consist of all nodes in nodes
		for (int i = 0; i < nodes.length; ++i) 
			insertOrderedNode(M, nodes[i]);
		
		// Remove all nodes which have a super node in M
		for (int i = 0; i < M.size(); ++i) 
			if (containsSuper(M, (Node) M.elementAt(i)))
				M.removeElementAt(i--);
		
		// Mothers all of whose daughters are in M replace daughters
		for (int i = 0; i < M.size(); ++i) {
			Node node = (Node) M.elementAt(i);
			Node mother = node.mother;

			// Only look at first daughters of mother
			if (mother.daughters.indexOf(node) == 0 && mother.mother != null 
					&& containsDaughters(M, mother)) {
				// All daughters of mother are in M: insert mother in M
				insertOrderedNode(M, mother);
				
				// Remove daughters of mother
				for (int j = 0; j < mother.daughters.size(); ++j)
					M.removeElement(mother.daughterAt(j));

				// Go through list once more
				i = -1;
			}
		}

		// Create list of maximals
		Node[] maximals = new Node[M.size()];
		for (int i = 0; i < M.size(); ++i) 
			maximals[i] = (Node) M.elementAt(i);
		
		// Return maximals
		return maximals;
	}
	public Node[] maxRoots(Node[] nodes) {
		return maximals(nodes);
	}

	// Selection manipulation (low level)
	public int selectedCount() {
		return selectedNodes.size();
	}
	public Node selectedAt(int i) {
		return (i < 0 || i >= selectedNodes.size()) 
			? null
			: (Node) selectedNodes.elementAt(i);
	}
	public String selectedPath() {
		return selectedPath;
	}
	private void insertOrderedNode(Vector nodes, Node node) {
		// Return if node already in nodes
		if (nodes.indexOf(node) == -1) {
			// Find index to insert node
			int index;
			for (index = 0; index < nodes.size() && node.number
				< ((Node) nodes.elementAt(index)).number; ++index);
			
			// Insert node
			nodes.insertElementAt(node, index);
		}
	}
	boolean containsSuper(Vector M, Node node) {
		if (node.mother == null)
			// node is the root node
			return false; 
		else if (M.contains(node.mother))
			// M contains the mother of node
			return true;
		else 
			// Mother of node knows the answer
			return containsSuper(M, node.mother);
	}
	boolean containsDaughters(Vector M, Node mother) {
		boolean containsAll = true;

		// Check that M contains each daughter of mother
		for(int i = 0; i < mother.daughters.size() && containsAll; ++i)
			containsAll = M.contains(mother.daughterAt(i));

		// Return result
		return containsAll;
	}
	// Manipulating nodes
	public boolean moveToMother(Node mother, boolean subtrees) {
		return moveToMother(mother, subtrees, true);
	}

	public boolean moveToMother(Node mother, boolean subtrees, boolean fireEvents) {
		// Return if no selected nodes or mother is null
		if (selectedNodes.size() == 0 || mother == null) 
			return false;

		// Unmark selected path
		if (selectedPath != null)
			selectedAt(0).removeOption(AVM.optionPath(selectedPath) + ".:selectAV");

		// Move selected nodes
		boolean success = mother.moveToMother(selected(), subtrees);

		// Select mother
		select(mother, null, false);

		// Fire Tree event
		fireTreeEvent(new TreeEvent(TreeEvent.MOVE, success));

		return success;
	} 

	public boolean moveToMother(Node[] nodes, Node mother, boolean subtrees, 
			boolean fireEvents) {
		// Return if no nodes or mother is null
		if (nodes.length == 0 || mother == null) 
			return false;

		// Move selected nodes
		boolean success = mother.moveToMother(nodes, subtrees);

		// Fire Tree event
		fireTreeEvent(new TreeEvent(TreeEvent.MOVE, success));

		return success;
	}
	public boolean reorder(Node daughter, int x, int y) {
		// Find new and old index
		int oldindex = daughter.indexInMother();
		int newindex = daughter.mother.indexAtXY(x, y);
		if (newindex > oldindex)
			--newindex;

		// Return if no new index found
		if (newindex == -1)
			return false;

		// Insert node at new index and remove at old
		boolean success = daughter.mother.reorder(oldindex, newindex);

		// Fire tree event
		fireTreeEvent(new TreeEvent(TreeEvent.REORDER, success));

		// Return result
		return success;
	}
	public boolean remove() {
		// Return if no selected nodes
		if (selectedNodes.size() == 0)
			return false;

		// Deselect and remove first selected node, select mother
		Node node = selectedAt(0);
		deselect(node, false);
		boolean success = node.remove();

		// Fire event and select result if successful
		fireTreeEvent(new TreeEvent(TreeEvent.REMOVE, success));

		// Return result
		return success;
	}
	public boolean newDaughterAt(Node node, int index) {
		// Return if no selected nodes
		if (selectedNodes.size() == 0)
			return false;
		
		// Insert new daughter
		boolean success = selectedAt(0).newDaughterAt(node, index);

		// Fire Tree event if successful
		fireTreeEvent(new TreeEvent(TreeEvent.NEWDAUGHTER, success));

		// Return result
		return success;
	}
	public boolean newDaughter(Node node) {
		if (selectedNodes.size() == 0)
			return false;
		else
			return newDaughterAt(node, selectedAt(0).daughters.size());
	}
	public Node addDaughter(String AVMstring, int index) {
		Node node = new Node(AVMstring);
		if (index >= 0) 
			newDaughterAt(node, index);
		else
			newDaughter(node);
		return node;
	}
	public boolean newMother(Node mother) {
		return newMother(mother, true);
	}

	public boolean newMother(Node mother, boolean subtrees) {
		// Return null if no selected nodes
		if (selectedNodes.size() == 0) 
			return false;

		// Add new mother to first selected node, return null if unsuccessful
		if (! selectedAt(0).newMother(mother))
			return false;

		// Deselect first selected node, move selected to new mother
		deselect(selectedAt(0), false);
		boolean success = (selectedNodes.size() == 0) 
			? true : moveToMother(mother, subtrees, false);

		// Select mother if successful, fire Tree event
		if (success) 
			select(mother, null, false);
		fireTreeEvent(new TreeEvent(TreeEvent.NEWMOTHER, success));

		// Return result
		return success;
	}
	public Node addMother(String AVMstring) {
		Node node = new Node(AVMstring);
		newMother(node);
		return node;
	}

	public Node addMother() {
		Node node = new Node();
		newMother(node);
		return node;
	}
	public boolean abbreviate() {
		boolean success = true;

		// Set abbreviation for each node
		for (int i = 0; i < selectedNodes.size(); ++i)
			success &= selectedAt(i).abbreviate();

		// Fire LAYOUT event
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT, success));

		// Return result
		return success;
	}

	public boolean abbreviate(boolean status) {
		boolean success = true;

		// Set abbreviation for each node
		for (int i = 0; i < selectedNodes.size(); ++i)
			success &= selectedAt(i).abbreviate(status);

		// Fire LAYOUT event
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT, success));

		// Return result
		return success;
	}
	public boolean bonsai() {
		boolean success = true;

		// Set bonsai for each node
		for (int i = 0; i < selectedNodes.size(); ++i)
			success &= selectedAt(i).bonsai();

		// Fire LAYOUT event
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT, success));

		// Return result
		return success;
	}

	public boolean bonsai(boolean status) {
		boolean success = true;

		// Set bonsai for each node
		for (int i = 0; i < selectedNodes.size(); ++i)
			success &= selectedAt(i).bonsai(status);

		// Fire LAYOUT event
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT, success));

		// Return result
		return success;
	}
	public boolean setAV(String path, Object value) {
		// Return if no selected nodes
		if (selectedNodes.size() == 0) 
			return false;
			
		// Set AV in first selected node
		Node node = selectedAt(0);
		boolean success = node.setAV(path, value);
		
		// Select node and fire Tree event
		select(node, null, false);
		fireTreeEvent(new TreeEvent(TreeEvent.CHANGE, success));

		// Return
		return success;
	}
	public boolean setAV(String AVpairs) {
		// Return if no selected nodes
		if (selectedNodes.size() == 0) 
			return false;
			
		// Set AV in first selected node
		Node node = selectedAt(0);
		boolean success = node.setAV(AVpairs);
		
		// Select node and fire Tree event
		select(node, null, false);
		fireTreeEvent(new TreeEvent(TreeEvent.CHANGE, success));

		// Return
		return success;
	}
	public boolean removeAV(String path) {
		// Return if no selected nodes
		if (selectedNodes.size() == 0) 
			return false;
			
		// Set AV in first selected node
		Node node = selectedAt(0);
		boolean success = node.removeAV(path);
		
		// Select node and fire Tree event
		select(node, null, false);
		fireTreeEvent(new TreeEvent(TreeEvent.CHANGE, success));

		// Return
		return success;
	}
	public Object getAV(String path) {
		// Return null if no selected node
		if (selectedNodes.size() == 0) 
			return null;
		
		// Return result of getAV on first selected node
		return selectedAt(0).getAV(path);
	}
	public String[][] getAVM() {
		// Return null if no selected node
		if (selectedNodes.size() == 0)
			return null;
			
		// Return getAVM on first selected node
		return selectedAt(0).getAVM();
	}
	public boolean setOption(String path, Object value) {
		boolean success = info.options.setAV(path, value);
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT));
		return success;
	}
	public Object getOption(String path) {
		return info.options.getAV(path);
	}
	public boolean removeOption(String path) {
		boolean success = info.options.removeAV(path);
		fireTreeEvent(new TreeEvent(TreeEvent.LAYOUT));
		return success;
	}
	public void setLayoutStyle(int layoutStyle) {
		setOption("tree.layoutStyle", new Integer(layoutStyle));
	}
	public void setTreeFont(Font font) {
		setOption("tree.font", font);
	}
	// Layout and drawing
	public void paint(Graphics g) {
		// Layout tree, calculate origin, post-layout
		layoutTree(g);
		positionTree();
		root.postlayoutNode(xref, yref);

		// Draw background and set foreground color
		Color oldFG = info.switchColor(g, info.treeBG);
		if (info.treeBG != null) 
			g.fillRect(0, 0, treeSize.width, treeSize.height);
		info.switchColor(g, oldFG);
		info.switchColor(g, info.treeFG);

		// Calculate postlayout and draw tree
		root.drawNode(g, info, xref, yref);

		// Restore color
		info.switchColor(g, oldFG);
	}
	public boolean layoutTree(Graphics g) {
		// Compile TreeInfo options and set font
		info.compileTreeInfo();
		info.setFont(g);
		
		// Compile tree layout and reset updateAll
		boolean changed = root.layoutNode(info);
		info.updateAll = false;

		// Return changed status
		return changed; 
	}
	public void positionTree() {
		// Get viewport's extent size, if possible
		Component ancestor = getParent();
		Dimension vrect = (ancestor instanceof JViewport) 
			? ((JViewport) ancestor).getExtentSize()
			: null;
		
		// New tree size width
		int newWidth  = root.bbox.width  + 2 * info.treeDW;
		int newHeight = root.bbox.height + 2 * info.treeDW;

		// Calculate reference point
		xref = info.treeDW - root.bbox.x 
			+ (int) (info.treeXR * max(0, vrect.width - newWidth));
		yref = info.treeDW - root.bbox.y
			+ (int) (info.treeYR * max(0, vrect.height - newHeight));

		// New tree size width at least extent of view port
		if (vrect != null) {
			newWidth  = max(newWidth,  vrect.width);
			newHeight = max(newHeight, vrect.height);
		}

		// Adjust canvas size if necessary
		if (treeSize.width != newWidth || treeSize.height != newHeight) {
			treeSize.width  = newWidth;
			treeSize.height = newHeight;
			
			setSize(treeSize.width, treeSize.height);
			if (ancestor != null) {
				ancestor.invalidate();
				ancestor.repaint();
			}
		}
	}

	int max(int a, int b) {
		return (a > b) ? a : b;
	}
	public void print(Graphics g, int x, int y) {
		// Calculate print layout
		printlayout(g, x, y);

		// Draw tree
		Color oldFG = info.switchColor(g, info.treeFG);
		root.drawNode(g, info, xref, yref);
		info.switchColor(g, oldFG);

		// Screen layout destroyed, so update all
		requestRepaintAll();
	}
	public void printlayout(Graphics g, int x, int y) {
		// Calculate print layout 
		info.updateAll = true;
		layoutTree(g);

		// Position tree so upper left corner at (x,y)
		xref = x - (root.xrel + root.bbox.x);
		yref = y - (root.yrel + root.bbox.y);
		root.postlayoutNode(xref, yref); 
	} 

	// Other painting methods
	public void requestRepaint() {
		super.repaint();
	}
	public void requestRepaintAll() {
		info.updateAll = true;
		requestRepaint();
	}
	public Dimension getPreferredSize() {
		return new Dimension(treeSize.width, treeSize.height);
	}
	public Dimension getMinimumSize() {
		return new Dimension(minimumSize.width, minimumSize.height);
	}
	public Rectangle getBBox() {
		return new Rectangle(xref + root.xrel + root.bbox.x, 
			yref + root.yrel + root.bbox.y, 
			root.bbox.width,
			root.bbox.height);
	}
	public void drawBBox() {
		Graphics g = getGraphics();
		for (int i = 0; i < selectedNodes.size(); ++i) {
			Node node = selectedAt(0);
			g.drawRect(node.xabs + node.bbox.x, node.yabs + node.bbox.y,
				node.bbox.width, node.bbox.height);
		}
	}
	class TreeMouseAdapter extends MouseAdapter {
		Point downPoint, upPoint;
		Node downNode, upNode;
		Cursor cursor;

		public void mousePressed(MouseEvent e) {
			// Locate down point and corresponding node
			downPoint = e.getPoint();
			downNode = root().nodeAtXY(e.getX(), e.getY());

			// Use MOVE cursor if pressed over a node
			if (downNode != null) {
				cursor = getCursor();
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			}
		}
		public void mouseReleased(MouseEvent e) {
			// Return if no down-node
			if (downNode == null)
				return;
				
			// Locate up-point and up-node, and reset cursor
			upPoint = e.getPoint();
			upNode = root().nodeAtXY(e.getX(), e.getY());
			setCursor(cursor);

			// If downNode is not selected, then select it
			if (downNode != upNode && selectedNodes.indexOf(downNode) == -1) {
				if (e.isControlDown() || e.isShiftDown() ||
						 (e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0) 
					addSelect(downNode, null);
				else 
					select(downNode, null);
			}

			// Handle events
			if (upNode == null) {
				// Reorder node
				reorder(downNode, upPoint.x, upPoint.y);
			} else if (upNode != downNode && selectedNodes.contains(downNode)) {
				// Move selected nodes to mother
				moveToMother(upNode, true, true);
			}
		}
		public void mouseClicked(MouseEvent e) {
			// Find clicked node and number of clicks
			upNode = root().nodeAtXY(e.getX(), e.getY());
			int clicks = e.getClickCount();

			// Deslect all and return if no clicked node
			if (upNode == null) {
				deselectAll();
				return;
			}
				
			// Selected path
			String spath = upNode.avm.pathAtXY(info, e.getX(), e.getY());

			if (clicks == 1) {
				// Single click: select nodes
				if (e.isControlDown() || e.isShiftDown() ||
						 (e.getModifiers() & MouseEvent.BUTTON3_MASK)!=0) {
					// Node add-selected
					toggleSelect(upNode, spath);
				} else {
					// Node selected
					select(upNode, spath);
				}
			} else if (clicks == 2) {
				// Double click: toggle abbreviation
				addSelect(upNode, spath, false);
				abbreviate();
			}
		}
	}
	class TreeMouseMotionAdapter extends MouseMotionAdapter {
		public void mouseMoved(MouseEvent e) {
			// Default is no tooltip text
			setToolTipText(null);

			// Return if !PERMtooltip
			if (! info.PERMtooltip) 
				return;
				
			// Find node corresponding to mouse position; return if null
			Node node = root().nodeAtXY(e.getX(), e.getY());
			if (node == null)
				return;

			// Find path corresponding to mouse position; return if null
			String path = node.avm.pathAtXY(info, e.getX(), e.getY());
			if (path == "" || path == null) 
				return; 

			// Find tooltip corresponding to path and set if string
			Value value = node.avm.getValue(path);
			if (value != null) 
				setToolTipText(value.tooltipString);
		}
	}
}

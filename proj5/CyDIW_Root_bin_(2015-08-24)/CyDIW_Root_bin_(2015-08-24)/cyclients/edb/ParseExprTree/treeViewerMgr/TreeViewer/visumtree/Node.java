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
import java.io.*;
import java.util.*;

public class Node implements Serializable, Cloneable {
	// Data variables
	AVM avm; 							// AVM associated with this node
	Node mother;						// Mother of this node
	Vector daughters;				 	// Daughters of this node

	// Compiled options
	transient Color treeFG;				// Tree foreground color
	transient Color lineColor;			// Color of line to mother
	transient Color nodeFG;				// Node foreground color
	transient Color nodeBG;				// Node background color
	transient String abbrMacro;			// Abbreviation macro
	transient String[] abbrStrings;		// Abbreviation (result of abbrMacro)
	transient boolean abbreviate;		// Abbreviate AVM?
	transient boolean bonsai;			// Hide daughters in bonsai mode

	// Compiled permissions
	transient boolean PERMchange;		// Permission to change AVM
	transient boolean PERMremove;		// Permission to remove node
	transient boolean PERMmove;			// Permission to move node
	transient boolean PERMreceive;		// Permission to receive moved nodes
	transient boolean PERMnmother;		// Permission to add new mother
	transient boolean PERMndaughter;	// Permission to add new daughter
	transient boolean PERMreorder;		// Permission to change order of node
	transient boolean PERMterminal;		// Permission to be a terminal node
	transient boolean PERMabbreviate;	// Permission to toggle abbrev status
	transient boolean PERMbonsai;		// Permission to toggle bonsai status

	// Layout variables
	transient Rectangle bbox;			// Bounding box of node
	transient Point topHandle; 			// Handle at top of AVM
	transient Point botHandle;			// Handle at bottom of AVM 
	transient Point midHandle; 			// Midpoint of line to mother
	transient int[] xpoly, ypoly;		// 4-polygon x- and y-coords

	transient boolean up2date = false;	// Is layout uptodate?
	transient int xrel, yrel; 			// Ref. point rel. to mother
	transient int xabs, yabs;			// Ref. point in abs. coords

	// Linearization and ordering
	transient int number;				// Sequential numbers in tree
	public Node(Node mother) {
		// Data variables
		this.mother = mother;
		daughters = new Vector();
		avm = new AVM();

		// Transient graphics variables
		bbox = new Rectangle();
		topHandle = new Point();
		midHandle = new Point();
		botHandle = new Point();
		xpoly = new int[4];
		ypoly = new int[4];

		// Permissions 
		PERMchange = PERMremove = PERMmove = PERMreceive 
			= PERMnmother = PERMndaughter = PERMreorder = PERMterminal 
			= PERMabbreviate = PERMbonsai = true;
	} 

	public Node(String AVMlist) {
		this((Node) null);
		avm.setAV(AVMlist);
	}

	public Node() {
		this((Node) null);
	} 

	public Object clone() {
		Node clone;
		
		try { 
			clone = (Node) super.clone();

			// Clone AVM
			clone.avm = (AVM) avm.clone();

			// Clone daughters
			Node daughter;
			clone.daughters = new Vector(daughters.size());
			for (int i = 0; i < daughters.size(); ++i) {
				daughter = (Node) (daughterAt(i).clone());
				daughter.mother = clone;
				clone.daughters.addElement(daughter);
			}

			// Clone bbox, handles
			clone.bbox = new Rectangle(bbox);
			clone.topHandle = new Point(topHandle);
			clone.midHandle = new Point(midHandle);
			clone.botHandle = new Point(botHandle);
			
			// Clone xpoly, ypoly
			clone.xpoly = new int[4];
			clone.ypoly = new int[4];
			for (int i = 0; i < 4; ++i) {
				clone.xpoly[i] = xpoly[i];
				clone.ypoly[i] = ypoly[i];
			}
		} catch (CloneNotSupportedException cnse) {
			System.err.println(cnse); 
			return null;
		}

		// Return clone
		return clone;
	}
	public Node(Node mother, String AVlist) {
		this(mother);
		avm.setAV(AVlist);
	}
	// Node manipulation (public)
	public boolean moveToMother(Node[] nodes, boolean subtrees) {
		// Fail if PERMreceive is false in new mother
		if (!PERMreceive) 
			return false; 

		// Repeat moving nodes while unmoved nodes left
		boolean finish = false, nosubtrees, moveable = false;
		boolean success = true;
		Move move = new Move();

		while (!finish) {
			move.reset();
			finish = true;

			// Find minimal move among all unmoved nodes
			for (int i = 0; i < nodes.length; ++i) {
				// Only look at unmoved nodes (non-null)
				if (nodes[i] != null) {
					nosubtrees = false; 

					// If !PERMterminal in mother, then no subtree-move if
					// moved is the only daughter; no move at all if 
					// moved also has no daughters itself 
					if ((!nodes[i].mother.PERMterminal)
							&& (nodes[i].mother.daughters.size() == 1)) {
						nosubtrees = true;
						if (nodes[i].daughters.size() == 0) 
							nodes[i] = null;
					}

					// Mark node as unmoveable if !PERMmove, if node is the 
					// root node, or if node is already daughter in new mother
					if ((nodes[i] == null) || (!nodes[i].PERMmove) 
							|| (nodes[i].mother == null) 
							|| (nodes[i].mother == this)) {
						nodes[i] = null;
						success = false;
					} else {
						// No subtree-move if node contains newmother in subtree
						nosubtrees |= supernode(nodes[i]);

						// Search backwards and forwards, picking best move
						moveable = move.setBest(nodes[i], this, subtrees 
							& !nosubtrees, false);
						moveable |= move.setBest(nodes[i], this, subtrees 
							& !nosubtrees, true); 

						// Discard nodes[i] if unmoveable
						if (!moveable) {
							nodes[i] = null;
							success = false;
						} else 
							finish = false;
					}
				}
			}

			// Perform best move, set moved to null
			if (move.index != -1) {
				Node oldmother = move.moved.mother;
				Node newmother = move.dest;
				Node moved = move.moved;

				// Subtree or single move?
				if (move.subtrees) {
					// Move moved and subtree from oldmother to newmother
					oldmother.daughters.removeElement(moved);
					moved.mother = newmother;
					newmother.daughters.insertElementAt(moved, move.index);
				} else {
					// Move move.moved alone: move daughters to oldmother
					int index = oldmother.daughters.indexOf(moved);
					oldmother.daughters.removeElementAt(index);
					for (int i = moved.daughters.size()-1; i >= 0; --i) {
						oldmother.daughters.insertElementAt(
							moved.daughterAt(i), index);
						moved.daughterAt(i).mother = oldmother;
					}
					moved.daughters.removeAllElements();

					// Insert moved in newmother
					moved.mother = newmother;
					newmother.daughters.insertElementAt(moved, move.index);
				}

				// Invalidate old and new mother
				moved.invalidate();
				oldmother.invalidate();

				// Discard moved node
				for (int i = 0; i < nodes.length; ++i) 
					if (nodes[i] == moved)
						nodes[i] = null;
			}
		}

		// All nodes moved; renumber tree
		root().number(0);

		// Return success status
		return success;
	}
	public boolean reorder(int index, int newindex) {
		// Fail if index and newindex are not within bounds
		if ((index < 0) || (newindex < 0) || (index >= daughters.size())
				|| (newindex >= daughters.size()))
			return false;
		
		// Move daughter from index to newindex
		Node node = daughterAt(index);
		daughters.removeElementAt(index);
		daughters.insertElementAt(node, newindex);

		// Restore old tree if the new tree is invalid
		if (check(0) < 0) {
			// Move node back from newindex to index
			daughters.removeElementAt(newindex);
			daughters.insertElementAt(node, index);

			// Invalid operation, return false
			return false;
		}

		// Number tree and mark this node for updating
		root().number(0);
		invalidate();

		// Successful operation, return moved node
		return true;
	}	
	public boolean remove() {
		// Check PERMremove and root node
		if (PERMremove == false || mother == null) 
			return false;

		// Save restore information
		Node oldmother = mother;
		int oldindex = mother.daughters.indexOf(this);

		// Remove node and insert daughters in mother
		mother.daughters.removeElementAt(oldindex);
		for (int i = daughters.size() - 1; i >= 0; --i) {
			mother.daughters.insertElementAt(daughterAt(i), oldindex);
			daughterAt(i).mother = mother;
		}
		
		// Restore old tree if new tree invalid
		if (mother.check(0) < 0) {
			// Remove node's daughters from oldmother
			for (int i = 0; i < daughters.size(); ++i) {
				mother.daughters.removeElementAt(oldindex);
				daughterAt(i).mother = this;
			}

			// Reinsert node in oldmother
			oldmother.daughters.insertElementAt(this, oldindex);

			// Invalid operation, return false
			return false;
		}
		
		// Number tree and mark oldmother for updating
		root().number(0);
		oldmother.invalidate();

		// Successful operation, return true
		return true;
	} 
	public boolean newDaughterAt(Node node, int index) {
		// Check PERMndaughter and index range
		if ((! PERMndaughter) || (index < 0) || (index > daughters.size())
				|| (node == null)) 
			return false;

		// Detach node from old mother and remove all daughters
		if (node.mother != null) 
			node.mother.daughters.removeElement(node);
		node.daughters.removeAllElements();

		// Insert new daughter in this node
		daughters.insertElementAt(node, index);
		node.mother = this;

		// Number tree and mark new daughter for updating
		root().number(0);
		node.invalidate();

		// Return true;
		return true;
	}
	public boolean newDaughter(Node node) {
		return newDaughterAt(node, daughters.size());
	}
	public boolean newMother(Node node) {
		// Check PERMnmother and root
		if ((! PERMnmother) || (mother == null)) 
			return false;
		
		// Detach node from old mother and remove all daughters
		if (node.mother != null) 
			node.mother.daughters.removeElement(node);
		node.daughters.removeAllElements();

		// Insert new mother between this node and old mother
		mother.daughters.setElementAt(node, mother.daughters.indexOf(this));
		node.daughters.insertElementAt(this, 0);
		node.mother = mother;
		mother = node;

		// Number tree and mark this node for updating
		root().number(0);
		invalidate();

		// Return true
		return true; 
	}
	public boolean abbreviate() {
		return abbreviate(! abbreviate);
	}

	public boolean abbreviate(boolean status) {
		// Return false if not PERMabbreviate
		if (! PERMabbreviate) 
			return false;
		
		// Set bonsai to desired status
		if (status == true)
			setOption("@@node.abbreviate", Boolean.TRUE);
		else
			removeOption("@@node.abbreviate");

		// Return true
		return true;
	}
	public boolean bonsai() {
		return bonsai(!bonsai);
	}

	public boolean bonsai(boolean status) {
		// Return false if not PERMbonsai
		if (! PERMbonsai) 
			return false;
		
		// Set bonsai to desired status
		if (status == true)
			setOption("@@node.bonsai", Boolean.TRUE);
		else
			removeOption("@@node.bonsai");

		// Return true
		return true;
	}
	public Node prev() {
		// Move from root node to null
		if (mother == null)
			return null;

		// Move from first daughter to mother (but return null for root node)
		if (mother.daughters.indexOf(this) == 0)
			return (mother.mother == null) ? null : mother;

		// Move from sister to previous sister, then down to right-corner terminal
		Node node = mother.daughterAt(mother.daughters.indexOf(this) - 1);
		while (node.daughters.size() != 0) 
			node = node.daughterAt(node.daughters.size() - 1);
		return node;
	}
	public Node next() {
		// Move from mother to first daughter
		if (daughters.size() != 0) 
			return daughterAt(0);

		// Move from right-corner terminal up to first non-right-corner
		// node, then to its next sister (null for root node)
		Node node = this; 
		while (node.mother != null && node.mother.daughters.indexOf(node) + 1 
				== node.mother.daughters.size()) 
			node = node.mother;
		return (node.mother == null) ? null 
			: node.mother.daughterAt(node.mother.daughters.indexOf(node) + 1);
	}
	public Node[] subnodes() {
		// Find number of nodes in tree and create array
		int last = number(number);
		Node[] result = new Node[last-number];

		// Insert nodes in tree order, using next-method
		result[0] = this;
		for (int i = 1; i < last - number; ++i)
			result[i] = result[i-1].next();

		// Return array
		return result;
	}

	// Node manipulation (low level)
	public AVM avm() {
		return avm;
	}
	public int size() {
		return daughters.size();
	}
	public int daughterCount() {
		return size();
	}
	public Node daughterAt(int i) {
		return ((i < 0) || (i >= daughters.size())) 
			? null : (Node) daughters.elementAt(i);
	}
	public Node mother() {
		return mother;
	}
	public Node getMother() {
		return mother;
	}
	public Node[] daughters() {
		Node[] list = new Node[daughters.size()];
		for (int i = 0; i < daughters.size(); ++i) 
			list[i] = daughterAt(i);
		return list;
	}
	public Node root() {
		return (mother == null) ? this : mother.root();
	}
	public boolean supernode(Node node) {
		if (node == this) 
			// The node itself
			return true;
		else if (mother == null) 
			// The node is root, no more mothers
			return false;
		else
			// The node has a mother, she knows
			return mother.supernode(node);
	}
	public int indexInMother() {
		return (mother==null) ? -1 : mother.daughters.indexOf(this);
	}

	// Node manipulation (private)
	public void invalidate() {
		up2date = false;
		if (mother != null) 
			mother.invalidate();
	}
	int number(int n) {
		// Numbering this node in
		number = n++;
		
		// Numbering each daughter
		for (int i = 0; i < daughters.size(); ++i) 
			n = daughterAt(i).number(n);

		// Return number
		return n;
	}
	public int check(int blockNumber) {
		// Check PERMterminal
		if ((!PERMterminal) && daughters.size() == 0)
			return -1;

		// Check PERMreorder
		if (!PERMreorder) {
			if (number < blockNumber)
				return -1;
			else 
				blockNumber = number;
		}

		// Check each daughter subtree
		for (int i = 0; i < daughters.size(); ++i) {
			blockNumber = daughterAt(i).check(blockNumber);

			// Return with -1 if PERMreorder was violated
			if (blockNumber == -1)
				return -1;
		}

		// Return last block number
		return blockNumber;
	}
	boolean blocks() {
		// Set blocks if !PERMreorder
		if (!PERMreorder)
			return true;

		// Set blocks if any daughter has blocks
		for (int i = 0; i < daughters.size(); ++i) {
			if (daughterAt(i).blocks())
				return true;
		}

		// Return false
		return false;
	}
	class Move {
		// Destination node, moved node, current node
		Node moved;				// Moved node
		Node dest;				// Destination node (new mother)
		int index;				// Index of best move
		boolean subtrees;		// Subtree status of best move
		int cost;				// Cost of best move

		// Constructor
		Move() {
			reset();
		}

		// Reset move
		void reset() {
			moved = dest = null;
			index = -1;
		}

		// Return absolute value
		int abs(int i) {
			return (i < 0) ? -i : i;
		}
		boolean setBest(Node moved, Node dest, boolean subtrees, 
				boolean forward) {
			Node RCnode = RCnode(dest);
			Node node = moved;
			boolean delay = false, addlastDelay = false;
			boolean moveable = false, addlast;
			boolean single = moved.PERMterminal;
			boolean blocks = moved.blocks();
			int index, cost = 0;

			// Is moved terminal RC of last daughter in local dest tree?
			if (moved == RCnode && forward && (subtrees || single)) 
				moveable |= pickBestMove(moved, dest, subtrees, 
					dest.daughters.size(), cost);
			
			// Continue moving until we reach the end
			while (true) {
				// Move to next node; return if null
				node = (forward) ? node.next() : node.prev();
				if (node == null)
					return moveable;

				// Is node contained in local dest tree?
				if (node.mother == dest) {
					// Reached daughter node of dest
					index = node.mother.daughters.indexOf(node);
					delay = forward;
				} else 
					// Outside mother's local tree
					index = -1;

				// Is node terminal RC of last daughter in local dest tree?
				if (node == RCnode) {
					// Reached right corner of last daughter
					addlast = true;
					addlastDelay = !forward;
				} else
					addlast = false;
			
				// Insert-spot for delayed crossing of node
				if (index != -1 && delay && (subtrees || single))
					moveable |= pickBestMove(moved, dest, subtrees, index, cost);
				if (addlast && addlastDelay && (subtrees || single))
					moveable |= pickBestMove(moved, dest, subtrees, 
						dest.daughters.size(), cost);

				// Update cost and check blocks
				cost += moved.number - node.number;
				if (!node.PERMreorder) {
					// Block single move if moved is itself a block
					if (!moved.PERMreorder) 
						single = false;

					// No subtree-move if moved tree blocks, and node not in subtree
					if (blocks && (!(node.supernode(moved)))) 
						subtrees = false;
				}

				// Return if subtree move and single move both illegal
				if (!(single || subtrees)) 
					return moveable;

				// Delayed insert-spot after we cross node
				if (index != -1 && (!delay) && (single || subtrees)) 
					moveable |= pickBestMove(moved, dest, subtrees, 
						index, cost);
				if (addlast && !addlastDelay && (single || subtrees))
					moveable |= pickBestMove(moved, dest, subtrees,
						dest.daughters.size(), cost);

			}
		}
		boolean pickBestMove(Node moved, Node dest, boolean subtrees, 
				int index, int cost) {
			// Preferences: subtrees true, then smallest cost
			if ((index != -1) && (this.index == -1 
					|| ((!this.subtrees) && subtrees) || ((this.subtrees
					== subtrees) && (abs(cost) < abs(this.cost))))) {
				this.dest = dest;
				this.moved = moved;
				this.subtrees = subtrees;
				this.index = index;
				this.cost = cost;
			}

			// Return true if new move is legal
			return (index == -1) ? false : true;
		}
		Node RCnode(Node node) {
			// Return node if no daughters, else return RCnode of last daughter
			if (node.daughters.size() == 0) 
				return node;
			else
				return RCnode(node.daughterAt(node.daughters.size()-1));
		}
		public String toString() {
			return
				"moved: " + ((moved == null) ? "null" : moved.avm.toString()) +
				"\ndest: " + ((dest == null) ? "null" : dest.avm.toString()) +
				"\nindex: " + index +
				"\nsubtrees: " + subtrees +
				"\ncost: " + cost + "\n";
		}
	}
	public Node addDaughter(String daughterAVM) {
		Node node = new Node(daughterAVM);
		newDaughter(node);
		return node;
	}

	public Node addDaughter(String daughterAVM, int index) {
		Node node = new Node(daughterAVM);
		if (index < 0 || index > daughters.size())
			index = daughters.size();
		newDaughterAt(node, index);
		return node;
	}

	public Node addDaughter(Node daughter, int index) {
		if (index < 0 || index > daughters.size()) 
			index = daughters.size();
		newDaughterAt(daughter, index);
		return daughter;
	}

	public Node addDaughter(int index) {
		Node node = new Node();
		newDaughterAt(node, index);
		return node;
	}
	public Node addMother(String motherAVM) {
		Node node = new Node(motherAVM);
		newMother(node);
		return node;
	}

	private Node addMother(Node mother) {
		newMother(mother);
		return mother;
	}

	public Node addMother() {
		Node node = new Node();
		newMother(node);
		return node;
	}
	public boolean setAV(String path, Object value) {
		if (!PERMchange)
			return false; 
		invalidate();
		return avm.setAV(path, value);
	}
	public boolean setAV(String AVlist) {
		if (!PERMchange)
			return false;
		invalidate();
		return avm.setAV(AVlist);
	}
	public boolean setOption(String path, Object value) {
		invalidate();
		return avm.setOption(path, value);
	}
	public boolean removeAV(String path) {
		if (!PERMchange)
			return false;
		invalidate();
		return avm.removeAV(path);
	}
	public boolean removeOption(String path) {
		invalidate();
		return avm.removeOption(path);
	} 
	public Object getAV(String path) {
		return avm.getAV(path);
	}
	public Object getOption(String path) {
		return avm.getOption(path);
	}
	public String[][] getAVM() {
		return avm.getAVM();
	}
	public void compileNode(TreeInfo info) {
		// Reset options
		treeFG = lineColor = nodeFG = nodeBG = null;
		abbreviate = bonsai = false;
		abbrMacro = null;
		abbrStrings = null;

		// Reset permissions
		PERMchange = PERMremove = PERMmove = PERMreceive 
			= PERMnmother = PERMndaughter = PERMreorder 
			= PERMabbreviate = PERMbonsai = true;

		// Compile options from node in info.options and @@node in root AVM
		compileNodeOptions(info, info.options.getAV("node"));
		compileNodeOptions(info, avm.getAV("@@node"));

		// Compile abbreviation
		if (abbreviate) 
		abbrStrings = (abbreviate && abbrMacro != null)
			? TreeInfo.splitString(info.expandMacro(abbrMacro, avm, "")) 
			: null;
	}
	private void compileNodeOptions(TreeInfo info, Object optionsObj) {
		String attr;
		Object val;

		if (optionsObj instanceof AVM) {
			AVM options = (AVM) optionsObj;

			// Process each attribute in options
			for (int i = 0; i < options.size(); ++i) {
				// Read attribute and value
				attr = options.attributeAt(i);
				val = options.valueAt(i);

				// Test for and process options
				if (attr.charAt(0) == ':') {
					// Option is a macro
					compileNodeOptions(info, info.options.getAV("macro."
						+attr.substring(1)));
				} else if (val instanceof Color || val == null) {
					// Option value is a color
					if (attr.equals("treeFG")) 
						treeFG = (Color) val;
					else if (attr.equals("lineColor"))
						lineColor = (Color) val;
					else if (attr.equals("nodeFG"))
						nodeFG = (Color) val;
					else if (attr.equals("nodeBG")) 
						nodeBG = (Color) val;
				} else if (val instanceof Boolean) {
					boolean boolval = ((Boolean) val).booleanValue();
					if (attr.equals("abbreviate")) 
						abbreviate = boolval;
					else if (attr.equals("bonsai")) 
						bonsai = boolval;
					else if (attr.equals("PERMchange"))
						PERMchange = boolval;
					else if (attr.equals("PERMremove"))
						PERMremove = boolval;
					else if (attr.equals("PERMmove"))
						PERMmove = boolval;
					else if (attr.equals("PERMreceive"))
						PERMreceive = boolval;
					else if (attr.equals("PERMnmother"))
						PERMnmother = boolval;
					else if (attr.equals("PERMndaughter"))
						PERMndaughter = boolval;
					else if (attr.equals("PERMreorder"))
						PERMreorder = boolval;
					else if (attr.equals("PERMterminal"))
						PERMterminal = boolval;
					else if (attr.equals("PERMabbreviate"))
						PERMabbreviate = boolval;
					else if (attr.equals("PERMbonsai"))
						PERMbonsai = boolval;
				} else if (val instanceof String || val == null) {
					if (attr.equals("abbrMacro")) 
						abbrMacro = (String) val;
				}
			}
		}
	}
	// Main layout
	public void drawNode(Graphics g, TreeInfo info, int x, int y) {
		Color color, tcolor;

		// Absolute reference point and AVM reference point
		xabs = x + xrel;
		yabs = y + yrel;
		int xavm = xabs - avm.bbox.x - avm.bbox.width  / 2; 
		int yavm = yabs - avm.bbox.y - avm.bbox.height / 2;

		// Draw link to mother if mother is not null and not root
		if (mother != null && mother.mother != null) {
			color = info.switchColor(g, lineColor);
			g.drawLine(xabs + topHandle.x, yabs + topHandle.y, 
				xabs + midHandle.x, yabs + midHandle.y);
			g.drawLine(xabs + midHandle.x, yabs + midHandle.y, 
				x + mother.botHandle.x, y + mother.botHandle.y);
			info.switchColor(g, color);
		}

		// Set tree foreground color
		tcolor = info.switchColor(g, treeFG);

		// Never draw the root node
		if (mother != null) {
			// Draw AVM background
			info.drawBG(g, nodeBG, xavm + avm.bbox.x, yavm + avm.bbox.y, 
				avm.bbox.width + 1, avm.bbox.height + 1);

			// Draw AVM foreground
			color = info.switchColor(g, nodeFG);
			avm.drawAVM(g, info, abbrStrings, xavm, yavm);
			info.switchColor(g, color);
		}

		// Draw subtree 
		if (bonsai && daughters.size() > 0) {
			// Calculate bonsai triangle (only if not terminal)
			xpoly[0] = xabs + botHandle.x;
			ypoly[0] = yabs + botHandle.y;

			xpoly[1] = xpoly[0] + info.bonsaiDX;
			ypoly[1] = ypoly[0] + info.bonsaiDY;

			xpoly[2] = xpoly[0] - info.bonsaiDX;
			ypoly[2] = ypoly[1]; 

			// Draw bonsai background
			info.switchColor(g, info.bonsaiBG);
			if (info.bonsaiBG != null) 
				g.fillPolygon(xpoly, ypoly, 3); 

			// Draw bonsai foreground
			info.switchColor(g, info.bonsaiFG);
			g.drawPolygon(xpoly, ypoly, 3);
		} else {
			// Draw daughters in full 
			for (int i = 0; i < daughters.size(); ++i) 
				daughterAt(i).drawNode(g, info, xabs, yabs);
		}

		// Reset tree foreground color
		info.switchColor(g, tcolor);
	}
	public boolean layoutNode(TreeInfo info) {
		// Variables
		int llx, lly, urx, ury, maxheight = 0;

		// Return if up-to-date
		if (up2date && ! info.updateAll) 
			return false;
		else 
			up2date = true;

		// Compile node options and layout AVM
		compileNode(info);
		avm.layoutAVM(info, abbrStrings);

		// Initialize bounding box
		if (mother == null) {
			// This is the root node, so AVM is invisible
			llx = urx = 0;
			lly = ury = info.nodeDY;
			avm.bbox.x = avm.bbox.y = 0;
			avm.bbox.height = avm.bbox.width = 0;
			botHandle.x = 0;
			botHandle.y = -1000;
		} else {
			// This is a normal node, so AVM is visible
			llx = - avm.bbox.width / 2;
			lly = - avm.bbox.height / 2;
			urx = llx + avm.bbox.width;
			ury = lly + avm.bbox.height;
		}

		// Daughter layout
		int width = 0;
		for (int i = 0; i < daughters.size(); ++i) {
			// Layout daughter
			Node daughter = daughterAt(i);
			daughter.layoutNode(info);
			width += daughter.bbox.width;

			// Calculate daughter's handles
			daughter.topHandle.x = daughter.midHandle.x = 0;
			daughter.topHandle.y = daughter.midHandle.y = 
				-daughter.avm.bbox.height / 2 - info.nodeHNDL;
			daughter.botHandle.x = 0;
			daughter.botHandle.y = -daughter.topHandle.y;
		} 
		width += (daughters.size()-1) * info.nodeDX; 
		int xpos = -width / 2;

		// Calculate bbox and positioning of subtrees
		if (bonsai && daughters.size() > 0 && mother != null) {
			// Layout bonsai tree (only if not terminal)
			llx = min(llx, -info.bonsaiDX);
			urx = max(urx, info.bonsaiDX);
			ury = max(ury, botHandle.y + info.bonsaiDY);
		} else {
			// Layout top-down or bottom-up tree

			// Find maximal height of daughter subtrees
			for (int i = 0; i < daughters.size(); ++i) 
				maxheight = max(maxheight, daughterAt(i).bbox.height);

			// Position daughters and calculate bounding box
			for (int i = 0; i < daughters.size(); ++i) {
				Node daughter = daughterAt(i);

				// Calculate x coordinate of daughter
				width = daughter.bbox.width;
				daughter.xrel = xpos + width / 2;
				xpos += width + info.nodeDX;

				// Calculate y coordinate of daughter
				switch (info.layoutStyle) {
					case TreeInfo.TOPDOWN: 
						daughter.yrel = avm.bbox.height / 2 
							+ daughter.avm.bbox.height / 2 
							+ ((mother == null) ? 0 : info.nodeDY);
						break;
					case TreeInfo.BOTTOMUP:
					default:
						daughter.yrel = avm.bbox.height / 2 
							+ maxheight - daughter.bbox.height
							+ daughter.avm.bbox.height / 2
							+ ((mother == null) ? 0 : info.nodeDY);
						break;
				}

				// Calculate maximal bounding box so far
				llx = min(llx, daughter.xrel + daughter.bbox.x);
				lly = min(lly, daughter.yrel + daughter.bbox.y);
				urx = max(urx, daughter.xrel + daughter.bbox.x 
					+ daughter.bbox.width);
				ury = max(ury, daughter.yrel + daughter.bbox.y 
					+ daughter.bbox.height);
			}
		}
			
		// Calculate bounding box
		bbox.x = llx;
		bbox.y = lly;
		bbox.width = urx - llx;
		bbox.height = ury - lly;

		// Calculate midHandles
		midHandles(info);

		// Return
		return true;
	}
	void postlayoutNode(int x, int y) {
		// Calculate absolute position
		xabs = x + xrel;
		yabs = y + yrel;

		// Calculate absolute position of daughters
		for (int i = 0; i < daughters.size(); ++i) 
			daughterAt(i).postlayoutNode(xabs, yabs);
	}
	public Rectangle getBBox() {
		return new Rectangle(xabs - avm.bbox.width / 2, 
			yabs - avm.bbox.height / 2, avm.bbox.width, avm.bbox.height);
	} 
	public Rectangle getPhysicalBBox() {
		return getBBox();
	}

	// Reverse calculation from coordinates
	public Node nodeAtXY(int x, int y) {
		// This node contains the specified point
		if (avm.bbox.contains(x - xabs + avm.bbox.x + avm.bbox.width / 2, 
				y - yabs + avm.bbox.y + avm.bbox.height / 2)) 
			return this;

		// One of the daughters contains the specified point
		for (int d = 0; d < daughters.size(); ++d) {
			Node daughter = daughterAt(d);
			if (daughter.bbox.contains(x-daughter.xabs, y-daughter.yabs)) 
				return daughter.nodeAtXY(x, y);
		}

		// No node contains the specified point
		return null;
	}
	public int indexAtXY(int x, int y) {
		// Undefined if P above mother's bottom handle
		if (y < botHandle.y + yabs) 
			return -1;

		// Find index of first daughter for which (x,y) is not to the right
		int index = 0;
		while (index < daughters.size() && daughterAt(index).rightPoint(x, y))
			++index;

		// Return index
		return index;
	}
	private boolean rightPoint(int px, int py) {
	    // Calculate absolute coordinates of B,M,T
		int bx = mother.botHandle.x + mother.xabs; 
		int by = mother.botHandle.y + mother.yabs;
	    int mx = midHandle.x + xabs; 
	    int my = midHandle.y + yabs;

	    // P is to the right of and below M
	    if (mx < px && my < py)
	        return true;
	   
	    // P is above M and det(BM,PM) > 0; 
	    if ((bx - mx) * (my - py) - (my - by) * (px - mx) < 0)
	        return true;
	    
	    // P is to the left of BMT
	    return false;
	}

	// Calculating midHandles
	public void midHandles(TreeInfo info) {
		// Calculate mid handles if bottomup layout
		if (info.layoutStyle == TreeInfo.BOTTOMUP) {
			// Create new slopes object
			Slopes slopes = new Slopes();

			// Calculate mid handles of daughters left of mother's center
			for (int i = daughters.size() - 2; i >= 0; --i) {
				Node daughter = daughterAt(i);
				if (daughter.xrel - daughter.avm.bbox.width / 2 < 0) {
					// Daughter is left of mother
					slopes.init(daughter.xrel + daughter.topHandle.x,
						daughter.yrel + daughter.topHandle.y, 
						botHandle.x, botHandle.y);
					
					// Adjust slopes recursively so p,q lie above right 
					// sister subtree, then calculate midHandle
					daughterAt(i + 1).adjustSlopes(slopes, info, 0, 0);
					slopes.setMidHandle(daughter);
				}
			}

			// Calculate mid handles of daughters right of mother's center
			for (int i = 1; i < daughters.size(); ++i) {
				Node daughter = daughterAt(i);
				if (daughter.xrel + daughter.avm.bbox.width / 2 > 0) {
					// Daughter is left of mother
					slopes.init(daughter.xrel + daughter.topHandle.x,
						daughter.yrel + daughter.topHandle.y, 
						botHandle.x, botHandle.y);
					
					// Adjust slopes recursively so p,q lie above left 
					// sister subtree, then calculate midHandle
					daughterAt(i - 1).adjustSlopes(slopes, info, 0, 0);
					slopes.setMidHandle(daughter);
				}
			}
		}
	}
	void adjustSlopes(Slopes slopes, TreeInfo info, int x, int y) {
		// Calculate reference point (xref,yref) of this node (=T) relative to Q
		int Tx = x + xrel;
		int Ty = y + yrel;

		// Return if P is above midHandle of T
		if (slopes.py < Ty + midHandle.y) 
			return;
			
		// Is P to the left or to the right of Q and T?
		if (slopes.px < slopes.qx) {
			// P is to the left of Q and T

			// Ensure p and q lie above midHandle of T
			slopes.above(Tx + midHandle.x - info.nodeMDX, Ty + midHandle.y);

			// Ensure p and q lie above top left corner of T
			slopes.above(Tx - avm.bbox.width / 2 - info.nodeCDX, 
				Ty - avm.bbox.height / 2 - info.nodeCDY);

			// Continue recursively with leftmost daughter of T
			if (daughters.size() > 0) 
				daughterAt(0).adjustSlopes(slopes, info, Tx, Ty);
		} else if (slopes.px > slopes.qx) {
			// P is to the right of Q and T
			
			// Ensure p and q lie above midHandle of T
			slopes.above(Tx + midHandle.x + info.nodeMDX, Ty + midHandle.y);

			// Ensure p and q lie above top right corner of T
			slopes.above(Tx + avm.bbox.width / 2 + info.nodeCDX, 
				Ty - avm.bbox.height / 2 - info.nodeCDY);

			// Continue recursively with rightmost daughter of T
			if (daughters.size() > 0) 
				daughterAt(daughters.size()-1).adjustSlopes(slopes, info, Tx, Ty);
		}
	}
	class Slopes {
		int px, py, qx, qy;
		double ps, qs;

		// Reset object
		void init(int x1, int y1, int x2, int y2) {
			px = x1; py = y1;
			qx = x2; qy = y2;
			ps = qs = slope(px, py, qx, qy);
		}
		
		// Calculate the slope of PQ
		double slope(int px, int py, int qx, int qy) {
			return (px == qx) ? 10000.0 : ((double) (py - qy)) 
				/ ((double) (px - qx));
		}

		// Ensures that p and q lie above R
		void above(int rx, int ry) {
			// p has slope absolutely bigger-or-equal than PR
			double slope = slope(px, py, rx, ry);
			ps = (Math.abs(slope) > Math.abs(ps) && py > ry
				&& slope * ps >= 0) ? slope : ps;

			// q has slope absolutely less-or-equal than QR
			slope = slope(qx, qy, rx, ry);
			qs = (Math.abs(slope) < Math.abs(qs) && qy < ry 
				&& slope * qs >= 0) ? slope : qs;
		}

		// Set midHandle in daughter to intersection of p and q
		public void setMidHandle(Node daughter) {
			// Ignore if no midHandle needed
			if (ps != qs) {
				// Middle point is intersection of two lines
				daughter.midHandle.x 
					= (int) (((qy-py)+(ps*px-qs*qx)) / (ps-qs));
				daughter.midHandle.y 
					= (int) (ps*(daughter.midHandle.x-px)+py);

				daughter.midHandle.x 
					= daughter.midHandle.x - daughter.xrel;
				daughter.midHandle.y 
					= daughter.midHandle.y - daughter.yrel;
			} 
		}
	}
	public String toString() {
		String str = "Node#" + number + avm.toString() + " --> {";
		int i;
		
		// Convert each daughter
		for (i = 0; i < daughters.size(); ++i) {
			str = str + daughterAt(i).toString();
			if (i != daughters.size() - 1) {
				str = str + ", ";
			}
		}

		return str + "}";
	}
	private int max(int a, int b) {
		return (a < b) ? b : a;
	}
	private int min(int a, int b) {
		return (a < b) ? a : b;
	}
}

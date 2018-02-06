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

public class TreeInfo implements Cloneable {
	// Data variables
	AVM options;						// AVM for uncompiled options

	// Options
	public boolean updateAll;			// Force update of everything

	// Layout variables
	transient Color currentColor;		// Current color

	// Compiled options
	transient Font font;				// Font
	transient FontMetrics fontMetrics;	// Font metrics
	transient int fontHeight;			// Font height
	transient int fontAscent;			// Font ascent
	transient int fontDescent;			// Font descent
	transient int fontLeading;			// Font leading

	transient int layoutStyle;			// Layout style
	transient double treeXR; 			// X-placement of small trees on canvas
	transient double treeYR; 			// Y-placement of small trees on canvas
	transient Color treeFG;				// Foreground color of tree
	transient Color treeBG;				// Background color of tree
	transient Color bonsaiFG;			// Foreground color of bonsai triangle
	transient Color bonsaiBG;			// Background color of bonsai triangle
	transient boolean PERMtooltip;		// Allow tooltip texts

	transient int treeDW;				// Tree margin (space around tree)
	transient int nodeDX;				// Horiz spacing between daughter nodes
	transient int nodeDY;				// Vert spacing between mother/daughter
	transient int nodeHNDL;				// Extra y-spacing for node handles
	transient int avmBW; 				// Horiz bracket width in AVM
	transient int avmDX; 				// Horiz space btw. bracket/attr/val
	transient int avmDAVM;				// Extra y-spacing for AVM values
	transient int bonsaiDX;				// Width of bonsai triangle
	transient int bonsaiDY;				// Height of bonsai triangle
	transient int nodeCDX;				// Extra x-spacing for corner handles
	transient int nodeCDY;				// Extra y-spacing for corner handles
	transient int nodeMDX;				// Extra x-spacing for corner mid handles

	// Uncompiled values 
	transient double DtreeDW, DnodeDX, DnodeDY, DnodeHNDL, DavmBW, DavmDX, 
		DavmDAVM, DbonsaiDX, DbonsaiDY, DnodeCDX, DnodeCDY, DnodeMDX;

	// Layout Styles.
	public final static int BOTTOMUP = 0;
	public final static int TOPDOWN = 1;
	public TreeInfo() {	
		// Create options AVM
		options = new AVM();
		options.up2date = false;

		// Standard settings
		options.setAV("macro.selectNode.nodeBG", Color.cyan);
		options.setAV("macro.selectAV.attrBG", Color.cyan);
		options.setAV("macro.selectAV.valBG", Color.cyan);
	}
	public Object clone() {
		TreeInfo clone;
		try { 
			clone = (TreeInfo) super.clone();

			// Clone options AVM
			clone.options = (AVM) options.clone();
		} catch (CloneNotSupportedException cnse) {
			System.err.println(cnse); 
			return null;
		}

		// Return clone
		return clone;
	}	
	void setFont(Graphics g) {
		// Calculate font parameters
		g.setFont(font);
		fontMetrics = g.getFontMetrics();
		fontHeight =  fontMetrics.getHeight();
		fontAscent =  fontMetrics.getAscent();
		fontDescent = fontMetrics.getDescent();
		fontLeading = fontMetrics.getLeading();

		// Set color
		currentColor = (treeFG != null) ? treeFG : Color.black;

		// Recalculate fontsize dependent parameters
		treeDW   = 1 + (int) (fontHeight * DtreeDW);
		nodeDX   = 1 + (int) (fontHeight * DnodeDX);
		nodeDY   = 1 + (int) (fontHeight * DnodeDY);
		nodeHNDL = 1 + (int) (fontHeight * DnodeHNDL);
		avmBW    = 1 + (int) (fontHeight * DavmBW);
		avmDX    = 1 + (int) (fontHeight * DavmDX);
		avmDAVM  = 1 + (int) (fontHeight * DavmDAVM);
		bonsaiDX = 1 + (int) (fontHeight * DbonsaiDX);
		bonsaiDY = 1 + (int) (fontHeight * DbonsaiDY);
		nodeCDX  = 1 + (int) (fontHeight * DnodeCDX);
		nodeCDY  = 1 + (int) (fontHeight * DnodeCDY);
		nodeMDX  = 1 + (int) (fontHeight * DnodeMDX);
	}
	boolean compileTreeInfo() {
		// Skip compilation if uptodate
		if (options.up2date && !updateAll)
			return false;
		
		// Compile options
		font = null;
		layoutStyle = BOTTOMUP;
		treeXR = treeYR = 0.5;
		treeFG = bonsaiFG = bonsaiBG = null;
		treeBG = Color.white;
		PERMtooltip = true;

		// Default values
		DtreeDW = 5.0;
		DnodeDX = 3.0;
		DnodeDY = 3.0;
		DnodeHNDL = 0.4;
		DavmBW = 0.25;
		DavmDX = 0.25;
		DavmDAVM = 0.2;
		DbonsaiDX = 2.0;
		DbonsaiDY = 1.0;
		DnodeCDX = 0.4;
		DnodeCDY = 0.4;
		DnodeMDX = 0.6;

		// Compile options in TreeInfo.options.tree
		compileTreeInfoOptions(options.getAV("tree"));

		// Set default font if null
		if (font == null)
			font = new Font("Helvetica", Font.PLAIN, 10);

		// Force update on all nodes and AVMs in the tree
		options.up2date = true;
		updateAll = true;

		// Return true
		return true;
	}
	void compileTreeInfoOptions(Object optionsObj) {
		String attr; 
		Object val;

		if (optionsObj instanceof AVM) {
			AVM opt = (AVM) optionsObj;

	        // Process each attribute in options
	        for (int i = 0; i < opt.size(); ++i) {
	            // Read attribute and value
	            attr = opt.attributeAt(i);
	            val = opt.valueAt(i);
	        
	            // Test for and process option
	            if (attr.charAt(0) == ':') {
	                // Option is a macro
	                compileTreeInfoOptions(options.getAV("macro."
						+attr.substring(1)));
	            } else if (val instanceof Double) {
					double d = ((Double) val).doubleValue();
					if (attr.equals("treeXR")) 
						treeXR = d;
					else if (attr.equals("treeYR"))
						treeYR = d;
					else if (attr.equals("treeDW"))
						DtreeDW = d;
					else if (attr.equals("nodeDX")) 
						DnodeDX = d;
					else if (attr.equals("nodeDY"))
						DnodeDY = d;
					else if (attr.equals("nodeHNDL"))
						DnodeHNDL = d;
					else if (attr.equals("avmBW"))
						DavmBW = d;
					else if (attr.equals("avmDX"))
						DavmDX = d;
					else if (attr.equals("avmDAVM")) 
						DavmDX = d;
					else if (attr.equals("bonsaiDX")) 
						DbonsaiDX = d;
					else if (attr.equals("bonsaiDY")) 
						DbonsaiDY = d;
					else if (attr.equals("nodeCDX")) 
						DnodeCDX = d;
					else if (attr.equals("nodeCDY")) 
						DnodeCDY = d;
					else if (attr.equals("nodeMDX")) 
						DnodeMDX = d;
				} else if (val instanceof Color) {
					if (attr.equals("treeFG"))
						treeFG = (Color) val;
					else if (attr.equals("treeBG"))
						treeBG = (Color) val;
					else if (attr.equals("bonsaiFG"))
						bonsaiFG = (Color) val;
					else if (attr.equals("bonsaiBG"))
						bonsaiBG = (Color) val;
				} else if (val instanceof Integer) {
					if (attr.equals("layoutStyle")) 
						layoutStyle = ((Integer) val).intValue();
				} else if (val instanceof Boolean) {
					if (attr.equals("PERMtooltip")) 
						PERMtooltip = ((Boolean) val).booleanValue();
				} else if (val instanceof Font) {
					if (attr.equals("font")) 
						font = (Font) val;
				}
			}
		}
	}
	public String expandMacro(String macro, AVM avm, String attr) {
		// Search for first occurrence of "${"
		int beginMacro = macro.indexOf("${"), endMacro;

		if (beginMacro == -1) 
			// Simple string
			return macro;
		else {
			// Macro string, find matching "}"
			endMacro = beginMacro + 2;
			int nest = 1;
			while (endMacro < macro.length() && nest > 0) {
				if (macro.charAt(endMacro) == '{') 
					++nest;
				else if (macro.charAt(endMacro) == '}')
					--nest;
				if (nest != 0)
					++endMacro;
			}

			// Return expanded macro
			if (nest != 0)
				// No matching "}"
				return macro;
			else {
				// Separate string = "first${middle}rest"
				String first = macro.substring(0, beginMacro);
				String path = expandMacro(macro.substring(beginMacro + 2, 
					endMacro), avm, attr); 
				String rest = expandMacro((endMacro == macro.length()) 
					? "" : macro.substring(endMacro + 1), avm, attr);
				
				// Find value of path
				Object value = (beginMacro + 2 == endMacro) 
					? attr : avm.getAV(path);
				if (value == null || value == "") 
					value = "?";
					
				return first + value.toString() + rest;
			}	
		}
	}
	public static String[] splitString(String string) {
		int c, i, j;
		String[] result;

		// How many newlines do we have?
		for (c = 0, i = -1; (i = string.indexOf("\n", i + 1)) != -1; ++c);
		
		// There were c+1 newlines: split into separate strings
		result = new String[c+1];
		for (c = 0, i = j = -1; (i = string.indexOf("\n", i + 1)) != -1; 
				++c, j = i) {
			result[c] = string.substring(j+1, i);
		} 
		result[c] = string.substring(j+1);

		// Return result
		return result;
	}		
	void drawBG(Graphics g, Color color, int x, int y, 
			int width, int height) {
		if (color != null) {
			g.setColor(color);
			g.fillRect(x, y, width, height);
			g.setColor(currentColor);
		}
	}
	Color switchColor(Graphics g, Color color) {
		Color old = currentColor;
		if (color != null) 
			g.setColor(currentColor = color);
		return old;
	} 
}

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

public class AVM implements Serializable, Cloneable {
	// Data variables
	Vector attributes;				// Attribute list
	Vector values;					// Value list

	// Layout variables
	transient Rectangle bbox;		// Bounding box
	transient int xref, yref;		// Reference point coordinates
	transient int maxw1, maxw2;		// Max total width of attrs and vals
	transient boolean up2date;		// Layout up-to-date?
	transient int[] xpoly, ypoly;	// 4-polygon x- and y- coordinates
	public AVM() {
		// Initialize data variables
		attributes = new Vector();
		values = new Vector();

		// Initialize layout variables
		bbox = new Rectangle();
		up2date = false;
		xpoly = new int[4]; 
		ypoly = new int[4]; 
	}
	public Object clone() {
		AVM clone = null;
		try {
			clone = (AVM) super.clone();

			// Clone attributes
			clone.attributes = new Vector(attributes.size());
			for (int i = 0; i < attributes.size(); ++i) 
				clone.attributes.addElement(attributeAt(i));
			
			// Clone values
			clone.values = new Vector(attributes.size());
			for (int i = 0; i < attributes.size(); ++i)
				clone.values.addElement(valueObjAt(i).clone());
			
			// Clone bbox, xpoly, ypoly
			clone.bbox = new Rectangle(bbox);
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
	// AVM manipulation (public)
	public boolean setAV(String path, Object value) {
		// Change permission must be true and path non-null
		if (path == null) 
			return false;
			
		// Indicate the layout is changed
		up2date = false;

		// Split path into base and rest, and search for base in AVM
		String base = pathBase(path);
		String rest = pathRest(path);
		int index = attributes.indexOf(base);

		// The path is either simple or composite
		if (rest == "") {
			// The path is simple
			if (index == -1) {
				// No base attribute exists. Create new value
				attributes.addElement(base);
				values.addElement(new Value(value));
			} else {
				// Base attribute exists already. Change value
				valueObjAt(index).value = value;
			}
		} else {
			// The path is composite
			AVM baseAVM;
			if (index == -1) {
				// No base attribute exists. Create new AVM
				baseAVM = new AVM();
				attributes.addElement(base);
				values.addElement(new Value(baseAVM));
			} else if (valueAt(index) instanceof AVM) {
				// Base attribute exists with AVM value
				baseAVM = (AVM) valueAt(index);
			} else {
				// Base attribute exists but not AVM, create new
				baseAVM = new AVM();
				valueObjAt(index).value = baseAVM;
			}

			// Set rest-path to value in baseAVM
			baseAVM.setAV(rest, value);
		}

		// Return true
		return true;
	}
	public boolean setAV(String AVlist) {
		// AVlist must be non-null
		if (AVlist == null)
			return false;
		boolean success = true;

		// Find index of "=" and following "|" in AVlist
		int i = AVlist.indexOf('=');
		int j = AVlist.indexOf('|', i);
		String attr, val;

		// Check that string is a non-empty AVlist
		if (i >= 0) {
			// Extract first attribute from AVlist
			attr = AVlist.substring(0, i).trim();

			if (j >= 0) {
				// AVlist contains several AV pairs
				val = AVlist.substring(i+1, j).trim();
				success &= setAV(attr, val);
				success &= setAV(AVlist.substring(j+1).trim());
			} else {
				// AVlist contains one AV pair
				val = AVlist.substring(i+1).trim();
				success &= setAV(attr, val);
			}
		}

		// Return
		return success;
	}
	public boolean setOption(String path, Object value) {
		return setAV(path, value);
	}
	public boolean removeAV(String path) {
		// Change permission must be true and path non-null
		if (path == null) 
			return false;

		// Indicate the layout is changed
		up2date = false;

		// Split path into base and rest, and search for base in AVM
		String base = pathBase(path);
		String rest = pathRest(path);
		int index = attributes.indexOf(base);

		if (rest == "" && index != -1) {
			// Path is simple and base is in AVM
			attributes.removeElementAt(index);
			values.removeElementAt(index);
			return true;
		} else if (index != -1 && valueAt(index) instanceof AVM) {
			// Path is composite and base has AVM value
			return ((AVM) valueAt(index)).removeAV(rest);
		} else {
			// Path does not exist
			return false;
		}
	}
	public boolean removeOption(String path) {
		return removeAV(path);
	}
	public Object getAV(String path) {
		// Return null if path null
		if (path == null)
			return null;

		// Split path into base and rest, and search for base in AVM
		String base = pathBase(path);
		String rest = pathRest(path);
		int index = attributes.indexOf(base);

		if (rest == "" && index != -1) {
			// path is simple and base attribute exists
			return valueAt(index);
		} else if (index != -1 && valueAt(index) instanceof AVM) {
			// path is composite and base attribute has AVM value
			return ((AVM) valueAt(index)).getAV(rest);
		} else {
			// path does not exist
			return null;
		}
	}
	public Object getOption(String path) {
		return getAV(path); 
	} 
	public Value getValue(String path) {
		// Return null if path null
		if (path == null)
			return null;

		// Split path into initial path and last attribute
		int index = path.lastIndexOf('.');
		String attr = (index == -1) ? path : path.substring(index + 1);
		String ipath = (index == -1) ? null : path.substring(0, index);

		// Find AVM presumably containing last attribute
		Object obj = (ipath == null) ? this : getAV(ipath);

		// Return Value object if found object is an AVM containing attr
		if (obj instanceof AVM 
				&& 0 <= (index = ((AVM) obj).attributes.indexOf(attr))) 
			return (Value) ((AVM) obj).values.elementAt(index);

		// Something went wrong, return null
		return null;
	}
	public String[][] getAVM() {
		String[][] AVMstring;
		int size = 0;

		// Calculate size of AVM (without options)
		for (int i = 0; i < attributes.size(); ++i)	
			if (attributeAt(i).charAt(0) != '@' ) 
				++size; 

		// Create new AVM string
		AVMstring = new String[size][2];

		// Save all non-option AV pairs in AVM string
		for (int i = 0, j = 0; i < attributes.size(); ++i) 
			if (attributeAt(i).charAt(0) != '@' ) {
				AVMstring[j][0] = attributeAt(i);
				AVMstring[j++][1] = valueAt(i).toString();
			}

		// Return AVM string
		return AVMstring;
	}

	// AVM manipulation (low level)
	public int size() {
		return attributes.size();
	}
	public String attributeAt(int i) {
		return (String) attributes.elementAt(i);
	} 
	public Object valueAt(int i) {
		return ((Value) values.elementAt(i)).value;
	}
	private Value valueObjAt(int i) {
		return (Value) values.elementAt(i);
	}
	public void drawAVM(Graphics g, TreeInfo info, String[] abbrStrings, 
			int x, int y) {

		// Variables
		Color color;
		String str;
		int ypos = 0;

		// Reference point
		xref = x;
		yref = y;

		// Abbreviate AVM?
		if (abbrStrings != null) {
			// Draw AVM in abbreviated form
			for (int i = 0; i < abbrStrings.length; ++i) {
				if ((str = abbrStrings[i]) != null) 
					g.drawString(str, 
						x - info.fontMetrics.stringWidth(str) / 2,
						y + ypos);
				ypos += info.fontHeight;
			}
		} else {
			// Draw AVM in full
			
			// Calculate xref of attributes and values
			int x1 = xref + info.avmDX;
			int x2 = xref + 3 * info.avmDX + maxw1;

			// Draw visible attributes one by one
			for (int i = 0; i < attributes.size(); ++i) {
				Value valueobj = valueObjAt(i);
				if (valueobj.visible) {
					// Calculate y-coordinate of attribute and value
					ypos = yref + valueobj.yref;

					// Draw attribute background 
					info.drawBG(g, valueobj.attrBG, x1 - info.avmDX, 
						ypos - info.fontAscent - 1, maxw1 + 2 * info.avmDX, 
						info.fontAscent + info.fontDescent + 1);

					// Draw attribute foreground 
					color = info.switchColor(g, valueobj.attrFG);
					if ((str = attributeAt(i)) != null)
						g.drawString(str, x1, ypos);
					info.switchColor(g, color);

					// Draw AVM or non-AVM value
					if (valueobj.value instanceof AVM) {
						// Value is an AVM
						AVM valueavm = (AVM) valueobj.value;

						// Draw AVM value background
						info.drawBG(g, valueobj.valBG, x2 + valueavm.bbox.x 
							- info.avmDX, ypos + valueavm.bbox.y, maxw2 + 1 
							+ 2 * info.avmDX, valueavm.bbox.height + 1);

						// Draw AVM value foreground
						color = info.switchColor(g, valueobj.valFG);
						valueavm.drawAVM(g, info, null, x2 - valueavm.bbox.x, ypos);
						info.switchColor(g, color);
					} else {
						// Draw non-AVM value background
						info.drawBG(g, valueobj.valBG, x2 - info.avmDX, 
							ypos - info.fontAscent - 1, maxw2 + 2 * info.avmDX, 
							info.fontAscent + info.fontDescent + 1);

						// Draw non-AVM value foreground if value non-null
						if (valueobj.value != null) {
							color = info.switchColor(g, valueobj.valFG);
							if (valueobj.value instanceof String) 
								g.drawString((String) valueobj.value, x2, ypos);
							info.switchColor(g, color);
						}
					}
				}
			}

			// Calculate and draw left and right bracket
			xpoly[0] = x1 - 1;		ypoly[0] = yref + bbox.y; 
			xpoly[1] = xref; 		ypoly[1] = ypoly[0]; 
			xpoly[2] = xpoly[1];	ypoly[2] = yref + bbox.y + bbox.height; 
			xpoly[3] = xpoly[0]; 	ypoly[3] = ypoly[2];
			g.drawPolyline(xpoly, ypoly, 4);

			xpoly[1] = xref + bbox.width; 
			xpoly[0] = xpoly[1] - info.avmDX + 1; 
			xpoly[2] = xpoly[1]; 
			xpoly[3] = xpoly[0];
			g.drawPolyline(xpoly, ypoly, 4);
		}
	}
	public boolean layoutAVM(TreeInfo info, String[] abbrStrings) {
		// Variables
		int ypos = 1;

		// Return if up2date and update is not forced
		if (up2date && info.updateAll == false) 
			return false;
		else 
			up2date = true;

		// Reset maxw1 and maxw2
		maxw1 = maxw2 = 0;

		// Full AVM: compile options for all attributes
		for (int i = 0; i < attributes.size(); ++i) 
			valueObjAt(i).compileAV(info, this, attributeAt(i));

		// Calculate maximal width of visible attributes.
		for (int i = 0; i < attributes.size(); ++i) 
			if (valueObjAt(i).visible) 
				maxw1 = max(maxw1,info.fontMetrics.stringWidth(attributeAt(i)));
			
		// Position each visible AV pair.
		for (int i = 0; i < attributes.size(); ++i) {
			Value valueobj = valueObjAt(i);
			if (valueobj.visible) {
				// AV pair visible
				if (valueobj.value instanceof AVM) {
					// Value is an AVM
					AVM valueavm = (AVM) valueobj.value;

					// Calculate layout of value
					valueavm.layoutAVM(info, null);

					// Calculate y-coordinate of AV pair's reference point
					ypos += info.avmDAVM;
					valueobj.yref = ypos;
					ypos += valueavm.bbox.height + valueavm.bbox.y 
							+ info.fontHeight - info.fontDescent 
							+ 2 * info.fontLeading;

					// Calculate max width of values
					maxw2 = max(maxw2, valueavm.bbox.width);
				} else {
					// Value is not an AVM.

					// Calculate y-coordinate of AV pair's reference point
					valueobj.yref = ypos;
					ypos += info.fontHeight;

					// Calculate max width of values
					maxw2 = max(maxw2, 
						info.fontMetrics.stringWidth((String) valueobj.value));
				}
			}
		}

		// Calculate new bounding box of AVM
		bbox.x = 0;
		bbox.y = - info.fontAscent - 1;
		bbox.width = maxw1 + maxw2 + 4 * info.avmDX;
		bbox.height = max(ypos - bbox.y - info.fontHeight + info.fontDescent, 
			info.fontAscent);

		// Abbreviate AVM?
		if (abbrStrings != null) {
			// Abbreviated AVM
			maxw1 = 0;
			for (int i = 0; i < abbrStrings.length; ++i)
				maxw1 = max(maxw1, info.fontMetrics.stringWidth(abbrStrings[i]));

			bbox.x = - maxw1 / 2 - info.avmDX;
			bbox.y = - info.fontAscent;
			bbox.width  = maxw1 + 2 * info.avmDX;
			bbox.height = (abbrStrings.length - 1) * info.fontHeight 
				- bbox.y + info.fontDescent;
		}

		// Return
		return true;
	}
	String pathAtXY(TreeInfo info, int x, int y) {
		// Return with null if the point is outside this AVM
		if (! bbox.contains(x-xref, y-yref)) 
			return null;

		// Variables
		Value valueobj;				// The value being examined
		int avpair = -1;			// Last visible AV pair
		int ypos = yref + bbox.y;	// Bottom y-coordinate of avpair

		// Find the visible AV pair that contains the point
		for (int i = 0; ypos < y && i < attributes.size(); ++i) {
			// Skip invisible AV pairs
			for (; i < attributes.size() && !valueObjAt(i).visible; ++i);
			
			// Process next visible AV pair, if there is one
			if (i < attributes.size()) {
				ypos = yref + valueObjAt(i).yref - info.fontAscent; 
				if (ypos < y)
					avpair = i;
			}
		}

		// Return the path of AV pair containing point
		if (avpair >= 0 && avpair < attributes.size()) {
			// The AV pair avpair contained the point
			valueobj = valueObjAt(avpair);

			// Find subpath
			if (x > xref + 3*info.avmDX + maxw1 && valueobj.value instanceof AVM) {
				// Value is an AVM, find restpath
				String rest = ((AVM) valueobj.value).pathAtXY(info, x, y);
					
				// The point was within AVM value?
				if (rest != null) 
					return makePath(attributeAt(avpair), rest); 
			} 

			// The point was not within AVM value
			return attributeAt(avpair);
		}

		// No AV pair contained point; return null
		return null;
	}
	String makePath(String base, String attr) {
		if (base == "") {	
			return attr;
		} else {
			return base + "." + attr;
		}
	}
	private String pathBase(String path) {
		int index = path.indexOf('.');
		if (index == -1) {
			return path;
		} else {
			return path.substring(0, index);
		}
	}
	private String pathRest(String path) {
		int index = path.indexOf('.');
		if (index == -1) {
			return "";
		} else {
			return path.substring(index + 1);
		}
	}
	public static String optionPath(String path) {
		// Return null if path or option null
		if (path == null)
			return null;

		// Else return the corresponding option path
		int index = path.lastIndexOf('.');
		if (path == "") {
			return "@@node";
		} else if (index == -1) {
			return "@" + path;
		} else {
			return path.substring(0, index+1) + "@" 
				+ path.substring(index+1);
		}
	}
	public String toString() {
		String str = "[";

		// Add each AV pair to the string
		for (int i = 0; i < attributes.size(); ++i) {
			Object value = valueAt(i);
			if (value == null) {
				value = "null";
			}
			str = str + attributeAt(i).toString() + "=" 
					+ value.toString();
			if (i != attributes.size() - 1) {		
				str = str + ", ";
			}
		}
		return str + "]";
	}
	private int max(int a, int b) {
		return (a > b) ? a : b;
	}
}

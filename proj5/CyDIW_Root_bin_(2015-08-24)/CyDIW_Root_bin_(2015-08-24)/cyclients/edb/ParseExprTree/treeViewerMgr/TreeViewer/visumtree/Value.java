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

class Value implements Cloneable {
	// Data variables
	Object value;						// Associated value of value object

	// Compiled options
	transient Color attrFG;				// Attribute's foreground color
	transient Color attrBG;				// Attribute's background color
	transient Color valFG;				// Value's foreground color
	transient Color valBG;				// Value's foreground color
	transient boolean visible;			// AV-pair visible?
	transient String tooltip;			// Tooltip text for AV pair
	transient String tooltipString;		// Compiled tooltip text for AV pair

	// Layout variables
	transient int yref; 				// Reference point's y-coordinate
	public Value(Object value) {
		this.value = value;
	}
	public Object clone() {
		Value clone;
		try {
			clone = (Value) super.clone();

			// Clone value
			if (clone.value instanceof AVM)
				clone.value = ((AVM) value).clone();
		} catch (CloneNotSupportedException cnse) {
			System.err.println(cnse);
			return null;
		}

		// Return clone
		return clone;
	}
	void compileAV(TreeInfo info, AVM avm, String attr) {
		if (value == null || attr.charAt(0) == '@') {
			// Value is null or attr is an option
			visible = false;
		} else {
			// Value is non-null, attr not an option

			// Compute string representation of value
			String val = value.toString();

			// Set compiled options to default values
			attrFG = null;
			attrBG = null;
			valFG = null;
			valBG = null;
			visible = true;
			tooltip = null;

			// Compile options from avm, avm.@attr, avm.@attr=value in info.options
			compileAVoptions(info, avm, info.options.getAV("avm"));
			compileAVoptions(info, avm, info.options.getAV("avm.@"+attr));
			compileAVoptions(info, avm, info.options.getAV("avm.@"+attr+"="+val));

			// Compile options @attr and @attr=value in avm
			compileAVoptions(info, avm, avm.getAV("@"+attr));
			compileAVoptions(info, avm, avm.getAV("@"+attr+"="+val));

			// Compile tooltip string
			tooltipString = info.expandMacro(tooltip, avm, attr);
		}
	}
	void compileAVoptions(TreeInfo info, AVM avm, Object optionsObj) {
		String attr;
		Object val;

		// optionsObj must be non-null AVM
		if (optionsObj instanceof AVM) {
			AVM options = (AVM) optionsObj;

			// Process each attribute in options
			for (int i = 0; i < options.size(); ++i) {
				// Read attribute and value
				attr = options.attributeAt(i);
				val = options.valueAt(i);
			
				// Test for and process option
				if (attr.charAt(0) == ':') {
					// Option is a macro
					compileAVoptions(info, avm, 
						info.options.getAV("macro."+attr.substring(1)));
				} else if (val instanceof Color || val == null) {
					// Option value is a color
					if (attr.equals("attrFG")) {
						attrFG = (Color) val;
					} else if (attr.equals("attrBG")) {
						attrBG = (Color) val;
					} else if (attr.equals("valFG")) {
						valFG = (Color) val;
					} else if (attr.equals("valBG")) {
						valBG = (Color) val;
					}
				} else if (val instanceof Boolean) {
					if (attr.equals("visible"))
						visible = ((Boolean) val).booleanValue();
				} else if (val instanceof String || val == null) {
					if (attr.equals("tooltip")) 
						tooltip = (String) val;
				}
			}
		}
	}
}

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

public class TreeEvent {
	public int type;
	public int status;

	// Tree events
	public static final int SELECT = 0;
	public static final int NEWDAUGHTER = 1;
	public static final int NEWMOTHER = 2;
	public static final int MOVE = 3;
	public static final int REORDER = 4;
	public static final int REMOVE = 5;
	public static final int CHANGE = 6;
	public static final int LAYOUT = 7;

	// Return status
	public static final int SUCCESS = 0;
	public static final int FAIL = 1;

	// Deprecated
	public static final int ADDDAUGHTER = 1;
	public static final int ADDMOTHER = 2;
	TreeEvent(int type, int status) {
		this.type = type;
		this.status = status;
	}
	TreeEvent(int type, boolean success) {
		this(type, success ? SUCCESS : FAIL);
	}
	TreeEvent(int type) {
		this(type, SUCCESS);
	}
	public int type() {
		return type;
	}
	public int status() {
		return status;
	}
	public String toString() {
		switch(type) {
			case SELECT: return "Select"; 
			case NEWDAUGHTER: return "New daughter"; 
			case NEWMOTHER: return "New mother"; 
			case MOVE: return "Move"; 
			case REORDER: return "Reorder"; 
			case REMOVE: return "Remove"; 
			case CHANGE: return "Change";
			case LAYOUT: return "Layout";
			default: return "?";
		}
	}
}

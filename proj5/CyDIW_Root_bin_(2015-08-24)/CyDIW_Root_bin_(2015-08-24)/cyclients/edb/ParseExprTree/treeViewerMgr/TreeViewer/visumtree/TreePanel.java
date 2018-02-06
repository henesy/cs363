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
import javax.swing.*;

public class TreePanel extends JScrollPane{
	// Copyright
	private String copyright = 
		"VisumTree 1.1 (Sep 9 1998) is freeware. Please read the license.\n"+
		"Copyright (c) 1998 Matthias T. Kromann <mtkromann@member.ams.org>.\n";

	// Tree
	private Tree tree;
	public TreePanel() {
		//super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		//ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		super();
		setBackground(Color.white);
		tree = new Tree();
		getViewport().add(tree);
	}
	public TreePanel(Tree t) {
		//super(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
		//	ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		super();
		setBackground(Color.white);
		tree = t;
		getViewport().add(tree);
	}
	public Tree tree() {
		return tree;
	}
	public void scrollSelectedToVisible() {
		Node[] selections = tree.getSelectedNodes();
		if (selections != null && selections.length > 0) {
			// Update tree layout
			if (tree.getGraphics() != null) 
				tree.layoutTree(tree.getGraphics());

			// Find the smallest bounding box containing all selections
			Rectangle selBox = selections[0].getBBox();
			for (int i = 1, max = selections.length; i < max; i++)
				selBox = selBox.union(selections[i].getBBox());

			// Find viewport
			JViewport vp = getViewport();
			Rectangle viewRect = vp.getViewRect();
			Point maxPoint = new Point(selBox.x+selBox.width, 
				selBox.y+selBox.height);

			// check to see if scrolling is needed,
			// i.e. if selBox is contained in the current view rectangle
			if (!viewRect.contains(selBox.getLocation())  ||
					!viewRect.contains(maxPoint)) {
				// scroll to make selections visible
				// first scroll to position 0 if left-scroll or up-scroll
				// is needed; otherwise the scrolling doesn't work well
				int xPos = (selBox.x<viewRect.x) ? 0 : viewRect.x;
				int yPos = (selBox.y<viewRect.y) ? 0 : viewRect.y;
				if (xPos==0  ||  yPos==0)
					vp.setViewPosition(new Point(xPos,yPos));

				vp.scrollRectToVisible(selBox);
			}
		}
	}
	
            	
}

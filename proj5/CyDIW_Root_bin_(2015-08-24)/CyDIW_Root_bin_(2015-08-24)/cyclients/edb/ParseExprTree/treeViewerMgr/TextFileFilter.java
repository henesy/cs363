/*
 * Authors:				Srikanth Krithivasan.
 *						Jose M Reyers Alamo.
 * Date of Creation:	November 07, 2004.
 * Course No:           CS 562
 * Course Name:         Implementation of Database Systems.
 * Instructor:          Dr. Shashi K. Gadia
 */

package cyclients.edb.ParseExprTree.treeViewerMgr;

import java.io.File;
import javax.swing.filechooser.*;
import java.util.*;

public class TextFileFilter extends FileFilter {
	
	String description;
	Vector exts = new Vector();

	public static String getExtension(File f) {
		String ext = null;
		String s = f.getName();
		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {
			ext = s.substring(i + 1).toLowerCase();
		}
		return ext;
	}

	//Accept all directories and all txt, xq files.
	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}

		String extension = getExtension(f);
		if (extension != null) {
			if (match(extension))
				return true;
		} else {
			return false;
		}
		return false;
	}
	
	public void addFilter(String s) {
		exts.add(s);
	}
	
	private boolean match(String s) {
		for(int i=0;i<exts.size();i++) {
			if(s.equals((String)exts.elementAt(i)))
				return true;						
		}
		return false;
	}

	//The description of this filter
	public String getDescription() {
		return description;
	}
	/**
	 * @param string
	 */
	public void setDescription(String string) {
		description = string;
	}

}
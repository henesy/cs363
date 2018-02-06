/*
 * Authors:				Srikanth Krithivasan.
 *						Jose M Reyers Alamo.
 * Date of Creation:	November 07, 2004.
 * Course No:           CS 562
 * Course Name:         Implementation of Database Systems.
 * Instructor:          Dr. Shashi K. Gadia
 */
package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.global;
/*
*/

import java.io.*;

public class Define {
    
    
    /* file name for xml file used in parsed tree and xml view  */
    private static final String iconPath = "./icon/";   

        
    public static String getAbsFileName(String fname)
    {
        String absfname = null;
        File f = new File(fname);
        f = f.getAbsoluteFile();
        absfname = f.toString();
        return absfname;
        
    }

    
    /**
    * get the file name with relative path
    * @param fname  file name of image file
    *
    * @return   path name with file name of the image
    */
    public static String getIconPath(String fname)
    {
        return iconPath+fname;
    }
    
    
}

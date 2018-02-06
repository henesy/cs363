package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer;

import java.io.File;

import speed.util.ServiceException;

public class HybridView {

	public HybridView(File f) throws ServiceException{
		new XmlTreeView(f);
	}
}

/*
 * Authors:				Srikanth Krithivasan.
 *						Jose M Reyers Alamo.
 * Date of Creation:	November 07, 2004.
 * Course No:           CS 562
 * Course Name:         Implementation of Database Systems.
 * Instructor:          Dr. Shashi K. Gadia
 */

package cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import org.w3c.dom.*;

import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.global.*;
import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.*;

import javax.xml.parsers.*;

import java.io.File;


import java.util.*;

public class ExpTreeViewer extends JFrame implements TreeListener, ActionListener, KeyListener
{
    public ExpTreeViewer(String fname)
    {
        xmlFile = fname;

	    treePopup = new JPopupMenu();
        treeIncreaseFontItem = new JMenuItem("Increase Font", new ImageIcon(Define.getIconPath("ball.gif")));
        treeDecreaseFontItem = new JMenuItem("Decrease Font", new ImageIcon(Define.getIconPath("ball.gif")));
        treeTopdownItem = new JMenuItem("Topdown", new ImageIcon(Define.getIconPath("ball.gif")));
        treeBottomupItem = new JMenuItem("Bottomup", new ImageIcon(Define.getIconPath("ball.gif")));
        treeIncreaseFontItem.addActionListener(this);
        treeDecreaseFontItem.addActionListener(this);
        treeTopdownItem.addActionListener(this);
        treeBottomupItem.addActionListener(this);
        treePopup.add(treeIncreaseFontItem);
        treePopup.add(treeDecreaseFontItem);
        treePopup.add(treeTopdownItem);
        treePopup.add(treeBottomupItem);

		genExpTreePanel();
		setSize(700, 600);
		setTitle("Expression Tree Viewer");
		getContentPane().add(treePanel, "Center");
		addKeyListener(this);
        addWindowListener(new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        setVisible(false);
                    }
                });
    }

	/**
	* generating a tree panel for visum tree
	*/
	private void genExpTreePanel()
	{
		treePanel = new TreePanel();
		tree = treePanel.tree();
        Border treeEtched = BorderFactory.createEtchedBorder();
        treePanel.setBorder(treeEtched);

		// Set tree listener
		tree.addTreeListener(this);

		// Tree
		treePanel.getHorizontalScrollBar().setUnitIncrement(20);
		treePanel.getHorizontalScrollBar().setBlockIncrement(20);
		treePanel.getVerticalScrollBar().setUnitIncrement(20);
		treePanel.getVerticalScrollBar().setBlockIncrement(20);

		// Setup Tree and TreeInfo
		initParseTree();

		// Fonts
		font = new Font[10];
		for(int i = 0; i < 10; i++)
		{
    		font[i] = new Font("Helvetica", Font.PLAIN, i*2+10);
    	}
	}
	
	/**
    * adding a root node to parsedTree
	*/
    public void initParseTree()
    {
        //System.out.println("Initializing parse tree.....");
   	    
		// Remove all nodes from tree
		tree.resetNodes();
		tree.resetInfo();

		// Common TreeInfo settings
		tree.setOption("tree.layoutStyle", new Integer(TreeInfo.TOPDOWN));
		tree.setOption("tree.treeMW", new Double(TREE_MW));
		tree.setOption("tree.nodeDX", new Double(NODE_DX));
		tree.setOption("tree.nodeMDX", new Double(NODE_MDX));
		tree.setOption("tree.bosaniDX", new Double(BOSANI_DX));
		tree.setOption("tree.bosaniDY", new Double(BOSANI_DY));

		tree.setOption("avm.tooltip", "${} is ${${}}");
		tree.setOption("macro.selectAV.attrBG", Color.yellow);
		tree.setOption("macro.selectAV.valBG", Color.yellow);
		tree.setOption("macro.selectNode.nodeBG", Color.cyan);
		
   
   		// TreeInfo settings
		tree.setOption("node.abbrMacro", "${cat}");
		tree.setOption("node.PERMndaughter", Boolean.FALSE);
		tree.setOption("node.PERMterminal", Boolean.FALSE);

		// TreeInfo macro for terminal nodes, overriding default
		tree.setOption("macro.terminal.abbrMacro", "${lex}");
		tree.setOption("macro.terminal.PERMchange", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMremove", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreceive", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMreorder", Boolean.FALSE);
		tree.setOption("macro.terminal.PERMterminal", Boolean.TRUE);
		tree.setOption("macro.terminal.PERMabbreviate", Boolean.FALSE);
		tree.setOption("macro.terminal.abbreviate", Boolean.TRUE);

		// Now force abbreviation of all nodes
		tree.setOption("node.PERMabbreviate", Boolean.FALSE);
		tree.setOption("node.abbreviate", Boolean.TRUE);

        //currentNode = new Node();
        currentNode = (cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node)tree.root();
		//tree.root().newDaughter(Snode = new Node("cat=s"));
		addingMouseListener();
		showTree();

        //System.out.println("Parse Tree initialization done!!");
	}

    private void addingMouseListener()
    {
		MouseListener mlistener = new MouseAdapter() 
		{
  			public void mousePressed(MouseEvent e) 
  			{
  			    /* this code is to solve the the problem of getting the catch
  			       when a user clicks arrows to scroll windows. In JPanel,
  			       KeyListener does not work, so we get a focus when a user
  			       click a window.
  			    */
  			       
                requestFocus();
			}

		    public void mouseReleased(MouseEvent e)
		    {
			    if(e.isPopupTrigger())
   			    {
    			    treePopup.show(e.getComponent(), e.getX(), e.getY());
	        	}
	        }    	
       	};
   		
	 	tree.addMouseListener(mlistener);
    }

    public void keyPressed(KeyEvent e) 
   	{
   	    if(e.getKeyCode() == KeyEvent.VK_LEFT)
   		{
   		    int min = treePanel.getHorizontalScrollBar().getMinimum();
   		    int cur = treePanel.getHorizontalScrollBar().getValue() - 
   		              treePanel.getHorizontalScrollBar().getUnitIncrement() ;
   		    if(cur <= min)
   		        treePanel.getHorizontalScrollBar().setValue(min);
   		    else
   		        treePanel.getHorizontalScrollBar().setValue(cur);
   		}
   		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
   		{
   		    int max = treePanel.getHorizontalScrollBar().getMaximum();
   		    int cur = treePanel.getHorizontalScrollBar().getValue() +
   		              treePanel.getHorizontalScrollBar().getUnitIncrement() ;
   		    if(cur >= max)
   		        treePanel.getHorizontalScrollBar().setValue(max);
   		    else
   		        treePanel.getHorizontalScrollBar().setValue(cur);
   		}
   		else if(e.getKeyCode() == KeyEvent.VK_UP)
   		{
   		    int min = treePanel.getVerticalScrollBar().getMinimum();
   		    int cur = treePanel.getVerticalScrollBar().getValue() -
   		              treePanel.getVerticalScrollBar().getUnitIncrement() ;
   		    if(cur <= min)
   		        treePanel.getVerticalScrollBar().setValue(min);
   		    else
   		        treePanel.getVerticalScrollBar().setValue(cur);
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN)
        {
   		    int max = treePanel.getVerticalScrollBar().getMaximum();
   		    int cur = treePanel.getVerticalScrollBar().getValue() +
   		              treePanel.getVerticalScrollBar().getUnitIncrement() ;
   		    if(cur >= max)
   		        treePanel.getVerticalScrollBar().setValue(max);
   		    else
   		        treePanel.getVerticalScrollBar().setValue(cur);
        }
   		        
    }
   	public void keyReleased(KeyEvent e) 
   	{
   	}
   	public void keyTyped(KeyEvent e) 
    {
    }
                
	public void treeActionPerformed(TreeEvent e) 
	{
		if (e.type() == TreeEvent.SELECT) 
		{
		}
	}

    
	/**   
	* function name : actionPerformed
	* @param eve action event
	*/
    public void actionPerformed(ActionEvent evt)
    {
        Object source = evt.getSource();

        if(source == treeIncreaseFontItem)
        {
			currentFont = (currentFont + 1) % font.length;
			tree.setOption("tree.font", font[currentFont]);
        }
        else if(source == treeDecreaseFontItem)
        {
            if((currentFont - 1) < 0)
                return;
			currentFont = (currentFont - 1) % font.length;
			tree.setOption("tree.font", font[currentFont]);
        }
        else if(source == treeTopdownItem)
        {
            if(curLayout)
            {
                return;
            }
            else
            {
                tree.setOption("tree.layoutStyle", new Integer(TreeInfo.TOPDOWN));
                curLayout = true; // topdown
            }
        }
        else if(source == treeBottomupItem)
        {
            if(curLayout)
            {
                tree.setOption("tree.layoutStyle", new Integer(TreeInfo.BOTTOMUP));
                curLayout = false; // bottom up
            }
            else
            {
            }
        }
    }
	

    private void showTree()
    {
		doc = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new File(xmlFile));
			root = (org.w3c.dom.Node)doc.getDocumentElement();
			//System.out.println(root.getNodeName());
			clean(root);
			traversing(root);
		}
		catch (Exception e)
		{
		    System.out.println("Loading "+xmlFile+" failed\n"+e);
		}
    }

    /**
     * This is to print all elements after cleaning text nodes.
     * @param node
     */
    private void travel(org.w3c.dom.Node node)
    {
        System.out.println(node.getNodeName());
        if(node.hasChildNodes() == false)
        {
            return;
        }
        
        NodeList nl = node.getChildNodes();
        for(int i = 0; i < nl.getLength(); i++)
        {
            org.w3c.dom.Node childNode = nl.item(i);
            travel(childNode);
        }
        
        
    }
    private void clean(org.w3c.dom.Node node)
    {
        if(node.getNodeType() == org.w3c.dom.Node.TEXT_NODE)
        {
            node.getParentNode().removeChild(node);
            return;
        }
        if(node.hasChildNodes() == false)
        {
            return;
        }
        
        org.w3c.dom.Node childNode = node.getFirstChild();
        while(childNode != null)
        {
            org.w3c.dom.Node nextNode = childNode.getNextSibling();
            clean(childNode);
            childNode = nextNode;
        }
    }
    
    private void traversing(org.w3c.dom.Node node)
    {
	    if(node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE)
	    {
	        String nodeName = node.getNodeName().trim();
	        if(nodeName.equals("dbRelExp"))
	        {
	            Element e = (Element)node;
	            String expType = e.getAttribute("dbRelExpType").trim(); 
	            if(expType.equals("dbProjection"))
	                processProjection(node);
	            else if (expType.equals("dbSelection"))
	                processSelection(node);
	            else if (expType.equals("dbNaturaljoin"))
	                processJoin(node);
	            
	            String expRelType = e.getAttribute("dbExpType").trim();
	            if(expRelType.equals("dbRelation"))
	                processRel(e.getAttribute("dbRelName").trim());
	            
	        }
	    }
	    
        if(node.hasChildNodes() == false)
        {
            return;
        }
        
        NodeList nl = node.getChildNodes();

        for(int i = 0; i < nl.getLength(); i++)
        {
            org.w3c.dom.Node childNode = nl.item(i);
            traversing(childNode);
        }
        
    }
   
    private void processRel(String relName)
    {
        
        cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node newNode = new cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node("cat="+relName);
		currentNode.newDaughter(newNode);
		currentNode = newNode;
        
    }
    private void processProjection(org.w3c.dom.Node node)
    {
        
        org.w3c.dom.Node attrListNode = node.getLastChild();
        Vector attrVec = processDBAttrList(attrListNode);

        String attrList = "";
        for(int i = 0; i < attrVec.size(); i++)
        {
            if(i == 0)
                attrList = (String)attrVec.elementAt(0);
            else
                attrList += ", "+(String)attrVec.elementAt(i);
        }
        String nodeValue = "";
        if(attrList.equals("*") || attrVec.size() == 1)
            nodeValue = "cat="+"\u03C0 "+attrList;
        else
            nodeValue = "cat="+"\u03C0\n"+attrList;
        
        cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node newNode = new cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node(nodeValue);
        if(jcount == 0)
        {
            currentNode.newDaughter(newNode);
            currentNode = newNode;
        }
        else if(jcount == 2)
        {
            currentNode.newDaughter(newNode);
            currentNode = newNode;
            --jcount;
        }
        else if (jcount == 1)
        {
            currentNode = joinNode;
            currentNode.newDaughter(newNode);
            currentNode = newNode;
            jcount = 0;
        }
        
    }
    private void processSelection(org.w3c.dom.Node node)
    {
        String con = boolExp(node.getLastChild());
        String nodeValue = "";
        if(con.equals("true"))
            nodeValue = "cat="+"\u03C3 "+con;
        else
            nodeValue = "cat="+"\u03C3\n"+con;
        cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node newNode = new cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node(nodeValue);
		currentNode.newDaughter(newNode);
		currentNode = newNode;
        
    }
    private String boolExp(org.w3c.dom.Node node)
    {
        String con = "";
        /**
         * if there is no child nodes, there is no conditions for
         * the selection.
         */
        if(!node.hasChildNodes())
            return con="true";
        
        org.w3c.dom.Node cNode = node.getFirstChild(); 
        con = processBoolFactor(cNode);
        
        if(cNode.getNextSibling() != null)
        {
            Element e = (Element)cNode.getNextSibling();
            con += " "+(String)e.getAttribute("connectiveType").trim()+" ";
            con += processBoolFactor(node.getLastChild());
        }

        return con;
    }
    private String processBoolFactor(org.w3c.dom.Node node)
    {
        String con = "";
        Element type = (Element)node;
        String ftype = type.getAttribute("booleanFactorType").trim();
        if(ftype.equals("attrOpConst"))
        {
            con = processAttrOpConst(node);
        }
        return con;
    }
    
    private String processAttrOpConst(org.w3c.dom.Node node)
    {
        String con = "";
        org.w3c.dom.Node attrNode = node.getFirstChild();
        Element attrEle = (Element)attrNode;
        con += "("+attrEle.getAttribute("dbRelName")+"."+
        		  attrEle.getAttribute("dbAttrName");
        
        org.w3c.dom.Node opNode = attrNode.getNextSibling();
        Element opEle = (Element)opNode;
        con += opEle.getAttribute("opType");
        
        org.w3c.dom.Node conNode = node.getLastChild();
        Element conEle = (Element)conNode;
        con += conEle.getAttribute("constValue")+")";
        
        return con;
    }
    private void processJoin(org.w3c.dom.Node node)
    {
        org.w3c.dom.Node attrListNode = node.getLastChild();
        Vector attrVec = processDBAttrList(attrListNode);
        
        String attrList = "";
        for(int i = 0; i < attrVec.size(); i++)
        {
            if(i == 0)
                attrList = (String)attrVec.elementAt(0);
            else
                attrList += "="+(String)attrVec.elementAt(i);
        }

        cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node newNode = new cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node("cat="+"\u22C8\n"+attrList);
		currentNode.newDaughter(newNode);
		currentNode = joinNode = newNode;
		jcount = 2;
    }
    
    private Vector processDBAttrList(org.w3c.dom.Node node)
    {
        Vector list = new Vector();
        Element e = (Element)node;
        String v = e.getAttribute("numOfAttrs").trim();
        if(v.equals("*"))
            list.add("*");
        else
        {
            NodeList nl = node.getChildNodes();

            for(int i = 0; i < nl.getLength(); i++)
            {
                
                org.w3c.dom.Node childNode = nl.item(i);
        	    if(childNode.getNodeType() != org.w3c.dom.Node.ELEMENT_NODE)
        	        continue;
                //list = list + dbAttr(childNode) +" ";
        	    list.add(dbAttr(childNode));
                
            }
        }
        return list;
    }
    
    private String dbAttr(org.w3c.dom.Node node)
    {
        
        String attr = "";
        Element e = (Element)node;
        
        attr = e.getAttribute("dbRelName")+"."+
        	   e.getAttribute("dbAttrName");
        return attr;
    }
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("Usage: java ExpTreeViewer <xmlfile>");
            //System.exit(0);
        }
        
        ExpTreeViewer viewer = new ExpTreeViewer(args[0]);
        viewer.show();
        
    }

   
    
    private String xmlFile;

	
    /**
    * for popup menu
    */
    private JPopupMenu treePopup;
    private JMenuItem treeIncreaseFontItem;	// popup menuItem to increase font
    private JMenuItem treeDecreaseFontItem;	// popup menuItem to decrease font
    private JMenuItem treeTopdownItem;	// popup menuItem to show topdown treee
    private JMenuItem treeBottomupItem;	// popup menuItem to show bottomup tree
    private boolean curLayout = true;   // TOPDOWN layout

    /**
    * variables for visumree
    */
	private TreePanel treePanel;
	private Tree tree;
	private cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node currentNode;
	private cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.Node joinNode;
	
	private final double TREE_MW = 1.0;
	private final double NODE_DX = 0.5;
	private final double NODE_MDX = 0.1;
	private final double BOSANI_DX = 2.0;
	private final double BOSANI_DY = 1.0;

	private Font[] font; 
	private int currentFont = 0;
	private int currentTree = 0;
	
	private Document doc;
	private org.w3c.dom.Node root;
	private int jcount = 0; // to check the number of projections in join
	
}




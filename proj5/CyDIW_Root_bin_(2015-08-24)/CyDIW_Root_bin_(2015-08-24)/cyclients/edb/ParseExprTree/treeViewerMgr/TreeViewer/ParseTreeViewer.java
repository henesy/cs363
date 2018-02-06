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

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.global.*;
import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.visumtree.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;


public class ParseTreeViewer extends JFrame implements TreeListener, ActionListener, KeyListener
{
    public ParseTreeViewer(String fname)
    {
        //setLayout(new GridLayout(1,1));
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

		genParseTreePanel();
		setSize(700, 600);
		setTitle("Parse Tree Viewer");
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
	private void genParseTreePanel()
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
		initParsedTree();

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
    public void initParsedTree()
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
        currentNode = (Node)tree.root();
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
        String arg = evt.getActionCommand();

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
        // Use the default (non-validating) parser
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            ParseTreeHandler handler = new ParseTreeHandler();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(xmlFile, handler);
            //System.out.println("Successfully, " + xmlFile+", parsed");

        }
        catch(Exception e)
        {
            System.out.println("Exception during reading file.");
            System.out.println("Contents : "+ e);
        
        }
    }
    

   
    public static void main(String[] args)
    {
        if(args.length != 1)
        {
            System.out.println("Usage: java ParseTreeViewer <xmlfile>");
            //System.exit(0);
        }
        
        ParseTreeViewer viewer = new ParseTreeViewer(args[0]);
        viewer.show();
        
    }

    
    private String xmlFile;
    private String statement;

	
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
	private Node currentNode;
	
	private final double TREE_MW = 1.0;
	private final double NODE_DX = 0.5;
	private final double NODE_MDX = 0.1;
	private final double BOSANI_DX = 2.0;
	private final double BOSANI_DY = 1.0;

	private Font[] font; 
	private int currentFont = 0;
	
	class ParseTreeHandler extends DefaultHandler implements ContentHandler, ErrorHandler
	{
	    private boolean isStringAvailable = false;
	    public ParseTreeHandler()
	    {
	        //super();
	    }
	    public void setDocumentLocator(Locator locator)
	    {
	  		//System.out.println("setDocumentLocator called.");
	    }
	    
	    public void startDocument() throws SAXException
	    {
	  		//System.out.println("startDocumentLocator called.");

	    }
	    public void endDocument() throws SAXException
	    {
	        //System.out.println("endDocumentLocator called.");
	    }

	   public void startElement(String namespaceURI, String localName, String fullName, 
	                             Attributes attributes) throws SAXException
	    {
	   		Node newNode = new Node("cat="+fullName);
			currentNode.newDaughter(newNode);
			currentNode = newNode;
			
			/**
			 * processing attribute. It might be better design if there is no
			 * attributes of parse tree.
			 */
			for(int i = 0; i < attributes.getLength(); i++)
			{
		   		Node nameNode = new Node("cat="+attributes.getQName(i));
		   		newNode.newDaughter(nameNode);
		   		Node valueNode = new Node("cat="+attributes.getValue(i));
		   		nameNode.newDaughter(valueNode);
			    //System.out.println(attributes.getValue(i));
			}
	    }

	    public void endElement(String namespzeURI, String localName, String fullName)
	                             throws SAXException
	    {
	        if(isStringAvailable)
	        {
	      		String term = "@@node.:terminal=true";

	       		Node newNode = new Node("lex="+statement+"|"+term);
		    	currentNode.newDaughter(newNode);
		    	isStringAvailable = false;
	        }
	        
	        currentNode = currentNode.getMother();
	    }


	    public void processingInstruction(String target, String instruction) throws SAXException
	    {
	    }

	    public void startPrefixMapping(String prefix, String uri) throws SAXException
	    {
	    }
	    public void endPrefixMapping(String prefix) throws SAXException
	    {
		}

	    public void characters(char[] chars, int start, int length) throws SAXException
	    {
	        if(isStringAvailable)
	        {
	            String temp = new String(chars, start, length);
	            statement += " "+temp;
	        }
	        else
	        {
	            statement = new String(chars, start, length);
	        }
	        
	        statement = statement.trim();
	        if(statement.equals(""))
	        {
	            isStringAvailable = false;
	            return;
	        }
	        //System.out.println(statement);
	        isStringAvailable = true;
		}

	    public void ignorableWhitespace(char[] chars, int start, int end) throws SAXException
	    {
	    }

	    public void skippedEntity(String name) throws SAXException
	    {
	    }
	    
	    public void warning(SAXParseException exception)
	    {
	   		System.out.println("-- WARNING --------------------------------------------");
	   		System.out.println("\t Line number :\t "+exception.getLineNumber());
	   		System.out.println("\t Column number :\t "+exception.getColumnNumber());
	   		System.out.println("\t Message :\t"+exception.getMessage());
	   		System.out.println("-------------------------------------------------------");
	   	}
	   	
	    public void error(SAXParseException exception)
	    {
	        System.out.println("-- ERROR ----------------------------------------------");
	        System.out.println("\t Line number :\t "+exception.getLineNumber());
	        System.out.println("\t Column number :\t "+exception.getColumnNumber());
	        System.out.println("\t Message :\t"+exception.getMessage());
	        System.out.println("-------------------------------------------------------");
	   	}
	   	
	    public void fatalError(SAXParseException exception) throws SAXException
	    {
	        System.out.println("-- ERROR ----------------------------------------------");
	        System.out.println("\t Line number :\t "+exception.getLineNumber());
	        System.out.println("\t Column number :\t "+exception.getColumnNumber());
	        System.out.println("\t Message :\t"+exception.getMessage());
	        System.out.println("-------------------------------------------------------");
	   		throw new SAXException("Fatal Error encountered - parsing terminated.");
	   	}

	}

}




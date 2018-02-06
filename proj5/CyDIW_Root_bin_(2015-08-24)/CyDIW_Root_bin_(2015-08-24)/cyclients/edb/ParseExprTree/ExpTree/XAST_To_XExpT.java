/*
 * Updated by		:	Saravana Chellappan
 * Date of Revision	:	May 4th, 2010
 * Updated by:			Xinyuan Zhao
 * Date of Updating:	July 10, 2008
 * 
 * Authors:				Srikanth Krithivasan.
 *						Jose M Reyers Alamo.
 * Date of Creation:	November 07, 2004.
 * Course No:           CS 562
 * Course Name:         Implementation of Database Systems.
 * Instructor:          Dr. Shashi K. Gadia
 */

package cyclients.edb.ParseExprTree.ExpTree;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class XMLWriter: This class is used to read the various attributes present in
 * the containers and create an Expression Tree File corresponding to the input
 * query. The containers are created using the XMLReader file.
 * 
 * This class was used to create intialExpress Tree and Optimal Expression Tree
 * // xiaofeng
 */
public class XAST_To_XExpT {

	private Document parseTreeDoc, ctlgDoc, doc; // doc: Use for help create
													// Node
	private Document intialTreeDoc, optimalDoc; // It can be changed with
												// different expTree
	private Hashtable<String, String> relnName; // It is used for check the
												// relationship between
												// relationName and aliasName

	private boolean valid;
	boolean isTwoRel = false;

	public XAST_To_XExpT(File parseTree, File catalogFile) {

		parseTreeDoc = docCreater(parseTree);
		valid = false;
		doc = docCreater(); // Was used for future .(Create Some element in
		ctlgDoc = docCreater(catalogFile);
		// document)
		intialTreeDoc = docCreater();
		optimalDoc = docCreater();
		relnName = new Hashtable<String, String>();

	}

	public void initialize() {
		traverse();

	}

	public Document generateIntialExp() {
		Element dbQuery = intialTreeDoc.createElement("dbQuery");
		Element dbSFWExp = intialTreeDoc.createElement("dbSFWExp");
		// Info from parseTree();

		String relName1 = " ", aliasName1 = " ", relName2 = " ", aliasName2 = " ";
		NodeList dbFromClause = parseTreeDoc
				.getElementsByTagName("dbFromClause").item(0).getChildNodes();
		if (dbFromClause.getLength() > 1)
			isTwoRel = true;
		relName1 = dbFromClause.item(0).getFirstChild().getAttributes()
				.getNamedItem("Token").getNodeValue();
		aliasName1 = dbFromClause.item(0).getFirstChild().getNextSibling()
				.getAttributes().getNamedItem("Token").getNodeValue();
		if (isTwoRel) {
			relName2 = dbFromClause.item(1).getFirstChild().getAttributes()
					.getNamedItem("Token").getNodeValue();
			aliasName2 = dbFromClause.item(1).getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("Token").getNodeValue();
			dbSFWExp.appendChild(intialTreeDoc.importNode(
					twoRelNodes(relName1, aliasName1, relName2, aliasName2),
					true));
		} else
			dbSFWExp.appendChild(intialTreeDoc.importNode(
					createRelSubTree(relName1, aliasName1), true));

		dbQuery.appendChild(dbSFWExp);
		intialTreeDoc.appendChild(dbQuery);
		// End of the tree setUp(no content in attriList and booleanExp)

		// Begin to graft attriList into tree.

		Node dbProj = dbQuery.getElementsByTagName("dbRelExp").item(0);
		dbProj.removeChild(dbProj.getLastChild());
		dbProj.appendChild(intialTreeDoc.importNode(createAttrList(), true));
		// End for graft attrList

		// Begin to graft booleanExp into tree.
		Node dbSel = dbQuery.getElementsByTagName("dbRelExp").item(1);
		Node boolFact = parseTreeDoc.getElementsByTagName("BooleanFactor")
				.item(0);
		Node booleanExp = dbSel.getLastChild();
		booleanExp.appendChild(intialTreeDoc.importNode(
				createBooleanNode(boolFact), true));
		((Element) booleanExp).setAttribute("condType", "Complex"); // Need more
																	// //
																	// thoughts.
		// End to graft booleanExp into tree.
		return intialTreeDoc;
	}

	public Document generateOptimalExp() {

		if (intialTreeDoc.getChildNodes() == null)
			new RuntimeException("Warning: Please Create IntialTree Firstly");

		try {
			optimBooleanExp();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		optimAttrList();
		return optimalDoc;
	}

	private void optimAttrList() {
		if (optimalDoc.getFirstChild() == null)
			System.out.print("n");
	}

	// traverse the boolean expression subtree of initalexpTree and put the leaf
	// into a list
	private void traverseBooleanSubTree(Node n, ArrayList<Node> booleannodelist) {
		// BooleanExp:
		if (n.getNodeName().equalsIgnoreCase("BooleanExp")) {
			booleannodelist.add(n);
			return;
		}
		// BoolanConnective: "AND"
		if (n.getNodeName().equalsIgnoreCase("booleanConnective")
				&& n.getAttributes().getNamedItem("connectiveType")
						.getNodeValue().equalsIgnoreCase("AND")) {

			traverseBooleanSubTree(n.getFirstChild(), booleannodelist);
			traverseBooleanSubTree(n.getLastChild(), booleannodelist);
			return;
		}

		// BooleanConnective: "OR", "NOT"
		booleannodelist.add(n);
	}

	/**
	 * An algorithm that move the condition down. Optimize the condition. (prune
	 * and graft)searching down the condition )
	 */
	private void optimBooleanExp() throws TransformerException {
		// Begin to copy intialExpDoc
		TransformerFactory tfactory = TransformerFactory.newInstance();
		Transformer tx = null;
		try {
			tx = tfactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		DOMSource source = new DOMSource(intialTreeDoc);
		DOMResult result = new DOMResult();
		tx.transform(source, result);
		optimalDoc = (Document) result.getNode();
		// End to copy intialExpDoc

		if (optimalDoc.getElementsByTagName("dbRelExp").getLength() == 2)
			return; // One Relation involved, no need to optimized

		ArrayList<Node> booleanNodeList = new ArrayList<Node>();
		Node root = optimalDoc.getElementsByTagName("dbRelExp").item(1)
				.getLastChild(); // root of boolean expression subtree
		traverseBooleanSubTree(root.getFirstChild(), booleanNodeList);

		root.removeChild(root.getFirstChild());
		((Element) root).setAttribute("condType", "TRUE"); // get ride of
															// boolean
															// expression
															// subtree

		for (Node n : booleanNodeList)
			moveBooleanexp(n);
	}

	private void moveBooleanexp(Node n) {
		int relnumber; // 4 means first rel iterator, 6 means second rel
		boolean flag = false; // iterator
		// this is an atomic booleanFactor
		// Begin to move the basic Atomic BooleanExp
		if (n.getNodeName().equalsIgnoreCase("BooleanExp")) {
			n = n.getFirstChild();
			if (n.getAttributes().getNamedItem("booleanFactorType")
					.getNodeValue().equalsIgnoreCase("attrOpattr")) {
				// attrOpattr; r1.a = r2.b
				String temp1 = n.getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName1").getNodeValue();
				String temp2 = n.getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName2").getNodeValue();
				if (!temp1.equalsIgnoreCase(temp2)) {
					// move to join iterator
					moveSubTree(optimalDoc.getElementsByTagName("dbRelExp")
							.item(2).getLastChild(), n.getParentNode());
					Element element0 = (Element) (optimalDoc
							.getElementsByTagName("dbRelExp").item(2));
					element0.setAttribute("joinType", "naturalJoin");

					// System.out.println("move to ji");
					return;
				} else {
					// move to related rel //
					flag = true; // r1.a > r1.b attrOpattr only involve one
									// relation
				}
			}
			// move to rel;
			String temp;
			if (flag) // r1.a > r1.b
				temp = n.getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName1").getNodeValue();
			else
				temp = n.getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName").getNodeValue();

			if (temp.equalsIgnoreCase(optimalDoc
					.getElementsByTagName("dbRelExp").item(4).getFirstChild()
					.getNextSibling().getAttributes()
					.getNamedItem("dbRelAliasName").getNodeValue()))
				relnumber = 4;
			else
				relnumber = 6;

			moveSubTree(
					optimalDoc.getElementsByTagName("dbRelExp").item(relnumber)
							.getLastChild(), n.getParentNode());
			// System.out.println("move to ri");
			return;
		} // End to move the basic Atomic BooleanExp

		// This is a booleanConnective with "OR", "NOT"
		Element root = (Element) n;
		NodeList nl = root.getElementsByTagName("booleanFactor");
		ArrayList<String> aliaslist = new ArrayList<String>();
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getAttributes().getNamedItem("booleanFactorType")
					.getNodeValue().equalsIgnoreCase("attrOpattr")) {
				String temp1 = nl.item(i).getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName1").getNodeValue();
				String temp2 = nl.item(i).getFirstChild().getAttributes()
						.getNamedItem("dbRelAliasName2").getNodeValue();
				if (!temp1.equalsIgnoreCase(temp2)) {
					// move to ji
					moveSubTree(optimalDoc.getElementsByTagName("dbRelExp")
							.item(2).getLastChild(), n);

					((Element) (optimalDoc.getElementsByTagName("dbRelExp")
							.item(2))).setAttribute("joinType", "naturalJoin");
					// System.out.println("move to ji");
					return;
				}
				if (!aliaslist.contains(temp1))
					aliaslist.add(temp1);
			}
			String temp = nl.item(i).getFirstChild().getAttributes()
					.getNamedItem("dbRelAliasName").getNodeValue();
			if (!aliaslist.contains(temp))
				aliaslist.add(temp);
		}

		if (aliaslist.size() > 1) {
			// move to si;
			if (n.getParentNode() != null)
				n.getParentNode().removeChild(n);
			moveSubTree(optimalDoc.getElementsByTagName("dbRelExp").item(1)
					.getLastChild(), n);
			// System.out.println("move to si");
		} else if (aliaslist.size() == 1) {// move to related ri;
			if (aliaslist.get(0).equalsIgnoreCase(
					optimalDoc.getElementsByTagName("dbRelExp").item(4)
							.getFirstChild().getNextSibling().getAttributes()
							.getNamedItem("dbRelAliasName").getNodeValue()))
				relnumber = 4;
			else
				relnumber = 6;
			moveSubTree(
					optimalDoc.getElementsByTagName("dbRelExp").item(relnumber)
							.getLastChild(), n);
			// System.out.println("move to ri");
		} else
			return;

	}

	// Move boolean expression subTree (booleanConnective or booleanExp) to the
	// (BFS to search the tree)
	// specified location
	private void moveSubTree(Node root, Node n) {
		// set root's attribute
		Element element0 = (Element) root;
		element0.setAttribute("condType", "Complex");

		Node insertPoint = null; // where the expression subTree will be put
		Queue<Node> queue = new LinkedList<Node>();
		if (root.getFirstChild() != null)
			queue.add(root.getFirstChild());
		else {
			root.appendChild(n);
			return;
		}
		while (!queue.isEmpty()) {
			int temp = queue.size();
			for (int i = 0; i < temp; i++) {
				Node current = queue.poll();
				if (current.getNodeName().equalsIgnoreCase("BooleanExp")) {
					insertPoint = current;
					break;// found it
				}
				if (current.getNodeName().equalsIgnoreCase("booleanConnective")
						&& current.getAttributes()
								.getNamedItem("connectiveType").getNodeValue()
								.equalsIgnoreCase("AND")) {
					queue.add(current.getFirstChild());
					queue.add(current.getLastChild());
				}
			}
		}
		if (insertPoint == null)
			insertPoint = root.getFirstChild();

		// now we insert n at insertPoint

		Element bc = optimalDoc.createElement("booleanConnective");
		bc.setAttribute("connectiveType", "AND");
		insertPoint.getParentNode().appendChild(bc);
		bc.appendChild(insertPoint);
		bc.appendChild(n);

	}

	private Node createAttrList() {

		Element dbAttrList = doc.createElement("dbAttrList");
		Element dbAttr;
		int i = 0;
		Node tempNode;
		String strAttrName, strAliasName, strRelName;
		for (i = 0; i < parseTreeDoc.getElementsByTagName("dbSelectClause")
				.item(0).getChildNodes().getLength(); i++) {
			// Create the attributes nodes based on the number of Select attr1,
			// attr2 dbSelectClause from the parse tree has this info
			dbAttr = doc.createElement("dbAttr");
			// Only one dbSelectClause would be there so item(0), in that
			// iterate through the children which is the attr name with relation
			tempNode = parseTreeDoc.getElementsByTagName("dbSelectClause")
					.item(0).getChildNodes().item(i);
			// get the alias
			strAttrName = tempNode.getLastChild().getAttributes()
					.getNamedItem("Token").getNodeValue();
			// Saravana changes
			if (strAttrName.equals("*")) {
				dbAttr.setAttribute("dbAttrName", strAttrName);
				strAliasName = tempNode.getFirstChild().getFirstChild()
						.getAttributes().getNamedItem("Token").getNodeValue();
				strRelName = (String) relnName.get(strAliasName);
				dbAttr.setAttribute("dbRelName", strRelName);
			} else {
				strAliasName = tempNode.getFirstChild().getFirstChild()
						.getAttributes().getNamedItem("Token").getNodeValue();
				strRelName = (String) relnName.get(strAliasName);
				dbAttr.setAttribute("dbRelAliasName", strAliasName);
				dbAttr.setAttribute("dbRelName", strRelName);
				dbAttr.setAttribute("dbAttrName", strAttrName);

				// validate( strRelName, strAttrName, null);
				// if (!valid)
				// {
				// System.err.println("Error in validating Select Clause");
				// //System.exit(-1);
				// }
				// item.setAttribute("dbAttrType", returnType);
			}
			// use validate() method to get the returnType later

			dbAttrList.appendChild(dbAttr);
		} // End for dbAttr

		dbAttrList.setAttribute("numOfAttrs", Integer.toString(i));

		return dbAttrList;

	}

	/**
	 * 
	 * @param relName
	 *            : Relation Name
	 * @param aliasName
	 *            : Relation Alias
	 * @return SubTree for single Relation
	 */
	private Node createRelSubTree(String relName, String aliasName) {

		// Selection Node(Start with dbRelExpSel)
		Element dbReln = doc.createElement("dbReln");
		dbReln.setAttribute("dbRelAliasName", aliasName);
		dbReln.setAttribute("dbRelName", relName);
		Element dbRelExpSel = doc.createElement("dbRelExp");
		dbRelExpSel.setAttribute("dbRelExpType", "dbSelection");
		Element evalStrategy = doc.createElement("EvalStrategy");
		Element booleanExp = doc.createElement("BooleanExp");
		booleanExp.setAttribute("condType", "TRUE");
		dbRelExpSel.appendChild(evalStrategy);
		dbRelExpSel.appendChild(dbReln);
		dbRelExpSel.appendChild(booleanExp);

		// Project Node(Start with dbRelExpSel)
		Element dbRelExpProj = doc.createElement("dbRelExp");
		dbRelExpProj.setAttribute("dbRelExpType", "dbProjection");
		Element evalStrategyTemp = doc.createElement("EvalStrategy");
		Element dbAttrList = doc.createElement("dbAttrList");
		dbAttrList.setAttribute("numOfAttrs", "ALL");
		dbRelExpProj.appendChild(evalStrategyTemp);
		dbRelExpProj.appendChild(dbRelExpSel);
		dbRelExpProj.appendChild(dbAttrList);

		return dbRelExpProj;

	}

	private Node twoRelNodes(String relName1, String aliasName1,
			String relName2, String aliasName2) {
		// Selection Node setUp
		Element dbRelExpSel = doc.createElement("dbRelExp");
		dbRelExpSel.setAttribute("dbRelExpType", "dbSelection");
		Element evelStr = doc.createElement("EvalStrategy");
		Element booleanExp = doc.createElement("BooleanExp");
		booleanExp.setAttribute("condType", "TRUE");
		dbRelExpSel.appendChild(evelStr);
		dbRelExpSel.appendChild(joinSubTree(relName1, aliasName1, relName2,
				aliasName2));
		dbRelExpSel.appendChild(booleanExp);

		// Projection Node setUp

		Element dbRelExpProj = doc.createElement("dbRelExp");
		dbRelExpProj.setAttribute("dbRelExpType", "dbProjection");
		Element evelStr2 = doc.createElement("EvalStrategy");
		Element dbAttrList = doc.createElement("dbAttrList");
		dbAttrList.setAttribute("numOfAttrs", "ALL");
		dbRelExpProj.appendChild(evelStr2);
		dbRelExpProj.appendChild(dbRelExpSel);
		dbRelExpProj.appendChild(dbAttrList);

		return dbRelExpProj;

	}

	/**
	 * 
	 * @param relName1
	 * @param aliasName1
	 * @param relName2
	 * @param aliasName2
	 * @return The whole join Node
	 */

	private Node joinSubTree(String relName1, String aliasName1,
			String relName2, String aliasName2) {
		Element dbRelExpJoin = doc.createElement("dbRelExp");
		dbRelExpJoin.setAttribute("dbRelExpType", "dbjoin");
		dbRelExpJoin.setAttribute("joinType", "null"); // It need to be decided
														// later
		Element evalStrat = doc.createElement("EvalStrategy");
		Element booleanExp = doc.createElement("BooleanExp");
		booleanExp.setAttribute("condType", "TRUE");

		dbRelExpJoin.appendChild(evalStrat);
		dbRelExpJoin.appendChild(createRelSubTree(relName1, aliasName1));
		dbRelExpJoin.appendChild(createRelSubTree(relName2, aliasName2));
		dbRelExpJoin.appendChild(booleanExp);
		return dbRelExpJoin;

	}

	@SuppressWarnings("unchecked")
	private void traverse() {
		String relAliasName = null;
		String relName = null;
		int i = 0;

		// Parse From Clause (extract relAliasName and relName, and put them
		// into a hashtable)
		NodeList nltmp = parseTreeDoc.getElementsByTagName("dbFromClause")
				.item(0).getChildNodes();
		for (i = 0; i < nltmp.getLength(); i++) {
			relName = nltmp.item(i).getFirstChild().getAttributes()
					.getNamedItem("Token").getNodeValue();
			relAliasName = nltmp.item(i).getLastChild().getAttributes()
					.getNamedItem("Token").getNodeValue();
			if (relAliasName != null && relName != null)
				// Add the attributes to a Hash Table to retrieve them later.
				relnName.put(relAliasName, relName);
		}

	}

	private Element createBooleanNode(Node n) {
		Element rootNew = null;
		if (n.getChildNodes().item(1).getNodeName()
				.equalsIgnoreCase("comparisonOp")) {
			rootNew = doc.createElement("BooleanExp");
			rootNew.setAttribute("condType", "Complex");
		} else {
			rootNew = doc.createElement("booleanConnective");
			rootNew.setAttribute("connectiveType", n.getChildNodes().item(1)
					.getNodeName());
		}

		if (n.getFirstChild().getNodeName().equalsIgnoreCase("dbAttr"))
			rootNew.appendChild(booleanFactor(n));
		// comparison Op = "Not"
		else if (n.getFirstChild().getNodeName().equalsIgnoreCase("Not")) {
			rootNew.setAttribute("connectiveType", n.getChildNodes().item(0)
					.getNodeName());
			rootNew.appendChild((createBooleanNode(n.getChildNodes().item(1))));
			// comparison Op = "or","And"
		} else if (n.getFirstChild().getNodeName()
				.equalsIgnoreCase("BooleanFactor")) {
			rootNew.appendChild((createBooleanNode(n.getChildNodes().item(0))));
			rootNew.appendChild((createBooleanNode(n.getChildNodes().item(2))));
			// Single booleanFactor
		}
		return rootNew;
	}

	/**
	 * 
	 * @param n
	 *            : It can produce attrOpattr and attrOpConst;
	 * @return
	 */
	private Node booleanFactor(Node n) {
		Element booleanFactor = null;
		Element comparisonOp = null;
		booleanFactor = doc.createElement("booleanFactor");
		if (n.getLastChild().getNodeName().equalsIgnoreCase("dbAttr"))
			booleanFactor.setAttribute("booleanFactorType", "attrOpattr");
		else
			booleanFactor.setAttribute("booleanFactorType", "attrOpConst");

		comparisonOp = doc.createElement("comparisonOp");
		String opType = n.getFirstChild().getNextSibling().getAttributes()
				.getNamedItem("Token").getNodeValue();
		String dbRelAliasName1 = n.getFirstChild().getFirstChild()
				.getFirstChild().getAttributes().getNamedItem("Token")
				.getNodeValue();
		String dbRelAliasName2 = " ";
		if (n.getLastChild().getNodeName().equalsIgnoreCase("dbAttr")) {
			comparisonOp.setAttribute("dbRelAliasName1", dbRelAliasName1);
			dbRelAliasName2 = n.getLastChild().getFirstChild().getFirstChild()
					.getAttributes().getNamedItem("Token").getNodeValue();
			comparisonOp.setAttribute("dbRelAliasName2", dbRelAliasName2);
			comparisonOp.setAttribute("opType", opType);
			comparisonOp.appendChild(createDbAttr(n.getFirstChild()));
			comparisonOp.appendChild(createDbAttr(n.getLastChild()));
		} else {
			comparisonOp.setAttribute("dbRelAliasName", dbRelAliasName1);
			comparisonOp.setAttribute("opType", opType);
			comparisonOp.appendChild(createDbAttr(n.getFirstChild()));
			comparisonOp.appendChild(createDbConstantValue(n.getLastChild()));
		}
		booleanFactor.appendChild(comparisonOp);
		return booleanFactor;

	}

	private Node createDbConstantValue(Node n) {
		Element dbConstVaule = doc.createElement("dbConstValue");
		if (n.getFirstChild().getNodeName().equalsIgnoreCase("INTEGERLITERAL"))
			dbConstVaule.setAttribute("constType", "integer");
		else
			dbConstVaule.setAttribute("constType", "string");

		dbConstVaule.setAttribute("constValue", n.getFirstChild()
				.getAttributes().getNamedItem("Token").getNodeValue());
		return dbConstVaule;
	}

	private Node createDbAttr(Node n) {
		Element dbAtrr = null;
		String strAliasName = n.getFirstChild().getFirstChild().getAttributes()
				.getNamedItem("Token").getNodeValue();
		String strAttrName = n.getLastChild().getAttributes()
				.getNamedItem("Token").getNodeValue();
		String strRelName = (String) relnName.get(strAliasName);
		dbAtrr = doc.createElement("dbAttr");
		dbAtrr.setAttribute("dbAttrName", strAttrName);
		dbAtrr.setAttribute("dbRelAliasName", strAliasName);
		dbAtrr.setAttribute("dbRelName", strRelName);
		String returnType = " ";
		dbAtrr.setAttribute("dbAttrType", returnType);

		return dbAtrr;
	}

	/**
	 * Method: walk Input Arguments: Node, PrintStream Returns: none
	 * Description: This method recurses through all the nodes starting from the
	 * root node validating each node and writing them onto the PrintStream..
	 */
	private void walk(Node node, PrintStream p) {
		int type = node.getNodeType();
		switch (type) {
		case Node.DOCUMENT_NODE: {
			// System.out.println("<?xml version=\"1.0\" encoding=\""+
			// "UTF-8" + "\"?>");
			p.println("<?xml version=\"1.0\" encoding=\"" + "UTF-8" + "\"?>");
			break;
		}
		// End of document
		case Node.ELEMENT_NODE: {
			// System.out.print('<' + node.getNodeName() );
			p.print('<' + node.getNodeName());
			NamedNodeMap nnm = node.getAttributes();
			if (nnm != null) {
				int len = nnm.getLength();
				Attr attr;
				for (int i = 0; i < len; i++) {
					attr = (Attr) nnm.item(i);
					// System.out.print(' ' + attr.getNodeName() + "=\"" +
					// attr.getNodeValue() + '"' );
					p.print(' ' + attr.getNodeName() + "=\""
							+ attr.getNodeValue() + '"');
				}
			}
			// System.out.print('>');
			p.print('>');

			break;

		}
		// End of element
		case Node.ENTITY_REFERENCE_NODE: {

			// System.out.print('&' + node.getNodeName() + ';' );
			p.print('&' + node.getNodeName() + ';');
			break;

		}
		// End of entity
		case Node.CDATA_SECTION_NODE: {
			// System.out.print( "<![CDATA[" + node.getNodeValue() + "]]>" );
			p.print("<![CDATA[" + node.getNodeValue() + "]]>");
			break;

		}
		case Node.TEXT_NODE: {
			// System.out.print(node.getNodeValue());
			p.print(node.getNodeValue());
			break;
		}
		case Node.PROCESSING_INSTRUCTION_NODE: {
			// System.out.print("<?"
			// + node.getNodeName() ) ;
			p.print("<?" + node.getNodeName());
			String data = node.getNodeValue();
			if (data != null && data.length() > 0) {
				// System.out.print(' ');
				p.print(' ');
				// System.out.print(data);
				p.print(data);
			}
			// System.out.println("?>");
			p.println("?>");
			break;

		}
		}
		// End of switch

		// recurse on all children of the node.
		for (Node child = node.getFirstChild(); child != null; child = child
				.getNextSibling()) {
			walk(child, p);
		}

		// without this the ending tags will miss
		if (type == Node.ELEMENT_NODE) {
			// System.out.print("</" + node.getNodeName() + ">");
			p.print("</" + node.getNodeName() + ">");
		}
	}

	// End of walk

	/**
	 * Method: print Input Arguments: Document, PrintStream Returns: none
	 * Description: This method walks through the document validating each node
	 * and writes them onto the Print Stream.
	 */

	public void print(Document doc, PrintStream p) {
		walk(doc, p);
		// System.out.println("End of walk");
	}

	/**
	 * Method: validate Input Arguments: Node, String, String, String Returns:
	 * none Description: This method validates the Node against the
	 * RelationIterator and Attribute Names and ascertains that the Relations
	 * exist in the Database and the attributes havethe proper Data type.
	 */
	// Xinyuan Zhao, July 10, 2008, this method has been rewritten
	private boolean relExist, attrExist;
	private String returnType;

	private void validate(String rName, String aName, String aType) {
		int i = 0, j = 0;

		if (rName == null || aName == null)
			System.err
					.println("Error in validating, relName and attrName could not be null");

		// validate the RelName, AttrName, and AttrType
		for (i = 0; i < ctlgDoc.getElementsByTagName("dbRel").getLength(); i++) {
			if (ctlgDoc.getElementsByTagName("dbRel").item(i).getAttributes()
					.getNamedItem("relName").getNodeValue()
					.equalsIgnoreCase(rName)) {
				relExist = true;
				NodeList nltmp = ctlgDoc.getElementsByTagName("dbRel").item(i)
						.getChildNodes();
				for (j = 0; j < nltmp.getLength(); j++) {
					if (nltmp.item(j).getNodeName().equalsIgnoreCase("dbAttr")) {
						if (nltmp.item(j).getAttributes()
								.getNamedItem("attrName").getNodeValue()
								.equalsIgnoreCase(aName)) {
							attrExist = true;

							if (aType == null) {
								valid = true;
								returnType = nltmp.item(j).getAttributes()
										.getNamedItem("attrType")
										.getNodeValue();
							} else if (nltmp.item(j).getAttributes()
									.getNamedItem("attrType").getNodeValue()
									.equalsIgnoreCase(aType)) {
								valid = true;
								returnType = aType;
							} else
								System.err
										.println("Error in validating, attrType \""
												+ aType + "\" does not exist!");
						}

					}
				}

				if (!attrExist)
					System.err.println("Error in validating, attrName \""
							+ aName + "\" does not exist!");
			}
		}

		if (!relExist)
			System.err.println("Error in validating, relName \"" + rName
					+ "\" does not exist!");

	} // End of validate

	/**
	 * Method: main Input Arguments: String[] Returns: none Description: Main
	 * Method.
	 */
	public static void main(String args[]) {
		File parseTreeFile, dbCatalogFile, expTreeFile;

		try {
			parseTreeFile = new File("ASTree.xml");
			dbCatalogFile = new File("catalog.xml");
			expTreeFile = new File("expTree.xml");

			// if(args.length != 3)
			// {
			// //Check to ensure user XML file name to parse
			// System.err.println("Usage::java xmlWriter ParseTreeFile dbCatalogFile ExpTreeFile");
			// //System.exit(0);
			// }
			// else
			// {
			// parseTreeFile = new File(args[0].trim());
			// dbCatalogFile = new File(args[1].trim());
			// expTreeFile = new File(args[2].trim());
			// }

			FileOutputStream out; // declare a file output object
			PrintStream p = null; // declare a print stream object
			try {
				// Create a new file output stream
				out = new FileOutputStream(expTreeFile);

				// Connect print stream to the output stream
				p = new PrintStream(out);
			} catch (Exception e) {
				System.err.println("Error opening the output file");
			}

			XAST_To_XExpT xw = new XAST_To_XExpT(parseTreeFile, dbCatalogFile);
			xw.initialize();
			xw.print(xw.generateIntialExp(), p); // Work basically

			// xw.print(xw.generateOptimalExp(), p);
			p.close();
			System.out.println("Expression tree is created successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(-1);
		}
	}

	// End of main

	private Document docCreater(File f) {
		Document doc = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.parse(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	private Document docCreater() {
		Document doc = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

}

package cyclients.edb.dataexecution;

import java.util.*;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import cyclients.edb.datageneration.*;

import storagemanager.StorageUtils;


/**
 * @author Xiaofeng Wang
 * 
 *         Mar 24, 2013 This class produce tuple. Input are data(database,
 *         catalogfile, expressionTree Output is iterator for result.
 */

public class QueryExec {

	private Document expDoc;
	private boolean isTwoRels = false;
	private ProjectionIterator pi = new ProjectionIterator(); // Root of //
																// executeTree

	private RelationIterator ri; // just for 3-layer config.

	// Set up excuteTree which is consisted of iterators
	private void initialize(String expTreeFile, String catalogFile,
			StorageUtils can, int outRelBufferNum, int innerRelBufNum) {
		expDoc = docCreater(expTreeFile);

		// determine the expdoc has 3-layer or 5-layer(with join and double
		// relations) configuration.
		NodeList RelExplist = expDoc.getElementsByTagName("dbRelExp");
		Node joinNode = null;
		for (int i = 0; i < RelExplist.getLength(); i++)
			if (RelExplist.item(i).getAttributes().getNamedItem("dbRelExpType")
					.getNodeValue().equalsIgnoreCase("dbjoin")) {
				isTwoRels = true;
				joinNode = RelExplist.item(i);
			}
		// Same in 3-layer and 5-layer expressionTree
		pi.setCond(RelExplist.item(0).getLastChild()); // set up Root pi
		Node selNode = RelExplist.item(0).getFirstChild().getNextSibling();
		SelectionIterator si = new SelectionIterator(); // set up si
		if (selNode.getLastChild().getAttributes().getNamedItem("condType")
				.getNodeValue().equalsIgnoreCase("True"))
			si.setCond(selNode.getLastChild());
		else
			si.setCond(selNode.getLastChild().getFirstChild());

		if (isTwoRels) { // 5-layer

			// Instantiate different iterators in execution Tree, and set up
			// condition for different iterators.
			JoinIterator ji = new JoinIterator(); // set up ji, and set up the
													// buffer
			// allocation

			if (joinNode.getAttributes().getNamedItem("joinType")
					.getNodeValue().equalsIgnoreCase("null"))
				ji.setCond(joinNode.getLastChild());
			else
				ji.setCond(joinNode.getLastChild().getFirstChild()); // Edited
																		// by
																		// Xiaofeng
																		// Wang,
																		// Need
																		// more
																		// consideration

			Node projNode1, projNode2;
			ProjectionIterator pi1 = new ProjectionIterator();
			ProjectionIterator pi2 = new ProjectionIterator();// set up pi1,pi2
			projNode1 = joinNode.getFirstChild().getNextSibling();
			projNode2 = projNode1.getNextSibling();
			pi1.setCond(projNode1.getLastChild());
			pi2.setCond(projNode2.getLastChild());

			Node selNode1 = projNode1.getFirstChild().getNextSibling();
			Node selNode2 = projNode2.getFirstChild().getNextSibling();
			SelectionIterator si1 = new SelectionIterator();
			SelectionIterator si2 = new SelectionIterator();
			// set up si1, si2
			if (selNode1.getLastChild().getAttributes()
					.getNamedItem("condType").getNodeValue()
					.equalsIgnoreCase("True")) // Add by xiaofeng Wang 4/9/2013
				si1.setCond(selNode1.getLastChild());
			else
				si1.setCond(selNode1.getLastChild().getFirstChild());
			if (selNode2.getLastChild().getAttributes()
					.getNamedItem("condType").getNodeValue()
					.equalsIgnoreCase("True")) // Add by xiaofeng Wang 4/9/2013)
												// // Add by xiaofeng Wang
												// 4/9/2013
				si2.setCond(selNode2.getLastChild());
			else
				si2.setCond(selNode2.getLastChild().getFirstChild());

			String relName1 = selNode1.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelName").getNodeValue();
			String aliasName1 = selNode1.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelAliasName")
					.getNodeValue();
			String relName2 = selNode2.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelName").getNodeValue();
			String aliasName2 = selNode2.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelAliasName")
					.getNodeValue();

			// ri1's bufferNum and ri2's buffer Num do not really matter the
			// performance, since
			// it is just take charge of the instreaming of tuples.
			RelationIterator ri1 = new RelationIterator(relName1, aliasName1,
					catalogFile, can, outRelBufferNum);
			RelationIterator ri2 = new RelationIterator(relName2, aliasName2,
					catalogFile, can, innerRelBufNum);

			// setup Relations: In order to control instream and outstream.
			// Relation can encapsulate the attribute list
			/*
			 * si1.setRelation(ri1.getRel()); si2.setRelation(ri2.getRel());
			 * pi1.setRelation(si1.getRel()); pi2.setRelation(si2.getRel()); //
			 * ji.setRelation(pi1.getRel(), pi2.getRel());
			 * si.setRelation(ji.getRel()); pi.setRelation(si.getRel());
			 */

			// setup ExecuteTree. In this tree, every Node is an iterator
			pi.setChild(si);
			si.setChild(ji);
			ji.setChild(pi1, pi2);
			ji.setRelIterators(ri1, ri2); // new
			pi1.setChild(si1);
			pi2.setChild(si2);
			si1.setChild(ri1);
			si2.setChild(ri2); // End of setting up child
			
			// Set up how many tuple in buffer
			/*
			 * ji.setTupleNumber((outRelBufferNum * buffSize) / tupleSize1,
			 * (innerRelBufNum * buffSize) / tupleSize2); // Add by xf
			 */
			pi.open();

		} else { // 3-layer
			String relName1 = selNode.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelName").getNodeValue();
			String aliasName1 = selNode.getFirstChild().getNextSibling()
					.getAttributes().getNamedItem("dbRelAliasName")
					.getNodeValue();

			ri = new RelationIterator(relName1, aliasName1, catalogFile, can,
					outRelBufferNum);

			// setup Relations: In order to control instream and outstream
			si.setRelation(ri.getRel());
			pi.setRelation(si.getRel());

			// setup ExecuteTree. In this tree, every Node is an iterator
			pi.setChild(si);
			si.setChild(ri);

			pi.open(); // After constructing, we need to open the
			// valve for pi.Otherwise, it can not getNext()
		}

	}
    
	public String execute() {	
		LinkedList<Node> rem = new LinkedList<Node>();
		ArrayList<Node> tempList;
		 int counter = 0;
        
      
		if (isTwoRels) {
	
			while ((tempList = pi.getNext()) != null) {

				Node joinTemp;
				if (tempList.size() == 2)
					joinTemp = join(tempList.get(0), tempList.get(1));
				else
					joinTemp = tempList.get(0);
//				rem.add(joinTemp);  // Just comment out this time, since it can take a lot of time
				
	
			}

		} else {
			boolean temp = true; // whether ri has next batch
			while (temp) {
				while ((tempList = pi.getNext()) != null) {
					Node joinTemp;
					if (tempList.size() == 2)
						joinTemp = join(tempList.get(0), tempList.get(1));
					else
						joinTemp = tempList.get(0);
				//	rem.add(joinTemp); // Just comment out this time, since it can take a lot of time for execution. If showing the result in the outputPane, it should add this line of code.
				}
				temp = ri.loadBuffers(); //load next batch of ri
			}
		}

		Element e = expDoc.createElement("dataoutput");
		if (rem.size() > 0) {
			Node[] nodes = rem.toArray(new Node[rem.size()]);
			for (int i = 0; i < nodes.length; i++)
				e.appendChild(expDoc.importNode(nodes[i], true));
		}
		return convertToTable(e);

	}

	// when there is one rel, inner bufferSize is 1.
	public QueryExec(String expTreeFile, String catalogFile, StorageUtils can,
			int outRelBufferSize, int innerRelBufferSize) {
		initialize(expTreeFile, catalogFile, can, outRelBufferSize,
				innerRelBufferSize); // Initialize exeTree
	}

	/**
	 * This method helps to print out DOMNode.
	 * 
	 * @param node
	 */

	public static void nodePrint(Node node) {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(node);
			StreamResult result = new StreamResult(System.out);
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Document docCreater(String fileName) {
		Document doc = null;
		// Create a new parser using the JAXP API (javax.xml.parser)
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.parse(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doc;
	}

	static String convertToTable(Node dataOutput) {
		if (dataOutput.getFirstChild() == null)
			return "The result do not show here";

		String outPut = "<table border=\"1\">";

		// print out the header
		outPut += "<tr>";
		NodeList nlTemp = dataOutput.getFirstChild().getChildNodes();
		for (int i = 0; i < nlTemp.getLength(); i++) {
			// differentiate tuple associated with one rels or two rels
			if (dataOutput.getFirstChild().getNodeName().contains("."))
				outPut += "<th>" + nlTemp.item(i).getNodeName() + "</th>";
			else {
				String alias = dataOutput.getFirstChild().getAttributes()
						.getNamedItem("relAlias").getNodeValue();
				outPut += "<th>" + alias + "." + nlTemp.item(i).getNodeName()
						+ "</th>";
			}
		}
		outPut += "</tr>";

		// printout tuples' content.
		NodeList nl = dataOutput.getChildNodes();
		for (int m = 0; m < nl.getLength(); m++) {
			NodeList n = nl.item(m).getChildNodes();
			outPut += "<tr>";
			for (int j = 0; j < n.getLength(); j++) {
				outPut += "<td>" + n.item(j).getTextContent() + "</td>";
			}
			outPut += "</tr>";
		}

		// printout ending tag.
		outPut += "</table>";

		return outPut;

	}

	// Join two nodes together
	static Node join(Node n1, Node n2) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBldr;
		try {
			docBldr = factory.newDocumentBuilder();
			Document doc = docBldr.newDocument();
			Element ret = doc.createElement(n1.getNodeName() + "."
					+ n2.getNodeName());
			Element nxtAtt;
			NodeList n1NL = n1.getChildNodes();
			for (int i = 0; i < n1NL.getLength(); i++) {
				nxtAtt = doc.createElement(n1.getAttributes()
						.getNamedItem("relAlias").getNodeValue()
						+ "." + n1NL.item(i).getNodeName());
				nxtAtt.setTextContent(n1NL.item(i).getTextContent());
				ret.appendChild(nxtAtt);
			}

			NodeList n2NL = n2.getChildNodes();
			for (int i = 0; i < n2NL.getLength(); i++) {
				nxtAtt = doc.createElement(n2.getAttributes()
						.getNamedItem("relAlias").getNodeValue()
						+ "." + n2NL.item(i).getNodeName());
				nxtAtt.setTextContent(n2NL.item(i).getTextContent());
				ret.appendChild(nxtAtt);
			}

			return ret;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public ProjectionIterator getPi() {
		return pi;
	}

}

package cyclients.edb.dataexecution;

import java.util.ArrayList;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Apr 18, 2013
 * 
 * @author Xiaofeng Wang: This class was used for helping open iterator for
 *         whole execution Tree
 */
public class OpenIteraotr {

	public ProjectionIterator pi;
	ArrayList<Node> nxt;
	private boolean hasNext = false;

	public boolean open() {
		pi.open();
		locateNext();
		return true;
	}

	private boolean locateNext() {
		ArrayList<Node> temp;
		if ((temp = pi.getNext()) != null)
			hasNext = true;
		else
			hasNext = false;
		nxt = temp;
		return true;
	}

	public boolean hasNext() {
		return hasNext;
	}

	public ArrayList<Node> getNext() {
		ArrayList<Node> temp = nxt;
		locateNext(); // renew nxt;
		return temp;
	}

	public String getRemaining() {
		LinkedList<Node> rem = new LinkedList<Node>();
		Node joinTemp;
		while (hasNext) {
			if (nxt.size() == 2)
				joinTemp = QueryExec.join(nxt.get(0), nxt.get(1));
			else
				joinTemp = nxt.get(0);
			rem.add(joinTemp);
			locateNext();
		}
		Node[] nodes = rem.toArray(new Node[rem.size()]);
		return printNodes(nodes);
	}

	public String getNextTupe() {
		LinkedList<Node> rem = new LinkedList<Node>();
		//ArrayList<Node> temp = getNext();
		Node joinTemp;
		if (nxt.size() == 2)
			joinTemp = QueryExec.join(nxt.get(0), nxt.get(1));
		else
			joinTemp = nxt.get(0);
		rem.add(joinTemp);

		Node[] nodes = rem.toArray(new Node[rem.size()]);
		locateNext();
		return printNodes(nodes);
	}

	public boolean reset() {
		return pi.reset();
	}

	public boolean close() {
		System.out.println("Projection Iterator "+pi);
		pi.close();
		return true;
	}

	private String printNodes(Node[] nodes) {
		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder parser = factory.newDocumentBuilder();
			doc = parser.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element e = doc.createElement("dataoutput");
		for (int i = 0; i < nodes.length; i++)
			e.appendChild(doc.importNode(nodes[i], true));
		return QueryExec.convertToTable(e);

	}
}

package cyclients.edb.dataexecution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cyclients.edb.datageneration.Relation;


public class ProjectionIterator {
	private SelectionIterator si;
	private HashSet<String> attrList; // subtree from expressTree
	private Relation relIn;
	private Relation relOut; // output relation
	private HashMap<String, ArrayList<Integer>> attrToProjected = new HashMap<String, ArrayList<Integer>>();
	// key is [rel,alias] values is [index of attribute of this tuple]
	boolean attrToProjectedflag = false;
	
	
	boolean open() {
		
		return si.open();
	}

	boolean close() {
		System.out.println("Selection Iterator"+si);
		return si.close();
	}

	boolean reset() {
		return si.reset();
	}

	private ArrayList<Node> rets0 = new ArrayList<Node>();

	ArrayList<Node> getNext() {
		while ((rets0 = si.getNext()) != null) {			
			if (attrList.size() == 1 && attrList.contains("ALL"))
				return rets0;			
			return filter(rets0);			
		}
				
		return null;
	}

	//ArrayList<Node> filteredRelt = new ArrayList<Node>();
	private ArrayList<Node> filter(ArrayList<Node> rets) {
		if (!attrToProjectedflag)
			intiattrToProjected(rets); // Using this way to save time
		
		for (Node n : rets) {
			
			String relalias = n.getNodeName() + "."
					+ n.getAttributes().getNamedItem("relAlias").getNodeValue();
			ArrayList<Integer> templist = attrToProjected.get(relalias);
			NodeList nl = n.getChildNodes();
			int indexRem = 0;
			int nllength = nl.getLength();
			for (int i = 0; i < nllength; i++) {
				if (!templist.contains(i)){  
					n.removeChild(nl.item(i-indexRem));
					indexRem++;  // Changed by xiaofeng
					
				}
			
			}
	
		}
		return rets;
	}

	void intiattrToProjected(ArrayList<Node> rets) {
		for (Node n : rets) {
			ArrayList<Integer> attrindex = new ArrayList<Integer>();
			String relalias = n.getNodeName() + "."
					+ n.getAttributes().getNamedItem("relAlias").getNodeValue();
			NodeList n1 = n.getChildNodes(); // all attributes of this tuple
			for (int i = 0; i < n1.getLength(); i++) {
				// String[] temp = new String[3];
				String temp;
				// temp[0] = n.getNodeName();
				// temp[1] = n.getAttributes().getNamedItem("relAlias")
				// .getNodeValue();
				// temp[2] = n1.item(i).getNodeName();
				temp = n.getAttributes().getNamedItem("relAlias")
						.getNodeValue()
						+ "." + n1.item(i).getNodeName();
				if (attrList.contains(temp))
					attrindex.add(i);
			}
			attrToProjected.put(relalias, attrindex);
		}
		attrToProjectedflag = true;
	}

	void setCond(Node n) {
		attrList = new HashSet<String>();
		if (n.getAttributes().getNamedItem("numOfAttrs").getNodeValue()
				.equalsIgnoreCase("ALL")) {
			attrList.add("ALL");
			return;
		}

		NodeList n1 = n.getChildNodes();
		// String[] relaliasattr = new String[3];
		String aliasAttr;
		for (int i = 0; i < n1.getLength(); i++) {
			// relaliasattr[0] = n1.item(i).getAttributes()
			// .getNamedItem("dbRelName").getNodeValue();
			// relaliasattr[1] = n1.item(i).getAttributes()
			// .getNamedItem("dbRelAliasName").getNodeValue();
			// relaliasattr[2] = n1.item(i).getAttributes()
			// .getNamedItem("dbAttrName").getNodeValue();
			aliasAttr = n1.item(i).getAttributes()
					.getNamedItem("dbRelAliasName").getNodeValue()
					+ "."
					+ n1.item(i).getAttributes().getNamedItem("dbAttrName")
							.getNodeValue();

			attrList.add(aliasAttr);
		}
	}

	/**
	 * setRel is used for getting inStream relation, and compute outStream
	 * relation
	 * 
	 * @param r0
	 */
	void setRelation(Relation r0) {
		// relIn = r0;
		// Vector<Attribute> attrsOut = new Vector<Attribute>();
		//
		//
		// Vector<Attribute> attrsIn = relIn.getAttrList();
		// Iterator<Attribute> itrAttr = attrsIn.iterator();
		// Attribute attr;
		// while (itrAttr.hasNext()) {
		// attr = itrAttr.next();
		// if (attrToProjected.contains(attr.getAttrName()))
		// attrsOut.add(attr);
		// }
		// relOut = new Relation();
		// relOut.setRelName(r0.getRelName());
		// relOut.setAttrList(attrsOut);
	}

	// Get the outStream relation
	public Relation getRel() {
		return relOut;
	}

	// Used for construct Execution Tree.
	void setChild(SelectionIterator s) {
		si = s;
	}

}

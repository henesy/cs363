package cyclients.edb.dataexecution;

import java.util.ArrayList;

import org.w3c.dom.Node;

import cyclients.edb.datageneration.*;

public class SelectionIterator {

	private RelationIterator ri;
	private JoinIterator ji;
	private Node booleanexp;
	private AtomicBooleanExp satisfyTree;
	Relation relIn;

	boolean open() {
		if (ri != null)
			return ri.open();
		else
			return ji.open();
	}

	boolean close() {
		if (ri != null)
			return ri.close();
		else
			return ji.close();
	}

	boolean reset() {
		if (ri != null)
			return ri.reset();
		else
			return ji.reset();

	}

	private ArrayList<Node> rets = new ArrayList<Node>();
   
	public long time =System.currentTimeMillis();
	ArrayList<Node> getNext() {
		Node ret;
		rets.clear();
		if (ri != null) { // child is relationIterator
			while ((ret = ri.getNext()) != null) {
				if (satisfyTree == null) {
					rets.add(ret);
					return rets;
				}
				if (satisfyTree.satisfies(ret)) {
					rets.add(ret);
					return rets;
				}
			}
			return null;
		}

		else { // child is joinIterator	
			rets = ji.getNext();
			while (rets != null) {
				if (satisfyTree == null){
					return rets;
				}
				else if (satisfyTree.satisfies(rets.get(0), rets.get(1)))
					return rets;			
				rets= ji.getNext();
			}
			return null;
					
/*			while ((rets = ji.getNext()) != null) {
				if (satisfyTree == null){
					return rets;
				}
				else if (satisfyTree.satisfies(rets.get(0), rets.get(1)))
					return rets;
				
			}
			return null;*/
		}

	}

	// inStreaming relation and outStreaming relation are same.
	void setRelation(Relation rin) {
		relIn = rin;
	}

	Relation getRel() {
		return relIn;
	}

	void setCond(Node n) {
		booleanexp = n;
		// Added by xiaofeng
		if (booleanexp.getAttributes().getNamedItem("condType") != null) {
			if (booleanexp.getAttributes().getNamedItem("condType")
					.getNodeValue().equalsIgnoreCase("True"))
				satisfyTree = null;
			else {
				satisfyTree = buildBooleanExpInfo(booleanexp);
				return;
			}
		}

		if (booleanexp.getAttributes().getNamedItem("connectiveType") != null) {
			satisfyTree = buildBooleanExpInfo(booleanexp);
			return;

		}

	}

	void setChild(JoinIterator ji0) {
		ji = ji0;
		ri = null;
	}

	void setChild(RelationIterator ri0) {
		ri = ri0;
		ji = null;
	}

	/**
	 * 
	 * @param n
	 * @return: use for parsing booleanExp
	 */
	private AtomicBooleanExp buildBooleanExpInfo(Node n) {
		if (n.getNodeName().equals("booleanConnective")) {

			AtomicBooleanExp left, right;
			String booleanType = n.getAttributes()
					.getNamedItem("connectiveType").getNodeValue();

			if (!(booleanType.equalsIgnoreCase("NOT"))) {
				left = buildBooleanExpInfo(n.getChildNodes().item(0));
				right = buildBooleanExpInfo(n.getChildNodes().item(1));
				return new BooleanExp(left, right, booleanType);
			} else {
				left = buildBooleanExpInfo(n.getChildNodes().item(0));
				return new BooleanExp(left, booleanType);
			}
		} else if (n.getNodeName().equals("BooleanExp")) {
			return new AtomicBooleanExp(n);
		}
		return null;

	}

}

package cyclients.edb.dataexecution;

import java.util.ArrayList;
import org.w3c.dom.Node;

/**
 * @author Xiaofeng Wang This class was used to join two relations May 06, 2013
 */
public class JoinIterator {

	private ProjectionIterator pi1, pi2; // Pi1 is iterator associated with
											// outerRel(When being joined, it is
											// outerloop)
	private RelationIterator ri1, ri2;
	private Node booleanexp;
	private AtomicBooleanExp satisfyTree;

	Node node1, node2;   // Current Pair

	boolean open() {
		boolean a1 = pi1.open();
		boolean a2 = pi2.open();
		// need initialize node1 and node2
		loadNode1();
		loadNode2();
		return a1 && a2;
	}

//	public long loadNode1time=0;
	void loadNode1() {
//		long time = System.currentTimeMillis();
		if ((temp = pi1.getNext()) != null)
			node1 = temp.get(0);
		else
			node1 = null;
//		loadNode1time+=System.currentTimeMillis()-time;
	}
	
	void loadNode2() {
		if ((temp = pi2.getNext()) != null)  // Pi.getNext() only return the Next tuple in the same batch
			                                 // This implementation helps join.
			node2 = temp.get(0);
		else
			node2 = null;
	}

	boolean close() {
		return pi1.close() && pi2.close();
	}

	boolean reset() {
		return pi1.reset() && pi2.reset();
	}

	private ArrayList<Node> temp = new ArrayList<Node>();
	private ArrayList<Node> rets = new ArrayList<Node>(); // result of getNext()
	private boolean rel2isdone = false;
	private boolean rel1isdone = false; 
	public int count=0;
	ArrayList<Node> getNext() {

		// initial node1 and node2 also getNext from pi1 pi2
		while (true) {
			if (node2 == null) {
				if (!rel2isdone) {
					if (!ri2.loadBuffers()) {
						rel2isdone = true;
						System.out.println("Rel2 is done!");
					}else{					
					loadNode2();
					ri1.resetInbatch();
					loadNode1();
					}
				} else {										
					if (!ri1.loadBuffers()) {
						rel1isdone=true;
						System.out.println("Rel1 is done!");
						System.out.println("Eva takes time:"+CondEvaluate.time);
						return null;
						//System.out.println("loading all rel1 takes time:"+loadNode1time);
					//	return null; // end of the whole join (That is added by xiaofeng, which is used for testing)
					}else{  //Modified by xiaofeng wang, The original should refer to other version
						loadNode1();
						ri2.reset(); // For current batch 1, rel2 has been visited.
						rel2isdone = false;
						loadNode2();
					}
		
				}
			}

			if (node1 == null) {
				if (rel2isdone) {
					System.out.println("this will not happen");
				} else {
					loadNode2();
					ri1.resetInbatch();
					loadNode1();
				}
			}
			
			if (node1 != null && node2 != null) {				
//				  if (countSatisf == 0){
//					long timeS = System.nanoTime();				
//					for(int i=0; i<3610000; i++){
//					   satisfyTree.satisfies(node1, node2);
//					   // rets.clear();
//						//rets.add(node1.cloneNode(true));
//						//rets.add(node2.cloneNode(true));
//					}			
//					timeS = System.nanoTime()- timeS;
//					timeSatisf = timeS;
//					System.out.println("timeSatisf " + timeSatisf);
//					countSatisf++;
//					return null;
//					
//				}
				
					if (satisfyTree == null) {
						rets.clear();
						rets.add(node1.cloneNode(true));
						rets.add(node2.cloneNode(true));
						//rets.add(node1);
						//rets.add(node2);
						loadNode1();
						return rets;
					} else if (satisfyTree.satisfies(node1, node2)) {
						rets.clear();
						rets.add(node1.cloneNode(true));
						rets.add(node2.cloneNode(true));
						//rets.add(node1);
						//rets.add(node2);
						loadNode1();
						//time0+=(System.currentTimeMillis()-time);
						return rets;
					}
					loadNode1();
			}
		}
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

	void setChild(ProjectionIterator pi11, ProjectionIterator pi22) {
		pi1 = pi11;
		pi2 = pi22;
	}

	void setRelIterators(RelationIterator outer, RelationIterator inner) {
		ri1 = outer;
		ri2 = inner;
	}

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

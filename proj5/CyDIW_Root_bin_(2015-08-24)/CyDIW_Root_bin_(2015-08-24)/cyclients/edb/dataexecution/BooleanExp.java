package cyclients.edb.dataexecution;

import org.w3c.dom.Node;
import java.util.ArrayList;

public class BooleanExp extends AtomicBooleanExp {

	private ArrayList<AtomicBooleanExp> cond = new ArrayList<AtomicBooleanExp>();
	private String booleanConnective;
     // "NOT"
	 BooleanExp(AtomicBooleanExp e, String booleanConnective) {
		super();
		cond.add(e);
		this.booleanConnective = booleanConnective;
	}
     // "AND" "OR"
	 BooleanExp(AtomicBooleanExp left, AtomicBooleanExp right,
			String booleanConnective) {
		super();
		cond.add(left);
		cond.add(right);
		this.booleanConnective = booleanConnective;
	}
     
	 
	 // for tuple (one relation) evaluation of selection iterator
	 public boolean satisfies(Node data) {	 
		 if (cond.size() == 2) {
			     if (booleanConnective.equalsIgnoreCase("AND"))
			    	return cond.get(0).satisfies(data)&& cond.get(1).satisfies(data);						
				 else if (booleanConnective.equalsIgnoreCase("OR")) 
					return cond.get(0).satisfies(data)|| cond.get(1).satisfies(data);
                 else
					return true;  //suppose this will not happen
		 }
		 
		 if (cond.size() == 1) {
			 if (booleanConnective.equalsIgnoreCase("NOT")) 
				 return !cond.get(0).satisfies(data);
			 else
				 return true; //suppose this will not happen
		 }		 
		 return true; //suppose this will not happen
	 }
	 
	 //only happens for tuple (two relations) evaluation of joinIterator, selectIterator
	 public boolean satisfies(Node data1, Node data2) {

		 if (cond.size() == 2) {
		     if (booleanConnective.equalsIgnoreCase("AND"))
		    	return cond.get(0).satisfies(data1,data2)&& cond.get(1).satisfies(data1,data2);						
			 else if (booleanConnective.equalsIgnoreCase("OR")) 
				return cond.get(0).satisfies(data1,data2)|| cond.get(1).satisfies(data1,data2);
             else
				return true;  //suppose this will not happen
	 }
	 
	 if (cond.size() == 1) {
		 if (booleanConnective.equalsIgnoreCase("NOT")) 
			 return !cond.get(0).satisfies(data1,data2);
		 else
			 return true; //suppose this will not happen
	 }		 
	 return true; //suppose this will not happen

	}

	private AtomicBooleanExp firstChild() {
		return cond.get(0);
	}

	private AtomicBooleanExp secondChild() {
		if (!booleanConnective.equalsIgnoreCase("Not"))
			return cond.get(1);
		return null;
	}

	 public String getBooleanConnective() {
		return this.booleanConnective;
	}

}
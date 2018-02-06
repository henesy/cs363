package cyclients.edb.dataexecution;

import org.w3c.dom.Node;

public class AtomicBooleanExp {

	private Node condition;
	// xf relationName;
	private String relName  ;
	

	public AtomicBooleanExp() {

	}

	public AtomicBooleanExp(Node n1) {
		condition = n1;
	}

	
	
	public boolean satisfies(Node data) {
         
		return CondEvaluate.evaluate(data, condition);
	}

	//only happens for tuple (two relations) evaluation of joinIterator, selectIterator
	public boolean satisfies(Node data1, Node data2) {
        
		 
		 
 		String relName1 = ((Node) condition).getFirstChild().getFirstChild()
				.getFirstChild().getAttributes().getNamedItem("dbRelName")
				.getNodeValue();
		String aliasName1=((Node) condition).getFirstChild().getFirstChild()
				.getFirstChild().getAttributes().getNamedItem("dbRelAliasName")
				.getNodeValue();

 		String data1RelName = data1.getNodeName();
		String data1AliasName = data1.getAttributes().getNamedItem("relAlias").getNodeValue();

		// "attrOpattr" case
		if (condition.getFirstChild().getAttributes()
				.getNamedItem("booleanFactorType").getNodeValue()
				.equalsIgnoreCase("attrOpattr")) {
			if (relName1.equalsIgnoreCase(data1RelName)&& aliasName1.equalsIgnoreCase(data1AliasName))
				//if (aliasName1.equalsIgnoreCase(data1AliasName))
				return CondEvaluate.evaluate(data1, data2, condition);
			else
				return CondEvaluate.evaluate(data2, data1, condition);
		}

		// "attrOpConst" case
		if (condition.getFirstChild().getAttributes()
				.getNamedItem("booleanFactorType").getNodeValue()
				.equalsIgnoreCase("attrOpConst")) {
			if (relName1.equalsIgnoreCase(data1RelName)&& aliasName1.equalsIgnoreCase(data1AliasName))
				//if (aliasName1.equalsIgnoreCase(data1AliasName))
				return CondEvaluate.evaluate(data1, condition);
			else
				return CondEvaluate.evaluate(data2, condition);
		}
		return true; // suppose this will not happen.
	}

	public String toString() {
		return condition.getNodeName();
	}

	public String getBooleanConnective() {
		return "";
	}

	public Node getNode() {
		return this.condition;
	}

}
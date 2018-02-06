package cyclients.edb.dataexecution;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class CondEvaluate {
     
	public static long time=0;
	// check two tuples at natural joinIterator, or selectIteraotr
	static boolean evaluate(Node data1, Node data2, Node cond) {
		long time1= System.nanoTime();
		Node condTemp = ((Element) cond).getElementsByTagName("booleanFactor")
				.item(0);
		String attrName1 = condTemp.getFirstChild().getFirstChild()
				.getAttributes().getNamedItem("dbAttrName").getNodeValue();
		time+= System.nanoTime()-time1;

		String attrVal1 = attrVal(data1, attrName1);

		String attrName2 = condTemp.getFirstChild().getFirstChild()
				.getNextSibling().getAttributes().getNamedItem("dbAttrName")
				.getNodeValue();
		String attrVal2 = attrVal(data2, attrName2);
		String opType = condTemp.getFirstChild().getAttributes()
				.getNamedItem("opType").getNodeValue();

		return check(attrVal1, opType, attrVal2);
	}

	static boolean evaluate(Node data, Node cond) {

		Node condTemp = ((Element) cond).getElementsByTagName("booleanFactor")
				.item(0);
		String attrName = condTemp.getFirstChild().getFirstChild()
				.getAttributes().getNamedItem("dbAttrName").getNodeValue();

		String operator = condTemp.getFirstChild().getAttributes()
				.getNamedItem("opType").getNodeValue();

		String constType = condTemp.getFirstChild().getChildNodes().item(1)
				.getAttributes().getNamedItem("constType").getNodeValue();

		String constVal = condTemp.getFirstChild().getChildNodes().item(1)
				.getAttributes().getNamedItem("constValue").getNodeValue();

		if (constVal.contains("'"))
			constVal = constVal.substring(constVal.indexOf("'") + 1,
					constVal.lastIndexOf("'"));

		if (constType.equalsIgnoreCase("integer"))
			return check(Integer.parseInt(attrVal(data, attrName).trim()),
					operator, Integer.parseInt(constVal.trim()));
		else
			return check(attrVal(data, attrName).trim(), operator,
					constVal.trim());

	}

	private static boolean check(int dataValue, String operator, int constValue) {
		String op = "= < > <= >= != !>= !<=";
		int index = op.indexOf(operator.trim());
		switch (index) {
		case 0:
			return dataValue == constValue;
		case 2:
			return dataValue < constValue;
		case 4:
			return dataValue > constValue;
		case 6:
			return dataValue <= constValue;
		case 9:
			return dataValue >= constValue;
		case 12:
			return dataValue != constValue;
		case 15:
			return dataValue < constValue;
		case 19:
			return dataValue > constValue;
		default:
			return false;
		}

	}

	static boolean check(String dataVal1, String operator, String dataVal2) {

		String op = "=!=";
		int index = op.indexOf(operator.trim());
		switch (index) {
		case 0:
			return dataVal1.equalsIgnoreCase(dataVal2);
		case 1:
			return !dataVal1.equalsIgnoreCase(dataVal2);
		default:
			return false;
		}
	}

	/**
	 * 
	 * @param data
	 *            : Tuple
	 * @param attrName
	 *            : attrName
	 * @return: The value of attribute associated with attrName
	 */
	static String attrVal(Node data, String attrName) {
		return ((Element) data).getElementsByTagName(attrName).item(0)
				.getTextContent();
	}

}

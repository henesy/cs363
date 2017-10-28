package cyclients.split;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

public class ChainConfigParser {
	private Document doc;
	private XPath xpath;
	
	public ChainConfigParser(String str) {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			doc = builder.parse(str);
			
			XPathFactory factory = XPathFactory.newInstance(XPathFactory.DEFAULT_OBJECT_MODEL_URI,
					"com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
					ClassLoader.getSystemClassLoader());
			xpath = factory.newXPath();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int parseNumOfItems() {
		try {
			String expr = "//ChainConfig/Item/@NumOfItems";
			Node result = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
			return Integer.parseInt(result.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int parsePageCapacity() {
		try {
			String expr = "//ChainConfig/PageCapacity/@NumOfBytes";
			Node result = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
			return Integer.parseInt(result.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int parsePageHeaderSize() {
		try {
			String expr = "//ChainConfig/PageHeader/@length";
			Node result = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
			return Integer.parseInt(result.getNodeValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;	
	}
	
	// parse offset
	public int parsePageHeader(String name) {
		NodeList nl = doc.getElementsByTagName("HeaderField");	
		for (int i = 0; i < nl.getLength(); i++) {
			NodeList ChildNodes = nl.item(i).getChildNodes();
			if (((Element) ChildNodes).getAttribute("Name").equalsIgnoreCase(name)) {
				String result = ((Element) ChildNodes).getAttribute("offset");
				return Integer.parseInt(result);
			}				
		}
		return -1;
	}
	
}

package cyclients.edb.datageneration;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.util.HashMap;
import org.w3c.dom.*;

import storagemanager.StorageUtils;

import storagemanager.StorageManagerClient;
import storagemanager.StorageDirectoryDocument;

public class CreateDB {
	private StorageManagerClient sm;
	private Catalog cat;
	StorageUtils strUtils;
	// private int pageSize =0;

	private HashMap<String, LinearIterator> bufMap = new HashMap<String, LinearIterator>();

	private StorageDirectoryDocument xmlParser;

	public CreateDB(StorageUtils strUtils, Catalog c) {
		this.strUtils = strUtils;
		cat = c;
		sm = strUtils.getXmlClient();
		File catalogFile = new File(getPath());
		System.out.println("getPath: " + getPath());
		xmlParser = strUtils.getXmlParser();
		// pageSize = can.getXmlClient().getXmlSto().getPageSize();

		// int a = can.g

	}

	private String getPath() {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse("StorageConfig.xml");

			// XPathFactory factory = XPathFactory.newInstance();
			// XPath xpath = factory.newXPath();
			XPath xpath = XPathFactory.newInstance(
					XPathFactory.DEFAULT_OBJECT_MODEL_URI,
					"com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
					ClassLoader.getSystemClassLoader()).newXPath();

			XPathExpression expr = xpath.compile("//Path");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes == null)
				return null;
			return nodes.item(0).getAttributes().getNamedItem("Location")
					.getNodeValue().concat("\\xmlCatalog.xml");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean create(dataGen dg) {
		byte[] buff;
		int pn1, pn2, psize, tsize, bufFill, i = 0;
		psize = getPSize();
		// psize = this.pageSize;
		// System.out.println("Before");
		Iterator<Relation> iter = cat.relations.iterator();
		// System.out.println("After");
		int strt;
		while (iter.hasNext()) {
			// System.out.println("Here");
			Relation r = iter.next();
			Iterator<Attribute> it = r.attrList.iterator();
			tsize = 0;
			while (it.hasNext())
				// for(int j=0;j<cat.relations[i].numAttr;++j)
				tsize += it.next().getLength();
			if (tsize > psize - cat.headerSize)
				return false;

			// System.out.println("getStartPage: " +
			// xmlParser.getStartPage(r.getLocation()));
			// System.out.println("getLocation: " + r.getLocation());
			// System.out.println("docLocation: "+xmlParser.get);
			if ((strt = xmlParser.getStartPage(r.getLocation())) != -1) {
				LinearIterator li = new LinearIterator(sm);
				li.setRel(r);
				li.setTsize(tsize);
				li.setStartPage(strt);
				// li.setStartPage(getStart(r.getLocation()));
				bufMap.put(r.getRelName().toLowerCase(), li);

			} else {
				
			
				pn1 = sm.allocatePage();
				System.out.println(pn1);
				System.out.println("xf: " + pn1);
				try {
					int pagesUsed = 1;// Not sure what this is used for
					xmlParser.addXMLDocument(r.getLocation(), r.getLocation(),
							new Integer(pn1).toString(), new Long(pagesUsed
									* psize).toString());
					xmlParser.writeXmlFile(new File(strUtils.xmlCatalog_File_Path),
							xmlParser.getXMLDocument()); // Need this function
															// to update the xml
															// file

				} catch (Exception e) {
					e.printStackTrace();
				}

				// startPages[i] = pn1;
//				try {
//					buff = sm.readPagewithPin(pn1);
//				} catch (Exception e) {
//					e.printStackTrace();
//					return false;
//				}
				buff = new byte[psize];
				bufFill = cat.headerSize * 100;

				LinearIterator li = new LinearIterator(sm);
				li.setRel(r);
				li.setTsize(tsize);
				li.setStartPage(pn1);
				bufMap.put(r.getRelName().toLowerCase(), li);
				System.out.println(li.isOpen());
				for (int j = 0; j < r.getNumOfTuples(); ++j) {
					copy(buff, (bufFill / 100), dg.getNext(r), 0, tsize);
					bufFill += (tsize * 100);
					if ((bufFill / psize >= r.getSpaceUtilization())
							|| ((psize - (bufFill / 100)) < tsize)) {
						if (j < r.getNumOfTuples() - 1)
							pn2 = sm.allocatePage();
						else
							pn2 = 0;
						sm.writeBitMap();
						writeBuff(buff, bufFill, tsize, pn1, pn2);
						pn1 = pn2;
						buff = new byte[psize];
						bufFill = cat.headerSize * 100;
					}
				}

				if ((bufFill != cat.headerSize * 100)
						|| (r.getNumOfTuples() == 0))// More data to be saved,
														// last page not filled
					writeBuff(buff, bufFill, tsize, pn1, 0);
				++i;
			}
		}
		sm.writeBitMap();
		try {
			sm.flushBuffer(); // xfwang 11/2/2012
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	private void writeBuff(byte[] buff, int bufFill, int tsize, int pn, int np) {
		// System.out.println("numTup:"+bToI(intToByte((bufFill-(cat.headerSize*100))/(tsize*100)),0));
		copy(buff, 0, intToByte(bufFill / (tsize * 100)), 0, 4);
		copy(buff, 4, intToByte(np), 0, 4);
		try {
			System.out.println("Page number: " + pn);
			// System.out.println("Next page: "+bToI(buff,4));
			// intToByte(np);
			sm.writePagewithoutPin(pn, buff);
			sm.unpinPage(pn);

			// System.out.println("Tuples on this page: "+((bufFill -
			// (cat.headerSize*100))/100)/tsize);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getPSize() {
		int size = 0;
		String unit, value;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			Document doc = builder.parse("StorageConfig.xml");

			// XPathFactory factory = XPathFactory.newInstance();
			// XPath xpath = factory.newXPath();
			XPath xpath = XPathFactory.newInstance(
					XPathFactory.DEFAULT_OBJECT_MODEL_URI,
					"com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
					ClassLoader.getSystemClassLoader()).newXPath();

			XPathExpression expr = xpath.compile("//PageSize");
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			if (nodes == null)
				return 0;
			unit = nodes.item(0).getAttributes().getNamedItem("Unit")
					.getNodeValue();
			value = nodes.item(0).getAttributes().getNamedItem("Value")
					.getNodeValue();
			if (unit.toLowerCase().equals("kbytes"))
				size = Integer.valueOf(value) * 1024;
			else if (unit.equalsIgnoreCase("bytes"))
				size = Integer.valueOf(value);
			// System.out.println("Unit: " + unit + ", Value: " + value +
			// ", PSize: " + size);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
		return size;
	}

	private byte[] copy(byte[] dest, int dstart, byte[] source, int sstart,
			int len) {
		if ((dest != null) && (source != null) && (dstart >= 0)
				&& (sstart >= 0) && (len >= 0)
				&& ((source.length - sstart) >= len)
				&& ((dest.length - dstart) >= len)) {
			for (int i = 0; i < len; ++i)
				dest[i + dstart] = source[i + sstart];
		}
		return dest;
	}

	private byte[] intToByte(int i) {
		byte[] b = new byte[4];

		b[0] = (byte) (i >>> 24);
		b[1] = (byte) ((i >>> 16) & 0xff);
		b[2] = (byte) ((i >>> 8) & 0xff);
		b[3] = (byte) (i & 0xff);
		return b;
	}

	private int bToI(byte[] src, int index) {
		int ret = 0;
		for (int j = 0; j < 4; ++j)
			ret += (src[index + j] < 0 ? (int) src[index + j] + 256
					: (int) src[index + j]) << (8 * (3 - j));
		return ret;
	}

	public LinearIterator getLinearIterator(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return null;

		// Changed by Xiaofeng Wang 2/23/2013
		LinearIterator li = bufMap.get(relName.toLowerCase());
		return li;

		/*
		 * LinearIterator sto = bufMap.get(relName.toLowerCase());
		 * LinearIterator ret = new LinearIterator(sm);
		 * ret.setRel(sto.getRel()); ret.setTsize(sto.getTsize());
		 * ret.setStartPage(sto.getStartPage()); return ret;
		 */
	}

	public boolean open(String relName, int B, Node con) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return false;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		boolean ret = li.open(B, con);
		return ret;
	}

	public boolean close(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return false;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		boolean ret = li.close();
		return ret;
	}

	public Node getNext(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return null;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		Node ret = li.getNext();
		return ret;
	}

	public Node[] getRemaining(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return null;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		Node[] ret = li.getRemaining();
		return ret;
	}

	public boolean reset(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return false;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		li.reset();
		return true;
	}

	public void loadBuffersWithNextSetOfPages(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		li.loadBuffersWithNextSetOfPages();
		// System.out.println("buffersReloaded");
	}

	public boolean hasNext(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return false;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		return li.hasNext();
	}

	public boolean hasPage(String relName) {
		if (!bufMap.containsKey(relName.toLowerCase()))
			return false;
		LinearIterator li = bufMap.get(relName.toLowerCase());
		return li.hasPage();
	}
}

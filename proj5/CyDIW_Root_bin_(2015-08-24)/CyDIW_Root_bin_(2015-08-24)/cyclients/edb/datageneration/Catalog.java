package cyclients.edb.datageneration;

import java.util.Iterator;
import java.util.Vector;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;

import org.w3c.dom.*;

import cysystem.diwGUI.gui.DBGui;

import cycsx.csxpagination.util.CanStoreXUtil;


public class Catalog {

	// catalog attributes
	String dbName;
	String catalogName;
	int numOfRelations;
	String DBStoreLocation;

	// config attributes
	int headerSize;
	Vector<Relation> relations;

	DBGui mc;
	CanStoreXUtil c;

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public int getNumOfRelations() {
		return numOfRelations;
	}

	public void setNumOfRelations(int numOfRelations) {
		this.numOfRelations = numOfRelations;
	}

	public String getDBStoreLocation() {
		return DBStoreLocation;
	}

	public void setDBStoreLocation(String dBStoreLocation) {
		DBStoreLocation = dBStoreLocation;
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public Vector<Relation> getRelations() {
		return relations;
	}

	public void setRelations(Vector<Relation> relations) {
		this.relations = relations;
	}

	public Catalog(String catalogName) {
		super();
		this.catalogName = catalogName;
		headerSize = 0;
	}

	public Catalog(String catalogName, DBGui mc, CanStoreXUtil c) {
		super();
		this.catalogName = catalogName;
		headerSize = 0;
		this.mc = mc;
		this.c = c;
	}

	public DBGui getMc() {
		return mc;
	}

	public void setMc(DBGui mc) {
		this.mc = mc;
	}

	public CanStoreXUtil getC() {
		return c;
	}

	public void setC(CanStoreXUtil c) {
		this.c = c;
	}

	public boolean readConfig() {
		int dbConfigIndex = -1;
		int dgConfigIndex = -1;
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			DocumentBuilder builder = domFactory.newDocumentBuilder();
			// Document doc = builder.parse("datageneration//"+catalogName);
			Document doc = builder.parse(new File(catalogName));

			// XPathFactory factory = XPathFactory.newInstance();
			// XPath xpath = factory.newXPath();
			XPath xpath = XPathFactory.newInstance(
					XPathFactory.DEFAULT_OBJECT_MODEL_URI,
					"com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl",
					ClassLoader.getSystemClassLoader()).newXPath();

			XPathExpression expr = xpath.compile("//XML_Db");

			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			NodeList childNodes = null;
			if (nodes != null)
				for (int i = 0; i < nodes.getLength(); i++) {

					if ((dbName = nodes.item(i).getAttributes()
							.getNamedItem("dbName").getNodeValue()) == null)
						return false;

					if (nodes.item(i).getAttributes().getNamedItem("dbName")
							.getNodeValue() != null) {
						numOfRelations = Integer.valueOf(nodes.item(i)
								.getAttributes().getNamedItem("numOfRelations")
								.getNodeValue());
						relations = new Vector<Relation>();

					} else
						return false;

					childNodes = nodes.item(i).getChildNodes();

				}

			if (childNodes != null)
				for (int i = 0; i < childNodes.getLength(); i++) {
					if (childNodes.item(i).getNodeName().equals("dbConfig")) {
						dbConfigIndex = i;
						
						continue;
					}

					else if (childNodes.item(i).getNodeName()
							.equals("dgConfig")) {
						dgConfigIndex = i;
						continue;
					}

					else if (childNodes.item(i).getNodeName().equals("dbRel")) {
						Relation r = new Relation();
						if (childNodes.item(i).getAttributes()
								.getNamedItem("relName").getNodeValue() != null)
							r.setRelName(childNodes.item(i).getAttributes()
									.getNamedItem("relName").getNodeValue());

						if (childNodes.item(i).getAttributes()
								.getNamedItem("numOfAttributes").getNodeValue() != null)
							r.setNumAttr(Integer.valueOf(childNodes.item(i)
									.getAttributes()
									.getNamedItem("numOfAttributes")
									.getNodeValue()));

						if (childNodes.item(i).getAttributes()
								.getNamedItem("location").getNodeValue() != null)
							r.setLocation(childNodes.item(i).getAttributes()
									.getNamedItem("location").getNodeValue());

						NodeList attrs = childNodes.item(i).getChildNodes();
						String key = null;
						for (int j = 0; j < attrs.getLength(); ++j) {
							// Read in the attributes
							Attribute attr = new Attribute();

							if (attrs.item(j).getAttributes() != null) {
								if (attrs.item(j).getNodeName().equals("dbKey")) {

									if (attrs.item(j).getAttributes()
											.getNamedItem("attrName")
											.getNodeValue() != null) {
										key = attrs.item(j).getAttributes()
												.getNamedItem("attrName")
												.getNodeValue();

										// System.out.println("tESTPRINT" +
										// key);
									}

								} else if (attrs.item(j).getNodeName()
										.equals("dbAttr")) {
									if (attrs.item(j).getAttributes()
											.getNamedItem("attrName")
											.getNodeValue() != null)
										// attrName = RelationName.
										// attributeName
										attr.setAttrName(attrs.item(j)
												.getAttributes()
												.getNamedItem("attrName")
												.getNodeValue());
									if (attrs.item(j).getAttributes()
											.getNamedItem("attrType")
											.getNodeValue() != null)
										attr.setAttrType(attrs.item(j)
												.getAttributes()
												.getNamedItem("attrType")
												.getNodeValue());
									if (attrs.item(j).getAttributes()
											.getNamedItem("attrLength")
											.getNodeValue() != null)
										attr.setLength(Integer.valueOf(attrs
												.item(j).getAttributes()
												.getNamedItem("attrLength")
												.getNodeValue()));

									r.addAttr(attr);
								}

							}
						}

						if (key == null)
							return false;

						Iterator<Attribute> iter = r.attrList.iterator();
						while (iter.hasNext()) {
							Attribute attr = iter.next();
							if (attr.getAttrName().equals(key))
								r.setKey(attr);
						}
						relations.add(r);

					}

				}

			// Process Db Config
			if (dbConfigIndex < 0)
				return false;

			if (childNodes.item(dbConfigIndex).getAttributes()
					.getNamedItem("headerSize").getNodeValue() != null) {
				headerSize = Integer.valueOf(childNodes.item(dbConfigIndex)
						.getAttributes().getNamedItem("headerSize")
						.getNodeValue());
			}

			NodeList dbConfigChildrenNodes = childNodes.item(dbConfigIndex)
					.getChildNodes();
			if (dbConfigChildrenNodes != null)
				for (int i = 0; i < dbConfigChildrenNodes.getLength(); i++) {
					Iterator<Relation> iter = relations.iterator();
					String tmpRelName = dbConfigChildrenNodes.item(i)
							.getNodeName();
					for (int j = 0; j < relations.size(); j++) {
						Relation r = iter.next();
						if (r.getRelName().equals(tmpRelName)) {
							if (dbConfigChildrenNodes.item(i).getAttributes()
									.getNamedItem("spaceUtilization")
									.getNodeValue() != null)
								r.setSpaceUtilization(Integer
										.valueOf(dbConfigChildrenNodes
												.item(i)
												.getAttributes()
												.getNamedItem(
														"spaceUtilization")
												.getNodeValue()));
							if (dbConfigChildrenNodes.item(i).getAttributes()
									.getNamedItem("numOfTuples").getNodeValue() != null)
								r.setNumOfTuples(Integer
										.valueOf(dbConfigChildrenNodes.item(i)
												.getAttributes()
												.getNamedItem("numOfTuples")
												.getNodeValue()));
						}

					}

				}
			// Process Dg Config
			if (dbConfigIndex >= 0) {
				NodeList dgConfigChildrenNodes = childNodes.item(dgConfigIndex)
						.getChildNodes();

				if (dgConfigChildrenNodes != null)
					for (int i = 0; i < dgConfigChildrenNodes.getLength(); i++) {
						Iterator<Relation> iter = relations.iterator();
						String tmpRelName = dgConfigChildrenNodes.item(i)
								.getNodeName();
						for (int j = 0; j < relations.size(); j++) {
							Relation r = iter.next();
							if (r.getRelName().equals(tmpRelName)) {
								NodeList dgAttrs = dgConfigChildrenNodes
										.item(i).getChildNodes();
								for (int k = 0; k < dgAttrs.getLength(); ++k) {
									Iterator<Attribute> aIter = r.attrList
											.iterator();
									String tmpAttrName = dgAttrs.item(k)
											.getNodeName();
									for (int l = 0; l < r.attrList.size(); ++l) {
										Attribute a = aIter.next();
										if (a.getAttrName().equals(tmpAttrName)) {
											if (a.getAttrType().toLowerCase()
													.equals("string")) {
												if (dgAttrs.item(k)
														.getAttributes()
														.getNamedItem("prefix")
														.getNodeValue() != null)
													a.setPrefix(dgAttrs
															.item(k)
															.getAttributes()
															.getNamedItem(
																	"prefix")
															.getNodeValue());
											} else if (a.getAttrType()
													.toLowerCase()
													.equals("integer")) {
												if (dgAttrs.item(k)
														.getAttributes()
														.getNamedItem("min")
														.getNodeValue() != null)
													a.setMinVal(Integer
															.valueOf(dgAttrs
																	.item(k)
																	.getAttributes()
																	.getNamedItem(
																			"min")
																	.getNodeValue()));
												if (dgAttrs.item(k)
														.getAttributes()
														.getNamedItem("max")
														.getNodeValue() != null)
													a.setMaxVal(Integer
															.valueOf(dgAttrs
																	.item(k)
																	.getAttributes()
																	.getNamedItem(
																			"max")
																	.getNodeValue()));
												if (dgAttrs.item(k)
														.getAttributes()
														.getNamedItem("step")
														.getNodeValue() != null)
													a.setStep(Integer
															.valueOf(dgAttrs
																	.item(k)
																	.getAttributes()
																	.getNamedItem(
																			"step")
																	.getNodeValue()));
											}

										}
									}

								}
							}

						}

					}
			}

			Iterator<Relation> hs = relations.iterator();
			while (hs.hasNext())
				hs.next().setHeaderSize(headerSize);

		} catch (Exception e) {
			e.printStackTrace();
			return false;
			// TODO: handle exception
		}
		return true;
	}

}

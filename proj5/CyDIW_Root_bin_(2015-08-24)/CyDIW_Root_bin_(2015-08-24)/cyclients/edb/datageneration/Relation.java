package cyclients.edb.datageneration;

import java.util.Vector;
public class Relation {
	public Vector<Attribute> attrList = new Vector<Attribute>();
	
	private String relName;
	private int numAttr;
	
	private Attribute key;
	private int spaceUtilization = 80;
	private int numOfTuples = 100;
	private String location;
	private int headerSize = 0;
	
	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getSpaceUtilization() {
		return spaceUtilization;
	}

	public void setSpaceUtilization(int spaceUtilization) {
		this.spaceUtilization = spaceUtilization;
	}

	public int getNumOfTuples() {
		return numOfTuples;
	}

	public void setNumOfTuples(int numOfTuples) {
		this.numOfTuples = numOfTuples;
	}

	public Vector<Attribute> getAttrList() {
		return attrList;
	}

	public void setAttrList(Vector<Attribute> attrList) {
		this.attrList = attrList;
	}

	public void addAttr(Attribute attrList) {
		if (attrList != null)
			this.attrList.add(attrList);	
	}

	public String getRelName() {
		return relName;
	}

	public void setRelName(String relName) {
		this.relName = relName;
	}

	public int getNumAttr() {
		return numAttr;
	}

	public void setNumAttr(int numAttr) {
		this.numAttr = numAttr;
	}

		public Attribute getKey() {
		return key;
	}

	public void setKey(Attribute key) {
		this.key = key;
	}

	public String toString() {
		String str = "relName: " + relName + " numAttr: " + numAttr + " key: " + getKey().getAttrName() + "\n";
		for (int i=0; i<attrList.size(); i++) {
			str += "      Attr ";
			str += i;
			str += " is ";
			str += "attrName: ";
			str += attrList.get(i).getAttrName();
			str += " attrType: ";
			str += attrList.get(i).getAttrType();
			str += " attrLength: ";
			str += attrList.get(i).getLength();
			str += " dgPrefix: ";
			str += attrList.get(i).getPrefix();
			str += " dgMaxVal: ";
			str += attrList.get(i).getMaxVal();
			str += " dgMinVal: ";
			str += attrList.get(i).getMinVal();
			str += " dgStep: ";
			str += attrList.get(i).getStep();
			str += "\n";
		}
		str += "      Config: spaceUtilization = " + spaceUtilization + " numOfTuples = " + numOfTuples; 
		return (str+"\n");
		
	}
}

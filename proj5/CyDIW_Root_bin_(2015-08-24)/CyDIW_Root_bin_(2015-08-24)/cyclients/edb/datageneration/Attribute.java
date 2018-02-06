package cyclients.edb.datageneration;

import com.sun.org.apache.xpath.internal.operations.Equals;

public class Attribute {
	private String attrName;
	private String attrType;
	private int length;
	private String prefix;
	private int minVal;
	private int maxVal;
	private int step;
		
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public int getMinVal() {
		return minVal;
	}

	public void setMinVal(int minVal) {
		this.minVal = minVal;
	}

	public int getMaxVal() {
		return maxVal;
	}

	public void setMaxVal(int maxVal) {
		this.maxVal = maxVal;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrType() {
		return attrType;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public Attribute(String attrName, String attrType, int length) {
		super();
		this.attrName = attrName;
		this.attrType = attrType;
		this.length = length;
		if (attrType.toLowerCase().equals("string")) {
			this.prefix = attrName;
			this.maxVal = Integer.MAX_VALUE;
			this.minVal = 0;
			this.step = 1;
		}
		else if (attrType.toLowerCase().equals("integer")) {
			this.prefix = null;
			this.maxVal = Integer.MAX_VALUE;
			this.minVal = 0;
			this.step = 1;
		}
			
		
	}
	public Attribute() {}
	
	public boolean  Equals(Attribute attr) {
		return (attr.getAttrName().equals(this.getAttrName()) && attr.getAttrType().equals(this.getAttrType()));
		
	}
	
}

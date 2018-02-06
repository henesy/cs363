package cyutils.btree;

public class TupleAttribute {
	private String name;
	private String type;
	private int length;
	private int keyOrder;
	
	public void setName(String givenName){
		this.name = givenName;
	}
	
	public void setType(String givenType){
		this.type = givenType;
	}
	
	public void setLength(int givenLength){
		this.length = givenLength;
	}
	
	public void setKeyOrder(int givenKeyOrder){
		this.keyOrder = givenKeyOrder;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
	
	public int getLength(){
		return this.length;
	}
	
	public int getKeyOrder(){
		return this.keyOrder;
	}
	
	@Override
	public String toString(){
		return "TupleAttribute[" + "name=" + getName() + ", type=" + getType() 
		+ ", length=" + getLength() + ", keyOrder=" + getKeyOrder() + "]";
	}
}

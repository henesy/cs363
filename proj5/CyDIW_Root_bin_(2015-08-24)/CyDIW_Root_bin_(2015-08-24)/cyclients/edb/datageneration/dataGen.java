package cyclients.edb.datageneration;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.HashMap;


public class dataGen {

	
	private final int RANDOM_NUMBER_RANGE = 10; 
	private HashMap<String,Integer> iterMap = new HashMap<String,Integer>();
	public byte[] getNext(Relation r) {
		byte[] retVal;
		int tsize=0, offset=0;
		if(!iterMap.containsKey(r.getRelName()))
			iterMap.put(r.getRelName(), 0 );
		Vector<Attribute> attrs = r.getAttrList();
		if (attrs != null) {
			Iterator<Attribute> iter = attrs.iterator();
			while(iter.hasNext())
				tsize += iter.next().getLength();
			retVal = new byte[tsize];
			iter = attrs.iterator();
			//System.out.println(attrs.size());
			
			while (iter.hasNext()) {
				Attribute attr = iter.next();
				byte[] nextValue  = getNext(r, attr, r.getKey().equals(attr));
				copy(retVal, offset, nextValue, 0, nextValue.length);
				offset += attr.getLength();
				
				/*if (attr.getAttrType().toLowerCase().contains("string") ) {
					sRetVal = attr.getPrefix();
					sRetVal += iterMap.get(r.getRelName());
					iterMap.put(r.getRelName(), iterMap.get(r.getRelName()+1));
				}
				else if (attr.getAttrType().toLowerCase().contains("integer") ){
					iRetVal = iterMap.get(r.getRelName());
					iterMap.put(r.getRelName(), iterMap.get(r.getRelName()+1));
				}*/
					
			}
			return retVal;
			
			
		}
		return null;
	
	}
	
	private byte[] getNext(Relation r, Attribute attr, boolean isKey) {
		
		if (isKey) {
			int counter = iterMap.get(r.getRelName());
			if (attr.getAttrType().toLowerCase().equals("string")){
				iterMap.put(r.getRelName(), counter+1);
				return (attr.getPrefix()+counter).getBytes();
			}else if(attr.getAttrType().toLowerCase().equals("integer")){
				iterMap.put(r.getRelName(), counter+1);
				int retVal = attr.getMinVal()+attr.getStep()*counter;
				if(retVal > attr.getMaxVal()){
					System.out.println("All keys "+attr.getAttrName()+" exhausted.");
					retVal = attr.getMaxVal();
				}
				return iToB(retVal);
			}
		}
		else {
			
			Random generator = new Random();
			if (attr.getAttrType().toLowerCase().equals("string")) {
				return (attr.getPrefix()+generator.nextInt(RANDOM_NUMBER_RANGE)).getBytes();
			}
			else if(attr.getAttrType().toLowerCase().equals("integer")){
				return iToB(generator.nextInt(RANDOM_NUMBER_RANGE));
			}
		}
		return null;
	}
	
	private byte[] copy(byte[] dest, int dstart, byte[] source, int sstart, int len){
		if((dest != null) && (source != null) && (dstart >= 0) && (sstart >= 0) && (len >= 0) && ((source.length - sstart) >= len) && ((dest.length - dstart) >= len)){
			for(int i=0;i<len;++i)
				dest[i+dstart] = source[i+sstart];
		}
		return dest;
	}
	
	private byte[] iToB(int i){
		byte[] b = new byte[4];
		
		b[0] = (byte) (i >>> 24);
		b[1] = (byte) ((i >>> 16) & 0xff);
		b[2] = (byte) ((i >>> 8) & 0xff);
		b[3] = (byte) (i & 0xff);
		return b;
	}
	
	
}

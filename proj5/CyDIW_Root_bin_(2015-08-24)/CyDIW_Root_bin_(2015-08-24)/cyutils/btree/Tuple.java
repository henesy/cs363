package cyutils.btree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Tuple {
	
	private int length;
	private int keyLength;
	private int[] keyBytes;
	
	private int attributeNum;
	private List<TupleAttribute> tupleAttributes;
	
	/**
	 * In this constructor, we define that length is given by len
	 * and keys are an integer array with the same length
	 * In keys: key byte are given by their index against an tuple and the rest are -1.
	 * From 0 to len-1, the importance goes down
	 * for example: a 8 bytes long tuple, key is the first integer (0-3 inclusive)
	 * so the keys should be [0,1,2,3,-1,-1,-1,-1]
	 * 
	 * Remember that all bytes are based on Big-Endian
	 * 
	 * @param len
	 * @param keys
	 */
	
	public int getLength(){
		return this.length;
	}
	
	public int getKeyLength(){
		return this.keyLength;
	}
	
	public int getAttributeNum() {
		return this.attributeNum;
	}
	
	public List<TupleAttribute> getTupleAttributes() {
		return this.tupleAttributes;
	}
	
	public void printKeyBytes(){
		for(int ii = 0; ii < length; ii++){
			System.out.print(this.keyBytes[ii] + ", ");
		}
		System.out.println();
	}
	
	public Tuple(String fileNameWithPath){
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(new File(fileNameWithPath));
			
			NodeList list = document.getElementsByTagName("Attribute");
			
			this.attributeNum = list.getLength();
			
			List<TupleAttribute> res = new ArrayList<TupleAttribute>();
			
			for(int ii = 0; ii < list.getLength(); ii++){
				TupleAttribute attribute = new TupleAttribute();
				
				Element element = (Element) list.item(ii);
				attribute.setName(element.getAttribute("name"));
				attribute.setType(element.getAttribute("type"));
				attribute.setLength(Integer.parseInt(element.getAttribute("length")));
				attribute.setKeyOrder(Integer.parseInt(element.getAttribute("keyorder")));
				
				res.add(attribute);
			}
			
			this.tupleAttributes = res;

			int[] eachKeyOffsets = new int[res.size()];
			int[] eachKeyLengths = new int[res.size()];
			int[] eachKeySequences = new int[res.size()];
			for(int ii = 0; ii < eachKeySequences.length; ii++){
				eachKeySequences[ii] = -1;
			}
			
			int tmpKeyOffset = 0;
			for(int ii = 0; ii < res.size(); ii++){
				int eachKeyLength = res.get(ii).getLength();
				int keyOrder = res.get(ii).getKeyOrder();
				
				eachKeyOffsets[ii] = tmpKeyOffset;
				eachKeyLengths[ii] = eachKeyLength;
				if(keyOrder != -1){
					eachKeySequences[keyOrder-1] = ii;
					this.keyLength += eachKeyLength;
				}
				tmpKeyOffset += eachKeyLength;
			}
			
			this.length = tmpKeyOffset;
			this.keyBytes = new int[this.length];
			for(int jj = 0; jj < this.length; jj++){
				this.keyBytes[jj] = -1;
			}
			
			int ii = 0;
			for(int jj = 0; jj < res.size(); jj++){
				if(eachKeySequences[jj] == -1){
					break;
				}
				int keyId = eachKeySequences[jj];
				for(int kk = eachKeyOffsets[keyId]; kk < eachKeyOffsets[keyId]+eachKeyLengths[keyId]; kk++){
					this.keyBytes[ii++] = kk;
				}
			}
		} catch(Exception e){
			System.out.println(e.toString());
		}
		
	}
	
	public byte[] generateKey(byte[] record){
		
		byte[] key = new byte[this.keyLength];
		
		for(int ii = 0; ii < this.keyLength; ii++){
			
			key[ii] = record[keyBytes[ii]];
			
		}
		
		return key;
		
	}
	
	public byte[] generateKey(byte[] buffer, int offset){
		
		byte[] key = new byte[this.keyLength];
		
		for(int ii = 0; ii < this.keyLength; ii++){
			
			key[ii] = buffer[offset+keyBytes[ii]];
		}
		
		return key;
		
	}
	
	/**
	 * if given bytes error, we return 0
	 * -1: one < two, 0: one == two, 1: one > two
	 * 
	 * @param one
	 * @param two
	 * @return an integer
	 */
	
	public int compare(byte[] one, byte[] two){
		
		if(one.length != this.length || two.length != this.length){
			
			System.out.println("given bytes error: one and two should be the same "
					+ "length as defined in Tuple class");
			
			return 0;
			
		}
		
		for(int ii = 0; ii < this.keyLength; ii++){

			int pos = this.keyBytes[ii];
			
			if((one[pos] & 0x0FF) < (two[pos] & 0x0FF)){
				return -1;
			}else if((one[pos] & 0x0FF) > (two[pos] & 0x0FF)){
				return 1;
			}
		
		}
		
		return 0;
		
	}
	
	public int compare(byte[] buffer, byte[] record, int offset){
		
		if(record.length != this.length || offset+record.length>buffer.length){
			
			System.out.println("given bytes error: record should be the same length as defined in Tuple class");
			
			return 0;
			
		}
		
		for(int ii = 0; ii < this.keyLength; ii++){
			
			int pos = this.keyBytes[ii];
				
			if((buffer[pos+offset] & 0x0FF) < (record[pos] & 0x0FF)){
				return -1;
			}else if((buffer[pos+offset] & 0x0FF) > (record[pos] & 0x0FF)){
				return 1;
			}
		
		}
		
		return 0;
		
	}
	
	
	/**
	 * compare record with key inside buffer
	 */
	
	public int compareKey(byte[] buffer, byte[] record, int offset){
		
		if(record.length != this.keyLength || offset+this.keyLength+Constant.INDEX_PTR_SIZE>buffer.length){
			
			System.out.println("keyLen: " + record.length + ", given bytes error: key should be the "
					+ "same length as defined in Tuple class");
			
			return 0;
			
		}
		
		for(int ii = 0; ii < this.keyLength; ii++){
			
			int pos = this.keyBytes[ii];
				
			if((buffer[ii+offset] & 0x0FF) < (record[pos] & 0x0FF)){
				return -1;
			}else if((buffer[ii+offset] & 0x0FF) > (record[pos] & 0x0FF)){
				return 1;
			}
		
		}	
		
		return 0;
		
	}

}



package cyutils.btree;

import java.util.*;

import cyutils.btree.ReWriUtils;
import cyutils.btree.Tuple;
import cyutils.btree.TupleAttribute;

import java.io.*;
import java.io.IOException;
import java.io.FileNotFoundException;

public class BTreeTupleGenerator implements Iterator {
	
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	private Tuple tuple;
	private String cachedLine;
	private boolean finished;
	
	/**
	 * Here is the tuple length, which need to be changed manually, in order to correspond to Tuple Class
	 */
	
	public BTreeTupleGenerator(String fileNameWithPath, Tuple tuple){
		try{
			this.tuple = tuple;
			this.fileReader = new FileReader(fileNameWithPath);
			this.bufferedReader = new BufferedReader(this.fileReader);
//			System.out.println("Succeed in creating a Tuple Generator!");
			
		} catch(FileNotFoundException e){
			System.out.println("can not find file");
		} catch(Exception e){
			System.out.println("Other Exception");
		}
		this.cachedLine = null;
		this.finished = false;
	}
	
	@Override
	public boolean hasNext(){
		if(cachedLine != null){
			return true;
		}else if(finished){
			return false;
		}else{
			try{
				while(true){
					String line = bufferedReader.readLine();
					if(line == null){
						finished = true;
						return false;
					}else if(isValidTuple(line)){ // line is valid
						cachedLine = line;
						return true;
					}
				}
			}catch(IOException e){
				System.out.println("IO Exception");
			}
		}
		return false;
	}
	
	protected boolean isValidTuple(String tuple){
		if(tuple.startsWith("[") && tuple.endsWith("]")){
			return true;
		}
		return false;
	}
	
	protected String nextLine(){
		if(!hasNext()){
			System.out.println("Do not have next tuple");
			return null;
		}
		String currentLine = cachedLine;
		cachedLine = null;
		return currentLine;
	}
	
	@Override
	public byte[] next(){
		byte[] buffer = new byte[this.tuple.getLength()];
		String currentLine = nextLine();
		if(currentLine == null){
			return null;
		}
		String currentContent = currentLine.substring(currentLine.indexOf("[") + 1, currentLine.indexOf("]"));
		String[] contentArray = currentContent.split(",");
		if(this.tuple.getAttributeNum() != contentArray.length){
			System.out.println("tuple data error!");
			return null;
		}
		
		// currentLine -> buffer
		int offset = 0;
		int count = 0;
		for(TupleAttribute tupleAttribute : this.tuple.getTupleAttributes()){
			String attrType = tupleAttribute.getType();
			int attrLen = tupleAttribute.getLength();
			if(attrType.equals("Integer") && attrLen==4){
				// if it is an integer
				ReWriUtils.intToByteArray(Integer.parseInt(contentArray[count].trim()), buffer, offset);
			}else if(attrType.equals("String")){
				// if it is a string
				ReWriUtils.stringToByteArray(contentArray[count], buffer, offset, offset+attrLen);
			}
			count++;
			offset += attrLen;
		}
		
		return buffer;
	}
	
	@Override
	public void remove() {
		// TODO Auto-generated method stub
		Iterator.super.remove();
	}
}


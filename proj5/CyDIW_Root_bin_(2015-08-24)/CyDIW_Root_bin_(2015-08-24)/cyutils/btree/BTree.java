package cyutils.btree;


import storagemanager.StorageUtils;
import storagemanager.BufferManager;
import storagemanager.StorageManager;
import storagemanager.StorageConfig;
import storagemanager.FileStorageManager;
import storagemanager.StorageManagerClient;
import storagemanager.StorageDirectoryDocument;

import java.io.IOException;
import java.io.File;

import java.util.*;

public class BTree {
	
	private String name;
	
	private StorageUtils sbStoUtils;
	
	private StorageManagerClient sbStoMgrClient;
	
	private StorageDirectoryDocument xmlParser;
	
	private int rootPageId = -1;
	
	private int sequencePageId = -1;

	// need to add the tuple class as a private member into BTree class
	private Tuple tupleDefinition;
	
	// limit page size in experiment to 256 Bytes(default)
	private int pageSizeUsed = 256;
	
	private int height = 0;
	
	/**
	 * Methods of BTree
	*/
	
	public StorageUtils getStorageUtils(){
		
		return this.sbStoUtils;
		
	}
	
	public StorageManagerClient getStorageManagerClient(){
		
		return this.sbStoMgrClient;
		
	}
	
	public int getRootPageId(){
		
		return this.rootPageId;
		
	}
	
	public int getSequencePageId(){
		
		return this.sequencePageId;
		
	}
	
	public void setRootPageId(int givenId){
		
		this.rootPageId = givenId;
		
	}
	
	public void setSequencePageId(int givenId){
		
		this.sequencePageId = givenId;
		
	}
	
	public Tuple getTupleDefinition(){
		
		return this.tupleDefinition;
		
	}
	
	public BTree(StorageUtils stoUtils, Tuple tupleDefinition){
		/**
		 *  build all storage related utils for a BTree
		 *  leave all pages initialization to InitializeBTree functions
		*/
		
		this.sbStoUtils = stoUtils;
		
		this.sbStoMgrClient = this.sbStoUtils.getXmlClient();
		
		this.xmlParser = this.sbStoUtils.getXmlParser();
		
		this.tupleDefinition = tupleDefinition;
		
	}
		
	public void initializeBTreeWithoutPageIdsInBTreeConfig(String btreeConfigFile){
		
		this.rootPageId = createNewLeaf();
		
		this.sequencePageId = this.rootPageId;
		
		this.pageSizeUsed = BTreeWithXml.getBTreePageSizeUsed(btreeConfigFile);
		
		this.name = BTreeWithXml.getBTreeName(btreeConfigFile);
		
		this.height = 1;
	}
	
	public void initializeBTreeWithPageIdsInBTreeConfig(String btreeConfigFile){
		/**
		 * initialize BTree from xml file
		 * @param fileNameWithPath
		 */
		
		this.rootPageId = BTreeWithXml.getBTreeRootPageId(btreeConfigFile);
		
		this.sequencePageId = BTreeWithXml.getBTreeSequencePageId(btreeConfigFile);
		
		this.pageSizeUsed = BTreeWithXml.getBTreePageSizeUsed(btreeConfigFile);

		this.name = BTreeWithXml.getBTreeName(btreeConfigFile);
		
		this.height = BTreeWithXml.getBTreeHeight(btreeConfigFile);
	}

	public void updateBTreeConfigAndStorageCatalog(String btreeConfigFile){
		
		BTreeWithXml.storeBTreeRootPageId(btreeConfigFile, this.rootPageId);
		
		BTreeWithXml.storeBTreeSequencePageId(btreeConfigFile, this.sequencePageId);
		
		BTreeWithXml.storeBTreePageSizeUsed(btreeConfigFile, pageSizeUsed);
		
		BTreeWithXml.storeBTreeHeight(btreeConfigFile, this.height);
		
		System.out.println("Root Id: " + this.rootPageId + "; Sequence Id: " + this.sequencePageId);
		
		xmlParser.removeXMLNode(this.name);
		
		xmlParser.addXMLDocument(this.name, this.name, String.valueOf(rootPageId), String.valueOf(1));
		
		try {
			xmlParser.writeXmlFile(new File(this.sbStoUtils.xmlCatalog_File_Path), xmlParser.getXMLDocument());
		} catch (Exception e) {
			System.out.println("Failed on updating catalog file!");
		}
	}
		
	public int bisect_leaf(int leafpageId, byte[] record){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(leafpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		int tupleLength = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int l = 0, r = tupleNum-1;
		
		while(l <= r){
			
			int mid = (l+r)/2;
			
			int midoffset = pageHeaderSize + mid*tupleLength;
			
			// compare(buffer, record, midoffset)
			int tmp = this.tupleDefinition.compare(buffer, record, midoffset);
			if(tmp == 0){
				return midoffset;	
			}else if(tmp == -1){
				l = mid+1;
			}else{
				r = mid-1;
			}
			
		}
		
		return pageHeaderSize + l*tupleLength;
		
	}
	
	public int bisect_index(int indexpageId, byte[] key){
		
		/** 
		 * we suppose that one pointer only takes 4 bytes here, 
		 * so we are using byte2Int function
		 */
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(indexpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		int keyLen = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int keyNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int pointerLen = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		int l = 0, r = keyNum-1;
		
		while(l <= r){
			
			int mid = (l+r)/2;
			
			int mid_offset = pageHeaderSize + + pointerLen + mid*(keyLen+pointerLen);
			
			// compare(buffer, record, midoffset)
			int tmp = this.tupleDefinition.compareKey(buffer, key, mid_offset);
			if(tmp == 0){
				return mid_offset;
			}else if(tmp == -1){
				l = mid+1;
			}else{
				r = mid-1;
			}
			
		}
		
		return pageHeaderSize + pointerLen + l*(keyLen + pointerLen);
		
	}
	
	public int bisect_pointer(int indexpageId, byte[] key){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(indexpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		int keyLen = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int keyNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int pointerLen = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		int l = 0, r = keyNum-1;
		
		while(l <= r){
			
			int mid = (l+r)/2;
			
			int mid_offset = pageHeaderSize + + pointerLen + mid*(keyLen+pointerLen);
			
			// compare(buffer, record, midoffset)
			int tmp = this.tupleDefinition.compareKey(buffer, key, mid_offset);
			if(tmp == 0){
				return ReWriUtils.byteArrayToInt(buffer, mid_offset-pointerLen);
			}else if(tmp == -1){
				l = mid+1;
			}else{
				r = mid-1;
			}
			
		}
		
		return  ReWriUtils.byteArrayToInt(buffer, pageHeaderSize+l*(keyLen+pointerLen));
	}
	
	public void addOneTupleToSortedLeafPage(int leafpageId, int offset, byte[] record){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(leafpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return;
		}
		
		int sbStoPageSize = this.sbStoMgrClient.getXmlSto().getPageSize()*1024;
		
		int pageSizeUsed = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_SIZE_USED_PTR);
		
		int avlbPageSize = Math.min(sbStoPageSize, pageSizeUsed);
		
		int tupleLength = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		if((tupleNum+1)*tupleLength + pageHeaderSize > avlbPageSize){
			System.out.println("This Page is already full, we need split");
			return;
		}
				
		int sequenceTail = pageHeaderSize+tupleLength*tupleNum;
		
		for(int ii = sequenceTail-1; ii >= offset; ii--){
			
			buffer[ii+tupleLength] = buffer[ii];
			
		}
		
		for(int ii = 0; ii < tupleLength; ii++){
			
			buffer[ii+offset] = record[ii];
			
		}
		
		/**
		 * Update Tuple Number in the page header
		 */
		ReWriUtils.intToByteArray(tupleNum+1, buffer, Constant.TUPLE_NUM_PTR);
		
		try{
			sbStoMgrClient.writePagewithoutPin(leafpageId, buffer);
			sbStoMgrClient.flushBuffer();
		} catch(IOException e){
			System.out.println("write page error");
		}
		
	}
	
	public void addOneIndexToEmptyIndexPage(int indexpageId, int leftPtr, byte[] key, int rightPtr){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(indexpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return;
		}
		
		int offset = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int tupleLength = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int pointerLength = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		if(pointerLength == 4){

			ReWriUtils.intToByteArray(leftPtr, buffer, offset);
			
			ReWriUtils.intToByteArray(rightPtr, buffer, offset+pointerLength+tupleLength);
			
		}
		
		for(int ii = 0; ii < tupleLength; ii++){
			
			buffer[ii+pointerLength+offset] = key[ii];
			
		}
		
		/**
		 * Update Tuple Number in the page header
		 */
		ReWriUtils.intToByteArray(1, buffer, Constant.TUPLE_NUM_PTR);
		
		try{
			this.sbStoMgrClient.writePagewithoutPin(indexpageId, buffer);
			this.sbStoMgrClient.flushBuffer();
		} catch(IOException e){
			System.out.println("write page error");
		}
		
	}
	
	public void addOneIndexToSortedIndexPage(int indexpageId, int offset, byte[] key, int rightPtr){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(indexpageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return;
		}
		
		int sbStoPageSize = this.sbStoMgrClient.getXmlSto().getPageSize()*1024;
		
		int pageSizeUsed = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_SIZE_USED_PTR);
		
		int avlbPageSize = Math.min(sbStoPageSize, pageSizeUsed);
		
		int tupleLength = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int pointerLength = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		if((tupleNum+1)*tupleLength + (tupleNum+2)*pointerLength + pageHeaderSize > avlbPageSize){
			System.out.println("This Page is already full, we need split");
			return;
		}
		
		int indexTail = pageHeaderSize + tupleNum*tupleLength + (tupleNum+1)*pointerLength;
		
		for(int ii = indexTail-1; ii >= offset; ii--){
			
			buffer[ii+pointerLength+tupleLength] = buffer[ii];
			
		}
		
		for(int ii = 0; ii < tupleLength; ii++){
			
			buffer[ii+offset] = key[ii];
			
		}
		
		if(pointerLength == 4){
		
			ReWriUtils.intToByteArray(rightPtr, buffer, offset+tupleLength);			
		
		}
		
		/**
		 * Update Tuple Number in the page header
		 */
		ReWriUtils.intToByteArray(tupleNum+1, buffer, Constant.TUPLE_NUM_PTR);
		
		try{
			sbStoMgrClient.writePagewithoutPin(indexpageId, buffer);
			sbStoMgrClient.flushBuffer();
		} catch(IOException e){
			System.out.println("write page error");
		}
		
	}
	
	public List<Integer> BTreeSearch(int rootpageId, byte[] record){
		
		List<Integer> stk = new ArrayList<Integer>();
		
		int node = rootpageId;
		
		while(!isLeafPage(node)){
			
			stk.add(node);
			
			node = bisect_pointer(node, this.tupleDefinition.generateKey(record));
			
		}
		
		stk.add(node);
		
		return stk;
		
	}
	
	public boolean isLeafPage(int pageId){
		/**
		 * true represents for leaf(sequence) page, false represents for index page
		 */
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return true;
		}
		
		int pageType = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		if(pageType == 0){
			
			return true;
		
		}
		
		return false;
		
	}
	
	public int createNewLeaf(){
		/**
		 * allocate new leaf page id via storage manager client
		 * and write corresponding numbers into page header
		 */
		
		int newLeafId = this.sbStoMgrClient.allocatePage();
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(newLeafId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		ReWriUtils.intToByteArray(Constant.PAGE_HEADER_SIZE, buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		ReWriUtils.intToByteArray(newLeafId, buffer, Constant.CURRENT_PAGE_PTR);
		
		ReWriUtils.intToByteArray(-1, buffer, Constant.NEXT_PAGE_PTR);
		
		ReWriUtils.intToByteArray(0, buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		ReWriUtils.intToByteArray(this.tupleDefinition.getLength(), buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		ReWriUtils.intToByteArray(0, buffer, Constant.TUPLE_NUM_PTR);
		
		ReWriUtils.intToByteArray(this.pageSizeUsed, buffer, Constant.PAGE_SIZE_USED_PTR);

		try{
			this.sbStoMgrClient.writePagewithoutPin(newLeafId, buffer);
			this.sbStoMgrClient.flushBuffer();
		} catch(IOException e){
			System.out.println("write page error");
		}
		
		return newLeafId;
		
	}
	
	public int createNewIndex(){
		/**
		 * allocate new index page id via storage manager client
		 * and write corresponding numbers into page header
		 */
		
		int newIndexId = this.sbStoMgrClient.allocatePage();
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(newIndexId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		ReWriUtils.intToByteArray(Constant.PAGE_HEADER_SIZE, buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		ReWriUtils.intToByteArray(newIndexId, buffer, Constant.CURRENT_PAGE_PTR);
		
		ReWriUtils.intToByteArray(-1, buffer, Constant.NEXT_PAGE_PTR);
		
		ReWriUtils.intToByteArray(Constant.INDEX_PTR_SIZE, buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		ReWriUtils.intToByteArray(this.tupleDefinition.getKeyLength(), buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		ReWriUtils.intToByteArray(0, buffer, Constant.TUPLE_NUM_PTR);
		
		ReWriUtils.intToByteArray(this.pageSizeUsed, buffer, Constant.PAGE_SIZE_USED_PTR);

		try{
			this.sbStoMgrClient.writePagewithoutPin(newIndexId, buffer);
			this.sbStoMgrClient.flushBuffer();
		} catch(IOException e){
			System.out.println("write page error");
		}
		
		return newIndexId;
		
	}
	
	public boolean isPageFull(int pageId){
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return true;
		}
		
		int pageType = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		int tupleLength = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int avlbPageSize = Math.min(this.sbStoMgrClient.getXmlSto().getPageSize()*1024, 
				ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_SIZE_USED_PTR));
		
		if(pageHeaderSize + (tupleNum+1)*tupleLength + (tupleNum+2)*pageType > avlbPageSize){
			return true;
		}
		
		return false;
		
	}
	
	public int printPageWithinPageSizeUsed(int pageId){
		
		System.out.println("Print Page Id: " + pageId);
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		int sbStoPageSize = this.sbStoMgrClient.getXmlSto().getPageSize()*1024;
		
		int sbPageSize = Math.min(this.pageSizeUsed, sbStoPageSize);
		
		System.out.println("bytes available in this page: " + sbPageSize);
		
		for(int ii = 0; ii < sbPageSize; ii++){
			
			System.out.print(buffer[ii] + ", ");
			
		}
				
		System.out.println();
		
		int nextpageId = ReWriUtils.byteArrayToInt(buffer, Constant.NEXT_PAGE_PTR);
		
		return nextpageId;
	}
	
	public int printAllTuples(int pageId) {
		byte[] buffer;
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
		}
		
		int offset = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		
		int sbStoPageSize = this.sbStoMgrClient.getXmlSto().getPageSize()*1024;
		
		int sbPageSize = Math.min(this.pageSizeUsed, sbStoPageSize);
		
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		
		int tupleAttributeNum = this.tupleDefinition.getAttributeNum();
		
		for (int ii = 0; ii < tupleNum; ii++) {
			
			StringBuilder builder = new StringBuilder();
			
			for (int jj = 0; jj < tupleAttributeNum; jj++) {
				
				String name = tupleDefinition.getTupleAttributes().get(jj).getName();
				
				String type = tupleDefinition.getTupleAttributes().get(jj).getType();
				
				int attributeLength = tupleDefinition.getTupleAttributes().get(jj).getLength();
				
				builder.append(name).append(": ");
				
				if (type.equals("Integer")) {
				
					builder.append(ReWriUtils.byteArrayToInt(buffer, offset));
				} else {
					
					builder.append(ReWriUtils.byteArrayToString(buffer, offset, offset+attributeLength-1).trim());
				}
				
				builder.append(", ");
				
				offset += attributeLength;
			}
			
			System.out.println(builder.toString());
		}
		
		int nextpageId = ReWriUtils.byteArrayToInt(buffer, Constant.NEXT_PAGE_PTR);
		
		return nextpageId;
	}
	
	public int printPageId(int pageId){
		System.out.println("Print Page Id: " + pageId);
		
		byte[] buffer;
		
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return -1;
		}
		
		int nextpageId = ReWriUtils.byteArrayToInt(buffer, Constant.NEXT_PAGE_PTR);
		
		return nextpageId;
	}
	
	public void printIndexPage(int pageId){
		System.out.println("Print Page Id: " + pageId);
		
		byte[] buffer;
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		} catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
		}
		
		int sbStoPageSize = this.sbStoMgrClient.getXmlSto().getPageSize()*1024;
		int sbPageSize = Math.min(this.pageSizeUsed, sbStoPageSize);
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		int keyLen = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		int keyNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		int pointerLen = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		
		for(int ii = 0; ii < keyNum+1; ii++){
			System.out.print(ReWriUtils.byteArrayToInt(buffer, pageHeaderSize+ii*(keyLen+pointerLen)));
			System.out.print(", ");
		}
		System.out.println();
		
		for(int ii = 0; ii < keyNum; ii++){
			System.out.print(ReWriUtils.byteArrayToInt(buffer, pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)));
			System.out.print(", ");
		}
		System.out.println();
		
	}
	
//	private void temp(int rootpageId, byte[] record){
//		List<Integer> parentStack = BTreeSearch(rootpageId, record);
//		int curPageId = parentStack.get(parentStack.size()-1);
//		parentStack.remove(parentStack.size()-1);
//		int curOffset = bisect_leaf(curPageId, record);
//		
//		if(!this.isPageFull(curPageId)){
//			this.addOneTupleToSortedLeafPage(curPageId, curOffset, record);
//		}else{
//			List<Object> packed = this.splitLeafNode(curPageId, curOffset, record);
//			int leftPtr = (Integer) packed.get(0);
//			byte[] midkey = (byte []) packed.get(1);
//			int rightPtr = (Integer) packed.get(2);
//			
//			while(!parentStack.isEmpty()){
//				curPageId = parentStack.get(parentStack.size()-1);
//				parentStack.remove(parentStack.size()-1);
//				curOffset = this.bisect_index(curPageId, midkey);
//				if(!this.isPageFull(curPageId)){
//					this.addOneIndexToSortedIndexPage(curPageId, curOffset, midkey, rightPtr);
//					return;
//				}else{
//					packed = this.splitIndexNode(curPageId, curOffset, midkey, rightPtr);
//					leftPtr = (Integer) packed.get(0);
//					midkey = (byte []) packed.get(1);
//					rightPtr = (Integer) packed.get(2);
//				}
//			}
//			int rootpageId_new = this.createNewIndex();
//			this.addOneIndexToEmptyIndexPage(rootpageId_new, leftPtr, midkey, rightPtr);
//			this.rootPageId = rootpageId_new;
//		}
//	}
	
	
	private void bulkload(List<Integer> parentStack, byte[] record) {
		
		List<Integer> tmpStack = new ArrayList<Integer>();
		
		int curPageId = parentStack.get(parentStack.size()-1);
		parentStack.remove(parentStack.size()-1);
		int curOffset = bisect_leaf(curPageId, record);
		
		if(!this.isPageFull(curPageId)){
			this.addOneTupleToSortedLeafPage(curPageId, curOffset, record);
			parentStack.add(curPageId);
		}else{			
			int newLeafNodeId = createNewLeaf();
			int newLeafNodeOffset = bisect_leaf(newLeafNodeId, record);
			this.addOneTupleToSortedLeafPage(newLeafNodeId, newLeafNodeOffset, record);
			byte[] curPageBuffer;
			try{
				curPageBuffer = this.sbStoMgrClient.readPagewithoutPin(curPageId);
			}catch(IOException e){
				curPageBuffer = new byte[0];
				System.out.println("read page error");
				return;
			}
			ReWriUtils.intToByteArray(newLeafNodeId, curPageBuffer, Constant.NEXT_PAGE_PTR);
			
			try{
				this.sbStoMgrClient.writePagewithoutPin(curPageId, curPageBuffer);
				this.sbStoMgrClient.flushBuffer();
			}catch(IOException e){
				System.out.println("write page error");
			}
			
			int leftPtr = curPageId;
			byte[] midkey = this.tupleDefinition.generateKey(record);
			int rightPtr = newLeafNodeId;
			
			tmpStack.add(newLeafNodeId);
			
			
			while(!parentStack.isEmpty()){
				curPageId = parentStack.get(parentStack.size()-1);
				parentStack.remove(parentStack.size()-1);
				curOffset = this.bisect_index(curPageId, midkey);
				
				if(!this.isPageFull(curPageId)){
					this.addOneIndexToSortedIndexPage(curPageId, curOffset, midkey, rightPtr);
					tmpStack.add(curPageId);
					
					while(!tmpStack.isEmpty()) {
						int tmpPageId = tmpStack.get(tmpStack.size()-1);
						parentStack.add(tmpPageId);
						tmpStack.remove(tmpStack.size()-1);
					}
					return;
				}else{
					int newIndexNodeId = createNewIndex();
					int newIndexNodeOffset = bisect_index(newIndexNodeId, midkey);
					this.addOneIndexToSortedIndexPage(newIndexNodeId, newIndexNodeOffset, midkey, rightPtr);
					tmpStack.add(newIndexNodeId);
					
					leftPtr = curPageId;
					rightPtr = newIndexNodeId;
				}
			}
			int rootpageId_new = this.createNewIndex();
			this.addOneIndexToEmptyIndexPage(rootpageId_new, leftPtr, midkey, rightPtr);
			this.rootPageId = rootpageId_new;
			this.height += 1;
			tmpStack.add(this.rootPageId);
			
			while(!tmpStack.isEmpty()) {
				int tmpPageId = tmpStack.get(tmpStack.size()-1);
				parentStack.add(tmpPageId);
				tmpStack.remove(tmpStack.size()-1);
			}
		}
	}
	
	private List<Object> splitLeafNode(int pageId, int offset, byte[] record){
		List<Object> res = new ArrayList<Object>();
		byte[] buffer;
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		}catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return res;
		}
		int pageId_newleaf = createNewLeaf();
		byte[] buffer_newleaf;
		try{
			buffer_newleaf = this.sbStoMgrClient.readPagewithoutPin(pageId_newleaf);
		}catch(IOException e){
			buffer_newleaf = new byte[0];
			System.out.println("read page error");
			return res;
		}
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		int tupleNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		int tupleLen = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		int mid_index = tupleNum/2;
		int offset_index = (offset-pageHeaderSize)/tupleLen;
		
		if(offset_index <= mid_index){
			for(int ii = mid_index; ii < tupleNum; ii++){
				int jj = ii - (mid_index);
				for(int kk = 0; kk < tupleLen; kk++){
					buffer_newleaf[pageHeaderSize+jj*tupleLen+kk] = buffer[pageHeaderSize+ii*tupleLen+kk];
				}
			}
			for(int ii = mid_index-1; ii >= offset_index; ii--){
				for(int kk = 0; kk < tupleLen; kk++){
					buffer[pageHeaderSize+(ii+1)*tupleLen+kk] = buffer[pageHeaderSize+ii*tupleLen+kk];
				}
			}
			for(int kk = 0; kk < tupleLen; kk++){
				buffer[pageHeaderSize+offset_index*tupleLen+kk] = record[kk];
			}
		}else{
			for(int ii = mid_index+1; ii < offset_index; ii++){
				int jj = ii - (mid_index+1);
				for(int kk = 0; kk < tupleLen; kk++){
					buffer_newleaf[pageHeaderSize+jj*tupleLen+kk] = buffer[pageHeaderSize+ii*tupleLen+kk];
				}
			}
			for(int kk = 0; kk < tupleLen; kk++){
				buffer_newleaf[pageHeaderSize+(offset_index-(mid_index+1))*tupleLen+kk] = record[kk];
			}
			for(int ii = offset_index; ii < tupleNum; ii++){
				int jj = ii - mid_index;
				for(int kk = 0; kk < tupleLen; kk++){
					buffer_newleaf[pageHeaderSize+jj*tupleLen+kk] = buffer[pageHeaderSize+ii*tupleLen+kk];
				}
			}
		}// end of else
		
		ReWriUtils.intToByteArray((tupleNum+2)/2, buffer, Constant.TUPLE_NUM_PTR);
		ReWriUtils.intToByteArray((tupleNum+1)/2, buffer_newleaf, Constant.TUPLE_NUM_PTR);
		int curpage_nextpage = ReWriUtils.byteArrayToInt(buffer, Constant.NEXT_PAGE_PTR);
		ReWriUtils.intToByteArray(curpage_nextpage, buffer_newleaf, Constant.NEXT_PAGE_PTR);
		ReWriUtils.intToByteArray(pageId_newleaf, buffer, Constant.NEXT_PAGE_PTR);
		res.add(pageId);
		byte[] key_generate = this.tupleDefinition.generateKey(buffer, pageHeaderSize+(tupleNum/2)*tupleLen);
		res.add(key_generate);
		res.add(pageId_newleaf);
		
		try{
			this.sbStoMgrClient.writePagewithoutPin(pageId, buffer);
			this.sbStoMgrClient.flushBuffer();
		}catch(IOException e){
			System.out.println("write page error");
		}
		
		try{
			this.sbStoMgrClient.writePagewithoutPin(pageId_newleaf, buffer_newleaf);
			this.sbStoMgrClient.flushBuffer();
		}catch(IOException e){
			System.out.println("write page error");
		}
		
		return res;
	}

	private List<Object> splitIndexNode(int pageId, int offset, byte[] key, int rightPtr){
		List<Object> res = new ArrayList<Object>();
		byte[] buffer;
		try{
			buffer = this.sbStoMgrClient.readPagewithoutPin(pageId);
		}catch(IOException e){
			buffer = new byte[0];
			System.out.println("read page error");
			return res;
		}
		int pageId_newindex = createNewIndex();
		byte[] buffer_newindex;
		try{
			buffer_newindex = this.sbStoMgrClient.readPagewithoutPin(pageId_newindex);
		}catch(IOException e){
			buffer_newindex = new byte[0];
			System.out.println("read page error");
			return res;
		}
		
		int pageHeaderSize = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_HEADER_SIZE_PTR);
		int keyNum = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_NUM_PTR);
		int keyLen = ReWriUtils.byteArrayToInt(buffer, Constant.TUPLE_OR_KEY_LEN_PTR);
		int pointerLen = ReWriUtils.byteArrayToInt(buffer, Constant.PAGE_TYPE_PTR_AND_PTR_LEN);
		int mid_index = (keyNum+1)/2;
		int offset_index = (offset-(pageHeaderSize+pointerLen))/(keyLen+pointerLen);
		
		int ptr_left = pageId;
		byte[] midkey;
		int ptr_right = pageId_newindex;
		
		if(offset_index == mid_index){
			ReWriUtils.intToByteArray(rightPtr, buffer_newindex, pageHeaderSize);
			for(int ii = mid_index; ii < keyNum; ii++){
				int jj = ii - mid_index;
				for(int kk = 0; kk < keyLen+pointerLen; kk++){
					buffer_newindex[pageHeaderSize+pointerLen+jj*(keyLen+pointerLen)+kk] 
							= buffer[pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)+kk];
				}
			}
			midkey = key;
		}else if(offset_index < mid_index){
			for(int kk = 0; kk < pointerLen; kk++){
				buffer_newindex[pageHeaderSize+kk]
						= buffer[pageHeaderSize+mid_index*(keyLen+pointerLen)+kk];
			}
			for(int ii = mid_index; ii < keyNum; ii++){
				int jj = ii - mid_index;
				for(int kk = 0; kk < keyLen+pointerLen; kk++){
					buffer_newindex[pageHeaderSize+pointerLen+jj*(keyLen+pointerLen)+kk]
							= buffer[pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)+kk];
				}
			}
			midkey = this.tupleDefinition.generateKey(buffer, pageHeaderSize+pointerLen+(keyLen+pointerLen)*(mid_index-1));
			for(int ii = mid_index-2; ii >= offset_index; ii--){
				for(int kk = 0; kk < keyLen+pointerLen; kk++){
					buffer[pageHeaderSize+pointerLen+(ii+1)*(keyLen+pointerLen)+kk]
							= buffer[pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)+kk];
				}
			}
			for(int kk = 0; kk < keyLen; kk++){
				buffer[pageHeaderSize+pointerLen+offset_index*(keyLen+pointerLen)+kk] = key[kk];
			}
			ReWriUtils.intToByteArray(rightPtr, buffer, pageHeaderSize+(offset_index+1)*(keyLen+pointerLen));
		}else{
			for(int kk = 0; kk < pointerLen; kk++){
				buffer_newindex[pageHeaderSize+kk]
						= buffer[pageHeaderSize+(mid_index+1)*(keyLen+pointerLen)+kk];
			}
			for(int ii = mid_index+1; ii < offset_index; ii++){
				int jj = ii - (mid_index+1);
				for(int kk = 0; kk < keyLen+pointerLen; kk++){
					buffer_newindex[pageHeaderSize+pointerLen+jj*(keyLen+pointerLen)+kk]
							= buffer[pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)+kk];
				}
			}
			for(int kk = 0; kk < keyLen; kk++){
				buffer_newindex[pageHeaderSize+pointerLen+(offset_index-(mid_index+1))*(keyLen+pointerLen)+kk] = key[kk];
			}
			ReWriUtils.intToByteArray(rightPtr, buffer_newindex, pageHeaderSize+(offset_index-mid_index)*(keyLen+pointerLen));
			for(int ii = offset_index; ii < keyNum; ii++){
				int jj = ii - mid_index;
				for(int kk = 0; kk < keyLen+pointerLen; kk++){
					buffer_newindex[pageHeaderSize+pointerLen+jj*(keyLen+pointerLen)+kk]
							= buffer[pageHeaderSize+pointerLen+ii*(keyLen+pointerLen)+kk];
				}
			}
			midkey = this.tupleDefinition.generateKey(buffer, pageHeaderSize+pointerLen+mid_index*(keyLen+pointerLen));
		}
		
		res.add(ptr_left);
		res.add(midkey);
		res.add(ptr_right);
		
		ReWriUtils.intToByteArray((keyNum+1)/2, buffer, Constant.TUPLE_NUM_PTR);
		ReWriUtils.intToByteArray(keyNum/2, buffer_newindex, Constant.TUPLE_NUM_PTR);
		
		try{
			this.sbStoMgrClient.writePagewithoutPin(pageId, buffer);
			this.sbStoMgrClient.flushBuffer();
		}catch(IOException e){
			System.out.println("write page error");
		}
		
		try{
			this.sbStoMgrClient.writePagewithoutPin(pageId_newindex, buffer_newindex);
			this.sbStoMgrClient.flushBuffer();
		}catch(IOException e){
			System.out.println("write page error");
		}
		
		return res;
	}
	
	public void BTreeBulkLoading(Iterator iter) {
		List<Integer> parentStack = new ArrayList<Integer>();
		parentStack.add(this.rootPageId);
		
		while(iter.hasNext()){
			byte[] tuple = (byte[]) iter.next();
			this.bulkload(parentStack, tuple);
		}
	}
	
	public void BTreeInsert(int rootpageId, byte[] record){
		List<Integer> parentStack = BTreeSearch(rootpageId, record);
		int curPageId = parentStack.get(parentStack.size()-1);
		parentStack.remove(parentStack.size()-1);
		int curOffset = bisect_leaf(curPageId, record);
		
		if(!this.isPageFull(curPageId)){
			this.addOneTupleToSortedLeafPage(curPageId, curOffset, record);
		}else{
			List<Object> packed = this.splitLeafNode(curPageId, curOffset, record);
			int leftPtr = (Integer) packed.get(0);
			byte[] midkey = (byte []) packed.get(1);
			int rightPtr = (Integer) packed.get(2);
			
			while(!parentStack.isEmpty()){
				curPageId = parentStack.get(parentStack.size()-1);
				parentStack.remove(parentStack.size()-1);
				curOffset = this.bisect_index(curPageId, midkey);
				if(!this.isPageFull(curPageId)){
					this.addOneIndexToSortedIndexPage(curPageId, curOffset, midkey, rightPtr);
					return;
				}else{
					packed = this.splitIndexNode(curPageId, curOffset, midkey, rightPtr);
					leftPtr = (Integer) packed.get(0);
					midkey = (byte []) packed.get(1);
					rightPtr = (Integer) packed.get(2);
				}
			}
			int rootpageId_new = this.createNewIndex();
			this.addOneIndexToEmptyIndexPage(rootpageId_new, leftPtr, midkey, rightPtr);
			this.rootPageId = rootpageId_new;
			this.height += 1;
		}
	}
	
}



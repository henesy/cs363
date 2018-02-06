package cyclients.split;

import java.io.File;
import java.util.Random;
import storagemanager.StorageDirectoryDocument;
import storagemanager.StorageManagerClient;
import storagemanager.StorageUtils;

public class DataGenerator {
		
	private String configFile;
	private StorageUtils storUtils;
	private StorageManagerClient smc;
	private StorageDirectoryDocument xmlParser;
	private String chainName;
	
	public DataGenerator(StorageUtils storUtils, String configFile, String chainName) {
		this.storUtils = storUtils;
		smc = storUtils.getXmlClient();	
		xmlParser = storUtils.getXmlParser();
		this.chainName = chainName;
		this.configFile = configFile;
		Constant.initializeConstant(configFile);		
	}
	
	public int dataGen() throws Exception {
		
		int totalItemNum = 0;
		int currPageTupleNum = 0;
		int pageNum = 0;
		int rootPageId = -1;
		int currPageId = -1;
		int nextPageId = -1;
		int pageLoad = Constant.HEADERSIZE;    // current page load
		
		int bufferPageSize = storUtils.getStorageConfig().getPageSize();  // in KB
		// System.out.println(bufferPageSize * 1024);
		byte[] buffer = new byte[bufferPageSize * 1024];
				
		// initialize a start page
		rootPageId = smc.allocatePage();
		System.out.println("rootPageId is " + rootPageId);
		currPageId = rootPageId;
		pageNum++;
		
		// write data to page
		while (totalItemNum < Constant.NUMOFITEMS) {
			
			byte[] id = idGenerator();
			if ((pageLoad + 4) <= Constant.PAGESIZE) {  // page can contain the new tuple
				System.arraycopy(id, 0, buffer, pageLoad, 4);
				pageLoad = pageLoad + 4;
				currPageTupleNum++;
				totalItemNum++;	
			}
			else {    // need to allocate a new page	
				nextPageId = smc.allocatePage();
				System.out.println("new page id is: " + nextPageId);
				// write header
				byte[] nextPageIdAsBytes = IntByte.intToByteArray(nextPageId);
				byte[] tupleNumAsBytes = IntByte.intToByteArray(currPageTupleNum);
				byte[] bytesUsedAsBytes = IntByte.intToByteArray(pageLoad);
				System.arraycopy(tupleNumAsBytes, 0, buffer, Constant.NUMOFTUPLES_OFFSET, 4);
				System.arraycopy(bytesUsedAsBytes, 0, buffer, Constant.NUMOFBYTESUSED_OFFSET, 4);
				System.arraycopy(nextPageIdAsBytes, 0, buffer, Constant.NEXTPAGEID_OFFSET, 4);
				pageNum++;
				
				// write current page
				smc.writePagewithoutPin(currPageId, buffer);
				
				// initialize next page
				currPageTupleNum = 0;
				pageLoad = Constant.HEADERSIZE;
				currPageId = nextPageId;
				
				// write data to the next page
				System.arraycopy(id, 0, buffer, pageLoad, 4);
				pageLoad = pageLoad + 4;
				currPageTupleNum++;
				totalItemNum++;
			}
		}
		
		if (nextPageId == -1) {  // if we only have 1 page, need to report 
			System.err.println("Page List has only one page.");
		}
		else {
			nextPageId = -1;
		}
		
		// write the last page
		System.out.println("Write the last page.");
		byte[] nextPageIdAsBytes = IntByte.intToByteArray(nextPageId);
		byte[] tupleNumAsBytes = IntByte.intToByteArray(currPageTupleNum);
		byte[] bytesUsedAsBytes = IntByte.intToByteArray(pageLoad);
		System.arraycopy(tupleNumAsBytes, 0, buffer, Constant.NUMOFTUPLES_OFFSET, 4);
		System.arraycopy(bytesUsedAsBytes, 0, buffer, Constant.NUMOFBYTESUSED_OFFSET, 4);
		System.arraycopy(nextPageIdAsBytes, 0, buffer, Constant.NEXTPAGEID_OFFSET, 4);
		smc.writePagewithoutPin(currPageId, buffer);

		// flush the buffer(s) of the buffer manager to the disk
		smc.flushBuffer(); 
		smc.writeBitMap();
				   
		// update the catalog file
		xmlParser.addXMLDocument(chainName, chainName, Integer.toString(rootPageId), Integer.toString(pageNum));
		xmlParser.writeXmlFile(new File(storUtils.xmlCatalog_File_Path), xmlParser.getXMLDocument());
		return rootPageId;
	}
	
	
	
	public byte[] idGenerator() {
		Random random = new Random();
		int id = 1 + random.nextInt(1000);  // id ranges [1, 1000]
		return IntByte.intToByteArray(id);	
	}

}

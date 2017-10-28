package cyclients.split;

import java.io.File;

import storagemanager.StorageDirectoryDocument;
import storagemanager.StorageManagerClient;
import storagemanager.StorageUtils;

public class splitChain {
	
	private int rootPageId;	
	private String configFile;
	private StorageUtils storUtils;
	private StorageManagerClient smc;
	private StorageDirectoryDocument xmlParser;
	private String oddChainName;
	private String evenChainName;
	private String chainName;
	
	public splitChain(StorageUtils storUtils, String configFile, int rootPageId, String chainName, String oddChainName, String evenChainName) {
		this.storUtils = storUtils;
		this.rootPageId = rootPageId;
		smc = storUtils.getXmlClient();
		xmlParser = storUtils.getXmlParser();
		this.oddChainName = oddChainName;
		this.evenChainName = evenChainName;
		this.chainName = chainName;
		this.configFile = configFile;
		Constant.initializeConstant(configFile);
	}
	
// Implement the following
	public void split() throws Exception {	
		int currentPageId = rootPageId;	//get the root page id
		int evenPageNumber = 0;	//count the total number of pages holding even numbers
		int oddPageNumber = 0;	//count the total number of pages holding odd numbers
		int pageLoadEven = Constant.HEADERSIZE;	//page load for even buffer
		int pageLoadOdd = Constant.HEADERSIZE;	//page load for odd buffer
		int rootEvenPageId = -1;
		int nextEvenPageId = -1;
		int currentEvenPageId = -1;
		int rootOddPageId = -1;
		int nextOddPageId = -1;
		int currentOddPageId = -1;
		int currentEvenPageTupleNumber = 0;
		int currentOddPageTupleNumber = 0;
		int bufferPageSize = storUtils.getStorageConfig().getPageSize();	//get the desired page size and buffer size
		
		byte[] readBuffer = new byte[bufferPageSize * 1024];		//allocate reading buffer;
		byte[] buffer_even = new byte[bufferPageSize * 1024];	//allocate buffer for even numbers;
		byte[] buffer_odd = new byte[bufferPageSize * 1024];	//allocate buffer for odd numbers;
		byte[] read = new byte[4];	//hold the value read from buffer
		byte[] nextEvenPageIdAsByte = new byte[4];
		byte[] evenTupleNumberAsByte = new byte[4];
		byte[] evenByteUsedAsByte = new byte[4];
		byte[] nextOddPageIdAsByte = new byte[4];
		byte[] oddTupleNumberAsByte = new byte[4];
		byte[] oddByteUsedAsByte = new byte[4];
		
		rootEvenPageId = currentEvenPageId = smc.allocatePage();
		evenPageNumber++;
		rootOddPageId = currentOddPageId = smc.allocatePage();
		oddPageNumber++;
		
		while (currentPageId != -1)
		{
			readBuffer = smc.readPagewithoutPin(currentPageId);	//read a page into reading buffer
			System.out.println("Currently reading page " + currentPageId);	
			int tupleNumber = readTupleNum(readBuffer);	//retrieve the total number of tuples in this page
			
			for (int i = 0; i < tupleNumber; i++)
			{
				System.arraycopy(readBuffer, Constant.HEADERSIZE + i * 4, read, 0, 4);	//read a tuple
				if (IntByte.byteArrayToInt(read) % 2 == 0)	//if the tuple is an even integer
				{
					if (pageLoadEven + 4 <= Constant.PAGESIZE)	//if current page for even numbers has more space
					{
						System.arraycopy(read, 0, buffer_even, pageLoadEven, 4);
						pageLoadEven += 4;
						currentEvenPageTupleNumber++;
					}
					else	//if current page for even number is full, then generate a new page
					{
						nextEvenPageId = smc.allocatePage();	//generate a new page for even numbers
						
						//write header into buffer
						nextEvenPageIdAsByte = IntByte.intToByteArray(nextEvenPageId);
						evenTupleNumberAsByte = IntByte.intToByteArray(currentEvenPageTupleNumber);
						evenByteUsedAsByte = IntByte.intToByteArray(pageLoadEven);
						System.arraycopy(nextEvenPageIdAsByte, 0, buffer_even, Constant.NEXTPAGEID_OFFSET, 4);
						System.arraycopy(evenTupleNumberAsByte, 0, buffer_even, Constant.NUMOFTUPLES_OFFSET, 4);
						System.arraycopy(evenByteUsedAsByte, 0, buffer_even, Constant.NUMOFBYTESUSED_OFFSET, 4);
						evenPageNumber++;
						
						//write the even buffer into page
						smc.writePagewithoutPin(currentEvenPageId, buffer_even);
						
						//initialize the new page
						currentEvenPageId = nextEvenPageId;
						nextEvenPageId = -1;
						pageLoadEven = Constant.HEADERSIZE;
						currentEvenPageTupleNumber = 0;
						
						//write this tuple into the new page
						System.arraycopy(read, 0,  buffer_even, pageLoadEven, 4);
						pageLoadEven += 4;
						currentEvenPageTupleNumber++;
					}
				}
				else
				{
					// ****************** YOU WRITE THIS CODE ****************** 
				}
			}
			
			smc.deallocatePage(currentPageId);	//deallocate the page which has been red
			currentPageId = readNextPageId(readBuffer);	//direct current page to next page
		}
		
		//write last even page into page file
		System.out.println("Write the last even page.");
		nextEvenPageIdAsByte = IntByte.intToByteArray(nextEvenPageId);
		evenTupleNumberAsByte = IntByte.intToByteArray(currentEvenPageTupleNumber);
		evenByteUsedAsByte = IntByte.intToByteArray(pageLoadEven);
		System.arraycopy(nextEvenPageIdAsByte, 0, buffer_even, Constant.NEXTPAGEID_OFFSET, 4);
		System.arraycopy(evenTupleNumberAsByte, 0, buffer_even, Constant.NUMOFTUPLES_OFFSET, 4);
		System.arraycopy(evenByteUsedAsByte, 0, buffer_even, Constant.NUMOFBYTESUSED_OFFSET, 4);
		smc.writePagewithoutPin(currentEvenPageId, buffer_even);
		
		//write last odd page into page file
		System.out.println("Write the last odd page.");
		nextOddPageIdAsByte = IntByte.intToByteArray(nextOddPageId);
		oddTupleNumberAsByte = IntByte.intToByteArray(currentOddPageTupleNumber);
		oddByteUsedAsByte = IntByte.intToByteArray(pageLoadOdd);
		System.arraycopy(nextOddPageIdAsByte, 0, buffer_odd, Constant.NEXTPAGEID_OFFSET, 4);
		System.arraycopy(oddTupleNumberAsByte, 0, buffer_odd, Constant.NUMOFTUPLES_OFFSET, 4);
		System.arraycopy(oddByteUsedAsByte, 0, buffer_odd, Constant.NUMOFBYTESUSED_OFFSET, 4);
		smc.writePagewithoutPin(currentOddPageId, buffer_odd);
		
		// flush the buffer(s) of the buffer manager to the disk
		smc.flushBuffer(); 
		smc.writeBitMap();
		
		// update the catalog file
		xmlParser.addXMLDocument(evenChainName, evenChainName, Integer.toString(rootEvenPageId), Integer.toString(evenPageNumber));
		xmlParser.writeXmlFile(new File(storUtils.xmlCatalog_File_Path), xmlParser.getXMLDocument());
		xmlParser.addXMLDocument(oddChainName, oddChainName, Integer.toString(rootOddPageId), Integer.toString(oddPageNumber));
		xmlParser.writeXmlFile(new File(storUtils.xmlCatalog_File_Path), xmlParser.getXMLDocument());
		
		xmlParser.removeXMLNode(chainName);	//try to delete original chain from XML document, but failed.
	}
	
	
	int readTupleNum(byte[] buffer) {
		byte[] tupleNum = new byte[4];
		System.arraycopy(buffer, Constant.NUMOFTUPLES_OFFSET, tupleNum, 0, 4);
		return IntByte.byteArrayToInt(tupleNum);
	}
	
	int readNextPageId(byte[] buffer) {
		byte[] nextPageId = new byte[4];
		System.arraycopy(buffer, Constant.NEXTPAGEID_OFFSET, nextPageId, 0, 4);
		return IntByte.byteArrayToInt(nextPageId);
	}
	
	int readId(byte[] buffer, int num) {
		byte[] idAsBytes = new byte[4];
		int offset = Constant.HEADERSIZE + 4 * (num - 1);
		System.arraycopy(buffer, offset, idAsBytes, 0, 4);
		return IntByte.byteArrayToInt(idAsBytes);
	}

}

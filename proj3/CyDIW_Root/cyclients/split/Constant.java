package cyclients.split;

public class Constant {
	
	public static int HEADERSIZE = 0;
	public static int PAGESIZE = 0;
	public static int NUMOFITEMS = 0;
	public static int NUMOFTUPLES_OFFSET = 0;
	public static int NUMOFBYTESUSED_OFFSET = 0;
	public static int NEXTPAGEID_OFFSET = 0;
	
	public static void initializeConstant(String configFile) {
		ChainConfigParser parser = new ChainConfigParser(configFile);
		
		HEADERSIZE = parser.parsePageHeaderSize();
		
		PAGESIZE = parser.parsePageCapacity();
		
		NUMOFITEMS = parser.parseNumOfItems();
		
		NUMOFTUPLES_OFFSET = parser.parsePageHeader("NumOfTuples");
		
		NUMOFBYTESUSED_OFFSET = parser.parsePageHeader("NumOfBytesUsed");
		
		NEXTPAGEID_OFFSET = parser.parsePageHeader("NextPageID");
		
	}

}

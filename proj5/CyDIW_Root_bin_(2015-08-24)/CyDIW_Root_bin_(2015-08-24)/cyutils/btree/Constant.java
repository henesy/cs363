package cyutils.btree;

public class Constant {
	
	// head size in bytes, this public member is not in page, we just read it from Constant
	public static int PAGE_HEADER_SIZE = 28;
	
	public static int PAGE_HEADER_SIZE_PTR = 0;
	
	public static int CURRENT_PAGE_PTR = 4;
	
	public static int NEXT_PAGE_PTR = 8;
	
	// we put page type and index pointer length into the same member
	// if it = 0, means it is a sequence page
	// if it = 4, means each pointer takes 4 bytes(int), and it is an index page
	public static int PAGE_TYPE_PTR_AND_PTR_LEN = 12;
	
	public static int TUPLE_OR_KEY_LEN_PTR = 16;
	
	public static int TUPLE_NUM_PTR = 20;
	
	public static int PAGE_SIZE_USED_PTR = 24;
	
	// so tuples will start from 24 by now
	// need discuss with Dr.Gadia to see what members should be put in the followings
	
	
	/**
	 * regular index pointer size
	 */

	public static int INDEX_PTR_SIZE = 4;
	
	/**
	 * Attributes Type Constant
	 */
	
	public static int ATTR_INTEGER = 0;
	
	public static int ATTR_STRING = 1;
	
	
}

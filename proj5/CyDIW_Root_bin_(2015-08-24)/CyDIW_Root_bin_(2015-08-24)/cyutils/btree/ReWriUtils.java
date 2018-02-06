package cyutils.btree;

public class ReWriUtils {
	
	/**
	 *  All converts are based on Big-Endian
	 * @param b
	 * @param offset
	 * @return
	 */
	
	public static int byteArrayToInt(byte[] b, int offset){
		
		int value = 0;
		
		for(int i = 0; i < 4; i++){
			
			value = (b[i+offset] & 0x000000FF) + (value << 8);
			
		}
		
		return value;
		
	}
	
	public static void intToByteArray(int a, byte[] b, int offset){
		
		b[3+offset] = (byte) (a & 0xFF);
		b[2+offset] = (byte) ((a >> 8) & 0xFF);
		b[1+offset] = (byte) ((a >> 16) & 0xFF);
		b[0+offset] = (byte) ((a >> 24) & 0xFF);
		
	}
	
	public static String byteArrayToString(byte[] b, int start, int end){
	
		char[] chs = new char[end-start+1];
		
		for(int ii = 0; ii <= end-start; ii++){
		
			chs[ii] = (char) b[ii+start];
		
		}
		
		return String.copyValueOf(chs);	
	}
	
	public static void stringToByteArray(String a, byte[] b, int start, int end){
		
		char[] chs = a.toCharArray();
		
		for(int ii = 0; ii <= end-start && ii < chs.length; ii++){
		
			b[ii+start] = (byte) chs[ii];
		
		}
		
	}
	
	
}

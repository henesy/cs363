package cyutils.btree;

import java.util.*;
import java.io.*;
import java.io.IOException;
import java.io.FileNotFoundException;

public class BTreeWithXml {

	public static boolean detectStringFromFile(String fileNameWithPath, String pattern){
		
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
		for(String cur : list){
			
			if(cur.contains(pattern)){
			
				System.out.println(cur);
				
				return true;
			
			}
		}
		
		return false;
	
	}
	
	
	public static int getBTreeRootPageId(String fileNameWithPath){
		
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
		
			System.out.println("I/O Exception");
		
		}
		
		String extractInt = null;
		
		String pattern1 = "<RootPageId>";
		
		String pattern2 = "</RootPageId>";
		
		for(String cur : list){
		
			if(cur.contains(pattern1)){
			
				extractInt = cur;
				break;
			
			}
			
		}
		
		if(extractInt == null){
			
			return -1;
		
		}
		
		String sInteger = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
		
		int res = Integer.parseInt(sInteger);
		
		return res;
	
	}
	
	
	public static int getBTreeSequencePageId(String fileNameWithPath){
	
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
		String extractInt = null;
		
		String pattern1 = "<SequencePageId>";
		
		String pattern2 = "</SequencePageId>";
		
		for(String cur : list){
		
			if(cur.contains(pattern1)){
			
				extractInt = cur;
				break;
			
			}
		
		}
		
		if(extractInt == null){
		
			return -1;
		
		}
		
		String sInteger = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
		
		int res = Integer.parseInt(sInteger);
		
		return res;
	
	}
	
	public static int getBTreePageSizeUsed(String fileNameWithPath){
		
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
		String extractInt = null;
		
		String pattern1 = "<PageSizeUsed>";
		
		String pattern2 = "</PageSizeUsed>";
		
		for(String cur : list){
		
			if(cur.contains(pattern1)){
			
				extractInt = cur;
				break;
			
			}
		
		}
		
		if(extractInt == null){
		
			return -1;
		
		}
		
		String sInteger = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
		
		int res = Integer.parseInt(sInteger);
		
		return res;
	
	}
	
//	public static int getBTreeNumberOfPages(String fileNameWithPath){
//		
//		List<String> list = new ArrayList<String>();
//		
//		String line = null;
//		
//		try{
//		
//			FileReader fileReader = new FileReader(fileNameWithPath);
//			
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
//			
//			while((line = bufferedReader.readLine()) != null){
//			
//				list.add(line);
//			
//			}
//			
//			bufferedReader.close();
//			
//		}catch(FileNotFoundException e){
//			
//			System.out.println("can not find file");
//		
//		}catch(IOException e){
//			
//			System.out.println("I/O Exception");
//		
//		}
//		
//		String extractInt = null;
//		
//		String pattern1 = "<NumberOfPages>";
//		
//		String pattern2 = "</NumberOfPages>";
//		
//		for(String cur : list){
//		
//			if(cur.contains(pattern1)){
//			
//				extractInt = cur;
//				break;
//			
//			}
//		
//		}
//		
//		if(extractInt == null){
//		
//			return -1;
//		
//		}
//		
//		String sInteger = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
//		
//		int res = Integer.parseInt(sInteger);
//		
//		return res;
//	
//	}
	
	public static String getBTreeName(String fileNameWithPath){
		
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
		String extractInt = null;
		
		String pattern1 = "<BTreeName>";
		
		String pattern2 = "</BTreeName>";
		
		for(String cur : list){
		
			if(cur.contains(pattern1)){
			
				extractInt = cur;
				break;
			
			}
		
		}
		
		if(extractInt == null){
		
			return "NULL";
		
		}
		
		String res = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
		
		return res;
	
	}
	
	public static int getBTreeHeight(String fileNameWithPath){
		
		List<String> list = new ArrayList<String>();
		
		String line = null;
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			while((line = bufferedReader.readLine()) != null){
			
				list.add(line);
			}
			
			bufferedReader.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
		
			System.out.println("I/O Exception");
		
		}
		
		String extractInt = null;
		
		String pattern1 = "<BTreeHeight>";
		
		String pattern2 = "</BTreeHeight>";
		
		for(String cur : list){
		
			if(cur.contains(pattern1)){
			
				extractInt = cur;
				break;
			
			}
			
		}
		
		if(extractInt == null){
			
			return -1;
		
		}
		
		String sInteger = extractInt.substring(extractInt.indexOf(pattern1)+pattern1.length(), extractInt.indexOf(pattern2));
		
		int res = Integer.parseInt(sInteger);
		
		return res;
	
	}
	

	
	public static void storeBTreeRootPageId(String fileNameWithPath, int rootPageId){
		
		String pattern1 = "<RootPageId>";
		
		String pattern2 = "</RootPageId>";
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			String input = "";
			
			while((line = bufferedReader.readLine()) != null){
			
				input += line + '\n';
			
			}
			
			int previousRootPageId = getBTreeRootPageId(fileNameWithPath);
			
			input = input.replace(pattern1+Integer.toString(previousRootPageId)+pattern2, 
					pattern1+Integer.toString(rootPageId)+pattern2);
			
			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
			
			fileOut.write(input.getBytes());
			
			fileOut.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
	}
	
	
	public static void storeBTreeSequencePageId(String fileNameWithPath, int sequencePageId){
		
		String pattern1 = "<SequencePageId>";
		
		String pattern2 = "</SequencePageId>";
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			String input = "";
			
			while((line = bufferedReader.readLine()) != null){
			
				input += line + '\n';
			
			}
			
			int previousSequencePageId = getBTreeSequencePageId(fileNameWithPath);
			
			input = input.replace(pattern1+Integer.toString(previousSequencePageId)+pattern2, 
					pattern1+Integer.toString(sequencePageId)+pattern2);
			
			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
			
			fileOut.write(input.getBytes());
			
			fileOut.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
	}
	
	public static void storeBTreePageSizeUsed(String fileNameWithPath, int pageSizeUsed){
		
		String pattern1 = "<PageSizeUsed>";
		
		String pattern2 = "</PageSizeUsed>";
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			String input = "";
			
			while((line = bufferedReader.readLine()) != null){
			
				input += line + '\n';
			
			}
			
			int previousSequencePageId = getBTreeSequencePageId(fileNameWithPath);
			
			input = input.replace(pattern1+Integer.toString(previousSequencePageId)+pattern2, 
					pattern1+Integer.toString(pageSizeUsed)+pattern2);
			
			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
			
			fileOut.write(input.getBytes());
			
			fileOut.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
	}
	
//	public static void storeBTreeNumberOfPages(String fileNameWithPath, int numberOfPages){
//		
//		String pattern1 = "<NumberOfPages>";
//		
//		String pattern2 = "</NumberOfPages>";
//		
//		try{
//		
//			FileReader fileReader = new FileReader(fileNameWithPath);
//			
//			BufferedReader bufferedReader = new BufferedReader(fileReader);
//			
//			String line = null;
//			
//			String input = "";
//			
//			while((line = bufferedReader.readLine()) != null){
//			
//				input += line + '\n';
//			
//			}
//			
//			int previousNumberOfPages = getBTreeNumberOfPages(fileNameWithPath);
//			
//			input = input.replace(pattern1+Integer.toString(previousNumberOfPages)+pattern2, 
//					pattern1+Integer.toString(numberOfPages)+pattern2);
//			
//			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
//			
//			fileOut.write(input.getBytes());
//			
//			fileOut.close();
//			
//		}catch(FileNotFoundException e){
//			
//			System.out.println("can not find file");
//		
//		}catch(IOException e){
//			
//			System.out.println("I/O Exception");
//		
//		}
//		
//	}
	
	public static void storeBTreeName(String fileNameWithPath, String btreename){
		
		String pattern1 = "<BTreeName>";
		
		String pattern2 = "</BTreeName>";
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			String input = "";
			
			while((line = bufferedReader.readLine()) != null){
			
				input += line + '\n';
			
			}
			
			int previousSequencePageId = getBTreeSequencePageId(fileNameWithPath);
			
			input = input.replace(pattern1+Integer.toString(previousSequencePageId)+pattern2, 
					pattern1+btreename+pattern2);
			
			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
			
			fileOut.write(input.getBytes());
			
			fileOut.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
		
	}
	
	public static void storeBTreeHeight(String fileNameWithPath, int height){
		
		String pattern1 = "<BTreeHeight>";
		
		String pattern2 = "</BTreeHeight>";
		
		try{
		
			FileReader fileReader = new FileReader(fileNameWithPath);
			
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			
			String line = null;
			
			String input = "";
			
			while((line = bufferedReader.readLine()) != null){
			
				input += line + '\n';
			
			}
			
			int previousHeight = getBTreeHeight(fileNameWithPath);
			
			input = input.replace(pattern1+Integer.toString(previousHeight)+pattern2, 
					pattern1+Integer.toString(height)+pattern2);
			
			FileOutputStream fileOut = new FileOutputStream(fileNameWithPath);
			
			fileOut.write(input.getBytes());
			
			fileOut.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("can not find file");
		
		}catch(IOException e){
			
			System.out.println("I/O Exception");
		
		}
	}
	

	
}



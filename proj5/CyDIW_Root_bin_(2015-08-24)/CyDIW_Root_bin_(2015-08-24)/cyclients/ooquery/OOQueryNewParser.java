/**
 * The class is to parse the user query.
 */
package cyclients.ooquery;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.dom4j.DocumentException;

/**
 * @author HuanLin WenZhao
 *
 */
public class OOQueryNewParser {

	private HashMap<String, String> var2DbMap = new HashMap<String, String>();
	private HashMap<String, String> var2ClassMap = new HashMap<String, String>();
	private HashMap<String, HashMap<String, ArrayList<String>>> db2SchemaMap = new HashMap<String, HashMap<String, ArrayList<String>>>();
	private String[] phrases;
	//private ArrayList<String> accesses = new ArrayList<String>();
	
	public String parse(String query) throws DocumentException, IOException
	{
		getAllPhrases(query);
		buildVar2DbMap();
		processAllAccesses();
		//System.out.println(getTranslatedXQuery());
		return getTranslatedXQuery();
	}
	
	public String getTranslatedXQuery()
	{
		StringBuilder res = new StringBuilder();
		for(int i=0;i<phrases.length;i++)
		{
			res.append(phrases[i]+" ");
		}
		return res.toString();
	}

	private String adaptOne2XQuery(String str)
	{
//		String postfix = "";
//		if(str.endsWith("/text()"))
//		{
//			postfix = "/text()";
//		}
		boolean flag = true;
		StringBuilder res = new StringBuilder();
		int startIdx = str.indexOf("$");
		ArrayList<String> fields = new ArrayList<String>();
		int index = str.indexOf(".", startIdx);
		String variable = str.substring(startIdx+1, index);
		//System.out.println(variable);
		int lastIdx = index;
		index = str.indexOf(".", lastIdx+1);
		while(index>0)
		{
			//index = str.indexOf(".", lastIdx+1);
			fields.add(str.substring(lastIdx+1,index));
			//System.out.println(str.substring(lastIdx+1,index));
			lastIdx = index;
			index = str.indexOf(".", lastIdx+1);
		}
		fields.add(str.substring(lastIdx+1));
		
		if(var2DbMap.get(variable)!=null && var2ClassMap.get(variable).length()>0)
		{
			String curClass = var2ClassMap.get(variable);
			String db = var2DbMap.get(variable);
			res.append("$"+variable);
			for(int i=0;i<fields.size();i++)
			{
				String field = fields.get(i);
				HashMap<String, ArrayList<String>> map = db2SchemaMap.get(db);
				ArrayList<String> list = map.get(curClass);
				curClass = "";
				boolean localFlag = false;
				if(list!=null)
				{
					for(int j=0;j<list.size();j++)
					{
						String item = list.get(j); 
						String[] items = item.split(" ");
						if(items[items.length-1].equals(field)/* || items[items.length-1].equals("@"+field)*/)
						{
							localFlag = true;
							{
								
								String[] strs = item.split(" ");
								if(strs.length<=1)
								{
									//res.append("$"+variable);
									res.append("/"+item);
								}
								else
								{
									boolean refFlag = false;
									for(int k=0;k<strs.length/2;k++)
									{
										if(strs[2*k].equals("@Ref"))
										{
											refFlag = true;
											res.insert(0, "$" + variable + "/id(");
											res.append("/"+strs[2*k+2]);
											res.append("/@oID)");
											curClass = strs[2*k+1];
										}
										else
										{
											String asClass = "ISA";
											res.insert(0, "$" + variable + "/id(");
											res.append("/" + asClass+"/"+strs[2*k+1]+")");
										}
									}
									if(!refFlag)
										res.append("/"+strs[strs.length-1]);
								}
							}
							break;
						}
					}
				}
				if(!localFlag)
				{
					flag = false;
					break;
				}
			}
		}
		if(flag)
			return res.toString();
		else 
			return null;
	}
	private String adaptOne2XQueryTransition(String str)
	{
//		String postfix = "";
//		if(str.endsWith("/text()"))
//		{
//			postfix = "/text()";
//		}
		boolean flag = true;
		StringBuilder res = new StringBuilder();
		int startIdx = str.indexOf("$");
		ArrayList<String> fields = new ArrayList<String>();
		int index = str.indexOf(".", startIdx);
		String variable = str.substring(startIdx+1, index);
		//System.out.println(variable);
		int lastIdx = index;
		index = str.indexOf(".", lastIdx+1);
		while(index>0)
		{
			//index = str.indexOf(".", lastIdx+1);
			fields.add(str.substring(lastIdx+1,index));
			//System.out.println(str.substring(lastIdx+1,index));
			lastIdx = index;
			index = str.indexOf(".", lastIdx+1);
		}
		fields.add(str.substring(lastIdx+1));
		
		if(var2DbMap.get(variable)!=null && var2ClassMap.get(variable).length()>0)
		{
			String curClass = var2ClassMap.get(variable);
			String db = var2DbMap.get(variable);
			res.append("$"+variable);
			for(int i=0;i<fields.size();i++)
			{
				String field = fields.get(i);
				HashMap<String, ArrayList<String>> map = db2SchemaMap.get(db);
				ArrayList<String> list = map.get(curClass);
				curClass = "";
				boolean localFlag = false;
				if(list!=null)
				{
					for(int j=0;j<list.size();j++)
					{
						String item = list.get(j); 
						String[] items = item.split(" ");
						if(items[items.length-1].equals(field)/* || items[items.length-1].equals("@"+field)*/)
						{
							localFlag = true;
							{
								
								String[] strs = item.split(" ");
								if(strs.length<=1)
								{
									//res.append("$"+variable);
									res.append("/"+item);
								}
								else
								{
									boolean refFlag = false;
									for(int k=0;k<strs.length/2;k++)
									{
										if(strs[2*k].equals("ref"))
										{
											refFlag = true;
											res.insert(0, "$" + variable + "/id(");
											res.append("/"+strs[2*k+2]);
											res.append("/@oID)");
											curClass = strs[2*k+1];
										}
										else
										{
											String asClass = "ISA";
											res.insert(0, "$" + variable + "/id(");
											res.append("/" + asClass+"/"+"@oID"+")");
										}
									}
									if(!refFlag)
										res.append("/"+strs[strs.length-1]);
								}
							}
							break;
						}
					}
				}
				if(!localFlag)
				{
					flag = false;
					break;
				}
			}
		}
		if(flag)
			return res.toString();
		else 
			return null;
	}	
	private String getClassFromOnePhrase(String str)
	{
		boolean flag = true;
		StringBuilder res = new StringBuilder();
		int startIdx = str.indexOf("$");
		ArrayList<String> fields = new ArrayList<String>();
		int index = str.indexOf(".", startIdx);
		int lastIdx = index;
		String variable;
		if(index<0)
			variable = str.substring(startIdx+1);
		else
			variable = str.substring(startIdx+1, index);
		//System.out.println(variable);
		
		index = str.indexOf(".", lastIdx+1);
		while(index>0)
		{
			//index = str.indexOf(".", lastIdx+1);
			fields.add(str.substring(lastIdx+1,index));
			//System.out.println(str.substring(lastIdx+1,index));
			lastIdx = index;
			index = str.indexOf(".", lastIdx+1);
		}
		if(lastIdx>0)
			fields.add(str.substring(lastIdx+1));
		String curClass = "";
		if(var2DbMap.get(variable)!=null && var2ClassMap.get(variable).length()>0)
		{
			curClass = var2ClassMap.get(variable);
			String db = var2DbMap.get(variable);
			res.append("$"+variable);
			for(int i=0;i<fields.size();i++)
			{
				String field = fields.get(i);
				HashMap<String, ArrayList<String>> map = db2SchemaMap.get(db);
				ArrayList<String> list = map.get(curClass);
				curClass = "";
				boolean localFlag = false;
				if(list!=null)
				{
					for(int j=0;j<list.size();j++)
					{
						String item = list.get(j); 
						String[] items = item.split(" ");
						if(items[items.length-1].equals(field)/* || items[items.length-1].equals("@"+field)*/)
						{
							localFlag = true;
							{
								
								String[] strs = item.split(" ");
								if(strs.length<=1)
								{
									//res.append("$"+variable);
									res.append("/"+item);
								}
								else
								{
									boolean refFlag = false;
									for(int k=0;k<strs.length/2;k++)
									{
										if(strs[2*k].equals("@Ref"))
										{
											refFlag = true;
											res.insert(0, "$" + variable + "/id(");
											res.append("/"+strs[2*k+2]);
											res.append("/@oID)");
											curClass = strs[2*k+1];
										}
										else
										{
											String asClass = "ISA";
											res.insert(0, "$" + variable + "/id(");
											res.append("/" + asClass+"/"+strs[2*k+1]+")");
										}
									}
									if(!refFlag)
										res.append("/"+strs[strs.length-1]);
								}
							}
							break;
						}
					}
				}
				if(!localFlag)
				{
					flag = false;
					break;
				}
			}
		}
		if(flag)
			return curClass;
		else 
			return null;
	}

	private String getClassFromOnePhraseTransition(String str)
	{
		boolean flag = true;
		StringBuilder res = new StringBuilder();
		int startIdx = str.indexOf("$");
		ArrayList<String> fields = new ArrayList<String>();
		int index = str.indexOf(".", startIdx);
		int lastIdx = index;
		String variable;
		if(index<0)
			variable = str.substring(startIdx+1);
		else
			variable = str.substring(startIdx+1, index);
		//System.out.println(variable);
		
		index = str.indexOf(".", lastIdx+1);
		while(index>0)
		{
			//index = str.indexOf(".", lastIdx+1);
			fields.add(str.substring(lastIdx+1,index));
			//System.out.println(str.substring(lastIdx+1,index));
			lastIdx = index;
			index = str.indexOf(".", lastIdx+1);
		}
		if(lastIdx>0)
			fields.add(str.substring(lastIdx+1));
		String curClass = "";
		if(var2DbMap.get(variable)!=null && var2ClassMap.get(variable).length()>0)
		{
			curClass = var2ClassMap.get(variable);
			String db = var2DbMap.get(variable);
			res.append("$"+variable);
			for(int i=0;i<fields.size();i++)
			{
				String field = fields.get(i);
				HashMap<String, ArrayList<String>> map = db2SchemaMap.get(db);
				ArrayList<String> list = map.get(curClass);
				curClass = "";
				boolean localFlag = false;
				if(list!=null)
				{
					for(int j=0;j<list.size();j++)
					{
						String item = list.get(j); 
						String[] items = item.split(" ");
						if(items[items.length-1].equals(field)/* || items[items.length-1].equals("@"+field)*/)
						{
							localFlag = true;
							{
								
								String[] strs = item.split(" ");
								if(strs.length<=1)
								{
									//res.append("$"+variable);
									res.append("/"+item);
								}
								else
								{
									boolean refFlag = false;
									for(int k=0;k<strs.length/2;k++)
									{
										if(strs[2*k].equals("ref"))
										{
											refFlag = true;
											res.insert(0, "$" + variable + "/id(");
											res.append("/"+strs[2*k+2]);
											res.append("/@oID)");
											curClass = strs[2*k+1];
										}
										else
										{
											String asClass = "ISA";
											res.insert(0, "$" + variable + "/id(");
											res.append("/" + asClass+"/"+"@oID"+")");
										}
									}
									if(!refFlag)
										res.append("/"+strs[strs.length-1]);
								}
							}
							break;
						}
					}
				}
				if(!localFlag)
				{
					flag = false;
					break;
				}
			}
		}
		if(flag)
			return curClass;
		else 
			return null;
	}
	
	private void getAllPhrases(String query)
	{
		query = query.replaceAll(","," , ");
		phrases = query.split("[ |\t|\n|\r ]");
		ArrayList<String> new_phrases = new ArrayList<String>();
		
		for(int i=0;i<phrases.length;i++)
		{		
			if(phrases[i].length()>0)
			{
				
				if(phrases[i].contains("["))
				{
					String[] temp = phrases[i].split(Pattern.quote("["));
					new_phrases.add(temp[0]);
					for(int j=1;j<temp.length;j++)
						new_phrases.add("["+temp[j]);
				}
				else
				{
					new_phrases.add(phrases[i]);
				}				
			}
			
		}
//		ArrayList<String> post_phrases = new ArrayList<String>();
//		for(int i=0;i<new_phrases.size();i++)
//		{
//			if(new_phrases.get(i).contains(","))
//			{
//				String[] temp = new_phrases.get(i).split(",");
//				//post_phrases.add(temp[0]);
//				for(int j=0;j<temp.length-1;j++)
//				{
//					post_phrases.add(temp[j]);
//					post_phrases.add(",");
//				}
//				post_phrases.add(temp[temp.length-1]);
//			}
//			else
//			{
//				post_phrases.add(new_phrases.get(i));
//			}
//		}
		phrases = new_phrases.toArray(new String[new_phrases.size()]);		
	}
	private void buildVar2DbMap() throws DocumentException, IOException
	{
		for(int i=0;i<phrases.length;i++)
		{
			if(phrases[i].endsWith("for") || phrases[i].endsWith("let"))
			{
				if(i<phrases.length-4 && phrases[i+1].startsWith("$"))
				{
					String variable = phrases[i+1].substring(1);
					if(phrases[i+3].contains("doc(\""))
					{
						String[] tempStrs = phrases[i+3].split("\"");

						String db;
						int index = tempStrs[1].lastIndexOf(".");
						
						db = tempStrs[1].substring(0,index+1)+"xsd";
						
						System.out.println(variable + "	" + db);
						var2DbMap.put(variable, db);
						index = tempStrs[tempStrs.length-1].lastIndexOf("/");
						String className = "";
						if(index>0)
						{
							className = tempStrs[tempStrs.length-1].substring(index+1);
							int leftIdx = className.indexOf('[');
							if(leftIdx>0)
								className = className.substring(0,leftIdx);
							className = className+"Type";
						}
						if(className.length()<=4)
						{
							int leftIdx = phrases[i+4].indexOf('[');
							if(leftIdx>=0)
								className = phrases[i+4].substring(0,leftIdx)+"Type";
							else
								className = phrases[i+4] + "Type";
						}
						System.out.println(variable + "	" + className);
						var2ClassMap.put(variable, className);
						
						if(!db2SchemaMap.containsKey(db))
						{
							DBNewSchemaMap schemaMap = new DBNewSchemaMap();
							db2SchemaMap.put(db, schemaMap.createMap(db));
							
						}
						
					}
					else if(phrases[i+3].startsWith("$"))
					{
						String className = getClassFromOnePhraseTransition(phrases[i+3]);
						System.out.println(variable + "	" + className);
						var2ClassMap.put(variable, className);
						Iterator<String> iter = db2SchemaMap.keySet().iterator();
						var2DbMap.put(variable, iter.next());
					}
				}
			}
		}
	}
	
	private void processAllAccesses()
	{
		for(int i=0;i<phrases.length;i++)
		{
			if(phrases[i].contains("$") && phrases[i].contains("."))
			{
				String str = phrases[i];
				String prefix = "";
				String suffix = "";
				int index = phrases[i].indexOf("$");
				if(index>0)
				{
					prefix = str.substring(0,index);
					str = str.substring(index+1);
				}
				index = str.indexOf("}");
				int index2 = str.indexOf(")");
				int index3 = str.indexOf("]");
				int index4 = str.indexOf("/");
				if(index<0)
					index = Integer.MAX_VALUE;
				if(index2<0)
					index2 = Integer.MAX_VALUE;
				if(index3<0)
					index3 = Integer.MAX_VALUE;
				if(index4<0)
					index4 = Integer.MAX_VALUE;
				
				index = Math.min(index4, Math.min(Math.min(index2, index3), index));
				
				if(index<Integer.MAX_VALUE)
				{
					suffix = str.substring(index);
					str = str.substring(0,index);
				}
				str = adaptOne2XQueryTransition(str);
				System.out.println(str);
				//accesses.add(str);
				if(str!=null)
					phrases[i] = prefix + str + suffix;
			}
		}
	}
	
	public HashMap<String, String> getVar2DbMap()
	{
		return var2DbMap;
	}
	/**
	 * @param args
	 * @throws DocumentException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws DocumentException, IOException {
		OOQueryNewParser parser = new OOQueryNewParser();
		String query = "for $s in doc(\"ComS363/Datasets/University.OODB.After.2.xml\")//Student return <F> { $s.Mentor.DOB } </F>;";
		// $e.Name		$e/id($e/id($e/As_Emp/@EmpID)/As_Person/@PersonID)/Name	
		//String query = "<Item> {for  $f  in  doc(\"ComS363/Datasets/University.OODB.After.2.xml\")//Faculty\n\tfor $d in doc(\"ComS363/Datasets/Personnel_OO_DB.xml\")//Dept		return <R>\n			<E> { $d.ManagedBy.Budget } </E>\n			<F> { $f.Name } </F>\n		</R>\n} </Item>;";
		//String query = "	for  $d in doc(\"ComS363/Datasets/Personnel_OO_DB.xml\")//Dept\n		return <D> {$d.ManagedBy.Budget}</D>\n		} </Item>;";
		//String query = "<Item> {\n	for $m in doc(\"ComS363/Datasets/Personnel_OO_DB.xml\")//Manager\n			return <M> { $m.Name } </M>\n			} </Item>;";
		//String query = "<Item> { for $d in doc(\"ComS363/Datasets/Personnel_OO_DB.xml\")//Dept return <D> { $d.ManagedBy.Budget } </D>\n} </Item>;";
		parser.parse(query);
	}

}
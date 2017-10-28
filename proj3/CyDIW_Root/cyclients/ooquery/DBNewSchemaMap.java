package cyclients.ooquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class DBNewSchemaMap {
	private HashMap<String, ArrayList<String>> schemaMap = new HashMap<String, ArrayList<String>>();
	private Document document = null;
	private String dbUrl;
	public HashMap<String, ArrayList<String>> createMap(String filename) throws DocumentException, IOException{
		if(!readTransitionMapXML(filename))
		{
			read(filename);
			xsdParser();
			//createMapXML(filename);
			createTransitionMapXML(filename);
			printMap();
		}
		return schemaMap;
	}
	public String getDbUrl()
	{
		return dbUrl;
	}
    private void read(String url) throws DocumentException {
    	dbUrl = url;
        SAXReader reader = new SAXReader();
        document = reader.read(url);
        //return document;
    }
    public class TreeNode {
    	 int priority;
	     String className;
	     ArrayList<TreeNode> children;
	     TreeNode parent;
	     TreeNode(String name, int val) {
	    	className = name;
	    	priority = val;
	    	parent = null;
	    	children = new ArrayList<TreeNode>();
	     }
	}
    
    private void addClass2SchemaMap(TreeNode node, HashMap<String, Element> elementMap)
    {
    	Element element = elementMap.get(node.className);
    	ArrayList<String> list = new ArrayList<String>();
    	for( Iterator j = element.elementIterator("attribute"); j.hasNext(); ){
    		Element child = (Element) j.next();
    		list.add("@"+child.attributeValue("name"));
    		//System.out.println(child.asXML());
    	}    	
    	for( Iterator j = element.elementIterator("sequence"); j.hasNext(); ){
    		Element child = (Element) j.next();
    		//list.add("@"+child.attributeValue("name"));
    		for( Iterator k = child.elementIterator("element"); k.hasNext(); ){
    			Element grandChild = (Element) k.next();
    			Iterator item = grandChild.elementIterator("complexType");
    			if(item.hasNext()){
    				Element grandGrandChild = (Element) item.next();
    				Iterator itemChild = grandGrandChild.elementIterator("attribute");
    				Element grandGrandChildContent = (Element) itemChild.next();
    				//System.out.println(grandGrandChildContent.toString());
    				String attrName = grandChild.attributeValue("name");
    				String attrFixed = grandGrandChildContent.attributeValue("fixed");
    				Element grandGrandChild2Content = (Element) itemChild.next();
    				if(attrName.equals("ISA") && attrFixed != null)
    				{
    					String className = attrFixed;
    					for(String str: schemaMap.get(className))
    					{
    						String temp = className +  " @" + grandGrandChild2Content.attributeValue("name") + " " + str;
    						list.add(temp);
    					}
    				}
    				else
    				{

    					list.add("@Ref "+ attrFixed +" "+grandChild.attributeValue("name"));
    				}
    				//list.add(grandGrandChild.attributeValue("name"));
    			}
    			else{
    				list.add(grandChild.attributeValue("name"));
    			}
    		}
    		schemaMap.put(element.attributeValue("name"), list);
    	}
    }	
    private void addClass2TransitionSchemaMap(TreeNode node, HashMap<String, Element> elementMap)
    {
    	Element element = elementMap.get(node.className);
    	ArrayList<String> list = new ArrayList<String>();
    	for( Iterator j = element.elementIterator("attribute"); j.hasNext(); ){
    		Element child = (Element) j.next();
    		list.add("@"+child.attributeValue("name"));
    		//System.out.println(child.asXML());
    	}    	
    	for( Iterator j = element.elementIterator("sequence"); j.hasNext(); ){
    		Element child = (Element) j.next();
    		//list.add("@"+child.attributeValue("name"));
    		for( Iterator k = child.elementIterator("element"); k.hasNext(); ){
    			Element grandChild = (Element) k.next();
    			Iterator item = grandChild.elementIterator("complexType");
    			if(item.hasNext()){
    				Element grandGrandChild = (Element) item.next();
    				Iterator itemChild = grandGrandChild.elementIterator("attribute");
    				Element grandGrandChildContent = (Element) itemChild.next();
    				//System.out.println(grandGrandChildContent.toString());
    				String attrName = grandChild.attributeValue("name");
    				String attrFixed = grandGrandChildContent.attributeValue("fixed");
    				Element grandGrandChild2Content = (Element) itemChild.next();
    				if(attrName.equals("ISA") && attrFixed != null)
    				{
    					String className = attrFixed;
    					for(String str: schemaMap.get(className))
    					{
    						//String temp = className +  " @" + grandGrandChild2Content.attributeValue("name") + " " + str;
    						String temp = "isa " + className + " " + str;
    						list.add(temp);
    					}
    				}
    				else
    				{

    					list.add("ref "+ attrFixed +" "+grandChild.attributeValue("name"));
    				}
    				//list.add(grandGrandChild.attributeValue("name"));
    			}
    			else{
    				list.add(grandChild.attributeValue("name"));
    			}
    		}
    		schemaMap.put(element.attributeValue("name"), list);
    	}
    }	    
    private void xsdParser() throws DocumentException {

        Element root = document.getRootElement();
        HashMap<String, TreeNode> classMap = new HashMap<String, TreeNode>();
        HashMap<String, Element> elementMap = new HashMap<String, Element>();
        // build up the class name list;
        for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            elementMap.put(element.attributeValue("name"), element);
            TreeNode node = new TreeNode(element.attributeValue("name"),0);
            classMap.put(element.attributeValue("name"), node);
        }

        for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
            Element element = (Element) i.next();
            for( Iterator j = element.elementIterator("sequence"); j.hasNext(); ){
            	Element child = (Element) j.next();
            	String curClassName = element.attributeValue("name");
            	for( Iterator k = child.elementIterator("element"); k.hasNext(); ){
	            	Element grandChild = (Element) k.next();
	            	Iterator item = grandChild.elementIterator("complexType");
	            	if(item.hasNext()){
	            		Element grandGrandChild = (Element) item.next();
	            		Iterator itemChild = grandGrandChild.elementIterator("attribute");
	            		Element grandGrandChildContent = (Element) itemChild.next();
	            		//System.out.println(grandGrandChildContent.asXML());
	            		String attrName = grandChild.attributeValue("name");
	            		String attrFixed = grandGrandChildContent.attributeValue("fixed");
	            		if(attrName.equals("ISA") && attrFixed != null)
	            		{
	            			String parentClassName = attrFixed;
	            			System.out.println(parentClassName);
	            			classMap.get(curClassName).parent = classMap.get(parentClassName);
	            			classMap.get(parentClassName).children.add(classMap.get(curClassName));
	            		}

	            	}

            	}
            }
        }
    	for(String key: classMap.keySet()){
    		if(classMap.get(key).parent == null)
    		{
    			LinkedList<TreeNode> queue = new LinkedList<TreeNode>();
    			queue.add(classMap.get(key));
    			int level = 0;
    			int nextNum = 0;
    			int curNum = 1;
    			while(!queue.isEmpty()){
    				TreeNode n = queue.poll();
    				n.priority = level;
    				//addClass2SchemaMap(n,elementMap);
    				addClass2TransitionSchemaMap(n,elementMap);
    				for(int i=0; i<n.children.size(); i++){
    					queue.add(n.children.get(i));
    					nextNum++;
    				}
    				curNum--;
    				if(curNum == 0)
    				{
    					level++;
    					curNum = nextNum;
    					nextNum = 0;
    				}
    				
    			}
    		}
    	}
    	for(String key: classMap.keySet()){
    		System.out.print(classMap.get(key).priority+"	"+key+"	");
    		String parentName = "null";
    		if(classMap.get(key).parent!=null)
    		{
    			parentName = classMap.get(key).parent.className;
    		}
    		System.out.print("parent: "+ parentName+"	");
    		int size = classMap.get(key).children.size();
    		for(int i=0;i<size;i++)
    		{
    			System.out.print(i+": "+ classMap.get(key).children.get(i).className+"	");
    		}
    		System.out.println("");
    	}
    	System.out.println("");

     }
    public void createMapXML(String path) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "map" );
        String db = path.substring(0,path.length()-4);
        if(db.contains("\\"))
        {
        	String[] strs = db.split("\\\\");
        	db = strs[strs.length-1];
        }
        else if(db.contains("/"))
        {
        	String[] strs = db.split("/");
        	db = strs[strs.length-1];
        }
        root.addAttribute("name", db);
    	for(String key: schemaMap.keySet()){
    		Element elem = root.addElement("key");
    		elem.addAttribute("name", key);
    		Element valueElem = elem.addElement("value");
    		for(String str: schemaMap.get(key)){
    			String[] strs = str.split(" ");
    			
    			StringBuilder pStr = new StringBuilder();
    			pStr.append(strs[strs.length-1]);
    			for(int i=0;i<strs.length-1;i++)
    			{
    				pStr.append(" ");
    				pStr.append(strs[i]);
    			}
    			valueElem.addElement("listitem").addText(pStr.toString());
    		}
    	}
//        Element author1 = root.addElement( "author" )
//            .addAttribute( "name", "James" )
//            .addAttribute( "location", "UK" )
//            .addText( "James Strachan" );
//        
//        Element author2 = root.addElement( "author" )
//            .addAttribute( "name", "Bob" )
//            .addAttribute( "location", "US" )
//            .addText( "Bob McWhirter" );
        	// Create a file named as person.xml
     		FileOutputStream fos = new FileOutputStream(path.substring(0,path.length()-4)+".DotToId.xml");
     		// Create the pretty print of xml document.
     		OutputFormat format = OutputFormat.createPrettyPrint();
     		// Create the xml writer by passing outputstream and format
     		XMLWriter writer = new XMLWriter(fos, format);
     		// Write to the xml document
     		writer.write(document);
     		// Flush after done
     		writer.flush();
    }
    
    public void createTransitionMapXML(String path) throws IOException {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement( "Map" );
        String db = path.substring(0,path.length()-4);
        if(db.contains("\\"))
        {
        	String[] strs = db.split("\\\\");
        	db = strs[strs.length-1];
        }
        else if(db.contains("/"))
        {
        	String[] strs = db.split("/");
        	db = strs[strs.length-1];
        }
        root.addAttribute("name", db);
    	for(String key: schemaMap.keySet()){
    		Element elem = root.addElement("ContextObjectType");
    		elem.addAttribute("name", key);
    		//Element valueElem = elem.addElement("value");
    		for(String str: schemaMap.get(key)){
    			String[] strs = str.split(" ");
    			
    			StringBuilder pStr = new StringBuilder();
    			//pStr.append(strs[strs.length-1]);
    			for(int i=0;i<strs.length-1;i++)
    			{
    				pStr.append(strs[i]);
    				pStr.append(" ");
    			}
    			if(pStr.length()>0)
    			{
    				pStr.substring(0, pStr.length()-1);
    				//elem.addElement("DottedIdentifier").addText(pStr.toString());
    				elem.addElement("DottedIdentifier").addAttribute("name", strs[strs.length-1]).addAttribute("transitionCode", pStr.toString());
    			}
    			else
    			{
    				elem.addElement("DottedIdentifier").addAttribute("name", strs[strs.length-1]).addAttribute("transitionCode", "local");
    			}
    			
    		}
    	}
//        Element author1 = root.addElement( "author" )
//            .addAttribute( "name", "James" )
//            .addAttribute( "location", "UK" )
//            .addText( "James Strachan" );
//        
//        Element author2 = root.addElement( "author" )
//            .addAttribute( "name", "Bob" )
//            .addAttribute( "location", "US" )
//            .addText( "Bob McWhirter" );
        	// Create a file named as person.xml
     		FileOutputStream fos = new FileOutputStream(path.substring(0,path.length()-4)+".DotToId.xml");
     		// Create the pretty print of xml document.
     		OutputFormat format = OutputFormat.createPrettyPrint();
     		// Create the xml writer by passing outputstream and format
     		XMLWriter writer = new XMLWriter(fos, format);
     		// Write to the xml document
     		writer.write(document);
     		// Flush after done
     		writer.flush();
    }    
    public boolean readMapXML(String path) throws DocumentException
    {
    	String mapPath = path.substring(0,path.length()-4)+".DotToId.xml";
    	if(new File(mapPath).isFile())
    	{
    		schemaMap.clear();
            SAXReader reader = new SAXReader();
            Document mapDoc = reader.read(mapPath);
            Element root = mapDoc.getRootElement();

            for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                String key = element.attributeValue("name");
                schemaMap.put(key, new ArrayList<String>());
                Iterator item = element.elementIterator();
                Element valueElem = (Element) item.next();
                
                for ( Iterator j = valueElem.elementIterator(); j.hasNext(); ) {
                	Element listitem = (Element) j.next();
                	String str = listitem.getText();
        			String[] strs = str.split(" ");
        			StringBuilder pStr = new StringBuilder();			
        			for(int k=1;k<strs.length;k++)
        			{
        				pStr.append(strs[k]);
        				pStr.append(" ");
        			}
        			pStr.append(strs[0]);
                	schemaMap.get(key).add(pStr.toString());
                }
            }

    		return true;
    	}
    	return false;
    }
    
    public boolean readTransitionMapXML(String path) throws DocumentException
    {
    	String mapPath = path.substring(0,path.length()-4)+".DotToId.xml";
    	if(new File(mapPath).isFile())
    	{
    		schemaMap.clear();
            SAXReader reader = new SAXReader();
            Document mapDoc = reader.read(mapPath);
            Element root = mapDoc.getRootElement();

            for ( Iterator i = root.elementIterator(); i.hasNext(); ) {
                Element element = (Element) i.next();
                String key = element.attributeValue("name");
                schemaMap.put(key, new ArrayList<String>());
                //Iterator item = element.elementIterator();
                //Element valueElem = (Element) item.next();
                
                for ( Iterator j = element.elementIterator(); j.hasNext(); ) {
                	Element listitem = (Element) j.next();
                	String attributeName = listitem.attributeValue("name");
                	String str = listitem.attributeValue("transitionCode");
                	if(str.equals("local"))
                	{
                		str = attributeName;
                	}
                	else
                	{
                		str = str + attributeName;
                	}
//        			String[] strs = str.split(" ");
//        			StringBuilder pStr = new StringBuilder();			
//        			for(int k=1;k<strs.length;k++)
//        			{
//        				pStr.append(strs[k]);
//        				pStr.append(" ");
//        			}
//       			pStr.append(strs[0]);
//                	schemaMap.get(key).add(pStr.toString());
                	schemaMap.get(key).add(str);
                }
            }

    		return true;
    	}
    	return false;
    }   
    private void printMap(){
    	for(String key: schemaMap.keySet()){
    		System.out.print(key+"	");
    		for(String str: schemaMap.get(key)){
    			System.out.print(str+"	");
    		}
    		System.out.println("");
    	}
    	System.out.println("");
    }
	public static void main(String[] argv) throws DocumentException, IOException{
		DBNewSchemaMap schemaMap = new DBNewSchemaMap();
		String url = ("C:\\HuanLin\\Projects\\java\\cydiw(v0)\\ComS363\\Datasets\\UniversityOODB.xsd");
		schemaMap.createMap(url);
		schemaMap.readMapXML(url);
		schemaMap.printMap();
	}
}

package cyclients.edb.dataexecution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cyclients.edb.datageneration.*;

import cycsx.csxpagination.util.CanStoreXUtil;


import storagemanager.StorageDirectoryDocument;
import storagemanager.StorageManagerClient;
import storagemanager.StorageUtils;

/**
 * @author Xiaofeng Wang This iterator is a little different with other
 *         iterators. Input is byte array, it directly connect to database.
 *         OutPut is tuple. However, For otherIterators, both input and output
 *         are tuples. Not directly connect to database. Mar 24, 2013
 */
public class RelationIterator {
	private long totaltime;
	private int curTupleIndex, tsize, startPage, relstartPage, B, curBuffIndex;
	private byte[][] buff;// These are pointers to buffers in the BufferManager.
	private int[] L;
	private Relation rel;
	private boolean open, hasNext, hasPage;
	private StorageManagerClient sm;
	private Node nxt;
	private String aliasName;
	private Node tupleNode; // Was used as a template for holding tuple
	private ArrayList<Node> []tuplesets;
	
	
//    private int tupleSize = -1;
	
	public boolean hasNext() {
		return hasNext;
	}

	/**
	 * 
	 * @param ism
	 * @param exprTree
	 * 
	 * 
	 * */
	public RelationIterator(String relName, String aliasName1, String catFile,
			StorageUtils can, int B) {
		curTupleIndex = 0;
		tsize = 0;
		startPage = 0;
		this.B = B;
		buff = null;
		rel = null;
		open = false;
		hasNext = false;
		aliasName = aliasName1;
		initialize(relName, catFile, can);
		iniTupleTemplate();
	}

	private void initialize(String relName, String catFile, StorageUtils can) {
		Catalog cat = new Catalog(catFile);
		cat.readConfig();
		Relation rel = null;
		Iterator<Relation> iter = cat.getRelations().iterator();
		while (iter.hasNext()) {
			Relation next = iter.next();
			if (next.getRelName().equalsIgnoreCase(relName))
				rel = next;
		}
		Iterator<Attribute> it = rel.getAttrList().iterator();
		tsize = 0;
		Vector<Attribute> attrs = new Vector<Attribute>();
		Attribute attr;
		while (it.hasNext()) {
			attr = it.next();
			tsize += attr.getLength();
			attr.setAttrName(attr.getAttrName());
			attrs.add(attr);
		}

		rel.setAttrList(attrs);

		int strt = -1;
		StorageDirectoryDocument xmlParser = can.getXmlParser();
		if ((strt = xmlParser.getStartPage(rel.getLocation())) != -1) {
			sm = can.getXmlClient();
			setRel(rel);
			setTsize(tsize);
			setStartPage(strt);
		}
	}

	public int getTsize() {
		return tsize;
	}

	private void setTsize(int tsize) {
		this.tsize = tsize;
	}

	private int getIndex() {
		return curTupleIndex;
	}

	Relation getRel() {
		return rel;
	}

	void setRel(Relation rel) {
		this.rel = rel;
	}

	private int getStartPage() {
		return startPage;
	}

	private void setStartPage(int startPage) {
		this.startPage = startPage;
		this.relstartPage=startPage;
	}

	private boolean isOpen() {
		return open;
	}

	private int getNumBuff() {
		return B;
	}

	private int getCurBuffNum() {
		return curBuffIndex;
	}

	boolean open() {
		if (!open) {
			hasPage = true;
			hasNext = false;
			if (buff==null)
			buff = new byte[B][];
			if (tuplesets==null){
				tuplesets = new ArrayList[B];
				for (int j=0;j<B;j++)
			    tuplesets[j]= new ArrayList<Node> ();// new added	
			}
			if (L==null){
			L = new int[B];
			for (int i = 0; i < B; ++i)
				L[i] = -1;
			}
			loadBuffers();
/*			curTupleIndex = -1;
			curBuffIndex = 0;
			locateNext();*/
			open = true;
		} else
			return false;
		return true;
	}

	boolean close() {
		if (open) {
			open = false;
			hasNext = false;
			for (int i = 0; i < B; ++i)
				if (L[i] != -1) {
					try {
						sm.unpinPage(L[i]);
						L[i] = -1;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
				}
		} else
			return false;
		return true;
	}
    

 	Node getNext() {
		if (!open)
			return null;
		if (!hasNext)
			return null;
		Node ret = nxt;
		hasNext = false;
		
		locateNext();
		//System.out.println(aliasName+count);
		return ret;

	}

	private String stringData(byte[] in, String type) {
		if (type.equalsIgnoreCase("string")) {
			return new String(in).trim();
		} else if (type.equalsIgnoreCase("integer")) {
			return String.valueOf(bToI(in, 0));
		}
		return null;
	}

	public boolean loadBuffers() {
		// long time = System.currentTimeMillis();		
		int nextBuff = startPage;
		if (nextBuff == 0) {
			hasPage = false;
			return false;
		}
		
//		System.out.println("First page of this batch is:"+nextBuff);
		
		try {
			for (int i = 0; i < B; ++i) {
				if (nextBuff != 0) {
					buff[i] = sm.readPagewithPin(nextBuff);
					convertPage2tuples(i);  //new added convert this page to tuples
					if (L[i] != -1) {
						sm.unpinPage(L[i]);
					}
					L[i] = nextBuff;
					nextBuff = bToI(buff[i], 4);
					// System.out.println(bToI(buff[i],0));

					if (nextBuff == 0)
						hasPage = false;
				} else {
					if (L[i] != -1)
						sm.unpinPage(L[i]);
					L[i] = -1;
				}
			}
			startPage=nextBuff;
			curBuffIndex = 0;
			curTupleIndex = -1;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}		
		 locateNext();		 
		// totaltime = totaltime + System.currentTimeMillis() - time;
		return true;
	}
    
	public void convertPage2tuples(int curBuff){
		tuplesets[curBuff].clear();
		for (int curTuple=0; curTuple<bToI(buff[curBuff], 0); curTuple++) {
		int loopOffset = 0;
		Iterator<Attribute> it = rel.attrList.iterator();
		byte[] tup;
		int indexOfAttr = 0;
		NodeList nl = tupleNode.getChildNodes();
		while (it.hasNext()) {
			Attribute a = it.next();
			tup = new byte[a.getLength()];
			nl.item(indexOfAttr).setTextContent(
					stringData(
							copy(tup,
									0,
									buff[curBuff],
									(curTuple * tsize)
											+ rel.getHeaderSize()
											+ loopOffset, a.getLength()), a
									.getAttrType()));

			indexOfAttr++;
			loopOffset += a.getLength();
		}
		tuplesets[curBuff].add(tupleNode.cloneNode(true));
		}
	}
	
	boolean reset() {
		if (!open)
			return false;
		close();
		startPage=relstartPage; //reset the starpage of this relation.
		open(); //xfwang 4.28.2013  (It has been located next)
		return true;
	}

	boolean resetInbatch() {
		if (!open)
		   return false;
		curBuffIndex = 0;
		curTupleIndex = -1;
		locateNext();
		return true;
	}
	
	private void locateNext() {
		while (true) {
			curTupleIndex++;
			if (curTupleIndex >= bToI(buff[curBuffIndex], 0)) {
				++curBuffIndex;
				curTupleIndex = 0;
				if (curBuffIndex < B && L[curBuffIndex] == -1) {
					hasNext = false;
					hasPage = false;
					startPage = 0;
					return;
				}
				if (curBuffIndex >= B) {
					startPage = bToI(buff[B - 1], 4);
					/*if (!loadBuffers()) {
						hasNext = false;
						return;
					}
					hasNext = true; // Need to be reconsidered (xiaofeng)					
*/					
					hasNext = false;
					
					return;
				}
			}
			

//			int loopOffset = 0;
//			Iterator<Attribute> it = rel.attrList.iterator();
//			byte[] tup;
//			int indexOfAttr = 0;
//			NodeList nl = tupleNode.getChildNodes();
//			while (it.hasNext()) {
//				Attribute a = it.next();
//				tup = new byte[a.getLength()];
//				nl.item(indexOfAttr).setTextContent(
//						stringData(
//								copy(tup,
//										0,
//										buff[curBuffIndex],
//										(curTupleIndex * tsize)
//												+ rel.getHeaderSize()
//												+ loopOffset, a.getLength()), a
//										.getAttrType()));
//
//				indexOfAttr++;
//				loopOffset += a.getLength();
//			}
			//nxt = tuplesets[curBuffIndex].get(curTupleIndex).cloneNode(true); //get from tuple sets
			nxt = tuplesets[curBuffIndex].get(curTupleIndex); 
			hasNext = true;
			return;

		}
	}

	private void iniTupleTemplate() {
		DocumentBuilderFactory domFactory;
		DocumentBuilder builder;
		Element ret;
		Document doc = null;
		try {
			domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			builder = domFactory.newDocumentBuilder();
			doc = builder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}

		ret = doc.createElement(rel.getRelName());
		ret.setNodeValue(rel.getRelName());
		ret.setAttribute("relAlias", aliasName);

		Iterator<Attribute> it = rel.attrList.iterator();
		Element nxtAtt;

		while (it.hasNext()) {
			Attribute a = it.next();
			nxtAtt = doc.createElement(a.getAttrName());
			nxtAtt.setTextContent(" "); // Set contents as empty string. And
										// when real tuple comes in, it replace
										// this empty string.
			ret.appendChild(nxtAtt);

		}
		tupleNode = ret;

	}

	private byte[] copy(byte[] dest, int dstart, byte[] source, int sstart,
			int len) {
		if ((dest != null) && (source != null) && (dstart >= 0)
				&& (sstart >= 0) && (len >= 0)
				&& ((source.length - sstart) >= len)
				&& ((dest.length - dstart) >= len)) {
			for (int i = 0; i < len; ++i)
				dest[i + dstart] = source[i + sstart];
		}
		return dest;
	}

	private int bToI(byte[] src, int index) {
		int ret = 0;
		for (int j = 0; j < 4; ++j)
			ret += (src[index + j] < 0 ? (int) src[index + j] + 256
					: (int) src[index + j]) << (8 * (3 - j));
		return ret;
	}


	private boolean hasPage() {
		return hasPage;
	}

	private void reload() {
		close();
		open();
	}
	
	

}

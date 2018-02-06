package cyclients.edb.datageneration;

import java.util.Iterator;
import java.util.LinkedList;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import storagemanager.StorageManagerClient;

public class LinearIterator {

	
	private int curTupleIndex, tsize, startPage, B, curBuffIndex;
	private byte[][] buff;//These are pointers to buffers in the BufferManager.
	private int[] L;
	private Relation rel;
	private boolean open, hasNext, hasPage;
	private StorageManagerClient sm;
	private Node condition;
	private Node nxt;
	
	public boolean hasNext() {
		return hasNext;
	}

	public LinearIterator(StorageManagerClient ism){
		curTupleIndex = 0;
		tsize = 0;
		startPage = 0;
		B = 0;
		buff = null;
		rel = null;
		open = false;
		hasNext = false;
		sm = ism;
	}
	
	public int getTsize() {
		return tsize;
	}
	public void setTsize(int tsize) {
		this.tsize = tsize;
	}
	public int getIndex() {
		return curTupleIndex;
	}
	public Relation getRel() {
		return rel;
	}
	public void setRel(Relation rel) {
		this.rel = rel;
	}
	public int getStartPage() {
		return startPage;
	}
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}

	public boolean isOpen() {
		return open;
	}

	public int getNumBuff() {
		return B;
	}

	public int getCurBuffNum() {
		return curBuffIndex;
	}
	
	public boolean open(int B, Node con){
		if(!open){
			condition = con;
			hasPage = true;
			hasNext = false;
			this.B = B;
			buff = new byte[B][];
			L = new int[B];
			for(int i=0;i<B;++i)
				L[i] = -1;
			loadBuffers(startPage);
			curTupleIndex = -1;
			curBuffIndex = 0;
			locateNext();
			open = true;
		}else
			return false;
		return true;
	}
	
	public boolean close(){
		if(open){
			open = false;
			hasNext = false;
			for(int i=0;i<B;++i)
				if(L[i] != -1){
					try{
						sm.unpinPage(L[i]);
						L[i] = -1;
					}catch(Exception e){
						e.printStackTrace();
						return false;
					}
				}
		}else
			return false;
		return true;
	}
	
	public Node getNext(){
		if(!open)
			return null;
		if(!hasNext)
			return null;
		Node ret = nxt;
		hasNext = false;
		locateNext();
		return ret;
	}
	
	private String stringData(byte[] in,String type){
		if(type.equalsIgnoreCase("string")){
			return new String(in).trim();
		}else if(type.equalsIgnoreCase("integer")){
			return String.valueOf(bToI(in,0));
		}
		return null;
	}
	
	
	public void loadBuffersWithNextSetOfPages(){
		if(open){
			do{
				if(hasPage){
					if(loadBuffers(bToI(buff[B-1],4)))
						locateNext();
				}
			}while(hasPage && !hasNext);
		}
	}
	
	private boolean loadBuffers(int startAddress) {
		int nextBuff = startAddress;
		
		
		if(nextBuff == 0){
			hasPage = false;
			return false;
		}
		try{
			for(int i=0;i<B;++i){
				if(nextBuff != 0){
					buff[i] = sm.readPagewithPin(nextBuff);
					if(L[i] != -1){
						sm.unpinPage(L[i]);
					}
					L[i] = nextBuff;
					nextBuff = bToI(buff[i],4);
//					System.out.println(bToI(buff[i],0));

					if(nextBuff == 0)
						hasPage = false;
				}else{
					if(L[i] != -1)
						sm.unpinPage(L[i]);
					L[i] = -1;
				}
			}
			curBuffIndex = 0;
			curTupleIndex = -1;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	public boolean reset(){
		if(!open)
			return false;
		curBuffIndex = 0;
		curTupleIndex = -1;
		locateNext();
		return true;
	}
	
	public boolean hasNext(String relName){
		return hasNext;
	}
	
	private void locateNext(){
		DocumentBuilderFactory domFactory;
		DocumentBuilder builder;
		Element ret;
		Document doc = null;
		try{
			domFactory = DocumentBuilderFactory.newInstance();
			domFactory.setNamespaceAware(true); // never forget this!
			builder = domFactory.newDocumentBuilder();
			doc = builder.newDocument();
		}catch(Exception e){
			e.printStackTrace();
		}
		while(true){
			curTupleIndex++;
			
		//	System.out.println(bToI(buff[curBuffIndex],0));
			

			if(curTupleIndex >= bToI(buff[curBuffIndex],0)){
				++curBuffIndex;
				curTupleIndex = 0;
				if(curBuffIndex < B && L[curBuffIndex] == -1){
					hasNext = false;
					hasPage = false;
					return;
				}
				if(curBuffIndex >= B){
			/*		if(!loadBuffers(bToI(buff[B-1],4))){
						hasNext = false;
						return;
					}*/
					hasNext = false;
					System.out.println("hasNext false 2");
					System.out.println(hasNext);
					return;
				}
			}
			ret = doc.createElement(rel.getRelName());
			ret.setNodeValue(rel.getRelName());
			int loopOffset = 0;
			Iterator<Attribute> it = rel.attrList.iterator();
			Element nxtAtt;
			byte[] tup;
			while(it.hasNext()){
				Attribute a = it.next();
				tup = new byte[a.getLength()];
				nxtAtt = doc.createElement(a.getAttrName());
//				System.out.println(stringData(copy(tup,0,buff[curBuffIndex],(curTupleIndex*tsize)+rel.getHeaderSize()+loopOffset,a.getLength()),a.getAttrType()));
				nxtAtt.setTextContent(stringData(copy(tup,0,buff[curBuffIndex],(curTupleIndex*tsize)+rel.getHeaderSize()+loopOffset,a.getLength()),a.getAttrType()));
//				System.out.println(nxtAtt.getTextContent());
				ret.appendChild(nxtAtt);
				loopOffset += a.getLength();
			}
			if(satisfies(ret)){
				nxt = ret;
				hasNext = true;

				return;
			}
		}
	}

	
	private byte[] copy(byte[] dest, int dstart, byte[] source, int sstart, int len){
		if((dest != null) && (source != null) && (dstart >= 0) && (sstart >= 0) && (len >= 0) && ((source.length - sstart) >= len) && ((dest.length - dstart) >= len)){
			for(int i=0;i<len;++i)
				dest[i+dstart] = source[i+sstart];
		}
		return dest;
	}
	
	private int bToI(byte[] src, int index){
		int ret=0;
		for(int j=0;j<4;++j)
			ret += (src[index+j] < 0 ? (int)src[index+j] + 256 : (int)src[index+j]) << (8 * (3-j));
		return ret;
	}
	
	public Node[] getRemaining(){
		LinkedList<Node> rem = new LinkedList<Node>();
		while(hasPage||hasNext){
			if(hasPage && !hasNext)
				loadBuffersWithNextSetOfPages();
			rem.add(getNext());
		}
		return rem.toArray(new Node[rem.size()]);
	}
	
	private boolean satisfies(Node tuple){
//		Random r = new Random();
//		return r.nextBoolean();
		return true;
		//return true;
	}
	
	public boolean hasPage(){
		return hasPage;
	}
	
	public void reload(){
		close();
		open(B, condition);
	}
}

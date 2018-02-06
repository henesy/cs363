package cyclients.edb.datageneration;

import storagemanager.StorageUtils;
import storagemanager.StorageManagerClient;

public class DataGenManager {
	private Catalog c;
	private CreateDB cdb;
	private StorageUtils strUtils;
	private StorageManagerClient sm;

	public DataGenManager(String catLoc, StorageUtils can) {

		
		c = new Catalog(catLoc);
		this.strUtils = can;
		sm = can.getXmlClient();
		
	}

	public void init() {
	}

	public void dataGenerate() {		
		c.readConfig();
	//	cdb = new CreateDB(sm, c);
		cdb = new CreateDB(strUtils, c);
		// System.out.println(cdb = new CreateDB(sm, c));
		dataGen dg = new dataGen();
		cdb.create(dg);
		
	}

	public LinearIterator getLinearIterator(String rel) {
		return cdb.getLinearIterator(rel);
	}
	
	


	public Catalog getCatalog() {
		return c;

	}
	
	

	
}

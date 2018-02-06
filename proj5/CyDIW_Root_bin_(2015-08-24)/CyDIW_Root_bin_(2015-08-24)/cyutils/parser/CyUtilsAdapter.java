package cyutils.parser;

import cysystem.clientsmanager.ClientsFactory;
import cysystem.clientsmanager.CyGUI;
import cysystem.diwGUI.gui.DBGui;
import cyutils.btree.BTree;
import cyutils.btree.BTreeTupleGenerator;
import cyutils.btree.BTreeWithXml;
import cyutils.btree.ReWriUtils;
import cyutils.btree.Tuple;
import cyutils.btree.TupleAttribute;
import cyutils.datagenerator.BTreePrepareSortedData;
import storagemanager.StorageUtils;


public class CyUtilsAdapter extends ClientsFactory {
	private StorageUtils stoUtils;
	
	public void initialize(CyGUI gui, int clientID) {
		this.dbgui = gui;
		this.stoUtils = ((DBGui) dbgui).getStorageUtils();
	}
	
	public void execute(int clientID, String text) {
		if (this.dbgui == null) {
			System.out.println("Error! The client parser is not initialized properly."
					+ " The handle to CyDIW GUI is not initialized.");
			return;
		}
		
		text = text.trim();
		String[] commands = text.split(" ");
		
		// List Commands
		if (commands[0].equalsIgnoreCase("list") && commands[1].equalsIgnoreCase("commands")) {
			
			dbgui.addOutputPlainText("$CyUtils Commands List:");
			dbgui.addOutputPlainText("$CyUtils:> list commands;");
			dbgui.addOutputPlainText("$CyUtils:> BTreeCreateEmpty <BTreeConfigXmlFile> <TupleConfigXmlFile>;");
			dbgui.addOutputPlainText("$CyUtils:> BTreeShowRootAndSequencePageId <BTreeConfigXmlFile>;");
			dbgui.addOutputPlainText("$CyUtils:> BTreePrepareSortedData <TupleConfigXmlFile> <TupleTxtFile>;");
			dbgui.addOutputPlainText("$CyUtils:> BTreeBulkLoad <BTreeConfigXmlFile> <TupleConfigXmlFile> <TupleTxtFile>;");
			dbgui.addOutputPlainText("$CyUtils:> BTreeInsert <BTreeConfigXmlFile> <TupleConfigXmlFile> [OneTuple];");
			dbgui.addOutputPlainText("$CyUtils:> BTreeSequenceScan <BTreeConfigXmlFile> <TupleConfigXmlFile>;");
			dbgui.addOutputPlainText("$CyUtils:> CompareTuple [TupleOne] [TupleTwo] <TupleConfigXmlFile>");
			
		} else if (commands[0].equalsIgnoreCase("BTreeCreateEmpty")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreeCreateEmpty <BTreeConfigXmlFile> <TupleConfigXmlFile>;");
			
			if (commands.length != 3) {
				dbgui.addOutputPlainText("Wrong command parameters, type 'list commands' for reference");
			} else {
				
				String btreeConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				String tupleConfigFile = dbgui.getVariableValue(commands[2].trim().substring(2));;
				
				Tuple tupleDefinition = new Tuple(tupleConfigFile);
				BTree btree = new BTree(this.stoUtils, tupleDefinition);
				btree.initializeBTreeWithoutPageIdsInBTreeConfig(btreeConfigFile);

				dbgui.addOutputPlainText("Successfully build a new btree, root page id is: " 
				+ String.valueOf(btree.getRootPageId()) + ", sequence page id is: " 
						+ String.valueOf(btree.getSequencePageId()));
				
				btree.updateBTreeConfigAndStorageCatalog(btreeConfigFile);
			}
			
		} else if (commands[0].equalsIgnoreCase("BTreeShowRootAndSequencePageId")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreeShowRootAndSequencePageId <BTreeConfigXmlFile>;");
			
			if (commands.length != 2) {
				dbgui.addOutputPlainText("Wrong command parameters, type 'list commands' for reference");
			} else {
				
				String btreeConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				
				int rootPageId = BTreeWithXml.getBTreeRootPageId(btreeConfigFile);
				int sequencePageId = BTreeWithXml.getBTreeSequencePageId(btreeConfigFile);

				dbgui.addOutputPlainText("BTree root page id is: " + String.valueOf(rootPageId));
				dbgui.addOutputPlainText("BTree sequence page id is: " + String.valueOf(sequencePageId));
			}
			
		} else if (commands[0].equalsIgnoreCase("BTreePrepareSortedData")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreePrepareSortedData <TupleConfigXmlFile> <TupleTxtFile>;");
			
			if (commands.length != 3) {
				dbgui.addOutputPlainText("Wrong command parameters, type 'list commands' for reference");
			} else {
				
				String tupleConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				String tupleDataFile = dbgui.getVariableValue(commands[2].trim().substring(2));;
				
				try {
					BTreePrepareSortedData.prepare(tupleConfigFile, tupleDataFile);
				} catch (Exception e) {
					dbgui.addOutputPlainText("IO Exception during preparing sorted data for btree");
				}
			}
			
		} else if (commands[0].equalsIgnoreCase("BTreeBulkLoad")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreeBulkLoad <BTreeConfigXmlFile> <TupleConfigXmlFile> <TupleTxtFile>;");
			
			if (commands.length != 4) {
				dbgui.addOutputPlainText("Lack of command parameters, type 'list commands' for reference");
			} else {
				
				String btreeConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				String tupleConfigFile = dbgui.getVariableValue(commands[2].trim().substring(2));;
				String tupleDataFile = dbgui.getVariableValue(commands[3].trim().substring(2));
				
				Tuple tupleDefinition = new Tuple(tupleConfigFile);
				BTree btree = new BTree(this.stoUtils, tupleDefinition);
				btree.initializeBTreeWithPageIdsInBTreeConfig(btreeConfigFile);
				
				BTreeTupleGenerator tupleGenerator = new BTreeTupleGenerator(tupleDataFile, tupleDefinition);
				btree.BTreeBulkLoading(tupleGenerator);
				
				dbgui.addOutputPlainText("Successfully bulkloaded sorted data into storage!");
				
				btree.updateBTreeConfigAndStorageCatalog(btreeConfigFile);
			}
			
		} else if (commands[0].equalsIgnoreCase("BTreeInsert")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreeInsert <BTreeConfigXmlFile> <TupleConfigXmlFile> [OneTuple];");
			
			if (commands.length != 4) {
				dbgui.addOutputPlainText("Wrong command parameters, type 'list commands' for reference");
			} else {
				
				String btreeConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				String tupleConfigFile = dbgui.getVariableValue(commands[2].trim().substring(2));;
				String tupleData = commands[3];
				
				Tuple tupleDefinition = new Tuple(tupleConfigFile);
				BTree btree = new BTree(this.stoUtils, tupleDefinition);
				btree.initializeBTreeWithPageIdsInBTreeConfig(btreeConfigFile);
				String currentContent = tupleData.substring(1, tupleData.length()-1);
				String[] contentArray = currentContent.split(",");
				
				if(tupleDefinition.getAttributeNum() != contentArray.length){
					dbgui.addOutputPlainText("Tuple format error!");
					
				} else {
					// currentLine -> buffer
					byte[] tuple = new byte[tupleDefinition.getLength()];
					int offset = 0;
					int count = 0;
					for(TupleAttribute tupleAttribute : tupleDefinition.getTupleAttributes()){
						String attrType = tupleAttribute.getType();
						int attrLen = tupleAttribute.getLength();
						if(attrType.equals("Integer") && attrLen==4){
							// if it is an integer
							ReWriUtils.intToByteArray(Integer.parseInt(contentArray[count].trim()), tuple, offset);
						}else if(attrType.equals("String")){
							// if it is a string
							ReWriUtils.stringToByteArray(contentArray[count], tuple, offset, offset+attrLen);
						}
						count++;
						offset += attrLen;
					}
					btree.BTreeInsert(btree.getRootPageId(), tuple);
					dbgui.addOutputPlainText("Successfully insert this tuple!");
				}
				
				btree.updateBTreeConfigAndStorageCatalog(btreeConfigFile);
			}
			
		} else if (commands[0].equalsIgnoreCase("BTreeSequenceScan")) {
			
			dbgui.addOutputPlainText("$CyUtils:> BTreeSequenceScan <BTreeConfigXmlFile> <TupleConfigXmlFile>;");
			
			if (commands.length != 3) {
				dbgui.addOutputPlainText("Lack of command parameters, type 'list commands' for reference");
			} else {

				String btreeConfigFile = dbgui.getVariableValue(commands[1].trim().substring(2));
				String tupleConfigFile = dbgui.getVariableValue(commands[2].trim().substring(2));;
				
				Tuple tupleDefinition = new Tuple(tupleConfigFile);
				BTree btree = new BTree(this.stoUtils, tupleDefinition);
				btree.initializeBTreeWithPageIdsInBTreeConfig(btreeConfigFile);
				
				int leafpageId = btree.getSequencePageId();
				while(leafpageId != -1){
					dbgui.addOutputPlainText("Print Leaf PageId " + String.valueOf(leafpageId) + " in console!");
					leafpageId = btree.printAllTuples(leafpageId);
				}
			}
			
		} else if (commands[0].equalsIgnoreCase("CompareTuple")) {
			dbgui.addOutputPlainText("$CyUtils:> CompareTuple [TupleOne] [TupleTwo] <TupleConfigXmlFile>");
			
			if (commands.length != 4) {
				dbgui.addOutputPlainText("Lack of command parameters, type 'list commands' for reference");
			} else {
				
				String tupleData1 = commands[1];
				String tupleData2 = commands[2];
				String tupleConfigFile = dbgui.getVariableValue(commands[3].trim().substring(2));
				
				String currentContent1 = tupleData1.substring(1, tupleData1.length()-1);
				String[] contentArray1 = currentContent1.split(",");
				String currentContent2 = tupleData2.substring(1, tupleData2.length()-1);
				String[] contentArray2 = currentContent2.split(",");
				
				Tuple tupleDefinition = new Tuple(tupleConfigFile);
				if(tupleDefinition.getAttributeNum() != contentArray1.length || tupleDefinition.getAttributeNum() != contentArray2.length){
					dbgui.addOutputPlainText("Tuple format error!");
					
				} else {
					// currentLine -> buffer
					byte[] tuple1 = new byte[tupleDefinition.getLength()];
					byte[] tuple2 = new byte[tupleDefinition.getLength()];
					int offset = 0;
					int count = 0;
					for(TupleAttribute tupleAttribute : tupleDefinition.getTupleAttributes()){
						String attrType = tupleAttribute.getType();
						int attrLen = tupleAttribute.getLength();
						if(attrType.equals("Integer") && attrLen==4){
							// if it is an integer
							ReWriUtils.intToByteArray(Integer.parseInt(contentArray1[count].trim()), tuple1, offset);
							ReWriUtils.intToByteArray(Integer.parseInt(contentArray2[count].trim()), tuple2, offset);
						}else if(attrType.equals("String")){
							// if it is a string
							ReWriUtils.stringToByteArray(contentArray1[count], tuple1, offset, offset+attrLen);
							ReWriUtils.stringToByteArray(contentArray2[count], tuple2, offset, offset+attrLen);
						}
						count++;
						offset += attrLen;
					}
					dbgui.addOutputPlainText("If given bytes error, we return 0. Otherwise -1: tuple1 < tuple2, 0: tuple1 == tuple2, 1: tuple1 > tuple2");
					int res = tupleDefinition.compare(tuple1, tuple2);
					dbgui.addOutputPlainText("Comparison result for tuple1 and tuple2 is: " + String.valueOf(res));
				}
			}
			
		} else {
			dbgui.addConsoleMessage("Wrong use of commands, type 'list commands' for reference");
		}
			
	}   // end for execute method

}

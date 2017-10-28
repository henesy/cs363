package cyclients.split.adapter;

import java.io.File;

import storagemanager.StorageUtils;
import cysystem.clientsmanager.ClientsFactory;
import cysystem.clientsmanager.CyGUI;
import cysystem.diwGUI.gui.DBGui;

import cyclients.split.*;

public class SplitAdapter extends ClientsFactory {
	private int rootPageId = -1;
	private String configFile = null;
	private StorageUtils storUtils;
	
	public void initialize(CyGUI gui, int clientID) {
		dbgui = gui;
		storUtils = ((DBGui) dbgui).getStorageUtils();
	}
	
	public void execute(int clientID, String text) {
		if (this.dbgui == null) {
			System.out.println("Error! The client parser is not initialized properly. The handle to CyDIW GUI is not initialized.");
			return;
		}
		
		text = text.trim();
		String[] commands = text.split(" ");
		String function = commands[0].trim();
		
		// List Commands
		if (commands[0].equalsIgnoreCase("list") && commands[1].equalsIgnoreCase("commands")) {
			dbgui.addOutputPlainText("$Split Commands List:");
			dbgui.addOutputPlainText("$Split:> list commands;");
			dbgui.addOutputPlainText("$Split:> CreateChain <ChainConfigFile> $Chain;");
			// dbgui.addOutputPlainText("$Split:> SplitChain;");
			dbgui.addOutputPlainText("$Split:> SplitChain <ChainConfigFile> $Chain $Chain1 $Chain2;");
			// dbgui.addOutputPlainText("$Split:> SplitChain rootPageId <ChainConfigFile>;");
		}
		
		// CreateChain Command
		else if (function.equalsIgnoreCase("CreateChain")) {
			if (commands.length != 3) {
				dbgui.addConsoleMessage("Wrong use of the CreateChain command.");
				dbgui.addConsoleMessage("$Split:> CreateChain <ChainConfigFile> $Chain;");
			}
			else {
				String chainName = dbgui.getVariableValue(commands[2].trim().substring(2));
				configFile = commands[1].trim();
				// check config file extension
				String ext[] = configFile.split("\\.");
				if (ext.length < 2)
					dbgui.addConsoleMessage("Config file must include file extension");
				else if (!(ext[1].equals("xml")))
					dbgui.addConsoleMessage("Extension of config file should be .xml");
				else {
					// check config file exists
					if (!((new File(configFile)).exists()))
						dbgui.addConsoleMessage(configFile + " does not exist");
					else {
						dbgui.addOutputPlainText(chainName);
						DataGenerator dg = new DataGenerator(storUtils, configFile, chainName);
						try {
							rootPageId = dg.dataGen();
							dbgui.addOutput("Create chain successfully");
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}	
							
			}
		}   // end for CreateChain method
		
		// SplitChain Command
		else if (function.equalsIgnoreCase("SplitChain")) {
			if (commands.length != 5) {
				dbgui.addConsoleMessage("Wrong use of the SplitChain command.");
				dbgui.addConsoleMessage("$Split:> SplitChain <ChainConfigFile> $Chain $Chain1 $Chain2;");
			}
			
			else {
				String chainName = dbgui.getVariableValue(commands[2].trim().substring(2));
				String chainName1 = dbgui.getVariableValue(commands[3].trim().substring(2));
				String chainName2 = dbgui.getVariableValue(commands[4].trim().substring(2));
				configFile = commands[1].trim();
				// check config file extension
				String ext[] = configFile.split("\\.");
				if (ext.length < 2)
					dbgui.addConsoleMessage("Config file must include file extension");
				else if (!(ext[1].equals("xml")))
					dbgui.addConsoleMessage("Extension of config file should be .xml");
				else {
					// check config file exists
					if (!((new File(configFile)).exists()))
						dbgui.addConsoleMessage(configFile + " does not exist");
					else {
						rootPageId = storUtils.getXmlParser().getStartPage(chainName);
						if (rootPageId == -1) {
							dbgui.addConsoleMessage("Can not parse the RootPageId in the xmlCatalog, check catalog.");
							return;
						}
						else {
							dbgui.addConsoleMessage("RootPageId is: " + rootPageId);
						}
						splitChain sc = new splitChain(storUtils, configFile, rootPageId, chainName, chainName1, chainName2);
						try {
							sc.split();
							dbgui.addOutput("Split pages successfully");
						}
						catch (Exception ex) {
							ex.printStackTrace();
						}							
					}
				}
			}
		}    // end for SplitChain command
			
		else {
			dbgui.addConsoleMessage("Wrong use of commands, type 'list commands' for reference");
		}
			
	}   // end for execute method

}

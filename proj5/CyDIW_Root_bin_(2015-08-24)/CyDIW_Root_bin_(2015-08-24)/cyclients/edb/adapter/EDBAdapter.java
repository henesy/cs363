package cyclients.edb.adapter;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import cyclients.edb.ParseExprTree.ASTree.Edb_Query_Parser;
import cyclients.edb.ParseExprTree.ASTree.SimpleNode;
import cyclients.edb.ParseExprTree.ExpTree.XAST_To_XExpT;
import cyclients.edb.ParseExprTree.treeViewerMgr.GUI_Browser_App;
import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.HybridView;
import cyclients.edb.ParseExprTree.treeViewerMgr.TreeViewer.ParseTreeViewer;
import cyclients.edb.dataexecution.OpenIteraotr;
import cyclients.edb.dataexecution.QueryExec;
import cyclients.edb.datageneration.DataGenManager;
import cysystem.clientsmanager.ClientsFactory;
import cysystem.clientsmanager.CyGUI;
import cysystem.diwGUI.gui.DBGui;

//import speed.util.ServiceException;

import storagemanager.StorageUtils;

public class EDBAdapter extends ClientsFactory {

	private int noOfAccess = 0;
	private StorageUtils storUtils;
//	private QueryExec[] qe = new QueryExec[10];
	private QueryExec qe;
	private QueryExec[] qeitr = new QueryExec[10]; // This is for iterator execution. 
	private OpenIteraotr[] oi = new OpenIteraotr[10];
	private XAST_To_XExpT ExpressTreeWriter; // was used to produce different
												// Expression Tree // expression
	private File catFile;
	private int bufferNum = -1;
	private long time;

	public EDBAdapter(DBGui m) {
		dbgui = m;
	}

	public EDBAdapter() {
	}

	@Override
	public void initialize(CyGUI gui, int clientID) {
		dbgui = gui;
		storUtils = ((DBGui) dbgui).getStorageUtils();
	}

	@Override
	@SuppressWarnings("unused")
	public void execute(int clientID, String text) {

		if (this.dbgui == null) {
			System.out
					.println("Error! The client parser is not initialized properly. The handle to CyDIW GUI is not initialized.");
			return;
		}

		String workspacePath = dbgui.getClientsManager()
				.getClientWorkspacePath(clientID).trim();
		if ((workspacePath == null) || workspacePath.isEmpty()) {
			System.out
					.println("Warning: The workspace path of the EDB client system is not set. The query results will be stored into the current working path.");
			dbgui.addConsoleMessage("Warning: The workspace path of the EDB client system is not set. The query results will be stored into the current working path.");
		} else {
			// EXP_FILE_NAME = new File(workspacePath, EXP_FILE_NAME).getPath();
		}

		text = text.trim(); // add @9/12/2012 by xfwang
		String[] commands = text.split(" ");
		String path = null;
		String function = commands[0].trim();
		if (function.contains(":>")) {
			path = function.substring(0, function.indexOf(":"));
			function = function.substring(function.indexOf(">") + 1); // When
																		// commands
																		// include
																		// "out>>"
		}

		if (function.equalsIgnoreCase("LoadDB")) {
			if (commands.length == 3) {
				catFile = new File(dbgui.getVariableValue(commands[1].trim()
						.substring(2)));

				bufferNum = Integer.parseInt(commands[2].trim());
				System.out.println(bufferNum);
				dbgui.addOutput("Initialize EDB successfully");
			}
		} else if (function.equalsIgnoreCase("BuildInitalExpressionTree")) {

			if (commands.length == 3) {
				DocumentBuilderFactory factory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBldr = null;
				try {
					docBldr = factory.newDocumentBuilder();
				} catch (ParserConfigurationException e1) {
					e1.printStackTrace();
				}
				Document document = docBldr.newDocument();
					
				String temp = dbgui.getVariableValue(commands[1].trim().substring(6, 7));
				String temp0 = commands[1].trim().substring(0,3) + "[" + temp + "]";				
				String temp1 = commands[2].trim().substring(0,3) + "[" + temp + "]";
				
				
				String parseTree = dbgui.getVariableValue(temp0.trim()
						.substring(2));
				File parseTreeFile = new File(parseTree);

				String expr = dbgui.getVariableValue(temp1.trim()
						.substring(2));
				File intilExpresTreeFile = new File(expr);
				if (intilExpresTreeFile.exists()) {
					intilExpresTreeFile.delete();
				}

				if (parseTreeFile.exists()) {

					PrintStream pStream = null;
					try {
						pStream = new PrintStream(new FileOutputStream(
								intilExpresTreeFile), true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					ExpressTreeWriter = new XAST_To_XExpT(parseTreeFile,
							catFile);
					ExpressTreeWriter.initialize();
					ExpressTreeWriter.print(
							ExpressTreeWriter.generateIntialExp(), pStream);

					dbgui.addOutput("Initial Expression Tree built successfully");

				} else {
					dbgui.addConsoleMessage("Please Parse the query before building Expression Tree");
				}
			} else {
				dbgui.addConsoleMessage("Wrong use of Build Initial ExpressionTree command.");
			}
		} else if (function.equalsIgnoreCase("BuildOptimalExpressionTree")) {

			if (commands.length == 3) {
				
				String temp = dbgui.getVariableValue(commands[1].trim().substring(6, 7));
				String temp0 = commands[1].trim().substring(0,3) + "[" + temp + "]";				
				String temp1 = commands[2].trim().substring(0,3) + "[" + temp + "]";
				
				String intiExp = dbgui.getVariableValue(temp0.trim()
						.substring(2));
				File intiFile = new File(intiExp);
				String optimalExpr = dbgui.getVariableValue(temp1.trim()
						.substring(2));
				File optimalFile = new File(optimalExpr);
				if (intiFile.exists()) {
					PrintStream pStream = null;
					try {
						pStream = new PrintStream(new FileOutputStream(
								optimalFile), true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					ExpressTreeWriter.print(
							ExpressTreeWriter.generateOptimalExp(), pStream);
					dbgui.addOutput("Optimal Expression Tree built successfully");

				} else {
					dbgui.addConsoleMessage("Please Parse the query before building Expression Tree");
				}
			} else {
				dbgui.addConsoleMessage("Wrong use of BuildOptimalExpressionTree command.");
			}
		} else if (function.equalsIgnoreCase("DisplayExpressionTree")) {
			if (commands.length == 3) {
				String exprPath = dbgui.getVariableValue(commands[1].trim()
						.substring(2));
				if (commands[2].equals("xmlview")) {
					GUI_Browser_App.displayURL(exprPath);
					dbgui.addOutput("XML View opened successfully");
				} else if (commands[2].equals("graphicalview")) {
					// Modified by xiaofeng
					ParseTreeViewer expTreeView = new ParseTreeViewer(exprPath);
					showCenter(expTreeView);
					dbgui.addOutput("Graphical view opened successfully");
				} else if (commands[2].equals("hybridview")) {
//					try {
//						try {
//							HybridView h = new HybridView(new File(exprPath));
//						} catch (ServiceException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					} catch (ServiceException e) {
//						e.printStackTrace();
//					}
					dbgui.addOutput("Hybrid view opened successfully");
				} else {
					dbgui.addConsoleMessage("Incorrect options specified for DisplayExpressionTree command");
				}
			} else {
				dbgui.addConsoleMessage("Wrong use of BuildExpressionTree command.");
			}
		}

		else if (function.equalsIgnoreCase("BuildAST")) {
			if (commands.length == 3) {
				
				String temp = dbgui.getVariableValue(commands[1].trim().substring(6, 7));
				String temp0 = commands[1].trim().substring(0,3) + "[" + temp + "]";
				String s = dbgui.getVariableValue(temp0.substring(2));
				
				String temp1 = commands[2].trim().substring(0,3) + "[" + temp + "]";
				String parseTreePath = dbgui.getVariableValue(temp1
						.substring(2)); // Where

				String s1 = getqueryfromcmd(s);

				if (s1 == null || s1.equals("")) {
					JOptionPane.showMessageDialog(new JFrame(), new String(
							"No Query specified."));
				} else {

					String QUERY_FILE_NAME = "queryFile.txt";
					File queryFile = new File(workspacePath, QUERY_FILE_NAME);

					if (queryFile.exists()) {
						queryFile.delete();
					}
					FileOutputStream fOut = null;
					try {
						fOut = new FileOutputStream(queryFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					if (fOut != null) {
						byte[] strBytes = s1.getBytes();
						try {
							fOut.write(strBytes);
							fOut.flush();
							fOut.close();
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (noOfAccess == 0) {
							try {
								new Edb_Query_Parser(new FileInputStream(
										queryFile));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						} else {
							try {
								Edb_Query_Parser.ReInit(new FileInputStream(
										queryFile));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						}
						noOfAccess++;

						// generate the ASTree from the query
						try {

							SimpleNode root = Edb_Query_Parser.parse();
							FileOutputStream out = new FileOutputStream(
									parseTreePath);
							PrintStream p = new PrintStream(out);
							p.println(root.dump(""));
							p.close();

							dbgui.addOutput("Parse Tree Successfully Created");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} else {
				dbgui.addConsoleMessage("Wrong use of ParseExpressionTree command.");
			}
		} else if (function.equalsIgnoreCase("DisplayASTree")) {
			if (commands.length == 3) {
				String parseTreePath = dbgui.getVariableValue(commands[1]
						.substring(2));
				if (commands[2].equals("xmlview")) {
					GUI_Browser_App.displayURL(parseTreePath);
					dbgui.addOutput("XML View opened successfully");
				} else if (commands[2].equals("graphicalview")) {
					ParseTreeViewer parseView = new ParseTreeViewer(
							parseTreePath);
					showCenter(parseView);
					dbgui.addOutput("Graphical View opened successfully");
				} else if (commands[2].equals("hybridview")) {

//						try {
//							HybridView h = new HybridView(new File(parseTreePath));
//						} catch (ServiceException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}

					dbgui.addOutput("Hybrid view opened successfully");
				} else {
					dbgui.addConsoleMessage("Incorrect options specified for DisplayParseTree command");
				}
			} else {
				dbgui.addConsoleMessage("Wrong use of BuildParseTree command.");
			}
		}

		else if (function.equalsIgnoreCase("Execute")) {

			if (commands.length == 4) {
				String exprPath, exprNum;
				int index;
				if(commands[1].trim().charAt(4) == '$'){ 
					 exprNum = dbgui.getVariableValue(commands[1].trim().substring(6, 7));
					 String temp = commands[1].trim().substring(0,3) + "[" + exprNum + "]";
					 exprPath = dbgui.getVariableValue(temp.substring(2));
					 
					 index = Integer.parseInt(exprNum);
				}else{
					 exprPath = dbgui.getVariableValue(commands[1].trim()
								.substring(2));
//					 index = Integer.parseInt(exprPath.trim().substring(2, 3));
				}
				  
				
				int outBuffNum, innerBuffNum;
				if (commands[2].trim().contains("$$")) {
					outBuffNum = Integer.parseInt(dbgui
							.getVariableValue(commands[2].trim().substring(2)));
					innerBuffNum = bufferNum - outBuffNum;  //Pretend firstly. (Need to revise) 
					// innerBuffNum = Integer.parseInt(commands[3].trim());
				} else {
					outBuffNum = Integer.parseInt(commands[2].trim());
					innerBuffNum = Integer.parseInt(commands[3].trim());
				}

				time = System.currentTimeMillis();
//				qe[index] = new QueryExec(exprPath, catFile.getPath(), storUtils,
//						outBuffNum, innerBuffNum);
				storUtils = ((DBGui) dbgui).getStorageUtils();
				qe = new QueryExec(exprPath, catFile.getPath(), storUtils,
						outBuffNum, innerBuffNum);
				if (outBuffNum + innerBuffNum != bufferNum)
					dbgui.addOutput("Please allocate the bufferNum properly");
				dbgui.addOutput(qe.execute());
				time = System.currentTimeMillis() - time;
			} else if (commands.length == 3) {
				String exprPath = dbgui.getVariableValue(commands[1].trim()
						.substring(2));
				int index = Integer.parseInt(commands[1].trim().substring(2)
						.substring(2, 3));
				int outBuffNum = Integer.parseInt(commands[2].trim());
				
//				qe[index] = new QueryExec(exprPath, catFile.getPath(), storUtils,
//						outBuffNum, 0);
				qe = new QueryExec(exprPath, catFile.getPath(), storUtils,
				outBuffNum, 0);

				if (outBuffNum != bufferNum)
					dbgui.addOutput("Please allocate the buffer properly");
				dbgui.addOutput(qe.execute());
			}

			else {
				dbgui.addConsoleMessage("Wrong use of Execute command.");
			}
		} else if (function.equalsIgnoreCase("CreateData")) {
			if (commands.length == 2) {
				String ctlgFile = dbgui.getVariableValue(commands[1].trim()
						.substring(2));
				new DataGenManager(ctlgFile, storUtils).dataGenerate();
				dbgui.addOutput("Create data successfully");
			} else {
				dbgui.addConsoleMessage("Wrong use of Execute command.");
			}
		} else if (function.equalsIgnoreCase("OpenIterator")) {
			String exprPath = dbgui.getVariableValue(commands[1].trim()
					.substring(2));
			int index = Integer.parseInt(commands[1].trim().substring(2)
					.substring(2, 3));
			int outBuffNum = Integer.parseInt(commands[2].trim());

			int innerBuffNum = Integer.parseInt(commands[3].trim());
//			qe = new QueryExec(exprPath, catFile.getPath(), storUtils,
//					outBuffNum, innerBuffNum);
			qeitr[index] = new QueryExec(exprPath, catFile.getPath(), storUtils,
					outBuffNum, innerBuffNum);

			oi[index] = new OpenIteraotr();
			oi[index].pi = qeitr[index].getPi();
			oi[index].open();
			dbgui.addOutput("Iterator was open successfully");

		} else if (function.equalsIgnoreCase("HasNextTuple")) {
			int index = Integer.parseInt(commands[1].trim().substring(2)
					.substring(2, 3));
			dbgui.addOutput("Whether there is next tuple or not: "
					+ Boolean.valueOf(oi[index].hasNext()).toString());
		} else if (function.equalsIgnoreCase("GetNextTuple")) {
			int index = Integer.parseInt(commands[1].trim().substring(2)
					.substring(2, 3));
			dbgui.addOutput(oi[index].getNextTupe());
			dbgui.addOutput("The next tuple was retrieved successfully");
		} else if (function.equalsIgnoreCase("GetRemainingTuples")) {
			int index = Integer.parseInt(commands[1].trim().substring(2)
					.substring(2, 3));
			dbgui.addOutput(oi[index].getRemaining());
			dbgui.addOutput("All the remianing tuples were retreived successfully");
		} else if (function.equalsIgnoreCase("CloseIterator")) {
			System.out.println("second"+commands[1].trim().substring(2));
			int index = Integer.parseInt(commands[1].trim().substring(2)
					.substring(2, 3));
			System.out.println("third"+index + " Fourth "+oi.length+oi[index]);
			oi[index].close();
			dbgui.addOutput("The iterator was closed successfully");
		} else if (function.equalsIgnoreCase("ExitDB")) {
			catFile = null;
			dbgui.addOutput("Database was disconnected successfully");
		} else {
			dbgui.addConsoleMessage("Command not supported by CyDB");
		}
	}

	@SuppressWarnings("deprecation")
	private void showCenter(JFrame frame) {
		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		frame.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);
		frame.show();
	}

	private String getqueryfromcmd(String strGUIcmd) {
		// Gather each parameter in the command
		String[] commands = strGUIcmd.split(":>");
		String strquery = "";
		if (commands.length == 2)
			strquery = commands[1].trim();
		return strquery;
	}

	@Override
	public String getCustomLogData() {

//		String sys = "<System> EDB </System>";
		// String fileN = "<FileName> " + fileName + "</FileName>";
		// String fileS = "<FileSize> " + fileSize + "</FileSize>";
		// String heapU = "<HeapUsed>" + Integer.toString((int) heapUsed)
		// + "</HeapUsed>";
		// String memoryS = "<MemorySize>" + Integer.toString(memorySize)
		// + "</MemorySize>";
//		String pageAllocateCount = "<pageAllocateCountutils>" + storUtils.getClientCountPageAllocated()+"</pageAllocateCountutils>";
//		String pageDeallocatedCount = "<pageDeallocatedCount>" + storUtils.getClientCountPageDeallocated()+"</pageDeallocatedCount>";
//		String pageAccessCount = "<pageAccessCount>" + storUtils.getCountPageAccess()+"</pageAccessCount>";
//		String pageRequestCount = "<pageRequestCount>" + storUtils.getClientCountPageRequest()+"</pageRequestCount>";
		String pageAccessRelativeCount = "<pageAccessRelativeCount>" + storUtils.getRelativeCountPageAccess()+"</pageAccessRelativeCount>";
	    // Need more consideration
		//	String resetPageAccessRelativeCount = "<resetPageAccessRelativeCount>" +"</resetPageAccessRelativeCount>";
		//  storUtils.resetRelativeCountPageAccess();
//		String ti = "<Time>" + Integer.toString((int) time) + "</Time>";

//		return sys + pageAllocateCount + pageDeallocatedCount + pageAccessCount +pageRequestCount + pageAccessRelativeCount;
		return  pageAccessRelativeCount;

	}

}

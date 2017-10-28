package cyclients.ooquery.adapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Serializer;
import net.sf.saxon.s9api.XQueryCompiler;
import net.sf.saxon.s9api.XQueryEvaluator;
import net.sf.saxon.s9api.XQueryExecutable;

import org.dom4j.DocumentException;

import cysystem.clientsmanager.ClientsFactory;
import cysystem.clientsmanager.CyGUI;
import cysystem.diwGUI.gui.DBGui;

import cyclients.ooquery.*;

public class OoqueryAdapter extends ClientsFactory {

	private Processor proc;
	private XQueryCompiler comp;
	private boolean schemaValidationStrict;
	public void initialize(CyGUI gui, int clientID) {
		this.dbgui = gui;
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
			this.dbgui.addOutputPlainText("$Ooquery Commands List:");
			this.dbgui.addOutputPlainText("$Ooquery:> list commands;");
			this.dbgui.addOutputPlainText("$Ooquery:> createDotToIDMap  [string];");
			this.dbgui.addOutputPlainText("$Ooquery:> translate [string] [string];");
			this.dbgui.addOutputPlainText("$Ooquery:> [string];");
			this.dbgui.addOutputPlainText("$Ooquery:> statement;");
		}
		else if(commands[0].equalsIgnoreCase("translate"))
		{
			OOQueryNewParser parser = new OOQueryNewParser();
			try {
				String statement = dbgui.getVariableValue(commands[1].substring(2));

				String xqueryString = parser.parse(statement);
				dbgui.setVariableValue(commands[2].substring(2),xqueryString);
//				dbgui.
//                if (htStringList.containsKey(s1))
//                    htStringList.put(s1, replaceVariableInString(s2));
				this.dbgui.addOutputPlainText(xqueryString);
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		else if(commands[0].startsWith("$$"))
		{
			OOQueryNewParser parser = new OOQueryNewParser();
			try {
				
				
				String statement = dbgui.getVariableValue(commands[0].substring(2));
				String xqueryString = parser.parse(statement);
				this.dbgui.addOutputPlainText(xqueryString);
				
				try {

						this.proc = new Processor(true);

						this.proc
								.setConfigurationProperty(
										"http://saxon.sf.net/feature/schema-validation-mode",
										"strict");
						this.comp = this.proc.newXQueryCompiler();

						this.comp.setSchemaAware(true);
 

					Serializer out = new Serializer();
					out.setOutputProperty(Serializer.Property.METHOD, "xml");
					out.setOutputProperty(Serializer.Property.INDENT, "yes");
					out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION,
							"yes");

					OutputStream resultStream = null;
					ByteArrayOutputStream baOS = null;

					baOS = new ByteArrayOutputStream();
					out.setOutputStream(baOS);

					XQueryExecutable exp = this.comp.compile(xqueryString);
					XQueryEvaluator eval = exp.load();
					eval.run(out);

					this.dbgui.addOutput(baOS.toString());
					this.dbgui.addOutputBlankLine();
						
				} catch (Exception x) {
					this.dbgui
							.addOutputPlainText("Could not execute the command \"$"
									+ this.dbgui.getClientsManager()
											.getClientPrefix(clientID) + ":>"
									+ text + "\"");
					this.dbgui.addOutputBlankLine();
					this.dbgui.addConsoleMessage("Exception Caught: " + x);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  catch(IOException e)
			{
				e.printStackTrace();
			}			
		}		
		else if(commands[0].equalsIgnoreCase("createDotToIDMap"))
		{
			DBNewSchemaMap schemaMap = new DBNewSchemaMap();
			try {
				schemaMap.createMap(commands[1]);
				this.dbgui.addOutputPlainText("The map for database "+commands[1]+" has been created.");
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				this.dbgui
				.addOutputPlainText("Could not execute the command \"$"
						+ this.dbgui.getClientsManager()
								.getClientPrefix(clientID) + ":>"
						+ text + "\"");
				this.dbgui.addOutputBlankLine();
				this.dbgui.addConsoleMessage("Exception Caught: " + e);
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.dbgui
				.addOutputPlainText("Could not execute the command \"$"
						+ this.dbgui.getClientsManager()
								.getClientPrefix(clientID) + ":>"
						+ text + "\"");
				this.dbgui.addOutputBlankLine();
				this.dbgui.addConsoleMessage("Exception Caught: " + e);
				e.printStackTrace();
			}
		}
		else
		{
			OOQueryNewParser parser = new OOQueryNewParser();
			try {
				
				
				
				String xqueryString = parser.parse(text);
				this.dbgui.addOutputPlainText(xqueryString);
				
				try {

						this.proc = new Processor(true);

						this.proc
								.setConfigurationProperty(
										"http://saxon.sf.net/feature/schema-validation-mode",
										"strict");
						this.comp = this.proc.newXQueryCompiler();

						this.comp.setSchemaAware(true);
 

					Serializer out = new Serializer();
					out.setOutputProperty(Serializer.Property.METHOD, "xml");
					out.setOutputProperty(Serializer.Property.INDENT, "yes");
					out.setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION,
							"yes");

					OutputStream resultStream = null;
					ByteArrayOutputStream baOS = null;

					baOS = new ByteArrayOutputStream();
					out.setOutputStream(baOS);

					XQueryExecutable exp = this.comp.compile(xqueryString);
					XQueryEvaluator eval = exp.load();
					eval.run(out);

					this.dbgui.addOutput(baOS.toString());
					this.dbgui.addOutputBlankLine();
						
				} catch (Exception x) {
					this.dbgui
							.addOutputPlainText("Could not execute the command \"$"
									+ this.dbgui.getClientsManager()
											.getClientPrefix(clientID) + ":>"
									+ text + "\"");
					this.dbgui.addOutputBlankLine();
					this.dbgui.addConsoleMessage("Exception Caught: " + x);
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(IOException e)
			{
				e.printStackTrace();
			}
			
		}

	}   // end for execute method

}
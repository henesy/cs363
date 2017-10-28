
package cyclients.Neo4j.adapter;
import org.neo4j.jdbc.Driver;
//import org.neo4j.graphdb.*;
import org.neo4j.jdbc.Neo4jConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;	

import java.io.BufferedReader;
import java.io.InputStreamReader;

import cysystem.clientsmanager.ClientsFactory;
import cysystem.clientsmanager.CyGUI;
import cysystem.diwGUI.gui.DBGui;

import cyclients.Neo4j.*;


public class Neo4jAdapter extends ClientsFactory
{
	private DBGui guiframe = null;
	
	public void execute(int clientID, String command) {
		
//		String path="cmd /c start C:\\Users\\yingw\\Desktop\\Shivan\\neo4j-community-2.1.8\\bin\\Neo4j.bat";
//		Runtime rn=Runtime.getRuntime();
//		try
//		{
//			Process pr=rn.exec(path);
//		} catch (Exception e)
//		{
//			// TODO: handle exception
//		}
		
		String prefix = dbgui.getClientsManager().getClientPrefix(clientID);
		String workspacePath = this.dbgui.getClientsManager().getClientWorkspacePath(clientID).trim();
		String resultFileName = null;
		
		if (this.dbgui == null) {
			System.out.println("Error! The client parser is not initialized properly. The handle to CyDIW GUI is not initialized.");
			return;
		}
		
		if (!prefix.equalsIgnoreCase("Neo")) {
			dbgui.addOutputPlainText("Could not execute the command \"$" + prefix + ":>" + command + "\"");
        	dbgui.addConsoleMessage("Error! The command is not a valid Neo4j command and cannot be processed.");
			return;
		}
		
		// get the classpath of R and surround it with double quotes
		String Rpath = dbgui.getClientsManager().getClientClassPath(clientID);
		
		
		//if (!Rpath.endsWith("R"))
		//	Rpath += "/R";
		// dbgui.addOutputPlainText("aagaya ");
		 dbgui.addOutputPlainText(command);
		 
		
		 try {
		     // Make sure Neo4j Driver is registered
		Class.forName("org.neo4j.jdbc.Driver");

		// Connect
		//System.out.println("x");
		Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
		//System.out.println("x");
		 dbgui.addOutputPlainText("connected to local host");
		// Querying
		try(Statement stmt = con.createStatement())
		{
		    ResultSet rs = stmt.executeQuery("CREATE (you:Person {name:'abc'})RETURN you");
		    ResultSet res = stmt.executeQuery("MATCH  (you:Person {name:'abc'}) RETURN you;");
		    // dbgui.addOutputPlainText(res);
		    System.out.println(res);
		    
		  //  res = stmt.executeQuery("MATCH (n:`Movie`) RETURN n LIMIT 25");
		  long startTime=0;
		  long compile_executionTime=0;
		 // dbgui.addOutputPlainText(Long.toString(startTime)+"Start time and end time set to 0"+Long.toString(compile_executionTime));
		 startTime = System.currentTimeMillis();
		  res = stmt.executeQuery(command);
		  compile_executionTime = System.currentTimeMillis()
						- startTime;
			//dbgui.addOutputPlainText(Long.toString(startTime)+"Start time and current time"+Long.toString(System.currentTimeMillis()));			
		  dbgui.addConsoleMessage("Execution time "+Long.toString(compile_executionTime));	
	       ResultSetMetaData rsmd = res.getMetaData();
		   
		    int cols = rsmd.getColumnCount();
	       System.out.println(cols);
		    //dbgui.addOutputPlainText(cols);
		    System.out.printf("The query fetched %d columns\n",cols);

		           System.out.println("These columns are: ");
		           for (int i=1;i<=cols;i++) {

		               String colName = rsmd.getColumnName(i);

		               String colType = rsmd.getColumnTypeName(i);

		               System.out.println(colName+" of type "+colType);

					 //  dbgui.addOutputPlainText(colName+" of type "+colType);
		                

		           }
		    

//		   while(res.next()) {
//		  	System.out.println(res);
//			dbgui.addOutputPlainText(res.getString(1));
//		
//		  }
		           int rowCount = 0;
		   		String rsOutput;

		   		rsOutput = "<TABLE BORDER=1>";
		   		//ResultSetMetaData rsmd = res.getMetaData();
		   		int columnCount = rsmd.getColumnCount();
		   		// table header
		   		rsOutput += "<TR>";
		   		for (int i = 0; i < columnCount; i++) {
		   			rsOutput += "<TH>" + rsmd.getColumnLabel(i + 1) + "</TH>";
		   		}
		   		rsOutput += "</TR>";
		   		// the data
		   		while (res.next()) {
		   			rowCount++;
		   			rsOutput += "<TR>";
		   			for (int i = 0; i < columnCount; i++) {
		   				rsOutput += "<TD>" + res.getString(i + 1) + "</TD>";
		   			}
		   			rsOutput += "</TR>";
		   		}
		   		rsOutput += "</TABLE>";
		   		dbgui.addOutput(rsOutput);

		   	//	return rowCount;
		    
		   //System.out.println(res);
		}
		 } catch (Exception e) {
		     
		 }   
		
		
		
		
	   
	}
}



//public class Neo4jAdapter {
////Class.forName("org.neo4j.jdbc.Driver");
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
// try {
//     // Make sure Neo4j Driver is registered
//Class.forName("org.neo4j.jdbc.Driver");
//
//// Connect
//System.out.println("x");
//Connection con = DriverManager.getConnection("jdbc:neo4j://localhost:7474/");
//System.out.println("x");
//// Querying
//try(Statement stmt = con.createStatement())
//{
//    ResultSet rs = stmt.executeQuery("CREATE (you:Person {name:'abc'})RETURN you");
//    ResultSet res = stmt.executeQuery("MATCH  (you:Person {name:'abc'}) RETURN you;");
//    
//    System.out.println(res);
//    
//    res = stmt.executeQuery("MATCH (n:`Movie`) RETURN n LIMIT 25");
//   
//       ResultSetMetaData rsmd = res.getMetaData();
//   
//    int cols = rsmd.getColumnCount();
//    System.out.println(cols);
//    System.out.printf("The query fetched %d columns\n",cols);
//
//           System.out.println("These columns are: ");
//
//           for (int i=1;i<=cols;i++) {
//
//               String colName = rsmd.getColumnName(i);
//
//               String colType = rsmd.getColumnTypeName(i);
//
//               System.out.println(colName+" of type "+colType);
//
//                
//
//           }
//    
//
//   while(res.next()) {
//  	System.out.println(res);
//  }
//    
//   //System.out.println(res);
//}
// } catch (Exception e) {
//     
// }       
//    }
//}
//
//
//
//class PrintColumnTypes  {
//
//	  public static void printColTypes(ResultSetMetaData rsmd)
//	                            throws SQLException {
//		  System.out.println("idk");
//	    int columns = rsmd.getColumnCount();
//	    for (int i = 1; i <= columns; i++) {
//	      int jdbcType = rsmd.getColumnType(i);
//	      String name = rsmd.getColumnTypeName(i);
//	      System.out.print("Column " + i + " is JDBC type " + jdbcType);
//	      System.out.println(", which the DBMS calls " + name);
//	    }
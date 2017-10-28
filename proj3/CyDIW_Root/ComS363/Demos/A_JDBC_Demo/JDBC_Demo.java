// Section 0. Import Java.sql package
import java.sql.*;

public class JDBC_Demo {

   public static void main (String [] args) {

      // ***************************************************************
      // A. Report Salaries, count and total salary of employees in Toys
      // ***************************************************************

      // Section 1: Load the driver 
      try {   
         // Load the driver (registers itself)
         Class.forName ("com.mysql.jdbc.Driver");
         } 
      catch (Exception E) {
            System.err.println ("Unable to load driver.");
            E.printStackTrace ();
      } 

      try { 

         // Section 2. Connect to the databse
         Connection conn1; // An object of type connection 
         String dbUrl = "jdbc:mysql://csdb.cs.iastate.edu:3306/db363demo";
         String user = "dbu363demo";
         String password = "demo-89";
         conn1 = DriverManager.getConnection (dbUrl, user, password);
         System.out.println ("*** Connected to the database ***"); 

         // Section 3A. Create stmt1 object conn1
         Statement stmt1 = conn1.createStatement ();

         // Section 4A. Execute a query, receive result in a result set 
         ResultSet rs1 = stmt1.executeQuery ("select e.Name, e.Salary" + " " + 
                                             "from Emp e" + " " + 
                                             "where e.DName = 'Toys' "); 

         // Section 5A. Process the result set 

         //Print a header for the report:
         System.out.println ( );		
         System.out.println ("Name           Salary");		
         System.out.println ("----           ------");		

         //Print report:

         int count = 0;
         double totalPayroll = 0.0; 

         String jName; // To store value of Name attribute 
         double jSalary; // To store value of Salary attribute
         while(rs1.next()) {
            // Access and print contents of one tuple
            jName = rs1.getString (1); // Value accessed by its position
            jSalary = rs1.getInt ("Salary"); // Access by attribute Name
            System.out.println (jName + "   " + jSalary);
            count = count + 1; 
            totalPayroll = totalPayroll + jSalary;
         }			

         // Print report of Toys department: 
         System.out.println ( ); 
         System.out.println ( );		
         System.out.println ("Number of employees in Toys Department: " + count);
         System.out.println ("Total payroll for Toys Department: " + totalPayroll);

         // Section 6A. Close statement 
         stmt1.close (); 

      // ***************************
      // B. Using Prepared Statement  
      // ***************************

         // Section 3B. Create a Prepared Statement object 
         PreparedStatement stmt2 = conn1.prepareStatement ("update Emp"  + " " + 
                                                           "set DName=? , Salary=?" + " " + 
                                                           "where Name = ? " ); 

         // Section 4B(i) Execute stmt2; Move Hari to Shoes with a salary of $65,000: 
         stmt2.setString(1,"Shoes");
         stmt2.setDouble(2,65000.0);
         stmt2.setString(3,"Hari");
         stmt2.executeUpdate();											  	                          

         // Section 4B(ii) Execute stmt2; Move Leu to Credit with a salary of $70,000
         stmt2.setString(1,"Credit");
         stmt2.setDouble(2,75000.0);
         stmt2.setString(3,"Leu");
         stmt2.executeUpdate();											  	                          

         // Section 6B. Close statement 
         stmt2.close (); 

         // Section 7. close connection
         
         conn1.close (); 

      } // End of try

      catch (SQLException E) {
         System.out.println ("SQLException: " + E.getMessage());
         System.out.println ("SQLState: " + E.getSQLState());
         System.out.println ("VendorError: " + E.getErrorCode());

      } // End of catch

   } // end of main

} //end of class DemoJDBC

package JDBC;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class P3 {
	public static void main(String[] args) throws Exception {
		// Load and register a JDBC driver
		try {
			// Load the driver (registers itself)
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception E) {
			System.err.println("Unable to load driver.");
			E.printStackTrace();
		}
		try {
			// Connect to the database
			Connection conn1;
			String dbUrl = "jdbc:mysql://mysql.cs.iastate.edu:3306/db363seh";
			String user = "dbu363seh";
			String password = "s0bDE2rC";
			conn1 = DriverManager.getConnection(dbUrl, user, password);
			System.out.println("*** Connected to the database ***");

			// Create Statement and ResultSet variables to use throughout the project
			Statement statement = conn1.createStatement();
			ResultSet rs;

			/* Begin A-F */
			
			//A
			System.out.println("\n\nA");
			rs = statement.executeQuery("Select Person.Name, Instructor.Salary from Person inner join Instructor on Person.ID = Instructor.InstructorID");

			int totalSalary = 0;
			int salary = 0;
			String name = "";

			while (rs.next()) {
				salary = rs.getInt("Salary");
				name = rs.getString("Name");
				totalSalary += salary;	
				
				System.out.println(name + ": " + salary);
			}
			System.out.println("\nTotal Salary: " + totalSalary);
			
			//B TODO
			System.out.println("\n\nB");
			
			//create MeritList table if it doesn't exist
			DatabaseMetaData dbm = conn1.getMetaData();
			ResultSet tables = dbm.getTables(null, null, "MeritList", null);
			if (!tables.next()) {
				statement.executeUpdate("create table MeritList (StudentID char (9) not null, Classification char (10), GPA double,  MentorID char(9), CreditHours int, Primary key (StudentID), Foreign key (MentorID) references Instructor(InstructorID))");
			}
			
			//select from students
			Statement statement2 = conn1.createStatement();
			rs = statement2.executeQuery("select StudentID, Classification, MentorID, GPA, CreditHours from Student order by GPA desc");
			
			int s = 0;
			double lastgpa = -1.0;
			while(rs.next()) {
				String sid = "";
				String classi = "";
				String mid = "";
				int ch = -1;
				double gpa = -1.0;
				
				//parse student row
				sid = rs.getString("StudentID");
				classi = rs.getString("Classification");
				mid = rs.getString("MentorID");
				ch = rs.getInt("CreditHours");
				gpa = rs.getDouble("GPA");
				
				if(s == 19) {
					//on 20th student
					lastgpa = gpa;
				} else if(s > 19) {
					//after 20th student
					if(gpa != lastgpa) {
						break;
					}
				}
				
				// add student to MeritList
				String update = "Insert Into MeritList (StudentID, Classification, GPA, MentorID, CreditHours) Values (";
				update += ("\"" + sid + "\"" + ", ");
				update += ("\"" + classi + "\"" + ", ");
				update += (gpa + ",");
				update += ("\"" + mid + "\"" + ", ");
				update += (ch + ")");
				//System.out.println("Update is: " + update);
				statement.executeUpdate(update);
				s += 1;
			}
			statement2.close();
			
			//C TODO
			System.out.println("\n\nC");
			
			rs = statement.executeQuery("select * from MeritList m order by m.GPA");
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			while (rs.next()) {
			    for(int i = 1; i < columnsNumber; i++)
			        System.out.print(rs.getString(i) + " ");
			    System.out.println();
			}
			
		    //D TODO
			System.out.println("\n\nD");
			
		    
		    //E TODO
			System.out.println("\n\nE");
		    
		    //F TODO
			System.out.println("\n\nF");

		
			/* Close all statements and connections */
			statement.close();
			rs.close();
			conn1.close();

		} catch (SQLException e) {
			System.out.println("SQLException: " + e.getMessage());
			System.out.println("SQLState: " + e.getSQLState());
			System.out.println("VendorError: " + e.getErrorCode());
		}
	}
}

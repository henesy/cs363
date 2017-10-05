package JDBC;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class P3 {
	private static HashMap<String, Integer> hm;
	
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
			//for comparator
			hm = new HashMap<String, Integer>();
			hm.put("Freshman", 0);
			hm.put("Sophomore", 1);
			hm.put("Junior", 2);
			hm.put("Senior", 3);
			
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
			
			//B
			//System.out.println("\n\nB");
			
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
				double gpa = -1.0;
				String mid = "";
				int ch = -1;
				
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
			
			//C 
			System.out.println("\n\nC");
			
			rs = statement.executeQuery("select * from MeritList m order by m.GPA");
			ResultSetMetaData rsmd = rs.getMetaData();
			int cn = rsmd.getColumnCount();

			while (rs.next()) {
			    for(int i = 1; i < cn; i++) {
			        System.out.print(rs.getString(i) + "|");
			    }
			    System.out.println();
			}
			
		    //D
			//System.out.println("\n\nD");
			
			//get meritlist mentors
			rs = statement.executeQuery("select Classification, MentorID from MeritList order by MentorID");
			HashMap<String, String> instrs = new HashMap<String, String>();
			List<String> instrName = new ArrayList<String>();
			
			while (rs.next()) {
			    String id = rs.getString("MentorID");
			    String cl = rs.getString("Classification");
			    
			    if(!instrs.containsKey(id)) {
			    	//add instructor
			    	instrs.put(id, cl);
			    	instrName.add(id);
			    } else if(instrs.containsKey(id)) {
			    	//see if we update classification
			    	if(compClassi(instrs.get(id), cl) < 0) {
			    		instrs.put(id, cl);
			    	}
			    }
			}
			
			//update instructors with raises
			int j = 0;
			for(j = 0; j < instrName.size(); j++) {
				String id = instrName.get(j);
				double raise = -1;
				
				//System.out.println("Entry is: " + id + ", " + instrs.get(id));
				
				if(instrs.get(id).equals("Freshman"))
					raise = 1.04;
				if(instrs.get(id).equals("Sophomore"))
					raise = 1.06;
				if(instrs.get(id).equals("Junior"))
					raise = 1.08;
				if(instrs.get(id).equals("Senior"))
					raise = 1.10;
				
				String str0 = "Update Instructor Set Instructor.Salary = CASE WHEN Instructor.InstructorID = \"";
				//ID here
				String str1 = "\" then Instructor.Salary*";
				//raise here
				String str2 = " ELSE Instructor.Salary END";
				
				String update = str0 + id + str1 + raise + str2;
				//System.out.println("Update is: " + update);
				statement.executeUpdate(update);
			}
		    
		    //E
			System.out.println("\n\nE");
			rs = statement.executeQuery("Select Person.Name, Instructor.Salary from Person inner join Instructor on Person.ID = Instructor.InstructorID");

			totalSalary = 0;
			salary = 0;
			name = "";

			while (rs.next()) {
				salary = rs.getInt("Salary");
				name = rs.getString("Name");
				totalSalary += salary;	
				
				System.out.println(name + ": " + salary);
			}
			System.out.println("\nTotal Salary: " + totalSalary);
		    
		    //F
			//System.out.println("\n\nF");
			statement.executeUpdate("drop table MeritList");
		
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
	
	private static int compClassi(String a, String b) {
		if(hm.get(a) < hm.get(b))
			return -1;
		if(hm.get(a) > hm.get(b))
			return 1;
		return 0;
	}
}

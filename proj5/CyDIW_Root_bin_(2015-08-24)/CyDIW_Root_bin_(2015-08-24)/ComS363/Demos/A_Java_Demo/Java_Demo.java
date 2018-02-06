//Import the java.io and java.* packages 
import java.io.*;
import java.lang.*;

public class Java_Demo
{
   public static void main(String[] args) 
   {
     String FirstPart = "JAVA";
     String SecondPart = "IS WORKING";
     System.out.println(JoinThem(FirstPart, SecondPart));

   } // end of main 

   public static String JoinThem(String Part1, String Part2) 
   {
      String temp= new String();
      temp = Part1 + " " + Part2;
      return temp;
   }
}  //end of class example


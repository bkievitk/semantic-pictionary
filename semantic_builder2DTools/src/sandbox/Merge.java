package sandbox;

import java.io.*;
import java.util.*;
import tools.*;

public class Merge {

	public static void main(String[] args) {
		
		/*BufferedWriter w = new BufferedWriter(new FileWriter);
		CSV csv1 = new CSV(new File("C:/Users/Brent/Desktop/accuracy.csv"));
		CSV csv2 = new CSV(new File("C:/Users/Brent/Desktop/new.csv"));

		Hashtable<String,Integer> lines = new Hashtable<String,Integer>();
		int lineID = 0;
		for(String[] line : csv2.lines) {
			lines.put(line[0], lineID);
			lineID++;
		}
		
		for(String[] line : csv1.lines) {
			for(int i=0;i<line.length;i++) {
				
			}
		}*/
		

		CSV csv2 = new CSV(new File("C:/Users/Brent/Desktop/accuracy.csv"));
		CSV csv1 = new CSV(new File("C:/Users/Brent/Desktop/new.csv"));

		csv1.readAll();
		csv2.readAll();
		
		CSV csv = csv1.joinOnRow(csv2, 0, 0, false);
		csv.write(new File("C:/Users/Brent/Desktop/join.csv"));
		csv.close();
		
	}
}

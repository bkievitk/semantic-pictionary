package integration;

import java.io.*;

public class Wikipedia {
	public static void main2() {
		
		try {
			BufferedReader wikiReader = new BufferedReader(new FileReader(new File("E:/wikipedia/enwiki-20091103-pages-meta-current.xml")));
			String line;
			int state = 0;
			
			while((line = wikiReader.readLine()) != null) {
				
				if(line.contains("<text xml")) {
					state = 1;
				} 
				
				if(line.contains("</text>")) {
					state = 2;
				}
				
				if(state == 1) {
					
				}
			}		
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

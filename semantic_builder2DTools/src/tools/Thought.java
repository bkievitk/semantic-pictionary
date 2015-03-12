package tools;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class Thought implements Serializable {
	
	private static final long serialVersionUID = -8837166574013829572L;
	
	// Track word name and data.
	public String representation;
	public float frequency;
	public double[] lexical;
	public double[] environmental;
	
	public Vector<double[]> tmp;
	
	public static void main(String[] args) {
		HashSet<String> stopList1 = loadStoplist(new File("data/stop.txt"));
		HashSet<String> stopList2 = loadStoplist(new File("data/stop2.txt"));

		Iterator<String> it = stopList1.iterator();
		while(it.hasNext()) {
			String word = it.next();
			if(!stopList2.contains(word)) {
				System.out.println(word + " is unique to l1");
			}
		}
		
		System.out.println();
		
		it = stopList2.iterator();
		while(it.hasNext()) {
			String word = it.next();
			if(!stopList1.contains(word)) {
				System.out.println(word + " is unique to l2");
			}
		}
		
		//Vector<Thought> words = loadWords(new File("data/words.txt"), stopList);
		//System.out.println("Size: " + words.size());
		//for(Thought word : words) {
		//	System.out.println(word);
		//}
		
	}

	public static int getThoughtCountLong() {
		HashSet<String> stopList = Thought.loadStoplist(new File("data/stop.txt"));
		Vector<Thought> words = Thought.loadWords(new File("data/words.txt"), stopList);
		return words.size();
	}
	
	public Thought(String word, float frequency) {
		this.representation = word;
		this.frequency = frequency;
	}
	
	public String toString() {
		return representation + " [" + frequency + "]";
	}
	
	/**
	 * Build a stoplist from a given file.
	 * Each line is a new word.
	 * @param f
	 * @return
	 */
	public static HashSet<String> loadStoplist(File f) {
		HashSet<String> stoplist = new HashSet<String>();
		
		CSV csv = new CSV(f);
		String[] line;
		
		while((line = csv.getLine()) != null) {
			stoplist.add(line[0].trim());
		}

		return stoplist;
	}

	public static Hashtable<String,Thought> buildWordSet(Vector<Thought> words) {
		Hashtable<String,Thought> ret = new Hashtable<String,Thought>();
		for(Thought word : words) {
			ret.put(word.representation, word);
		}
		return ret;
	}
	
	/**
	 * Load words from a file in csv form using X format.
	 * @param f
	 * @param stopList
	 * @return
	 */
	public static Vector<Thought> loadWords(File f, HashSet<String> stopList) {
		
		Vector<Thought> ret = new Vector<Thought>();
		
		CSV csv = new CSV(f);
		String[] line;
		
		HashSet<String> words = new HashSet<String>();
		
		while((line = csv.getLine()) != null) {
			String word = line[0].trim();
			word = word.replaceAll(" ?\\(.*\\)", "");
			
			if(word.indexOf(' ') != -1) {
				// Ignore sentences.
			}
			else {
				word = word.replaceAll("[^a-zA-Z]", "");
				
				// Only add words without spaces.
				if(stopList == null || !stopList.contains(word)) {
					if(!words.contains(word)) {
						words.add(word);
						float frequency = Float.parseFloat(line[line.length-1]);
						ret.add(new Thought(word,frequency));
					}
				}
			}
		}

		return ret;
	}
}

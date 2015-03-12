package mcrae;

import gui.WordMap;

import java.io.*;
import java.util.HashSet;

import modelManager.SemanticSpaceDiskReader;
import relations.beagle.VectorTools;
import relations.wordnet.WordRelatorWordNet;
import rosch.WordRelationCrystalized;

public class Sims {

	public static void main(String[] args) {

		try {
			WordMap wm = new WordMap();
			
			WordRelationCrystalized rel1 = new WordRelationCrystalized(null, wm, new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_beagle.csv"))));
			WordRelationCrystalized rel2 = new WordRelationCrystalized(null, wm, new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_histogram.csv"))));
			WordRelationCrystalized rel3 = new WordRelationCrystalized(null, wm, new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_pixel.csv"))));
			WordRelationCrystalized rel4 = new WordRelationCrystalized(null, wm, new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_recursive.csv"))));
			
			WordRelationCrystalized relMcRae = new WordRelationCrystalized(null, wm, new BufferedReader(new FileReader(new File("similarity/McRaeNormsCosine.csv"))));

			SemanticSpaceDiskReader reader = new SemanticSpaceDiskReader(new File("word_labels.txt"),new File("matrix.txt"),14002);

			WordRelatorWordNet.loadWordNet(wm);
			WordRelatorWordNet wordNet = new WordRelatorWordNet(null, null, wm);
			wordNet.type = WordRelatorWordNet.TYPE_JIANG_AND_CONRATH;
			
			HashSet<String> words = new HashSet<String>();
			for(String word : rel1.getWords()) {
				if(relMcRae.wordToInt.containsKey(word) && reader.getID(word) != null && (WordRelatorWordNet.test(word) || !WordRelatorWordNet.test(word.replaceAll("s$", "")))) {
					if(word.equals("bluejay") || word.equals("shelve") || word.equals("bluejays") || word.equals("shelves")) {
					} else {
						words.add(word);
					}
				}
			}
			
			
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("mcRaeXBeagleXSP.csv")));
			w.write("word1,word2,SPBeagle,SPHistogram,SPPixel,SPRecurse,MCRae,BEAGLE,WN\r\n");
			int total = words.size() * words.size();
			int count = 0;
			int percent = 0;
			
			for(String word1 : words) {
				for(String word2 : words) {
					if(word1 != word2) {

						count++;
						int newPercent = count * 100 / total;
						if(newPercent > percent) {
							System.out.println(newPercent);
							percent = newPercent;
						}
						
						w.write(word1 + "," + word2 + ",");
						w.write(rel1.getDistance(word1, word2) + ",");
						w.write(rel2.getDistance(word1, word2) + ",");
						w.write(rel3.getDistance(word1, word2) + ",");
						w.write(rel4.getDistance(word1, word2) + ",");
						w.write(relMcRae.getDistance(word1, word2) + ",");	
						w.write(VectorTools.getAngle(reader.getVector(word1), reader.getVector(word2)) + ",");

						if(!WordRelatorWordNet.test(word1)) {
							word1 = word1.replaceAll("s$", "");
						}
						if(!WordRelatorWordNet.test(word2)) {
							word2 = word2.replaceAll("s$", "");
						}
						
						w.write(wordNet.getDistance(word1, word2) + "\r\n");
					}
				}	
			}
			w.close();

			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

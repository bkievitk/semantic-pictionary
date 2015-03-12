package dissertation;

import java.io.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import modelManager.Correlation;
import modelManager.Guess;
import modelManager.SemanticSpaceDiskReader;

import tools.VectorTools;

import admin.ModelData;
import admin.ModelManager;

public class PredictFromGuesses {

	public static void main(String[] args) {
		
		Vector<Guess> guesses = Guess.getAllGuesses(new File("8-21-2014_guesses.dat"));
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		ModelManager.refineModels(models);	
		Guess.linkModels(models, guesses);
		
		// Load words.
		Hashtable<String,Integer> wordIDs = new Hashtable<String,Integer>();
		Vector<String> words = new Vector<String>();
		
		try {
			URL url = new URL("http://www.indiana.edu/~semantic/io/getAllWords.php");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = r.readLine()) != null) {
				String[] parts = line.split(",");
				String word = parts[0];
				int id = Integer.parseInt(parts[1]) - 1;
				
				wordIDs.put(word, id);
				
				while(words.size() <= id) {
					words.add(null);
				}
				words.set(id, word);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double[][] counts = new double[words.size()][words.size()];
		for(Guess guess : guesses) {
			if(guess.model != null) {
				counts[guess.model.wordID - 1][guess.guessID - 1]++;
			}
		}
		
		for(int i=0;i<counts.length;i++) {
			int sum = 0;
			for(int j=0;j<counts.length;j++) {
				sum += counts[i][j];
			}
			for(int j=0;j<counts.length;j++) {
				if(sum > 0) {
					counts[i][j] /= sum;
				}
			}
		}		
		
		Vector<Double> vp1 = new Vector<Double>();
		Vector<Double> vp2 = new Vector<Double>();
		
		SemanticSpaceDiskReader reader = new SemanticSpaceDiskReader(new File("../math_dynamicalSystemWordSpace/word_labels.txt"),new File("../math_dynamicalSystemWordSpace/matrix.txt"),14002);
		double[][] counts2 = new double[words.size()][words.size()];
		for(int i=0;i<counts2.length;i++) {
			String word1 = words.get(i).replaceAll("_.*", "");
			double[] v1 = reader.getVector(word1);
			
			for(int j=0;j<counts2.length;j++) {
				String word2 = words.get(j).replaceAll("_.*", "");
				double[] v2 = reader.getVector(word2);
				
				if(v1 == null || v2 == null) {
					counts2[i][j] = -1;
				} else {
					counts2[i][j] = VectorTools.getCosine(v1, v2);
					
					if(counts[i][j] > 0 && counts2[i][j] > 0 && j != i) {
						vp1.add(counts[i][j]);
						vp2.add(counts2[i][j]);
					}
				}
				
			}
		}

		double[] p1 = new double[vp1.size()];
		double[] p2 = new double[vp2.size()];
		for(int i=0;i<vp1.size();i++) {
			p1[i] = vp1.get(i);
			p2[i] = vp2.get(i);
		}
		
		double c = Correlation.distance(p1, p2, Correlation.CORRELATION_PEARSON);
		System.out.println(c);
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("correlation.csv")));
			for(int i=0;i<vp1.size();i++) {
				w.write(vp1.get(i) + "," + vp2.get(i) + "\r\n");
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		/*
		System.out.println("-- bad matches --");
		int count = 0;
		for(int i=0;i<counts.length;i++) {
			String word = words.get(i);
			int start = word.indexOf("_");
			if(start > 0) {
				word = word.substring(0, start);
			}
			
			if(reader.getID(word) == null) {
				System.out.println(word);
				count++;
			}
		}
		System.out.println(count);
		*/
		
		
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("guessErrorMatrix.csv")));
			for(int i=0;i<counts.length;i++) {
				w.write("," + words.get(i));
			}
			w.write("\r\n");
			
			for(int i=0;i<counts.length;i++) {				
				w.write(words.get(i));
				for(int j=0;j<counts.length;j++) {					
					w.write("," + counts[i][j]);
				}
				w.write("\r\n");
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("BEAGLESimMatrix.csv")));
			for(int i=0;i<counts.length;i++) {
				w.write("," + words.get(i));
			}
			w.write("\r\n");
			
			for(int i=0;i<counts.length;i++) {				
				w.write(words.get(i));
				for(int j=0;j<counts.length;j++) {					
					w.write("," + counts2[i][j]);
				}
				w.write("\r\n");
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		

		
		
	}
}

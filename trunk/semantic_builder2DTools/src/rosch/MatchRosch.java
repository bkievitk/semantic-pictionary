package rosch;

import gui.WordMap;
import integration.HistogramIntegration;

import java.awt.Color;
import java.io.*;
import java.util.*;

import tools.Stats;
import tools.VectorTools;
import admin.ModelData;
import admin.ModelManager;

public class MatchRosch {

	public static void main(String[] args) {
		try {
			
			Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
			ModelManager.refineModels(models);	
			Hashtable<String,Vector<ModelData>> modelSort = ModelManager.getAllWordModels(models);
						
			BufferedReader r = new BufferedReader(new FileReader(new File("Typicality_Ratings.csv")));
			String line = r.readLine();
			Hashtable<String,Hashtable<String,Double>> rosch = new Hashtable<String,Hashtable<String,Double>>();
			while((line = r.readLine()) != null) {
				String[] parts = line.split(",");
				String category = parts[0].trim().toLowerCase();
				String word = parts[1].trim().toLowerCase();
				double rating = Double.parseDouble(parts[2]);
				
				if(word.equals("bicycle")) {
					word = "bicycles";
				} else if(word.equals("tank")) {
					word = "tank_(army)";
				} else if(word.equals("crayon")) {
					word = "crayons";
				}

				if(category.length() > 0 && word.length() > 0) {
					
					Hashtable<String,Double> cat = rosch.get(category);
					if(cat == null) {
						cat = new Hashtable<String,Double>();
						rosch.put(category, cat);
					}
					
					cat.put(word, rating);
				}
			}
			
			/*
			for(String catVal : rosch.keySet()) {
				int found = 0;
				for(String wordVal : rosch.get(catVal).keySet()) {
					
					if(modelSort.containsKey(wordVal)) {
						//System.out.println("Match: " + wordVal);
						found++;
					} else {
						boolean matched = false;
						for(String wordMatch : modelSort.keySet()) {
							if(wordMatch.length() > 0 && wordVal.contains(wordMatch) || wordMatch.contains(wordVal)) {
								System.out.println("Possible: " + wordMatch + " " + wordVal);
							}
						}
						if(!matched) {
							//System.out.println("No match: " + wordVal);							
						}
					}
				}
				System.out.println(catVal + " " + found);
			}
			*/
			r.close();
			
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("roschOut2.csv")));
			
			/*
			Hashtable<String,double[]> store = new Hashtable<String,double[]>();
			
			// For each category.
			for(String catVal : rosch.keySet()) {
				w.write(catVal + "\r\n");
				w.write("word,rosch,histogram\r\n");
				
				double[] overallSum = null;
				int count = 0;
				
				// For each words.
				for(String word : rosch.get(catVal).keySet()) {
					Vector<ModelData> list = modelSort.get(word);
					if(list != null) {
						double[] sum = null;
						for(ModelData model : list) {
							double[] toAdd = HistogramIntegration.toVector(Stats.histogram(model, 5));
							if(sum == null) {
								sum = toAdd;
							} else {
								VectorTools.setAdd(toAdd, sum);
							}
						}
						
						// Average for the word.
						double[] avg = VectorTools.mult(sum, 1.0 / list.size());
						store.put(word, avg);
						
						if(overallSum == null) {
							overallSum = VectorTools.mult(avg, 1);
						} else {
							VectorTools.setAdd(avg, overallSum);
						}
						count ++;
					}
				}
				
				double[] overallAvg = VectorTools.mult(overallSum, 1.0 / count);

				for(String word : rosch.get(catVal).keySet()) {
					double[] vec = store.get(word);
					if(vec != null) {
						double cos = VectorTools.getCosine(vec,overallAvg);
						w.write(word + "," + rosch.get(catVal).get(word) + "," + cos + "\r\n");
					}
				}
			}
			*/
			
			WordRelationCrystalized rel1 = new WordRelationCrystalized(null, new WordMap(), new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_beagle.csv"))));
			WordRelationCrystalized rel2 = new WordRelationCrystalized(null, new WordMap(), new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_histogram.csv"))));
			WordRelationCrystalized rel3 = new WordRelationCrystalized(null, new WordMap(), new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_pixel.csv"))));
			WordRelationCrystalized rel4 = new WordRelationCrystalized(null, new WordMap(), new BufferedReader(new FileReader(new File("similarity/dissimilarity_good_recursive.csv"))));
			
			// For each category.
			for(String catVal : rosch.keySet()) {
				w.write(catVal + "\r\n");
				w.write("word,rosch,beagle,histogram,pixel,recursive\r\n");
				
				// For each pair of words.
				for(String word1 : rosch.get(catVal).keySet()) {
					
					if(rel1.wordToInt.containsKey(word1)) {
						double avgDist1 = 0;
						double avgDist2 = 0;
						double avgDist3 = 0;
						double avgDist4 = 0;
						int count = 0;
						for(String word2 : rosch.get(catVal).keySet()) {
							if(!word1.equals(word2) && rel1.wordToInt.containsKey(word2)) {
								avgDist1 += rel1.getDistance(word1, word2);
								avgDist2 += rel2.getDistance(word1, word2);
								avgDist3 += rel3.getDistance(word1, word2);
								avgDist4 += rel4.getDistance(word1, word2);
								count ++;
							}
						}
						w.write(word1 + "," + rosch.get(catVal).get(word1) + "," + (avgDist1 / count) + "," + (avgDist2 / count) + "," + (avgDist3 / count) + "," + (avgDist4 / count) + "\r\n");
					}
				}
			}
			
			w.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

package dissertation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;

import modelManager.Guess;

import tools.KBox;
import tools.WeightedObject;

import comparison.ComparisonManager;
import comparison.tree2D.Comparator2DTree;
import comparison.tree2D.Comparator2DTreeBEAGLE;
import creator2DTree.Model2DTree;

import admin.ModelData;
import admin.ModelManager;
import java.io.*;
import java.net.URL;

public class ModelSimilarity {

	public static void main(String[] args) {
		//0.06948325676123715 accuracy
		Vector<ModelData> models = ModelManager.getAllModels(new File("9-25-13_models.dat"));
		ModelManager.refineModels(models);	
		
		//System.out.println("Begin optimizing.");
		Comparator2DTree metric = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_NONE);
		//[1.8537287498304007][0.20940611861417952][0.08263143593411795][4.198149746795483][0.02064144139074943][0.494839822867015]	 0.5716688333374003
		//[1.3590466653332487][0.39068371350685194][0.10904962422695279][6.264842072909486][3.250641701069815E-4][0.4999187339581888]
		double[] weights = {1.8537287498304007,0.20940611861417952,0.08263143593411795,4.198149746795483,0.02064144139074943,0.494839822867015};
		//ComparisonManager.optimize(models, metric, weights);

		
		/*
		int correct = 0;
		int count = 0;
		for(ModelData model : models) {
			if(model.word.length() > 0) {
				String classification = classifyKNearest(model, models, 5, metric, weights);
				count ++;
				
				if(classification.equals(model.word)) {
					correct++;
					System.out.println("[" + classification + "] " + (correct / (double)count) + " " + count);
				} else {
					System.out.println("[" + classification + "] : [" + model.word + "] " + (correct / (double)count) + " " + count);
				}
			}
		}
		*/
		
		Vector<Guess> guesses = Guess.getAllGuesses(new File("9-25-13_guesses.dat"));
		Guess.linkModels(models, guesses);
		
		// Load words.
		Hashtable<String,Integer> wordData = new Hashtable<String,Integer>();
		try {
			URL url = new URL("http://www.indiana.edu/~semantic/io/getAllWords.php");
			BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;
			while((line = r.readLine()) != null) {
				String[] data = line.split(",");
				String word = data[0].trim();
				int value = Integer.parseInt(data[1]);
				
				if(word.contains(" ")) {
					System.out.println(word + " ERROR");
				}
				wordData.put(word,value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("guessAccuracy.csv")));
			w.write("answer,humanRight,computerRight,humanGuess,computerGuess\r\n");
			
			// 5, 100 0.09
			int correct = 0;
			int count = 0;
			for(Guess guess : guesses) {
				
				if(guess.model == null) {
					System.out.println("no model");
				} else {
					String classification = classifyKNearest(guess.model, models, 5, metric, weights);
					if(classification.equals(guess.model.word)) {
						correct++;
					}
					count++;
					System.out.println(classification + " " + count + " " + (correct / (double)count));
					
					int humanRight = 0;
					int computerRight = 0;
					if(guess.model.wordID == guess.guessID) {
						humanRight = 1;
					} if(guess.model.wordID == wordData.get(classification)) {
						computerRight = 1;
					}
					
					String outStr = (guess.model.wordID + "," + humanRight + "," + computerRight + "," + guess.guessID + "," + wordData.get(classification) + "\r\n");
					System.out.print(outStr);
					w.write(outStr);
					w.flush();

				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}

		
	}
	
	public static String classifyKNearest(ModelData testModel, Vector<ModelData> models, int k, Comparator2DTree comparator, double[] weights) {
		KBox<ModelData> closest = new KBox<ModelData>(k, true);
		for(ModelData model : models) {
			if(model != testModel && model.word.length() > 0) {
				double dist = comparator.similarity((Model2DTree)model.model, (Model2DTree)testModel.model, weights);
				closest.add(new WeightedObject<ModelData>(model, dist));
			}
		}
		
		Hashtable<String,Double> wordCounts = new Hashtable<String,Double>();
		for(WeightedObject<ModelData> model : closest.getObjects()) {
			Double count = wordCounts.remove(model.object.word);
			if(count == null) {
				count = 0.0;
			}
			wordCounts.put(model.object.word, count + 1 / model.weight);
		}
		
		//System.out.println("  " + testModel.word);
		String maxStr = null;
		double bestVal = 0;
		for(String str : wordCounts.keySet()) {
			if(maxStr == null || wordCounts.get(str) > bestVal) {
				maxStr = str;
				bestVal = wordCounts.get(str);
			}
			//System.out.println("    " + str + " " + wordCounts.get(str));
		}
		
		return maxStr;
	}
}

package integration;

import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import modelManager.Guess;
import comparison.tree2D.Comparator2DTreeBEAGLE;
import creator2DTree.Model2DTree;
import tools.KBox;
import tools.Stats;
import tools.VectorTools;
import tools.WeightedObject;
import admin.ModelData;
import admin.ModelManager;

public class PredictSimple {


	public static void main(String[] args) {
		//buildAll();
		
		
		try {
			ObjectInputStream ois;
			
			String[] types = {"beagleWord","flickrHistogram","googleHistogram","mcRae","spBEAGLE","spHistogram"};
			Hashtable<String,Hashtable<String,double[]>> dataSets = new Hashtable<String,Hashtable<String,double[]>>();
			
			for(String type : types) {
				ois = new ObjectInputStream(new FileInputStream(new File("objVecs/" + type + ".dat")));
				Hashtable<String,double[]> data = (Hashtable<String,double[]>)ois.readObject();
				ois.close();
				dataSets.put(type, data);
				
				for(String key : data.keySet()) {
					double[] val = data.get(key);
					double max = 0;
					double min = 0;
					for(double v : val) {
						max = Math.max(max, v);
						min = Math.min(min, v);
					}
					if(max > 10000000 || min < -10000000) {
						System.out.println(type + ":" + key + ":" + max + ":" + min);
					}
				}
			}
			System.out.println("Done loading.");

			BufferedWriter w = new BufferedWriter(new FileWriter(new File("know1Predict1.csv")));
			for(String type1 : types) {
				for(String type2 : types) {
					if(type1 != type2) {
						
						int count = 0;
						Hashtable<String,double[]> other = dataSets.get(type2);
						for(String s : dataSets.get(type1).keySet()) {
							if(other.containsKey(s)) {
								count++;
							}
						}
						
						System.out.println(type1 + " x " + type2);
						w.write(type1 + "," + type2 + "," + count + "\r\n");
						double[][] resultReal = knowOnePredictOne(dataSets.get(type1), dataSets.get(type2));
						double[][] resultRand = knowOnePredictOneRand(dataSets.get(type1), dataSets.get(type2));

						for(int i=0;i<100;i++) {
							w.write((i+1) + ",");
						}
						w.write("\r\n");
						
						
						for(int i=0;i<resultReal[0].length;i++) {
							double sd = Math.sqrt(resultReal[1][i] - resultReal[0][i] * resultReal[0][i]);
							w.write((resultReal[0][i] - sd) + ",");
						}
						w.write("\r\n");
						for(int i=0;i<resultReal[0].length;i++) {
							double sd = Math.sqrt(resultReal[1][i] - resultReal[0][i] * resultReal[0][i]);
							w.write((resultReal[0][i]) + ",");
						}
						w.write("\r\n");
						for(int i=0;i<resultReal[0].length;i++) {
							double sd = Math.sqrt(resultReal[1][i] - resultReal[0][i] * resultReal[0][i]);
							w.write((resultReal[0][i] + sd) + ",");
						}
						w.write("\r\n");
						for(int i=0;i<resultRand[0].length;i++) {
							double sd = Math.sqrt(resultRand[1][i] - resultRand[0][i] * resultRand[0][i]);
							w.write((resultRand[0][i] - sd) + ",");
						}
						w.write("\r\n");
						for(int i=0;i<resultRand[0].length;i++) {
							double sd = Math.sqrt(resultRand[1][i] - resultRand[0][i] * resultRand[0][i]);
							w.write((resultRand[0][i]) + ",");
						}
						w.write("\r\n");
						for(int i=0;i<resultRand[0].length;i++) {
							double sd = Math.sqrt(resultRand[1][i] - resultRand[0][i] * resultRand[0][i]);
							w.write((resultRand[0][i] + sd) + ",");
						}
						w.write("\r\n");
						w.flush();
					}
				}
			}
			w.close();
			
			/*
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("know1Predict1_rnd_sqr.csv")));
			for(String type1 : types) {
				for(String type2 : types) {
					if(type1 != type2) {
						
						int count = 0;
						Hashtable<String,double[]> other = dataSets.get(type2);
						for(String s : dataSets.get(type1).keySet()) {
							if(other.containsKey(s)) {
								count++;
							}
						}
						
						System.out.println(type1 + " x " + type2);
						w.write(type1 + "," + type2 + "," + count + "\r\n");
						double[] result = knowOnePredictOneRand(dataSets.get(type1), dataSets.get(type2));
						for(double resultVal : result) {
							w.write(resultVal + ",");
						}
						w.write("\r\n");
					}
				}
			}
			w.close();
			*/
			
			//w.close();
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public static double[][] knowOnePredictOne(Hashtable<String,double[]> know, Hashtable<String,double[]> predict) {

		double[] overall = new double[100];
		double[] overallSqr = new double[100];
		
		HashSet<String> overlap = new HashSet<String>();
		for(String s : know.keySet()) {
			if(predict.containsKey(s)) {
				overlap.add(s);
			}
		}
		
		for(String word : overlap) {
			double[] knownTarget = know.get(word);

			Vector<WeightedObject<String>> sims = new Vector<WeightedObject<String>>();
			for(String helpWord : overlap) {
				if(!helpWord.equals(word)) {
					double[] knownHelp = know.get(helpWord);
					double dist = VectorTools.getCosine(knownTarget, knownHelp);
					
					if(Double.isNaN(dist)) {
						System.out.println(helpWord);
						VectorTools.show(knownTarget);
						VectorTools.show(knownHelp);
					}
					
					sims.add(new WeightedObject<String>(helpWord, dist));
				}
			}
			
			WeightedObject[] sorted = sims.toArray(new WeightedObject[0]);
			Arrays.sort(sorted);

			double[] toPredict = predict.get(word);
			
			double[] guess = VectorTools.mult(predict.get(sorted[0].object), sorted[0].weight);
			for(int n=0;n<overall.length;n++) {
				
				double offBy = VectorTools.getCosine(guess, toPredict);
				overall[n] += offBy;
				overallSqr[n] += offBy * offBy;
				
				VectorTools.setAdd(VectorTools.mult(predict.get(sorted[n + 1].object), sorted[n + 1].weight), guess);
			}
		}

		double[][] ret = {VectorTools.mult(overall, 1.0 / overlap.size()), VectorTools.mult(overallSqr, 1.0 / overlap.size())};
		return ret;
	}
	
	public static double[][] knowOnePredictOneRand(Hashtable<String,double[]> know, Hashtable<String,double[]> predict) {

		double[] overall = new double[100];
		double[] overallSqr = new double[100];
		HashSet<String> overlap = new HashSet<String>();
		for(String s : know.keySet()) {
			if(predict.containsKey(s)) {
				overlap.add(s);
			}
		}
		
		Random rand = new Random();
		
		for(String word : overlap) {
			double[] knownTarget = know.get(word);

			Vector<WeightedObject<String>> sims = new Vector<WeightedObject<String>>();
			for(String helpWord : overlap) {
				if(!helpWord.equals(word)) {
					double[] knownHelp = know.get(helpWord);
					double dist = VectorTools.getCosine(knownTarget, knownHelp);
					sims.add(new WeightedObject<String>(helpWord, dist));
				}
			}
			
			WeightedObject[] unsorted = sims.toArray(new WeightedObject[0]);
			for(int i=0;i<unsorted.length;i++) {
				int j = rand.nextInt(unsorted.length - i) + i;
				WeightedObject tmp = unsorted[i];
				unsorted[i] = unsorted[j];
				unsorted[j] = tmp;
			}

			double[] toPredict = predict.get(word);
			
			double[] guess = VectorTools.mult(predict.get(unsorted[0].object),1);
			for(int n=0;n<overall.length;n++) {
				
				double offBy = VectorTools.getCosine(guess, toPredict);
				overall[n] += offBy;
				overallSqr[n] += offBy * offBy;
				
				VectorTools.setAdd(VectorTools.mult(predict.get(unsorted[n + 1].object),1), guess);
			}

		}

		double[][] ret = {VectorTools.mult(overall, 1.0 / overlap.size()), VectorTools.mult(overallSqr, 1.0 / overlap.size())};
		return ret;
		
	}
	
	public static double[] predictDomain(Hashtable<String,double[]>[] knowledgeDomains, String target, int domainOfInterest) {
		
		KBox<String> topN = new KBox<String>(10, true);
		
		for(String word : knowledgeDomains[domainOfInterest].keySet()) {
			
			double sum = 0;
			int count = 0;
			
			for(int i=0;i<knowledgeDomains.length;i++) {
				if(i != domainOfInterest) {
					double[] targetValue = knowledgeDomains[i].get(target);
					double[] testValue = knowledgeDomains[i].get(word);
					
					if(targetValue != null && testValue != null) {
						sum += VectorTools.getArcCosine(targetValue, testValue);
						count ++;
					}
				}
			}
			
			if(count > 0) {
				double avgScore = sum / count;
				topN.add(new WeightedObject<String>(word, avgScore));
			}
		}
		
		double[] sum = null;
		System.out.println();
		System.out.println(target + ":");
		for(WeightedObject<String> obj : topN.getObjects()) {

			System.out.println(obj.object + " " + obj.weight);
			
			double[] toAdd = VectorTools.mult(knowledgeDomains[domainOfInterest].get(obj.object), obj.weight);
			if(sum == null) {
				sum = toAdd;
			} else {
				VectorTools.setAdd(toAdd, sum);
			}
		}
		
		return sum;
	}
	
	public static void buildAll() {
		
		Vector<Guess> guesses = Guess.getAllGuesses(new File("8-21-2014_guesses.dat"));
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		ModelManager.refineModels(models);	
		Guess.linkModels(models, guesses);

		Set<String> words = null;
		
		for(int i=0;i<models.size();i++) {
			if(models.get(i).correctGuesses < 1) {
				models.remove(i);
				i--;
			}
		}
		
		try {
			System.out.println("McRae Features.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/mcRae.dat")));
			Hashtable<String,double[]> mcRae = buildMCRaeFeatures(new File("McRaeFeatures.csv"));
			words = mcRae.keySet();
			oos.writeObject(mcRae);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("BEAGLE Word Similarities.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/beagleWord.dat")));
			oos.writeObject(buildBEAGLEWordSimilarity(new File("matrix.txt"), new File("word_labels.txt"), words));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Semantic Pictionary Histogram.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/spHistogram.dat")));
			oos.writeObject(buildSPHistograms(models));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Google Image Histogram.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/googleHistogram.dat")));
			oos.writeObject(buildImageHistograms(new File("google")));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Flickr Image Histogram.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/flickrHistogram.dat")));
			oos.writeObject(buildImageHistograms(new File("flickr")));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Taste.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/taste.dat")));
			oos.writeObject(loadConceptNodes(new File("senses/foodData.csv")));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println("Smell.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/smell.dat")));
			oos.writeObject(loadConceptNodes(new File("senses/smellVectors.csv")));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			System.out.println("Semantic Pictionary BEAGLE.");
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("objVecs/spBEAGLE.dat")));
			oos.writeObject(loadSPBEAGLE(models));
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
	}


	public static double[] buildBEAGLEWordSimilarity(File matrix, File labels, String word) {
		try {
			BufferedReader lFile = new BufferedReader(new FileReader(labels));
			String line;
			int i = 0;
			while((line = lFile.readLine()) != null) {
				if(line.trim().equals(word)) {
					
					RandomAccessFile mFile = new RandomAccessFile(matrix, "r");
					
					mFile.seek(i * 14002);
					String[] conceptStr = mFile.readLine().trim().split(" +");
					double[] concept = new double[conceptStr.length];
					for(int j=0;j<concept.length;j++) {
						concept[j] = Double.parseDouble(conceptStr[j]);
					}
					
					mFile.close();
					lFile.close();
					return concept;
				}
				i++;
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Hashtable<String,double[]> buildBEAGLEWordSimilarity(File matrix, File labels, Set<String> words) {
		try {
			Hashtable<String,double[]> ret = new Hashtable<String,double[]>();
			
			BufferedReader mFile = new BufferedReader(new FileReader(matrix));
			BufferedReader lFile = new BufferedReader(new FileReader(labels));
			String mLine;

			lFile.readLine();
			
			while((mLine = mFile.readLine()) != null) {
				String lLine = lFile.readLine().trim();
				
				if(words == null || words.contains(lLine)) {
				
					String[] conceptStr = mLine.trim().split(" +");
					double[] concept = new double[conceptStr.length];
					for(int i=0;i<concept.length;i++) {
						concept[i] = Double.parseDouble(conceptStr[i]);
					}
					
					ret.put(lLine, concept);
				}
			}
			
			mFile.close();
			lFile.close();
			
			return ret;
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Hashtable<String,double[]> buildMCRaeFeatures(File file) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			String line;
			
			Hashtable<String,double[]> ret = new Hashtable<String,double[]>();

			Hashtable<String, Integer> features = new Hashtable<String, Integer>();
			while((line = r.readLine()) != null) {
				String feature = line.split(",")[1];
				int id = features.size();
				if(!features.containsKey(feature)) {
					features.put(feature, id);
				}
			}
			r.close();

			r = new BufferedReader(new FileReader(file));
			
			while((line = r.readLine()) != null) {
				String[] parts = line.split(",");
				String word = parts[0];
				String feature = parts[1];
				int frequency = Integer.parseInt(parts[2]);

				double[] concept = ret.remove(word);
				
				if(concept == null) {
					concept = new double[features.size()];
				}
				concept[features.get(feature)] = frequency;
				
				ret.put(word, concept);
			}
			r.close();

			return ret;
			
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static Hashtable<String,double[]> buildSPHistograms(Vector<ModelData> models) {
		
		Hashtable<String,double[]> results = new Hashtable<String,double[]>();
				
		for(ModelData model : models) {
			String label = model.word;
			double[] concept = results.remove(label);
			
			if(concept == null) {
				concept = HistogramIntegration.toVector(Stats.histogram(model, 5));
			}  else {
				VectorTools.setAdd(HistogramIntegration.toVector(Stats.histogram(model, 5)), concept);
			}
			
			results.put(label, concept);
		}
		
		return results;
	}
	
	public static Hashtable<String,double[]> buildImageHistograms(File rootDir) {
		Set<String> words = new HashSet<String>();
		for(File f : rootDir.listFiles()) {
			words.add(f.getName());
		}
		return buildImageHistograms(words, rootDir);
	}
	
	public static Hashtable<String,double[]> buildImageHistograms(Set<String> words, File rootDir) {
		
		Hashtable<String,double[]> results = new Hashtable<String,double[]>();

		for(String word : words) {
			
			File dir = new File(rootDir + "/" + word);
			File[] files = dir.listFiles();
			
			double[] concept = null;
			
			for(File img : files) {
				try {
					
					if(concept == null) {
						concept = HistogramIntegration.toVector(Stats.histogram(ImageIO.read(img), 5, null));
					}  else {
						VectorTools.setAdd(HistogramIntegration.toVector(Stats.histogram(ImageIO.read(img), 5, null)), concept);
					}
					
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			
			results.put(word, concept);
		}
		
		return results;
	}
	
	public static Hashtable<String,double[]> loadConceptNodes(File file) {
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			r.readLine();
			r.readLine();
			String line;			
						
			line = r.readLine();
			int count = 0;
			for(int i=0;i<line.length();i++) {
				if(line.charAt(i) == ',') {
					count++;
				}
			}
			
			Hashtable<String, double[]> vectors = new Hashtable<String,double[]>();
			do {
				String[] parts = line.split(",");
				String word = parts[0].toLowerCase();
				word = word.replaceAll("\"", "");
				
				double[] vector = new double[count];
				for(int i=1;i<parts.length;i++) {
					if(parts[i].length() == 0) {
						vector[i-1] = 0;
					} else {
						vector[i-1] = Double.parseDouble(parts[i]);
					}
				}
								
				double[] oldVec = vectors.remove(word);
				if(oldVec == null) {
					oldVec = VectorTools.normalize(vector);
				} else {
					VectorTools.setAdd(VectorTools.normalize(vector), oldVec);
				}
				
				vectors.put(word, oldVec);
			} while((line = r.readLine()) != null);
			
			return vectors;
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Hashtable<String,double[]> loadSPBEAGLE(Vector<ModelData> models) {
		double[] weights = {1.9666618683917867,0.09277887855848543,0.24119939936888588,3.8057107938604644,0.0936797352763701,0.47659717873232377};
		Comparator2DTreeBEAGLE metric = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_ROTATE);

		Hashtable<String,double[]> results = new Hashtable<String,double[]>();
		
		for(ModelData model : models) {
			String label = model.word;
			double[] concept = results.remove(label);
			
			double[] encoded = metric.encodeModel((Model2DTree)model.model,weights);
						
			if(concept == null) {
				concept = encoded;
			} else {
				VectorTools.setAdd(encoded, concept);
			}
			
			results.put(label, concept);
		}
		
		return results;
	}
	
}

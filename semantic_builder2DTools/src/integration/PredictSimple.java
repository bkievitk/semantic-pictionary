package integration;

import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

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
				
		try {
			ObjectInputStream ois;
			String[] newWords = {"law","love","dog"};
			
			ois = new ObjectInputStream(new FileInputStream(new File("objVecs/beagleWord.dat")));
			Hashtable<String,double[]> beagleWord = (Hashtable<String,double[]>)ois.readObject();
			ois.close();

			for(String newWord : newWords) {
				beagleWord.put(newWord, buildBEAGLEWordSimilarity(new File("matrix.txt"), new File("word_labels.txt"), newWord));
			}
			
			ois = new ObjectInputStream(new FileInputStream(new File("objVecs/mcRae.dat")));
			Hashtable<String,double[]> mcRae = (Hashtable<String,double[]>)ois.readObject();
			ois.close();

			ois = new ObjectInputStream(new FileInputStream(new File("objVecs/spHistogram.dat")));
			Hashtable<String,double[]> spHistogram = (Hashtable<String,double[]>)ois.readObject();
			ois.close();
			
			Hashtable[] knowledgeDomains = {beagleWord, mcRae, spHistogram};

			// Extract feature numbers.
			BufferedReader r = new BufferedReader(new FileReader(new File("McRaeFeatures.csv")));
			String line;
			
			Vector<String> featureList = new Vector<String>();
			HashSet<String> featureSet = new HashSet<String>();
			while((line = r.readLine()) != null) {
				String feature = line.split(",")[1];
				if(!featureSet.contains(feature)) {
					featureSet.add(feature);
					featureList.add(feature);
				}
			}
			r.close();
			
			for(String newWord : newWords) {
				double[] ret = predictDomain(knowledgeDomains, newWord, 2);				
				int[][][] histogram = HistogramIntegration.toHistogram(ret, 1.0);
				Stats.show3DHistogram(histogram, newWord);
				
				System.out.println(newWord + ":");
				
				ret = predictDomain(knowledgeDomains, newWord, 1);
				for(int i=0;i<ret.length;i++) {
					if(ret[i] > 5) {
						System.out.println("  " + featureList.get(i) + " " + ret[i]);
					}
				}
				System.out.println();
			}
			
			
			
			
				
			
		} catch(IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		
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
		
		Vector<ModelData> models = ModelManager.getAllModels(new File("4-9-2012.dat"));
		ModelManager.refineModels(models);
		Set<String> words = null;
		
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
		double[] weights = {1.54582184401584794,-1.46883378664609434,-1.95336694189284813,2.4129212585283405,-3.0464443467093407,.000000007};
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

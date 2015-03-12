package integration;

import flanagan.math.Maximisation;
import flanagan.math.MaximisationFunction;
import gui.SentenceCleaner;
import gui.WordMap;

import java.awt.Color;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import comparison.tree2D.Comparator2DTreeBEAGLE;
import creator2DTree.Model2DTree;

import relations.WordRelator;
import relations.beagle.Thought;
import relations.beagle.WordRelatorBEAGLE;
import relations.helpers.WordRelationCrystalized;
import tools.KBox;
import tools.Stats;
import tools.VectorTools;
import tools.WeightedObject;
import tools.TASA;
import tools.TASABook;

import admin.HumanLabeler;
import admin.ModelData;
import admin.ModelManager;

public class HistogramIntegration {

	public static final Random rand = new Random();

	public static final int HISTOGRAM_ID = 0;
	public static final int BEAGLE_ID = 0;
	public static final int SEMANTIC_ID = 1;
	
	public static final int PREDICT_PICTIONARY_HISTOGRAM = 0;
	public static final int PREDICT_FLICKER_HISTOGRAM = 1;
	public static final int PREDICT_GOOGLE_HISTOGRAM = 2;
	public static final int PREDICT_TASTE = 3;
	public static final int PREDICT_SMELL = 4;
	public static final int PREDICT_PICTIONARY_BEAGLE = 5;
	
	public static final int INDICATOR_BEAGLE = 20;
	public static final int INDICATOR_MCRAE = 21;
	public static final int INDICATOR_MCRAE_BEAGLE = 22;
	
	public static void main(String[] args) {
		//runPredict(PREDICT_PICTIONARY_HISTOGRAM, INDICATOR_MCRAE_BEAGLE);
		runNewPredict();
	}
	
	public static void runNewPredict() {
		
		Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
		ModelManager.refineModels(models);
		Hashtable<String,ConceptNode> concepts = buildImageConceptNodes(models, null, HISTOGRAM_ID, 2);
		String[] conceptStrings = concepts.keySet().toArray(new String[0]);
		
		PredictHistogram predictor1 = new PredictHistogram() {
			@Override
			public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator[] relators, String wordSelected, String[] conceptStrings) {
				KBox<String> topMatches = topMatches(concepts, relators, wordSelected, 20);
				
				double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
				for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
					double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
					VectorTools.setAdd(normalized, expected);
				}							
				return expected;
			}
		};

		WordMap wordMap = new WordMap();
		WordRelatorBEAGLE relator = new WordRelatorBEAGLE(null, null, 1000, wordMap);
		relator.beagle.options.learnContext = true;
		//relator.beagle.options.autoAddWords = true;
		
		Vector<SentenceCleaner> allCleaners = SentenceCleaner.getCleaners();
		relator.cleaners.add(allCleaners.get(0));
		relator.cleaners.add(allCleaners.get(1));
		relator.cleaners.add(allCleaners.get(3));
		relator.cleaners.add(allCleaners.get(4));
		
		WordRelatorBEAGLE[] relators = {relator};
		
		long wordCount = 0;
		long calcStep = 1000000;
		int steps = 0;
		String line;

		try {
			BufferedReader r = new BufferedReader(new FileReader(new File("E:/workspace/BEAGLEBot/data/elp.frequencies.v2.txt")));
			while((line = r.readLine()) != null) {
				line = line.substring(0,line.indexOf('\t')).toLowerCase();
				Thought t = new Thought(line, relator.beagle.options.dimensions);
				relator.beagle.thoughts.put(line, t);
				
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
		// Connect to TASA.
		TASA tasa = new TASA(new File("../BEAGLEBot/data/tasaDocs.txt"));
		TASABook book;
		
		// Read until no more books.
		while((book = tasa.readBook()) != null) {
						
			// For each sentence.
			for(String sentence : book.sentences) {
					
				wordCount += relator.learn(sentence);
								
				if(calcStep * steps < wordCount) {
					steps++;
					double val = avgDot(concepts, relators, conceptStrings, predictor1);
					System.out.println(wordCount + "," + val);
				}
			}

			//System.out.println(relator.getDistance("dog", "boy"));
		}
		
		
		
		/*
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("result.csv")));
			BufferedReader wikiReader = new BufferedReader(new FileReader(new File("E:/ccl Lab 5-3-2012/data sets/wikipedia/enwiki-20091103-pages-meta-current.xml")));
			int state = 0;
			
			while((line = wikiReader.readLine()) != null) {
				
				if(line.contains("<text xml")) {
					state = 1;
				} 
				
				if(line.contains("</text>")) {
					state = 2;
				}
				
				if(state == 1) {
					wordCount += relator.learn(line);
					
					//for(int i=0;i<parsed.length;i++) {
					//	System.out.print("[" + parsed[i] + "]");
					//}
					//System.out.println();
					
					if(calcStep * steps < wordCount) {
						steps++;
						double val = avgDot(concepts, relators, conceptStrings, predictor1);
						System.out.println(wordCount + "," + val);
						w.write(wordCount + "," + val + "\n");
						w.flush();
												
					}
				}
			}	
			
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		*/
				
	}
	
	public static void runPredict(int predictType, int indicatorType) {
		try {
			
			// Load all models.
			Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
			ModelManager.refineModels(models);
			HumanLabeler.keepModelsGeonRange(models, 5, 50);
			
			// Build histograms.
			System.out.println("Building concept nodes.");

			Hashtable<String,ConceptNode> concepts = null;
			Set<String> words;
			String predictLabel = null;
			
			switch(predictType) {
				case PREDICT_PICTIONARY_HISTOGRAM:
					concepts = buildImageConceptNodes(models, null, HISTOGRAM_ID, 2);
					predictLabel = "PictionaryHistogram";
					break;
				case PREDICT_FLICKER_HISTOGRAM:
					words = ModelManager.getAllWordModels(models).keySet();
					concepts = loadImageConceptNodes(words, null, HISTOGRAM_ID, 2, new File("flickr"));
					predictLabel = "FlickrHistogram";
					break;
				case PREDICT_GOOGLE_HISTOGRAM:
					words = ModelManager.getAllWordModels(models).keySet();
					concepts = loadImageConceptNodes(words, null, HISTOGRAM_ID, 2, new File("google"));
					predictLabel = "GoogleHistogram";
					break;
				case PREDICT_TASTE:
					concepts = setConceptNodes(loadFoodConceptNodes(), null, 0, 2);
					predictLabel = "TasteHistogram";
					break;
				case PREDICT_SMELL:
					concepts = setConceptNodes(loadSmellConceptNodes(), null, 0, 2);
					predictLabel = "SmellHistogram";
					break;
				case PREDICT_PICTIONARY_BEAGLE:
					double[] weights = {1.54582184401584794,-1.46883378664609434,-1.95336694189284813,2.4129212585283405,-3.0464443467093407,.000000007};
					Comparator2DTreeBEAGLE metric = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_ROTATE);
					concepts = buildBEAGLEVecConceptNodes(models, null, HISTOGRAM_ID, 2, metric, weights);
					predictLabel = "PictionaryBEAGLE";
					break;
				default:
					return;
			}
						
			// Get similarities.
			System.out.println("Loading similarity values.");
			WordMap wordMap = new WordMap();

			WordRelationCrystalized relator = null;
			WordRelationCrystalized relator2 = null;
			String indicatorLabel = null;
			
			switch(indicatorType) {
				case INDICATOR_BEAGLE:
					relator = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("similarity/semanticPictionaryBEAGLE.csv"))));
					indicatorLabel = "BEAGLE";
					break;
				case INDICATOR_MCRAE:
					relator = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("similarity/McRaeNormsCosine.csv"))));
					indicatorLabel = "MCRae";
					break;
				case INDICATOR_MCRAE_BEAGLE:
					relator = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("similarity/semanticPictionaryBEAGLE.csv"))));
					relator2 = new WordRelationCrystalized(Color.BLACK, wordMap, new BufferedReader(new FileReader(new File("similarity/McRaeNormsCosine.csv"))));
					indicatorLabel = "MCRaeANDBEAGLE";
					break;
				default:
					return;
			}
						
			HashSet<String> inWords = new HashSet<String>();
			inWords.addAll(relator.getWords());
			for(String word : concepts.keySet().toArray(new String[0])) {
				if(!inWords.contains(word)) {
					concepts.remove(word);
				}
			}
			
			String[] conceptStrings = concepts.keySet().toArray(new String[0]);
				
			/*
			double[][] vectors = Inverter.buildVectors(relator.weights, 200);
			double avgErr = Inverter.getAvgError(relator.weights, Inverter.getSimilarity(vectors));
			
			System.out.println("Error: " + avgErr);
			
			
			for(String concept : concepts.keySet()) {
				Integer conceptInt = relator.wordToInt.get(concept);
				
				if(conceptInt != null) {
					double[] vector = vectors[conceptInt];
					concepts.get(concept).knowledge[SEMANTIC_ID] = vector;
				}
			}
			*/
						
			final int[] count = new int[1];
			final int[] power = new int[1];
			
			String written = indicatorLabel + "_X_" + predictLabel + ".csv";
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("C:/Users/iami/Desktop/" + written)));
					
			w.write("Equal\n");
			for(int i=1;i<50;i++) {
				System.out.println(i);
				count[0] = i;
				PredictHistogram predictor1 = new PredictHistogram() {
					@Override
					public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator[] relators, String wordSelected, String[] conceptStrings) {
						KBox<String> topMatches = topMatches(concepts, relators, wordSelected, count[0]);
						
						double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
						for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
							double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
							VectorTools.setAdd(normalized, expected);
						}							
						return expected;
					}
				};			
				WordRelator[] relators = {relator, relator2};
				w.write(count[0] + ", " + avgDot(concepts, relators, conceptStrings, predictor1) + "\n");		
				w.flush();
			}
			
			
			
			
			
			/*
			w.write("Semi-Limit\n");						
			for(int i=1;i<50;i++) {
				System.out.println(i);
				count[0] = i;
				
				final double[] weights = findBestDistributions(concepts, relator, conceptStrings, 1);
				
				PredictHistogram predictor1 = new PredictHistogram() {
					@Override
					public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {
						KBox<String> topMatches = topMatches(concepts, relator, wordSelected, count[0]);
						double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
						
						int j = 0;
						for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
							double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
							
							double weight = weights[j];
							VectorTools.setAdd(VectorTools.mult(normalized,weight), expected);
							j++;
						}							
						return expected;
					}
				};			
				w.write(count[0] + ", " + avgDot(concepts, relator, conceptStrings, predictor1) + "\n");		
				w.flush();
			}
								
			w.write("Equal\n");
			for(int i=1;i<50;i++) {
				System.out.println(i);
				count[0] = i;
				PredictHistogram predictor1 = new PredictHistogram() {
					@Override
					public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {
						KBox<String> topMatches = topMatches(concepts, relator, wordSelected, count[0]);
						double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
						for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
							double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
							VectorTools.setAdd(normalized, expected);
						}							
						return expected;
					}
				};			
				w.write(count[0] + ", " + avgDot(concepts, relator, conceptStrings, predictor1) + "\n");		
				w.flush();
			}
			

			for(int p=1;p<5;p++) {
				power[0] = p;

				System.out.println(p);
				w.write("1/x^" + (p+1) + "\n");
				
				for(int i=1;i<50;i++) {
					count[0] = i;
					PredictHistogram predictor2 = new PredictHistogram() {
						@Override
						public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {
							KBox<String> topMatches = topMatches(concepts, relator, wordSelected, count[0]);
							double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
							for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
								double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
								VectorTools.setAdd(VectorTools.mult(normalized, Math.pow(semanticMatch.weight,power[0])), expected);
							}							
							return expected;
						}
					};			
					w.write(count[0] + ", " + avgDot(concepts, relator, conceptStrings, predictor2) + "\n");		
					w.flush();
				}
			}
			
			w.write("Random\n");
			for(int i=1;i<50;i++) {
				System.out.println("Rand: " + i);
				count[0] = i;
				PredictHistogram predictorRandom = new PredictHistogram() {
					@Override
					public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {
						KBox<String> topMatches = randomMatches(concepts, relator, wordSelected, count[0], conceptStrings);
						double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
						for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
							double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
							VectorTools.setAdd(normalized, expected);
						}							
						return expected;
					}
				};			
				w.write(count[0] + ", " + avgDot(concepts, relator, conceptStrings, predictorRandom) + "\n");		
				w.flush();
			}

			w.write("Best\n");
			w.flush();
			
			for(int i=1;i<50;i++) {

				System.out.println(i + "BEST");
				
				count[0] = i;
				PredictHistogram predictorBest = new PredictHistogram() {
					@Override
					public double[] predictHistogram(Hashtable<String, ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {
						
						final Hashtable<String, ConceptNode> conceptsF = concepts;
						final String wordSelectedF = wordSelected;
						final KBox<String> topMatches = topMatches(concepts, relator, wordSelected, count[0]);
						final double[] normalized = VectorTools.normalize(concepts.get(wordSelected).knowledge[HISTOGRAM_ID]);
						
						Maximisation max = new Maximisation();
						MaximisationFunction funct = new MaximisationFunction() {
							public double function(double[] param) {
								
								double[] expected = new double[conceptsF.get(wordSelectedF).knowledge[HISTOGRAM_ID].length];
								int i = 0;
								for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
									double[] normalized = VectorTools.normalize(conceptsF.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
									VectorTools.setAdd(VectorTools.mult(normalized,param[i]), expected);
									i++;
								}
								
								expected = VectorTools.normalize(expected);
								return VectorTools.dot(expected, normalized);
							}
						};
						
						// initial estimates
						double[] start = new double[count[0]];
	
				        // initial step sizes
				        double[] step = new double[count[0]];
				        for(int i=0;i<step.length;i++) {
				        	step[i] = .1;
				        }
				        
				        // convergence tolerance
				        double ftol = 1e-15;
				        
				        // Nelder and Mead maximisation procedure
				        max.nelderMead(funct, start, step, ftol, 10000);	
				        
						double[] param = max.getParamValues();
						double[] expected = new double[conceptsF.get(wordSelectedF).knowledge[HISTOGRAM_ID].length];
						int i = 0;
						for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
							double[] normalized2 = VectorTools.normalize(conceptsF.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
							VectorTools.setAdd(VectorTools.mult(normalized2,param[i]), expected);
							i++;
						}
		
						//expected = VectorTools.normalize(expected);
						//double val = VectorTools.dot(expected, normalized);
												
						return expected;
					}
				};
				w.write(count[0] + ", " + avgDot(concepts, relator, conceptStrings, predictorBest) + "\n");				
				w.flush();
			}	
			*/
									
			w.close();
			System.out.println("Finished writing " + written);
						
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static double[] findBestDistributions(final Hashtable<String, ConceptNode> concepts, WordRelator relator, String[] conceptStrings, int i) {
		double[] distribution = new double[i];
		
		for(String word : conceptStrings) {
			
			final KBox<String> topMatches = topMatches(concepts, relator, word, i);
			final double[] normalized = VectorTools.normalize(concepts.get(word).knowledge[HISTOGRAM_ID]);
			final String wordF = word;
			
			Maximisation max = new Maximisation();
			MaximisationFunction funct = new MaximisationFunction() {
				public double function(double[] param) {
					
					double[] expected = new double[concepts.get(wordF).knowledge[HISTOGRAM_ID].length];
					int j = 0;
					for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
						double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
						VectorTools.setAdd(VectorTools.mult(normalized,param[j]), expected);
						j++;
					}
					
					expected = VectorTools.normalize(expected);
					return VectorTools.dot(expected, normalized);
				}
			};
			
			// initial estimates
			double[] start = new double[i];

	        // initial step sizes
	        double[] step = new double[i];
	        for(int j=0;j<step.length;j++) {
	        	step[j] = .1;
	        }
	        
	        // convergence tolerance
	        double ftol = 1e-15;
	        
	        // Nelder and Mead maximisation procedure
	        max.suppressNoConvergenceMessage();
	        max.nelderMead(funct, start, step, ftol, 10000);	
	        
			double[] param = max.getParamValues();
			VectorTools.setAdd(VectorTools.normalize(param), distribution);
		}
		
		return VectorTools.normalize(distribution);
	}
	
	interface PredictHistogram {
		public abstract double[] predictHistogram(Hashtable<String,ConceptNode> concepts, WordRelator[] relators, String wordSelected, String[] conceptStrings);
	}
	
	public static double avgDot(Hashtable<String,ConceptNode> concepts, WordRelator relator[], String[] conceptStrings, PredictHistogram predictor) {
		double sum = 0;
		for(String wordSelected : conceptStrings) {
			double[] expected = VectorTools.normalize(predictor.predictHistogram(concepts, relator, wordSelected, conceptStrings));
			double[] normalized = VectorTools.normalize(concepts.get(wordSelected).knowledge[HISTOGRAM_ID]);
			double dist = VectorTools.dot(expected, normalized);
			sum += dist;
		}
		return sum / conceptStrings.length;
	}
	
	public static Object selectRandomKey(Hashtable<?,?> hash) {
		Set<?> keys = hash.keySet();
		int selectedID = rand.nextInt(hash.size());		
		Iterator<?> keyItterator = keys.iterator();
		Object keySelected = keyItterator.next();
		for(int i=0;i<selectedID;i++) {
			keySelected = keyItterator.next();
		}
		return keySelected;
	}

	/*
	public static double[] predictRandomHistogram(Hashtable<String,ConceptNode> concepts, WordRelator relator, String wordSelected, String[] conceptStrings) {

		int HISTOGRAM_ID = 0;
		
		KBox<String> topMatches = new KBox<String>(10, true);
		for(int i=0;i<10;i++) {
			String selected = conceptStrings[rand.nextInt(conceptStrings.length)];
			while(selected.equals(wordSelected)) {
				selected = conceptStrings[rand.nextInt(conceptStrings.length)];
			}
			topMatches.add(new WeightedObject<String>(selected,0));
		}
		
		double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
		for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
			double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
			VectorTools.setAdd(normalized, expected);
		}
		return expected;
	}
	
	public static double[] predictHistogram(Hashtable<String,ConceptNode> concepts, WordRelator relator, String wordSelected) {

		int HISTOGRAM_ID = 0;
		
		// Now find the top n similar words.
		KBox<String> topMatches = new KBox<String>(10, true);
		int count = 0;
		for(String word : concepts.keySet()) {
			if(!word.equals(wordSelected)) {
				double distance = relator.getDistance(word, wordSelected);
				if(distance < 0 || distance > 1) {
					count ++;
				} else {
					topMatches.add(new WeightedObject<String>(word, distance));
				}
			}
		}
		
		//System.out.println(count + "/" + concepts.size() + " failures.");
		
		double[] expected = new double[concepts.get(wordSelected).knowledge[HISTOGRAM_ID].length];
		for(WeightedObject<String> semanticMatch : topMatches.getObjects()) {
			double[] normalized = VectorTools.normalize(concepts.get(semanticMatch.object).knowledge[HISTOGRAM_ID]);
			VectorTools.setAdd(normalized, expected);
		}
				
		return expected;
	}
	*/
	
	public static KBox<String> topMatches(Hashtable<String,ConceptNode> concepts, WordRelator[] relators, String wordSelected, int count) {
		KBox<String> topMatches = new KBox<String>(count, true);
		for(String word : concepts.keySet()) {
			if(!word.equals(wordSelected)) {
				
				double distance = 0;
				for(WordRelator relator : relators) {
					distance += relator.getDistance(word, wordSelected);
				}
				distance /= relators.length;
								
				/*
				double distance = 0;
				for(WordRelator relator : relators) {
					distance = Math.max(distance, relator.getDistance(word, wordSelected));
				}
				*/
				
				topMatches.add(new WeightedObject<String>(word, distance));
				
			}
		}
		return topMatches;
	}
	
	public static KBox<String> topMatches(Hashtable<String,ConceptNode> concepts, WordRelator relator, String wordSelected, int count) {
		KBox<String> topMatches = new KBox<String>(count, true);
		for(String word : concepts.keySet()) {
			if(!word.equals(wordSelected)) {
				double distance = relator.getDistance(word, wordSelected);
				if(distance > 0 && distance < 1) {
					topMatches.add(new WeightedObject<String>(word, distance));
				}
			}
		}
		return topMatches;
	}
	
	public static KBox<String> randomMatches(Hashtable<String,ConceptNode> concepts, WordRelator relator, String wordSelected, int count, String[] conceptStrings) {
		KBox<String> randomMatches = new KBox<String>(10, true);
		for(int i=0;i<10;i++) {
			String selected = conceptStrings[rand.nextInt(conceptStrings.length)];
			while(selected.equals(wordSelected)) {
				selected = conceptStrings[rand.nextInt(conceptStrings.length)];
			}
			randomMatches.add(new WeightedObject<String>(selected,0));
		}
		return randomMatches;
	}
	
	public static double[] toVector(int[][][] histogram) {
		double[] vec = new double[histogram.length * histogram[0].length * histogram[0][0].length];
		int i=0;
		for(int r=0;r<histogram.length;r++) {
			for(int g=0;g<histogram[r].length;g++) {
				for(int b=0;b<histogram[r][g].length;b++) {
					vec[i] = histogram[r][g][b];
					i++;
				}	
			}	
		}
		return vec;
	}
	
	public static int thirdRootInt(int val) {
		int i;
		for(i=0; i*i*i < val;i++);
		return i;
	}		
		
	public static int[][][] toHistogram(double[] vec, double mult) {
		
		int len = thirdRootInt(vec.length);
		
		int[][][] histogram = new int[len][len][len];
		int i=0;
		for(int r=0;r<histogram.length;r++) {
			for(int g=0;g<histogram[r].length;g++) {
				for(int b=0;b<histogram[r][g].length;b++) {
					histogram[r][g][b] = (int)(vec[i] * mult);
					i++;
				}	
			}	
		}
		return histogram;
	}
	
	public static Hashtable<String,ConceptNode> buildImageConceptNodes(Vector<ModelData> models, Hashtable<String,ConceptNode> nodes, int knowledgeID, int knowledgeCount) {
		
		Hashtable<String,ConceptNode> newNodes;
		
		if(nodes != null) {
			newNodes = nodes;
		} else {
			newNodes = new Hashtable<String,ConceptNode>();
		}
		
		for(ModelData model : models) {
			String label = model.word;
			ConceptNode concept = newNodes.get(label);
			
			if(concept == null) {
				concept = new ConceptNode(label, knowledgeCount);
				newNodes.put(label, concept);
			} 
			
			if(concept.knowledge[knowledgeID] == null) {
				concept.knowledge[knowledgeID] = toVector(Stats.histogram(model, 5));
			} else {
				VectorTools.setAdd(toVector(Stats.histogram(model, 5)), concept.knowledge[knowledgeID]);
			}
		}
		
		return newNodes;
	}

	public static Hashtable<String,double[]> loadFoodConceptNodes() {
		return loadConceptNodes(new File("senses/foodData.csv"));
	}
	
	public static Hashtable<String,double[]> loadSmellConceptNodes() {
		return loadConceptNodes(new File("senses/smellVectors.csv"));
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
		
	public static Hashtable<String,ConceptNode> setConceptNodes(Hashtable<String,double[]> vectors, Hashtable<String,ConceptNode> nodes, int knowledgeID, int knowledgeCount) {
		
		Hashtable<String,ConceptNode> newNodes;
		
		if(nodes != null) {
			newNodes = nodes;
		} else {
			newNodes = new Hashtable<String,ConceptNode>();
		}
		
		for(String word : vectors.keySet()) {
			
			ConceptNode concept = newNodes.get(word);
			
			if(concept == null) {
				concept = new ConceptNode(word, knowledgeCount);
				newNodes.put(word, concept);
			} 
			
			if(concept.knowledge[knowledgeID] == null) {
				concept.knowledge[knowledgeID] = vectors.get(word);
			} else {
				VectorTools.setAdd(vectors.get(word), concept.knowledge[knowledgeID]);
			}
		}
		
		return newNodes;
	}
	
	public static Hashtable<String,ConceptNode> loadImageConceptNodes(Set<String> words, Hashtable<String,ConceptNode> nodes, int knowledgeID, int knowledgeCount, File rootDir) {
		
		Hashtable<String,ConceptNode> newNodes;
				
		if(nodes != null) {
			newNodes = nodes;
		} else {
			newNodes = new Hashtable<String,ConceptNode>();
		}

		int i = 0;
		for(String word : words) {
			System.out.println(i + "/" + words.size());
			i++;
			ConceptNode concept = newNodes.get(word);
			
			if(concept == null) {
				concept = new ConceptNode(word, knowledgeCount);
				newNodes.put(word, concept);
			} 
			
			File dir = new File(rootDir + "/" + word);
			File[] files = dir.listFiles();
			
			if(dir.listFiles() == null || dir.listFiles().length < 10) {
				System.out.println(word);
			} else {
				System.out.println("O " + word);
			}
			
			for(File img : files) {
				try {
					if(concept.knowledge[knowledgeID] == null) {
						concept.knowledge[knowledgeID] = toVector(Stats.histogram(ImageIO.read(img), 5, null));
					} else {
						VectorTools.setAdd(toVector(Stats.histogram(ImageIO.read(img), 5, null)), concept.knowledge[knowledgeID]);
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return newNodes;
	}
	
	public static Hashtable<String,ConceptNode> buildBEAGLEVecConceptNodes(Vector<ModelData> models, Hashtable<String,ConceptNode> nodes, int knowledgeID, int knowledgeCount, Comparator2DTreeBEAGLE beagleComparator, double[] weights) {
		
		Hashtable<String,ConceptNode> newNodes;
		
		if(nodes != null) {
			newNodes = nodes;
		} else {
			newNodes = new Hashtable<String,ConceptNode>();
		}
		
		for(ModelData model : models) {
			String label = model.word;
			ConceptNode concept = newNodes.get(label);
			
			if(concept == null) {
				concept = new ConceptNode(label, knowledgeCount);
				newNodes.put(label, concept);
			} 
			
			double[] encoded = beagleComparator.encodeModel((Model2DTree)model.model,weights);
			
			//for(int i=0;i<encoded.length;i++) {
			//	System.out.print("[" + encoded[i] + "]");
			//}
			//System.out.println();
			
			if(concept.knowledge[knowledgeID] == null) {
				concept.knowledge[knowledgeID] = encoded;
			} else {
				VectorTools.setAdd(encoded, concept.knowledge[knowledgeID]);
			}
		}
		
		return newNodes;
	}
	
}

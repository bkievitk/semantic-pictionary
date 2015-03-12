package comparison;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import modelManager.Guess;
import modelTools.GeonModel;
import tools.*;
import comparison.tree2D.*;
import creator2DTree.Model2DTree;
import admin.HumanLabeler;
import admin.ModelData;
import admin.ModelManager;
import flanagan.math.*;

public class ComparisonManager  {
		
	static double bestScore = 0;
	
	// Color and geon count histogram.
	public static void main(String[] args) {
		
		Vector<Guess> guesses = Guess.getAllGuesses(new File("8-21-2014_guesses.dat"));
		Vector<ModelData> models = ModelManager.getAllModels(new File("8-21-2014.dat"));
		ModelManager.refineModels(models);	
		Guess.linkModels(models, guesses);

		for(int i=0;i<models.size();i++) {
			if(models.get(i).correctGuesses < 1) {
				models.remove(i);
				i--;
			}
		}

		//Num models: 7940
		/*try {
			System.out.println("Num models: " + models.size());
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		Comparator2DTree[] comparators = {
				//new Comparator2DTreeHistogram(),
				//new Comparator2DTreePixel(),
				new Comparator2DTreeRecurse(new NodeSimilarityMetricHard()),
				//new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_ADD_ROTATE),
		};
		
		double[][] weights = {
				//null,
				//null,
				{0.346679923644288,6.75139126313392E-8,5.097229758851925E-6,8.09428707563313E-7,0.001078098794167262,0.4997304753275638},
				//{1.9666618683917867,0.09277887855848543,0.24119939936888588,3.8057107938604644,0.0936797352763701,0.47659717873232377}
		};
		
		
		for(int i=0;i<comparators.length;i++) {
			System.out.println("Comparator - " + comparators[i].description());
			dissimilarityMatrix(models, comparators[i], weights[i], new File("dissimilarity_" + i + ".csv"));
		}
		
		
		//Comparator2DTreeHistogram metric1 = new Comparator2DTreeHistogram();
		//showMostSimilar(models, metric1, 8, null);
		
		//Comparator2DTreePixel metric1 = new Comparator2DTreePixel();
		//showMostSimilar(models, metric1, 8, null);

		//Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());
		//Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricHard());
		//Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());
		//double[] weights = {0.346679923644288,6.75139126313392E-8,5.097229758851925E-6,8.09428707563313E-7,0.001078098794167262,0.4997304753275638};
		//showMostSimilar(models, metric2, 8, weights);
		
		//Comparator2DTree metric3 = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_ADD_ROTATE);
		//double[] weights2 = {1.9666618683917867,0.09277887855848543,0.24119939936888588,3.8057107938604644,0.0936797352763701,0.47659717873232377};
		//showMostSimilar(models, metric3, 8, weights2);
			
		//System.out.println("Begin optimizing.");
		//Comparator2DTree metric = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_ADD_ROTATE);
		//Comparator2DTree metric = new Comparator2DTreeRecurse(new NodeSimilarityMetricHard());
		//double[] start = {1,1,1,1,1,1};
		//double[] start = {1.248025651013001,0.025182475571135732,0.22716162283301428,2.4821363048767413,0.16815857643316082,0.4580591405576287};
				
		//optimize(models, metric, start);		
		
		//CONNECTION_ROTATE      [1.54582184401584794E18][-1.46883378664609434E18][-1.95336694189284813E18][2.4129212585283405E18][-3.0464443467093407E18][7.748341568089777E10]	 0.6165295015474997
		//CONNECTION_ADD_ROTATE  [4.801515135795339E15][1.17474671174335232E18][3.7657489052699208E18][5.4572468640870618E17][-4.1717005402628137E18][1.4924116242011993]	 0.6143197914181755
		
		
		/*
		Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricHard());
		double[] weights = {0.346679923644288,6.75139126313392E-8,5.097229758851925E-6,8.09428707563313E-7,0.001078098794167262,0.4997304753275638};

		ModelData mdBest = null;
		double simBest = 0;
		
        for(ModelData md : models) {
        	if(md != models.get(0)) {
	        	double sim = metric2.similarity((Model2DTree)md.model, (Model2DTree)models.get(0).model, weights);
	        	System.out.println(sim);
	        	if(sim > simBest) {
	        		simBest = sim;
	        		mdBest = md;
	        	}
        	}
        }

        ImageFrame.makeFrame(models.get(0).model.thumbnail(null, 200, 200, 20));
        ImageFrame.makeFrame(mdBest.model.thumbnail(null, 200, 200, 20));
        */
		
        /*
		for(WeightedObject<String> word : mostDissimilarInType(models, metric2, weights, true).getObjects()) {
			
			Vector<ModelData> modelsInGroup = modelsWordGrouped.get(word.object);			
			BufferedImage[] images = new BufferedImage[modelsInGroup.size()];
			for(int i=0;i<modelsInGroup.size();i++) {
				images[i] = ((Model2DTree)modelsInGroup.get(i).model).thumbnail(Color.WHITE, 100, 100, 5);
				System.out.println();
			}
			ImageFrame.makeFrame(images, 4, 4, word.object);
		}
		*/
		
		
		
		//ModelManager.showAll(models, ModelManager.ORDER_MODEL_TYPE);
		
		
	}	

	public static void dissimilarityMatrix(Vector<ModelData> models, Comparator2DTree metric, double[] weights, File f) {
		
		try {
	        Hashtable<String,Vector<ModelData>> modelsWordGrouped = ModelManager.getAllWordModels(models);
	        BufferedWriter w = new BufferedWriter(new FileWriter(f));
	        w.write(metric.description() + "\n");
	        for(String word1 : modelsWordGrouped.keySet()) {
   	        	w.write("," + word1);
	        }
        	w.write("\n");
        	
        	long startTime = System.currentTimeMillis();
        	long done = 0;
        	long toDo = modelsWordGrouped.keySet().size() * modelsWordGrouped.keySet().size();
        	
	        for(String word1 : modelsWordGrouped.keySet()) {
   	        	w.write(word1);
	        	for(String word2 : modelsWordGrouped.keySet()) {
		        	Vector<ModelData> models1 = modelsWordGrouped.get(word1);
		        	Vector<ModelData> models2 = modelsWordGrouped.get(word2);
	   	        	double similarity = ModelManager.getSimilarity(models1, models2, metric, weights);
	   	        	w.write("," + similarity);
	   	        	w.flush();
	   	        	
	   	        	long thisTime = System.currentTimeMillis();
	   	        	done ++;
	   	        	
	   	        	long timePassed = (thisTime - startTime);
	   	        	long predict = timePassed * toDo / done - timePassed;
	   	        	
	   	        	long seconds = (predict / 1000) % 60;
	   	        	long minutes = (predict / 1000 / 60) % 60;
	   	        	long hours = (predict / 1000 / 60 / 60) % 24;
	   	        	long days = (predict / 1000 / 60 / 60 / 24);
	   	        	
	   	        	System.out.println(days + ":" + hours + ":" + minutes + ":" + seconds);
	            }
   	        	w.write("\n");
	        }
	        w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public static KBox<String> mostDissimilarInType(Vector<ModelData> models, Comparator2DTree metric, double[] weights, boolean isSimilar) {
		KBox<String> similarityList = new KBox<String>(10, isSimilar);
		
        Hashtable<String,Vector<ModelData>> modelsWordGrouped = ModelManager.getAllWordModels(models);
        
        for(String key : modelsWordGrouped.keySet()) {
        	Vector<ModelData> modelsInGroup = modelsWordGrouped.get(key);
        	double similarity = ModelManager.getSimilarity(modelsInGroup, modelsInGroup, metric, weights);
        	similarityList.add(new WeightedObject<String>(key,similarity));
        }
        
		return similarityList;
	}
	
	public static int[] geonCountHistogram(Vector<ModelData> models) {
		int[] histogram = new int[20];
		for(ModelData model : models) {
			int count = model.model.countObjects();
			count = Math.min(count, histogram.length-1);
			histogram[count]++;
		}
		
		// 3-30-2012 values
		// 0,822,1131,1020,750,467,321,220,116,84,43,27,32,22,10,5,0,0,0,0
		return histogram;
	}
	
	public static void showModel(Vector<ModelData> models, int modelID) {

		Color backgroundColor = new Color(250,250,250);
		
		GeonModel selectedModel = models.get(modelID).model;
		Model2DTree selectedModelTree = (Model2DTree)selectedModel;		
		
		BufferedImage background = new BufferedImage(500, 500, BufferedImage.TYPE_INT_RGB);
		Graphics g = background.getGraphics();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, background.getWidth(), background.getHeight());
		
		//BufferedImage img = selectedModelTree.thumbnail(background.getWidth(), background.getHeight(), 25, 0, 1, selectedModelTree.root, backgroundColor);
		BufferedImage img = selectedModelTree.thumbnail(background, background.getWidth(), background.getHeight(), 5);
			
		ImageFrame.makeFrame(img);
		
	}
	
	public static void showModels(Vector<ModelData> models, String description) {

		Color backgroundColor = new Color(250,250,250);
		
		BufferedImage[] images = new BufferedImage[models.size()];
		for(int i=0;i<models.size();i++) {
			Model2DTree selectedModelTree = (Model2DTree)models.get(i).model;		
			
			BufferedImage background = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
			Graphics g = background.getGraphics();
			g.setColor(backgroundColor);
			g.fillRect(0, 0, background.getWidth(), background.getHeight());
			
			BufferedImage img = selectedModelTree.thumbnail(background, background.getWidth(), background.getHeight(), 5);
			images[i] = img;
		}

		ImageFrame.makeFrame(images, 4, 4, description);
		
	}
	
	public static void showMostSimilar(Vector<ModelData> models, Comparator2DTree metric, int target, double[] weights) {
										
		KBox<ModelData> similar = getNMostSimilar(models, target, 15, metric, weights);
		WeightedObject<ModelData>[] similarModels = similar.getObjects();
		BufferedImage[] images = new BufferedImage[similarModels.length + 1];
		
		images[0] = ((Model2DTree)models.get(target).model).thumbnail(Color.WHITE, 100, 100, 5);
		for(int i=0;i<similarModels.length;i++) {
			images[i + 1] = ((Model2DTree)similarModels[i].object.model).thumbnail(Color.WHITE, 100, 100, 5);
			System.out.println((i+1) + ") " + similarModels[i].weight + " [" + similarModels[i].object + "]");
		}
		
		ImageFrame.makeFrame(images, 4, 4, metric.description());
		
	}
		
	public static void optimize(final Vector<ModelData> models, final Comparator2DTree metric, double[] start) {
		
        //Create instance of Maximisation
        Maximisation max = new Maximisation();
        
        final Hashtable<Integer,Vector<ModelData>> modelsWordGrouped = ModelManager.getAllWordIDModels(models);
		final Hashtable<String,Vector<ModelData>> modelsSubjectGrouped = ModelManager.getAllUserModels(models);
		
        MaximisationFunction funct = new MaximisationFunction() {
        	
            public double function(double[] x){	
            	
            	for(int i=0;i<x.length;i++) {
            		x[i] = Math.abs(x[i]);
            		x[i] = Math.max(.00000001, x[i]);
            	}
            	x[4] = 1 / (1 + Math.pow(Math.E, x[4]));
            	x[5] = 1 / (1 + Math.pow(Math.E, x[4]));
            	
                double score = ModelManager.withinBetween(metric, models, x, modelsWordGrouped, modelsSubjectGrouped);
                                
                if(score > bestScore) {
                	bestScore = score;
	               	for(int i=0;i<x.length;i++) {
	                   	System.out.print("[" + x[i] + "]");
	                }
	               	System.out.println("\t " + score);
                } else {
                	System.out.print(".");
                }                
                
                return score;
            }
        };
        
        // initial step sizes
        double[] step = new double[start.length];
        for(int i=0;i<step.length;i++) {
        	step[i] = 1;
        }

        // convergence tolerance
        double ftol = 1e-15;
        
        // Nelder and Mead maximisation procedure
        max.nelderMead(funct, start, step, ftol, 10000);

        // get values of y and z at maximum
        double[] param = max.getParamValues();

        for(int i=0;i<param.length;i++) {
        	System.out.print("[" + param[i] + "]");
        }
        System.out.println();
	}
	
	public static KBox<ModelData> getNMostSimilar(Vector<ModelData> models, int target, int n, Comparator2DTree metric, double[] weights) {
		KBox<ModelData> matches = new KBox<ModelData>(n, true);
		
		ModelData targetModelData = models.get(target);
		Model2DTree targetModel = (Model2DTree)targetModelData.model;
				
		for(ModelData model : models) {
			if(model != targetModelData) {
				double similarity = metric.similarity((Model2DTree)model.model, targetModel, weights);
				matches.add(new WeightedObject<ModelData>(model, similarity));
			}
		}
		
		return matches;
	}
	
	/*
	
	public static void main(String[] args){

        //Create instance of Maximisation
        Maximisation max = new Maximisation();

        // Create instace of class holding function to be maximised
        //MaximFunct funct = new MaximFunct();

        // Set value of the constant a to 5
        //funct.setA(5.0D);
        
        MaximisationFunction funct = new MaximisationFunction() {

            private double a = 5.0D;

            // evaluation function
            public double function(double[] x){
                double z = a - (x[0]-1.0D)*(x[0]-1.0D) - 3.0D*Math.pow((x[1]+1.0D), 4);
                return z;
            }

            // Method to set a
            public void setA(double a){
                this.a = a;
            }
        };
        

        // initial estimates
        double[] start = {1.0D, 3.0D};

        // initial step sizes
        double[] step = {0.2D, 0.6D};

        // convergence tolerance
        double ftol = 1e-15;

        // Nelder and Mead maximisation procedure
        max.nelderMead(funct, start, step, ftol);

        // get the maximum value
        double maximum = max.getMaximum();

        // get values of y and z at maximum
         double[] param = max.getParamValues();

        // Print results to a text file
        max.print("MaximExampleOutput.txt");

        // Output the results to screen
        System.out.println("Maximum = " + max.getMaximum());
        System.out.println("Value of x at the maximum = " + param[0]);
        System.out.println("Value of y at the maximum = " + param[1]);


    }
    
    */
}

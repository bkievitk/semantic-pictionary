package comparison;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

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
		
		Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
		ModelManager.refineModels(models);
		
		System.out.println(models.size());
		//Comparator2DTreePixel metric1 = new Comparator2DTreePixel();
		//showMostSimilar(models, metric1, 8, null);
		
		//Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());
		//double[] weights = {4.313359510131976,4.590585382304277E-19,0.008973204333907029,6.675949873572398E-12,0.4226279152152204,2.6782405957593314};
		//showMostSimilar(models, metric2, 8, weights);
		
		//Comparator2DTree metric3 = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_NONE);
		//double[] weights2 = {1,1,1,1,1,1};
		//showMostSimilar(models, metric3, 8, weights2);
			
		System.out.println("Begin optimizing.");
		Comparator2DTree metric = new Comparator2DTreeBEAGLE(Comparator2DTreeBEAGLE.CONNECTION_NONE);
		double[] start = {1,1,1,1,1,1};
		optimize(models, metric, start);		
		
		//CONNECTION_ROTATE      [1.54582184401584794E18][-1.46883378664609434E18][-1.95336694189284813E18][2.4129212585283405E18][-3.0464443467093407E18][7.748341568089777E10]	 0.6165295015474997
		//CONNECTION_ADD_ROTATE  [4.801515135795339E15][1.17474671174335232E18][3.7657489052699208E18][5.4572468640870618E17][-4.1717005402628137E18][1.4924116242011993]	 0.6143197914181755
		
		
		/*
		Comparator2DTree metric2 = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());
		double[] weights = {4.313359510131976,4.590585382304277E-19,0.008973204333907029,6.675949873572398E-12,0.4226279152152204,2.6782405957593314};

        Hashtable<String,Vector<ModelData>> modelsWordGrouped = ModelManager.getAllWordModels(models);
		for(WeightedObject<String> word : mostDissimilarInType(models, metric2, weights, true).getObjects()) {
			
			Vector<ModelData> modelsInGroup = modelsWordGrouped.get(word.object);			
			BufferedImage[] images = new BufferedImage[modelsInGroup.size()];
			for(int i=0;i<modelsInGroup.size();i++) {
				images[i] = ((Model2DTree)modelsInGroup.get(i).model).thumbnail(Color.WHITE, 100, 100, 5);
			}
			ImageFrame.makeFrame(images, 4, 4, word.object);
		}
		
		*/
		
		//ModelManager.showAll(models, ModelManager.ORDER_MODEL_TYPE);
		
		
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
		
		//final Comparator2DTree metric = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());
		//final Comparator2DTree metric = new Comparator2DTreePixel();
		
        MaximisationFunction funct = new MaximisationFunction() {
        	
            public double function(double[] x){	
            	/*
            	double[] newX = new double[x.length];
            	for(int i=0;i<x.length;i++) {
            		if(x[i] < 0) {
            			newX[i] = -x[i];
            		} else {
            			newX[i] = x[i];
            		}
            	}
            	
            	newX[4] = 1 / (1 + Math.pow(Math.E, newX[4]));
            	*/
            	
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
                
        // get the maximum value
        //double maximum = max.getMaximum();

        // get values of y and z at maximum
        double[] param = max.getParamValues();

        for(int i=0;i<param.length;i++) {
        	System.out.print("[" + param[i] + "]");
        }
        System.out.println();
 		
        //[4.313359510131976][4.590585382304277E-19][0.008973204333907029][6.675949873572398E-12][0.4226279152152204][2.6782405957593314]	 0.5938904728232494
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

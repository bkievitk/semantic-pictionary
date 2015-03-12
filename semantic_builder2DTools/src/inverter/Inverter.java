package inverter;
import integration.ConceptNode;

import java.util.Hashtable;
import java.util.Random;

import relations.WordRelator;
import tools.VectorTools;


public class Inverter {

	public static void main(String[] args) {
		
		int size = 50;
		int vecSize = 20;
		int count = 10;
		
		double errorBuildSum = 0;

		for(int i=0;i<count;i++) {
			System.out.println("Trial " + i + " of " + count);
			
			//double[][] similarity = createRandomSimilarity(size);
			double[][] similarity = createRandomSimilarity2(size, vecSize);
			
			//double[][] vecsBuilt = buildVectors1(similarity, vecSize);
			//double[][] vecsBuilt = VectorTools.copy(similarity);
			double[][] vecsBuilt = createRandomVectors(size, vecSize);
			
			/*
			double[][] vecsBuilt = buildVectors1(similarity, vecSize);
			for(int k=0;k<1;k++) {
				double[][] vecsBuilt2 = buildVectors1(similarity, vecSize);
				for(int j=0;j<vecsBuilt.length;j++) {
					VectorTools.setAdd(VectorTools.getPointwiseMultiply(vecsBuilt2[i],vecsBuilt2[i]), vecsBuilt[j]);
				}
			}
			*/
			
			double step = 1;
			System.out.println("-----");
			for(int k=0;k<50;k++) {
				double sum = 0;
				for(int j=0;j<size;j++) {
					sum += updateStepVectors(similarity, vecsBuilt,j,step);
				}			
				//System.out.println(sum);
				step *= 0.9;
			}
			
			double[][] similarityBuilt = getSimilarity(vecsBuilt);
			double errorBuild = getAvgError(similarity, similarityBuilt);
			errorBuildSum += errorBuild;

			System.out.println("Error: " + errorBuild);
			
			//double[][] vecsRandom = createRandomVectors(size, vecSize);
			//double[][] similarityRandom = getSimilarity(vecsRandom);
			//double errorRandom = getAvgError(similarity, similarityRandom);
			//errorRandomSum += errorRandom;
		}
		
		System.out.println("Final " + (errorBuildSum / count));
		
		
	}
	
	public static double[][] buildVectors(double[][] similarity, int vecSize) {

		double[][] vecsBuilt = buildVectors1(similarity, vecSize);
		
		double step = 1;
		for(int k=0;k<50;k++) {
			double sum = 0;
			for(int j=0;j<similarity.length;j++) {
				sum += updateStepVectors(similarity, vecsBuilt, j, step);
			}			
			step *= 0.9;
		}
		
		return vecsBuilt;
		
	}
	
	public static double getAvgError(double[][] similarity, double[][] newSimilarity) {
		// Get fitness
		double sum = 0;
		int count = 0;
		int size = similarity.length;
		
		for(int w1=0;w1<size;w1++) {
			for(int w2=w1+1;w2<size;w2++) {
				count++;
				sum += Math.abs(newSimilarity[w1][w2] - similarity[w1][w2]);
			}	
		}
		
		return sum / count;
	}
	
	public static double[][] buildVectors1(double[][] similarity, int vecSize) {
		
		int size = similarity.length;
		double[][] vecsTarget = new double[size][vecSize];
		double[][] vecsInitial = createRandomVectors(size, vecSize);
		
		for(int w1=0;w1<size;w1++) {
			for(int w2=0;w2<size;w2++) {
				if(w1 != w2) {
					VectorTools.setAdd(VectorTools.mult(vecsInitial[w2], Math.pow(similarity[w1][w2],.5)), vecsTarget[w1]);
				}
			}	
		}
		
		return vecsTarget;
	}

	public static double updateStepVectors(double[][] similarity, double[][] vecs, int wrd1, double step) {
		
		double errorUpdate = 0;
		
		// Select random vector
		Random rand = new Random();
		
		for(int i=0;i<vecs[wrd1].length;i++) {
			double tmp = vecs[wrd1][i];
			
			double sum = 0;
			for(int wrd2=0;wrd2<vecs.length;wrd2++) {
				if(wrd1 != wrd2) {
					sum += Math.abs(similarity[wrd1][wrd2] - VectorTools.getCosine(vecs[wrd1], vecs[wrd2]));
				}
			}
			
			vecs[wrd1][i] = tmp + step;
			
			double sumPlus = 0;
			for(int wrd2=0;wrd2<vecs.length;wrd2++) {
				if(wrd1 != wrd2) {
					sumPlus += Math.abs(similarity[wrd1][wrd2] - VectorTools.getCosine(vecs[wrd1], vecs[wrd2]));
				}
			}
			
			vecs[wrd1][i] = tmp - step;
			
			double sumMinus = 0;
			for(int wrd2=0;wrd2<vecs.length;wrd2++) {
				if(wrd1 != wrd2) {
					sumMinus += Math.abs(similarity[wrd1][wrd2] - VectorTools.getCosine(vecs[wrd1], vecs[wrd2]));
				}
			}
			
			if(sum < sumPlus && sum < sumMinus) {
				vecs[wrd1][i] = tmp;
			} else if(sumPlus < sumMinus) {
				errorUpdate += ((sum - sumPlus) / (vecs.length - 1));
				vecs[wrd1][i] = tmp + step;
			} else {
				errorUpdate += ((sum - sumMinus) / (vecs.length - 1));
				vecs[wrd1][i] = tmp - step;
			}			
			
		}
		
		return errorUpdate;
	}
	
	public static double[][] getSimilarity(double[][] vecs) {
		int size = vecs.length;
		double[][] similarity = new double[size][size];
		for(int w1=0;w1<size;w1++) {
			for(int w2=0;w2<size;w2++) {
				if(w1 != w2) {
					similarity[w1][w2] = VectorTools.getCosine(vecs[w1], vecs[w2]);
				}
			}	
		}
		return similarity;
	}
	
	public static double[][] createRandomVectors(int size, int length) {
		double[][] similarity = new double[size][length];
		Random rand = new Random();
		
		for(int x=0;x<similarity.length;x++) {
			for(int y=0;y<similarity[x].length;y++) {
				double value = rand.nextGaussian();
				//double value = Math.pow(rand.nextDouble(),10000);
				
				similarity[x][y] = value;
			}
		}
		
		return similarity;		
	}
	
	public static double[][] createRandomSimilarity2(int size, int length) {
		double[][] vecs = createRandomVectors(size, length);
		double[][] similarity = new double[size][size];
		
		for(int x=0;x<similarity.length;x++) {
			for(int y=x+1;y<similarity[x].length;y++) {
				similarity[x][y] = similarity[y][x] = VectorTools.getCosine(vecs[x], vecs[y]);
			}
		}
		
		return similarity;
	}
	
	public static double[][] createRandomSimilarity(int size) {
		double[][] similarity = new double[size][size];
		Random rand = new Random();
		
		for(int x=0;x<similarity.length;x++) {
			for(int y=x+1;y<similarity[x].length;y++) {
				double value = rand.nextDouble();
				similarity[x][y] = similarity[y][x] = value;
			}
		}
		
		return similarity;		
	}
}

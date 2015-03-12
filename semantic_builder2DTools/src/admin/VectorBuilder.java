package admin;

import java.io.*;
import java.util.Hashtable;
import java.util.Vector;

import comparison.tree2D.Comparator2DTreeBEAGLE;
import creator2DTree.Model2DTree;

import beagle.BEAGLESP;

public class VectorBuilder {

	public static void main(String[] args) {
		try {
			Vector<ModelData> models = ModelManager.getAllModels(new File("4-9-2012.dat"));
			ModelManager.refineModels(models);
			Hashtable<String,Vector<ModelData>> sorted = ModelManager.getAllWordModels(models);
	
			BufferedWriter w = new BufferedWriter(new FileWriter(new File("seeVector.csv")));
			for(String word : sorted.keySet()) {
				
				double[] vec = new double[Comparator2DTreeBEAGLE.DIMENSIONS];
				Vector<ModelData> wordModels = sorted.get(word);
				for(ModelData model : wordModels) {
					double[] newVec = BEAGLESP.encodeInstance(((Model2DTree)model.model).root);
					for(int i=0;i<vec.length;i++) {
						vec[i] += newVec[i];
					}
				}
				
				System.out.println(word);
				w.write(word);
				for(int i=0;i<vec.length;i++) {
					w.write("," + vec[i] / wordModels.size());
				}
				w.write("\n");
				
			}
			w.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

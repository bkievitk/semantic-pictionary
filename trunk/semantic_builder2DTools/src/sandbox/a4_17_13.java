package sandbox;

import java.io.File;
import java.util.*;

import comparison.ComparisonManager;
import comparison.tree2D.*;
import admin.*;

public class a4_17_13 {

	public static void main(String[] args) {
		final Vector<ModelData> models = ModelManager.getAllModels(new File("4-17-2013.dat"));
		ModelManager.refineModels(models);
		
		ModelManager.showAll(models, ModelManager.ORDER_MODEL_TYPE);
		
		/*
		Hashtable<String,Vector<ModelData>> modelSort = ModelManager.getAllWordModels(models);
		for(String s : modelSort.keySet()) {
			System.out.println(s + " " + modelSort.get(s).size());
		}
		
		double[] start = {1,1,1,1,1,1};
		Comparator2DTree metric = new Comparator2DTreeRecurse(new NodeSimilarityMetricSoft());		
		ComparisonManager.optimize(models, metric, start);
		*/
	}
}

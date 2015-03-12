package beagle;


import java.io.File;
import java.util.Vector;

import admin.ModelData;
import admin.ModelManager;
import modelTools.GeonModel;
import modelTools.Primitive2D;
import comparison.tree2D.Comparator2DTreeBEAGLE;
import tools.VectorTools;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;

public class BEAGLESP {

	public static void main(String[] args) {

		Vector<ModelData> models = ModelManager.getAllModels(new File("3-29-2012.dat"));
		ModelManager.refineModels(models);
		
		double[] weights = {1.0,1.0,1.0,1.0,1.0,1.0};
		
		for(ModelData model : models) {
			Model2DTree modelTree = (Model2DTree)model.model;
			encodeInstance(modelTree.root, weights);
		}

		Model2DTree modelTree = (Model2DTree)models.get(0).model;
		double[] encoded = encodeInstance(modelTree.root, weights);
		
		PrimitiveInstance2DTree decoded = decodeInstance(encoded, weights);	
	}
	public static double[] encodeInstance(PrimitiveInstance2DTree instance) {
		double[] weights = {1.54582184401584794E18,-1.46883378664609434E18,-1.95336694189284813E18,2.4129212585283405E18,-3.0464443467093407E18,7.748341568089777E10};
		return encodeInstance(instance, weights);
	}
	
	public static PrimitiveInstance2DTree decodeInstance(double[] encoded, double[] weights) {
		return null;
	}
	
	public static double[] encodeInstance(PrimitiveInstance2DTree instance, double[] weights) {

		double[] vec = VectorTools.zero(Comparator2DTreeBEAGLE.DIMENSIONS);
		
		// Color.
		double colorWeight = weights[0];
		vec = VectorTools.getAdd(vec, VectorTools.mult(Comparator2DTreeBEAGLE.getColorVector(instance.color),colorWeight));

		// rotation.
		double rotationWeight = weights[1];
		vec = VectorTools.getAdd(vec, VectorTools.mult(Comparator2DTreeBEAGLE.getRotationVector(instance.rotation),rotationWeight));
		
		// scale.
		double scaleWeight = weights[2];
		vec = VectorTools.getAdd(vec, VectorTools.mult(Comparator2DTreeBEAGLE.getScaleVector(instance.scale),scaleWeight));
		
		// shape.
		double shapeWeight = weights[3];
		vec = VectorTools.getAdd(vec, VectorTools.mult(Comparator2DTreeBEAGLE.getShapeVector(instance.shape),shapeWeight));
				
		return vec;
	}
}

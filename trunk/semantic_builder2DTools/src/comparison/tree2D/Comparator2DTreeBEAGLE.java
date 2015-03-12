package comparison.tree2D;

import java.awt.Color;
import java.util.Hashtable;

import modelTools.PrimitiveBase;

import tools.MyArrays;
import tools.VectorTools;

import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;


public class Comparator2DTreeBEAGLE implements Comparator2DTree {

	public static final int DIMENSIONS = 200;
	private static Hashtable<Color,double[]> colorVectors = new Hashtable<Color,double[]>();
	private static Hashtable<PrimitiveBase,double[]> shapeVectors = new Hashtable<PrimitiveBase,double[]>();
	private static double[][][] rotationVectors = buildVectorSet(3,3);
	private static double[][][] scaleVectors = buildVectorSet(3,3);
	
	private static double[][][] childAttachmentVectors = buildVectorSet(3,3);
	private static double[][][] parentAttachmentVectors = buildVectorSet(3,3);

	private int connectionType;
	public static final int CONNECTION_ROTATE = 0;
	public static final int CONNECTION_ADD_ROTATE = 1;
	public static final int CONNECTION_NONE = 2;
	
	public Comparator2DTreeBEAGLE(int connectionType) {
		this.connectionType = connectionType;
	}
	
	public String description() {
		return "BEAGLE";
	}
	
	public double[] encodeModel(Model2DTree model, double[] weights) {
		return encodeModel(model.root, weights);
	}
	
	private double[] encodeModel(PrimitiveInstance2DTree node, double[] weights) {
		double[] vec = encodeInstance(node, weights);
		
		for(PrimitiveInstance2DConnection c : node.children) {
			double[] childEncoding = encodeModel(c.child, weights);
			

			if(connectionType == CONNECTION_ROTATE) {
				childEncoding = VectorTools.rotate(childEncoding, 1);
			} else if(connectionType == CONNECTION_ADD_ROTATE) {
				double connectionWeight = weights[4];
				childEncoding = VectorTools.getAdd(childEncoding, VectorTools.mult(getConnectionVector(c.childAttachmentPoint,c.parentAttachmentPoint),connectionWeight));
				childEncoding = VectorTools.rotate(childEncoding, 1);
			}			
			
			double childWeight = weights[5];
			vec = VectorTools.getAdd(vec, VectorTools.mult(childEncoding,childWeight));
		}
		
		return vec;
	}
	
	public static double[] encodeInstance(PrimitiveInstance2DTree instance, double[] weights) {

		double[] vec = VectorTools.zero(DIMENSIONS);
		
		// Color.
		double colorWeight = weights[0];
		vec = VectorTools.getAdd(vec, VectorTools.mult(getColorVector(instance.color),colorWeight));

		// rotation.
		double rotationWeight = weights[1];
		vec = VectorTools.getAdd(vec, VectorTools.mult(getRotationVector(instance.rotation),rotationWeight));
		
		// scale.
		double scaleWeight = weights[2];
		vec = VectorTools.getAdd(vec, VectorTools.mult(getScaleVector(instance.scale),scaleWeight));
		
		// shape.
		double shapeWeight = weights[3];
		vec = VectorTools.getAdd(vec, VectorTools.mult(getShapeVector(instance.shape),shapeWeight));
				
		return vec;
	}
	
	private static double[][][] buildVectorSet(int is, int js) {
		double[][][] rotation = new double[is][js][];
		for(int i = 0; i < is; i++) {
			for(int j = 0; j < js; j++) {
				rotation[i][j] = VectorTools.newGaussian(DIMENSIONS);
			}
		}
		return rotation;
	}

	public static double[] getColorVector(Color color) {
		double[] vec = colorVectors.get(color);
		if(vec == null) {

			double[] red = VectorTools.newGaussian(DIMENSIONS / 3);
			double[] green = VectorTools.newGaussian(DIMENSIONS / 3);
			double[] blue = VectorTools.newGaussian(DIMENSIONS / 3);
			
			vec = new double[DIMENSIONS];
			MyArrays.copyIntoRange(red, vec, 0, 0, red.length);
			MyArrays.copyIntoRange(green, vec, 0, red.length, green.length);
			MyArrays.copyIntoRange(blue, vec, 0, red.length + green.length, blue.length);
			
			vec = VectorTools.normalize(vec);
						
			colorVectors.put(color, vec);
		}
		return vec;
	}

	public static double[] getShapeVector(PrimitiveBase shape) {
		double[] vec = shapeVectors.get(shape);
		if(vec == null) {
			vec = VectorTools.newGaussian(DIMENSIONS);
			shapeVectors.put(shape, vec);
		}
		return vec;
	}

	public static double[] getRotationVector(int[] rotation) {
		double[] vec = VectorTools.zero(DIMENSIONS);
		for(int dimension = 0;dimension < rotation.length; dimension++) {

			int index = rotation[dimension] * rotationVectors[dimension].length / 600;
			index = Math.min(rotationVectors[dimension].length-1, Math.max(index, 0));
			
			double[] toAdd = rotationVectors[dimension][index];
			vec = VectorTools.getAdd(vec, toAdd);
		}
		return vec;
	}
	
	public static double[] getScaleVector(int[] scale) {
		double[] vec = VectorTools.zero(DIMENSIONS);
		for(int dimension = 0;dimension < scale.length; dimension++) {
			
			int index = scale[dimension] * scaleVectors[dimension].length / 400;
			index = Math.min(scaleVectors[dimension].length-1, Math.max(index, 0));
			
			double[] toAdd = scaleVectors[dimension][index];
			vec = VectorTools.getAdd(vec, toAdd);
		}
		return vec;
	}
	
	public static double[] getConnectionVector(short[] child, short[] parent) {
		double[] vec = VectorTools.zero(DIMENSIONS);
		
		for(int dimension = 0;dimension < child.length; dimension++) {
			double[] toAdd = childAttachmentVectors[dimension][child[dimension]+1];
			vec = VectorTools.getAdd(vec, toAdd);
			toAdd = parentAttachmentVectors[dimension][parent[dimension]+1];
			vec = VectorTools.getAdd(vec, toAdd);
		}
		return vec;
	}

	@Override
	public double similarity(Model2DTree m1, Model2DTree m2, double[] weights) {
		double[] enc1 = VectorTools.normalize(encodeModel(m1, weights));
		double[] enc2 = VectorTools.normalize(encodeModel(m2, weights));
		//double cosine = VectorTools.getCosine(enc1, enc2);
		double dot = VectorTools.dot(enc1, enc2);
		
		if(dot < -1.1 || dot > 1.1) {
			System.out.println("ERROR:" + dot);
		}
		return dot;
	}
	
}

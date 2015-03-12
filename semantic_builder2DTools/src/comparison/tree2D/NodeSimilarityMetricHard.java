package comparison.tree2D;

import java.awt.Color;

import modelTools.Primitive2D;
import tools.MyArrays;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;

public class NodeSimilarityMetricHard extends NodeSimilarityMetric {
	
	public double connectionSimilarity(PrimitiveInstance2DConnection m1, PrimitiveInstance2DConnection m2, double[] weights) {

		double similarityScore = 0;
		
		short[] c1 = m1.childAttachmentPoint;
		short[] c2 = m2.childAttachmentPoint;
		short[] p1 = m1.parentAttachmentPoint;
		short[] p2 = m2.parentAttachmentPoint;

		// Boolean check for array similarity.
		if(MyArrays.equal(c1,c2)) {
			similarityScore += .5;
		}
		
		// Boolean check for array similarity.
		if(MyArrays.equal(p1,p2)) {
			similarityScore += .5;
		}
		
		return similarityScore;
	}
	
	public double similarity(PrimitiveInstance2DTree m1, PrimitiveInstance2DTree m2, double[] weights) {
		
		double similarityScore = 0;
		
		Color c1 = m1.color;
		Color c2 = m2.color;
		
		// Boolean check for similarity.
		if(c1.equals(c2)) {
			similarityScore += colorWeight(weights);
		}
		
		int[] r1 = m1.rotation;
		int[] r2 = m2.rotation;
		
		// Boolean check for array similarity.
		if(MyArrays.equal(r1,r2)) {
			similarityScore += rotationWeight(weights);
		}
		
		int[] s1 = m1.scale;
		int[] s2 = m2.scale;
		
		// Boolean check for array similarity.
		if(MyArrays.equal(s1,s2)) {
			similarityScore += scaleWeight(weights);
		}
		
		Primitive2D p1 = m1.shape;
		Primitive2D p2 = m2.shape;
		
		// Boolean check for similarity.
		if(p1 == p2) {
			similarityScore += shapeWeight(weights);
		}
		
		return similarityScore / combinedWeight(weights);
	}
}

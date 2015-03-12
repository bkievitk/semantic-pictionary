package comparison.tree2D;

import java.awt.Color;

import modelTools.Primitive2D;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;

public class NodeSimilarityMetricSoft extends NodeSimilarityMetric {

	public double connectionSimilarity(PrimitiveInstance2DConnection m1, PrimitiveInstance2DConnection m2, double[] weights) {

		double similarityScore = 0;
		
		short[] c1 = m1.childAttachmentPoint;
		short[] c2 = m2.childAttachmentPoint;
		short[] p1 = m1.parentAttachmentPoint;
		short[] p2 = m2.parentAttachmentPoint;

		// Take the fraction of correct matches.
		for(int i=0;i<c1.length;i++) {
			if(c1[i] == c2[i]) {
				similarityScore += .5 / c1.length;
			}
		}

		// Take the fraction of correct matches.
		for(int i=0;i<p1.length;i++) {
			if(p1[i] == p2[i]) {
				similarityScore += .5 / p1.length;
			}
		}
		
		return similarityScore;
	}
	
	public double similarity(PrimitiveInstance2DTree m1, PrimitiveInstance2DTree m2, double[] weights) {
		
		double similarityScore = 0;
		
		Color c1 = m1.color;
		Color c2 = m2.color;

		// Take the color difference scaled between 0 and 1.
		int diff = Math.abs(c1.getRed() - c2.getRed()) + Math.abs(c1.getGreen() - c2.getGreen()) + Math.abs(c1.getBlue() - c2.getBlue());
		int maxDiff = 255 * 3;
		similarityScore += colorWeight(weights) * (maxDiff - diff) / maxDiff;
		
		int[] r1 = m1.rotation;
		int[] r2 = m2.rotation;
		
		// Take the fraction of correct matches.
		// Could use partial matches here as well.
		for(int i=0;i<r1.length;i++) {
			if(r1[i] == r2[i]) {
				similarityScore += rotationWeight(weights) / r1.length;
			}
		}

		int[] s1 = m1.scale;
		int[] s2 = m2.scale;
		
		// Take the fraction of correct matches.
		// Could use partial matches here as well.
		for(int i=0;i<s1.length;i++) {
			if(s1[i] == s2[i]) {
				similarityScore += scaleWeight(weights) / s1.length;
			}
		}
		
		Primitive2D p1 = m1.shape;
		Primitive2D p2 = m2.shape;
		
		// Boolean check for similarity.
		// Could use other values but no basis for them yet.
		if(p1 == p2) {
			similarityScore += shapeWeight(weights);
		}
		
		return similarityScore / combinedWeight(weights);
	}
}
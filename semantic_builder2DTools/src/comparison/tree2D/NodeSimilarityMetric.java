package comparison.tree2D;

import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;

public abstract class NodeSimilarityMetric {
		
	// Returns between 0 and 1 where 1 is highly similar and 0 is not similar.
	public abstract double similarity(PrimitiveInstance2DTree m1, PrimitiveInstance2DTree m2, double[] weights);
	public abstract double connectionSimilarity(PrimitiveInstance2DConnection m1, PrimitiveInstance2DConnection m2, double[] weights);

	public double colorWeight(double[] weights) {
		return weights[0];
	}

	public double rotationWeight(double[] weights) {
		return weights[1];
	}

	public double scaleWeight(double[] weights) {
		return weights[2];
	}

	public double shapeWeight(double[] weights) {
		return weights[3];
	}

	public double combinedWeight(double[] weights) {
		return weights[0] + weights[1] + weights[2] + weights[3];
	}
}
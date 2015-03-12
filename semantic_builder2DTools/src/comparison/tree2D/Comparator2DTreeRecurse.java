package comparison.tree2D;

import java.util.HashSet;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DConnection;
import creator2DTree.PrimitiveInstance2DTree;

public class Comparator2DTreeRecurse implements Comparator2DTree {
		
	private NodeSimilarityMetric metric;
	
	public Comparator2DTreeRecurse(NodeSimilarityMetric metric) {
		this.metric = metric;
	}

	public String description() {
		return "Recursive";
	}
	
	@Override
	public double similarity(Model2DTree m1, Model2DTree m2, double[] weights) {
		PrimitiveInstance2DTree root1 = m1.root;
		PrimitiveInstance2DTree root2 = m2.root;
		return modelSimilarityMetric_Tree(root1,root2,weights, 0);
	}
	
	private double modelSimilarityMetric_Tree(PrimitiveInstance2DTree n1, PrimitiveInstance2DTree n2, double[] weights, int depth) {
		
		// Track your similarity score and max. The result will be the ratio of the two.
		double similarityScore = 0;
		double similarityScoreMax = 0;
		double similarityScoreChildren = 0;
		double similarityScoreChildrenMax = 0;
		
		// Add similarity of this object.
		similarityScore += metric.similarity(n1, n2, weights);
		similarityScoreMax += 1.0;
		
		// This is a list of all children in the second object already matched with something in the first object.
		HashSet<PrimitiveInstance2DTree> c2Matched = new HashSet<PrimitiveInstance2DTree>();
		
		// For every child in the first object.
		for(PrimitiveInstance2DConnection c1 : n1.children) {
			
			// Track best match.
			double bestSimilarityValue = 0.0;
			PrimitiveInstance2DTree bestSimilarityObject = null;
			
			// Try matching it to ever child in the second object.
			for(PrimitiveInstance2DConnection c2 : n2.children) {
				
				// Only match if not already used.
				if(!c2Matched.contains(c2.child)) {
					
					// Get the similarity score.
					double similarityModel = modelSimilarityMetric_Tree(c1.child,c2.child, weights, depth + 1); 
					double similarityConnection = metric.connectionSimilarity(c1, c2, weights);
										
					double connectionWeight = weights[4];
					
					double similarity = (1 - connectionWeight) * similarityModel + connectionWeight * similarityConnection;
						
					if(similarity > bestSimilarityValue) {
						bestSimilarityValue = similarity;
						bestSimilarityObject = c2.child;
					}
				}
			}
			
			if(bestSimilarityObject != null) {
				c2Matched.add(bestSimilarityObject);

				double childPenalty = weights[5];
				
				// Here we apply a child penalty.
				similarityScoreChildren += bestSimilarityValue * childPenalty;
				similarityScoreChildrenMax += childPenalty;
			} else {
				// No object found. We are done matching possible children.
				
				// We can add a penalty for unmatched children here.
				// Ex. similarityScoreMax += (n1.children.length - n2.children.length) * c
				break;
			}
		}

		similarityScore += similarityScoreChildren;
		similarityScoreMax += similarityScoreChildrenMax;
				
		if(similarityScore > similarityScoreMax) {
			System.out.println("Error " + similarityScore + " " + similarityScoreMax + " " + similarityScoreChildren + " " + similarityScoreChildrenMax);
		}
		
		
		return similarityScore / similarityScoreMax;
	}
	
}



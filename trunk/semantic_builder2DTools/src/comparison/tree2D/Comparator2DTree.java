package comparison.tree2D;

import creator2DTree.Model2DTree;

public interface Comparator2DTree {
	public abstract double similarity(Model2DTree m1, Model2DTree m2, double[] weights);
	public abstract String description();
}

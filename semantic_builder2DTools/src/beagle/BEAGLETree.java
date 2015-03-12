package beagle;

import java.util.*;

import tools.VectorTools;

public class BEAGLETree {

	public static int dim = 1000;
	
	public static Hashtable<Object,double[]> symbols = new Hashtable<Object,double[]>();
	public static Vector<double[]> orderMarkers = new Vector<double[]>();
	public static Vector<double[]> levelMarkers = new Vector<double[]>();
	public static Object nullChild = new Object();
	
	static {
		symbols.put(nullChild, VectorTools.newGaussian(dim));
	}
	
	public Vector<BEAGLETree> children = new Vector<BEAGLETree>();
	public Object symbol;
	
	public BEAGLETree(Object symbol) {
		this.symbol = symbol;
	}
	
	public BEAGLETree() {
	}
	
	public static void main(String[] args) {
				
		BEAGLETree node1 = new BEAGLETree("a");
		BEAGLETree node2 = new BEAGLETree("b");
		BEAGLETree node3 = new BEAGLETree("c");
		BEAGLETree node4 = new BEAGLETree("d");

		node1.children.add(node2);
		node1.children.add(node3);
		node2.children.add(node4);
		
		node1.show();
		
		double[] output = encode(node1);
		BEAGLETree nodeNew = decode(output);
		
		System.out.println();
		nodeNew.show();
	}
	
	public void show() {
		show("");
	}

	public void show(String depth) {
		System.out.println(depth + symbol);
		for(BEAGLETree child : children) {
			child.show(" " + depth);
		}
	}
	
	public static double[] encode(BEAGLETree tree) {
		return encode(tree, 0);
	}
	
	public static double[] encode(BEAGLETree tree, int level) {
		double[] ret = symbols.get(tree.symbol);
		
		if(ret == null) {
			ret = VectorTools.newGaussian(dim);
			symbols.put(tree.symbol, ret);
		}
		
		// Copy.
		ret = VectorTools.mult(ret, 1);

		if(level >= levelMarkers.size()) {
			levelMarkers.add(level, VectorTools.newGaussian(dim));
		}
		double[] levelVec = levelMarkers.get(level);
		
		double[] children = new double[dim];
		
		for(int i=0;i<=tree.children.size();i++) {
			
			if(i >= orderMarkers.size()) {
				orderMarkers.add(i, VectorTools.newGaussian(dim));
			}
			double[] order = orderMarkers.get(i);
			
			double[] child;
			if(i == tree.children.size()) {
				child = symbols.get(nullChild);
			} else {
				child = encode(tree.children.get(i), level + 1);
			}
			
			VectorTools.setAdd(VectorTools.convolve(child, order), children);
		}
		
		children = VectorTools.normalize(children);
		ret = VectorTools.getAdd(ret, VectorTools.convolve(levelVec, children));
		
		return ret;
	}
	
	public static BEAGLETree decode(double[] value) {
		return decode(value, 0);
	}
	
	public static BEAGLETree decode(double[] value, int level) {
		
		if(level >= levelMarkers.size()) {
			return null;
		}
		
		BEAGLETree root = new BEAGLETree();
		
		double bestSim = 999;
		Object bestObj = null;
		
		for(Object symbol : symbols.keySet()) {
			double[] symbolRep = symbols.get(symbol);
			double sim = VectorTools.getCosine(symbolRep, value);
			
			if(sim < bestSim) {
				bestSim = sim;
				bestObj = symbol;
			}
		}
		
		if(bestObj == nullChild) {
			return null;
		}
		
		double[] children = VectorTools.corelate(levelMarkers.get(level), value);
		
		for(int i=0;i<orderMarkers.size();i++) {
			double[] child = VectorTools.corelate(orderMarkers.get(i), children);
			BEAGLETree childTree = decode(child, level + 1);
			if(childTree != null) {
				root.children.add(childTree);
			} else {
				break;
			}
		}
		
		root.symbol = bestObj;
		
		return root;
	}
	
	
}

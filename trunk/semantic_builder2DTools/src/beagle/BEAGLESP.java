package beagle;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import admin.ModelData;
import admin.ModelManager;
import modelTools.GeonModel;
import modelTools.Primitive2D;
import modelTools.PrimitiveBase;
import comparison.tree2D.Comparator2DTreeBEAGLE;
import tools.ImageFrame;
import tools.VectorTools;
import creator2DTree.Model2DTree;
import creator2DTree.PrimitiveInstance2DTree;

public class BEAGLESP {

	public static void main(String[] args) {

		/*
		final Vector<ModelData> models = ModelManager.getAllModels(new File("1-25-2013.dat"));
		ModelManager.refineModels(models);
		Hashtable<String,Vector<ModelData>> modelSort = ModelManager.getAllWordModels(models);

		JFrame frame = new JFrame();
		frame.setSize(400,400);
		JTabbedPane tab = new JTabbedPane();
		frame.add(tab);

		for(ModelData model : modelSort.get("pumpkin")) {
			final BufferedImage image = model.model.thumbnail(null, 400, 400, 5);
			tab.add(new JPanel() {
				private static final long serialVersionUID = 1L;
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(image, 0, 0, this);
				}
			});
		}

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		/*
		
		double[] weights = {1.0,1.0,1.0,1.0,1.0,1.0};
		
		for(ModelData model : models) {
			Model2DTree modelTree = (Model2DTree)model.model;
			encodeInstance(modelTree.root, weights);
		}

		Model2DTree modelTree = (Model2DTree)models.get(0).model;
		double[] encoded = encodeInstance(modelTree.root, weights);
		
		PrimitiveInstance2DTree decoded = decodeInstance(encoded, weights);	
		*/
		
		Comparator2DTreeBEAGLE.init();
		
		PrimitiveInstance2DTree root = new PrimitiveInstance2DTree(Primitive2D.shapes.get("circle"));
		root.color = Color.BLACK;
		Model2DTree model = new Model2DTree();
		model.root = root;
		ImageFrame.makeFrame(model.thumbnail((BufferedImage)null, 400, 400, 10));
		
		
		double[] weights = {1,1,1,1,1,1,1,1};
		
		PrimitiveInstance2DTree root2 = decodeInstance(encodeInstance(root, weights), weights);
		Model2DTree model2 = new Model2DTree();
		model2.root = root2;
		ImageFrame.makeFrame(model2.thumbnail((BufferedImage)null, 400, 400, 10));
		
	}
	public static double[] encodeInstance(PrimitiveInstance2DTree instance) {
		double[] weights = {1.54582184401584794E18,-1.46883378664609434E18,-1.95336694189284813E18,2.4129212585283405E18,-3.0464443467093407E18,7.748341568089777E10};
		return encodeInstance(instance, weights);
	}
	
	public static PrimitiveInstance2DTree decodeInstance(double[] encoded, double[] weights) {
		
		// shape
		PrimitiveBase bestShape = null;
		double bestValue = 9999;
		
		for(PrimitiveBase shape : Comparator2DTreeBEAGLE.shapeVectors.keySet()) {
			double[] vec = Comparator2DTreeBEAGLE.shapeVectors.get(shape);
			double dist = VectorTools.getCosine(vec, encoded);
			if(dist < bestValue) {
				dist = bestValue;
				bestShape = shape;
			}
		}
		
		PrimitiveInstance2DTree instance = new PrimitiveInstance2DTree((Primitive2D)bestShape);

		// color
		bestValue = 9999;
		for(Color color : Comparator2DTreeBEAGLE.colorVectors.keySet()) {
			double[] vec = Comparator2DTreeBEAGLE.colorVectors.get(color);
			double dist = VectorTools.getCosine(vec, encoded);
			if(dist < bestValue) {
				dist = bestValue;
				instance.color = color;
			}
		}

		/*
		// rotation
		bestValue = 9999;
		for(Color color : Comparator2DTreeBEAGLE.colorVectors.keySet()) {
			double[] vec = Comparator2DTreeBEAGLE.colorVectors.get(color);
			double dist = VectorTools.getCosine(vec, encoded);
			if(dist < bestValue) {
				dist = bestValue;
				instance.color = color;
			}
		}

		// scale
		bestValue = 9999;
		for(Color color : Comparator2DTreeBEAGLE.colorVectors.keySet()) {
			double[] vec = Comparator2DTreeBEAGLE.colorVectors.get(color);
			double dist = VectorTools.getCosine(vec, encoded);
			if(dist < bestValue) {
				dist = bestValue;
				instance.color = color;
			}
		}
		*/
			
		return instance;
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

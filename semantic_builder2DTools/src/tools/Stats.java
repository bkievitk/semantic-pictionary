package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import comparison.ComparisonManager;
import creator2DTree.Model2DTree;

import admin.ModelData;
import admin.ModelManager;

import shapes3D.*;
import my3D.*;

public class Stats {

	public static void main(String[] args) {
		
		Vector<ModelData> models = ModelManager.getAllModels(new File("9-24-13.dat"));
		ModelManager.refineModels(models);

        Hashtable<String,Vector<ModelData>> modelsWordGrouped = ModelManager.getAllWordModels(models);
        
		Color backgroundColor = new Color(250,250,250);
		
		String word = "car";
		
		show3DHistogram(histogram(modelsWordGrouped.get(word), 5, backgroundColor));
		ComparisonManager.showModels(modelsWordGrouped.get(word), "");
	}

	public static int[][][] histogram(Vector<ModelData> models, int bins, Color backgroundColor) {
		int[][][] histogram = new int[bins][bins][bins];
		
		for(ModelData model : models) {
			int[][][] partialHistogram = histogram(model, bins, backgroundColor);
			for(int r=0;r<histogram.length;r++) {
				for(int g=0;g<histogram.length;g++) {
					for(int b=0;b<histogram.length;b++) {
						histogram[r][g][b] += partialHistogram[r][g][b];
					}
				}	
			}
		}
		
		return histogram;
	}
	
	public static int[][][] histogram(ModelData model, int bins) {
		return histogram(model, bins, new Color(250,250,250));
	}
	
	public static int[][][] histogram(Model2DTree model, int bins) {
		return histogram(model, bins, new Color(250,250,250));
	}
	
	public static int[][][] histogram(ModelData model, int bins, Color backgroundColor) {
		return histogram((Model2DTree)model.model, bins, backgroundColor);
	}
	
	public static int[][][] histogram(Model2DTree selectedModelTree, int bins, Color backgroundColor) {
				
		BufferedImage background = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics g = background.getGraphics();
		g.setColor(backgroundColor);
		g.fillRect(0, 0, background.getWidth(), background.getHeight());

		BufferedImage img = selectedModelTree.thumbnail(background, background.getWidth(), background.getHeight(), 5);
		
		return histogram(img, bins, backgroundColor);
	}
	
	public static int[][][] histogram(BufferedImage image, int bins, Color backgroundColor) {
		int[][][] histogram = new int[bins][bins][bins];
		for(int x=0;x<image.getWidth();x++) {
			for(int y=0;y<image.getHeight();y++) {
				Color c = new Color(image.getRGB(x, y));
				if(!c.equals(backgroundColor)) {
					int redBin = Math.min(bins - 1, c.getRed() * bins / 255);
					int greenBin = Math.min(bins - 1, c.getGreen() * bins / 255);
					int blueBin = Math.min(bins - 1, c.getBlue() * bins / 255);				
					histogram[redBin][greenBin][blueBin] += 1;
				}
			}
		}
		return histogram;
	}
	
	public static void show3DHistogram(int[][][] histogram) {
		show3DHistogram(histogram, null);
	}
	
	public static void show3DHistogram(int[][][] histogram, String name) {
		JPanel3DChangeObject panel = new JPanel3DChangeObject(null);
		
		if(name != null) {
			panel.setBorder(BorderFactory.createTitledBorder(name));
		}
		
		int max = 0;
		for(int r=0;r<histogram.length;r++) {
			for(int g=0;g<histogram.length;g++) {
				for(int b=0;b<histogram.length;b++) {
					max = Math.max(max, histogram[r][g][b]);
				}
			}
		}
		
		int bins = histogram.length;
		panel.u.root = new NullObject3D();
		for(int r=0;r<histogram.length;r++) {
			for(int g=0;g<histogram.length;g++) {
				for(int b=0;b<histogram.length;b++) {
					
					double radius = histogram[r][g][b] / (double)max;
					double[] center = {r - (bins - 1) / 2.0, g - (bins - 1) / 2.0, b - (bins - 1) / 2.0};
					panel.u.root.children.add(new Cube3D(center, radius, new Material(new Color(r * 255 / bins, g * 255 / bins, b * 255 / bins), null, null)));
					
				}
			}
		}
				
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 400);
		frame.add(panel);
		frame.setVisible(true);
	}
}

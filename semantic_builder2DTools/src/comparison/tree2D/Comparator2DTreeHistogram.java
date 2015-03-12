package comparison.tree2D;

import integration.HistogramIntegration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.imageio.ImageIO;

import relations.beagle.VectorTools;
import tools.Stats;
import creator2DTree.Model2DTree;

public class Comparator2DTreeHistogram implements Comparator2DTree {

	private int size = 100;
	private double scale = 5;
	private static Color background = new Color(250,250,250);
	
	public Hashtable<String,double[]> histograms = new Hashtable<String,double[]>();
	
	public String description() {
		return "Histogram";
	}
	
	public BufferedImage toImage(Model2DTree m) {		
		BufferedImage backgroundImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = backgroundImage.getGraphics();
		g.setColor(background);
		g.fillRect(0, 0, size, size);
		return m.thumbnail(backgroundImage, size, size, scale);
	}
	
	@Override
	public double similarity(Model2DTree m1, Model2DTree m2, double[] weights) {
		
		double[] a = histograms.get(m1.toReduced());
		if(a == null) {
			a = HistogramIntegration.toVector(Stats.histogram(m1, 5));
			histograms.put(m1.toReduced(), a);
		}
		
		double[] b = histograms.get(m2.toReduced());
		if(b == null) {
			b = HistogramIntegration.toVector(Stats.histogram(m2, 5));
			histograms.put(m2.toReduced(), b);
		}
	
		return VectorTools.getCosine(a, b);
	}

}

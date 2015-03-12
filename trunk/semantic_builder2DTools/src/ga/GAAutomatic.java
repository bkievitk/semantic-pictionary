package ga;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GAAutomatic extends JPanel {

	private static final long serialVersionUID = 2172513608722831762L;
	public Genome[] models;
	public BufferedImage target;
	public static final Random random = new Random();
	public int generation = 0;
	
	public static void main(String[] args) {
		BufferedImage img;
		try {
			
			img = ImageIO.read(new File("pic.jpg"));
			GAAutomatic ga = new GAAutomatic(img);
			
			JFrame frame = new JFrame();
			frame.add(ga);
			frame.setSize(1000,400);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
			
			
			while(true) {
				ga.nextGeneration();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public GAAutomatic(BufferedImage target) {
		this.target = target;
		
		models = new Genome[10];
		for(int i=0;i<10;i++) {
			Genome g = new Genome();
			g.score = g.computeSimilarity(target);
			models[i] = g;
		}
		Arrays.sort(models);
		
	}
	
	public void nextGeneration() {
		Genome[] newModels = new Genome[models.length];
		for(int i=0;i<2;i++) {
			newModels[i] = models[i];
		}
		for(int i=2;i<models.length;i++) {
			Genome p1 = selectParent(3);
			Genome p2 = selectParent(3);
			newModels[i] = new Genome(p1,p2);
			newModels[i].score = newModels[i].computeSimilarity(target);
		}
		Arrays.sort(newModels);
		models = newModels;
		generation++;
		if(generation % 10 == 0) {
			repaint();
		}
	}
	
	public Genome selectParent(int opponents) {
		int best = random.nextInt(models.length);
		for(int i=1;i<opponents;i++) {
			int newBest = random.nextInt(models.length);
			if(best > newBest) {
				best = newBest;
			}
		}
		return models[best];
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int width = this.getWidth() / (models.length + 1);
		g.drawImage(target, 0, 0, target.getWidth()/2, target.getHeight()/2, this);
		
		for(int i=0;i<models.length;i++) {			
			int x = this.getWidth() * (i + 1) / models.length;
			BufferedImage img = models[i].model.thumbnail(Color.WHITE, width, this.getHeight(),5);
			g.drawImage(img,x, 0, null);
			g.setColor(Color.BLACK);
			g.drawString(models[i].score+"", x + 10, 20);
		}
	}
}

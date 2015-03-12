package tools;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;

import javax.swing.*;

public class ImageFrame extends JPanel {
	private static final long serialVersionUID = -4821072852458221265L;
	public BufferedImage image;
	
	public ImageFrame(BufferedImage image) {
		this.image = image;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this);
	}
	
	public static void makeFrame(BufferedImage image){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(image.getWidth(), image.getHeight() + 40);
		frame.add(new ImageFrame(image));
		frame.setVisible(true);
	}
	
	public static void makeFrame(BufferedImage[] images, int x, int y, String label){
		
		int width = 0;
		int height = 0;
		
		JPanel grid = new JPanel(new GridLayout(x,y));
		for(BufferedImage image : images) {
			grid.add(new ImageFrame(image));
			width = Math.max(width, image.getWidth());
			height = Math.max(height, image.getHeight());
		}
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(width * x, height * y);
		Container c = frame.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(grid, BorderLayout.CENTER);
		c.add(new JLabel(label), BorderLayout.NORTH);
		
		frame.setVisible(true);
	}
}

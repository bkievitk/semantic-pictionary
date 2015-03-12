package ga;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;

import modelTools.Primitive2D;
import creator2DTree.*;

public class Genome extends JPanel implements Comparable<Genome> {

	private static final long serialVersionUID = -7900708223647364213L;
	public Model2DTree model;
	public double score;	
	public static Random random = new Random();

	public Genome() {
		model = new Model2DTree();
		PrimitiveInstance2DTree root = model.root;
		root.shape = randShape();
		buildRand(root,0);
		this.setBackground(Color.WHITE);
	}
	
	public Genome(Genome p1, Genome p2) {
		model = new Model2DTree();
		PrimitiveInstance2DTree root = p1.model.root.cloneRecursive();
		model.root = root;
		permutation(root);
	}
	
	public PrimitiveInstance2DTree getBranch() {
		return getBranch(model.root,0);
	}
	
	public PrimitiveInstance2DTree getBranch(PrimitiveInstance2DTree root, int depth) {
		if(root.children.size() <= 0) {
			return root;
		}
				
		int id = random.nextInt(root.children.size());		
		PrimitiveInstance2DTree next = root.children.get(id).child;
		
		if(random.nextInt(5-depth) == 0) {
			return next;
		} else {
			return getBranch(next,depth+1);
		}
	}
	
	public void permutation(PrimitiveInstance2DTree root) {
		int threshold = 10;
		
		if(random.nextInt(threshold) == 0) {
			root.color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
		}
		if(random.nextInt(threshold) == 0) {
			root.rotation[0] = random.nextInt(600);
		}
		if(random.nextInt(threshold) == 0) {
			root.scale[0] = random.nextInt(300) + 50;
		}
		if(random.nextInt(threshold) == 0) {
			root.scale[1] = random.nextInt(300) + 50;
		}
		if(random.nextInt(threshold) == 0) {
			root.shape = randShape();
		}
		
		if(random.nextInt(10) == 0) {
			buildRand(root, 2);
		}
		
		if(random.nextInt(10) == 0) {
			root.children.clear();
		}
		
		/*
		if(random.nextInt(10) == 0) {
			PrimitiveInstance2DTree child = getBranch().cloneRecursive();
			PrimitiveInstance2DConnection connection = new PrimitiveInstance2DConnection(child,root);
			connection.childAttachmentPoint[0] = (short)(random.nextInt(3)-1);
			connection.childAttachmentPoint[1] = (short)(random.nextInt(3)-1);
			connection.parentAttachmentPoint[0] = (short)(random.nextInt(3)-1);
			connection.parentAttachmentPoint[1] = (short)(random.nextInt(3)-1);
			child.parent = connection;
			root.children.add(connection);
		}
		*/
		
		for(PrimitiveInstance2DConnection child : root.children) {
			if(random.nextInt(threshold) == 0) {
				child.childAttachmentPoint[0] = (short)(random.nextInt(3)-1);
			}
			if(random.nextInt(threshold) == 0) {
				child.childAttachmentPoint[1] = (short)(random.nextInt(3)-1);
			}
			if(random.nextInt(threshold) == 0) {
				child.parentAttachmentPoint[0] = (short)(random.nextInt(3)-1);
			}
			if(random.nextInt(threshold) == 0) {
				child.parentAttachmentPoint[1] = (short)(random.nextInt(3)-1);
			}
			permutation(child.child);
		}
	}
	
	public Primitive2D randShape() {
		return Primitive2D.shapesVec.get(random.nextInt(Primitive2D.shapesVec.size()));
	}
	
	public void buildRand(PrimitiveInstance2DTree root, int depth) {
		root.color = new Color(random.nextInt(255),random.nextInt(255),random.nextInt(255));
		root.rotation[0] = random.nextInt(600);
		root.scale[0] = random.nextInt(300) + 50;
		root.scale[1] = random.nextInt(300) + 50;
		
		// Build children.
		for(int i=0;i<random.nextInt(3)+2;i++) {
			if(random.nextInt(depth+1) == 0) {
				PrimitiveInstance2DTree child = new PrimitiveInstance2DTree(randShape());
				PrimitiveInstance2DConnection connection = new PrimitiveInstance2DConnection(child,root);
				connection.childAttachmentPoint[0] = (short)(random.nextInt(3)-1);
				connection.childAttachmentPoint[1] = (short)(random.nextInt(3)-1);
				connection.parentAttachmentPoint[0] = (short)(random.nextInt(3)-1);
				connection.parentAttachmentPoint[1] = (short)(random.nextInt(3)-1);
				child.parent = connection;
				root.children.add(connection);
				buildRand(child,depth+1);
			}
		}
	}
		
	public double computeSimilarity(BufferedImage imgCmp) {
		BufferedImage img = new BufferedImage(imgCmp.getWidth(),imgCmp.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics g2 = img.getGraphics();
		g2.setColor(new Color(0,0,0));
		g2.fillRect(0,0,this.getWidth(),this.getHeight());				
		model.thumbnail(img,this.getWidth(), this.getHeight(),5);
		
		long dist = 0;
		for(int x=0;x<img.getWidth();x+=3) {
			for(int y=0;y<img.getHeight();y+=3) {
				Color c1 = new Color(imgCmp.getRGB(x, y));
				Color c2 = new Color(img.getRGB(x, y));
				
				dist += Math.abs(c1.getRed() - c2.getRed()) +
						Math.abs(c1.getGreen() - c2.getGreen()) +
						Math.abs(c1.getBlue() - c2.getBlue());
			}
		}
		
		return dist;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage img = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics g2 = img.getGraphics();
		g2.setColor(new Color(0,0,0));
		g2.fillRect(0,0,this.getWidth(),this.getHeight());				
		g.drawImage(model.thumbnail(img,this.getWidth(), this.getHeight(),5),0,0,this);
	}

	public int compareTo(Genome o) {
		if(o.score < score) {
			return 1;
		} else {
			return -1;
		}
	}
}

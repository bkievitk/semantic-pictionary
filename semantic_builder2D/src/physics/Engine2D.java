package physics;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JPanel;
import creator2DTree.Model2DTree;

public class Engine2D {
	private Vector<Model2DTree> models = new Vector<Model2DTree>();
	private AffineTransform toView = new AffineTransform();

	public static void main(String[] args) {
		final Engine2D engine = new Engine2D();

		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 7868270107635197940L;
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				engine.render(g);
			}
		};
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		frame.add(panel);
		frame.setVisible(true);
		
		Model2DTree model = new Model2DTree();
		model.fromReduced("[128,128,128,1,50,151,0,-1[128,128,128,0,50,84,0,-1,1,-1,-1,1][128,128,128,0,50,89,0,-1,-1,-1,0,1]]");
		engine.models.add(model);
	}
	
	public Engine2D() {
		toView.scale(10, 10);
		toView.translate(10, 10);
	}
	
	public void render(Graphics g) {
		for(Model2DTree model : models) {
			System.out.println(toView);
			model.root.render(g, toView, null, false);
		}
	}
	
	public void tick() {
	}
	
}

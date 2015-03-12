package my3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import modelTools.GeonModel;

import shapes3D.*;
import templates.WindowRender;


public abstract class JPanel3D extends WindowRender implements MouseListener, MouseMotionListener, ComponentListener, KeyListener {
	
	private static final long serialVersionUID = 767985053073837254L;
	
	// The 3D data.
	public Universe u = new Universe();

	// Track mouse movement.
	protected Point mouseDown = null;
	
	// Image first rendered to this.
	protected BufferedImage img = new BufferedImage(500,500, BufferedImage.TYPE_INT_RGB);
	public int renderType = Universe.RENDER_NORMAL;
		
	public static BufferedImage renderObject(Object3D p) {
		BufferedImage background = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
		Graphics g = background.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0,0,50,50);
		
		BufferedImage img = new BufferedImage(50,50,BufferedImage.TYPE_INT_RGB);
		Universe u = new Universe();
		u.background = background;
		u.root = p;

		u.view = TransformMy3D.translate(0,0,-Object3D.SCREEN_DISTANCE - 10);
		u.view.combine(TransformMy3D.rotateX(-.5));
		u.view.combine(TransformMy3D.rotateZ(.1));
		u.view.combine(TransformMy3D.stretch(4,4,4));
		
		//u.view = TransformMy3D.translate(img.getWidth() / 2, img.getHeight() / 2, 0);
		//u.view.combine(TransformMy3D.stretch(17,17,17));
		//u.view.combine(TransformMy3D.rotateX(-.5));
		//u.view.combine(TransformMy3D.rotateZ(.1));
		u.render(img,Universe.RENDER_NORMAL);
		return img;
	}
	
	/**
	 * This builds a test scene.
	 * @return Returns the test scene.
	 */
	public static Object3D buildTestPerspective() {
		
		Object3D p = new Object3D();

		// Load example image.
		/*BufferedImage img = null;
		try {
			img = ImageIO.read(new File("einstein.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		/*
		double[] sphereCenter = {2,0,0};
		p.children.add(new Sphere3D(sphereCenter, 2, 20, 20, new Material(Color.RED, "Sphere", null)));
		
		double[] boxCenter1 = {0,-2,0};
		p.children.add(new Cube3D(boxCenter1, 1, new Material(Color.BLUE,"Box1", null)));
		
		double[] boxCenter2 = {0,2,0};
		p.children.add(new Cube3D(boxCenter2, 1, new Material(Color.BLUE,"Box2",null)));
		*/
		double[] cylinderCenter = {0,0,0};
		p.children.add(new Handle3D(cylinderCenter, 1, 2, 10, new Material(Color.ORANGE,"Cylinder", null)));
		/*
		double[] coneCenter = {0,0,2};
		Cone3D cone3D = new Cone3D(coneCenter, 1, 2, 20, new Material(Color.PINK,"Cone", null));
		cone3D.transform = TransformMy3D.rotateX(Math.PI / 2); 
		p.children.add(cone3D);
		*/
		
		return p;
	}
	
	public JPanel3D(GeonModel model) {
		super(model);
		// Use the test perspective.
		setupPanel(buildTestPerspective());
	}
	
	//public JPanel3D(Object3D p) {
	//	// Use the given perspective.
	//	setupPanel(p);
	//}
	
	public void setupPanel(Object3D p) {

		// Add listeners.
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addComponentListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		
		// Set perspective.
		u.root = p;
				
		// This performs the render.
		setView();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Render to image and then to screen.
		u.render(img,renderType);
		g.drawImage(img,0,0,this);
	}

	public void mouseClicked(MouseEvent arg0) {
		
		// Make sure the object buffer exists.
		if(u.objBuffer != null) {
			
			// Retrieve the object from the object buffer.
			Object clicked = u.objBuffer[arg0.getX()][arg0.getY()];
			System.out.println(clicked);
		}
	}

	public void mousePressed(MouseEvent arg0) {
		
		// This is the active object.
		this.requestFocus();
		
		// Track that the mouse button was pressed.
		mouseDown = arg0.getPoint();
	}

	/**
	 * Render given the current location and rotation.
	 */
	public abstract void setView();

	public void componentResized(ComponentEvent arg0) {
		// If window has changed, then resize the bitmap and rebuild.
		int width = Math.max(getWidth(), 50);
		int height = Math.max(getHeight(), 50);
		img = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);
		setView();
	}
	
	public void mouseMoved(MouseEvent arg0) {}
	public void componentHidden(ComponentEvent arg0) {} 
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}
	public void keyReleased(KeyEvent arg0) {}
	public void keyTyped(KeyEvent arg0) {}
	
}

package creator2DTree;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import modelTools.PrimitiveInstance;

import templates.WindowRender;
import tools.MatchPoint;

import action.tree2D.Model2DTreeActionMove;
import action.tree2D.Model2DTreeActionTreeConnection;

public class WindowRender2DTree extends WindowRender implements MouseListener, MouseMotionListener {
	
	private static final long serialVersionUID = -8390088585992796840L;
	
	public Model2DTree model;
	private BufferedImage background;
	public AffineTransform rootTransform;
	public boolean renderFunctionalLabels = false;
	
	public WindowRender2DTree(Model2DTree model) {
		super(model);
		this.model = model;
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Create background.
		if(background == null || getWidth() != background.getWidth() || getHeight() != background.getHeight()) {
			background = makeBackground(getWidth(),getHeight());
		}
		g.drawImage(background, 0, 0, this);

		// Create root transform to be centered and scaled by 20.
		rootTransform = new AffineTransform();
		rootTransform.translate(getWidth()/2,getHeight()/2);
		rootTransform.scale(20, 20);
		
		// Only render if root is not null.
		if(model.root != null) {
			
			rootTransform.rotate(model.root.rotation[0]/100.0);
			
			// Render.
			model.root.render(g, rootTransform,model.getSelected(),dragged);
			
			// Render dragged.
			if(dragged && model.getSelected() != null) {
				
				// Render the moving chunk.
				model.getSelected().renderChunk(g, px, py);
				
				Point2D[][] controlPoints = model.getSelected().getControlPoints(px, py);
				MatchPoint mp = model.root.findBestMatchPoint(model.getSelected(),controlPoints);
				
				// If match found.
				if(mp != null) {
					
					// Draw line to indicate the best match.
					Point2D child = mp.connection.parent.controlPoints[mp.connection.parentAttachmentPoint[0]+1][mp.connection.parentAttachmentPoint[1]+1];
					Point2D parent = controlPoints[mp.connection.childAttachmentPoint[0]+1][mp.connection.childAttachmentPoint[1]+1];
					g.setColor(Color.BLACK);
					g.drawLine((int)child.getX(), (int)child.getY(), (int)parent.getX(), (int)parent.getY());
				}
			}
		}
		
		if(renderFunctionalLabels) {
			g.setColor(Color.RED);
			model.root.renderFunctionalLabels(g);
		}
	}
	
	public void mouseClicked(MouseEvent arg0) {
		if(model.root != null) {
			PrimitiveInstance2DTree active = model.root.getClicked(arg0.getX(), arg0.getY());			
			model.setSelected(active);
			repaint();
		}
	}	
	
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	
	public void mouseReleased(MouseEvent arg0) {
		if(dragged) {
			
			Point2D[][] controlPoints = model.getSelected().getControlPoints(px, py);
			MatchPoint mp = model.root.findBestMatchPoint(model.getSelected(),controlPoints);
						
			if(model.getSelected().parent.parent != mp.connection.parent) {
				model.performAction(new Model2DTreeActionMove(model.getSelected(),mp.connection.parent));
			}
			
			short[] newParent = mp.connection.parentAttachmentPoint;
			short[] newChild = mp.connection.childAttachmentPoint;
			
			model.performAction(new Model2DTreeActionTreeConnection(newChild,newParent,model.getSelected().parent));

			dragged = false; 
			repaint();
		}
	}

	int px = 0;
	int py = 0;
	boolean dragged = false;
	
	public void mouseDragged(MouseEvent arg0) {
		if(model.getSelected() != null && model.getSelected() != model.root && model.getSelected().isEditable(PrimitiveInstance.EDIT_MOVE)) {
			px = arg0.getX();
			py = arg0.getY();
			dragged = true;
			repaint();
		}
	}

	public void mouseMoved(MouseEvent arg0) {
	}
}

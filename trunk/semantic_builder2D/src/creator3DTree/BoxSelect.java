package creator3DTree;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;
import my3D.JPanel3DChangeObject;
import my3D.TransformMy3D;

import action.tree3D.Model3DActionConnection;


/**
 * This panel allows the user to select where on the box to make the attachment point.
 * @author brentkk
 */

public class BoxSelect extends JPanel implements MouseListener, ChangeListener {

	private static final long serialVersionUID = -3361362198371595655L;
	
	// This is the attachment point that is currently selected.
	private short[] selected;
	
	// Model.
	private GeonModel model;
	
	// The attachment point in question.
	private ObjectAttachment attachment;
	
	// True if this represents the parent attachment.
	private boolean parent;
	
	// The new points that you can attach to.
	public Vector<double[]> points = new Vector<double[]>();

	/**
	 * Use the given transform to build the point set for a cube.
	 * @param t
	 */
	public void buildPoints(TransformMy3D t) {
		points = new Vector<double[]>();
		
		// Build points.
		for(int x=-1;x<=1;x++) {
			for(int y=-1;y<=1;y++) {
				for(int z=-1;z<=1;z++) {
					double[] p = {x,y,z};
					points.add(p);
				}
			}
		}
		
		// Apply transform.
		points = t.apply(points);
	}

	/**
	 * Given an attachment, set selected.
	 * @param attachment
	 */
	public void setAttachment(ObjectAttachment attachment) {
		this.attachment = attachment;

		// Make sure you have an attachment specified.
		if(attachment != null) {
			if(parent) {
				// Use the parent or child attachment.
				selected = attachment.parentAttachmentPoint;
			} else {
				selected = attachment.childAttachmentPoint;
			}
		} else {
			selected = null;
		}
	}
	
	/**
	 * Set the model, initial attachment point and permentantly set as parent or child.
	 * @param model
	 * @param attachment
	 * @param parent
	 */
	public BoxSelect(GeonModel model, ObjectAttachment attachment, boolean parent) {

		// Set the size.
		this.setPreferredSize(new Dimension(75,75));
		
		// Copy values.
		this.model = model;
		this.attachment = attachment;
		this.parent = parent;

		// Set the attachment points.
		setAttachment(attachment);
		
		// Listen for clicking on points.
		addMouseListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Show either child or parent label only if there are points to render.
		if(points.size() > 0) {
			g.setColor(Color.WHITE);
			if(parent) {
				g.drawString("parent", 20, 10);
			} else {
				g.drawString("child", 25, 10);
			}
		} else {
			// If no points, then no need to render any farther.
			g.drawString("root", 29, 10);
			g.drawString("node", 25, 22);
			return;
		}
		
		// This comparator compares the third element in the array (z value)
		Comparator<double[]> c = new Comparator<double[]>() {
			public int compare(double[] arg0, double[] arg1) {
				double diff = arg0[2] - arg1[2];
				if(diff < 0) {
					return -1;
				} else if(diff == 0) {
					return 0;
				} else {
					return 1;
				}
			}
		};

		// Create a copy.
		Vector<double[]> pts2 = new Vector<double[]>();
		for(double[] pt : points) {
			// We will add an indicator for selected.
			double[] pt2 = {pt[0],pt[1],pt[2],-1.0};
			pts2.add(pt2);
		}

		// Indicate this point as selected.
		pts2.get((selected[0]+1)*9+(selected[1]+1)*3+(selected[2]+1))[3] = 1.0;
		
		// Sort the farthest points back first.
		Collections.sort(pts2, c);

		// Render all points.
		for(double[] point : pts2) {
			
			// Color based on marker.
			if(point[3] > 0) { 
				g.setColor(Color.RED);
			} else {
				// Color based on distance.
				int color = (int)((point[2] + 25) * 3);
				color = Math.min(Math.max(0, color),255);
				g.setColor(new Color(color,color,color));
			}

			// Render center.
			g.fillOval((int)point[0]-2, (int)point[1]-2, 4, 4);

			// Render surrounding ring.
			g.setColor(Color.BLUE);
			g.drawOval((int)point[0]-2, (int)point[1]-2, 4, 4);			
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		
		// Check every point.
		for(int i=0;i<points.size();i++) {
			
			// Get coordinates.
			double[] point = points.get(i);
			int dx = (int)point[0] - arg0.getX();
			int dy = (int)point[1] - arg0.getY();

			// If clicked closer than 3 pixels.
			if(dx * dx + dy * dy <= 9) {

				// Parse x,y,z values.
				int x = i/9;
				int y = (i%9)/3;
				int z = i%3;
				
				short[] newParent;
				short[] newChild;
				
				// Build a new parent and child connection.
				if(parent) {
					newParent = new short[3];
					newParent[0] = (short)(x-1);
					newParent[1] = (short)(y-1);
					newParent[2] = (short)(z-1);
					newChild = attachment.childAttachmentPoint;
				} else {
					newChild = new short[3];
					newChild[0] = (short)(x-1);
					newChild[1] = (short)(y-1);
					newChild[2] = (short)(z-1);
					newParent = attachment.parentAttachmentPoint;
				}

				// Apply the new connection.
				model.performAction(new Model3DActionConnection(newChild, newParent, attachment));
					
				// Already found point. Quit.
				return;
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {}
	public void mouseReleased(MouseEvent arg0) {}

	public void stateChanged(ChangeEvent arg0) {
		
		// The source is a change panel and we need the rotation values.
		JPanel3DChangeObject co = (JPanel3DChangeObject)arg0.getSource();
		
		TransformMy3D transform = TransformMy3D.translate(37.5, 37.5, 0);
		transform.combine(TransformMy3D.stretch(15, 15, 15));
		transform.combine(TransformMy3D.rotateX(co.tX));
		transform.combine(TransformMy3D.rotateY(co.tY));
		
		if(attachment != null) {
			if(parent) {
				// Build transform to represent this object.
				transform.combine(attachment.parent.getOrientationOf());
				buildPoints(transform);
			} else {
				// Build transform to represent this object.
				transform.combine(attachment.child.getOrientationOf());
				buildPoints(transform);
			}
		} else {
			// No points, quit.
			points = new Vector<double[]>();
		}
		repaint();
	}
	
}

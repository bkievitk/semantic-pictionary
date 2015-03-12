package my3D;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;


public class JPanel3DChangeObject extends JPanel3D {

	private static final long serialVersionUID = 8260415992743063710L;
	
	public double tX = 0;	// Rotation around x (left, right)
	public double tY = 0;	// Rotation around y (up, down)

	private Vector<ChangeListener> viewChange = new Vector<ChangeListener>();
		
	public JPanel3DChangeObject(GeonModel model) {
		super(model);
	}
	
	public void addViewChangeListener(ChangeListener cl) {
		viewChange.add(cl);
	}
	
	public static void main(String[] args) {
		// Create frame.
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000,700);
		frame.add(new JPanel3DChangeObject(null));
		frame.setVisible(true);
	}
	
	public void setView() {
		u.view = TransformMy3D.translate(0,0,-Object3D.SCREEN_DISTANCE - 10);
		u.view.combine(TransformMy3D.rotateX(tX));
		u.view.combine(TransformMy3D.rotateY(tY));		
		
		if(viewChange != null) {
			for(ChangeListener cl : viewChange) {
				ChangeEvent ce = new ChangeEvent(this);
				cl.stateChanged(ce);
			}
		}
		repaint();
	}

	public void mouseDragged(MouseEvent arg0) {
		int dx = mouseDown.x - arg0.getX();
		int dy = mouseDown.y - arg0.getY();
		if(dx * dx + dy * dy > 4) {
			tX += .01 * dy;
			tY -= .01 * dx;
			mouseDown = arg0.getPoint();
			setView();
		}
	}

	public void keyPressed(KeyEvent arg0) {
		
		// Change render type.
		switch(arg0.getKeyChar()) {
			case '1': renderType = Universe.RENDER_WIREFRAME; break;
			case '2': renderType = Universe.RENDER_NORMAL; break;
			case '3': renderType = Universe.RENDER_DEAPTH; break;
			case '4': renderType = Universe.RENDER_FLAT; break;
		}
		setView();
	}

}

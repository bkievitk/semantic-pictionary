package creator2DNoTree;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import modelTools.Primitive2D;

import templates.WindowRender;

import action.noTree2D.Model2DNoTreeActionAdd;
import action.noTree2D.Model2DNoTreeActionMove;


public class WindowRender2DNoTree extends WindowRender implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -6507665899343026513L;
	private Model2DNoTree model;
	private BufferedImage background;
	
	public WindowRender2DNoTree(Model2DNoTree model) {
		super(model);
		
		this.model = model;
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(background == null || getWidth() != background.getWidth() || getHeight() != background.getHeight()) {
			background = makeBackground(getWidth(),getHeight());
		}
		
		g.drawImage(background, 0, 0, this);
		
		for(PrimitiveInstance2DNoTree object : model.objects) {
			object.render(g, model.getSelected());
		}
	}

	public void mouseClicked(MouseEvent arg0) {
		
		// If an item is selected to add to the world.
		Primitive2D selected = model.selectedToMake;
		if(selected != null) {
			PrimitiveInstance2DNoTree newInstance = new PrimitiveInstance2DNoTree(selected,arg0.getX(),arg0.getY());
			model.performAction(new Model2DNoTreeActionAdd(newInstance));			
		} else {
			for(PrimitiveInstance2DNoTree instance : model.objects) {
				if(instance.getClicked(arg0.getX(), arg0.getY()) != null) {
					model.setSelected(instance);
					break;
				}
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

	public void mouseDragged(MouseEvent arg0) {
		if(model.getSelected() != null) {
			// There is something to drag.
			int[] translate = {arg0.getX(),arg0.getY()};
			model.performAction(new Model2DNoTreeActionMove(translate,model.getSelected()));
		}
	}

	public void mouseMoved(MouseEvent arg0) {
	}
}

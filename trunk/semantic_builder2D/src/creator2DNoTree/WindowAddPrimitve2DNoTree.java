package creator2DNoTree;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;

import modelTools.Primitive2D;

import templates.WindowAddPrimitive;


/**
 * This panel is used to add new primitive instances to the scene.
 * Instances are added to the active object.
 * @author bkievitk
 *
 */
public class WindowAddPrimitve2DNoTree extends WindowAddPrimitive {
	
	private static final long serialVersionUID = -4609532875916623274L;
	
	private Vector<JButton> buttons = new Vector<JButton>();
	private Model2DNoTree model;
		
	public WindowAddPrimitve2DNoTree(Model2DNoTree modelIn) {
		super(modelIn);
		
		model = modelIn;
		
		setLayout(new GridLayout(2,4));
		
		for(Primitive2D shape : Primitive2D.shapes.values()) {
			
			final JButton button = new JButton(shape.img);
			button.setDisabledIcon(shape.disabled);
			
			buttons.add(button);
			add(button);
			
			final Primitive2D finalShape = shape;
			
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					//model.performAction(new Model2DActionAdd(finalShape,model.getSelected()));
					model.setSelected(null);
					
					if(model.selectedToMake == null) {
						model.selectedToMake = finalShape;
						setEnabledState(button);
					} else {
						model.selectedToMake = null;
						setEnabledState(null);
					}
				}
			});			
		}
		
	}
		
	public void setEnabledState(JButton button) {
		// Check if an object is selected then turn on or off all buttons.
		for(JButton b : buttons) {
			if(button == b || button == null) {
				b.setEnabled(true);
			} else {
				b.setEnabled(false);
			}
		}
	}
}
package creator3DTree;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.Primitive3D;

import templates.CreatorPanel;
import templates.WindowPrimitiveEdit;

import action.tree3D.*;


public class WindowPrimitiveEdit3DTree extends WindowPrimitiveEdit {

	private static final long serialVersionUID = -7747594275918411650L;

	public Model3DTree model;
	public JButton delete;
		
	public JSlider xSize;
	public JSlider ySize;
	public JSlider zSize;
	public JSlider xRotation;
	public JSlider yRotation;
	public JSlider zRotation;
	
	public Vector<JButton> buttons = new Vector<JButton>();

	private static int SCALE_STEP = 50;
	private static int ROTATE_STEP = 100;
	private static Hashtable<Integer, JComponent> scales = buildSlider(50,400,SCALE_STEP,"%");
	private static Hashtable<Integer, JComponent> rotations = buildSlider(0,629,ROTATE_STEP,"");
	
	public WindowPrimitiveEdit3DTree(Model3DTree modelIn) {
		
		super(modelIn);
		
		model = modelIn;

	    model.addUpdateListener(this);
	    model.addSelectListener(this);
		
		this.setLayout(new BorderLayout());
				
		xSize = new JSlider(50,400);
		xSize.setLabelTable(scales);
		xSize.setPaintLabels(true);
		xSize.setMajorTickSpacing(SCALE_STEP);
		xSize.setMinorTickSpacing(1);
		xSize.setSnapToTicks(false);
		components.add(xSize);

		ySize = new JSlider(50,400);
		ySize.setLabelTable(scales);
		ySize.setPaintLabels(true);
		ySize.setMajorTickSpacing(SCALE_STEP);
		ySize.setMinorTickSpacing(1);
		ySize.setSnapToTicks(false);
		components.add(ySize);	

		zSize = new JSlider(50,400);
		zSize.setLabelTable(scales);
		zSize.setPaintLabels(true);
		zSize.setMajorTickSpacing(SCALE_STEP);
		zSize.setMinorTickSpacing(1);
		zSize.setSnapToTicks(false);
		components.add(zSize);	

		xRotation = new JSlider(0,629);
		xRotation.setLabelTable(rotations);
		xRotation.setPaintLabels(true);
		xRotation.setMajorTickSpacing(ROTATE_STEP);
		xRotation.setMinorTickSpacing(1);
		xRotation.setSnapToTicks(false);
		components.add(xRotation);
		
		yRotation = new JSlider(0,629);
		yRotation.setLabelTable(rotations);
		yRotation.setPaintLabels(true);
		yRotation.setMajorTickSpacing(ROTATE_STEP);
		yRotation.setMinorTickSpacing(1);
		yRotation.setSnapToTicks(false);		
		components.add(yRotation);
		
		zRotation = new JSlider(0,629);
		zRotation.setLabelTable(rotations);
		zRotation.setPaintLabels(true);
		zRotation.setMajorTickSpacing(ROTATE_STEP);
		zRotation.setMinorTickSpacing(1);
		zRotation.setSnapToTicks(false);
		components.add(zRotation);
				
		Box changeSizeRotation = Box.createVerticalBox();
		this.add(changeSizeRotation,BorderLayout.CENTER);
		
		changeSizeRotation.add(CreatorPanel.labeledPanel(xSize,"xSize"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(ySize,"ySize"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(zSize,"zSize"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(xRotation,"xRotation"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(yRotation,"yRotation"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(zRotation,"zRotation"));
		changeSizeRotation.add(colorSelect);
		
		JPanel changeType = new JPanel(new GridLayout(2,4));
		this.add(changeType,BorderLayout.NORTH);
		
		
		for(Primitive3D shape : Primitive3D.shapes.values()) {
			
			JButton button = new JButton(shape.img);
			button.setDisabledIcon(shape.disabled);
			button.setName(shape.name);
			
			changeType.add(button);
			components.add(button);
			buttons.add(button);
		}
		
		
		delete = new JButton("Delete");
		changeType.add(delete);
		components.add(delete);
		
		setEnabledState();
		
		xSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionScale(xSize.getValue(),0,model.getSelected()));
					disableSetVals = false;
				}
			}
		});

		ySize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionScale(ySize.getValue(),1,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		zSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionScale(zSize.getValue(),2,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		xRotation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionRotation(xRotation.getValue(),0,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		yRotation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionRotation(yRotation.getValue(),1,model.getSelected()));
					disableSetVals = false;
				}
			}
		});
		
		zRotation.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if(!disableSelect) {
					disableSetVals = true;
					model.performAction(new Model3DActionRotation(zRotation.getValue(),2,model.getSelected()));
					disableSetVals = false;
				}
			}
		});

		colorSelect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				model.performAction(new Model3DActionColor(colorSelect.getColor(),model.getSelected()));
			}
		});
		
		
		for(int buttonID = 0; buttonID < Primitive3D.shapes.size();buttonID++) {
			final Primitive3D finalShape = Primitive3D.shapes.get(buttons.get(buttonID).getName());	
			
			buttons.get(buttonID).addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					model.performAction(new Model3DActionType(finalShape.id,model.getSelected()));
				}
			});	
		}
		
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(model.getSelected() != null && model.getSelected() != model.root) {
					model.performAction(new Model3DActionDelete(model.getSelected().parent));
					model.setSelected(null);
				}
			}
		});
		
	}
			
	protected void setVals() {
		xSize.setValue(model.getSelected().scale[0]);
		ySize.setValue(model.getSelected().scale[1]);
		zSize.setValue(model.getSelected().scale[2]);
		xRotation.setValue(model.getSelected().rotation[0]);
		yRotation.setValue(model.getSelected().rotation[1]);
		zRotation.setValue(model.getSelected().rotation[2]);
	}
}

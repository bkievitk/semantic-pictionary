package templates;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;

import modelTools.GeonModel;
import modelTools.Primitive2D;
import modelTools.PrimitiveInstance;


public abstract class WindowPrimitiveEdit2D extends WindowPrimitiveEdit implements ActionListener, ChangeListener {

	private static final long serialVersionUID = -7747594275918411650L;

	public GeonModel model;
	public JButton delete;
		
	public JSlider xSize;
	public JSlider ySize;
	public JSlider rotation;

	private static int SCALE_STEP = 50;
	private static int ROTATE_STEP = 100;
	private static Hashtable<Integer, JComponent> scales = buildSlider(50,400,SCALE_STEP,"%");
	private static Hashtable<Integer, JComponent> rotations = buildSlider(0,629,ROTATE_STEP,"");
	
	protected Vector<JButton> buttons = new Vector<JButton>();
	public JPanel changeType;
	
	public void setEnabledState() {
		// Check if an object is selected then turn on or off all buttons.
		PrimitiveInstance instance = model.getSelected();
		boolean setVal = instance != null;
		for(JComponent component : components) {

			if(!setVal) {
				component.setEnabled(false);
			} else {
			
				boolean thisSetVal = setVal;
				
				if(!instance.isEditable(PrimitiveInstance.EDIT_COLOR)) {
					if(component == colorSelect) {
						thisSetVal = false;
					}
				}
				if(!instance.isEditable(PrimitiveInstance.EDIT_ROTATION)) {
					if(component == rotation) {
						thisSetVal = false;
					}
				}
				if(!instance.isEditable(PrimitiveInstance.EDIT_SCALE)) {
					if(component == xSize || component == ySize) {
						thisSetVal = false;
					}
				}
				if(!instance.isEditable(PrimitiveInstance.EDIT_SHAPE)) {
					if(component == changeType) {
						thisSetVal = false;
					}
				}
				component.setEnabled(thisSetVal);
			}
		}
	}
	
	public WindowPrimitiveEdit2D(GeonModel modelIn) {
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
		
		rotation = new JSlider(0,629);
		rotation.setLabelTable(rotations);
		rotation.setPaintLabels(true);
		rotation.setMajorTickSpacing(ROTATE_STEP);
		rotation.setMinorTickSpacing(1);
		rotation.setSnapToTicks(false);
		components.add(rotation);
				
		Box changeSizeRotation = Box.createVerticalBox();
		this.add(changeSizeRotation,BorderLayout.CENTER);
		
		
		JPanel tmp = CreatorPanel.labeledPanel(xSize,"xSize");
		//Log.v("","");
		
		changeSizeRotation.add(tmp);
		changeSizeRotation.add(CreatorPanel.labeledPanel(ySize,"ySize"));
		changeSizeRotation.add(CreatorPanel.labeledPanel(rotation,"rotation"));
		changeSizeRotation.add(colorSelect);
		
		changeType = new JPanel(new GridLayout(2,4));
		this.add(changeType,BorderLayout.NORTH);
		
		for(Primitive2D shape : Primitive2D.shapes.values()) {
			
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
	}
			
	protected void setVals() {
		xSize.setValue(model.getSelected().scale[0]);
		ySize.setValue(model.getSelected().scale[1]);
		rotation.setValue(model.getSelected().rotation[0]);
	}

}

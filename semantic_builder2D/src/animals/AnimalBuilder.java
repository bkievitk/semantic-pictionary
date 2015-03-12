package animals;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import modelTools.Primitive2D;
import modelTools.PrimitiveInstance;

import templates.CreatorPanel;

import creator2DTree.*;

public class AnimalBuilder extends JPanel {

	private static final long serialVersionUID = -4163528944607495291L;
	
	// This is used to rig the model.
	public KeyAssigner assigner;
	
	// This is the model.
	public Animal animal;
	public AnimalController controller;
	
	// This is used to prevent infinite event firing.
	public boolean disableListener = false;
	
	public AnimalBuilder(boolean fullAccess) {

		boolean withDefault = false;
		
		if(withDefault) {
			// A default scary monster :)
			String animalString = "[255,0,0,-84,100,100,4,-5[128,128,128,0,200,100,0,-31,-1,-1,1,-1[192,192,192,0,260,75,0,-46,-1,0,1,0,e[192,192,192,0,74,76,0,-31,-1,1,1,1[128,128,128,214,60,56,4,-47,0,0,0,0,t[255,0,0,0,63,155,1,-31,0,1,0,-1,s[255,0,0,563,51,100,1,-64,0,1,0,1,s][255,0,0,61,55,105,1,-64,0,1,0,1,s]]]]]]]:0,0,0";
			String controllerString = "wasd:q,0,t:e,0,f";
			Animal animal = Animal.decodeString(animalString, null);
			AnimalController controller = new AnimalController(animal);
			controller.decodeString(controllerString);
			init(fullAccess,animal,controller);
		} else {		
			Model2DTree model = new Model2DTree();
			Animal animal = new Animal(model);
			AnimalController controller = new AnimalController(animal);
			PrimitiveInstance2DTree body = new PrimitiveInstance2DTree(Primitive2D.shapes.get("circle"));
			body.color = Color.RED;
			body.setEditable(PrimitiveInstance.EDIT_COLOR, false);
			animal.model.root = body;
			init(fullAccess,animal,controller);
		}
	}
	
	public AnimalBuilder(boolean fullAccess, final Animal animal) {
		init(fullAccess,animal,new AnimalController(animal));
	}
	
	private void init(boolean fullAccess, final Animal animal, final AnimalController controller) {
				
		setLayout(new BorderLayout());	
		setFocusable(true);
		requestFocus();
		
		// Build the body of the model.
		this.animal = animal;
		this.controller = controller;

		// Build the render window and add to the center.
		WindowRender2DTree render = new WindowRender2DTree(animal.model);
		add(render,BorderLayout.CENTER);	
				
		JTabbedPane sidePanel = new JTabbedPane();
		
		add(sidePanel,BorderLayout.EAST);
		
		// The build panel.
		JPanel buildPanel = new JPanel(new BorderLayout());		
		sidePanel.addTab("build", buildPanel);
		
		// The rigging panel.
		JPanel riggingPanel = new JPanel(new BorderLayout());		
		sidePanel.addTab("rigging", riggingPanel);

		// The io panel.
		Box ioPanel = Box.createVerticalBox();		
		sidePanel.addTab("io", ioPanel);
		
		if(fullAccess) {
			FunctionalPanel functionalPanel = new FunctionalPanel(animal.model,render);
			sidePanel.addTab("functional", functionalPanel);
		}
		
		JPanel tmpPanel = new JPanel(new BorderLayout());
		buildPanel.add(tmpPanel,BorderLayout.NORTH);	
		
		// Show the editor for primitives.
		WindowPrimitiveEdit2DTree primitiveEditor = new WindowPrimitiveEdit2DTree(animal.model);
		if(!fullAccess) {
			primitiveEditor.setPreferredSize(new Dimension(300,200));
		} else {
			primitiveEditor.setPreferredSize(new Dimension(300,300));
		}
		tmpPanel.add(primitiveEditor,BorderLayout.CENTER);	
		
		WindowAttachment2DTree attachment = new WindowAttachment2DTree(animal.model,false);
		attachment.setPreferredSize(new Dimension(300,100));
		tmpPanel.add(attachment,BorderLayout.SOUTH);
		

		// Show the parts window.
		PartsWindow partsAvailable = new PartsWindow(animal.model,PartsLoader.getParts());
		buildPanel.add(partsAvailable,BorderLayout.CENTER);
	
		// Show the key assignment window.
		assigner = new KeyAssigner(animal,controller);		
		riggingPanel.add(assigner,BorderLayout.NORTH);

		final JTextArea encoding = new JTextArea();
		ioPanel.add(CreatorPanel.labelAbovePanel(encoding,"Paste into sim to set this model."));
		encoding.setPreferredSize(new Dimension(200,50));
		
		JButton encodeAnimal = new JButton("encode");
		encodeAnimal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//System.out.println(animal.encodeString());
				//System.out.println(controller.encodeString());
				String text = "";
				text += "SET_YOU~" + animal.encodeString() + "~" + controller.encodeString() + "\r\n";
				text += "SET_NEW~" + animal.encodeString() + "~x~y\r\n";
				encoding.setText(text);
			}
		});
		ioPanel.add(encodeAnimal);
				
		// This determines if this is a full access editor.
		if(fullAccess) {
			
			// Add the "add geon" panel.
			buildPanel.add(new WindowAddPrimitive2DTree(animal.model),BorderLayout.SOUTH);
			
			// Adds the panel to set what is editable.
			JPanel editable = new JPanel(new FlowLayout());
			final JCheckBox[] editables = new JCheckBox[6];
			for(int i=0;i<editables.length;i++) {
				JCheckBox check = new JCheckBox();
				editables[i] = check;
				check.setEnabled(false);
				
				final int editableType = i;
				check.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						PrimitiveInstance2DTree selected = (PrimitiveInstance2DTree)animal.model.getSelected();
						if(selected != null) {
							selected.setEditable(editableType, editables[editableType].isSelected());
							animal.model.updateModel();
						}
					}				
				});	
			}
				
			// Listen to a change in editability.
			ChangeListener cl = new ChangeListener() {
				public void stateChanged(ChangeEvent e) {

					// On change, set all of the editables appropriately.
					PrimitiveInstance2DTree selected = (PrimitiveInstance2DTree)animal.model.getSelected();
					if(selected != null) {
						for(int i = 0;i<editables.length;i++) {
							editables[i].setSelected(selected.isEditable(i));
						}
					}

					// Either enable or disable.
					boolean val = selected != null;
					for(int i = 0;i<editables.length;i++) {
						editables[i].setEnabled(val);
					}
				}				
			};
			
			// Listen to changes in the model.
			animal.model.addUpdateListener(cl);
			animal.model.addSelectListener(cl);
			
		    // Add labeled to the editable window.
		    for(int i = 0;i<editables.length;i++) {
		    	editable.add(CreatorPanel.labeledPanel(editables[i], PrimitiveInstance.EDIT_NAMES[i]));
		    }
		    
		    // Add to the build panel.
			riggingPanel.add(editable,BorderLayout.SOUTH);
			
		} else {
			// Disable change of geon type.
			primitiveEditor.changeType.removeAll();	
		}
			
		CreatorPanel.recolor(this);		
		sidePanel.setForeground(Color.WHITE);
		sidePanel.setBackground(Color.BLACK);			
	}

}

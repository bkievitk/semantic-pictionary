package animals;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import creator2DTree.PrimitiveInstance2DTree;

import templates.CreatorPanel;


public class KeyAssigner extends JPanel {
	
	private static final long serialVersionUID = 3742811672476764265L;
		
	public AnimalController controller;
	
	public Animal animal;
	public JPanel newAssignment;
	public JPanel directions;
	public JPanel assignmentsShow;
	
	public JTextField forward;
	public JTextField left;
	public JTextField backward;
	public JTextField right;
	
	public JLabel message = new JLabel();
	
	public KeyAssigner(final Animal animal, final AnimalController controller) {
		this.controller = controller;
		this.animal = animal;

		buildNewAssignmentPanel();
		
		assignmentsShow = new JPanel();		
		setLayout(new BorderLayout());
		add(newAssignment,BorderLayout.WEST);
		add(assignmentsShow,BorderLayout.CENTER);
		add(directions,BorderLayout.SOUTH);
		
		final Timer timer = new Timer(AnimalWorld.TICK_TIME_MS, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				controller.tick();
				animal.tick();
				animal.model.updateModel();
			}
		});
		
		final JButton timerPause = new JButton("start");
		timerPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(timerPause.getText().equals("start")) {
					timerPause.setText("stop");
					timer.start();
				} else {
					timerPause.setText("start");
					timer.stop();
				}
			}			
		});
		add(timerPause,BorderLayout.NORTH);
		
		showAssignments();
		//this.setPreferredSize(new Dimension(500,400));
		
	}
	
	public void showAssignments() {
		assignmentsShow.removeAll();		
		assignmentsShow.setLayout(new GridLayout(controller.assignments.size(),1));		
		for(final KeyAssignment assignment : controller.assignments) {			
			JButton remove = new JButton("remove " + assignment);
			remove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					controller.assignments.remove(assignment);
					showAssignments();
				}				
			});
			assignmentsShow.add(remove);
		}				
		validate();
		repaint();
	}
	
	public boolean assigned(char c, boolean isPrimary) {
		for(KeyAssignment assn : controller.assignments) {
			if(assn.key == c) {
				return true;
			}
		}
		if(!isPrimary) {
			for(char key : controller.basicMovement) {
				if(key == c) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void setFieldColor(JTextField field, boolean isPrimary) {
		String val = field.getText();
		if(val.length() != 1) {
			field.setForeground(Color.RED);
		} else {
			if(assigned(val.charAt(0),isPrimary)) {
				field.setForeground(Color.ORANGE);
			} else {
				field.setForeground(Color.BLACK);
			}
		}
	}
	
	public JTextField buildCharEditor(String text, boolean myIsPrimary) {
		
		final boolean isPrimary = myIsPrimary;
		final JTextField c = new JTextField(text);	
		c.setPreferredSize(new Dimension(20,20));
		
		c.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {
				setFieldColor(c,isPrimary);
				c.setSelectionStart(0);
				c.setSelectionEnd(c.getText().length());
			}
			public void mouseReleased(MouseEvent e) {}
		});
		
		c.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				setFieldColor(c,isPrimary);
			}
			public void keyTyped(KeyEvent e) {}
		});
		
		return c;
	}
	
	public void buildNewAssignmentPanel() {
		
		newAssignment = new JPanel(new BorderLayout());
		
		final JTextField c1 = buildCharEditor("",false);
		final JTextField c2 = buildCharEditor("",false);
		
		JPanel chars = new JPanel(new BorderLayout());
		chars.add(CreatorPanel.labeledPanel(c1,"char (large/clockwise)  "),BorderLayout.NORTH);
		chars.add(CreatorPanel.labeledPanel(c2,"char (small/counter)      "),BorderLayout.SOUTH);		
		newAssignment.add(chars,BorderLayout.NORTH);
		
		JButton assign = new JButton("assign");
		
		newAssignment.add(assign,BorderLayout.EAST);
		JPanel types = new JPanel(new GridLayout(4,1));
		newAssignment.add(types,BorderLayout.CENTER);
		
		JButton testDrive = new JButton("Click to Test Rigging");
		newAssignment.add(testDrive,BorderLayout.SOUTH);
		testDrive.addKeyListener(controller);
				
		final JRadioButton rotation = new JRadioButton("rotation");
		types.add(rotation);
		final JRadioButton scaleX = new JRadioButton("scaleX");
		types.add(scaleX);
		final JRadioButton scaleY = new JRadioButton("scaleY");
		types.add(scaleY);
		final JRadioButton scale = new JRadioButton("scale");
		types.add(scale);
		
		final ButtonGroup group = new ButtonGroup();
		group.add(rotation);
		group.add(scaleX);
		group.add(scaleY);
		group.add(scale);
		
		assign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(c1.getText().length() == 1 && c2.getText().length() == 1 && animal.model.getSelected() != null) {
					
					char key1 = c1.getText().charAt(0);
					char key2 = c2.getText().charAt(0);
					
					PrimitiveInstance2DTree object = (PrimitiveInstance2DTree)animal.model.getSelected();
					int type = -1;
					if(rotation.isSelected()) {
						type = 0;
					} else if(scaleX.isSelected()) {
						type = 1;
					} else if(scaleY.isSelected()) {
						type = 2;
					} else if(scale.isSelected()) {
						type = 3;
					}
					
					if(type >= 0) {
						KeyAssignment assignment1 = new KeyAssignment(key1,object,type,animal.model,true);
						controller.assignments.add(assignment1);
						KeyAssignment assignment2 = new KeyAssignment(key2,object,type,animal.model,false);
						controller.assignments.add(assignment2);
						showAssignments();
						
						c1.setText("");
						c2.setText("");
					} else {
						System.out.println("Invalid assignment type.");
					}
				} else {
					System.out.println("invalid text length or object not selected.");
					
				}
			}			
		});
		
		directions = new JPanel(new BorderLayout());
		JPanel holder = new JPanel(new GridLayout(2,3));
		
		KeyListener primary = new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if(forward.getText().length() == 1) {
					controller.basicMovement[0] = forward.getText().charAt(0);
				}
				if(left.getText().length() == 1) {
					controller.basicMovement[1] = left.getText().charAt(0);
				}
				if(backward.getText().length() == 1) {
					controller.basicMovement[2] = backward.getText().charAt(0);
				}
				if(right.getText().length() == 1) {
					controller.basicMovement[3] = right.getText().charAt(0);
				}
			}
			public void keyTyped(KeyEvent e) {}			
		};
		
		forward = buildCharEditor("w",true);
		forward.addKeyListener(primary);
		controller.basicMovement[0] = 'w';
		
		left = buildCharEditor("a",true);
		left.addKeyListener(primary);
		controller.basicMovement[1] = 'a';
		
		backward = buildCharEditor("s",true);
		backward.addKeyListener(primary);
		controller.basicMovement[2] = 's';
		
		right = buildCharEditor("d",true);
		right.addKeyListener(primary);
		controller.basicMovement[3] = 'd';
		
		holder.add(new JLabel(""));
		holder.add(forward);
		holder.add(new JLabel(""));		

		holder.add(left);
		holder.add(backward);
		holder.add(right);
		holder = CreatorPanel.labelAbovePanel(holder,"Primary Movement");
		directions.add(holder,BorderLayout.WEST);
	}

}


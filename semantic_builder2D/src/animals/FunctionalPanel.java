package animals;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import creator2DTree.Model2DTree;
import creator2DTree.WindowRender2DTree;
import animals.functional.*;

public class FunctionalPanel extends JPanel {

	private static final long serialVersionUID = 1348117334571605880L;
	
	public Model2DTree model;
	public WindowRender2DTree render;
	
	public FunctionalPanel(Model2DTree model, final WindowRender2DTree render) {
		this.model = model;
		this.render = render;

		Box box = Box.createVerticalBox();
		add(box);
		
		final JCheckBox showLabels = new JCheckBox("show labels");
		showLabels.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				render.renderFunctionalLabels = showLabels.isSelected();
				repaint();
				render.repaint();
			}
		});
		
		box.add(showLabels);
		
		box.add(makeFunctionPanel(null));
		
		box.add(makeFunctionPanel(new FunctionalUnitStinger(null,null)));
		box.add(makeFunctionPanel(new FunctionalUnitTwirl(null,null)));
		box.add(makeFunctionPanel(new FunctionalTelescope(null,null)));
	}
	
	public JPanel makeFunctionPanel(final FunctionalUnit unit) {
		JPanel panel = new JPanel(new BorderLayout());

		String label;
		if(unit == null) {
			label = "   none";
		} else {
			label = "   " + unit.toString();
		}
		
		panel.add(new JLabel(label),BorderLayout.CENTER);
		JButton set = new JButton("set");
		panel.add(set,BorderLayout.WEST);
		set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(model.getSelected() != null) {
					if(unit == null) {
						model.getSelected().functional = null;
					} else {
						model.getSelected().functional = unit.clone();
					}
					repaint();
					render.repaint();
				}				
			}			
		});

		return panel;
	}
}

package creator3DTree;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class WindowPrimitiveChildrenEdit extends JPanel implements ChangeListener {
	
	private static final long serialVersionUID = 7038963876753614216L;
	
	private Box vertical;
	public WindowAddPrimitive3DTree add;
	private Model3DTree model;
	
	public WindowPrimitiveChildrenEdit(Model3DTree model) {
		this.model = model;
		
		vertical = Box.createVerticalBox();
		this.add(vertical);
		add = new WindowAddPrimitive3DTree(model);

		// Ask to be informed of changes.
	    model.addUpdateListener(this);
	    model.addSelectListener(this);
	}
	
	public void stateChanged(ChangeEvent e) {
		
		vertical.removeAll();
		
		if(model.getSelected() == null) {
			vertical.add(new JLabel("No object selected."));
		} else {
			vertical.add(new JLabel("Add child"));
			vertical.add(add);
		}
		
		this.revalidate();
		//CreatorPanel.recolor(this);
		this.repaint();	
	}
	
}

package creator3DTree;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

/**
 * Shows tree representation of the 3D object model.
 * @author bkievitk
 */

public class WindowTreeView extends JScrollPane implements TreeSelectionListener, ChangeListener {

	private static final long serialVersionUID = 3038634552637179967L;

	// This is the model that you are showing.
	public Model3DTree model;
	
	// Tree data.
	public JTree tree;
	public DefaultTreeModel treeModel;
	public DefaultMutableTreeNode rootNode;
	
	public WindowTreeView(Model3DTree model) {
		this.model = model;
		this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		this.setBackground(Color.BLACK);;
		
		// Build tree.
		tree = makeTree();		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(this);
		setViewportView(tree);
		setPreferredSize(new Dimension(100,150));
		
		// Ask to be informed of changes.
		model.addUpdateListener(this);
	}
	
	/**
	 * Check when tree item is changed.
	 */
	public void valueChanged(TreeSelectionEvent e) {
		   
		// Get selected item.
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();

		// If there was nothing selected.
		if (node == null) {
			return;
		}

		// If you selected an object.
		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof PrimitiveInstance3DTree) {
			
			// Set the selected object.
			PrimitiveInstance3DTree object = (PrimitiveInstance3DTree)nodeInfo;
			model.setSelected(object);
		}
	}

	public void stateChanged(ChangeEvent arg0) {
		// The model has been updated.
		
		// Create new root node.
		rootNode = new DefaultMutableTreeNode("Object");
		
		// Build the tree.
		model.buildFromRoot(rootNode);
		
		// Set as root node.
		treeModel.setRoot(rootNode);
	}
	
	public JTree makeTree() {
				
		// Build root node and model.
		rootNode = new DefaultMutableTreeNode("Object");
		treeModel = new DefaultTreeModel(rootNode);

		// Build the tree.
		model.buildFromRoot(rootNode);
		
		// Build the JTree.
	    JTree tree = new JTree(treeModel);
	    tree.setEditable(true);    
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(true);
		
	    return tree;
	}
}

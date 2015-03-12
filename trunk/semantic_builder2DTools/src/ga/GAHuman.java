package ga;

import java.awt.*;
import java.util.*;
import javax.swing.*;

public class GAHuman extends JPanel {
	
	private static final long serialVersionUID = 7115479882005770621L;
	
	public Vector<Genome> models = new Vector<Genome>();

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.add(new GAHuman());
		frame.setSize(600,600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public GAHuman() {
		setLayout(new BorderLayout());
		add(new JLabel("face"),BorderLayout.NORTH);
		JPanel options = new JPanel(new GridLayout(1,5));
		add(options,BorderLayout.CENTER);
		for(int i=0;i<5;i++) {
			Genome genome = new Genome();
			models.add(genome);
			options.add(genome);
		}		
	}
}

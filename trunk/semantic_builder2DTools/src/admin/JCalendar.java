package admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;

import javax.swing.*;

public class JCalendar extends JPanel {

	public Calendar c;
	private JTextField year;
	private JComboBox<String> month;
	private JComboBox<String> day;
	
	public JCalendar() {		
		
		String[] months = {"January","February","March","April","May","June","July","August","September","October","Noveber","December"};
		String[] days = new String[31];
		for(int i=0;i<days.length;i++) {
			days[i] = i + "";
		}
		
		c = new GregorianCalendar();		
		year = new JTextField(c.get(Calendar.YEAR) + "");
		month = new JComboBox<String>(months);
		month.setSelectedIndex(c.get(Calendar.MONTH));
		day = new JComboBox<String>(days);
		day.setSelectedIndex(c.get(Calendar.DAY_OF_MONTH));
		
		year.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {
				try {
					c.set(Calendar.YEAR, Integer.parseInt(year.getText()));
				} catch(NumberFormatException e) {}
			}
		});

		month.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				c.set(Calendar.MONTH, month.getSelectedIndex());
			}			
		});
		
		day.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				c.set(Calendar.DAY_OF_MONTH, day.getSelectedIndex());
			}			
		});	
		
		setLayout(new FlowLayout());
		add(month);
		add(day);
		add(year);
	}
}

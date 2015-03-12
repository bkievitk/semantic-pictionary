package animals;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;
import javax.swing.event.ChangeListener;

import creator2DTree.PrimitiveInstance2DTree;

public class AnimalWorld {
	public Vector<Animal> animals = new Vector<Animal>();
	public Semaphore animalList = new Semaphore(1);
	public static final int TICK_TIME_MS = 10;
	
	
	public PlayerPortal portal;
	
	
	public Vector<ChangeListener> changeListeners = new Vector<ChangeListener>();
	
	public Animal touching(PrimitiveInstance2DTree instance, Animal you) {		
		for(Animal animal : animals) {
			if(animal != you) {
				PrimitiveInstance2DTree touch = animal.model.root.touching(instance);
				if(touch != null) {
					return animal;
				}
			}
		}
		return null;
	}
	
	public AnimalWorld() {
		Timer t = new Timer(TICK_TIME_MS,new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					animalList.acquire();
					for(Animal animal : animals) {
						animal.tick();
					}
					animalList.release();
					
					for(ChangeListener cl : changeListeners) {
						cl.stateChanged(null);
					}					
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}				
			}
		});
		t.start();
	}
}

package animals.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import animals.Animal;

public class ClientViewerServer extends Thread {
	
	//private Vector<Animal> world;
	
	public ClientViewerServer(InputStream input, OutputStream output, Vector<Animal> world) {
		//this.world = world;
	}
}
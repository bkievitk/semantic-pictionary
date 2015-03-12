package animals.server;

import java.io.*;
import java.net.*;

public class IndividualConnection extends Thread {

	public static final int STATE_START = 0;
	public static final int STATE_GET_CREDENTIALS = 1;
	public static final int STATE_TMP = 2;

	public static final int TIME_READER_STEP_MS = 20;

	public WorldServer server;
	public Socket socket;
	public InputStream input;
	public OutputStream output;
	
	public String name;
	
	public int state = STATE_START;
	
	public void run() {
		while(true) {
			if(socket != null) {
				switch(state) {
					case STATE_START:
						state = STATE_GET_CREDENTIALS;
					break;
					case STATE_GET_CREDENTIALS:
						
						// Size of name field.
						byte[] size = new byte[1];
						if(!timedReader(size, 600000)) { // 10 minutes.
							returnThread();
							break;
						}
						
						// Get name.
						byte[] nameArray = new byte[size[0]];
						if(!timedReader(nameArray, 1000)) { // 1 second.
							returnThread();
							break;
						}
						
						// Size of password field.
						if(!timedReader(size, 1000)) { // 1 second.
							returnThread();
							break;
						}
						
						// Get password.
						byte[] passwordArray = new byte[size[0]];
						if(!timedReader(passwordArray, 1000)) { // 1 second.
							returnThread();
							break;
						}
						
						String name = new String(nameArray);
						//String password = new String(passwordArray);
						
						// Confirm name and password.
						boolean confirmed = true;
						
						if(confirmed) {
							state = STATE_TMP;
							this.name = name;
						}
						
					break;
				}
			}
		}
	}
	
	public boolean timedReader(byte[] b, long timeLimitMS) {
		long timeMS = 0;
		try {
			while(input.available() < b.length) {
				try {
					Thread.sleep(TIME_READER_STEP_MS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				timeMS += TIME_READER_STEP_MS;
				if(timeMS >= timeLimitMS) {
					return false;
				}
			}
			input.read(b);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean returnThread() {
		try {
			state = STATE_START;
			
			socket.close();
			socket = null;
			
			input.close();
			input = null;
			
			output.close();
			output = null;
			
			name = null;
			
			server.threadLock.acquire();
			server.availableThreads.add(this);
			server.threadLock.release();
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void initialize(Socket socket) {
		try {
			this.socket = socket;
			input = socket.getInputStream();
			output = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public IndividualConnection(WorldServer server) {
		this.server = server;
	}
}

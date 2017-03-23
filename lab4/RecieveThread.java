
import java.net.Socket;
import java.io.*;
public class RecieveThread extends Thread {
	Socket com;
	BufferedReader in;
	Inventory it;
	public RecieveThread(Socket cSocket, Inventory iv){
		 this.com = cSocket;
		 this.it = iv;
	}
	
	public void run() {
		while(true) {
			System.out.println("Started RecieveThread");
			try {
				in = new BufferedReader(new InputStreamReader(this.com.getInputStream()));
				String in_str;
				if ((in_str = in.readLine()) != null){
					if (in_str.equals("Client\n")){
						System.out.println("Entering Client mode");
				    	new TCPServerThread(com, it).start();
				    	break;
					} else if (in_str.equals("Server")){
						//new SThread(com).start();
						break;
					}
				}
			} catch (IOException e) {e.printStackTrace();}
		}
	}
}
import java.net.DatagramSocket;
import java.net.Socket;
import java.io.*;
public class TCPServerThread extends Thread {
	BufferedReader in;
	Socket s;
	DataOutputStream out;
	DatagramSocket ds;
	Inventory iv;
	int udpPort;
	
	public TCPServerThread(Socket s, DatagramSocket ds, Inventory iv, int udpPort) {
		this.s = s;
		this.ds = ds;
		this.iv = iv;
		this.udpPort = udpPort;
	}
	
	public void run() {
		while(true){
			try {
				in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			    out = new DataOutputStream(this.s.getOutputStream());
				String client_str;
			    if ((client_str = in.readLine()) != null) {
					String[] commands = {"purchase", "cancel", "search", "list"};
			    	String request[] = client_str.split(" ");
			    	for (int i = 0; i < request.length; i++) {
						request[i] = request[i].replaceAll("\\P{Print}", "");
					}
			        String reply = null;
			        if(request[0].equals("setmode")){
			        	if(request[0].equals("T")){
			        		//call tcp thread
			        	} else {
			        		UDP_Thread ut = new UDP_Thread(ds, iv);
			        		ut.start();
			        		out.writeInt(udpPort);
			        		break;
			        	}
			        }
			        else if (request[0].equals(commands[0])) {
			        	System.out.println("Success 0");
						reply = new String(iv.purchase(request));
					} else if (request[0].equals(commands[1])){
						reply = new String(iv.cancel(Integer.parseInt(request[1])));
					} else if (request[0].equals(commands[2])){
						reply = new String(iv.search(request[1]));
					} else if (request[0].equals(commands[3])){
						reply = new String(iv.list());
					}
			        out.writeUTF(reply + "\n");
			    }
			}catch (IOException e){e.printStackTrace();}
		}
		try {
			s.close();
		}catch (IOException e){e.printStackTrace();}
	}

}

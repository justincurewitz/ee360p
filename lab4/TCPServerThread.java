//package lab4;

import java.net.Socket;
import java.io.*;

public class TCPServerThread extends Thread {
	BufferedReader in;
	Socket s;
	DataOutputStream out;
	Inventory iv;
	
	public TCPServerThread(Socket s, Inventory iv) {
		this.s = s;
		this.iv = iv;
		try {
			in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			out = new DataOutputStream(this.s.getOutputStream());
		}catch(IOException e){e.printStackTrace();}
	}
	
	public void run() {
		System.out.println("TCP Thread Started");
		String c_str = "Client";
		while(true){
			try {
				String in_str = null;
				if((in_str=in.readLine()) != null){
					in_str = in_str.substring(1);
					in_str = in_str.trim();
					System.out.println(in_str);
					System.out.println(in_str.equals(c_str));
					System.out.println(in_str.length());
					if(in_str.equals(c_str)){
						System.out.println("");
						Client();
						break;
					} else if (in_str.equals("Server")){
						Server();
					}
				}
			}catch (IOException e){e.printStackTrace();}
		}
	}
	
	private void Client() {
		System.out.println("Started Client Function");
		while(true){
			try {
				
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
//			        		UDP_Thread ut = new UDP_Thread(ds, iv);
//			        		ut.start();
//			        		out.writeInt(udpPort);
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
	
	private void Server() {
		System.out.println("Server Contact");
	}

}

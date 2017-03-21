
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class UDP_Thread extends Thread{
	DatagramSocket ds = null;
	BufferedReader in = null;
	Inventory iv;
	
	public UDP_Thread(DatagramSocket ds, Inventory iv){
		super();
		this.iv = iv;
		this.ds = ds;
	}
	
	public void run() {
		//System.out.println("New UDP Thread Started");
		String[] commands = {"purchase", "cancel", "search", "list"};
		while(true) {
			try {
				byte[] buf = new byte[1024];
				//receive request
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				ds.receive(dp);
				//do transaction
				char [] data = new char[buf.length];
				for (int i = 0; i < buf.length; i++){
					data[i] = (char)buf[i];
				}
				String[] request = new String(buf, StandardCharsets.UTF_8).split(" ");
				for (int i = 0; i < request.length; i++) {
					request[i] = request[i].replaceAll("\\P{Print}", "");
				}
				//access inventory
				if(request[0].equals("setmode")){
					if(request[1].equals("T")){
						break;
					}
				}
				if (request[0].equals(commands[0])) {
					buf = iv.purchase(request);
					//System.out.println("Success0");
				} else if (request[0].equals(commands[1])){
					buf = iv.cancel(Integer.parseInt(request[1]));
					//System.out.println("Success1");
				} else if (request[0].equals(commands[2])){
					buf = iv.search(request[1]);
					//System.out.println("Success2");
				} else if (request[0].equals(commands[3])){
					buf = iv.list();
				}
				//respond
				InetAddress address = dp.getAddress();
				int port = dp.getPort();
				dp = new DatagramPacket(buf, buf.length, address, port);
				ds.send(dp);
				//break;
			}catch (IOException e){e.printStackTrace();}
		}
		//ds.close();
	}
}

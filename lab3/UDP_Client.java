import java.io.*;
import java.net.*;

public class UDP_Client extends Thread {
	private String command;
	DatagramSocket ds;
	int port;
	InetAddress address;
	int recPort;
	
	public UDP_Client(String command, int port, InetAddress address, int recPort) {
		this.command = command;
		this.port = port;
		this.address = address;
		this.recPort = recPort;
	}
	
	public void run() {
		try{
			ds = new DatagramSocket(port);
			byte[] buf = command.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, address, recPort);
			ds.send(dp);
			buf = new byte[256];
			ds.receive(dp);
			System.out.println(buf[0]);
			
		} catch(IOException e){e.printStackTrace();}
	}
}

//package lab4;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class test_thread extends Thread {
	DatagramSocket ds = null;
	
	public test_thread(int port){
		super();
		try {
			ds = new DatagramSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("FUCK2");
		}
	}
	public void run() {
		String[] commands = {"purchase", "cancel", "search", "list"};
		int i = 0;
		while(true){
			try {
				byte[] buf = commands[i].getBytes(StandardCharsets.UTF_8);
				InetAddress address = InetAddress.getLocalHost();
				DatagramPacket dp = new DatagramPacket(buf, buf.length, address, 6790);
				ds.send(dp);
				//ds.receive(dp);
				//System.out.println("Test Received");
				//System.out.println((char)buf[0]);
				i = (i+1)%4;
				//break;
			}catch (IOException e){e.printStackTrace();}
		}
	}
}

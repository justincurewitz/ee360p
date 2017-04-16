import java.util.*;
import java.net.*;
import java.io.*;

public class Connector {
	
	public byte[] dataIn;
	public ObjectOutputStream[] dataOut;
	MulticastSocket ms;
	
	public Connector(MulticastSocket multisoc){
		ms = multisoc;
	}
	public void connect(InetAddress group) throws Exception {
		ms.joinGroup(group);
	}
	public void broadcastMessage(String msg,InetAddress group, int port){
		DatagramPacket packet = new DatagramPacket(msg.getBytes(),msg.length(),group,port);
		try {
			ms.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public byte[] receiveMessage(){
		dataIn = new byte[10000];
		DatagramPacket recv = new DatagramPacket(dataIn, dataIn.length);
		try {
			ms.receive(recv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataIn;
	}
	public void leaveGroup(InetAddress group){
		try {
			ms.leaveGroup(group);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void closeSockets() {
		try {
			ms.close();
		} catch (Exception e) { System.err.println(e); }
	}
}

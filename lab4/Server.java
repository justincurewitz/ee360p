/*
		Justin Curewitz jmc6579
		Kristian Wang kw26434
*/
//package lab4;

import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class Server {
  InetAddress ip_address;
  String ip_string;
  int port_number;
  public Server(InetAddress ip_address, int port_number){
	  this.ip_address = ip_address;
	  this.port_number = port_number;
  }
  public Server(String ip_string, int port_number){
	InetAddress addr;
	try {
		addr = InetAddress.getByName(ip_string);
		this.ip_address = addr;
		this.ip_string = ip_string;
		this.port_number = port_number;
	} catch (UnknownHostException e) {
		e.printStackTrace();
	}
	  
  }
  public String toString(){
	  return ip_string + ":" + port_number;
  }
  
  public static void main (String[] args) throws Exception{
	ArrayList<Integer> ServerPorts = new ArrayList<Integer>();
	ArrayList<String> ServerIPs = new ArrayList<String>();
	Integer myPort;
	int numOrders;
    ArrayList<User> users = new ArrayList<User>();
    
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter Configuration Data");
    int myID = sc.nextInt();
    int numServer = sc.nextInt();
    String inventoryPath = sc.next();
    System.out.println("Enter " + numServer + " IPs");
    for(int i = 1; i <= numServer; i++){
    	String ip = sc.next();
    	String[] ips = ip.split(":");
    	System.out.println(ips.length);
		ServerIPs.add(ips[0]);
    	ServerPorts.add(Integer.parseInt(ips[1]));
    }
    //debug
//    System.out.println("my ip: " + ServerIPs.get(myID-1));
//    System.out.println("my port " + ServerPorts.get(myID-1));
    Inventory it = new Inventory(inventoryPath);
	ServerSocket ss = new ServerSocket(ServerPorts.get(myID-1));
    while(true){
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection");
    	new TCPServerThread(cSocket, it).start();
    }
  }
}

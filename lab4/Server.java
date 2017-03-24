/*
		Justin Curewitz jmc6579
		Kristian Wang kw26434
*/
//package lab4;

import java.util.Scanner;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


public class Server  {
  
  /*
   * Variables for Server
   * */
  InetAddress ip_address;
  String ip_string;
  int port_number;
  static ArrayList<Server> server_list = new ArrayList<Server>();
  private ArrayList<RemoteInventory> inventories = new ArrayList<RemoteInventory>();
   // each Server object has a reference to the server it creates
  static ArrayList<Integer> ServerPorts = new ArrayList<Integer>();
  static ArrayList<String> ServerIPs = new ArrayList<String>();
  static Inventory it;
  static ServerSocket ss;
  int myId;
  
  
  /**
   * This server constructor takes in 
   * @param InetAddress ip_address: the ip_address 
   * @param int port_number: the portnumber
   * */
@SuppressWarnings("unchecked")
public Server(InetAddress ip_address, int port_number, Linker initComm){
	  // Initialization for our code
	  this.ip_address = ip_address;
	  this.port_number = port_number;
	  
  }
  /**
   * This server constructor takes in an
   * @param String ip_string: the string representation of the IP from console
   * @param int port_number: the port number from console
   * */
 @SuppressWarnings( "unchecked")
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
	//System.setProperties(java.rmi.server.hostname, "127.0.0.1");
	//This block instantiates the remote object
	
	  
  }
  /*
   * This function gets called whenever a new Server is created
   * 
   * */
  public static int init() throws IOException{
	  Scanner sc = new Scanner(System.in);
	  System.out.println("Enter server-id: ");
	  System.out.println("server id n should indicate the nth server");
	  int tempId = sc.nextInt();
	  tempId = 1;
	  System.out.println("Enter number of servers n:");
	  int tempN = sc.nextInt();
	  tempN = 1;
	  System.out.println("Please enter the filepath as: topologyi.txt where i is your server-id");
	  String inventoryPath = sc.next();
	  inventoryPath = "topology1.txt";
	  String topologyi = inventoryPath;
	  System.out.println("Enter " + tempN + " IPs");
	  System.out.println("example format: 127.0.0.1:8000");
	  Inventory iv = new Inventory("inventory.txt");
		try {
			RI ri = new RI(iv, tempId);
			String ri_name = "Remote"+ tempId;
			Registry rg = LocateRegistry.createRegistry(1099);
			rg.bind(ri_name, ri);
			System.out.println(ri_name);
		}catch (Exception e) {
			System.out.println("Failed to create remote inventory");
			e.printStackTrace();
		}
	    for(int i = 1; i <= tempN; i++){
	    	String ip = sc.next();
	    	ip = "127.0.0.1:8000";
	    	try{
	      	    PrintWriter writer = new PrintWriter(topologyi, "UTF-8");
	      	    writer.println(tempId);
	      	    writer.close();
	      	  } catch (IOException e) {
	      	   e.printStackTrace();
	      	  }
	    	String[] ips = ip.split(":");
	    	System.out.println(ips.length);
			ServerIPs.add(ips[0]);
	    	ServerPorts.add(Integer.parseInt(ips[1]));
	    	Server s = new Server(ips[0],Integer.parseInt(ips[1]));
	    	if(i == tempId){
	    		s.myId = tempId; // now every server should be assigned an ID
	    	}
	    	server_list.add(s); 
	    }
	  it = new Inventory(inventoryPath);
      ss = new ServerSocket(ServerPorts.get(0)); // only get the first element right now
      return tempId;
  }
  
  public String toString(){
	  return ip_string + ":" + port_number;
  }
  
  public static void main (String[] args) throws Exception{
	int generatedServerID = init();
    /*Attempting to receive new connection*/
    while(true){
    	System.out.println("Awaiting new connection request");
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection at port:" + cSocket.getPort());
    	new TCPServerThread(cSocket,generatedServerID, it,server_list).start();
    }
  }
  
  
  
  public boolean getRemoteInventory(int id){
	  String ri_name = "Remote"+id;
	  try {
		  Registry rg = LocateRegistry.getRegistry();
	  	  RemoteInventory ri = (RemoteInventory)rg.lookup(ri_name);
	  	  if (ri.isValid()){
	  		  System.out.println(ri.testFunc());
	  		  return true;
	  	  }
	  } catch (Exception e){}
  	  return false;

  }

  
 
}

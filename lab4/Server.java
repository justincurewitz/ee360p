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


public class Server {
  /*
   * Variables for Lamport
   * */
//  int numAcks;
//  LamportClock c;
//  int myId;
//  /*
//   * Variables for Server
//   * */
//  InetAddress ip_address;
//  String ip_string;
//  int port_number;
//  Inventory iv;
//  //public  ArrayList<Server> server_list = new ArrayList<Server>();
  static int registryPort = 2000;
  public int myID;
  public  ArrayList<RemoteInventory> inventories;// = new ArrayList<RemoteInventory>();
   // each Server object has a reference to the server it creates
  public ArrayList<Integer> ServerPorts;// = new ArrayList<Integer>();
  public ArrayList<String> ServerIPs;// = new ArrayList<String>();
  public Inventory it;
  public ServerSocket ss;
  
	
 
	

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
  Linker linker;
  
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
	  System.out.println("Please enter the inventory filepath:");
	  String inventoryPath = sc.next();
	  Inventory iv = new Inventory(inventoryPath);
	  String topologyi = "topology" + tempId + ".txt";
	  System.out.println("Enter " + tempN + " IPs");
	  System.out.println("example format: 127.0.0.1:8000");
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
	    
        
         
	  for(int i = 1; i <= numServer; i++){
		String ip = sc.next();
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
	  }
	  //create inventory and Remote Inventory
	  it = new Inventory(inventoryPath);
	  RI ri = null;
	  try {
		  ri = new RI(it, myID);
		  String ri_name = "Remote"+myID;
		  Registry rg = null;
		  rg = LocateRegistry.createRegistry(registryPort + 2*myID);
		  rg.bind(ri_name, ri);
		  System.out.println(ri_name);
	  }catch (Exception e) {
		  System.out.println("Failed to create remote inventory");
		  e.printStackTrace();
	  }
	  
	  inventories = new ArrayList<RemoteInventory>();
	  inventories.add(ri);
	  getAllInventories(numServer);
	  ss = null;
	  try {
		  ss = new ServerSocket(ServerPorts.get(myID));
	  } catch(IOException e) {System.out.println("failed to create server socket");}
        
        
        
        
	    	Server s = new Server(ips[0],Integer.parseInt(ips[1]));
	    	if(i == tempId){
	    		s.myId = tempId; // now every server should be assigned an ID
	    	}
	    	server_list.add(s); 
	    }
	    
	    for(int i = 0; i < server_list.size(); i++){
	    	if(i == tempId){
	    		Server s = server_list.get(i);
	    		try {
					s.linker = new Linker(s.ip_string,s.myId,s.port_number,server_list);
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		
	    	}
	    	
	    }
	    it = new Inventory(inventoryPath);
      ss = new ServerSocket(ServerPorts.get(0)); // only get the first element right now
      return tempId;
    

  }
  
  
  public String toString(){
	  //return ip_string + ":" + port_number;
	  return ServerIPs.get(myID) + ":" + ServerPorts.get(myID);
  }
  
  public static void main (String[] args) throws Exception{
	  Server s = new Server();
	  ServerSocket ss = s.ss;
	  int generatedServerID = init();

    /*Attempting to receive new connection*/
    while(true){
    	System.out.println("Awaiting new connection request");
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection at port:" + cSocket.getPort());

    	new TCPServerThread(cSocket,generatedServerID, it,server_list, s.inventories).start();

    }
  }
  
  
  
  public boolean getRemoteInventory(int id){
	  String ri_name = "Remote"+id;
	  try {
		  Registry rg = LocateRegistry.getRegistry(registryPort + 2*id);
	  	  RemoteInventory ri = (RemoteInventory)rg.lookup(ri_name);
	  	  if (ri.isValid()){
	  		  inventories.add(ri);
	  		  System.out.println(ri.testFunc());
	  		  return true;
	  	  }
	  } catch (Exception e){}
  	  return false;

  }
  
  public synchronized void getAllInventories(int numServer) {
	  ArrayList<String> servers = new ArrayList<String>();
	  for (Integer i = 0; i < numServer; i++){
		  if (i != myID){
			  servers.add(Integer.toString(i));
		  }
	  }
	  while(!(servers.isEmpty())) {
		  for (int i = 0; i < servers.size(); i++) {
			  if (getRemoteInventory(Integer.parseInt(servers.get(i)))){
				  servers.remove(servers.get(i));
				  i--;
			  }
		  }
	  }
	  //debug
	  try{
		  for (RemoteInventory ri : inventories){
			  System.out.println(ri.testFunc());
		  }
	  }catch(Exception e) {}
  }

  
 
}

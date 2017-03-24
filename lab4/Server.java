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
  static int myID;
  static  ArrayList<RemoteInventory> inventories;// = new ArrayList<RemoteInventory>();
   // each Server object has a reference to the server it creates
  static ArrayList<Integer> ServerPorts;// = new ArrayList<Integer>();
  static ArrayList<String> ServerIPs;// = new ArrayList<String>();
  static Inventory it;
  static ServerSocket ss;
  
//  
//  /**
//   * This server constructor takes in 
//   * @param InetAddress ip_address: the ip_address 
//   * @param int port_number: the portnumber
//   * */
//@SuppressWarnings("unchecked")
//public Server(InetAddress ip_address, int port_number, Linker initComm){
//	  // Initialization for Lamport
//	  super(initComm);
//	  c = new LamportClock(); 
//	  numAcks = 0;
//	  // Initialization for our code
//	  this.ip_address = ip_address;
//	  this.port_number = port_number;
//	  
//  }
//  /**
//   * This server constructor takes in an
//   * @param String ip_string: the string representation of the IP from console
//   * @param int port_number: the port number from console
//   * */
// @SuppressWarnings( "unchecked")
//public Server(int id, ArrayList<String> ips, ArrayList<Integer> ports, Linker initComm, String inventoryPath){
//	super(initComm);
//
//	InetAddress addr;
//	try {
//		addr = InetAddress.getByName(ip_string);
//		this.ip_address = addr;
//		this.ip_string = ip_string;
//		this.port_number = port_number;
//	} catch (UnknownHostException e) {
//		e.printStackTrace();
//	}
//	//System.setProperties(java.rmi.server.hostname, "127.0.0.1");
//	//This block instantiates the remote object
//	iv = new Inventory(inventoryPath);
//	try {
//		RI ri = new RI(iv, myId);
//		String ri_name = "Remote"+this.myId;
//		Registry rg = LocateRegistry.createRegistry(1099);
//		rg.bind(ri_name, ri);
//		System.out.println(ri_name);
//	}catch (Exception e) {
//		System.out.println("Failed to create remote inventory");
//		e.printStackTrace();
//	}
//	ServerPorts = ports;
//	ServerIPs = ips;
//	  
//  }
//  /*
//   * This function gets called whenever a new Server is created
//   * 
//   * */
//  public static Server init() throws IOException{
//	  //get initialization info first line
//	  Scanner sc = new Scanner(System.in);
//	  System.out.println("Enter server-id, number of servers, and path to inventory: ");
//	  int myID = sc.nextInt();
//	  int numServer = sc.nextInt();
//	  String inventoryPath = sc.next();
//	  String topologyi = inventoryPath;
//	  Linker l = null;
//	  //debug
//	  /*
//	  myID = 1;
//	  numServer = 1;
//	  inventoryPath = "inventory.txt";
//	  */
//	  //ask for ips
//	  System.out.println("Enter " + numServer + " IPs");
//	  System.out.println("example format: 127.0.0.1:8000");
//	  
//	  //get all ips
//	  ArrayList<String> ips = new ArrayList<String>();
//	  ArralList<Integer> ports = new ArrayList<Integer>();
//	  for(int i = 1; i <= tempN; i++){
//    	String ip = sc.next();
//    	//ip = "127.0.0.1:8000";
//    	//don't know what this does; I do not think we need the topology file, refer to pdf handout
//    	/*
//    	try{
//      	    PrintWriter writer = new PrintWriter(topologyi, "UTF-8");
//      	    writer.println(ip);
//      	    writer.close();
//      	} catch (IOException e) {
//      	   e.printStackTrace();
//      	}
//      	*/
//    	String[] ips = ip.split(":");
//    	System.out.println(ips.length);
//		ips.add(ips[0]);
//    	ports.add(Integer.parseInt(ips[1]));
//    	try {
//    		l = new Linker(ips[0], tempId,tempN,Integer.parseInt(ips[1]));
//    	  } catch (Exception e) {
//    		// TODO Auto-generated catch block
//    		e.printStackTrace();
//    	  }
//    	//Server s = new Server(ips[0],Integer.parseInt(ips[1]),l);
//    	//server_list.add(s);
//	  }
//	  Server s = new Server(myID, ips, ports, l, inventoryPath);
//	  return s;
//  }
//  
	
  public static ServerSocket init() {
	  Scanner sc = new Scanner(System.in);
	  System.out.println("Enter server-id, number of servers, and path to inventory: ");
	  myID = sc.nextInt();
	  int numServer = sc.nextInt();
	  String inventoryPath = sc.next();
	  String topologyi = inventoryPath;
	  Linker l = null;
	  //debug
	  /*
	  myID = 1;
	  numServer = 1;
	  inventoryPath = "inventory.txt";
	  */
	  //ask for ips
	  System.out.println("Enter " + numServer + " IPs");
	  System.out.println("example format: 127.0.0.1:8000");
	  
	  //get all ips and ports then add to lists
	  ServerIPs = new ArrayList<String>();
	  ServerPorts = new ArrayList<Integer>();
	  for(int i = 1; i <= numServer; i++){
		String ip = sc.next();
		
		String[] ips = ip.split(":");
		System.out.println(ips.length);
		ServerIPs.add(ips[0]);
		ServerPorts.add(Integer.parseInt(ips[1]));
	  }
	  //create inventory and Remote Inventory
	  it = new Inventory(inventoryPath);
	  try {
		  RI ri = new RI(it, myID);
		  String ri_name = "Remote"+myID;
		  Registry rg = null;
		  if (myID == 0){
			  rg = LocateRegistry.createRegistry(1099);
		  } else {
			  rg = LocateRegistry.getRegistry();
		  }
		  rg.bind(ri_name, ri);
		  System.out.println(ri_name);
	  }catch (Exception e) {
		  System.out.println("Failed to create remote inventory");
		  e.printStackTrace();
	  }
	  
	  inventories = new ArrayList<RemoteInventory>();
	  getAllInventories(numServer);
	  ServerSocket ss = null;
	  try {
		  ss = new ServerSocket(ServerPorts.get(myID));
	  } catch(IOException e) {}
	  return ss;
  }
  
  
  public String toString(){
	  //return ip_string + ":" + port_number;
	  return ServerIPs.get(myID) + ":" + ServerPorts.get(myID);
  }
  
  public static void main (String[] args) throws Exception{
	ServerSocket ss = init();
    /*Attempting to receive new connection*/
    while(true){
    	System.out.println("Awaiting new connection request");
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection at port:" + cSocket.getPort());
    	//new TCPServerThread(cSocket, it,server_list, inventories).start();
    }
  }
  
  
  
  public static boolean getRemoteInventory(int id){
	  String ri_name = "Remote"+id;
	  try {
		  Registry rg = LocateRegistry.getRegistry();
	  	  RemoteInventory ri = (RemoteInventory)rg.lookup(ri_name);
	  	  if (ri.isValid()){
	  		  inventories.add(ri);
	  		  System.out.println(ri.testFunc());
	  		  return true;
	  	  }
	  } catch (Exception e){}
  	  return false;

  }
  
  public static void getAllInventories(int numServer) {
	  ArrayList<Integer> servers = new ArrayList<Integer>();
	  for (int i = 0; i < numServer; i++){
		  if (i != myID){
			  servers.add(i);
		  }
	  }
	  while(!(servers.isEmpty())) {
		  for (Integer i:servers) {
			  if (getRemoteInventory(i)){
				  servers.remove(i);
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

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
  
	
  public Server() {
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
  }
  
  
  public String toString(){
	  //return ip_string + ":" + port_number;
	  return ServerIPs.get(myID) + ":" + ServerPorts.get(myID);
  }
  
  public static void main (String[] args) throws Exception{
	  Server s = new Server();
	  ServerSocket ss = s.ss;
    /*Attempting to receive new connection*/
    while(true){
    	System.out.println("Awaiting new connection request");
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection at port:" + cSocket.getPort());
    	new TCPServerThread(cSocket, s.inventories, s.myID).start();
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

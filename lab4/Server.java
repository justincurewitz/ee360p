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
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;


public class Server extends MyProcess {
  /*
   * Variables for Lamport
   * */
  int numAcks;
  Queue<Timestamp> requestQueue;
  LamportClock c;
  int myId;
  /*
   * Variables for Server
   * */
  InetAddress ip_address;
  String ip_string;
  int port_number;
  ArrayList<Server> server_list = new ArrayList<Server>();
  static Server s; // each Server object has a reference to the server it creates
  static ArrayList<Integer> ServerPorts = new ArrayList<Integer>();
  static ArrayList<String> ServerIPs = new ArrayList<String>();
  static String inventoryPath;
  static Inventory it;
  static ServerSocket ss;
  
  /**
   * This server constructor takes in 
   * @param InetAddress ip_address: the ip_address 
   * @param int port_number: the portnumber
   * */
@SuppressWarnings("unchecked")
public Server(InetAddress ip_address, int port_number, Linker initComm){
	  // Initialization for Lamport
	  super(initComm);
	  c = new LamportClock(); 
	  
	  requestQueue= new PriorityQueue<Timestamp>(initComm.n,new Comparator<Timestamp>() {
			public int compare(Timestamp a, Timestamp b) {
				return Timestamp.compare(a, b);
			}
		} );
	  numAcks = 0;
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
public Server(String ip_string, int port_number, Linker initComm){
	super(initComm);
	requestQueue= new PriorityQueue<Timestamp>(initComm.n,new Comparator<Timestamp>() {
		public int compare(Timestamp a, Timestamp b) {
			return Timestamp.compare(a, b);
		}
	} );
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
  /*
   * This function gets called whenever a new Server is created
   * 
   * */
  public static void init() throws IOException{
	  Scanner sc = new Scanner(System.in);
	  System.out.println("Enter Configuration Data");
	  int tempId = sc.nextInt();
	  int tempN = sc.nextInt();
	  inventoryPath = sc.next();
	  String topologyi = inventoryPath;
	  Linker l = null;
	  System.out.println("Enter " + tempN + " IPs");
	    for(int i = 1; i <= tempN; i++){
	    	String ip = sc.next();
	    	String[] ips = ip.split(":");
	    	System.out.println(ips.length);
			ServerIPs.add(ips[0]);
	    	ServerPorts.add(Integer.parseInt(ips[1]));
	    	try {
	    		l = new Linker(ips[0], tempId,tempN);
	    	  } catch (Exception e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	  }
	    	s = new Server(ips[0],Integer.parseInt(ips[1]),l);
	    }
	  it = new Inventory(inventoryPath);
      ss = new ServerSocket(ServerPorts.get(s.getMyId()-1));
  }
  
  public String toString(){
	  return ip_string + ":" + port_number;
  }
  
  public static void main (String[] args) throws Exception{
	init();
	  
    /*Attempting to receive new connection*/
    while(true){
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection");
    	new TCPServerThread(cSocket, it).start();
    }
  }
  public void requestInventoryAccess(int timestamp){
	  
  }
}

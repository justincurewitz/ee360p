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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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
  static ArrayList<Server> server_list = new ArrayList<Server>();
   // each Server object has a reference to the server it creates
  static ArrayList<Integer> ServerPorts = new ArrayList<Integer>();
  static ArrayList<String> ServerIPs = new ArrayList<String>();
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
	  System.out.println("Enter server-id: ");
	  int tempId = sc.nextInt();
	  System.out.println("Enter number of servers n:");
	  int tempN = sc.nextInt();
	  System.out.println("Please enter the filepath as: topologyi.txt where i is your server-id");
	  String inventoryPath = sc.next();
	  String topologyi = inventoryPath;
	  Linker l = null;
	  System.out.println("Enter " + tempN + " IPs");
	  System.out.println("example format: 127.0.0.1:8000");
	    for(int i = 1; i <= tempN; i++){
	    	String ip = sc.next();
	    	try{
	      	    PrintWriter writer = new PrintWriter(topologyi, "UTF-8");
	      	    writer.println(ip);
	      	    writer.close();
	      	  } catch (IOException e) {
	      	   e.printStackTrace();
	      	  }
	    	String[] ips = ip.split(":");
	    	System.out.println(ips.length);
			ServerIPs.add(ips[0]);
	    	ServerPorts.add(Integer.parseInt(ips[1]));
	    	try {
	    		l = new Linker(ips[0], tempId,tempN,Integer.parseInt(ips[1]));
	    	  } catch (Exception e) {
	    		// TODO Auto-generated catch block
	    		e.printStackTrace();
	    	  }
	    	Server s = new Server(ips[0],Integer.parseInt(ips[1]),l);
	    	server_list.add(s);
	    }
	  it = new Inventory(inventoryPath);
      ss = new ServerSocket(ServerPorts.get(0)); // only get the first element right now
  }
  
  public String toString(){
	  return ip_string + ":" + port_number;
  }
  
  public static void main (String[] args) throws Exception{
	init();
    /*Attempting to receive new connection*/
    while(true){
    	System.out.println("Awaiting new connection request");
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection at port:" + cSocket.getPort());
    	new TCPServerThread(cSocket, it,server_list).start();
    }
  }
  public void requestInventoryAccess(int timestamp){
	  c.tick();
	  requestQueue.add(new Timestamp(c.getValue(), myId));
	  sendMsg(neighbors, "request", c.getValue());
	  numAcks = 0;
	  while ((requestQueue.peek().pid != myId) || (numAcks < n-1))
			myWait();
  }
  public void finishedUsingInventory(){
	  requestQueue.remove();
	  sendMsg(neighbors, "release", c.getValue());
  }
  public synchronized void handleMsg(Msg m, int src, String tag) {
		int timeStamp = m.getMessageInt();
		c.receiveAction(src, timeStamp);
		if (tag.equals("request")) {
			requestQueue.add(new Timestamp(timeStamp, src));
			sendMsg("ack",src,c.getValue());
		} else if (tag.equals("release")) {
			Iterator<Timestamp> it =  requestQueue.iterator();			    
			while (it.hasNext()){
				if (it.next().getPid() == src) it.remove();
			}
		} else if (tag.equals("ack"))
			numAcks++;
		notifyAll();
	}

  
 
}

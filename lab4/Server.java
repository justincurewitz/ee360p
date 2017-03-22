/*
		Justin Curewitz jmc6579
		Kristian Wang kw26434
*/

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
    int tcpPort;
    int udpPort;
    int numOrders;
    ArrayList<User> users = new ArrayList<User>();
    boolean TCP = true;
    if (args.length != 3) {
    	System.out.println("ERROR: Provide 3 arguments");
		System.out.println("\t(1) <tcpPort>: the port number for TCP connection");
		System.out.println("\t(2) <udpPort>: the port number for UDP connection");
		System.out.println("\t(3) <file>: the file of inventory");
		System.exit(-1);
    }
    tcpPort = Integer.parseInt(args[0]);
    udpPort = Integer.parseInt(args[1]);
    String fileName = args[2];
    DatagramSocket ds = new DatagramSocket(udpPort);
    Inventory it = new Inventory(fileName);
	ServerSocket ss = new ServerSocket(tcpPort);
    while(true){
    	Socket cSocket = ss.accept();
    	System.out.println("New Connection");
    	new TCPServerThread(cSocket, ds, it, udpPort).start();
    }
  }
}


//
//    /*
//    * Populating the inventory by reading from disk
//    */
//
//    try{
//    Scanner inScanner = new Scanner(file);
//    while(inScanner.hasNext()){
//      String line = inScanner.nextLine();
//      String tokens[] = line.split(" ");
//      int temp = Integer.parseInt(tokens[1]);
//      inv.put(tokens[0],temp);
//    }
//    inScanner.close();
//    } catch(FileNotFoundException e){
//      System.out.println("File not found");
//    }
//
//    
//    if(TCP){
//      ServerSocket ss = new ServerSocket(tcpPort);
//      try{
//      while(true){
//        Socket cSocket = ss.accept();
//        try{
//        BufferedReader in = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
//        DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
//        String client_str;
//        while((client_str = in.readLine()) != null){
//          String elements[] = client_str.split(" ");
//          if(elements[0].equals("setmode")){
//
//          } else if (elements[0].equals("purchase")){
//
//            if(!inv.keySet().contains(elements[2])){
//              out.writeBytes("Not Available - We do not sell this product");
//            } else if (inv.get(elements[2]) == 0) {
//              out.writeBytes("Not Available - Not enough items");
//            } else {
//              User client = new User();
//              client.name = elements[1];
//              System.out.println(client.name);
//              client.orders.add(numOrders);
//              System.out.println(numOrders);
//              users.add(client);
//              orderID.put(numOrders,elements[2]);
//              out.writeBytes("You order has been placed, " + numOrders + " " + elements[1] + " " + elements[2] + " " + elements[3] + "\n");
//              numOrders++;
//            }
//
//          } else if (elements[0].equals("cancel")){
//            if(!orderID.keySet().contains(elements[1])){
//              out.writeBytes( elements[1] + " not found, no such order");
//            } else{
//              //implement the logic with the file
//              out.writeBytes("Order " + orderID.get(elements[1]) + "is canceled");
//            }
//          } else if (elements[0].equals("search")){
//            String user_key = elements[1];
//            User search_usr = new User();
//            for(User usr: users){
//              if(user_key.equals(usr.name)){
//                search_usr = usr;
//              }
//            }
//            if(search_usr.orders.size() == 0){
//              out.writeBytes("No order found for " + user_key);
//            } else{
//              for(Integer k: search_usr.orders){
//                String product_name = orderID.get(k);
//                int quantity = inv.get(product_name);
//                out.writeBytes("" + k + product_name + quantity);
//              }
//            }
//
//          } else if (elements[0].equals("list")){
//            for(String product_name: inv.keySet()){
//              out.writeBytes(product_name + inv.get(product_name));
//            }
//          } else {
//            System.out.println("Not valid command");
//          }
//        }
//      } finally{
//        cSocket.close();
//       }
//      }
//      } finally{
//        ss.close();
//      } 
//    }else{
//      //startUDP(udpPort);
//    	ServerSocket ss = new ServerSocket(udpPort);
//    	try{
//    		while(true){
//    			Socket my_s = ss.accept();
//    		}
//    	}catch (IOException e) {e.printStackTrace();}
//    	
//    }
//
//  } 
//
//
//
//
//
//
//}





  

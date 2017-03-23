import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.ArrayList;



public class Client {
  static ArrayList<Server> servers = new ArrayList<Server>();
  static Socket clientSocket;
  /*
   * This method attempts to connect to the nearest (determined by order in arraylist) server
   * If the nearest server does not respond within 100 ms, then move onto the next server. 
   * */
  public static void connectToNearestServer() throws IOException {
	  int counter = 1;
	  for(Server s: servers){
		  String hostAddress = s.ip_string;
		  int tcpPort = s.port_number;
		  InetAddress address = InetAddress.getByName(hostAddress);
		  clientSocket = new Socket(address,tcpPort); // static variable gets rewritten every time we connect to new server
		  InetSocketAddress sa = new InetSocketAddress(address,tcpPort);
		  try{
			  clientSocket.connect(sa,100);
			  break;
		  } catch(SocketTimeoutException e){
			  System.out.println("Server" + counter + "timed out, attempting to connect to next nearest server");
		  }  
	  }
	  
  }

  public static void main (String[] args) throws Exception {
    
 
    boolean invalid_arguments = false;
    
    Scanner sc = new Scanner(System.in);
    
    int n = Integer.parseInt(args[0]);
    while(n != 0){
    	if(sc.hasNextLine()){
    		String server_string = sc.nextLine();
    		String ip_string = server_string.split(":")[0];
    		String port_string = server_string.split(":")[1];
    		int port = Integer.parseInt(port_string);
    		Server s = new Server(ip_string,port);
    		servers.add(s);
    	}
    	n--;
    }
    
    if (invalid_arguments) {
      System.out.println("ERROR: Provide n + 1 arguments");
      System.out.println("\t(1) <n>: the number of servers present");
      System.out.println("\t(2) <ip-address>:<port-number>");
      System.out.println("\t(3) <ip-address>:<port-number>");
      System.out.println("\t(4) repeat until n servers");
      System.exit(-1);
    }
    for(Server x: servers) System.out.println(x);
    
    while(sc.hasNextLine()){
    	/*
    	 * Try to connect to nearest server
    	 * */
    	connectToNearestServer();
    	
    	/*
    	 * Handle like it's one server
    	 * */
    	System.out.println("Enter Next Command");
    	String cmd = sc.nextLine();
        String[] tokens = cmd.split(" ");
        if (tokens[0].equals("quit")){
        	break;
        }else if (tokens[0].equals("purchase")) {
		   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		   BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		   outToServer.writeBytes(tokens[0] + " " + tokens[1] + " " + tokens[2] + " "  + tokens[3] + '\n');
		   String modifiedSentence = inFromServer.readLine();
		   System.out.println("FROM SERVER: " + modifiedSentence);
		   clientSocket.close();
	   } else if (tokens[0].equals("cancel")) {
		   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		   BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		   outToServer.writeBytes("cancel" + '\n');
		   String modifiedSentence = inFromServer.readLine();
		   System.out.println("FROM SERVER: " + modifiedSentence);
		   clientSocket.close();
	   } else if (tokens[0].equals("search")) {
		   DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	       BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	       outToServer.writeUTF(cmd+"\n");
	    	  if (tokens[0].equals("search") || tokens[0].equals("list")){
	    		  String reply;
	    		  while ((reply = inFromServer.readLine()) != null){
	    			  if (reply.equals("quit")){break;}
	    			  System.out.println(reply);
	    		  }
	    	  } else {
		    	  String modifiedSentence = inFromServer.readLine();
				  System.out.println("FROM SERVER: " + modifiedSentence);
	    	  }
			  clientSocket.close();

	   } else {
	   System.out.println("ERROR: No such command");
	   }
    }
    System.out.println("Client Exited");
  }
}

//package lab4;

import java.util.Scanner;
import java.net.*;
import java.io.*;

public class Client {
    static DatagramSocket ds;

  public static void main (String[] args) throws Exception {
    String hostAddress;
    int tcpPort;
    int udpPort;
    boolean TCP = true;
    InetAddress address;
    int newUDP = 0;
    
    if (args.length != 3) {
      System.out.println("ERROR: Provide 3 arguments");
      System.out.println("\t(1) <hostAddress>: the address of the server");
      System.out.println("\t(2) <tcpPort>: the port number for TCP connection");
      System.out.println("\t(3) <udpPort>: the port number for UDP connection");
      System.exit(-1);
    }

    hostAddress = args[0];
    tcpPort = Integer.parseInt(args[1]);
    udpPort = Integer.parseInt(args[2]);
	address = InetAddress.getByName(hostAddress);
    Scanner sc = new Scanner(System.in);
    Socket clientSocket;
    clientSocket = new Socket(hostAddress,tcpPort);
    System.out.println("Connected");
    DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	String re = null;
	outToServer.writeUTF("Client\n");
//	while (re == null) {
//		re = inFromServer.readLine();
//	}
	System.out.println("Entering while loop");
    while(sc.hasNextLine()) {
  	  System.out.println("Enter Next Command");
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");
      if (tokens[0].equals("quit")){break;}
      
      else if (TCP){
    	  System.out.println("TCP");
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
      } 
    }
    System.out.println("Exiting loop");
    clientSocket.close();
    ds.close();
    System.out.println("Client Exited");
  }
}
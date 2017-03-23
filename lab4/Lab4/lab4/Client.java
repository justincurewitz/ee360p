package lab4;

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
    while(sc.hasNextLine()) {
  	  System.out.println("Enter Next Command");
      String cmd = sc.nextLine();
      String[] tokens = cmd.split(" ");
      if (tokens[0].equals("quit")){break;}
      else if (tokens[0].equals("setmode")) {
        // TODO: set the mode of communication for sending commands to the server 
        // and display the name of the protocol that will be used in future
    	  DataInputStream in = new DataInputStream(clientSocket.getInputStream());
    	  DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
    	  if (tokens[1].equals("U")){
    		if (TCP) {
	    		TCP = false;
	      	  	ds = new DatagramSocket(udpPort);
	      	  	out.writeUTF("setmode U \n");
	      	  	newUDP = in.readInt();
    		}
    	  } else if (tokens[1].equals("T")){
    		  if(!TCP){
	    		  TCP = true;
	    		  clientSocket = new Socket(hostAddress,tcpPort);
    		  }
    	  }
    	  //new UDP_Client(command, udpPort, address, newUdpPort);
      } 
      else if (TCP){
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
		  //clientSocket.close();
      } else {
    	  try {
			byte[] buf = cmd.getBytes();
			DatagramPacket dp = new DatagramPacket(buf, buf.length, address, newUDP);
			ds.send(dp);
			buf = new byte[1024];
			dp = new DatagramPacket(buf, buf.length);
			ds.receive(dp);
			System.out.println(new String(buf));
    	  } catch(IOException e){e.printStackTrace();}
      }
    }
    clientSocket.close();
    ds.close();
    System.out.println("Client Exited");
  }
}
//else if (tokens[0].equals("purchase")) {
//// TODO: send appropriate command to the server and display the
//// appropriate responses form the server
//DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//outToServer.writeBytes(tokens[0] + " " + tokens[1] + " " + tokens[2] + " "  + tokens[3] + '\n');
//String modifiedSentence = inFromServer.readLine();
//System.out.println("FROM SERVER: " + modifiedSentence);
//clientSocket.close();
//} else if (tokens[0].equals("cancel")) {
//// TODO: send appropriate command to the server and display the
//// appropriate responses form the server
//DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//outToServer.writeBytes("cancel" + '\n');
//String modifiedSentence = inFromServer.readLine();
//System.out.println("FROM SERVER: " + modifiedSentence);
//clientSocket.close();
//} else if (tokens[0].equals("search")) {
//// TODO: send appropriate command to the server and display the
//// appropriate responses form the server
//} else if (tokens[0].equals("list")) {
//// TODO: send appropriate command to the server and display the
//// appropriate responses form the server
//} else {
//System.out.println("ERROR: No such command");
//}
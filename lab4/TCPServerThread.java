//package lab4;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.io.*;

public class TCPServerThread extends Thread {
	
	BufferedReader in;
	Socket s;
	DataOutputStream out;
	Inventory iv;
	int numAcks;
	Queue<Timestamp> requestQueue;
	LamportClock c;
	int myId;
	ArrayList<Server> all_servers;
	Linker linker;
	public TCPServerThread(Socket s,int id, Inventory iv, ArrayList<Server> server_list) {
		this.s = s; // this is the passed in clientSocket from Server.java
		this.iv = iv;
		all_servers = server_list;
		c = new LamportClock();
		requestQueue = new PriorityQueue<Timestamp>(all_servers.size(), 	
				new Comparator<Timestamp>() {
					public int compare(Timestamp a, Timestamp b) {
						return Timestamp.compare(a, b);
					}
				});
		linker = getServerByID(id,all_servers).linker; // essentially passing down the top level linker here.
		try {
			in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			out = new DataOutputStream(this.s.getOutputStream());
		}catch(IOException e){e.printStackTrace();
		}
	}
	
	public static Server getServerByID(int ID,ArrayList<Server> listOfServers){
		for(int i = 0; i < listOfServers.size(); i++){
			if(listOfServers.get(i).myId == ID){
				return listOfServers.get(i);
			}
		}
		return null;	
	}
	
	public synchronized void requestInventoryAccess(Timestamp timestamp) throws InterruptedException{
		  c.tick();
		  requestQueue.add(timestamp); // adding to my own queue
		  try {
			linker.sendMsg(all_servers, "request",Integer.toString(c.getValue()));
			//sendMsg(all_servers, "request", c.getValue());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		  numAcks = 0;
		  while ((requestQueue.peek().pid != myId) || (numAcks < all_servers.size()-1))
				wait();
	  }
	/*
	private void sendMsg(ArrayList<Server> neighbors, String string, int value) throws IOException {
		// TODO Auto-generated method stub
		  for(Server s: neighbors){
			  Socket soc = new Socket(s.ip_address,s.port_number);
			  DataOutputStream outToServer = new DataOutputStream(soc.getOutputStream());
			  out.writeUTF("Client\n");
			  out.writeUTF(string + value);
		  }
		
	}
	*/
    public synchronized void finishedUsingInventory() throws IOException{
		  requestQueue.remove();
		  try {
			linker.sendMsg(all_servers, "release", Integer.toString(c.getValue()));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	  }
    public synchronized void handleMsg(Msg m, int src, String tag) throws IOException {
			//int timeStamp = m.getMessageInt();
    	    // need to do some testing becuase right now im just transforming the byte array into String and then parsing as int which should break
			Msg message = linker.receiveMsg(src);
			byte[] buffer = message.bytemsgBuf;
			String frombytes = new String(buffer);
			int timeStamp = Integer.parseInt(frombytes);
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
	/*
	 * This is broken needs dest to be resolved to the right thing
	 * 
	 * */
	private void sendMsg(String string, int dest, int value) throws IOException {
			  Socket soc = new Socket(s.getInetAddress(), dest);
			  DataOutputStream outToServer = new DataOutputStream(soc.getOutputStream());
			  out.writeUTF("Client\n");
			  out.writeUTF(string + value);
		
	}
	public void run() {
		System.out.println("TCP Thread Started");
		String c_str = "Client";
		while(true){
			try {
				String in_str = null;
				if((in_str=in.readLine()) != null){
					in_str = in_str.substring(1);
					in_str = in_str.trim();
					System.out.println(in_str);
					System.out.println(in_str.equals(c_str));
					System.out.println(in_str.length());
					if(in_str.equals(c_str)){
						System.out.println("");
						Client();
						break;
					} else if (in_str.equals("Server")){
						Server();
					}
				}
			}catch (IOException e){e.printStackTrace();}
		}
	}
	
	private void Client() {
		System.out.println("Started Client Function");
		while(true){
			try {
			    String client_str;
			    if ((client_str = in.readLine()) != null) {
					String[] commands = {"purchase", "cancel", "search", "list"};
			    	String request[] = client_str.split(" ");
			    	for (int i = 0; i < request.length; i++) {
						request[i] = request[i].replaceAll("\\P{Print}", "");
					}
			        String reply = null;
			        requestInventoryAccess(new Timestamp(c.getValue(),myId));
			        if (request[0].equals(commands[0])) {
						reply = new String(iv.purchase(request));
					} else if (request[0].equals(commands[1])){
						reply = new String(iv.cancel(Integer.parseInt(request[1])));
					} else if (request[0].equals(commands[2])){
						reply = new String(iv.search(request[1]));
					} else if (request[0].equals(commands[3])){
						reply = new String(iv.list());
					}
			        if(reply != null){
						finishedUsingInventory();
					}
			        out.writeUTF(reply + "\n");
			    }
			}catch (IOException | InterruptedException e){e.printStackTrace();}
		}
		
	}
	
	private void Server() {
		System.out.println("Server Contact");
	}

}

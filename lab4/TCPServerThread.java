//package lab4;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Queue;
import java.io.*;

public class TCPServerThread extends Thread {
	BufferedReader in;
	Socket s;
	DataOutputStream out;
	//Inventory iv;
	
	int numAcks;
	Queue<Timestamp> requestQueue;
	LamportClock c;
	int myId;
	ArrayList<Server> neighbors = new ArrayList<Server>();
	ArrayList<RemoteInventory> inventories;
	public TCPServerThread(Socket s, ArrayList<RemoteInventory> ivs) {
		this.s = s;
		//this.iv = iv;
		//neighbors = server_list;
		inventories = ivs;
		try {
			in = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			out = new DataOutputStream(this.s.getOutputStream());
		}catch(IOException e){e.printStackTrace();}
	}
	  public void requestInventoryAccess(Timestamp timestamp) throws InterruptedException{
		  c.tick();
		  requestQueue.add(new Timestamp(c.getValue(), myId));
		  sendMsg(neighbors, "request", c.getValue());
		  numAcks = 0;
		  while ((requestQueue.peek().pid != myId) || (numAcks < neighbors.size()-1))
				wait();
	  }
	  private void sendMsg(ArrayList<Server> neighbors, String string, int value) {
		// TODO Auto-generated method stub
		
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
	
	private void sendMsg(String string, int dest, int value) {
		// TODO Auto-generated method stub
		
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
		//System.out.println("Started Client Function");
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
			        //requestInventoryAccess(new Timestamp(c.getValue(),myId));
			        try {
				        if (request[0].equals(commands[0])) {
				        	String req = "";
				        	for (String s:request){
				        		req += s+" ";
				        	}
							reply = allPurchase(req);
						} else if (request[0].equals(commands[1])){
							reply = allCancel(request[1]);
						} else if (request[0].equals(commands[2])){
							reply = allSearch(request[1]);
						} else if (request[0].equals(commands[3])){
							reply = new String(allList());
						}
			        } catch(Exception e) {System.out.println("oops, TCPServerThread line 118");
			        	e.printStackTrace();
			        }
			        if(reply != null){
						//finishedUsingInventory();
					}
			        System.out.println(reply);
			        if (reply != null) {
			        	out.writeUTF(reply + "\n");
			        }
			    }
			}catch (IOException e){e.printStackTrace();}
		}
		
	}
	
	private void Server() {
		System.out.println("Server Contact");
	}
	
	
	public String allPurchase(String req){
		String reply = null;
		for (RemoteInventory ri : inventories){
			try {
				if(reply == null) {reply = ri.purchase(req);}
				else {ri.purchase(req);}
			}catch(Exception e){}
		}
		return reply;
	}
	
	public String allCancel(String req) {
		String reply = null;
		try{
			for (RemoteInventory ri : inventories){
				if(reply == null) {reply = ri.cancel(req);}
				else {ri.cancel(req);}
			}
		}catch(Exception e){}
		return reply;
	}
	
	public String allSearch(String req){
		String reply = null;
		try{
			for (RemoteInventory ri : inventories){
				if(reply == null) {reply = ri.search(req);}
				else {ri.search(req);}
			}
		}catch(Exception e) {}
		return reply;
	}
	
	public String allList() {
		String reply = null;
		try{
			for (RemoteInventory ri : inventories) {
				if (reply == null) {reply = ri.list();}
				else{ri.list();}
			}
		} catch(Exception e){}
		
		return reply;
	}
	
	
}





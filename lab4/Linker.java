import java.util.*; import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.net.UnknownHostException;
public class Linker implements MsgHandler {
	public int myId;	
	public int n; // number of neighbors including myself
	Connector connector = null;
	MsgHandler app = null;// upper layer
	MsgHandler comm = null;// lower layer
	public boolean appFinished = false;
	public List<Integer> neighbors = new ArrayList<Integer>();	
	public Properties prop = new Properties();
	String ipstr;
	int port;
	public Linker (String args[]) throws Exception { 
		super();
	}
	public Linker(String ip_string, int id, int numProc,int port) throws Exception{
		myId = id;
		n = numProc;
		ipstr = ip_string;
		this.port = port;
		// reads the neighbors from a file called topologyi
		Topology.readNeighbors(myId, neighbors);
		for(int s: neighbors){
			System.out.println(s);
		}
		connector = new Connector();
		connector.Connect(ipstr, myId, neighbors);
	}
	public void init(MsgHandler app){
		this.app = app;	
		for (int pid : neighbors)
			(new ListenerThread(pid, this)).start();		    	
	}
	public Msg receiveMsg() {
		return null;
	}
	/*
	 * Everything after this is only done to satisfy MsgHandler
	 * */
	public void sendMsg(int destId, Object ... objects) throws ClassNotFoundException, UnknownHostException, IOException {	
			connector.broadcastMessagesToNeighbors(new Socket(ipstr,port), neighbors, myId, destId, objects);
	}
	
	public Msg receiveMsg(int fromId) {
		int i = neighbors.indexOf(fromId);
		try {
			ObjectInputStream oi = connector.getMessagesFromNeighbors(new Socket(ipstr,port), neighbors)[myId];
			int numItems = ((Integer) oi.readObject()).intValue();
			LinkedList<Object> recvdMessage = new LinkedList<Object>();
			for (int j = 0; j < numItems; j++) 
				recvdMessage.add(oi.readObject());
			String tag = (String) recvdMessage.removeFirst();
			return new Msg(fromId, myId, tag, recvdMessage);
		} catch (Exception e) { System.out.println(e);
			close(); return null;		
		}
	}
	public synchronized void handleMsg(Msg m, int src, String tag) { }
	public synchronized void executeMsg(Msg m) {	
		handleMsg(m, m.src, m.tag);
		notifyAll();
		if (app != null) app.executeMsg(m);		
	}
	public synchronized int getMyId() { return myId; }
	public Properties getProp() { return prop;}
	public List<Integer> getNeighbors() { return neighbors; }
	public void close() { appFinished = true; connector.closeSockets(); }
	public void turnPassive() {	}
	@Override
	public void sendMsg(String i, Object... objects) {
		// TODO Auto-generated method stub
		
	}
}

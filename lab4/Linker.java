import java.util.*; import java.io.*;
import java.net.InetAddress;
import java.net.MulticastSocket;
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
		
	}
	public void init(MsgHandler app){
		this.app = app;	
		for (int pid : neighbors)
			(new ListenerThread(pid, this)).start();		    	
	}
	public Msg receiveMsg() {
		return null;
	}
	public void broadcastMsg(String s) throws UnknownHostException{
	}
	/*
	 * Everything after this is only done to satisfy MsgHandler
	 * */
	public void sendMsg(int destId, Object ... objects) {	
			int j = neighbors.indexOf(destId);
			try {
				LinkedList<Object> objectList = Util.getLinkedList(objects);
				ObjectOutputStream os = connector.dataOut[j];
				os.writeObject(Integer.valueOf(objectList.size()));
				for (Object object : objectList) 
					os.writeObject(object);
				os.flush();
			} catch (IOException e) {System.out.println(e);close();	}
	}
	
	
	public Msg receiveMsg(int fromId) {
		return null;
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

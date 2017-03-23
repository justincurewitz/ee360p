import java.util.*; import java.io.*;
public class Linker implements MsgHandler {
	public int myId;	
	public int n; // number of neighbors including myself
	Connector connector = null;
	MsgHandler app = null;// upper layer
	MsgHandler comm = null;// lower layer
	public boolean appFinished = false;
	public List<String> neighbors = new ArrayList<String>();	
	public Properties prop = new Properties();
	public Linker (String args[]) throws Exception { 
		String basename = args[0];
		myId = Integer.parseInt(args[1]);
		if (!Topology.readNeighbors(myId, neighbors)) 
			Topology.setComplete(myId, neighbors, Integer.parseInt(args[2]));
		n = neighbors.size() + 1;
		prop.loadFromXML(new FileInputStream("LinkerProp.xml")); // not sure what this does. 
		connector = new Connector();
		connector.Connect(basename, myId, neighbors);
	}
	public Linker(String ip_string, int id, int numProc) throws Exception{
		myId = id;
		n = numProc;
		// reads the neighbors from a file called topologyi
		Topology.readNeighbors(myId, neighbors);
		connector = new Connector();
		connector.Connect(ip_string, myId, neighbors);
	}
	public void init(MsgHandler app){
		this.app = app;	
		for (String pid : neighbors)
			(new ListenerThread(Integer.parseInt(pid), this)).start();		    	
	}
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
		int i = neighbors.indexOf(fromId);
		try {
			ObjectInputStream oi = connector.dataIn[i];
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
	public List<String> getNeighbors() { return neighbors; }
	public void close() { appFinished = true; connector.closeSockets(); }
	public void turnPassive() {	}
}
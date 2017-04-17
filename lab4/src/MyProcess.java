
import java.util.*; 
import java.io.*;
public class MyProcess implements MsgHandler {
	public int myId;	
	public int n; // number of neighbors including myself
	public int N; // total number of nodes in the system
	public MsgHandler app = null;// upper layer
	MsgHandler comm = null;// lower layer
	public boolean debug = true;
	public boolean appFinished = false;
	//public Properties prop = new Properties();
	public List<String> neighbors = new ArrayList<String>();	
	public MyProcess(MsgHandler initComm)  {
		comm = initComm;
		myId = comm.getMyId();	       
		neighbors = comm.getNeighbors();
		n = neighbors.size() + 1;
		N = n; // total number as known to me
		//prop = comm.getProp();
	}
	public void init(MsgHandler app){
		this.app = app;
		comm.init(this);    	
	}
	public synchronized void handleMsg(Msg m, int src, String tag){}
//	public synchronized void executeMsg(Msg m) {	
//		handleMsg(m, m.src, m.tag);
//		notifyAll();
//		//if (app != null) app.executeMsg(m);		
//	}
	public void sendMsg(String i, Object ... objects) {
		comm.sendMsg(i, objects);		
	        //if (debug) System.out.println("Process "+ myId + " sends " + objects + " to " + destId);
	}
	public void sendMsg(List<String> destIds, Object... objects) {
		for (String i : (destIds))
			if (Integer.parseInt(i) != myId)
				sendMsg(i, objects);
	}	
	
	//public void broadcastMsg(String tag, int msg) {
        	//sendMsg(neighbors, tag, msg);
    	//}
	public synchronized int getMyId() { return myId; }
	//public Properties getProp() { return prop;}
	public synchronized void myWait() {
		try {wait();
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}	
	public static void println(String s) {
		System.out.println(s);
	}
	public List<String> getNeighbors() { return comm.getNeighbors(); }
	public void close() {
		comm.close();
	}
	public void turnPassive() { 
		comm.turnPassive();
	}
	@Override
	public void executeMsg(Msg m) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Properties getProp() {
		// TODO Auto-generated method stub
		return null;
	}
}

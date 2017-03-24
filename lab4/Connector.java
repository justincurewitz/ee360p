
import java.util.*;import java.net.*;import java.io.*;

public class Connector {
	ServerSocket listener; 
	Socket[] link;
	public ObjectInputStream[] dataIn;
	public ObjectOutputStream[] dataOut;
	Name myNameclient;
	public void Connect(String basename, int myId, List<Integer> neighbors)
			throws Exception {
		int numNeigh = neighbors.size();
		link = new Socket[numNeigh];
		dataIn = new ObjectInputStream[numNeigh];
		dataOut = new ObjectOutputStream[numNeigh];
		int localport = getLocalPort(myId);
		listener = new ServerSocket(localport);
		/* register my name in the name server */
		myNameclient.insertName(basename + myId, (InetAddress.getLocalHost()) .getHostName(), localport);
		/* accept connections from all the smaller processes */
	}
	int getLocalPort(int id) {return Symbols.ServerPort + 20 + id;	}
	public void broadcastMessagesToNeighbors(Socket s, List<Integer> neighbors,int myId,int destId, Object ... objects) throws IOException,ClassNotFoundException{
		for (int neighbor : neighbors) {
			InetSocketAddress addr = null;
			int i = neighbors.indexOf(neighbor);
			link[i] = new Socket(addr.getHostName(), addr.getPort());
			dataOut[i] = new ObjectOutputStream(link[i].getOutputStream());
			/* send a hello message to P_i */
			dataOut[i].writeObject(new Integer(myId));
			dataOut[i].writeObject(new String("inventory_state"));
			int j = neighbors.indexOf(destId);
			try {
				LinkedList<Object> objectList = Util.getLinkedList(objects);
				ObjectOutputStream os = dataOut[j];
				os.writeObject(Integer.valueOf(objectList.size()));
				for (Object object : objectList) 
					os.writeObject(object);
				os.flush();
			} catch (IOException e) {System.out.println(e);	
			}
			// change what we send out
			dataOut[i].flush();
			//dataIn[i]=new ObjectInputStream(link[i].getInputStream());
		}
		
	}
	
	public ObjectInputStream[] getMessagesFromNeighbors(Socket s, List<Integer> neighbors) throws IOException, ClassNotFoundException{
		for(int neighbor: neighbors){
			s = listener.accept();
			InputStream is = s.getInputStream();
			ObjectInputStream din = new ObjectInputStream(is);
			Integer hisId = (Integer) din.readObject();
			int i = neighbors.indexOf(hisId);
			String tag = (String) din.readObject();
			if (tag.equals("inventory_state")) {
				link[i] = s;
				dataIn[i] = din;
				//dataOut[i] = new ObjectOutputStream(s.getOutputStream()); // we dont need output here
		    }
			
		}
		return dataIn;
	}
	public void closeSockets() {
		try {
			listener.close();
			for (Socket s : link) s.close();
		} catch (Exception e) { System.err.println(e); }
	}
}

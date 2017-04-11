

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RI extends UnicastRemoteObject implements RemoteInventory{
	Inventory iv;
	String name;
	public RI(Inventory iv, int id) throws RemoteException{
		this.iv = iv;
		this.name = "Remote"+id;
	}
	
	public String testFunc() {
		return name;
	}
	public String purchase(String request){
		//System.out.println("Started purchase");
		String[] req = request.split(" ");
		//System.out.println(request);
		String reply = iv.purchase(req);
		//System.out.println(reply);
		return reply;
	}
	
	public String search(String request) {
		return new String(iv.search(request));
	}
	
	public String cancel(String request) {
		return new String(iv.cancel(Integer.parseInt(request)));
	}
	
	public String list() {
		return new String(iv.list());
	}
	
	public boolean isValid() {
		return true;
	}
}
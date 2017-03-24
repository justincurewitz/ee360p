

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
		System.out.println("Started purchase");
		String[] req = request.split(" ");
		System.out.println(request);
		return new String(iv.purchase(req));
	}
	
	public String search(String request) {
		return new String(iv.search(request));
	}
	
	public String cancel(int request) {
		return new String(iv.cancel(request));
	}
	
	public boolean isValid() {
		return true;
	}
}
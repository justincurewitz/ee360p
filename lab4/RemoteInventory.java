

import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;

public class RemoteInventory extends UnicastRemoteObject implements rmiInventory{
	Inventory iv;
	
	public RemoteInventory(Inventory iv) throws RemoteException{
		this.iv = iv;
	}
	
	public void purchase(String[] request){
		iv.purchase(request);
	}
	
	public void search(String request) {
		iv.search(request);
	}
	
	public void cancel(int request) {
		iv.cancel(request);
	}
}
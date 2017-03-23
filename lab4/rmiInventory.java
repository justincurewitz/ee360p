import java.rmi.*;

public interface rmiInventory extends Remote {
	void purchase(String[] request) throws RemoteException;
	void search(String request) throws RemoteException;
	void cancel(int order) throws RemoteException;
}
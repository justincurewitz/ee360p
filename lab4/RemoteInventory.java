import java.rmi.*;

public interface RemoteInventory extends Remote {
	String testFunc() throws RemoteException;
	String purchase(String request) throws RemoteException;
	String search(String request) throws RemoteException;
	String cancel(String order) throws RemoteException;
	String list() throws RemoteException;
	boolean isValid() throws RemoteException;
}
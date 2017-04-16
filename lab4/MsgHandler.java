import java.rmi.*;
import java.util.*;
public interface MsgHandler extends Remote {
    public void handleMsg(Msg m, int src, String tag);
    public void executeMsg(Msg m);
    public void sendMsg(String i, Object ...objects);
	public void init(MsgHandler app);
	public void close();
	public int getMyId();	
	public List<String> getNeighbors();
	public void turnPassive();
	public Properties getProp();	
   }

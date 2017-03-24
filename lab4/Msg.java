import java.util.*;
public class Msg {
    public int src, dest;
    public String tag;
    LinkedList<Object> msgBuf;
    byte[] bytemsgBuf;
    public Msg(int s, int t, String msgType, LinkedList<Object> buf) {
        this.src = s;
        dest = t;
        tag = msgType;
        msgBuf = buf;
    }
    public Msg(int from, int to, String msgType, byte[] buff){
    	this.src = from;
    	dest = to;
    	tag = msgType;
    	bytemsgBuf = buff;
    }
    public Msg(int from, int to, byte[] buff){
    	this.src = from;
    	dest = to;
    	bytemsgBuf = buff;
    }
    public Msg(int to, byte[] buff){
    	dest = to;
    	bytemsgBuf = buff;
    }
    public byte[] getByteMsgBuf(){
    	return bytemsgBuf;
    }
    public LinkedList<Object> getMsgBuf() {
        return msgBuf;
    }
    public int getMessageInt() {
         return (Integer) msgBuf.removeFirst();
    }
    
    public String toString(){
        String s = String.valueOf(src)+" " +
                    String.valueOf(dest)+ " " +
                    tag + " " + msgBuf.toString() ;
        return s;
    } 
}

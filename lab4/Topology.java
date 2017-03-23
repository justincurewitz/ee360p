import java.io.*;
import java.util.*;
public class Topology {
	// returns true if read neighbors successfully
	public static boolean readNeighbors(int myId,List<String> neighbors) {
		System.out.println("Reading topology" + myId);
		try {
			Scanner sc = new Scanner(new FileReader("topology" + myId));
			while (sc.hasNext()) {
				String neighbor = sc.nextLine();
				neighbors.add(neighbor);
			}
		} catch (IOException e) {
			return false;
		}
		System.out.println(neighbors.toString());
		return true;
	}
	public static void setComplete(int myId,List<String> neighbors, int numProc) {
		for (int i = 0; i < numProc; ++i) {
			if (i != myId) neighbors.add(Integer.toString(i));
		}
	}
	
}

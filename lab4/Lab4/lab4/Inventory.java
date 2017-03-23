package lab4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

public class Inventory {
	//ArrayList<String> items = new ArrayList<String>();
	//ArrayList<Integer> item_count = new ArrayList<Integer>();
	//ArrayList<ArrayList<Integer>> orders = new ArrayList<ArrayList<Integer>>();
	BufferedReader br;
	private int orderID = 0;
	HashMap<String,Integer> items = new HashMap<String,Integer>();
	HashMap<Integer,Order> orders = new HashMap<Integer,Order>();
	ArrayList<User> users = new ArrayList<User>();
	
	public Inventory(String filename){
		try {
			FileReader fl = new FileReader(filename);
			BufferedReader br = new BufferedReader(fl);
			String line = null;
			//get input
			while((line = br.readLine()) != null){
				String[] entry = line.split(" ");
				if (entry.length == 2){
					items.put(entry[0], Integer.parseInt(entry[1]));
				}
			}
			//print for debug
//			for (int i = 0; i < items.size(); i++){
//				System.out.println(items.get(i) + " " + item_count.get(i));
//			}
		} catch (IOException e){e.printStackTrace();}
	}
	
	public synchronized byte[] purchase(String[] request){
		byte[] buf;
		String username = request[1];
		String product = request[2];
		int quantity = Integer.parseInt(request[3]);
		Integer num =  items.get(product);
		
		
		if (num != null){
			if (num >= quantity){
				String summary = "Your order has been placed, " + (orderID)+" "+username+" "+product+" "+quantity;
				buf = summary.getBytes();
				items.put(product, num - quantity);
				Order o = new Order();
				
				User client = new User();
				boolean found = false;
				for(User usr:users){
					if (username.equals(usr.name)){
						client = usr;
						found = true;
						break;
					}
				}
				if(!found){
					client.name = username;
					users.add(client);
				}
				o.u = client;
				o.quant = quantity;
				o.product = product;
				orders.put(orderID, o);
				client.orders.add(orderID);
				orderID++;
				return buf;
			} else {
				String summary = "Not Available - Not enough items";
				buf = summary.getBytes();
				return buf;
			}
		}
		String summary = "Not Available - We do not sell this product";
		buf = summary.getBytes();
		return buf;		
	}
	
	public synchronized byte[] cancel(int order) {
		if(orders.containsKey(order)){
			Order o = orders.get(order);
			if (items.containsKey(o.product)){
				items.put(o.product, items.get(o.product) + o.quant);
				String summary = "Order " + order +" was canceled";
				byte[] buf = summary.getBytes();
				return buf;
			}
		} 
		String summary = order + " not found, no such order.";
		return summary.getBytes();
	}
	
	public synchronized byte[] search(String username){
		String all = "";
		User u = null;
		for (User usr:users){
			if(usr.name.equals(username)){
				u = usr;
			}
		}
		if (u == null){
			String summary = "No order found for user";
			return summary.getBytes();
		}
		
		for (Integer o:u.orders){
			all = all + orders.get(o).toString() + "\n";
		}
		all = all + "quit\n";
		return all.getBytes();
	}
	
	public synchronized byte[] list(){
		String all = "";
		for(String product_name :items.keySet()){
			all = all + product_name + " " +items.get(product_name) + "\n";
		}
		all = all + "quit\n";
		return all.getBytes();
	}
}

class User {
	  String name;
	  ArrayList<Integer> orders = new ArrayList<Integer>();
}

class Order{
	User u;
	int quant;
	String product;
	
	public String toString() {
		return product + " " + quant;
	}
}


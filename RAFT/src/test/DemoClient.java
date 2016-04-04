package test;

import server.queue.ImageClient;

public class DemoClient {	
	
	public static void main(String args[]) {			 
		 ImageClient client;
		try {
			client = new ImageClient(args[0]);
//			 String key = client.post("/home/jagruti/Pictures/i3.png");
//			 System.out.println("POST DONE");
//	     	 
//			 client.get(key, "/home/jagruti/Pictures/Output");
//			 System.out.println("GET DONE");
			 String key = "20cc3f4c-955b-4fda-805a-b0d5f15ba61e";
//			 client.put(key, "/home/jagruti/Pictures/200_radis.png");
//			 System.out.println("UPDATE DONE");
			 client.get(key, "/home/jagruti/Pictures/Output");
			 client.delete(key);
			 System.out.println("delete done");

		} catch (Exception e) {
			e.printStackTrace();
		}		 		 		 		
	}
}

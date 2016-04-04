package test;

import server.queue.ByteClient;

public class DemoByteClient {
	public static void main(String args[]) {			 
		 ByteClient client;
		try {
			client = new ByteClient(args[0]);
			String key = client.post("abc1".getBytes());
			System.out.println("POST DONE : " + key);

			String key1 = client.post("abc2".getBytes());
			System.out.println("POST DONE : " + key1);

//			byte[] data = client.get(key);
//			System.out.println(new String(data));
//			System.out.println("GET DONE");
//			
//			client.put(key, "xyz".getBytes());
//	     	byte[] updated = client.get(key); 
//			System.out.println("PUT DONE : "+ new String(updated));
			
//
//	     	client.delete(key);
//			System.out.println("DELETE DONE");

		} catch (Exception e) {
			e.printStackTrace();
		}		 		 		 		
		System.exit(0);
	}
}

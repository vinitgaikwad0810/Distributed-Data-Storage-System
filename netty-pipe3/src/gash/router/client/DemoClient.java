package gash.router.client;

import gash.router.client.api.ImageClient;

public class DemoClient {	
	
	public static void main(String args[]) {		
		 ImageClient client = new ImageClient();
		 
		 String key = client.post("/home/jagruti/Pictures/2_1_after_Assigning_IP.png");
		 client.get(key, "/home/jagruti/workspace/ProjectFluffy/ImageTransfer/src");
		
	}
}

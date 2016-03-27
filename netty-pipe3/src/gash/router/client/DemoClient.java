package gash.router.client;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class QueueClient {
	
	public void createQueue() throws IOException {
		ConnectionFactory factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    Connection connection = factory.newConnection();
	    Channel channel = connection.createChannel();
	    
	    channel.queueDeclare("inbound_queue", false, false, false, null);
	    String message = "Hello World!";
	    channel.basicPublish("", "inbound_queue", null, message.getBytes());
	    System.out.println(" [x] Sent '" + message + "'");
	    
	    channel.close();
	    connection.close();
	}
	
	
	
	public static void main(String args[]) {		
		try {
			QueueClient client = new QueueClient();
			client.createQueue();			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}

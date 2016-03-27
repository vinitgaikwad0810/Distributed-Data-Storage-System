package gash.router.client.api.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import gash.Image.ImageTransfer;

public class ClientQueueService {
	
	static ClientQueueService instance = null;	
	Connection connection = null;
	Channel channel = null;	
	String inbound_queue = "inbound_queue";
	String queueURL = "amqp://jagruti:linux2015@localhost:5672";
	String callbackQueueName = null;
	QueueingConsumer consumer = null;
	
	public static ClientQueueService getInstance() {
		if (instance == null) {
			instance = new ClientQueueService();
		}
		return instance;
	}
	
	private ClientQueueService() {
	    try {
			ConnectionFactory factory = new ConnectionFactory();
		    factory.setUri(queueURL);
	    	connection = factory.newConnection();
		    channel = connection.createChannel();		    
		    channel.queueDeclare(inbound_queue, false, false, false, null);
		    
		    callbackQueueName = channel.queueDeclare().getQueue();
		    consumer = new QueueingConsumer(channel); 
		    channel.basicConsume(callbackQueueName, true, consumer);
		} catch (IOException e) {		    
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	private void shutdown() throws IOException {
		channel.close();
	    connection.close();
	}
		
	public void putMessage(String key, ImageTransfer.ImageMsg message) throws IOException {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("request_type", "put");
		headers.put("key", key);
		
		channel.basicPublish("", inbound_queue, new AMQP.BasicProperties().builder()
				.headers(headers).build()
				, message.getImageData().toByteArray());
	}
	
	public String postMessage(ImageTransfer.ImageMsg message) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("request_type", "post");
		
		String corrId = java.util.UUID.randomUUID().toString();

	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .correlationId(corrId)
	                                .replyTo(callbackQueueName)
	                                .build();
		
		channel.basicPublish("", inbound_queue, props, message.getImageData().toByteArray());
		while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            return new String(delivery.getBody());
	        }
	    }
	}
	
	public void deleteMessage(ImageTransfer.ImageMsg message) throws IOException {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("request_type", "delete");
		channel.basicPublish("", inbound_queue, new AMQP.BasicProperties().builder()
				.headers(headers).build()
				, message.getImageData().toByteArray());
	}
	
	public byte[] getMessage(String key) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("request_type", "get");
		String corrId = java.util.UUID.randomUUID().toString();

	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .correlationId(corrId)
	                                .replyTo(callbackQueueName)
	                                .build();

		channel.basicPublish("", inbound_queue, props, key.getBytes());
		while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            return delivery.getBody();
	        }
	    }
	}
	
}

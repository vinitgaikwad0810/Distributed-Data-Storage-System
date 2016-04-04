package server.queue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import raft.proto.ImageTransfer;

public class ClientQueueService {
	
	static ClientQueueService instance = null;

	private static final String QUEUE_URL = QueueConfiguration.getInstance().getQueueURL();

	Connection connection = null;
	Channel channel = null;	
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
				factory.setUri(QUEUE_URL);
		    	connection = factory.newConnection();
			    channel = connection.createChannel();		    		    
			    callbackQueueName = channel.queueDeclare().getQueue();
			    consumer = new QueueingConsumer(channel); 
			    channel.basicConsume(callbackQueueName, true, consumer);

			} catch (KeyManagementException | NoSuchAlgorithmException | URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	private void shutdown() throws IOException {
		channel.close();
	    connection.close();
	}
		
	public void putMessage(String key, ImageTransfer.ImageMsg message) throws IOException {
		String corrId = java.util.UUID.randomUUID().toString();
	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .type(QueueOperationConstants.PUT)
	                                .correlationId(corrId)
	                                .userId(key)
	                                .replyTo(callbackQueueName)
	                                .build();
	    System.out.println("Client Queue Server put");
		channel.basicPublish("", QueueOperationConstants.INBOUND_QUEUE, props, message.getImageData().toByteArray());
	}
	
	public String postMessage(ImageTransfer.ImageMsg message) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String corrId = java.util.UUID.randomUUID().toString();

	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .type(QueueOperationConstants.POST)
	                                .correlationId(corrId)
	                                .replyTo(callbackQueueName)
	                                .build();
	    System.out.println("Client Queue Server post");
		channel.basicPublish("", QueueOperationConstants.INBOUND_QUEUE, props, message.getImageData().toByteArray());
		System.out.println(channel.getConnection().getAddress());
		while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            String key = new String(delivery.getBody());
//	            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	            return key;
	        }
	    }
	}
	
	public void deleteMessage(String key) throws IOException {
		 BasicProperties props = new BasicProperties
                 .Builder()
                 .type(QueueOperationConstants.DELETE)
                 .replyTo(callbackQueueName)
                 .build();

		 channel.basicPublish("", QueueOperationConstants.INBOUND_QUEUE, props, key.getBytes());
	}
	
	public byte[] getMessage(String key) throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {		
		String corrId = java.util.UUID.randomUUID().toString();

	    BasicProperties props = new BasicProperties
	                                .Builder()
	                                .type(QueueOperationConstants.GET)
	                                .correlationId(corrId)
	                                .replyTo(callbackQueueName)
	                                .build();

		channel.basicPublish("", QueueOperationConstants.INBOUND_QUEUE, props, key.getBytes());
		while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();
	        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
	            byte[] data = delivery.getBody();
//	            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	            return data;
	        }
	    }
	}	
}

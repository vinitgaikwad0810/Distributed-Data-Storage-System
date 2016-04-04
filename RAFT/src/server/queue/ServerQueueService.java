package server.queue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

import raft.NodeState;

public class ServerQueueService {
	
	private static final String INBOUND_QUEUE = SystemConstants.INBOUND_QUEUE;
	private static final String QUEUE_URL = ConfigurationReader.getInstance().getQueueURL();
	
	private static ServerQueueService instance = null;	
	private Channel channel = null;
	private QueueingConsumer consumer = null;
	public static ServerQueueService getInstance() {
		if (instance == null) {
			instance = new ServerQueueService();
		}
		return instance;
	}
	
	private ServerQueueService() {		
	}
	
	public void createQueue() {			
		    try {
		    	//TODO INBOUND QUEUE IS DURABLE, OUTBOUND QUEUE IS NOT DURABLE
		    	ConnectionFactory factory = new ConnectionFactory();
				factory.setUri(QUEUE_URL);
			    Connection connection = factory.newConnection();
				channel = connection.createChannel();
			    channel.queueDeclare(INBOUND_QUEUE, true, false, false, null);	    
			    channel.basicQos(1);

			    consumer = new QueueingConsumer(channel);
			    channel.basicConsume(INBOUND_QUEUE, false, consumer);
			    processQueue();

			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ShutdownSignalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void processQueue() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException, SQLException {
	    while (true) {
	        QueueingConsumer.Delivery delivery = consumer.nextDelivery();	 
	        BasicProperties props = delivery.getProperties();
	        String request = props.getType();
	        System.out.println(request);

	        if (request != null) {
	        	if (request.equals(SystemConstants.GET))  {
	        		String key = new String(delivery.getBody());	        		        	
		        	BasicProperties replyProps = new BasicProperties
		        	                                     .Builder()
		        	                                     .correlationId(props.getCorrelationId())
		        	                                     .build();
		        	byte[] image = NodeState.getService().handleGetMessage(key); 		        			
		        	channel.basicPublish( "", props.getReplyTo(), replyProps, image);
		        } 
		        
		        if (request.equals(SystemConstants.PUT))  {
		        	NodeState.getService().handlePutMessage(props.getCorrelationId(), delivery.getBody(), System.currentTimeMillis());
		        }
		        
		        if (request.equals(SystemConstants.POST))  {
		        	String key = NodeState.getService().handlePostMessage(delivery.getBody(), System.currentTimeMillis());
		        	BasicProperties replyProps = new BasicProperties
		        	                                     .Builder()
		        	                                     .correlationId(props.getCorrelationId())
		        	                                     .build();
		        	
		        	channel.basicPublish( "", props.getReplyTo(), replyProps, key.getBytes());
		        }
		        
		        if (request.equals(SystemConstants.DELETE))  {
	        		String key = new String(delivery.getBody());
	        		NodeState.getService().handleDelete(key);
		        }
	        	channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
	        }	        	
	    }	        
	 }

}

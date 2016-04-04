package server.queue;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class QueueConfiguration {
		
	private String queueURL = null;
	private int queuePort = 0;
	private Properties props = new Properties();
	
	private static QueueConfiguration instance = null;
	
	public static QueueConfiguration getInstance() {
		if (instance == null) {
			instance = new QueueConfiguration();
		}
		return instance;
	}
	
	private QueueConfiguration() {
		
	}

	public void loadProperties(File file) {
		InputStream is;
		try {
			is = new FileInputStream(file);
			props.load(is);			
			queueURL = (String) props.get(QueueOperationConstants.QUEUE_URL);
			if (props.get(QueueOperationConstants.QUEUE_PORT) != null) {
				queuePort = Integer.parseInt((String)props.get(QueueOperationConstants.QUEUE_PORT));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}

	public String getQueueURL() {
		return queueURL;
	}

	public int getQueuePort() {
		return queuePort;
	}
	
}

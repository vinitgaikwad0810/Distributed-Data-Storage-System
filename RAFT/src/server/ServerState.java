package server;

import router.container.RoutingConf;
import server.edges.EdgeMonitor;
import server.queue.QueueConfiguration;

public class ServerState {
	private RoutingConf conf;
	private EdgeMonitor emon;
	private QueueConfiguration queueConf = QueueConfiguration.getInstance();
//	private TaskList tasks;


	
	public RoutingConf getConf() {
		return conf;
	}

	public void setConf(RoutingConf conf) {
		this.conf = conf;
	}

	public EdgeMonitor getEmon() {
		return emon;
	}

	public void setEmon(EdgeMonitor emon) {
		this.emon = emon;
	}
	
	public QueueConfiguration getQueueConf() {
		return queueConf;
	}

	/*public TaskList getTasks() {
		return tasks;
	}*/
/*
	public void setTasks(TaskList tasks) {
		this.tasks = tasks;
	}*/
}

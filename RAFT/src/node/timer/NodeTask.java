/**
 * 
 */
package node.timer;

import java.util.TimerTask;

public class NodeTask extends TimerTask{

	Runnable task = null;
	
	NodeTask(Runnable task) {
		this.task = task;
	}
	
	@Override
	public void run() {
		//TODO change state to candidate, keep centralized state somewhere
		task.run();
	}

}

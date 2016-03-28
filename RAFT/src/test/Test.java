package test;

import node.timer.NodeTimer;
import raft.CandidateService;
import raft.NodeState;

public class Test {
	public static void main(String args[]) {
		NodeTimer timer = new NodeTimer();
		
		System.out.println("Before updating state" + System.currentTimeMillis());
		timer.schedule(new Runnable() {
			@Override
			public void run() {				
				NodeState.getInstance().setState(NodeState.CANDIDATE);;
				Thread candidateThread = new Thread(new CandidateService());
				candidateThread.start();
			}			
		}, 1000);
		
		try {
            Thread.sleep(12000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
		System.out.println("After updating state" + System.currentTimeMillis());
		timer.reschedule(500);
		
		try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}

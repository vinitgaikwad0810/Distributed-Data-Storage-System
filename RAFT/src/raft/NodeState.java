package raft;

public class NodeState {
	
	public static final int LEADER = 0;
	
	public static final int CANDIDATE = 1;
	
	public static final int FOLLOWER = 2;

	private static int state = 2;
	
	private static NodeState instance = null;
	
	private NodeState() {
	}
	
	public static NodeState getInstance() {
		if (instance == null) {
			instance = new NodeState();
		}
		return instance;
	}
		
	public void setState(int newState) {
		state = newState;
	}
	
	public int getState() {
		return state;
	}	
}

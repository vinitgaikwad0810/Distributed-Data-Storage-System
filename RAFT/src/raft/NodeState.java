package raft;

import server.ServerState;

public class NodeState {

	public static final int LEADER = 0;

	public static final int CANDIDATE = 1;

	public static final int FOLLOWER = 2;

	private static int state = 2;

	private static long timeStampOnLatestUpdate;
	
	public static long getTimeStampOnLatestUpdate() {
		return timeStampOnLatestUpdate;
	}

	public static void setTimeStampOnLatestUpdate(long timeStampOnLatestUpdate) {
		NodeState.timeStampOnLatestUpdate = timeStampOnLatestUpdate;
	}

	public static Service getService() {
		return service;
	}

	

	private static Service service;

	private static NodeState instance = null;
	
	private  ServerState serverState = null;

	private NodeState() {

		service = FollowerService.getInstance();

	}

	public static NodeState getInstance() {
		if (instance == null) {
			instance = new NodeState();
		}
		return instance;
	}
	public void setServerState(ServerState serverState){
		this.serverState= serverState;
	}
	
	public ServerState getServerState()
	{
		return serverState;
		
	}

	public synchronized void setState(int newState) {
		state = newState;

		if (newState == NodeState.FOLLOWER) {
			service.stopService();
			service = FollowerService.getInstance();
			service.startService(service);
		} else if (newState == NodeState.LEADER) {
			service.stopService();
			service = LeaderService.getInstance();
			service.startService(service);

		} else if (newState == NodeState.CANDIDATE) {
			service.stopService();
			service = CandidateService.getInstance();
			service.startService(service);
		}
	}

	public synchronized int getState() {
		return state;
	}
}

package raft;

import raft.proto.Work.WorkMessage;

public  class Service {

	protected volatile Boolean running = Boolean.TRUE;
	static Thread cthread;

	public void startService(Service service){
		
	}

	public void stopService() {
		// TODO Auto-generated method stub
		
	}
	
	

	public void handleResponseVoteRPCs(WorkMessage workMessage) {
		// TODO Auto-generated method stub
		
	}

	public WorkMessage handleRequestVoteRPC(WorkMessage workMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	


	
}

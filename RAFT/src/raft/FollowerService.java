package raft;

import io.netty.channel.ChannelFuture;
import logger.Logger;
import node.timer.NodeTimer;
import raft.proto.AppendEntriesRPC.LogEntries;
import raft.proto.HeartBeatRPC.HeartBeatPacket;
import raft.proto.HeartBeatRPC.HeartBeatResponse;
import raft.proto.VoteRPC.ResponseVoteRPC;
import raft.proto.VoteRPC.VoteRPCPacket;
import raft.proto.Work.WorkMessage;
import server.ServerUtils;
import server.edges.EdgeInfo;

public class FollowerService extends Service implements Runnable {

	public static Boolean isHeartBeatRecieved = Boolean.FALSE;
	NodeTimer timer;

	private static FollowerService INSTANCE = null;
	

	private FollowerService() {
		// TODO Auto-generated constructor stub
	}

	public static FollowerService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new FollowerService();

		}
		return INSTANCE;
	}

	@Override
	public void run() {
		Logger.DEBUG("Follower service started");
		initFollower();
		while (running) {

			while (NodeState.getInstance().getState() == NodeState.FOLLOWER) {

			}
		}

	}

	private void initFollower() {
		// TODO Auto-generated method stub

		timer = new NodeTimer();

		timer.schedule(new Runnable() {
			@Override
			public void run() {
				NodeState.getInstance().setState(NodeState.CANDIDATE);
				;
				Thread candidateThread = new Thread(CandidateService.getInstance());
				candidateThread.start();
			}
		}, ServerUtils.getElectionTimeout());

	}

	public void onReceivingHeartBeatPacket() {
		timer.reschedule(ServerUtils.getElectionTimeout());
	}

	@Override
	public WorkMessage handleRequestVoteRPC(WorkMessage workMessage) {

		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		VoteRPCPacket.Builder voteRPCPacket = VoteRPCPacket.newBuilder();
		voteRPCPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());

		ResponseVoteRPC.Builder responseVoteRPC = ResponseVoteRPC.newBuilder();
		responseVoteRPC.setTerm(1);
		responseVoteRPC.setIsVoteGranted(ResponseVoteRPC.IsVoteGranted.YES);

		voteRPCPacket.setResponseVoteRPC(responseVoteRPC);

		work.setVoteRPCPacket(voteRPCPacket);
		
		return work.build();
		
	}

	public void handleHeartBeat(WorkMessage wm)
	{
		Logger.DEBUG("HeartbeatPacket received from leader :" + wm.getHeartBeatPacket().getHeartbeat().getLeaderId());
		onReceivingHeartBeatPacket();
		WorkMessage heartBeatResponse = prepareHeartBeatResponse();

		for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap().values()) {

			if (ei.isActive() && ei.getChannel() != null
					&& ei.getRef() == wm.getHeartBeatPacket().getHeartbeat().getLeaderId()) {

				Logger.DEBUG("Sent HeartBeatResponse to " + ei.getRef());
				ChannelFuture cf = ei.getChannel().writeAndFlush(heartBeatResponse);
				if (cf.isDone() && !cf.isSuccess()) {
					Logger.DEBUG("failed to send message (HeartBeatResponse) to server");
				}
			}
		}
		
		
	}
	
	public WorkMessage prepareHeartBeatResponse() {
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		HeartBeatResponse.Builder heartbeatResponse = HeartBeatResponse.newBuilder();
		heartbeatResponse.setNodeId(NodeState.getInstance().getServerState().getConf().getNodeId());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey(1);
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		heartbeatResponse.addLogEntries(logEntry);

		HeartBeatPacket.Builder heartBeatPacket = HeartBeatPacket.newBuilder();
		heartBeatPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
		heartBeatPacket.setHeartBeatResponse(heartbeatResponse);

		work.setHeartBeatPacket(heartBeatPacket);

		return work.build();
	}

	
	@Override
	public void startService(Service service) {

		running = Boolean.TRUE;
		cthread = new Thread((FollowerService) service);
		cthread.start();

	}

	@Override
	public void stopService() {
		running = Boolean.FALSE;
	}

}

package raft;

import com.google.protobuf.ByteString;

import io.netty.channel.ChannelFuture;
import logger.Logger;
import raft.proto.AppendEntriesRPC;
import raft.proto.AppendEntriesRPC.AppendEntries;
import raft.proto.AppendEntriesRPC.AppendEntriesPacket;
import raft.proto.AppendEntriesRPC.LogEntries;
import raft.proto.HeartBeatRPC.HeartBeat;
import raft.proto.HeartBeatRPC.HeartBeatPacket;
import raft.proto.Work.WorkMessage;
import server.ServerUtils;
import server.edges.EdgeInfo;

public class LeaderService extends Service implements Runnable {

	private static LeaderService INSTANCE = null;

	private LeaderService() {
		// TODO Auto-generated constructor stub

	}

	public static LeaderService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new LeaderService();
		}
		return INSTANCE;
	}

	@Override
	public void run() {
		Logger.DEBUG("Leader Service Started");
		while (running) {

			try {
				Thread.sleep(NodeState.getInstance().getServerState().getConf().getHeartbeatDt());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendHeartBeat();

		}
	}

	@Override
	public void sendHeartBeat() {
		for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap().values()) {

			if (ei.isActive() && ei.getChannel() != null) {
				WorkMessage workMessage = prepareHeartBeat();
				Logger.DEBUG("Sent HeartBeatPacket to " + ei.getRef());
				ChannelFuture cf = ei.getChannel().writeAndFlush(workMessage);
				if (cf.isDone() && !cf.isSuccess()) {
					Logger.DEBUG("failed to send message (HeartBeatPacket) to server");
				}
			}
		}

	}

	public WorkMessage prepareHeartBeat() {
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		HeartBeat.Builder heartbeat = HeartBeat.newBuilder();
		heartbeat.setLeaderId(NodeState.getInstance().getServerState().getConf().getNodeId());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey(1);
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		heartbeat.addLogEntries(logEntry);
		heartbeat.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);

		HeartBeatPacket.Builder heartBeatPacket = HeartBeatPacket.newBuilder();
		heartBeatPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
		heartBeatPacket.setHeartbeat(heartbeat);

		work.setHeartBeatPacket(heartBeatPacket);

		return work.build();
	}

	public WorkMessage prepareAppendEntries() {

		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesPacket.Builder appendEntriesPacket = AppendEntriesPacket.newBuilder();
		appendEntriesPacket.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey(1);
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesRPC.ImageMsg.Builder imageMsg = AppendEntriesRPC.ImageMsg.newBuilder();
		// TO-DO
		imageMsg.setKey(1);

		// TO-DO
		ByteString byteString;
		byteString = ByteString.copyFrom("This is a test".getBytes());
		imageMsg.setImageData(byteString);

		AppendEntries.Builder appendEntries = AppendEntries.newBuilder();
		appendEntries.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);
		appendEntries.setImageMsg(imageMsg);
		appendEntries.setLeaderId(NodeState.getInstance().getServerState().getConf().getNodeId());
		appendEntries.addLogEntries(logEntry);

		appendEntriesPacket.setAppendEntries(appendEntries);

		return work.build();

	}

	public void startService(Service service) {
		running = Boolean.TRUE;
		cthread = new Thread((LeaderService) service);
		cthread.start();
	}

	public void stopService() {
		running = Boolean.FALSE;

	}

}

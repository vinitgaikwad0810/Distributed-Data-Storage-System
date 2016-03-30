package raft;

import com.google.protobuf.ByteString;

import raft.proto.AppendEntriesRPC;
import raft.proto.AppendEntriesRPC.AppendEntries;
import raft.proto.AppendEntriesRPC.AppendEntriesPacket;
import raft.proto.AppendEntriesRPC.AppendEntriesResponse;
import raft.proto.AppendEntriesRPC.LogEntries;
import raft.proto.AppendEntriesRPC.AppendEntriesResponse.IsUpdated;
import raft.proto.HeartBeatRPC.HeartBeat;
import raft.proto.HeartBeatRPC.HeartBeatPacket;
import raft.proto.HeartBeatRPC.HeartBeatResponse;
import raft.proto.VoteRPC.RequestVoteRPC;
import raft.proto.VoteRPC.VoteRPCPacket;
import raft.proto.Work.WorkMessage;
import server.ServerUtils;

public class ServiceUtils {

	public static WorkMessage prepareRequestVoteRPC() {
		// TO-DO
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		RequestVoteRPC.Builder requestVoteRPC = RequestVoteRPC.newBuilder();
		requestVoteRPC.setTerm(1);
		requestVoteRPC.setCandidateId("" + NodeState.getInstance().getServerState().getConf().getNodeId());

		LogEntries.Builder logEntries = LogEntries.newBuilder();
		logEntries.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());
		logEntries.setKey("1234");

		requestVoteRPC.addLogEntriesBuilder(0);
		requestVoteRPC.setLogEntries(0, logEntries);
		requestVoteRPC.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);

		VoteRPCPacket.Builder voteRPCPacket = VoteRPCPacket.newBuilder();
		voteRPCPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
		voteRPCPacket.setRequestVoteRPC(requestVoteRPC);

		work.setVoteRPCPacket(voteRPCPacket);

		return work.build();
	}

	
	public static WorkMessage prepareAppendEntriesResponse() {
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesPacket.Builder appendEntriesPacket = AppendEntriesPacket.newBuilder();
		appendEntriesPacket.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesResponse.Builder appendEntriesResponse = AppendEntriesResponse.newBuilder();

		// TO-DO
		appendEntriesResponse.setIsUpdated(IsUpdated.YES);

		appendEntriesPacket.setAppendEntriesResponse(appendEntriesResponse);

		work.setAppendEntriesPacket(appendEntriesPacket);

		return work.build();

	}
	public static WorkMessage prepareHeartBeatResponse() {
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		HeartBeatResponse.Builder heartbeatResponse = HeartBeatResponse.newBuilder();
		heartbeatResponse.setNodeId(NodeState.getInstance().getServerState().getConf().getNodeId());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey("1");
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		heartbeatResponse.addLogEntries(logEntry);
		heartbeatResponse.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);

		HeartBeatPacket.Builder heartBeatPacket = HeartBeatPacket.newBuilder();
		heartBeatPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
		heartBeatPacket.setHeartBeatResponse(heartbeatResponse);

		work.setHeartBeatPacket(heartBeatPacket);

		return work.build();
		
		
	}

	public static WorkMessage prepareHeartBeat() {
		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		HeartBeat.Builder heartbeat = HeartBeat.newBuilder();
		heartbeat.setLeaderId(NodeState.getInstance().getServerState().getConf().getNodeId());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey("1");
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		heartbeat.addLogEntries(logEntry);
		heartbeat.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);

		HeartBeatPacket.Builder heartBeatPacket = HeartBeatPacket.newBuilder();
		heartBeatPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
		heartBeatPacket.setHeartbeat(heartbeat);

		work.setHeartBeatPacket(heartBeatPacket);

		return work.build();
	}

	public static WorkMessage prepareAppendEntriesPacket(String key, byte[] imageData, long timestamp) {

		WorkMessage.Builder work = WorkMessage.newBuilder();
		work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesPacket.Builder appendEntriesPacket = AppendEntriesPacket.newBuilder();
		appendEntriesPacket.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		LogEntries.Builder logEntry = LogEntries.newBuilder();
		logEntry.setKey(key);
		logEntry.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());

		AppendEntriesRPC.ImageMsg.Builder imageMsg = AppendEntriesRPC.ImageMsg.newBuilder();
		// TO-DO
		imageMsg.setKey(key);

		// TO-DO
		ByteString byteString;
		byteString = ByteString.copyFrom(imageData);
		imageMsg.setImageData(byteString);

		AppendEntries.Builder appendEntries = AppendEntries.newBuilder();
		appendEntries.setTimeStampOnLatestUpdate(Service.timeStampOnLatestUpdate);
		appendEntries.setImageMsg(imageMsg);
		appendEntries.setLeaderId(NodeState.getInstance().getServerState().getConf().getNodeId());
		appendEntries.addLogEntries(logEntry);

		appendEntriesPacket.setAppendEntries(appendEntries);
		
		work.setAppendEntriesPacket(appendEntriesPacket);

		return work.build();

	}

}

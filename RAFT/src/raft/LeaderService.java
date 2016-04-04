package raft;

import java.util.List;

import io.netty.channel.ChannelFuture;
import logger.Logger;
import raft.proto.AppendEntriesRPC.AppendEntries.RequestType;
import raft.proto.Work.WorkMessage;
import server.db.DatabaseService;
import server.db.Record;
import server.edges.EdgeInfo;
import server.queue.ServerQueueService;

public class LeaderService extends Service implements Runnable {

	private static LeaderService INSTANCE = null;
	Thread heartBt = null;
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
		Logger.DEBUG("-----------------------LEADER SERVICE STARTED ----------------------------");
		initLatestTimeStampOnUpdate();
		heartBt = new Thread(){
		    public void run(){
				while (running) {
					try {
						Thread.sleep(NodeState.getInstance().getServerState().getConf().getHeartbeatDt());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					sendHeartBeat();
					// TODO Append Messages are to be sent only when master receives
					// update message from queue
				}
		    }
		 };

		heartBt.start();
		ServerQueueService.getInstance().createQueue();
	}

	private void initLatestTimeStampOnUpdate() {

		NodeState.setTimeStampOnLatestUpdate(DatabaseService.getInstance().getDb().getCurrentTimeStamp());

	}

	private void sendAppendEntriesPacket(WorkMessage workMessage) {

			for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap()
					.values()) {

				if (ei.isActive() && ei.getChannel() != null) {

					Logger.DEBUG("Sent AppendEntriesPacket to " + ei.getRef() + "for the key " + workMessage.getAppendEntriesPacket().getAppendEntries().getImageMsg().getKey());

					ChannelFuture cf = ei.getChannel().writeAndFlush(workMessage);
					if (cf.isDone() && !cf.isSuccess()) {
						Logger.DEBUG("failed to send message (AppendEntriesPacket) to server");
					}
				}
			}
	}

	public void handleHeartBeatResponse(WorkMessage wm) {

		long timeStampOnLatestUpdate = wm.getHeartBeatPacket().getHeartBeatResponse().getTimeStampOnLatestUpdate();

		if (DatabaseService.getInstance().getDb().getCurrentTimeStamp() > timeStampOnLatestUpdate) {
			List<Record> laterEntries = DatabaseService.getInstance().getDb().getNewEntries(timeStampOnLatestUpdate);

			for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap()
					.values()) {

				if (ei.isActive() && ei.getChannel() != null
						&& ei.getRef() == wm.getHeartBeatPacket().getHeartBeatResponse().getNodeId()) {

					//TODO decide it will be post or put
					for (Record record : laterEntries) {
						WorkMessage workMessage = ServiceUtils.prepareAppendEntriesPacket(record.getKey(),
								record.getImage(), record.getTimestamp(), RequestType.POST);
						Logger.DEBUG("Sent AppendEntriesPacket to " + ei.getRef() + "for the key (later Entries) "
								+ record.getKey());
						ChannelFuture cf = ei.getChannel().writeAndFlush(workMessage);
						if (cf.isDone() && !cf.isSuccess()) {
							Logger.DEBUG("failed to send message (AppendEntriesPacket) to server");
						}
					}
				}
			}

		}

	}

	@Override
	public void sendHeartBeat() {
		for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap().values()) {
			if (ei.isActive() && ei.getChannel() != null) {
				WorkMessage workMessage = ServiceUtils.prepareHeartBeat();
				Logger.DEBUG("Sent HeartBeatPacket to " + ei.getRef());
				ChannelFuture cf = ei.getChannel().writeAndFlush(workMessage);
				if (cf.isDone() && !cf.isSuccess()) {
					Logger.DEBUG("failed to send message (HeartBeatPacket) to server");
				}
			}
		}
	}

	public byte[] handleGetMessage(String key) {
		return DatabaseService.getInstance().getDb().get(key);
	}
	
	public String handlePostMessage(byte[] image, long timestamp) {
		NodeState.setTimeStampOnLatestUpdate(timestamp);
		String key = DatabaseService.getInstance().getDb().post(image, timestamp);
		WorkMessage wm = ServiceUtils.prepareAppendEntriesPacket(key, image, timestamp, RequestType.POST);
		sendAppendEntriesPacket(wm);
		return key;
	}

	public void handlePutMessage(String key, byte[] image, long timestamp) {
		NodeState.setTimeStampOnLatestUpdate(timestamp);
		DatabaseService.getInstance().getDb().put(key, image, timestamp);
		WorkMessage wm = ServiceUtils.prepareAppendEntriesPacket(key, image, timestamp, RequestType.PUT);
		sendAppendEntriesPacket(wm);
	}
	
	@Override
	public void handleDelete(String key) {
		NodeState.setTimeStampOnLatestUpdate(System.currentTimeMillis());
		DatabaseService.getInstance().getDb().delete(key);
		WorkMessage wm = ServiceUtils.prepareAppendEntriesPacket(key, null, 0 ,RequestType.DELETE);
		sendAppendEntriesPacket(wm);
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

package raft;

import java.util.List;

import io.netty.channel.ChannelFuture;
import logger.Logger;
import raft.proto.Work.WorkMessage;
import server.db.DatabaseService;
import server.db.Record;
import server.edges.EdgeInfo;

public class LeaderService extends Service implements Runnable {

	private static LeaderService INSTANCE = null;
	private Boolean dummy = Boolean.TRUE;

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
		initLatestTimeStampOnUpdate();
		while (running) {

			try {
				Thread.sleep(NodeState.getInstance().getServerState().getConf().getHeartbeatDt());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sendHeartBeat();
			// TODO Append Messages are to be sent only when master recieves
			// update message from queue
			if (dummy) {
				sendAppendEntriesPacket();
				dummy = Boolean.FALSE;
			}
		}
	}

	private void initLatestTimeStampOnUpdate() {

		NodeState.setTimeStampOnLatestUpdate(DatabaseService.getInstance().getDb().getCurrentTimeStamp());

	}

	private void sendAppendEntriesPacket() {
		List<Record> list = DatabaseService.getInstance().getDb().getAllEntries();

		for (Record record : list) {

			WorkMessage workMessage = ServiceUtils.prepareAppendEntriesPacket(record.getKey(), record.getImage(),
					record.getTimestamp());

			for (EdgeInfo ei : NodeState.getInstance().getServerState().getEmon().getOutboundEdges().getMap()
					.values()) {

				if (ei.isActive() && ei.getChannel() != null) {

					Logger.DEBUG("Sent AppendEntriesPacket to " + ei.getRef() + "for the key " + record.getKey());

					ChannelFuture cf = ei.getChannel().writeAndFlush(workMessage);
					if (cf.isDone() && !cf.isSuccess()) {
						Logger.DEBUG("failed to send message (AppendEntriesPacket) to server");
					}
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

					for (Record record : laterEntries) {
						WorkMessage workMessage = ServiceUtils.prepareAppendEntriesPacket(record.getKey(),
								record.getImage(), record.getTimestamp());
						Logger.DEBUG("Sent AppendEntriesPacket to " + ei.getRef() + "for the key (later Entries) " + record.getKey());
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

	public void startService(Service service) {
		running = Boolean.TRUE;
		cthread = new Thread((LeaderService) service);
		cthread.start();
	}

	public void stopService() {
		running = Boolean.FALSE;

	}

}

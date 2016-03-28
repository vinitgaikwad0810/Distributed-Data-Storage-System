/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package server;

import java.util.Date;

//import java.util.logging.Logger;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import logger.Logger;
import raft.proto.AppendEntriesRPC.LogEntries;
import raft.proto.HeartBeatRPC.HeartBeatPacket;
import raft.proto.HeartBeatRPC.HeartBeatResponse;
import raft.proto.Work.WorkMessage;

/**
 * The message handler processes json messages that are delimited by a 'newline'
 * 
 * TODO replace println with logging!
 * 
 * @author gash
 * 
 */
public class WorkHandler extends SimpleChannelInboundHandler<WorkMessage> {
	// protected static Logger logger = LoggerFactory.getLogger("work");
	protected ServerState state;
	protected boolean debug = false;

	public WorkHandler(ServerState state) {
		if (state != null) {
			this.state = state;
		}
	}

	/**
	 * override this method to provide processing behavior. T
	 * 
	 * @param msg
	 */
	public void handleMessage(WorkMessage msg, Channel channel) {
		if (msg == null) {
			// TODO add logging
			System.out.println("ERROR: Unexpected content - " + msg);
			return;
		}

		// if (debug)
		PrintUtil.printWork(msg);

		// TODO How can you implement this without if-else statements?
		try {
			if(msg.hasTrivialPing()){
				Logger.DEBUG(" The node: " + msg.getTrivialPing().getNodeId() + " Is Active to this IP: " + msg.getTrivialPing().getIP());
			}
			
			else if (msg.hasHeartBeatPacket() && msg.getHeartBeatPacket().hasHeartbeat()) {
				System.out.println(
						"HeartBeatPacket is recieved from " + msg.getHeartBeatPacket().getHeartbeat().getLeaderId());
				WorkMessage.Builder work = WorkMessage.newBuilder();
				work.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());
				HeartBeatPacket.Builder heartBeatPacket = HeartBeatPacket.newBuilder();
				heartBeatPacket.setUnixTimestamp(ServerUtils.getCurrentUnixTimeStamp());
				HeartBeatResponse.Builder heartBeatResponse = HeartBeatResponse.newBuilder();
				heartBeatResponse.setNodeId(1234);
				LogEntries.Builder logEntries = LogEntries.newBuilder();
				logEntries.setKey(1);
				logEntries.setUnixTimeStamp(ServerUtils.getCurrentUnixTimeStamp());
				heartBeatResponse.setLogEntries(0, logEntries);
				heartBeatPacket.setHeartBeatResponse(heartBeatResponse);
				work.setHeartBeatPacket(heartBeatPacket);

				channel.write(work.build());

			} else if (msg.hasHeartBeatPacket() && msg.getHeartBeatPacket().hasHeartBeatResponse()) {
				System.out.println(
						"Response is Received from " + msg.getHeartBeatPacket().getHeartBeatResponse().getNodeId());
			}

		} catch (Exception e) {
			e.printStackTrace();

		}

		System.out.flush();

	}

	/**
	 * a message was received from the server. Here we dispatch the message to
	 * the client's thread pool to minimize the time it takes to process other
	 * messages.
	 * 
	 * @param ctx
	 *            The channel the message was received from
	 * @param msg
	 *            The message
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WorkMessage msg) throws Exception {
		handleMessage(msg, ctx.channel());
		System.out.println("");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// logger.error("Unexpected exception from downstream.", cause);
		ctx.close();
	}

	

}
/*
 * copyright 2016, gash
 * 
 * Gash licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package adapter.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import adapter.server.proto.Global;
import adapter.server.proto.Global.GlobalCommandMessage;
import adapter.server.proto.Storage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import logger.Logger;
import raft.proto.Monitor.ClusterMonitor;

public class AdapterHandler extends SimpleChannelInboundHandler<Global.GlobalCommandMessage> {

	// protected ConcurrentMap<String, MonitorListener> listeners = new
	// ConcurrentHashMap<String, MonitorListener>();
	public AdapterHandler() {
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Global.GlobalCommandMessage globalCommandMessage)
			throws Exception {
		// TODO Auto-generated method stub

		if (globalCommandMessage.hasHeader()) {
			Logger.DEBUG("Inter-Cluster message recieved from " + globalCommandMessage.getHeader().getNodeId());
		}

		if (globalCommandMessage.hasQuery()) {

			if (globalCommandMessage.getQuery().getAction() == Storage.Action.GET) {
				GlobalCommandMessage response =QueryMessageHandler.getHandler(globalCommandMessage);

			ctx.channel().writeAndFlush(response);
			}

		}
	
		

	}

	/*
	 * public void addListener(MonitorListener listener) { if (listener == null)
	 * return;
	 * 
	 * listeners.putIfAbsent(listener.getListenerID(), listener); }
	 */

}
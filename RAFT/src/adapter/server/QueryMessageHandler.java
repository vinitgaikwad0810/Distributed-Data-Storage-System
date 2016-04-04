package adapter.server;

import adapter.server.proto.Global;
import adapter.server.proto.Global.GlobalCommandMessage;
import server.queue.ByteClient;

public class QueryMessageHandler {

	public static void postHandler() {

	}

	public static GlobalCommandMessage getHandler(GlobalCommandMessage globalCommandMessage) {

		try {
			ByteClient byteClient = new ByteClient(null);

			byte[] imageBytes = byteClient.get(globalCommandMessage.getQuery().getKey());
			String keyToBeSent = globalCommandMessage.getQuery().getKey();
			if (imageBytes == null) {

				for (AdapterServerConf.RoutingEntry routingEntry : AdapterServer.conf.getRouting()) {

					Global.GlobalCommandMessage globalCommandMessageToBeSent = AdapterUtils
							.prepareClusterRouteRequestForGET(routingEntry.getId(), keyToBeSent);
					AdapterClient.sendGlobalCommandMessage(globalCommandMessageToBeSent, routingEntry.getHost(),
							routingEntry.getPort());
					return null;
				}

			}else
				return AdapterUtils.ReponseBuilderForGET(imageBytes);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static void deleteHandler() {

	}

	public static void updateHandler() {

	}
}

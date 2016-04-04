package adapter.server;

import adapter.server.proto.Global;
import adapter.server.proto.Global.GlobalCommandMessage;
import server.queue.ByteClient;

public class QueryMessageHandler {

	public static GlobalCommandMessage postHandler(GlobalCommandMessage postGlobalRequest) {
		
		byte[] dataStore = postGlobalRequest.getQuery().getData().toByteArray();
		
		ByteClient byteClient = null;
		try {
			byteClient = new ByteClient(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String key=byteClient.post(dataStore);
		
		return AdapterUtils.ReponseBuilderForPOST(key);
		
	}

	public static GlobalCommandMessage getHandler(GlobalCommandMessage globalCommandMessage) {

		try {
			ByteClient byteClient = new ByteClient("/home/vinit/workspace/RAFT/resources/queue.conf");

			byte[] imageBytes = byteClient.get(globalCommandMessage.getQuery().getKey());
			String keyToBeSent = globalCommandMessage.getQuery().getKey();
			if (imageBytes == null) {

				for (AdapterServerConf.RoutingEntry routingEntry : AdapterServer.conf.getRouting()) {
					try {
						Global.GlobalCommandMessage globalCommandMessageToBeSent = AdapterUtils
								.prepareClusterRouteRequestForGET(routingEntry.getId(), keyToBeSent);
						AdapterClient.sendGlobalCommandMessage(globalCommandMessageToBeSent, routingEntry.getHost(),
								routingEntry.getPort());
					} catch (Exception e) {
						e.printStackTrace();
					}
					return null;
				}

			} else
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

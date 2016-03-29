package raft;

import logger.Logger;

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

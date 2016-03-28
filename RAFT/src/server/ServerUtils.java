package server;

public class ServerUtils {

	public static long getCurrentUnixTimeStamp() {
		long unixTime = System.currentTimeMillis() / 1000L;
		return unixTime;

	}
}

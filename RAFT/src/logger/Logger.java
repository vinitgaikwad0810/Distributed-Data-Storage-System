package logger;

public class Logger {

	public static void DEBUG(String toBeLogged) {

		System.out.println(toBeLogged);

	}

	public static void DEBUG(String toBeLogged, Exception ex) {

		System.out.println(toBeLogged);

		ex.printStackTrace();

	}

}

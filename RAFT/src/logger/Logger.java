package logger;

public class Logger {

	

	
	public static void DEBUG(String toBeLogged) {
		System.out.println("\n");
		System.out.println(toBeLogged);
		System.out.println("\n");
	}

	public static void DEBUG(String toBeLogged, Exception ex) {
		System.out.println("\n");
		System.out.println(toBeLogged);
		System.out.println("\n");
		ex.printStackTrace();
		
	}

}

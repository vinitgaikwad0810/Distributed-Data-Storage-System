package logger;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Logger {

	public static synchronized void DEBUG(String toBeLogged) {

		System.out.println(toBeLogged);
	//	writeDEBUG(toBeLogged);

	}

	public static synchronized void DEBUG(String toBeLogged, Exception ex) {

		System.out.println(toBeLogged);
	//	writeDEBUG(toBeLogged);
		ex.printStackTrace();

		

	}
	
	public static void writeDEBUG(String debug){
		try {
			Path file = Paths.get("debug.log");
			Files.write(file, debug.getBytes(), StandardOpenOption.APPEND);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		
	}

}

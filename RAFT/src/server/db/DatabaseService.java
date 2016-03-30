package server.db;

import java.sql.SQLException;

public class DatabaseService {
	
	protected PostgreSQL postgre;
	protected String url="jdbc:postgresql://localhost:5432/db275";
	protected String username="jagruti";
	protected String password="linux2015";
	protected String dbname="db275";
	protected String ssl="false";

	private static DatabaseService instance = null;
	
	public static DatabaseService getInstance() {
		if (instance == null) {
			instance = new DatabaseService();			
		}
		return instance;
	}
	
	private DatabaseService() {
		try {
			postgre = new PostgreSQL(url, username, password, dbname, ssl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public DatabaseClient getDb() {
		return postgre;
	}
}

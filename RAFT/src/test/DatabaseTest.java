package test;

import java.sql.SQLException;

import server.db.PostgreSQL;

public class DatabaseTest {
	protected PostgreSQL postgre;
	protected String url="jdbc:postgresql://localhost:5432/db275";
	protected String username="faisal";
	protected String password="6992";
	protected String dbname="db275";
	protected String ssl="false";
	
	public DatabaseTest() {
		try {
			postgre= new PostgreSQL(url, username, password, dbname, ssl);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void put(String key, String value) {
		postgre.put(key, value.getBytes());
	}

	public String post(String value) {
		return postgre.post(value.getBytes());
	}

	public byte[] get(String key) {
		return postgre.get(key);
	}

	public void delete(String key) {
		postgre.delete(key);
	}

	public static void main(String args[]) {
		DatabaseTest db = new DatabaseTest();
		String key = db.post("abc");
		System.out.println("key:" + key);
		System.out.println(db.get(key));
		
	}

	
}

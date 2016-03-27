package gash.router.server.db;

import java.nio.ByteBuffer;
import java.sql.*;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

public class PostgreSQL implements DatabaseClient {

	Connection conn = null;

	public PostgreSQL(String url, String username, String password, String dbname, String ssl) throws SQLException {
		
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		props.setProperty("ssl", ssl);
		conn = DriverManager.getConnection(url, props);
	}

	@Override
	public byte[] get(byte[] key) {
		Statement stmt = null;
		try {
			String sql="SELECT image FROM testtable where key="+key;
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				//
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO connection handling
		}

		return null;
	}

	@Override
	public byte[] put(byte[] image){
		Statement stmt = null;
		Random rand=new Random();
		char images= 'a';//ByteBuffer.allocate(4).putInt(rand.nextInt(200) + 1).array();
		char keys='s';
		byte[] key= ByteBuffer.allocate(4).putInt(rand.nextInt(200) + 1).array();
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO testtable VALUES ( '");
			sql.append(keys + "' , '" + images + "' );");
			
			stmt.executeUpdate(sql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO close connection, decide if need to keep open all time or
			// initiate new everytime
		}
		return key;
	}

}

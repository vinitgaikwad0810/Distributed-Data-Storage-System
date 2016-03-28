package gash.router.server.db;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.sql.*;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

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
	public byte[] get(String key) {
		Statement stmt = null;
		byte[] image=null; 
		System.out.println(key);
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT image FROM testtable where key = \""+key+"\";");
			ResultSet rs = stmt.executeQuery(sql.toString());
			while (rs.next()) {
				image=rs.getBytes(0);
				System.out.println("Image found");
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO connection handling
		}
		return image;
		
	}

	@Override
	public String put(byte[] image){
		Statement stmt = null;
		//Random rand=new Random();
		//byte[] key=ByteBuffer.allocate(4).putInt(rand.nextInt(200) + 1).array();
		String key = UUID.randomUUID().toString();
		//byte[] key= ByteBuffer.allocate(4).putInt(rand.nextInt(200) + 1).array();
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO testtable VALUES ( '");
			sql.append(key + "' , '" + image + "' );");
			
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

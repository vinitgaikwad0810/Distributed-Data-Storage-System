package gash.router.server.db;

import java.sql.*;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PostgreSQL implements DatabaseClient {

	Connection conn = null;

	public PostgreSQL(String url, String username, String password, String ssl) throws SQLException {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", username);
		props.setProperty("ssl", ssl);
		conn = DriverManager.getConnection(url, props);
	}

	@Override
	public Map<String, Object> get(String tablename) {
		Statement stmt = null;
		try {
			String sql = "SELECT * FROM " + tablename;
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
	public void put(String tableName, Map<String, Object> keyValue) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			StringBuilder sql = new StringBuilder();
			sql.append("INSERT INTO " + tableName + " ");
			StringBuilder columns = new StringBuilder();
			StringBuilder values = new StringBuilder();

			for (Map.Entry<String, Object> entry : keyValue.entrySet()) {
				columns.append(entry.getKey() + ",");
				values.append(entry.getValue() + ",");
			}

			sql.append("(" + columns.toString().substring(0, columns.length() - 1) + ")");
			sql.append(" VALUES ");
			sql.append("(" + values.toString().substring(0, values.length() - 1) + ")");
			stmt.executeUpdate(sql.toString());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// TODO close connection, decide if need to keep open all time or
			// initiate new everytime
		}
	}

}

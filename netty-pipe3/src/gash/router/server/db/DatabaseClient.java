package gash.router.server.db;

import java.util.Map;

public interface DatabaseClient {
	
	Map<String, Object> get(String key);
	
	void put(String tableName, Map<String, Object> keyValue);
	
}

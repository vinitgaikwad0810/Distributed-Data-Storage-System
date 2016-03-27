package gash.router.server.db;

import java.util.Map;

public interface DatabaseClient {
	
	byte[] get(byte[] key);
	
	byte[] put(byte[] image);//String tableName, Map<String, Object> keyValue);
	
}

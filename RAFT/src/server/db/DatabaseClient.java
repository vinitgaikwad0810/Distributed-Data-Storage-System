package server.db;

public interface DatabaseClient {
	
	byte[] get(String key);
	
	String post(byte[] image);
	
	public void put(String key, byte[] image);
	
	public void delete(String key);
	
}

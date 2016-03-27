package gash.router.client.api;

public interface ClientAPI {

	public byte[] get(String key);
	
	public void put(String key, byte[] data);
	
	public String post(byte[] data);
	
	public void delete(String key);	
}

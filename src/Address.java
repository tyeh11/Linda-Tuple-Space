import java.io.Serializable;

public class Address implements Serializable{
	private String ip;
	private int port;
	private long timestamp;
	
	Address(String ip, int port, long timestamp){
		this.ip = ip;
		this.port = port;
		this.timestamp = timestamp;
	}
	
	public String getIP(){
		return ip;
	}
	
	public int getPort(){
		return port;
	}
	
	public long getTimeStamp(){
		return timestamp;
	}
	
	public void setTimeStamp(long timestamp){
		this.timestamp = timestamp;
	}
	
	public boolean equals(Address address){
		//System.out.println("address equals test");
		return (this.ip.equals(address.getIP()) && this.port == address.getPort());
	}
	
	public String toString(){
		return "IP: " + ip + " Port: " + port;
	}
}

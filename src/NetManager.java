import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

public class NetManager {
	private HashMap<String, Address> net;
	private HashMap<Integer, String> idMap;
	private String hostName;
	private int netID;
	private long timestamp; // timeStamp is used for decide with host is master 
	
	NetManager(String hostName){
		net = new HashMap<String, Address>();
		idMap = new HashMap<Integer, String>();
		this.hostName = hostName;
		netID = -1;
		timestamp = 0;
	}
	
	public boolean isHostInList(String hostName){
		return net.containsKey(hostName);
	}
	
	public void removeHost(String hostName){
		net.remove(hostName);
	}
	
	public boolean addHost(HashMap<String, Address> newMap){ //return false if there is a host name maped to different ip/port
		
		for(Entry<String, Address> a : newMap.entrySet()){ //check if duplicate host name
			if( net.containsKey(a.getKey()) && !net.get(a.getKey()).equals(a.getValue()) ){
				return false;
			}
		}
		for(Entry<String, Address> a : newMap.entrySet()){ //add host name if no duplicate
			net.put(a.getKey(), a.getValue());
		}
		return true;
	}
	
	public void addHost(String hostName, String ip, int port, long timeStamp){
		//can we have same ip and different port number for different host name?
		Address newHost = new Address(ip, port, timeStamp);
		net.put(hostName, newHost);
		updateID(hostName);
		//System.out.println("host: " + hostName + " is added to list.");
	}
	
	public Address getHostAddress(String hostName){
		return net.get(hostName);
	}
	
	public Address getHostAddress(int id){
//		for(Entry<Integer, String> a: idMap.entrySet()){
//			System.out.println("id:" + a.getKey() + "host" + a.getValue());
//			System.out.println(net.get(a.getValue()).toString());
//		}
		return net.get(idMap.get(id));
	}
	
	public void setNetID(int id){
		netID = id;
	}
	
	public int getNetID(int id){
		return netID;
	}
	
	public void setTimeStamp(){
		if(timestamp == 0){
			timestamp = new Date().getTime();
		}
	}
	
	public long getTimestamp(){
		return timestamp;
	}
	
	public void updateAHostTimestamp(String hostName, long timestamp){
		this.getHostAddress(hostName).setTimeStamp(timestamp);
	}
	
	public HashMap<String, Address> compareHostMap(HashMap<String, Address> aMap){
		String[] newHosts = aMap.keySet().toArray(new String[aMap.size()]);
		for(int i = 0; i < aMap.size(); i++){
			if(net.containsKey(newHosts[i]) && net.get(newHosts[i]) == aMap.get(newHosts[i])){
				aMap.remove(newHosts[i]);
			}
		}
		return aMap;
	}
	
	public HashMap<String, Address> getNet(){
		return net;
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer("");
		
		for(Entry<String, Address> a: net.entrySet()){
			result.append("Host Name: " + a.getKey() + " IP: " + a.getValue().getIP() + " Port: " + a.getValue().getPort() + "\n");
		}
		return result.toString();
	}
	
	public boolean updateID(String hostName){
		if(net.containsKey(hostName) && !idMap.containsValue(hostName)){
			for(int i = 0; i < idMap.size(); i++){
				if(!idMap.containsKey(i)){
					idMap.put(i, hostName);
					return true;
				}
			}
			idMap.put(idMap.size(), hostName);
			return true;
		}
		return false;
	}
	
	public boolean updateID(HashMap<Integer, String> aMap){
		idMap = aMap;
		return updateID(hostName);
	}
	
	public String getHostName(){
		return hostName;
	}
	
	public HashMap<Integer, String> getIDMap(){
		return idMap;
	}
	
	public void printID(){
		for(Entry<Integer, String> a: idMap.entrySet()){
			System.out.println("ID:" + a.getKey() + " Host:" + a.getValue());
		}
	}
	
	public void replaceIDMap(HashMap<Integer, String> aMap){
		idMap = aMap;
	}
	
	public int hostNumber(){
		return net.size();
	}
}


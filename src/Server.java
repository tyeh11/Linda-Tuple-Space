import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server implements Runnable{
	ServerSocket ss;
	NetManager netManager;
	FileManager fileManager;
	TupleManager tupleManager;
	Client p1Client;
	
	Server(NetManager netManager, String hostName, Client p1Client, TupleManager tupleManager, FileManager fileManager){
		this.netManager = netManager;
		this.p1Client = p1Client;
		this.tupleManager = tupleManager;
		this.fileManager = fileManager;
		
		try {
			ss = new ServerSocket(0);
			String outString;
			Process p = Runtime.getRuntime().exec("hostname --ip-address");
			BufferedReader br = new BufferedReader(new InputStreamReader( p.getInputStream()));
			
			while( (outString = br.readLine()) != null){
				System.out.println(outString + " at port number: " + ss.getLocalPort());
				this.netManager.addHost(P1.localHostName, outString, ss.getLocalPort(), -1);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		try {
			ArrayList<Socket> clients= new ArrayList<Socket>();
			ArrayList<ServerConnection> hosts = new ArrayList<ServerConnection>();
			//Socket client1 = ss.accept();
			while(true){
				//System.out.println("Server starts!");
				clients.add(ss.accept());
				//System.out.println(clients.get(clients.size()-1).getInetAddress());
				//System.out.println("Port:" + clients.get(clients.size()-1).getLocalPort());
				ServerConnection aHost = new ServerConnection(clients.get(clients.size()-1), netManager, p1Client, tupleManager, fileManager);
				hosts.add(aHost);
				aHost.start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}

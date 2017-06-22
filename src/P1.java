
public class P1 {
	public static String localHostName = "";
	Server p1Server;
	Client p1Client;
	FileManager fileManager;
	NetManager netManager;
	TupleManager tupleManager;
	
	P1(String hostName){
		fileManager = new FileManager(localHostName);
		netManager = new NetManager(hostName);
		tupleManager = new TupleManager();
		p1Server = new Server(netManager, localHostName, p1Client, tupleManager, fileManager);
		p1Client = new Client(netManager, tupleManager, fileManager);
		fileManager.iniNetPrintStream(false);
		fileManager.iniTuplePrintStream(false);
	}
	
	public static void main(String args[]){
		localHostName = args[0];
		P1 a = new P1(localHostName);
		Thread aa = new Thread(a.p1Server);
		Thread bb = new Thread(a.p1Client);
		aa.start();
		bb.start();
	}
}

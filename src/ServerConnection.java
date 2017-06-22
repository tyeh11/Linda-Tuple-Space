import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

public class ServerConnection extends Thread{
	Socket aSocket;
	String hostName;
	NetManager netManager;
	TupleManager tupleManager;
	FileManager fileManager;
	BufferedReader aReader;
	ObjectInputStream oIn;
	ObjectOutputStream oOut;
	Client p1Client;
	
	ServerConnection(Socket aSocket, NetManager netManager, Client p1Client, TupleManager tupleManager, FileManager fileManager){
		this.aSocket = aSocket;
		this.netManager = netManager;
		this.p1Client = p1Client;
		this.tupleManager = tupleManager;
		this.fileManager = fileManager;
		try {
			oOut = new ObjectOutputStream(aSocket.getOutputStream());
			oOut.flush();
			oIn = new ObjectInputStream(aSocket.getInputStream());
		} catch (IOException e) {
			System.out.println("PrintWriter fail to create.");
			System.exit(0);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void run(){
		try {
			String command = (String)oIn.readObject();
			//System.out.println("Get Command: " + command);
			String receivedString;
			switch(command){
				case "add":
					String name = (String)oIn.readObject();
					if(P1.localHostName.equals(name)){
						oOut.writeObject("Ok");
					}
					else{
						oOut.writeObject("notOk");
						break;
					}
					oOut.writeObject(netManager.getNet()); //send netfile to client
					receivedString = (String)oIn.readObject(); //waiting for client ack to know if hostname wrong
					if(receivedString.equals("netReceived")){ //get net file
						//net file updated
						//System.out.println("netReceived");
						netManager.addHost((HashMap<String, Address>) oIn.readObject());
						fileManager.writeToNet(netManager.getNet()); //save net file to disk
						//update id map
						oOut.writeObject(netManager.getIDMap());
						netManager.updateID((HashMap<Integer, String>)oIn.readObject());
						oOut.writeObject("done");
						//oOut.writeObject(netManager.getHostName());
						//System.out.println(netManager.toString());
					}
					else{
						//fail to update
						System.out.println("same hostname with different ip/port");
						System.out.println("Terminated");
						System.exit(0);
					}
					oIn.close();
					oOut.close();
					aSocket.close();
					break;
					
				case "updateNet":
					oOut.flush();
					if(!netManager.addHost((HashMap<String, Address>) oIn.readObject())){
						System.out.println("same hostname with different ip/port");
						oOut.writeObject("hostNameError");
						System.out.println("Linda");
					}
					else {
						netManager.replaceIDMap((HashMap<Integer, String>) oIn.readObject());
						//netManager.printID();
						oOut.writeObject("done");
						//System.out.println("done");
						//System.out.println(netManager.toString());
					}
					break;
					
				case "in":
					Tuple temp1 = (Tuple)oIn.readObject();
					Tuple resultTuple;
					synchronized(tupleManager){
						while((resultTuple = tupleManager.readTuple(temp1)) == null){
							try {
								tupleManager.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						oOut.writeObject(resultTuple);
						if(((String)oIn.readObject()).equals("done")){
							tupleManager.removeTuple(resultTuple);
							fileManager.writeToTuple(tupleManager.getTupleList());	
						}
					}
					break;
					
				case "rd":
					Tuple temp2 = (Tuple)oIn.readObject();
					Tuple resultTuple2;
					synchronized(tupleManager){
						while((resultTuple2 = tupleManager.readTuple(temp2)) == null){
							try {
								tupleManager.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						oOut.writeObject(resultTuple2);
					}
					break;
					
				case "out":
					Tuple temp = (Tuple)oIn.readObject();
					synchronized(tupleManager){
						tupleManager.addTuple(temp);
						//tupleManager.printTuples();
						fileManager.writeToTuple(tupleManager.getTupleList());
						oOut.writeObject("done");
						tupleManager.notifyAll();
					}
			
			}
			//System.out.println("thread done");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}
	
}

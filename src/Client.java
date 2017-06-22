import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;


public class Client implements Runnable{

	private Scanner keyboardScanner;
	private TupleManager tupleManager;
	private NetManager netManager;
	private FileManager fileManager;
	private Object result;
	private String resultIP;
	
	Client(NetManager netManager, TupleManager tupleManager, FileManager fileManager){
		keyboardScanner = new Scanner(System.in);
		this.netManager = netManager;
		this.tupleManager = tupleManager;
		this.fileManager = fileManager;
	}
	
	private void updateResult(Object result){
		this.result = result;
	}
	
	@Override
	public void run() {
		String command;
		String inputString;
		String[] splitInput;
		Tuple aTuple;

		while(true){
			System.out.print("Linda> ");
			inputString = keyboardScanner.nextLine();
			splitInput = inputString.split("[()]");
			command = splitInput[0].trim();
			
			switch(command){
			case "add":
				//to check input form
				for(int i = 1; i < splitInput.length; i = i + 2){
					try{
						String[] addPara = splitInput[i].split(",");
						if(addPara.length != 3){
							throw new NumberFormatException();
						}
						String[] ipSection = addPara[1].trim().split(".");
						if(ipSection.length == 4){
							for(String a: ipSection){
								int temp = Integer.parseInt(a);
								if(temp > 0 && temp < 256){
									
								}
								else{
									throw new NumberFormatException();
								}
							}
						}
						int port = Integer.parseInt(addPara[2].trim());
//						System.out.println(addPara[1]);
//						System.out.println(addPara[1].trim());
						add(addPara[0].trim(), addPara[1].trim(), port);
					}
					catch(NumberFormatException e){
						System.out.println("Input is invalid.");
					}
				}
				break;
				
			case "in":
			case "rd":
				aTuple = tupleManager.buildTuple(splitInput[1].trim());
				if(aTuple != null){
					inOrRd(aTuple, command);
					System.out.println("Get tuple " + result + " on " + resultIP);
				}
				break;
				
			case "out":
				aTuple = tupleManager.buildTuple(splitInput[1].trim());
				if(aTuple != null){
					out(aTuple);
					System.out.println("put tuple " + aTuple + " on " + resultIP);
				}
				break;
//			case "print":
//				fileManager.readWholeNet();
			default:
				System.out.println("Invalid Command");
				break;
				}
			}
	}
	
	public void getCommand(){
		String command;
	}
	
	@SuppressWarnings("unchecked")
	public void add(String hostName, String ip, int port){
		try {
			Socket connection = new Socket(ip, port);
			ObjectOutputStream oOut = new ObjectOutputStream(connection.getOutputStream());
			oOut.flush();
			ObjectInputStream oIn = new ObjectInputStream(connection.getInputStream());
			
			oOut.writeObject("add"); //tell server this is add request.
			oOut.writeObject(hostName); //check host name
			if(!((String)oIn.readObject()).equals("Ok")){
				System.out.println("HostName mismatch");
				oIn.close();
				oOut.close();
				connection.close();
				throw new IOException();
			}
			if( !netManager.addHost( (HashMap<String, Address>) oIn.readObject() )){ //waiting for net file from another host
				// handle duplicate hostname(same hostname with different ip/port)
				System.out.println("same hostname with different ip/port");
				oOut.writeObject("hostNameError");
				oIn.close();
				oOut.close();
				connection.close();
			}
			else {
				//System.out.println(netManager.toString());
				oOut.writeObject("netReceived");
				oOut.writeObject(netManager.getNet());
				HashMap<Integer, String> tempMap = (HashMap<Integer, String>)oIn.readObject();
				
				if(netManager.getIDMap().size() == 1 && tempMap.size() == 1){
					netManager.updateID(hostName);
				}
				else if(netManager.getIDMap().size() != 1 && tempMap.size() == 1){
					netManager.updateID(hostName);
				}
				else if(netManager.getIDMap().size() == 1 && tempMap.size() != 1){
					netManager.updateID(tempMap);
				}
				else {
					//not yet done here
				}
				oOut.writeObject(netManager.getIDMap());
				if(((String)oIn.readObject()).equals("done")){
					//System.out.println("id updated");
					fileManager.writeToNet(netManager.getNet());
					//netManager.printID();
				}
				else{
					System.out.println("id fail");
				}
				oIn.close();
				oOut.close();
				connection.close();
				//oOut.writeObject("done");
				
				//broadcast newest net file to every host
				ClientSocket[] broadcastSockets = new ClientSocket[netManager.getNet().size()]; 
				int i = 0;
				
				for(Entry<String, Address> a: netManager.getNet().entrySet()){
					broadcastSockets[i] = new ClientSocket(a.getValue().getIP(), a.getValue().getPort(), "updateNet", null, null);
					broadcastSockets[i].start();
					i++;
				}
				
				for(int j = 0; j < broadcastSockets.length; j++){
					broadcastSockets[j].join();
				}
			}
			
			//System.out.println(netManager.toString());
		} catch (IOException e) {
			System.out.println(hostName + ": fail to add");
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
			
	}
	
	public void inOrRd(Tuple aTuple, String command){
		if(aTuple.isBroadcast){
			ClientSocket[] broadcastSockets = new ClientSocket[netManager.getNet().size()]; 
			int i = 0;
			for(Entry<String, Address> a: netManager.getNet().entrySet()){ //since clientsocket's constructor need clientsocket array do ini first
				broadcastSockets[i] = new ClientSocket(a.getValue().getIP(), a.getValue().getPort(), command, aTuple, broadcastSockets);
				i++;
			}
			
			for(int j = 0; j < broadcastSockets.length; j++){ //run all threads
				broadcastSockets[j].start();
			}
			
			for(int j = 0; j < broadcastSockets.length; j++){ //wait for reply
				try {
					broadcastSockets[j].join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else{
			Address address = netManager.getHostAddress(aTuple.md5Sum()%netManager.hostNumber());
			ClientSocket[] broadcastSockets = new ClientSocket[1]; 
			ClientSocket cs = new ClientSocket(address.getIP(),address.getPort(), "in", aTuple, broadcastSockets);
			broadcastSockets[0] = cs;
			cs.start();
			try {
				cs.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void out(Tuple tuple){
//		System.out.println(netManager.hostNumber());
//		System.out.println(tuple.md5Sum()%netManager.hostNumber());
		Address address = netManager.getHostAddress(tuple.md5Sum()%netManager.hostNumber());
		ClientSocket cs = new ClientSocket(address.getIP(), address.getPort(), "out", tuple, null);
		cs.start();
		try {
			cs.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	class ClientSocket extends Thread{
		Socket connection;
		ObjectOutputStream oOut;
		ObjectInputStream oIn;
		String command;
		Object para;
		ClientSocket[] otherSocket;
		
		ClientSocket(String ip, int port, String command, Object para, ClientSocket[] sockets){
			this.para = para;
			this.command = command;
			this.otherSocket = sockets;
			try {
				connection = new Socket(ip, port);
				oOut = new ObjectOutputStream(connection.getOutputStream());
				oOut.flush();
				oIn = new ObjectInputStream(connection.getInputStream());
				
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				oOut.writeObject(command);
				switch(command){
				
				case "updateNet":
					oOut.writeObject(netManager.getNet());
					oOut.writeObject(netManager.getIDMap());
					if(!((String)oIn.readObject()).equals("done")){
						System.out.println("fail to update net to IP:" + connection.getInetAddress() + "port: " + connection.getPort());
					}
					
					oIn.close();
					oOut.close();
					connection.close();
					break;
					
				case "in":
				case "rd":
					oOut.writeObject(para);
					Object temp = oIn.readObject();
					synchronized(otherSocket){
						oOut.writeObject("done");
						for(ClientSocket a: otherSocket){
							a.closeSocket();
						}
						result = (Tuple)temp;
						resultIP = connection.getInetAddress().toString().substring(1);
						updateResult(result);
					}
					//System.out.println("client");
					break;
				
				case "out":
					oOut.writeObject(para);
					resultIP = connection.getInetAddress().toString().substring(1);
					if(((String)oIn.readObject()).equals("done")){
						//System.out.println("out done");
					}
					else{
						//System.out.println("out fail");
					}
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		public void closeSocket(){
			try {
				oIn.close();
				oOut.close();
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class FileManager {
	private String pathString;
	private String hostName;
	private File net;
	private File tuple;	
	private ObjectOutputStream netOOutS;
	private ObjectOutputStream tupleOOutS;
	
	FileManager(String hostName){
		this.hostName = hostName;
		pathString = "/tmp/tyeh/linda/" + hostName;
		buildPathAndFile();
	}
	
	private void buildPathAndFile(){
		Path path = Paths.get(pathString);
		try {
			if(!Files.exists(path)){ //check if folder is exist
				Runtime.getRuntime().exec("mkdir -p " + pathString);
			}
			Runtime.getRuntime().exec("chmod -R 777" + " /tmp/tyeh");
			Runtime.getRuntime().exec("chmod -R 777" + " /tmp/tyeh/linda");
			Runtime.getRuntime().exec("chmod -R 777" + " /tmp/tyeh" + hostName);
			
			net = new File(pathString + "/net"); //if net is not exist, create it
			if(!net.exists() || net.isDirectory()){
				net.createNewFile();
			}
			Runtime.getRuntime().exec("chmod 666 " + pathString + "/net");
			
			tuple = new File(pathString + "/tuple"); //if tuple is not exist, create it
			if(!tuple.exists() || tuple.isDirectory()){
				tuple.createNewFile();
			}
			Runtime.getRuntime().exec("chmod 666 " + pathString + "/tuple");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Can't create folder or file.");
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, Address> readWholeNet(){
		try {
			ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(net));
			HashMap<String, Address> temp = (HashMap<String, Address>)oIn.readObject();
			for(Entry<String,Address> a: temp.entrySet()){
				System.out.println("HostName: " + a.getKey() + "IP: " + a.getValue().getIP() + "Port: " + a.getValue().getPort());
			}
			oIn.close();
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read Net File Fail!");
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ArrayList<Tuple> readWholeTuple(){
		try {
			ObjectInputStream oIn = new ObjectInputStream(new FileInputStream(tuple));
			@SuppressWarnings("unchecked")
			ArrayList<Tuple> temp = (ArrayList<Tuple>)oIn.readObject();
			for(Tuple a: temp){
				System.out.println(a);
			}
			oIn.close();
			return temp;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Read Net File Fail!");
			e.printStackTrace();
			return null;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public void iniNetPrintStream(boolean isAppened){
		try {
			netOOutS = new ObjectOutputStream(new FileOutputStream(net.getAbsolutePath(), isAppened));
		} catch (FileNotFoundException e) {
			System.out.println("Fail to initiate Net PrintStream");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Fail to open net");
			e.printStackTrace();
		}
	}
	
	public void iniTuplePrintStream(boolean isAppened){
		try {
			tupleOOutS = new ObjectOutputStream(new FileOutputStream(tuple.getAbsolutePath(), isAppened));
		} catch (FileNotFoundException e) {
			System.out.println("Fail to initiate Tuple PrintStream");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Fail to open tuple");
			e.printStackTrace();
		}
	}
	
	public void writeToNet(HashMap<String, Address> net){
		try {
			netOOutS.writeObject(net);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeToTuple(ArrayList<Tuple> tupleList){
		try {
			tupleOOutS.writeObject(tupleList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeNetPrintStream(){
		try {
			netOOutS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeTuplePrintStream(){
		try {
			tupleOOutS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Tuple implements Serializable{
	ArrayList<Object> tuple;
	boolean isBroadcast;
	Tuple(String[] tupleString, String[] tupleType) throws NumberFormatException{
		tuple = new ArrayList<Object>();
		isBroadcast = false;
		
		for(int i = 0; i < tupleString.length; i++){
			switch (tupleType[i]){
			case "String":  // format: "abc"
				if(tupleString[i].equals("?")){
					isBroadcast = true;
					tuple.add(new SpecialTuple(tupleType[i]));
				}
				else{
					tuple.add(tupleString[i].substring(1, tupleString[i].length() - 1));
				}
				break;
				
			case "Integer": // format: 123
				if(tupleString[i].equals("?")){
					isBroadcast = true;
					tuple.add(new SpecialTuple(tupleType[i]));
				}
				else {
					tuple.add(Integer.parseInt(tupleString[i]));
				}
				break;
				
			case "Float": // format: 12.3
				if(tupleString[i].equals("?")){
					isBroadcast = true;
					tuple.add(new SpecialTuple(tupleType[i]));
				}
				else {
					tuple.add(Float.parseFloat(tupleString[i]));
				}
				break;
			}
		}
	}
	
	public boolean equals(Tuple atuple){
		if(tuple.size() != atuple.size()) return false;

		for(int i = 0; i < tuple.size(); i++){
			//System.out.println(atuple.get(i).getClass());
			if(!tuple.get(i).equals(atuple.get(i))){
				return false;
			}
		}
		return true;
	}
	
	public int size(){
		return tuple.size();
	}
	
	public Object get(int index){
		return tuple.get(index);
	}
	
	public String toString(){
		StringBuffer result = new StringBuffer("(");
		for(Object a: tuple){
			String c[] = a.getClass().toString().split("\\.");
			String type = c[c.length - 1];
			
			switch(type){
			case "String":
				result.append("\"" + (String)a + "\", ");
				break;
			case "Integer":
				result.append(String.valueOf(a) + ", ");
				break;
			case "Float":
				result.append(String.valueOf(a) + ", ");
				break;
			}
		}
		String tupleString = result.substring(0, result.length()-2);
		return tupleString + ")";
	}
	
	public boolean isBroadcast(){
		return isBroadcast;
	}
	
	public class SpecialTuple implements Serializable{ //used for ?:int/String/float, should tuple with specialtuple should not be stored in to tuple list.
		String type;
		
		SpecialTuple(String type){
			this.type = type;
		}
		
		public String getType(){
			return type;
		}
		
		public boolean equals(Object o){
			return (o.getClass().toString().indexOf(type) >= 0);
		}
	}
	
	public int md5Sum(){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			return Math.abs(new BigInteger(1, md.digest(this.toString().getBytes())).intValue());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public ArrayList<Object> getList(){
		return tuple;
	}
}

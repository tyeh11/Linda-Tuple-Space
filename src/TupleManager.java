import java.util.ArrayList;

public class TupleManager {
	
	private ArrayList<Tuple> tupleList;
	
	TupleManager(){
		tupleList = new ArrayList<Tuple>();
	}
	
	public Tuple buildTuple(String tupleString){
		String[] splitedString = tupleString.split(",");
		ArrayList<String> tupleData = new ArrayList<String>();
		ArrayList<String> type = new ArrayList<String>();
		try{
			for(int i = 0; i < splitedString.length; i++){
				if(splitedString[i].trim().charAt(0) == '?'){
					String[] temp = splitedString[i].trim().split(":");
					tupleData.add("?");
					String dataType = temp[temp.length-1].trim();
					switch(dataType){
					case "String":
					case "string":
						type.add("String");
						break;
						
					case "int":
						type.add("Integer");
						break;
						
					case "float":
						type.add("Float");
						break;
					default:
						throw new NumberFormatException();
					}
				}
				else if(splitedString[i].trim().charAt(0) == '\"'){
					StringBuffer temp = new StringBuffer("");
					while(splitedString[i].trim().charAt(splitedString[i].trim().length() - 1) != '\"'){
						temp.append(splitedString[i++].trim() + ",");
						if(i > splitedString.length - 1){
							throw new NumberFormatException(); // handle format: ("wrwefwefwefwee)
						}
					}
					temp.append(splitedString[i].trim());
					tupleData.add(temp.toString());
					type.add("String");
				}
				else if(splitedString[i].contains(".")){
					//Float.parseFloat(splitedString[i]);
					tupleData.add(splitedString[i].trim());
					type.add("Float");
				}
				else{
					tupleData.add(splitedString[i].trim());
					type.add("Integer");
				}
			}
			Tuple aTuple = new Tuple(tupleData.toArray(new String[tupleData.size()]), type.toArray(new String[tupleData.size()]));
			return aTuple;
		}
		catch(NumberFormatException e){
			System.out.println("Invalid tuple formant");
		}
		
		return null;
	}
	
	public void addTuple(Tuple tuple){
		tupleList.add(tuple);
	}
	
	public Tuple getTuple(Tuple tuple){
		for(Tuple a: tupleList){
			if(tuple.equals(a)){
				tupleList.remove(a);
				return a;
			}
		}
		return null;
	}
	
	public Tuple readTuple(Tuple tuple){
		for(Tuple a: tupleList){
			if(tuple.equals(a)){
				return a;
			}
		}
		return null;
	}
	
	public void printTuples(){
		for(Tuple a: tupleList){
			System.out.println(a.toString());
		}
	}
	
	public boolean removeTuple(Tuple aTuple){
		return tupleList.remove(aTuple);
	}
	
	public ArrayList<Tuple> getTupleList(){
		return tupleList;
	}
}

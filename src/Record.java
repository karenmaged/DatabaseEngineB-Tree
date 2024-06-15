import java.io.Serializable;
import java.util.Hashtable;
import java.util.Map.Entry;


public class Record implements Serializable{
Object[] record; 
private static final long serialVersionUID = 2334452008303102636L;

//
//public static void farah() {
//	
//}

 public Record(Object [] h){
	 this.record=h;
 }
 public String toString(){
	 String res="";
	 for (int j =0; j<record.length;j++) {
		//System.out.println("in");
		 if(record[j]!= null){
			 String value = record[j].toString();
			 if(j== record.length -1){

			        res+=value;  
			   }
			  else{
			   	 res+=value+",";
			  }
		 }
		 else{
			 if(j== record.length -1){

			        res += "null";
			   }
			  else{
				  res+="null, ";
			  }
		 }
         
         
	 }
//	 int toot = res.length();
//	 toot = toot.deleteCharAt(res.length() - 1);

	 return res;
 }
}

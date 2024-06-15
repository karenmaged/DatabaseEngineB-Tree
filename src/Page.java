import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;


public class Page implements Serializable {
	
//	private static final long serialVersionUID = -9028051588654028230L;
	public int nRecords;
	public Vector <Record> tuplesArray ;
	public Table table;
	private static final long serialVersionUID = -9028051588654028230L;
public static void farah() {
		
	}	
	
	public Page ( Table t){
		tuplesArray = new Vector<>();
		table=t;
		
		Properties prop= new Properties();
		 String nrec="";
		try (FileInputStream fileInputStream = new FileInputStream("src\\DBApp.config")) {
            prop.load(fileInputStream);

        nrec = prop.getProperty("MaximumRowsCountinPage");
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		nRecords = Integer.parseInt(nrec) ;
		this.serializePage(t.name+t.npages+".ser");
		MINMAX m= new MINMAX(null,null);
		t.MMpages.add(m);
		t.pageNames.add(t.name+t.npages);	
	}
	
	
	public String toString(){
		String res="";
		for(int i=0;i<tuplesArray.size();i++){
			Record r= tuplesArray.get(i);
			res+=r.toString();
			res+="\n";
		}
		return res;
	}
	
	
	public void serializePage (String filename){
		 try {
			 FileOutputStream temp=	 new FileOutputStream(filename);
			 ObjectOutputStream out = new ObjectOutputStream(temp);
		     out.writeObject(this);
		     temp.close(); 
		     out.close();
		}
		catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	public static Page deserialisePage( String filename){
		Page tmp = null;
		try {
	         FileInputStream fileIn = new FileInputStream(filename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	          tmp  = (Page) in.readObject();
	         in.close();
	         fileIn.close();
	         return tmp;
	      } catch (IOException i) {
	         i.printStackTrace();
	         return tmp;
	      } catch (ClassNotFoundException c) {
	         System.out.println("this class is not found");
	         c.printStackTrace();
	         return tmp;
	      }
	}
	
	
public static void main(String [] args){
	
	
}
	
	
	
	

}

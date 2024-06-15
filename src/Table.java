import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;




public class Table implements Serializable{//transient
	public String name; 
	public String primary;
	 public ArrayList <String> pageNames = new ArrayList<String>();
	 public ArrayList <MINMAX> MMpages = new ArrayList<MINMAX>();
	 public ArrayList <String> bplustreenames = new ArrayList<String>();
	 public ArrayList <Integer> bpluscol = new ArrayList<Integer>();
	 public String [] arrangementcol;
	 public  int npages=0;
	 private static final long serialVersionUID = 1L;
	 public static void farah() {
			
		}
	 
	public Table(String n, String p, String [] arrangementcol){
		this.name=n;
		this.primary=p;
		this.arrangementcol=arrangementcol;
	}
	public void serializeTable(String filename){
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
	
	
	public static Table deserialiseTable( String filename){
		Table tmp = null;
		try {
	         FileInputStream fileIn = new FileInputStream(filename);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	          tmp  = (Table) in.readObject();
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
	
	
	
	
	
	public void addrec(Record r){// do the index to search
	
		}
		
	}
		
	


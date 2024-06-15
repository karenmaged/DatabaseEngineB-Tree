import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;


public class DBApp {
	public ArrayList <String> Tablenames = new ArrayList<String>();
	//public ArrayList <Table> tables = new ArrayList<Table>();
	
	public void init( ) {}
	public void createTable(String strTableName, String strClusteringKeyColumn, Hashtable<String,String> htblColNameType)
			 throws DBAppException
	{
		if(checkmytable("src\\metadata.csv",strTableName )){
			throw new DBAppException("This table already exists");
			
		}
		
		else{
			
		
		 String primtype="";
		 String [] arrangementcol= new String[htblColNameType.size()];
		try {
			FileWriter writer = new FileWriter("src\\metadata.csv", true);
			Set<Entry<String, String>> entry = htblColNameType.entrySet();
			int arr=1;
			for (Entry<String, String> entryx : entry) {
                String key = entryx.getKey();
                String value = entryx.getValue();
                if(value.equals( "java.lang.Integer" ) || value.equals( "java.lang.Double" ) || value.equals( "java.lang.String" )){
                writer.append(strTableName+",");
                String k= "False";
                if(key.equals(strClusteringKeyColumn)){
                	k="True";
                	primtype=value;
                	arrangementcol[0]=strClusteringKeyColumn;
                }
                else{
                	arrangementcol[arr]=key;
                	arr++;
                }
                writer.append(String.join(",", key, value,k,null, null));
                writer.append("\n");
                }
                else{
                	throw new DBAppException("Data types are either double, integer or string ");
                }
            }
            writer.close();

        } catch (IOException e) {
            System.err.println("There is an Error" + e.getMessage());
            e.printStackTrace();
        }
		
		Table t= new Table(strTableName, primtype, arrangementcol);
		t.serializeTable(strTableName+".ser");
		Tablenames.add(strTableName);
		}
	}
	public static  boolean checkexistence(Hashtable<String,Object> htblColNameValue,  String strTableName) throws DBAppException{
		Set<Entry<String, Object>> entry = htblColNameValue.entrySet();
		for (Entry<String, Object> entryx : entry) {
			if(!checkcolumn("src\\metadata.csv",strTableName, entryx.getKey())){
				return false;
		}
			
            }
		return true;
	}
	public void insertIntoTable(String strTableName,Hashtable<String,Object> htblColNameValue)throws DBAppException
	{
		
		Object s= checkprimkey("src\\metadata.csv", strTableName, htblColNameValue);
		 if(!checktable("src\\metadata.csv", strTableName)){
			throw new DBAppException("Table does not exist");
		}
		 else if(s == null){
			throw new DBAppException("Please enter a primary key");
		}
		
		else{
			Table tmp= Table.deserialiseTable(strTableName+".ser");
			
			 if(!checkexistence(htblColNameValue,strTableName)){
				throw new DBAppException("Column does not exist");
			}
			 else if(!(checkforcorrectdatatypes("src\\metadata.csv", strTableName, htblColNameValue))){
				throw new DBAppException("Invalid columns or types");
			}
			
			
			
			
			else if(tmp.pageNames.size()==0){
			//	System.out.println("ana da5alt hena");
				Page p= new Page(tmp);
				tmp.npages+=1;
				p= Page.deserialisePage(tmp.pageNames.get(0)+".ser");
				Object [] rec= makerecord(tmp, htblColNameValue);
				Record record= new Record(rec);
				p.tuplesArray.add(record);
				updateindex(tmp,p,0,0,s);
				updateminmax(tmp,p,0);
				p.serializePage(tmp.pageNames.get(0)+".ser");
				tmp.serializeTable(strTableName+".ser");
				
			}
			else{
				int posp= pageposition(s,tmp);
				Page p= Page.deserialisePage(tmp.pageNames.get(posp)+".ser");
				int pos= recordposition(p, s);
						    
						    
	    
	     Object [] rec= makerecord(tmp, htblColNameValue);
	//     System.out.println("ana el position btat3ty "+ pos+" " +posp);
	    Record record= new Record(rec); 
	  	p.tuplesArray.add(pos, record);
	//  	System.out.println(p.toString());
	  	
		while(p.tuplesArray.size()>p.nRecords){
		//	System.out.println(p.tuplesArray.size()+" "+ p.nRecords);
			updateindex(tmp, p, pos,posp,s);

			Record r= p.tuplesArray.get(p.tuplesArray.size()-1);
			p.tuplesArray.remove(p.tuplesArray.size()-1); //delete el key bta3 akher wahda 
			
		
			
			
	//	   System.out.println("I am shifting");
		
		updateminmax(tmp,p,posp);
		p.serializePage(tmp.pageNames.get(posp)+".ser");
	    if (tmp.pageNames.size() != posp+1){
	    //	System.out.println("I have a page under me");
	    	p=Page.deserialisePage(tmp.pageNames.get(posp+1)+".ser");
	    	p.tuplesArray.add(0,r);
	    }
	    else{
	    //	System.out.println("I dont have a page under me");
	      p = new Page(tmp);
	      tmp.npages+=1;
	      p= Page.deserialisePage(tmp.pageNames.get(tmp.pageNames.size()-1)+".ser");
	      p.tuplesArray.add(r);
		 // p.serializePage(tmp.pageNames.get(0)+".ser");
	    }
	    posp=posp+1;
	    pos=0;
			
	}
		updateminmax(tmp,p,posp);			    
		updateindex(tmp, p, pos,posp,s);					 
		p.serializePage(tmp.pageNames.get(posp)+".ser");
        tmp.serializeTable(strTableName+".ser");							
					
				
				 
		
			}
			}
		
		
		
		
	}
	
	public static void updateminmax(Table tmp, Page p, int posp){
		Object min= p.tuplesArray.get(0).record[0];
		Object max= p.tuplesArray.get(p.tuplesArray.size()-1).record[0];
		MINMAX nmm= new MINMAX(min, max);
		tmp.MMpages.remove(posp);
		tmp.MMpages.add(posp, nmm);
	}
	
	public static void updateindex(Table tmp, Page p, int pos, int posp, Object s){
		
		for(int i=0;i<tmp.bplustreenames.size();i++){
			BTree t = BTree.deserialiseBTree(tmp.bplustreenames.get(i)+".ser");
			String colname= getcolumnname(tmp.bplustreenames.get(i));
			int col= getcolumnindex(colname, tmp);
			int n= p.tuplesArray.size();
			for(int j=pos+1;j<n;j++){
				if((p.tuplesArray.get(j)).record[col].getClass().getName().equals("java.lang.Integer")){
					Integer key= (int) (p.tuplesArray.get(j).record[col]);
					Integer key1= (int) (p.tuplesArray.get(j-1).record[col]);

					if(key!=key1) {
					 Reference r= new Reference(posp,j-1);
						t.delete(key,r );
					}
					 
						Reference ref= new Reference(posp,j); //j msh j plus 1
						LinkList val= new LinkList();
						val.insertLast(ref);
						t.insert(key,val);
				//		System.out.println("ana b shift");

				}
				else if((p.tuplesArray.get(j)).record[col].getClass().getName().equals("java.lang.Double")){
					double key= (Double) (p.tuplesArray.get(j).record[col]);
					double key1= (Double) (p.tuplesArray.get(j-1).record[col]);

					if(key!=key1) {
				//		 System.out.println("ana b delet:"+" "+ key +"ana b test el common");

					 Reference r= new Reference(posp,j-1);
						t.delete(key,r );
					}
				//	 System.out.println("ana b insert:"+" "+ key +"ana b test el common");

						Reference ref= new Reference(posp,j); //j msh j plus 1
						LinkList val= new LinkList();
						val.insertLast(ref);
						t.insert(key,val);
				//		System.out.println("ana b shift");


				}
				else{
					String key=  (p.tuplesArray.get(j).record[col]).toString();
					String key1=  (p.tuplesArray.get(j-1).record[col]).toString();

					if(!(key.equals(key1))) {
					 Reference r= new Reference(posp,j-1);
						t.delete(key,r );
					}
					
						Reference ref= new Reference(posp,j); //j msh j plus 1
						LinkList val= new LinkList();
						val.insertLast(ref);
						t.insert(key,val);
					//	System.out.println("ana b shift");

				}
			 
			}
			if(p.tuplesArray.size()>p.nRecords && pos!=p.nRecords){
				if((p.tuplesArray.get(p.tuplesArray.size()-1)).record[col].getClass().getName().equals("java.lang.Integer")){
					
					Integer key= (int) (p.tuplesArray.get(p.tuplesArray.size()-1).record[col]);	
				//	System.out.println("I delete:"+" "+ key);
					 Reference r= new Reference(posp,p.tuplesArray.size()-1);
				//		System.out.println("I delete:"+" "+ r.page+" "+r.record);

						t.delete(key,r );
					//	System.out.println("ana b delete a5er wahda fl page");
				}
				else if ((p.tuplesArray.get(p.tuplesArray.size()-1)).record[col].getClass().getName().equals("java.lang.Double")){
					Double key= (double) (p.tuplesArray.get(p.tuplesArray.size()-1).record[col]);	
					 Reference r= new Reference(posp,p.tuplesArray.size()-1);
			//		 System.out.println(key+"key and ref "+r.page +" "+r.record);
						t.delete(key,r );
			//			System.out.println("ana b delete a5er wahda fl page");
	
				}
				else{
					String key=  (p.tuplesArray.get(p.tuplesArray.size()-1).record[col]).toString();	
					 Reference r= new Reference(posp,p.tuplesArray.size()-1);
						t.delete(key,r );
					//	System.out.println("ana b delete a5er wahda fl page");
	
				}
				
				}
			if(pos<p.nRecords) {
			if ((p.tuplesArray.get(pos)).record[col].getClass().getName().equals("java.lang.Integer")){
			 int key1= (int)((p.tuplesArray.get(pos)).record[col]);
		//	 System.out.println("I add in pos:"+ key1);
			Reference ref1= new Reference(posp,pos);
		//	 System.out.println("in :"+ ref1.page+" "+ref1.record);

			LinkList val1= new LinkList();
			val1.insertLast(ref1);
			t.insert(key1,val1);//ezay ha7ot s f kolo
		//	System.out.println("put the new record");

			}
			else if ((p.tuplesArray.get(pos)).record[col].getClass().getName().equals("java.lang.Double")){
				 double key1= (Double)((p.tuplesArray.get(pos)).record[col]);
				Reference ref1= new Reference(posp,pos);
				LinkList val1= new LinkList();
				val1.insertLast(ref1);
				t.insert(key1,val1);// ezay ha7ot s f kolo
			//	System.out.println("put the new record");

			}
			else{
				String key1= ((p.tuplesArray.get(pos)).record[col]).toString();
				Reference ref1= new Reference(posp,pos);
				LinkList val1= new LinkList();
				val1.insertLast(ref1);
				t.insert(key1,val1);// ezay ha7ot s f kolo
			//	System.out.println("put the new record");

			}
			}
			 t.serializeBtree(tmp.bplustreenames.get(i)+".ser");

		}
		
		
	}
	
	
	public static Object [] makerecord(Table tmp, Hashtable htblColNameValue){ 
	 Object [] rec = new Object[tmp.arrangementcol.length];	
     Set<Entry<String, Object>> entry = htblColNameValue.entrySet();
     for (Entry<String, Object> entryx : entry) {
    	 for (int i = 0; i < tmp.arrangementcol.length; i++) {
             if (tmp.arrangementcol[i].equals(entryx.getKey())) {
                rec[i]=entryx.getValue();
             }
         }
        
     }
     return rec;
	}
	public static String getcolumnname(String BTreename){
		try{
    		BufferedReader rdr= new BufferedReader(new FileReader("src\\metadata.csv"));
    		String csvline="";
    		String primarykey="";
    		while((csvline=rdr.readLine())!= null){
    			String [] prim= csvline.split(",");
    			if(prim[4].equals(BTreename) ){
    				return prim[1];
    				
    			}
    		}
    		return null;
    		
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return null;
    	}
    	
	}
	public static int  getcolumnindex(String colname, Table t){
		for (int i = 0; i < t.arrangementcol.length; i++) {
            if (t.arrangementcol[i].equals(colname)) {
               return i;
            }
        }
		return -1;
    	
	}
	
    public static Object checkprimkey(String filepath, String Tablename,Hashtable<String,Object> htblColNameValue ){
    	try{
    		BufferedReader rdr= new BufferedReader(new FileReader(filepath));
    		String csvline="";
    		String primarykey="";
    		while((csvline=rdr.readLine())!= null){
    			String [] prim= csvline.split(",");
    			//System.out.println(prim[3]);
    			if(prim[0].equals(Tablename) && (prim[3].equalsIgnoreCase("True"))){
    				
    				primarykey=prim[1];
    				break;
    			}
    		}
    		Set<Entry<String, Object>> entry = htblColNameValue.entrySet();
    		for(Entry<String, Object> entry1 : entry){
    		

    			if(entry1.getKey().equals(primarykey) && (entry1.getValue()!=null)){
    				return entry1.getValue();
    			}
    		}
    		return null;
    		
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return null;
    	}
    	
    }
    
    public static boolean checkforcorrectdatatypes(String filepath, String Tablename,Hashtable<String,Object> htblColNameValue) throws DBAppException{
    	
    	try{
    		if (htblColNameValue.size() == 0) {
    			return true;
    		}
    		BufferedReader rdr= new BufferedReader(new FileReader(filepath));
    		String csvline="";
    		boolean right=false;
    		while((csvline=rdr.readLine())!= null){
    			String [] prim= csvline.split(",");
    			if(prim[0].equals(Tablename) && htblColNameValue.containsKey(prim[1])){
    			//	System.out.println(htblColNameValue.get(prim[1]).getClass().getName());
    				if(htblColNameValue.get(prim[1]).getClass().getName().equals(prim[2]))
    				right=true;
    				else{
    					right=false;
        				throw new DBAppException("Invalid Type of: "+ prim[1]);
    				}
    			}
    		}
    		return right;

    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    	
    }
    
    public static boolean checktable (String filepath, String tablename) throws DBAppException{
    	try {
    		
    		BufferedReader br = new BufferedReader(new FileReader(filepath));
    		String line = "";
    		//boolean match = false;
    		String[] arr;
    		while ((line = br.readLine()) != null) {
    			arr = line.split(",");
    			if (arr[0].equals(tablename)) {
    			return true;
    			}
    		}
    		
    	} 
    	catch (IOException e) {
    		e.printStackTrace();
    	}
    	throw new DBAppException("wrong table");
    }
    
    public static boolean checkmytable (String filepath, String tablename) throws DBAppException{
    	try {
    		
    		BufferedReader br = new BufferedReader(new FileReader(filepath));
    		String line = "";
    		//boolean match = false;
    		String[] arr;
    		while ((line = br.readLine()) != null) {
    			arr = line.split(",");
    			if (arr[0].equals(tablename)) {
    			return true;
    			}
    		}
    		
    	} 
    	catch (IOException e) {
    		e.printStackTrace();
    	}
        return false;
    }
    
    
    public static boolean checkcolumn(String filepath, String tablename, String colname) throws DBAppException{
    	boolean match = false;
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(filepath));
    		String line = "";
    	
    		while ((line = br.readLine()) != null) {
    			String[] arrline = line.split(",");
    			if ((arrline[0].equals(tablename)) && (arrline[1].equals(colname))) {
    				match = true;
    				break;	
    			}
    		}
    		if (!match) {
    			throw new DBAppException("Column does not exist in the specified table");
    		}
    		
    	}
    	catch (IOException e){
    		e.printStackTrace();	
    	}
    	return match;
    }
    
    
    public static boolean checkindex(String filepath, String tablename, String colname) throws DBAppException{
		boolean match = false;
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(filepath));
    		String line = "";
    		while ((line = br.readLine()) != null) {
    			String[] arrline = line.split(",");
    			if ((arrline[0].equals(tablename)) && (arrline[1].equals(colname))&& (arrline[4].equals("null"))) {
    				match = true;
    				break;	
    			}
    		}
    		if (!match) {
    			throw new DBAppException("Index on this column already exists");
    		}
    		
    	}
    	catch (IOException e){
    		e.printStackTrace();	
    	}
    	return match;
    }	
    
    public void addindex(String filepath, String tablename, String colname, String indxname) throws DBAppException{
		String csvFile = filepath;
		 
       
        try {
           
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(","); // Assuming comma-separated values
                if (/*data.length > 4 &&*/ data[4].equals("null")&& data[0].equals(tablename)&& data[1].equals(colname)) {
                   
                    data[4] = indxname;
                    line = String.join(",", data);
                }
                sb.append(line).append("\n");
            }
            br.close();

            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));
            bw.write(sb.toString());
            bw.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   
    public int getIndex(String strTableName, String strColName){
		int index = 0;
		Table t = Table.deserialiseTable(strTableName+".ser");
		String arr[] = t.arrangementcol;
		for (int i =0; i<arr.length; i++){
			
			if (arr[i].equals(strColName)){
				index = i;
		}
		}
		
		t.serializeTable(strTableName+".ser");
		return index;
	}
    
    public static String getcolumntype(String filepath, String tablename, String colname) throws DBAppException{
    	String type ="";
    	try {
    		BufferedReader br = new BufferedReader(new FileReader(filepath));
    		String line = "";
    		boolean match = false;
    		
    		while ((line = br.readLine()) != null) {
    			String[] arrline = line.split(",");
    			if ((arrline[0].equals(tablename)) && (arrline[1].equals(colname))) {
    				match = true;
    				type= arrline[2];		
    				break;	
    			}
    		}
    		if (!match) {
    			throw new DBAppException("Column not found");
    		}
    		
    	}
    	catch (IOException e){
    		e.printStackTrace();	
    	}
    	return type;
    }
	
	public void createIndex(String strTableName, String strColName, String strIndexName) throws DBAppException{
		
		String filepath="src\\metadata.csv";
		if (checktable(filepath, strTableName)&&checkcolumn(filepath, strTableName,strColName)&&checkindex(filepath, strTableName,strColName)){
			String type= getcolumntype(filepath,strTableName,strColName);
		  //    System.out.println("I am in");
		int index = getIndex(strTableName, strColName);
//		System.out.println(index);
		if (type.equals("java.lang.Integer")){
		
		BTree<Integer, LinkList> strIndexName1 = new BTree<>();
		
		Table t = Table.deserialiseTable(strTableName+".ser");
		t.bplustreenames.add(strIndexName);
		for (int i =0; i<t.pageNames.size();i++){
			String page=t.pageNames.get(i);
			Page p = Page.deserialisePage(page+".ser");
			for (int j =0; j<p.tuplesArray.size();j++){
				Record r = p.tuplesArray.get(j);
				Reference ref = new Reference(i,j);
				LinkList reflist = new LinkList();
				reflist.insertLast(ref);
				
				
				Integer key=(Integer)r.record[index]; 
				strIndexName1.insert(key, reflist);}
			
			p.serializePage(page +".ser");}
		t.serializeTable(strTableName+".ser");
		
		
		strIndexName1.serializeBtree(strIndexName+".ser");
		strIndexName1.displayTree();
		}
		
		if (type.equals("java.lang.String")){
			
			BTree<String, LinkList> strIndexName1 = new BTree<>();
			
			Table t = Table.deserialiseTable(strTableName+".ser");
			t.bplustreenames.add(strIndexName);
			for (int i =0; i<t.pageNames.size();i++){
				String page=t.pageNames.get(i);
				Page p = Page.deserialisePage(page+".ser");
				for (int j =0; j<p.tuplesArray.size();j++){
					Record r = p.tuplesArray.get(j);
					Reference ref = new Reference(i,j);
					LinkList reflist = new LinkList();
					reflist.insertLast(ref);
					
					
					String key=(String)r.record[index]; 
					strIndexName1.insert(key, reflist);}
				
				p.serializePage(page +".ser");}
			t.serializeTable(strTableName+".ser");
			strIndexName1.serializeBtree(strIndexName+".ser");
			}
		
		if (type.equals("java.lang.Double")){
			
			BTree<Double, LinkList> strIndexName1 = new BTree<>();
			
			Table t = Table.deserialiseTable(strTableName+".ser");
			t.bplustreenames.add(strIndexName);
			for (int i =0; i<t.pageNames.size();i++){
				String page=t.pageNames.get(i);
				Page p = Page.deserialisePage(page+".ser");
				for (int j =0; j<p.tuplesArray.size();j++){
					Record r = p.tuplesArray.get(j);
					Reference ref = new Reference(i,j);
					LinkList reflist = new LinkList();
					reflist.insertLast(ref);
					
					
					Double key=(Double)r.record[index]; 
					strIndexName1.insert(key, reflist);}
				
				p.serializePage(page +".ser");}
			t.serializeTable(strTableName+".ser");
			strIndexName1.serializeBtree(strIndexName+".ser");
			}
		
		addindex(filepath, strTableName, strColName, strIndexName);
		
		
		
		}}
    
	
	public void deleteFromTable(String strTableName, Hashtable<String,Object> htblColNameValue) 
   		 throws DBAppException, FileNotFoundException, IOException, ClassNotFoundException{
	   	
   	Set<Entry<String, Object>> entrySet = htblColNameValue.entrySet();
   	List<Entry<String, Object>> entryList = new ArrayList<>(entrySet);
 //  System.out.println(entryList.size());
   
   	 if(!checktable("src\\metadata.csv", strTableName)){
		throw new DBAppException("Table does not exist");
	}
 
   else if(!checkexistence(htblColNameValue,strTableName)){
		throw new DBAppException("Column does not exist");
	}
   else if(!(checkforcorrectdatatypes("src\\metadata.csv", strTableName, htblColNameValue))){
		throw new DBAppException("Invalid columns or types");
	}
	
	
	else {
		Table t = Table.deserialiseTable(strTableName+".ser");
   
   
   	if (entryList.size()==0) {
   		for(int i=0;i<t.pageNames.size();i++) {
	    File file = new File(t.pageNames.get(i)+".ser") ;
	   file.delete();
	    	t.pageNames.remove(t.pageNames.get(i));
	    	t.MMpages.remove(i);
   		}
   		for (int i = 0; i < t.bplustreenames.size(); i++) {
   			BTree bt = BTree.deserialiseBTree(t.bplustreenames.get(i) + ".ser");
   			bt.deleteAll();
   			bt.serializeBtree(t.bplustreenames.get(i) + ".ser");
   		}
   		t.serializeTable(strTableName + ".ser");
   		return;
   	}
   	String farah = entryList.get(0).getKey() ;
   	Object ob  = entryList.get(0).getValue();
   	LinkList res = search(ob ,t , farah ,"src\\metadata.csv");
   	

   	for (int i = 1; i < entryList.size(); i ++) {
   	   
   		String st = entryList.get(i).getKey() ;
       	Object obj  = entryList.get(i).getValue();
       	LinkList res2 = search(obj ,t , st ,"src\\metadata.csv");
//       	System.out.println(res);
//       	System.out.println(res2);

       res = operation (res, res2 , "AND");
       	
       	
   }
 //  	System.out.println(res);
   	
   	while ((res!=null) && !(res.isEmpty()) ) {
   		Reference r =(Reference) res.removeFirst();
   		Page p = Page.deserialisePage(t.pageNames.get(r.page) + ".ser");
   		
  
   		
   		
   		
   	//   	System.out.println(res);
   		for (int j=0 ; t.bplustreenames.size()>j ; j++ ) { // deleting from the btree
   			BTree bt = BTree.deserialiseBTree(t.bplustreenames.get(j)+".ser");
   			
   			String colname= getcolumnname(t.bplustreenames.get(j));
   			int col= getcolumnindex(colname, t); 
   			if (p.tuplesArray.size()==1) {
   				bt.decrementIfValueGreaterThanX(r.page);
   			}
   			if ((p.tuplesArray.get(r.record)).record[col].getClass().getName().equals("java.lang.String")) {
   				String keydelete= ((p.tuplesArray.get(r.record)).record[col]).toString();
   				Reference reference= new Reference(r.page,r.record);
   	   			bt.delete(keydelete, reference);
   			}
   			else if ((p.tuplesArray.get(r.record)).record[col].getClass().getName().equals("java.lang.Double")) {
   				double keydelete=(Double) ((p.tuplesArray.get(r.record)).record[col]);
   				Reference reference= new Reference(r.page,r.record);
   	   			bt.delete(keydelete, reference);
   			}
   			else {
   				
   				int keydelete= (int)((p.tuplesArray.get(r.record)).record[col]);
   				Reference reference= new Reference(r.page,r.record);
   	   			bt.delete(keydelete, reference);
   			}
   			
   			for(int k=r.record+1;k<p.tuplesArray.size();k++){ // shifting el reference bta3et ba2y el records 
   			//	System.out.print("ana d5alt a shift");
   				if (((p.tuplesArray.get(k)).record[col]).getClass().getName().equals("java.lang.String")) {
   	   				String keydelete= ((p.tuplesArray.get(k)).record[col]).toString();
   	   				Reference reference= new Reference(r.page,k);
   	   	   			bt.delete(keydelete, reference);
   	   	   	Reference ref= new Reference(r.page,k-1);//j msh j plus 1
				LinkList val= new LinkList();
				val.insertLast(ref);
				bt.insert(keydelete,val);
   	   			}
   	   			else if (((p.tuplesArray.get(k)).record[col]).getClass().getName().equals("java.lang.Double")) {
   	   				double keydelete=(Double) ((p.tuplesArray.get(k)).record[col]);
   	   				Reference reference= new Reference(r.page,k);
   	   	   			bt.delete(keydelete, reference);
   	   	   	Reference ref= new Reference(r.page,k-1);//j msh j plus 1
				LinkList val= new LinkList();
				val.insertLast(ref);
				bt.insert(keydelete,val);
   	   			}
   	   			else {
   	   				
   	   				int keydelete= (int)((p.tuplesArray.get(k)).record[col]);
   	   				Reference reference= new Reference(r.page,k);
   	   	   			bt.delete(keydelete, reference);
   	   	   	Reference ref= new Reference(r.page,k-1);//j msh j plus 1
				LinkList val= new LinkList();
				val.insertLast(ref);
				bt.insert(keydelete,val);
   	   			}
   					

   				}
   			
   			
   			
   			bt.serializeBtree(t.bplustreenames.get(j)+".ser");
   			
   			} // mafrood a7oto hena
   		
   	
   	
   		p.tuplesArray.remove(r.record);
   	//	System.out.println(p.tuplesArray.size());
   		
   	 
   		
   	    if (p.tuplesArray.isEmpty()) {
   	    //	System.out.println("ana el page fadya");
   	    	p.serializePage(t.pageNames.get(r.page) + ".ser");
   	    	p= null;
   	    	int i = t.pageNames.indexOf(t.pageNames.get(r.page));
   	    //	System.out.println(t.pageNames.get(r.page).equals("finaltest490"));
   	    	t.MMpages.remove(i);
   	     File file = new File(t.pageNames.get(r.page)+".ser") ;
  	   file.delete();   	     
  	   
   	 //     System.out.println(file.exists());
   	    	t.pageNames.remove(t.pageNames.get(r.page));
   	    	
   	        }
   	    else {
   	   // 	System.out.println("ana el page msh fadya");
   	    	updateminmax(t, p, r.page);
   	    	p.serializePage(t.pageNames.get(r.page) + ".ser");
   	    }
   	 farah = entryList.get(0).getKey() ;
    	 ob  = entryList.get(0).getValue();
    	 res = search(ob ,t , farah ,"src\\metadata.csv");
    	

    	for (int i = 1; i < entryList.size(); i ++) {
    	   
    		String st = entryList.get(i).getKey() ;
        	Object obj  = entryList.get(i).getValue();
        	LinkList res2 = search(obj ,t , st ,"src\\metadata.csv");
        res = operation (res, res2 , "AND");
        	
        	
    }
   	    
   	}
   	 t.serializeTable(strTableName+".ser");
	}
   	
  }
	
	
	
	
   
	
	public static LinkList search(Object key, Table t, String colname, String filepath ) {
    	int i = 0;
    	while (!(colname.equals( t.arrangementcol[i]))) {
    	//	System.out.println(t.arrangementcol[i]);
    		i++;
    	}
    	int pos = i;
    	LinkList toot = new LinkList();
    //	System.out.println("toot:" + toot);
    	try {
    	BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = "";
		boolean match = false;
		while ((line = br.readLine()) != null) {
			String[] arrline = line.split(",");
			if ((t.name.equals(arrline[0])) && colname.equals(arrline[1]) && (!arrline[4].equals("null"))) {
			//	System.out.println("ana fl btree");
				String temp = arrline[4]; 
				BTree bt = BTree.deserialiseBTree(temp+".ser");
				match=true;
				if(key.getClass().getName().equals("java.lang.Integer")) {
					int keyy= (int)key;
				return (LinkList)bt.search(keyy);
				}
				else if (key.getClass().getName().equals("java.lang.Double")) {
					double keyy= (Double)key;
					return (LinkList)bt.search(keyy);
				}
				else {
					return (LinkList)bt.search(key.toString());	
				}
				
			}
		}
    	    if(!match) {
    		//LinkList toot = new LinkList();
    	//	System.out.println("ana fl linear search");
    		for (int j = 0; j < t.pageNames.size(); j++) {
    			Page p = Page.deserialisePage(t.pageNames.get(j)+".ser");
    			for (int k = 0; k < p.tuplesArray.size(); k++) {
    				if (p.tuplesArray.get(k).record[pos].equals(key)) { //make sure equals works with doubles
    					Reference r = new Reference(j,k);
    				    toot.insertLast(r);
    				}
    			}
    			p.serializePage(t.pageNames.get(j)+".ser");
    		
    	 
    	}
    	    }
		
    	}
			catch (IOException e) {
				e.printStackTrace();
			}
    //	System.out.println("toot:" + toot);

    		return toot;
    	
     
    }
	
	
	public void updateTable(String strTableName,String strClusteringKeyValue, Hashtable<String,Object> htblColNameValue )
			throws DBAppException{
		Set<Entry<String, Object>> entry = htblColNameValue.entrySet();
		String key = "";
        Object value=null;
		for (Entry<String, Object> entryx : entry) {
             key = entryx.getKey();
             value = entryx.getValue();
		}
		
		if(!checktable("src\\metadata.csv",strTableName)){
			throw new DBAppException("The table does not exist");
		}
		else if(!checkexistence(htblColNameValue,strTableName)){
			throw new DBAppException("A column does not exist");

		}
		else 	if(!(checkforcorrectdatatypes("src\\metadata.csv", strTableName, htblColNameValue))){
			throw new DBAppException("Invalid columns or types");
		}
		else{
			
			String prim = getprimkey("src\\metadata.csv",strTableName);
		//	System.out.println(prim);
			Table t= Table.deserialiseTable(strTableName+".ser");
		String s=	getcolumntype("src\\metadata.csv" ,t.name,  prim);
		Object obj = null;
			if(s.equals("java.lang.Integer")) {
				obj = new Integer(strClusteringKeyValue);
			}
			else if (s.equals("java.lang.Double")) {
				double d =Double.parseDouble(strClusteringKeyValue);
				 obj = d;
			}
			else {
			//	System.out.println("ana string");
				 obj = new String(strClusteringKeyValue);
			}
			 LinkList ll= search(obj , t, prim, "src\\metadata.csv" );
			 if (ll == null || ll.size()== 0) {
				 throw new DBAppException("This primary key dose not exist");
				 
			 }

		//	System.out.println(ll.size());
			Reference r = (Reference) ll.getFirst();
			int page= r.page;
			int record= r.record;
		//	System.out.println(r.page+" di  "+r.record);
			Page p= Page.deserialisePage(t.pageNames.get(r.page)+".ser");
			for (Entry<String, Object> entryx : entry) {
			//	System.out.println("ana gowa el loop");
	             key = entryx.getKey();
	             value = entryx.getValue();
			
			int index= getIndex( strTableName, key);
			boolean hasindexonit=false;
			String indxname="";
			for(int i=0;i<t.bplustreenames.size();i++){
				String colname= getcolumnname(t.bplustreenames.get(i));
				if(colname.equals(key)){
					hasindexonit=true;
					indxname= t.bplustreenames.get(i);
					break;
				}
			}
			if(hasindexonit){
			//	System.out.println("mafish index");
				BTree bt= BTree.deserialiseBTree(indxname+".ser");
				if(p.tuplesArray.get(record).record[index].getClass().getName().equals("java.lang.Integer")){
					int keyy= (int) p.tuplesArray.get(record).record[index];
//					System.out.println("key:" + keyy);
//					System.out.println("ref:" + r.page + r.record);

					bt.delete(keyy, r);
					int newkey= (int)value;
					LinkList ll1= new LinkList();
					ll1.insertLast(r);
					bt.insert(newkey,ll1);
				}
				else if(p.tuplesArray.get(record).record[index].getClass().getName().equals("java.lang.Double")){
					double keyy= (Double) p.tuplesArray.get(record).record[index];
					bt.delete(keyy, r);
					double newkey= (Double)value;
					LinkList ll1= new LinkList();
					ll1.insertLast(r);
					bt.insert(newkey,ll1);
				}
				else if(p.tuplesArray.get(record).record[index].getClass().getName().equals("java.lang.String")){
					String keyy= p.tuplesArray.get(record).record[index].toString();
					bt.delete(keyy, r);
					String newkey= value.toString();
					LinkList ll1= new LinkList();
					ll1.insertLast(r);
					bt.insert(newkey,ll1);
				}
				bt.serializeBtree(indxname+".ser");
			}
		//	System.out.println("ana ha updwqate ");
			p.tuplesArray.get(record).record[index]=value;
		//	System.out.println(p.tuplesArray.get(record).record[index].toString());
			}
			p.serializePage(t.pageNames.get(r.page)+".ser");

			t.serializeTable(strTableName+".ser");
		
		}
		
	}
	
	public static String getprimkey(String filepath, String Tablename){
		try{
    		BufferedReader rdr= new BufferedReader(new FileReader(filepath));
    		String csvline="";
    		String primarykey="";
    		while((csvline=rdr.readLine())!= null){
    			String [] prim= csvline.split(",");
    			if(prim[0].equals(Tablename) && prim[3].equals("True")){
    				
    				primarykey=prim[1];
    				break;
    			}
    		}
    		return primarykey;
    		
    		
    	}
    	catch(IOException e){
    		e.printStackTrace();
    		return null;
    	}
	}
	
	
	public static  int pageposition(Object s1, Table tmp) throws DBAppException{
		int posp=-1;
		int pos=-1;
		int low =0;
		int high= tmp.MMpages.size()-1; // lw el table fadya
		if(s1.getClass().getName().equals("java.lang.Integer")){
			 while(low<=high){
					int mid = (low+high)/2;
				//	System.out.println("bashoof" + posp);
					int max= (int)(tmp.MMpages.get(mid).Max);
					int min = (int)(tmp.MMpages.get(mid).Min);
					int s= (int) s1;
					 if (max> s && s> min){
						posp=mid;
					//	System.out.println("3:" + posp);
						break;
						

					}
					 else if(max< s ){
						low=mid+1;
						posp= mid; 
					//	System.out.println(posp);
					}
					
					else if (min> s ){
						high= mid-1;
						posp= high;
					//	System.out.println("2:" + posp);

					}
					else{
						throw new DBAppException("This primary key already exist. It needs to be unique ");
					}
					
				}
		}
		else if (s1.getClass().getName().equals("java.lang.Double")){
			 while(low<=high){
					int mid = (low+high)/2;
				//	System.out.println("bashoof" + posp);
					double max= (Double)(tmp.MMpages.get(mid).Max);
					double min = (Double)(tmp.MMpages.get(mid).Min);
					double s= (Double) s1;
					 if (max> s && s> min){
						posp=mid;
					//	System.out.println("3:" + posp);
						break;
						

					}
					 else if(max< s ){
						low=mid+1;
						posp= mid; 
					//	System.out.println(posp);
					}
					else if (min> s ){
						high= mid-1;
						posp= high;
					//	System.out.println("2:" + posp);

					}
					else{
						throw new DBAppException("This primary key already exist. It needs to be unique ");
					}
					
				}
		}
		else{
			 while(low<=high){
					int mid = (low+high)/2;
				//	System.out.println("bashoof" + posp);
					String max= (tmp.MMpages.get(mid).Max).toString();
					String min= (tmp.MMpages.get(mid).Min).toString();
					String s=  s1.toString();
					if (max.compareTo(s)>0 && s.compareTo(min)>0){
						posp=mid;
					//	System.out.println("3:" + posp);
						break;
						

					}
					else if(max.compareTo(s)<0 ){
						low=mid+1;
						posp= mid; 
					//	System.out.println(posp);
					}
					else if (min.compareTo(s) >0){
						high= mid-1;
						posp= high;
					//	System.out.println("2:" + posp);

					}
					else{
						throw new DBAppException("This primary key already exist. It needs to be unique ");
					}
					
				}
		}
		
		 if(posp==-1) posp=0;
		 
		 return posp;
	}
	
	public static LinkList searchselect(Object key, Table t, String colname, String filepath, String operator) {
    	int i = 0;
    	while (!colname.equals( t.arrangementcol[i])) {
    		i++;
    	}
    	int pos = i;
    	LinkList toot = new LinkList();
    	try {
    	BufferedReader br = new BufferedReader(new FileReader(filepath));
		String line = "";
		boolean match = false;
		while ((line = br.readLine()) != null) {
			String[] arrline = line.split(",");
			if ((t.name.equals(arrline[0])) && colname.equals(arrline[1]) && (!arrline[4].equals("null"))) {
				//String operator = null;
				String temp = arrline[4]; 
				BTree bt = BTree.deserialiseBTree(temp + ".ser");
				match = true;
				switch (operator) {
				case "=":
					if(key.getClass().getName().equals("java.lang.Integer")) {
						int keyy= (int)key;
						toot = (LinkList)bt.search(keyy);
						
					}
					else if(key.getClass().getName().equals("java.lang.Double")) {
						Double keyy= (double)key;
						toot = (LinkList)bt.search(keyy);
						
					}
					else {
						String keyy= (String)key;
						toot = (LinkList)bt.search(keyy);
					}
					break;
				case "!=":
				//	System.out.println("dakhalt");
					toot = bt.traverseAndCheckKeyEqualsToOne(key, "!=");
					break;
				case ">":
					toot = bt.traverseAndCheckKeyEqualsToOne(key, ">");
					break;
				case ">=":
					toot = bt.traverseAndCheckKeyEqualsToOne(key, ">=");
					break;
				case "<":
					toot = bt.traverseAndCheckKeyEqualsToOne(key, "<");
					break;
				case "<=":
					toot = bt.traverseAndCheckKeyEqualsToOne(key, "<=");
					break;
				default: 
				}
				bt.serializeBtree(temp + ".ser");
//				toot = (LinkList)bt.search(key.toString());
//				bt.serializeBtree(temp + ".ser");
				
				
			}

		}
    	if (!match) {
    		//LinkList toot = new LinkList();
    		for (int j = 0; j < t.pageNames.size(); j++) {
			//	System.out.println(j);

    		//	System.out.println("ana fl page loop");
    			Page p = Page.deserialisePage(t.pageNames.get(j) + ".ser");
    			for (int k = 0; k < p.tuplesArray.size(); k++) {
    			//	System.out.println("ana fl record loop");
    			//	System.out.println(k);

    				if (p.tuplesArray.get(k).record[pos].getClass().getName().equals("java.lang.Integer")) {
    					Integer val = (Integer) p.tuplesArray.get(k).record[pos];
    					
    				if (operator.equals("=") && val.equals(key)) { //make sure equals works with doubles
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    				else if (operator.equals("!=") && !val.equals(key)){
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    				else if (operator.equals(">") && (val.compareTo((Integer)key) > 0)) {
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    				else if (operator.equals(">=") && ((val.compareTo((Integer)key) > 0) || val.equals(key))){
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    				else if (operator.equals("<") && (val.compareTo((Integer)key) < 0)) {
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    				else if (operator.equals("<=") && ((val.compareTo((Integer)key) < 0) || val.equals(key))) {
    					Reference r = new Reference(j,k);
    					toot.insertLast(r);
    				}
    			}
    				else if (p.tuplesArray.get(k).record[pos].getClass().getName().equals("java.lang.String")) {
    					String val = (String) p.tuplesArray.get(k).record[pos];
    					if (operator.equals("=") && val.equals(key)) { //make sure equals works with doubles
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("!=") && !val.equals(key)){
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals(">") && (val.compareTo((String)key) > 0)) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals(">=") && ((val.compareTo((String)key) > 0) || val.equals(key))){
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("<") && (val.compareTo((String)key) < 0)) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("<=") && ((val.compareTo((String)key) < 0) || val.equals(key))) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}

    				}
    				else if (p.tuplesArray.get(k).record[pos].getClass().getName().equals("java.lang.Double")) {
    					Double val = (Double) p.tuplesArray.get(k).record[pos];
    				//	System.out.println(val);
    					if (operator.equals("=") && val.equals(key)) { //make sure equals works with doubles
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("!=") && !val.equals(key)){
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals(">") && (val.compareTo((Double)key) > 0)) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals(">=") && ((val.compareTo((Double)key) > 0) || val.equals(key))){
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("<") && (val.compareTo((Double)key) < 0)) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}
        				else if (operator.equals("<=") && ((val.compareTo((Double)key) < 0) || val.equals(key))) {
        					Reference r = new Reference(j,k);
        					toot.insertLast(r);
        				}


    				}
    			p.serializePage(t.pageNames.get(j) + ".ser");
    		}
    	 
    	} 
    	}
    	}
    	
			catch (IOException e) {
				e.printStackTrace();
			}
    		return toot;
    	
     
    }
	
	public static LinkList  operation(LinkList l1, LinkList l2, String operator) {
    	LinkList result = new LinkList();
//    	if (l1 == null || l2 == null) {
//    		System.out.print("either result set is empty or nothing is deleted");
//    		return null;
//    	}
    	if (operator.equals("AND")) {
    	result = LinkList.common(l1, l2);	
    	}
    	else if (operator.equals("OR")) {
    		while (!(l1.isEmpty())) {
    			result.insertLast(l1.removeFirst());
    		}
    		while (!(l2.isEmpty())) {
    			result.insertLast(l2.removeFirst());
    		}
    		result.removedupes();
    	}
    	else if (operator.equals("XOR")) {
    		result = LinkList.notCommon(l1, l2);
    		
    	}
    	return result; 
    }
	
	 public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators)
	    		throws DBAppException{
	    		Vector<Record> resultset = new Vector<Record>(); //mafrood yeb2a 3andy hena el records el condition bey apply to
	    		LinkList finalll = new LinkList();
	    		//check table and columns existence and compatibility
	    		String filepath = "src\\metadata.csv";
	    		if (!(checktable(filepath, arrSQLTerms[0]._strTableName))) {
	    			throw new DBAppException("Wrong table name");
	    		}
	    		else {
	    			for (int i = 0; i < arrSQLTerms.length; i++) {
	    				String currentcolumn = arrSQLTerms[i]._strColumnName;
	    				if (!(checkcolumn(filepath, arrSQLTerms[0]._strTableName, currentcolumn))) {
	    					throw new DBAppException("Wrong query conditions");
	    				}
	  
	    			}

	    		}
	    		for (int i = 0; i < arrSQLTerms.length - 1; i++) {
	    			String currtable = arrSQLTerms[i]._strTableName;
	    			String nexttable = arrSQLTerms[i + 1]._strTableName;
	    			if (!(currtable.equals(nexttable))){
	    				throw new DBAppException("you cannot select from more than one table");
	    			}
	    		}
	    		Table t = Table.deserialiseTable(arrSQLTerms[0]._strTableName + ".ser");
	    		if (arrSQLTerms.length == 1 && strarrOperators.length == 0) {
	    		    SQLTerm curr = arrSQLTerms[0];
	    			finalll = searchselect(curr._objValue, t, curr._strColumnName, filepath, curr._strOperator);	
	    			}
	    		else if (arrSQLTerms.length == 2 && strarrOperators.length == 1) {
	    			SQLTerm curr1 = arrSQLTerms[0];
	    			SQLTerm curr2 = arrSQLTerms[1];
	    			LinkList l1 = searchselect(curr1._objValue, t, curr1._strColumnName, filepath, curr1._strOperator);
	    			LinkList l2 = searchselect(curr2._objValue, t, curr2._strColumnName, filepath, curr2._strOperator);
	    		    finalll = operation(l1, l2, strarrOperators[0]);
	    		}
	    		else if (arrSQLTerms.length > 2 && strarrOperators.length == arrSQLTerms.length - 1) {
	    			SQLTerm curr1 = arrSQLTerms[0];
	    			SQLTerm curr2 = arrSQLTerms[1];
	    			LinkList l1 = searchselect(curr1._objValue, t, curr1._strColumnName, filepath, curr1._strOperator);
	    			LinkList l2 = searchselect(curr2._objValue, t, curr2._strColumnName, filepath, curr1._strOperator);
	    			LinkList temp = operation(l1, l2, strarrOperators[0]);
	    			for (int i = 2; i < arrSQLTerms.length; i++) {
	    				int j = 1;
	    				SQLTerm curri = arrSQLTerms[i];
	    				LinkList currl = searchselect(curri._objValue, t, curri._strColumnName, filepath, curri._strOperator);
	    				temp = operation(temp, currl, strarrOperators[j]);
	    				j++;
	    			}
	    			finalll = temp;
	    		}
	    		else {
	    			throw new DBAppException("Wrong query conditions");
	    		}
	    		t.serializeTable(arrSQLTerms[0]._strTableName + ".ser");
	    		while (!(finalll.isEmpty())) {
	    			Reference r = (Reference)finalll.removeFirst();
	    			Page p = Page.deserialisePage(arrSQLTerms[0]._strTableName + r.page + ".ser");
	    			resultset.add(p.tuplesArray.get(r.record));	
	    			p.serializePage(arrSQLTerms[0]._strTableName + r.page + ".ser");
	    		}
	    		
	    		Iterator<Record> itr = resultset.iterator();
	    		return itr;   			
	    
	    }

	
	public static int recordposition(Page p, Object s1) throws DBAppException{
		int pos=-1;
		int low =0;
		int  high= p.tuplesArray.size()-1;
		if (s1.getClass().getName().equals("java.lang.String")){
				    while (low <= high) {
				    //	System.out.println(low+" "+ high);
				        int mid = (low + high) / 2;
				       
                        String s= s1.toString();
                        
				        if ((p.tuplesArray.get(mid).record[0]).toString().compareTo(s)<0) {
				       // 	System.out.println("ana hena");
				            low = mid + 1;
				            pos = low;
				        } else if ((p.tuplesArray.get(mid).record[0]).toString().compareTo(s)>0) {
				        //	System.out.println("ana fl else if hena");
				        	high = mid - 1;
				            pos = mid;
				        } else {
				        	throw new DBAppException("This primary key already exist");
				          /*  pos = mid + 1;
				            break;*/
				        }
				    }
		}
		else if (s1.getClass().getName().equals("java.lang.Integer")){
			   while (low <= high) {
				    //	System.out.println(low+" "+ high);
				        int mid = (low + high) / 2;
				      
                       int s= (int)s1;
                       int record= (int)(p.tuplesArray.get(mid).record[0]);
                 //      System.out.println(record +" " + s);
				  //      System.out.println(record<s);
				        if (record<s) {
				        	System.out.println("ana hena");
				            low = mid + 1;
				            pos = low;
				        } else if (record >s) {
				      //  	System.out.println("ana fl else if hena");
				        	high = mid - 1;
				            pos = mid;
				        } else {
				        	throw new DBAppException("This primary key already exist");
				          /*  pos = mid + 1;
				            break;*/
				        }
				    }
		}
		else{
			   while (low <= high) {
				    //	System.out.println(low+" "+ high);
				        int mid = (low + high) / 2;
				        
                      double s= (Double)s1;
                      double record= (Double)(p.tuplesArray.get(mid).record[0]);
                 //     System.out.println(record +" " + s);
				 //       System.out.println(record<s);
				        if (record<s) {
				 //       	System.out.println("ana hena");
				            low = mid + 1;
				            pos = low;
				        } else if (record >s) {
				   //     	System.out.println("ana fl else if hena");
				        	high = mid - 1;
				            pos = mid;
				        } else {
				        	throw new DBAppException("This primary key already exist");
				          /*  pos = mid + 1;
				            break;*/
				        }
				    }
		}
		return pos;
	}
	
	//public static boolean existinpage()

	
	
	
	
	
	
	
	
	
	
	
}

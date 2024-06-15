import java.io.Serializable;


public class Reference implements Serializable, Comparable<Reference>{
	
	int page;
	int record;
	private static final long serialVersionUID = -923785761771364708L;
	//
//public static void farah() {
//		
//	}
	
	public Reference(int k, int i){
		
	page=k;
	record=i;
	}
public String toString(){
	return this.page+","+this.record;
}
public int compareTo(Reference tanyref) { //yarab teshta8al
    int pageComparison = Integer.compare(this.page, tanyref.page);
    if (pageComparison != 0) {
        return pageComparison;
    }
    return Integer.compare(this.record, tanyref.record);
}


}

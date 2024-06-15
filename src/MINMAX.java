import java.io.Serializable;


public class MINMAX implements Serializable{
	Object Min;
	Object Max;
	private static final long serialVersionUID = 3443673431315027040L;
	//
//public static void farah() {
//		
//	}
	public MINMAX(Object m, Object mx){
		Min=m;
		Max=mx;
	}
	
	public void UpdateMIN(Object m){
		Min=m;
	}
	
     public void UpdateMAX(Object mx){
	Max=mx;
    }
}

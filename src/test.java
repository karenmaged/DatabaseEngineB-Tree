import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class test {
   public static void main(String args[]){
	  
			String csvFile = "C:\\Users\\user\\git\\Database2\\src\\metadata.csv";
			 
	       
	        try {
	            BufferedReader br = new BufferedReader(new FileReader(csvFile));
	            String line;
	            StringBuilder sb = new StringBuilder();

	            while ((line = br.readLine()) != null) {
	                String[] data = line.split(","); 
	                if (data.length > 4 && data[4].equals("null")&& data[0].equals("TestTable")&& data[1].equals("name")) {
	                    data[4] = "farah";
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
   }


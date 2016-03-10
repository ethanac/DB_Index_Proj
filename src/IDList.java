import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class IDList {
	// Create an integer array which size is 10000. 
	//For each age string takes 2 bytes, the string array length will be 20000 bytes at most.
	private final int MAX_SIZE = 10000;
	private ArrayList<String> ids = new ArrayList<String>();
	private int counter = 0;
	private int index;
	private Path ixfile;
//	private String delimiter = System.getProperty("line.separator");
	
	public IDList(int index){	
		this.index = index;
		ixfile = Paths.get("ix"+index);
	}
	
	public void add(int id) throws IOException{
		ids.add(""+ id);
		counter++;
		if(counter == MAX_SIZE){
			saveToFile();
			counter = 0;
		}
	}
	
	public void saveToFile() throws IOException{
		//StringBuilder rec = new StringBuilder();
		String rec = "";
		for(int i=0; i < ids.size(); i++){
			if(new File("ix"+index).length() == 0 && i == 0)
				rec += ids.get(i);				
			else
				rec += "\n" + ids.get(i);
		}
		Files.write(ixfile, rec.getBytes(), StandardOpenOption.APPEND);  
		ids.clear();
	}
}

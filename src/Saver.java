import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;

public class Saver extends Thread{
	// Create an integer array which size is 10000. 
	//For each age string takes 2 bytes, the string array length will be 20000 bytes at most.
//	private final int MAX_SIZE = 3800;
	public ArrayList<Integer> ids = new ArrayList<Integer>();
//	private int counter = 0;
	private int index;
	private Path ixfile;
	public boolean full = false;
	public boolean running = true;
//	private String delimiter = System.getProperty("line.separator");
	
	public Saver(int index){	
		this.index = index;
		ixfile = Paths.get("ix"+index);
	}
	
	public void run(){
		try {
			while(true)
				saveToFile();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized void readyToSave() throws IOException{
		full = true;
		notify();
	}
	
	public synchronized void saveToFile() throws IOException, InterruptedException{
		//StringBuilder rec = new StringBuilder();
		while(!full){
			wait();
		}
		String rec = "";
		for(int i=0; i < ids.size(); i++){
			if(new File("ix"+index).length() == 0 && i == 0)
				rec += ids.get(i);				
			else
				rec += "\n" + ids.get(i);
		}
		Files.write(ixfile, rec.getBytes(), StandardOpenOption.APPEND);  
		ids.clear();
		full = false;
	}
}

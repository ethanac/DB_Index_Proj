
import java.util.ArrayList;

public class SaverList {
//	// Create an integer array which size is 10000. 
//	//For each age string takes 2 bytes, the string array length will be 20000 bytes at most.
//	private final int MAX_SIZE = 3800;
	public ArrayList<Saver> saverList = new ArrayList<Saver>();
//	private int counter = 0;
//	private int index;
//	private Path ixfile;
//	
//	
	public SaverList(){	
		for(int i=0; i < 2; i++){
			saverList.add(new Saver(i));
			saverList.get(i).start();
		}
	}
//	
//	public void creatThreads() throws IOException{
//		ids.add(id);
//		counter++;
//		if(counter == MAX_SIZE){
//			saveToFile();
//			counter = 0;
//		}
//	}
//	
//	public void saveToFile() throws IOException{
//		//StringBuilder rec = new StringBuilder();
//		String rec = "";
//		for(int i=0; i < ids.size(); i++){
//			if(new File("ix"+index).length() == 0 && i == 0)
//				rec += ids.get(i);				
//			else
//				rec += "\n" + ids.get(i);
//		}
//		Files.write(ixfile, rec.getBytes(), StandardOpenOption.APPEND);  
//		ids.clear();
//	}
}

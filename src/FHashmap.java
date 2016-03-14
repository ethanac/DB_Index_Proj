import java.util.ArrayList;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.io.IOException;
import java.io.File;
	
public class FHashmap {

	private final static int MIN_AGE = 18;
	private final static int MAX_AGE = 99;
	private final static int TOTAL_BUCKETS = MAX_AGE-MIN_AGE+1; //total buckets
	private final static int MAX_SIZE = 3500;
	private ArrayList<Path> indexFiles = new ArrayList<Path>();
	private SaverList sl = new SaverList();
	public long incomeSum, number = 0;
	
	/*
	 * Constructor
	 */
	public FHashmap() throws IOException {
		// Initialize the indexFiles array list with creating the index files.
		boolean create = true;
		if(exists())
			create = false;
		for(int i=0; i< TOTAL_BUCKETS; i++){
			Path ixfile = Paths.get("ix"+i);
			indexFiles.add(ixfile);
			if(create)	
				Files.write(ixfile, "".getBytes(), StandardOpenOption.CREATE);
			//idList.add(new IDList(i));
		}
	}
	
	/**
	 * Get the ID of each record an put them into separate files according to the age of the record.
	 * @param record  One record with ID and age.
	 * @throws IOException  IO exception.
	 * @throws InterruptedException 
	 */
	public void insertToIndex(Record record) throws IOException, InterruptedException {
		
		int index = record.age-MIN_AGE;
		int id = record.id;
		if(sl.saverList.get(index).ids.size() == MAX_SIZE){
			sl.saverList.get(index).readyToSave();
		}
		while(sl.saverList.get(index).full);
		sl.saverList.get(index).ids.add(id);
		incomeSum += record.income;
		number++;
	}
	
	/**
	 * Flush all records which are still in the memory to disk.
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void flush() throws IOException, InterruptedException{
		for(Saver l : sl.saverList){
			l.readyToSave();
			l.saveToFile();
			l.running = false;
		}
	}
	
	/**
	 * Check if the all index files exist.
	 * @return  Even one index file does not exist, return false.
	 */
	public boolean exists(){
		boolean exist = false;
		File ix = new File("indexFile");
		File os = new File("offsetsFile");
		if(ix.exists() && os.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * Get the size of all index files.
	 * @return
	 */
	public int getSize(){
		int size = 0;
		File indexfile = new File("indexFile");
		size += (int) indexfile.length();

		return size;
	}

}

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
	private ArrayList<Path> indexFiles = new ArrayList<Path>();
	private ArrayList<IDList> idList = new ArrayList<IDList>();
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
			idList.add(new IDList(i));
		}
	}
	
	/**
	 * Get the ID of each record an put them into separate files according to the age of the record.
	 * @param record  One record with ID and age.
	 * @throws IOException  IO exception.
	 */
	public void insertToIndex(Record record) throws IOException {

		int index = record.age-MIN_AGE;
		int id = record.id.intValue();
		idList.get(index).add(id);
		incomeSum += record.income;
		number++;
	}
	
	/**
	 * Flush all records which are still in the memory to disk.
	 * @throws IOException
	 */
	public void flush() throws IOException{
		for(IDList l : idList){
			l.saveToFile();
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
//		for(int i=0; i <= MAX_AGE-MIN_AGE; i++){
			File indexfile = new File("indexFile");
			size += (int) indexfile.length();
//		}
		return size;
	}

}

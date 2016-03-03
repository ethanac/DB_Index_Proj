import java.util.ArrayList;
import java.lang.Math;
import java.util.HashMap;

import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import java.math.BigInteger;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.PrintStream;

public class IndexStructure {

	private final static int AGE_OFFSET = 39; // age is 39 bytes offset in record
	private final static int AGE_BYTE_SIZE = 2;
	private final static int MIN_AGE = 18;
	private final static int MAX_AGE = 99;
	private final static int BUCKET_SIZE = 3; //bucket age size	
	private final static int TOTAL_BUCKETS = (MAX_AGE - MIN_AGE) / BUCKET_SIZE + 1; //total buckets
	private static File path = new File("");
	private final static String INDEX_FILE = path.getAbsolutePath() + "/src/index";
	private final static String VALUE_SIZE = path.getAbsolutePath() + "/src/value_size";
	private final static String OFFSETS = path.getAbsolutePath() + "/src/offsets";
	private final static String IN_FILE = path.getAbsolutePath() + "/src/mymap";
	private FileHashMap < Integer, ArrayList < BigInteger >> indexStructure;
	private ArrayList<String> mapArray = new ArrayList<String>();
	/**
	 * This array list contains the offset of each bucket.
	 * int[0] means the start position of the middle number, int[1] means the ending position.
	 */
	public ArrayList<int[]> offsetArray = new ArrayList<int[]>();
	private ArrayList<Integer> keyArray = new ArrayList<Integer>(TOTAL_BUCKETS); 
	
	public IndexStructure(int option) throws IOException, ObjectExistsException, ClassNotFoundException, VersionMismatchException {
		indexStructure = new FileHashMap < Integer, ArrayList < BigInteger >> (IN_FILE,option);
		for(int i=0; i<TOTAL_BUCKETS; i++){
			int[] init = {0,0};
			offsetArray.add(init);
			keyArray.add(0);
			mapArray.add(" ");
		}
	}

	public int extractAge(Record record) {
		String rec = record.toString().substring(AGE_OFFSET, AGE_OFFSET + AGE_BYTE_SIZE);
		return Integer.parseInt(rec);
	}
	
	public FileHashMap <Integer, ArrayList<BigInteger>> getMap(){
		return indexStructure;
	}
	public void insertToIndex(Record record) {
		ArrayList < BigInteger > recordsList;
//		int age = extractAge(record);
		int index = ageToKey(record.age);

		// insert into index key
		// According to different offset, insert an age into different positions.
		if(!indexStructure.containsKey(index)){
			recordsList = new ArrayList < BigInteger > ();
		}
		else{
			recordsList = indexStructure.get(index);
		}
		if((record.age-MIN_AGE) % BUCKET_SIZE == 1){
			recordsList.add(offsetArray.get(index)[0], record.id);
			offsetArray.get(index)[1]++;
		}
		else if((record.age-MIN_AGE) % BUCKET_SIZE == 0){
			recordsList.add(0,record.id);
			offsetArray.get(index)[0]++;
			offsetArray.get(index)[1]++;
		}
		else{
			recordsList.add(record.id); 
		}			
		
		indexStructure.put(index, recordsList);
	}

	public int ageToKey(int age) {
		return (int) ((Math.floor(age - MIN_AGE) / BUCKET_SIZE) % MAX_AGE);
	}

	public String getIndexStructure() throws FileNotFoundException {
		String result = "";
		for (HashMap.Entry<Integer, ArrayList < BigInteger >> struct : indexStructure.entrySet()) {
//		  	result += struct.getKey() +":"+struct.getValue() + "\n";
			mapArray.set(struct.getKey(), ""+struct.getValue());
			keyArray.set(struct.getKey(), struct.getValue().size());

		}
		try (PrintStream out = new PrintStream(new FileOutputStream(VALUE_SIZE))) {
			for(int i=0; i<keyArray.size(); i++){
				if(i<keyArray.size()-1)
					out.print(keyArray.get(i)+",");
				else
					out.print(keyArray.get(i));
			}
		}
		for(int i = 0; i<mapArray.size(); i++){	
			result += mapArray.get(i)+"\n";
		}
		// remove all white spaces from index file to reduce size further (10kb saved)
		return result.replace(" ","").replace("[","").replace("]", "");
	}

	// ISSUE: what if we choose an age in the middle of the bucket
	// IDEA: should keep track of where the two offsets are in the list e.g. ages 18-20, keep offset of where 19 starts
	public ArrayList < BigInteger > getRecordsbyAge(int age1, int age2) {
		ArrayList < BigInteger > list = new ArrayList < BigInteger > ();
		ArrayList < BigInteger > list1 = new ArrayList < BigInteger > ();
		if (age1 > age2)
//			return "Age can't be bigger";
			return list;
		list = indexStructure.get(ageToKey(age1));
		list1 = indexStructure.get(ageToKey(age2));
		ArrayList < BigInteger > resultList = new ArrayList < > (list.size() + list1.size());
		resultList.addAll(list);
		resultList.addAll(list1);

		// TODO: returns a list of student ids from offsets of the records with that specific age (given by resultList)
		return resultList;
	}

	public String showList(ArrayList < BigInteger > list) {
		String listString = "";
		for (BigInteger s: list) {
			listString += s.toString() + "\t";
		}
		return listString;
	}
	
//	private String getOffSet(){
//		String offsets = "";
//		for(int i=0; i<offsetArray.size(); i++){
//			if(i == offsetArray.size()-1)
//				offsets += (offsetArray.get(i)[0]+","+offsetArray.get(i)[1]);
//			else
//				offsets += (offsetArray.get(i)[0]+","+offsetArray.get(i)[1]+";");
//		}
//		return offsets.replace(" ", "").replace("[", "").replace("]","");
//	}
	
	public void saveIndexStructure() throws IOException {
//		try (PrintStream out = new PrintStream(new FileOutputStream(INDEX_FILE))) {
//			out.print(this.getIndexStructure());
//		}
//		try (PrintStream out = new PrintStream(new FileOutputStream(OFFSETS))) {
//			out.print(this.getOffSet());
//		}
		this.indexStructure.save();
		File indexf = new File(IN_FILE+".ix");
		if(indexf.exists()){
			System.out.println("Successfully saved");
		}
//		File file = new File(INDEX_FILE);
//        FileOutputStream f = new FileOutputStream(file);
//        ObjectOutputStream s = new ObjectOutputStream(f);
//        s.writeObject(indexStructure);
//        s.close();
	}
	
//	public void loadIndexStructure() throws IOException, ClassNotFoundException {
//			File file = new File(INDEX_FILE);
//			FileInputStream f = new FileInputStream(file);
//		    ObjectInputStream s = new ObjectInputStream(f);
//		    indexStructure = (FileHashMap<Integer, ArrayList<BigInteger>>) s.readObject();
//		    s.close();
//	}
	
	public boolean exists(){
		File indexfile = new File(IN_FILE+".ix");
		File dbfile = new File(IN_FILE+".db");
//		File oFile = new File(OFFSETS);
		return (indexfile.exists() && dbfile.exists());
	}
	
	public int size(){
		File indexfile = new File(IN_FILE+".ix");
		File dbfile = new File(IN_FILE+".db");
		return (int) indexfile.length() + (int) dbfile.length();
	}
}

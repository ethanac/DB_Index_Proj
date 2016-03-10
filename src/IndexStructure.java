//import java.util.ArrayList;
//import java.lang.Math;
//import java.util.HashMap;
//import java.util.Scanner;
//
//import org.clapper.util.misc.FileHashMap;
//import org.clapper.util.misc.ObjectExistsException;
//import org.clapper.util.misc.VersionMismatchException;
//
//import java.math.BigInteger;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.File;
//import java.io.PrintStream;
//
//public class IndexStructure {
//
//	private final static int AGE_OFFSET = 39; // age is 39 bytes offset in record
//	private final static int AGE_BYTE_SIZE = 2;
//	private final static int MIN_AGE = 18;
//	private final static int MAX_AGE = 99;
//	private final static int BUCKET_SIZE = 3; //bucket age size	
//	private final static int TOTAL_BUCKETS = (MAX_AGE - MIN_AGE) / BUCKET_SIZE + 1; //total buckets
//	private static File path = new File("");
//	private final static String OFFSETS = path.getAbsolutePath() + "/src/offsets";
//	private final static String IN_FILE = path.getAbsolutePath() + "/src/mymap";
//	private FileHashMap < Integer, ArrayList < BigInteger >> indexStructure;
//	//private ArrayList<String> mapArray = new ArrayList<String>();
//	/**
//	 * This array list contains the offset of each bucket.
//	 * int[0] means the start position of the middle number, int[1] means the ending position.
//	 */
//	public ArrayList<int[]> offsetArray = new ArrayList<int[]>();
//	//private ArrayList<Integer> keyArray = new ArrayList<Integer>(TOTAL_BUCKETS); 
//	
//	public IndexStructure(int option) throws IOException, ObjectExistsException, ClassNotFoundException, VersionMismatchException {
//		for(int i=0; i<TOTAL_BUCKETS; i++){
//			int[] init = {0,0};
//			offsetArray.add(init);
//		}
//		if(option == FileHashMap.FORCE_OVERWRITE){
//			indexStructure = new FileHashMap < Integer, ArrayList < BigInteger >> (IN_FILE, option);
//		}
//		else{
//			indexStructure = new FileHashMap < Integer, ArrayList < BigInteger >> (IN_FILE, 0);
//			Scanner sc = new Scanner(new File(OFFSETS));
//		
//			for(int i=0; i< TOTAL_BUCKETS; i++){
//				int[] tmp ={0,0};
//				tmp[0] = sc.nextInt();
//				tmp[1] = sc.nextInt();
//				offsetArray.set(i, tmp);
//			}
//			sc.close();
//		}	
//	}
//
//	public int extractAge(Record record) {
//		String rec = record.toString().substring(AGE_OFFSET, AGE_OFFSET + AGE_BYTE_SIZE);
//		return Integer.parseInt(rec);
//	}
//	
//	public FileHashMap <Integer, ArrayList<BigInteger>> getMap(){
//		return indexStructure;
//	}
//	
//	public void insertToIndex(Record record) {
//		ArrayList < BigInteger > recordsList;
////		int age = extractAge(record);
//		int index = ageToKey(record.age);
//
//		// insert into index key
//		// According to different offset, insert an age into different positions.
//		if(!indexStructure.containsKey(index)){
//			recordsList = new ArrayList < BigInteger > ();
//		}
//		else{
//			recordsList = indexStructure.get(index);
//		}
//		if((record.age-MIN_AGE) % BUCKET_SIZE == 1){
//			recordsList.add(offsetArray.get(index)[0], record.id);
//			offsetArray.get(index)[1]++;
//		}
//		else if((record.age-MIN_AGE) % BUCKET_SIZE == 0){
//			recordsList.add(0,record.id);
//			offsetArray.get(index)[0]++;
//			offsetArray.get(index)[1]++;
//		}
//		else{
//			recordsList.add(record.id); 
//		}			
//		
//		indexStructure.put(index, recordsList);
//	}
//
//	public int ageToKey(int age) {
//		return (int) ((Math.floor(age - MIN_AGE) / BUCKET_SIZE) % MAX_AGE);
//	}
//
//	public String getIndexStructure() throws FileNotFoundException {
//		String result = "";
//		for (HashMap.Entry<Integer, ArrayList < BigInteger >> struct : indexStructure.entrySet()) {
//		  	result += struct.getKey() +":"+struct.getValue() + "\n";
//		}
//
//		// remove all white spaces from index file to reduce size further (10kb saved)
//		return result.replace(" ","").replace("[","").replace("]", "");
//	}
//
//	// ISSUE: what if we choose an age in the middle of the bucket
//	// IDEA: should keep track of where the two offsets are in the list e.g. ages 18-20, keep offset of where 19 starts
////	public ArrayList < BigInteger > getRecordsbyAge(int age1, int age2) {
////		ArrayList < BigInteger > list = new ArrayList < BigInteger > ();
////		ArrayList < BigInteger > list1 = new ArrayList < BigInteger > ();
////		if (age1 > age2)
//////			return "Age can't be bigger";
////			return list;
////		list = indexStructure.get(ageToKey(age1));
////		list1 = indexStructure.get(ageToKey(age2));
////		ArrayList < BigInteger > resultList = new ArrayList < > (list.size() + list1.size());
////		resultList.addAll(list);
////		resultList.addAll(list1);
////
////		// TODO: returns a list of student ids from offsets of the records with that specific age (given by resultList)
////		return resultList;
////	}
//
//	public String showList(ArrayList < BigInteger > list) {
//		String listString = "";
//		for (BigInteger s: list) {
//			listString += s.toString() + "\t";
//		}
//		return listString;
//	}
//	
//	public void saveIndexStructure() throws IOException {
////		try (PrintStream out = new PrintStream(new FileOutputStream(INDEX_FILE))) {
////			out.print(this.getIndexStructure());
////		}
//		try (PrintStream out = new PrintStream(new FileOutputStream(OFFSETS))) {
//			for(int i=0; i<offsetArray.size(); i++){
//				out.print(offsetArray.get(i)[0]);
//				out.print("\n");
//				out.print(offsetArray.get(i)[1]);
//				out.print("\n");
//			}
//		}
//		
//		this.indexStructure.save();
//		File indexf = new File(IN_FILE+".ix");
//		File dbf = new File(IN_FILE+".db");
//		File offsets = new File(OFFSETS);
//		if(indexf.exists() && dbf.exists() && offsets.exists()){
//			System.out.println("Successfully saved");
//		}
//
//	}
//	
//	public boolean exists(){
//		File indexfile = new File(IN_FILE+".ix");
//		File dbfile = new File(IN_FILE+".db");
//		File oFile = new File(OFFSETS);
//		return (indexfile.exists() && dbfile.exists() && oFile.exists());
//	}
//	
//	public int size(){
//		File indexfile = new File(IN_FILE+".ix");
//		File dbfile = new File(IN_FILE+".db");
//		File offsets = new File(OFFSETS);
//		return (int) indexfile.length() + (int) dbfile.length() + (int) offsets.length();
//	}
//}

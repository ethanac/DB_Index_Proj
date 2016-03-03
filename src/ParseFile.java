import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;

import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import java.io.File;


/**
 * Parse the text/data File. Get the record from the text/data file.
 * 
 * @author HaoZhang
 */
public class ParseFile {
	
	File path = new File("");
	private final static int MIN_AGE = 18;
	private final static int MAX_AGE = 99;
	private final static int BUCKET_SIZE = 3; //bucket age size	
	private final static int TOTAL_BUCKETS = (MAX_AGE - MIN_AGE) / BUCKET_SIZE + 1; //total buckets
	private String filePath = path.getAbsolutePath()+"/src/person.txt";
	private String VALUE_SIZE = path.getAbsolutePath()+"/src/value_size";
	private String OFFSETS = path.getAbsolutePath() + "/src/offsets";
	private String INDEX = path.getAbsolutePath()+"/src/index";
	private FileReader inputStream = null;
	private int charCounter = 0;
	private ArrayList<Record> records = new ArrayList<Record>();
	private ArrayList<Integer> splitArray = new ArrayList<Integer>(TOTAL_BUCKETS);
	private ArrayList<int[]> offsetArray = new ArrayList<int[]>(TOTAL_BUCKETS);
	
	public ParseFile(){
		
	}
	public ParseFile(String path){
		filePath = path;
	}
	
	public void getSplitValueAndOffsets() throws IOException{
		RandomAccessFile rak = new RandomAccessFile(VALUE_SIZE, "r");
		RandomAccessFile rao = new RandomAccessFile(OFFSETS, "r");
		String idLine = rak.readLine();
		String offset = rao.readLine();
		String[] tmpOffset;
		
		String[] sValue = idLine.split(",", TOTAL_BUCKETS);
		String[] offsets = offset.split(";", TOTAL_BUCKETS);
		for(int i=0; i<TOTAL_BUCKETS; i++){
			splitArray.add(Integer.parseInt(sValue[i]));
			tmpOffset = offsets[i].split(",");
			int[] tmp = {Integer.parseInt(tmpOffset[0]),Integer.parseInt(tmpOffset[1])};
			offsetArray.add(tmp);
		}
		rak.close();
		rao.close();
	}
	
//	private String[] getIDs(int key) throws IOException{
//		 RandomAccessFile rai = new RandomAccessFile(INDEX, "r");
//		 int i=0;
//		 String tmp = "";
//		 String[] IDs = null;
//		 while(i <= key){
//			 tmp = rai.readLine();
//			 i++;
//		 }
//		 rai.close();
//		 if(tmp != null)
//			 IDs = tmp.split(",", splitArray.get(key));
//		 return IDs;
//	}
	/**
	 * Get Records by the given range of age. This method is public.
	 * @param datafile  The file path of the data file.
	 * @param is  Index structure created by demo
	 * @return result  The result we wanted.
	 * @throws IOException Hehe.
	 * @throws VersionMismatchException 
	 * @throws ClassNotFoundException 
	 * @throws ObjectExistsException 
	 */
	public ArrayList<String> getRecordsByAge(String datafile, IndexStructure istructure, int sAge, int eAge) throws IOException, ObjectExistsException, ClassNotFoundException, VersionMismatchException{
		ArrayList<String> result = new ArrayList<String>();
		
		int startingAge;
		int endingAge;
		int startingKey = 0;
		int startingOffSet = 0;
		int endingKey = 0;
		int endingOffSet;
		int startingPosition = 0;
		int endingPosition = 0;
		RandomAccessFile rad = new RandomAccessFile(datafile, "r");	
		
		IndexStructure is = istructure;
		startingAge = sAge;
		endingAge = eAge;
		startingKey = is.ageToKey(startingAge);
		startingOffSet = (startingAge-MIN_AGE)%BUCKET_SIZE - 1;
		endingKey = is.ageToKey(endingAge);
		endingOffSet = (endingAge-MIN_AGE)%BUCKET_SIZE -1;
		
		for(int i = startingKey; i < endingKey+1; i++){
			ArrayList<BigInteger> ids = new ArrayList<BigInteger>();
			//String[] idFromFile;
			ArrayList<String> tmpRec = new ArrayList<String>();
			
			if(istructure != null){
				is = istructure;
				if(is.getMap().get(i) == null){
					continue;
				}
				ids = is.getMap().get(i);
				
				int[] tmp = getRange(ids.size(), i, startingOffSet, endingOffSet, startingKey, endingKey);
				startingPosition = tmp[0];
				endingPosition = tmp[1];
				tmpRec = getTargetRecords(ids, startingPosition, endingPosition, rad);	
			}
//			else{
//				if(getIDs(i) == null){
//					continue;
//				}
//				idFromFile = getIDs(i);
//				int[] tmp = getRange(idFromFile.length, i, startingOffSet, endingOffSet, startingKey, endingKey);
//				startingPosition = tmp[0];
//				endingPosition = tmp[1];
//				tmpRec = getTargetRecords(idFromFile, startingPosition, endingPosition, rad);	
//			}
//					
			result.addAll(tmpRec);
		}
		return result;

	}
	
	/**
	 * Compute the starting position and ending position for each bucket.
	 * @param idLength
	 * @param i
	 * @param startingOffSet
	 * @param endingOffSet
	 * @param startingKey
	 * @param endingKey
	 * @return
	 */
	private int[] getRange(int idLength, int i, int startingOffSet, int endingOffSet, int startingKey, int endingKey){
		int[] p = {0,0};
		int startingPosition = 0;
		int endingPosition = idLength;
		
		if(startingOffSet>=0){
			startingPosition = offsetArray.get(startingKey)[startingOffSet];
		}
		else{
			startingPosition = 0;
		}
		
		if(endingOffSet < 1){
			endingPosition = offsetArray.get(endingKey)[endingOffSet+1];
		}
		else{
			endingPosition = idLength;
		}
		//For different situation, startingP and endingP are different.
		if(endingKey == startingKey){
			p[0] = startingPosition;
			p[1] = endingPosition;
		}
		else if(i == startingKey){
			p[0] = startingPosition;
			p[1] = idLength;
		}
		else if(i == endingKey){
			p[0] = 0;
			p[1] = endingPosition;
		}
		else{
			p[0] = 0;
			p[1] = idLength;
		}
		return p;
	}
	
	/**
	 * Get the records from the text file using hash map and random access file.
	 * @param idArray  All IDs of records that belong to the given range of age.
	 * @param rad  The data file opened by RAF.
	 * @return result  A String array list contains all the targets.
	 * @throws IOException  I have nothing to say here.
	 */
	private ArrayList<String> getTargetRecords(ArrayList<BigInteger> idArray, int startingPosition, int endingPosition, RandomAccessFile rad) throws IOException{
		int k;
		String record = "";
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i=startingPosition; i<endingPosition; i++){
			k = idArray.get(i).intValue();
			byte[] b = new byte[100];
			rad.seek(k*100);
			rad.read(b,0,100);

			record = byteToString(b);		
			result.add(record);

		}
		return result;
	}
	
//	/**
//	 * Get record data from text data file. Overloading the method above.
//	 * @param idArray  This is an array. All IDs of records that belong to the given range of age.
//	 * @param rad  The data file opened by RAF.
//	 * @return result  A String array list contains all the targets.
//	 * @throws IOException  I have nothing to say here.
//	 */
//	private ArrayList<String> getTargetRecords(String[] idArray, int startingPosition, int endingPosition, RandomAccessFile rad) throws IOException{
//		int k;
//		String record = "";
//		ArrayList<String> result = new ArrayList<String>();
//		
//		for(int i=startingPosition; i<endingPosition; i++){
//			k = Integer.parseInt(idArray[i]);
//			byte[] b = new byte[100];
//			rad.seek(k*100);
//			rad.read(b,0,100);
//
//			record = byteToString(b);		
//			result.add(record);
//
//		}
//		return result;
//	}
	
	/**
	 * convert the byte[] read from the data file into a String.
	 * @param b  The byte[] as a record read from the data file.
	 * @return  result  The string.
	 */
	public String byteToString(byte[] b){
		String result = "";
		int c = 0;
		for(int i=0; i<b.length; i++){
			if(i<9 || (i>=39 && i<51)){
				c = Character.getNumericValue(b[i]);
				//System.out.println(c);
				result += c;
			}
			else{
				result += Character.toString((char) b[i]);
			}
		}
		return result;
	}
	
	/**
	 * Read record data from a text file and save it into an array list.
	 * 
	 * @return records  Return an array list of Record objects.
	 * 					Each Record object represents a record. 
	 * @throws IOException
	 */
	public ArrayList<Record> getParsedFile() throws IOException{
		inputStream = new FileReader(filePath);
		
		/**
		 * An integer to store a character read from the text file.
		 */
		int c;
		
		BigInteger recordCount = new BigInteger("0");
		
		String sin = "";
		String fName = "";
		String lName = "";
		String age = "";
		String income = "";
		String addr = "";
		
		try{
			
			while((c=inputStream.read()) != -1){
				charCounter++;
				
				if(charCounter%100 < 10 && charCounter%100 > 0){
					if(c != 32){
						//sin = (int) (Math.pow(10.0, 9.0-charCounter%100) * ((int)c));
						sin += Character.getNumericValue(c);
					}
					else {
						continue;
					}
				}
				else if(charCounter%100 < 25){
					
						fName += (char)c;	
				}
				else if(charCounter%100 < 40){
					
						lName += (char)c;	
				}
				else if(charCounter%100 < 42){
					if(c != 32){
						age += (char)c;
					}
					else{
						continue;
					}
				}
				else if(charCounter%100 < 52){
					if(c != 32){
						income += (char)c;
					}
					else{
						continue;
					}
				}
				else if(charCounter%100 == 0 || charCounter%100 >= 52){
					
						addr += (char)c;
				}
				if(charCounter%100 == 0){
					records.add(new Record());
					records.get(charCounter/100-1).SIN = Integer.parseInt(sin);
					records.get(charCounter/100-1).id = recordCount;
					records.get(charCounter/100-1).firstName = fName;
					records.get(charCounter/100-1).lastName = lName;
					records.get(charCounter/100-1).age = Integer.parseInt(age);
					records.get(charCounter/100-1).income = Integer.parseInt(income);
					records.get(charCounter/100-1).address = addr;
					//records.add(r);
					sin = "";
					fName = "";
					lName = "";
					age = "";
					income = "";
					addr = "";
					recordCount = recordCount.add(BigInteger.ONE);
				}
			} 
		} finally {
		
	            if (inputStream != null) {
	                inputStream.close();
	            }
	        }
//		Test
//		for(int ai=10; ai < 100; ai++){
//			System.out.println(records.get(ai).age);
//		}
		return records;
	}
}

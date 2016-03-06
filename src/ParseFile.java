
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.util.ArrayList;

import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;


/**
 * Parse the text/data File. Get the record from the text/data file.
 * 
 * @author HaoZhang
 */
public class ParseFile {
	
	File path = new File("");
	private final static int MIN_AGE = 18;
//	private final static int MAX_AGE = 99;
	private final static int BUCKET_SIZE = 3; //bucket age size	
	//private final static int TOTAL_BUCKETS = (MAX_AGE - MIN_AGE) / BUCKET_SIZE + 1; //total buckets
	private String filePath = path.getAbsolutePath()+"/src/person.txt";

	
	private int charCounter = 0;
	private ArrayList<Record> records = new ArrayList<Record>();
	
	public ParseFile(){
		
	}
	public ParseFile(String path){
		filePath = path;
	}
	
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
	public ArrayList<String> getRecordsByAge(String datafile, IndexStructure istructure, int sAge, int eAge) throws IOException{
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
			ArrayList<String> tmpRec = new ArrayList<String>();
			
			if(istructure != null){
				if(is.getMap().get(i) == null){
					continue;
				}
				ids = is.getMap().get(i);
				
				int[] tmp = getRange(is, ids, i, startingOffSet, endingOffSet, startingKey, endingKey);
				startingPosition = tmp[0];
				endingPosition = tmp[1];
				tmpRec = getTargetRecords(ids, startingPosition, endingPosition, rad);	
			}
				
			result.addAll(tmpRec);
		}
		return result;

	}
	
	/**
	 * Compute the starting position and ending position for each bucket.
	 * @param idLength  The size of array list contains IDs.
	 * @param i  Current key.
	 * @param startingOffSet
	 * @param endingOffSet
	 * @param startingKey
	 * @param endingKey
	 * @return
	 */
	private int[] getRange(IndexStructure is, ArrayList<BigInteger> ids, int i, int startingOffSet, int endingOffSet, int startingKey, int endingKey){
		int[] p = {0,0};
		int startingPosition = 0;
		int endingPosition = ids.size();
			
		//For different situation, startingP and endingP are different.

		if(i == startingKey){
			if(startingOffSet >= 0){
				startingPosition = is.offsetArray.get(startingKey)[startingOffSet];
			}
			else{
				startingPosition = 0;
			}
			p[0] = startingPosition;
			p[1] = ids.size();
		}
		if(i == endingKey){
			if(endingOffSet < 1){
				endingPosition = is.offsetArray.get(endingKey)[endingOffSet+1];
			}
			else{
				endingPosition = ids.size();
			}
			p[0] = 0;
			p[1] = endingPosition;
		}
		if(i != startingKey && i != endingKey){
			p[0] = 0;
			p[1] = ids.size();
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
		InputStream inputStream = new BufferedInputStream(new FileInputStream(filePath));
		
		/**
		 * An integer to store a character read from the text file.
		 */
		int c;
		
		BigInteger recordCount = new BigInteger("0");
		String age = "";
		
		try{
			
			while((c=inputStream.read()) != -1){
				charCounter++;
				
				if(charCounter%100 > 39 && charCounter%100 < 42){
					if(c != 32){
						age += (char)c;
					}
					else{
						continue;
					}
				}

				if(charCounter%100 == 0){
					records.add(new Record());
					records.get(charCounter/100-1).id = recordCount;
					records.get(charCounter/100-1).age = Integer.parseInt(age);

					age = "";
					recordCount = recordCount.add(BigInteger.ONE);
				}
			} 
		} finally {
		
	            if (inputStream != null) {
	                inputStream.close();
	            }
	        }

		return records;
	}
}

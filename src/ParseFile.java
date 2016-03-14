
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.io.File;


/**
 * Parse the text/data File. Get the record from the text/data file.
 * 
 * @author HaoZhang
 */
public class ParseFile {
	
	private final int MIN_AGE = 18;
	//private final int MAX_AGE = 99;
	private final int REC_SIZE = 100;

	File path = new File("");
	private String DATA_FILE = path.getAbsolutePath()+"/src/person.txt";
	private String INDEX_FILE = path.getAbsolutePath()+"/indexFile";	
//	private long charCounter = 0;
	public long incomeByAge = 0;
	
	public ParseFile(){
		
	}
	
	/**
	 * Get record by age. It read IDs from index file and passes it to getTargetRecord method.
	 * @param sAge  Staring age.
	 * @param eAge  Ending age.
	 * @throws IOException  IO exception.
	 */
	public void getRecordByAge(int sAge, int eAge) throws IOException{
		RandomAccessFile rad = new RandomAccessFile(DATA_FILE, "r");
		RandomAccessFile ix = new RandomAccessFile(INDEX_FILE, "r");
		
		long number = 0;
		long start = MergerFiles.bucketSizes.get(sAge-MIN_AGE);
		long end = MergerFiles.bucketSizes.get(eAge-MIN_AGE+1);
		ix.seek(start+1);  // We made a mistake here before
		incomeByAge = 0;
		
		while(ix.getFilePointer() < end){
			String tmp = ix.readLine();
			
			if(!tmp.equals("")){
				long id = Integer.parseInt(tmp);
				getTargetRecord(id, rad);
				number++;
			}
		}
		System.out.println("\n" + number + " records found.");
		System.out.println("The average income for age from " + sAge + " to " + eAge + " is: "+ incomeByAge/number);
		ix.close();
	}
	
	/**
	 * Get target record from the data file.
	 * @param id  The position of the target record in the data file.
	 * @param rad  The data file opened by Random Access File.
	 * @throws IOException  Throws exception.
	 */
	private void getTargetRecord(long id, RandomAccessFile rad) throws IOException{
		int income;
		String record = "";
		byte[] b = new byte[REC_SIZE];
		rad.seek(id*REC_SIZE);
		rad.read(b,0,REC_SIZE);
		
		record = byteToString(b);
		income = Integer.parseInt(record.substring(42, 52));
		incomeByAge += income;
		//System.out.println(record);
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
	 * @throws InterruptedException 
	 */
	public void getParsedFile(FHashmap is) throws IOException, InterruptedException{
//		FileReader inputStream = new FileReader(DATA_FILE);
//		
//		/**
//		 * An integer to store a character read from the text file.
//		 */
//		int c;
//		
//		BigInteger recordCount = new BigInteger("0");
//		String age = "";
//		String income = "";
//		
//		try{
//			
//			while((c=inputStream.read()) != -1){
//				charCounter++;
//				
//				if(charCounter%REC_SIZE > 39 && charCounter%REC_SIZE < 42){
//					if(c != 32){
//						age += (char)c;
//					}
//					else{
//						continue;
//					}
//				}
//				if(charCounter%REC_SIZE > 41 && charCounter%REC_SIZE < 52){
//					if(c != 32){
//						income += (char)c;
//					}
//					else{
//						continue;
//					}
//				}
//
//				if(charCounter%REC_SIZE == 0){
//					
//					Record r = new Record();
//					r.id = recordCount;
//					if(age.equals(""))
//						continue;
//					r.age = Integer.parseInt(age);
//					
//					if(age.equals(""))
//						r.income = 0;
//					else
//						r.income = Integer.parseInt(income);
//					
//					is.insertToIndex(r);
//
//					age = "";
//					income = "";
//					recordCount = recordCount.add(BigInteger.ONE);
//				}
//			} 
//		} finally {
//		
//	            if (inputStream != null) {
//	                inputStream.close();
//	            }
//	        }
		final int BUFFER_SIZE = 100*1000*10*2+100*1000*5;
        final int REC_SIZE = 100;
    	RandomAccessFile aFile = new RandomAccessFile
                ("/Users/Ethan/Documents/workspace/ix/person.txt", "r");
        FileChannel inChannel = aFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        String age = "";
        long st = System.currentTimeMillis();
        long et;
        int ID = -1;
        Record r = new Record();
        
		while(inChannel.read(buffer) > 0)
        {
            buffer.flip();
            //System.out.println(buffer.limit());
            for (int i = 0; i < buffer.limit(); i += REC_SIZE)
            {
                if(i>buffer.limit()-REC_SIZE){
                	System.out.println(i);
                	continue;
                }
                ID++;
                r.id = ID;
            	age = ""+ (char)buffer.get(i+39) + (char)buffer.get(i+40);
            	r.age = Integer.parseInt(age);
            	is.insertToIndex(r);
            	//System.out.print((char) buffer.get());
                //System.out.println(s.length());
            }
            System.out.println(ID+", "+age);
            buffer.clear(); // do something with the data and clear/compact it.
            //buffer.flip();
            //break;
        }
        inChannel.close();
        aFile.close();
        et = System.currentTimeMillis();
        System.out.println("Building time is " + (et-st));
    
	}
}

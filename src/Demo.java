import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.clapper.util.misc.FileHashMap;
import org.clapper.util.misc.ObjectExistsException;
import org.clapper.util.misc.VersionMismatchException;

import java.io.File;

public class Demo {

	public static void main(String[] args) throws IOException, ObjectExistsException, ClassNotFoundException, VersionMismatchException {
		// TODO Auto-generated method stub
	
		ParseFile test = new ParseFile();
		ArrayList<Record> list = new ArrayList<Record>();
		IndexStructure indexStructure;
		File path = new File("");
		
		String datafile = path.getAbsolutePath()+"/src/person.txt";
		String IN_FILE = path.getAbsolutePath() + "/src/mymap";
		
		File indexfile = new File(IN_FILE);
		list = test.getParsedFile();
		ArrayList<String> result = new ArrayList<String>();
		int startingAge = 0;
		int endingAge = 0;
		long startingTime = 0;
		long endingTime = 0;
	
		//IMPROVEMENT: compare md5 of data file if different than before, the ncompute index file
		if(!indexfile.exists()){
			System.out.println("Please wait while the Index File is being built...");
			indexStructure = new IndexStructure(FileHashMap.FORCE_OVERWRITE);
			startingTime = System.currentTimeMillis();
			for(int i = 0; i < list.size(); i++){
				indexStructure.insertToIndex(list.get(i));
			}
			indexStructure.saveIndexStructure();
			//indexStructure.saveIndexStructure();
			endingTime = System.currentTimeMillis();
			System.out.println("Index File Build Successfully in " + (endingTime-startingTime)/1000 + "s");
			System.out.println("Index File Size: " + indexStructure.size() + " Bytes.");
		}
		else{
			indexStructure = new IndexStructure(FileHashMap.NO_CREATE);
		}
		
		//test.getSplitValueAndOffsets();
		/*
		 * Get the input from user in a loop.
		 */
		Scanner sc = new Scanner(System.in);
		do{
			System.out.println("Please enter the starting age: ");
			startingAge = sc.nextInt();
		
			if(startingAge == -1)
				break;
			if(startingAge < 18 || startingAge > 99){
				System.out.println("Error: starting age must be greater than 17 and less than 100!");
				continue;
			}
		
			System.out.println("Please enter the ending age: ");
			endingAge = sc.nextInt();
			startingTime = System.currentTimeMillis();
//			System.out.println(startingTime);
			if(endingAge >99 || endingAge < 18){
				System.out.println("Error: ending age must be less than 100 and greater than 17!");
				continue;
			}
			else if(startingAge > endingAge){
				System.out.println("Error: starting age must be smaller than ending age!");
				continue;
			}
			else{
				result = test.getRecordsByAge(datafile, null, startingAge, endingAge);
				
				long resultSize = result.size();
				for(int i = 0; i < result.size(); i++){
					System.out.println(result.get(i));
				}
				endingTime = System.currentTimeMillis();
//				System.out.println(endingTime);
//				System.out.println("Searching the file takes: " + (endingTime-startingTime) + "milliseconds.");
				System.out.println("\nTotal Records Found: "+ resultSize);
				System.out.println("Index File Search Time: " + (endingTime-startingTime)+ "ms");
				continue;
			}	
		}
		while(true);
		sc.close();	
		System.out.println("Exit successfully!");
	}

}


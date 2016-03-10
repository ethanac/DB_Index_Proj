
import java.io.IOException;
import java.util.Scanner;

public class Demo {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		final int MIN_AGE = 18;
		final int MAX_AGE = 99;
		ParseFile parseFile = new ParseFile();
		FHashmap index = new FHashmap();

		int startingAge = 0;
		int endingAge = 0;
		long startingTime = 0;
		long endingTime = 0;
	
		/*
		 * Initialize the index structure.
		 */
		startingTime = System.currentTimeMillis();
		if(!index.exists()){
			System.out.println("Please wait while the Index File is being built...");
			//parseFile.getParsedFile(index);
			//index.flush();
			MergerFiles.merge();
			//System.out.println("\nThe average income is: " + index.incomeSum/index.number);			
		}
		else{
			MergerFiles.getOffsets();
		}
		
		endingTime = System.currentTimeMillis();
		System.out.println("Index File Build Successfully in " + (endingTime-startingTime)/1000 + "s");
		System.out.println("Index File Size: " + index.getSize() + " Bytes.");
		
		/*
		 * Get the input from user in a loop.
		 */
		Scanner sc = new Scanner(System.in);
		do{
			System.out.println("Please enter the starting age: ( enter -1 when you want to exit. )");
			startingAge = sc.nextInt();
		
			if(startingAge == -1)
				break;
		
		if(startingAge < MIN_AGE || startingAge > MAX_AGE){
				System.out.println("Error: starting age must be greater than 17 and less than 100!");
				continue;
			}
		
			System.out.println("Please enter the ending age: ");
			endingAge = sc.nextInt();
			startingTime = System.currentTimeMillis();
			System.out.println(startingTime);
		
			if(endingAge > MAX_AGE || endingAge < MIN_AGE){
				System.out.println("Error: ending age must be less than 100 and greater than 17!");
				continue;
			}
			else if(startingAge > endingAge){
				System.out.println("Error: starting age must be smaller than ending age!");
				continue;
			}
			else{
				parseFile.getRecordByAge(startingAge, endingAge);
				endingTime = System.currentTimeMillis();
				
				System.out.println(endingTime);
				System.out.println("Searching the file takes: " + (endingTime-startingTime) + " milliseconds.");
				System.out.println("Index File Search Time: " + (endingTime-startingTime)+ " ms");
				continue;
			}	
		}
		while(true);
		sc.close();	
		System.out.println("Exit successfully!");
	}
}


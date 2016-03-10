import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;

public class MergerFiles {
	final static int TOTAL_BUCKETS = 82;
	public static ArrayList<Long> bucketSizes = new ArrayList<Long>();
	private static long sum = 0;
	
	public static void merge() throws IOException {
		File path = new File("");
		Files.write(Paths.get("offsetsFile"), "".getBytes(), StandardOpenOption.CREATE);
		File[] files = new File[TOTAL_BUCKETS];
		String sourceFilePath = "";
		String mergedFilePath = path.getAbsolutePath() + "/indexFile";
		
		for(int i=0; i < TOTAL_BUCKETS; i++){
			sourceFilePath = path.getAbsolutePath() + "/ix"+i;
			files[i] = new File(sourceFilePath);
		}
		
		File mergedFile = new File(mergedFilePath);
 
		mergeFiles(files, mergedFile);
		BufferedWriter writer = new BufferedWriter(new FileWriter("offsetsFile"));
		for(long i : bucketSizes){
			writer.write(i+"");
			writer.newLine();
		}
		writer.close();
		sum = 0;
	}
 
	private static void mergeFiles(File[] files, File mergedFile) {
 
		FileWriter fstream = null;
		BufferedWriter out = null;
		try {
			fstream = new FileWriter(mergedFile, true);
			 out = new BufferedWriter(fstream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
 
		for (File f : files) {

			System.out.println("merging: " + f.getName());
			FileInputStream fis;
			bucketSizes.add(sum);
			sum += f.length();
			try {
				fis = new FileInputStream(f);
				BufferedReader in = new BufferedReader(new InputStreamReader(fis));
				
				String aLine;
				while ((aLine = in.readLine()) != null) {
					out.write(aLine+System.lineSeparator());
					//out.newLine();
				}
				
				//Files.delete(Paths.get(f.getName()));
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		bucketSizes.add(sum);
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
 
	}
	
	public static void getOffsets() throws FileNotFoundException{
		Scanner sc = new Scanner(new File("offsetsFile"));
		while(bucketSizes.size() < 82)
			bucketSizes.add(sc.nextLong());
		sc.close();
	}
}
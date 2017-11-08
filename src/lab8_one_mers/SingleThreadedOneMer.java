package lab8_one_mers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
//import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadedOneMer {
	private static String myDirectoryPath = "/Users/aaronyerke/Desktop/fall_2017/adv_prog/adenomasRelease-master/fasta";
	private static File[] directoryListing;
	private static int fileCounter;
	private static HashMap<String, Integer> nucleotideHash = new HashMap<>();
	private static BufferedReader reader;
	
	//instantiate calendar
	Calendar cal = Calendar.getInstance();
	
	//gather all the files in the directory
	private static void fileHerder()
	{
	directoryListing = new File(myDirectoryPath).listFiles();
	fileCounter = directoryListing.length-1;
	nucleotideHash.put("A", 0);
	nucleotideHash.put("C", 0);
	nucleotideHash.put("G", 0);
	nucleotideHash.put("T", 0);
	nucleotideHash.put("N", 0);
	}
	
	private static File fileShoot()
	{
	File file = directoryListing[fileCounter];
	fileCounter -= 1;
	return file;
	}
	
	private static void nucleotideCounter(File file) throws IOException
	{
		if( ! file.getName().startsWith("D5"))
			return;
		
		System.out.println(file.getName());
		
		reader = new BufferedReader( new FileReader( file ));
		
		for (String nextLine = reader.readLine(); nextLine != null; 
				nextLine = reader.readLine()) 
		{
			nextLine = nextLine.trim();
			
			if (nextLine.charAt(0) != '>')
			{
				for (Character ch: nextLine.toUpperCase().toCharArray()) {
					if (ch == 'A') {
						nucleotideHash.put("A", nucleotideHash.get("A") + 1);
					}
					else if (ch == 'C') {
						nucleotideHash.put("C", nucleotideHash.get("C") + 1);
					}
					else if (ch == 'G') {
						nucleotideHash.put("G", nucleotideHash.get("G") + 1);
					}
					else if (ch == 'T') {
						nucleotideHash.put("T", nucleotideHash.get("T") + 1);
					}
					else{
						nucleotideHash.put("N", nucleotideHash.get("N") + 1);
					}
						
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Single Threaded");
		long start = System.currentTimeMillis();
		fileHerder();
		while (fileCounter > -1){
			try {
				nucleotideCounter(fileShoot());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(Arrays.asList(nucleotideHash));
		long end = System.currentTimeMillis();
		System.out.println("Time: " + String.valueOf(end-start) + " miliseconds");
		
	}
	
}
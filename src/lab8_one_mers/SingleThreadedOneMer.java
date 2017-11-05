package lab8_one_mers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadedOneMer {
	private static String myDirectoryPath = "/Users/aaronyerke/Desktop/fall_2017/adv_prog/adenomasRelease-master/fasta";
	private static File[] directoryListing;
	private static int fileCounter;
	private static ConcurrentHashMap<String, Integer> nucleotideHash = new ConcurrentHashMap<>();
	private static BufferedReader reader;
	
	//instantiate calendar
	private static Calendar cal = Calendar.getInstance();
	
	//gather all the files in the directory
	private static void fileHerder()
	{
	File[] directoryListing = new File(myDirectoryPath).listFiles();
	System.out.println(directoryListing.toString());
	fileCounter = directoryListing.length;
	
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
	System.out.println(directoryListing.toString());
	return file;
	}
	
	private static void nucleotideCounter(File file) throws IOException
	{
		reader = new BufferedReader( new FileReader( file ));
		
		for (String nextLine = reader.readLine(); nextLine != null; 
				nextLine = reader.readLine()) 
		{
			if (nextLine.charAt(0) != '>')
			{
				for (Character ch: nextLine.toUpperCase().toCharArray()) {
					if (ch == 'A') {
						nucleotideHash.put("A", nucleotideHash.get("A"));
					}
					if (ch == 'C') {
						nucleotideHash.put("C", nucleotideHash.get("C"));
					}
					if (ch == 'G') {
						nucleotideHash.put("G", nucleotideHash.get("G"));
					}
					if (ch == 'T') {
						nucleotideHash.put("T", nucleotideHash.get("T"));
					}
					else{
						nucleotideHash.put("N", nucleotideHash.get("N"));
					}
						
				}
			}
		}
	}
	
	public static void main(String[] args)
	{
		System.out.println("Single Threaded");
		long start = cal.getTimeInMillis();	
		fileHerder();
		while (fileCounter > 0){
			try {
				nucleotideCounter(fileShoot());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(Arrays.asList(nucleotideHash));
		long end = cal.getTimeInMillis();	
		System.out.println("Time: " + (end-start)/1000 + " seconds");
		
	}
	
}
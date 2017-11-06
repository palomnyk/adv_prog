package lab8_one_mers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class MultiThreadedOneMer {
	private static String myDirectoryPath = "/Users/aaronyerke/Desktop/fall_2017/adv_prog/adenomasRelease-master/fasta";
	private static File[] directoryListing;
	private static int fileCounter;
	private static ConcurrentHashMap<String, Integer> nucleotideHash = new ConcurrentHashMap<>();
	private static BufferedReader reader;
	
	//gather all the files in the directory
	private static void fileHerder()
	{
	directoryListing = new File(myDirectoryPath).listFiles();
	fileCounter = directoryListing.length - 1;
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
		reader = new BufferedReader( new FileReader( file ));
		
		for (String nextLine = reader.readLine(); nextLine != null; 
				nextLine = reader.readLine()) 
		{
			if (nextLine.charAt(0) != '>')
			{
				for (Character ch: nextLine.toUpperCase().toCharArray()) {
					if (ch == 'A') {
						nucleotideHash.put("A", nucleotideHash.get("A") + 1);
					}
					if (ch == 'C') {
						nucleotideHash.put("C", nucleotideHash.get("C") + 1);
					}
					if (ch == 'G') {
						nucleotideHash.put("G", nucleotideHash.get("G") + 1);
					}
					if (ch == 'T') {
						nucleotideHash.put("T", nucleotideHash.get("T") + 1);
					}
					else{
						nucleotideHash.put("N", nucleotideHash.get("N") + 1);
					}
						
				}
			}
		}
	}
	
	private static class nucleotideWrangler implements Runnable{
		
		private final File file;
		private final Semaphore semaphore;

		public nucleotideWrangler(File file, Semaphore semaphore) {
			this.file = file;
			this.semaphore = semaphore;
		}
		public void run() {
			try {
				nucleotideCounter(file);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("exit");
				System.exit(1);
			}
			finally{
				semaphore.release();
			}
			
		}
	}
	
	
	public static void main(String[] args)
	{
		System.out.println("Multi-Threaded");
		long start = System.currentTimeMillis();
		fileHerder();
		
		Semaphore s = new Semaphore(5);
		
		for (int i = 0; i < fileCounter; i++){
			try {
				s.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			nucleotideWrangler nW = new nucleotideWrangler(directoryListing[i], s);
			new Thread(nW).start();
			
			/*try {
				nucleotideCounter(fileShoot());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		System.out.println(Arrays.asList(nucleotideHash));
		long end = System.currentTimeMillis();
		System.out.println("Time: " + String.valueOf(end-start) + " miliseconds");
		
	}
	

}

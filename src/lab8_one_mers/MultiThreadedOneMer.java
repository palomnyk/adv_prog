package lab8_one_mers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class MultiThreadedOneMer {
	private static String myDirectoryPath = "/Users/aaronyerke/Desktop/fall_2017/adv_prog/adenomasRelease-master/fasta";
	private static File[] directoryListing;
	private static int fileCounter;
	private static int a_glob = 0;
	private static int c_glob = 0;
	private static int g_glob = 0;
	private static int t_glob = 0;
	private static int n_glob = 0;
	/*private static ConcurrentHashMap<String, Integer> nucleotideHash = new ConcurrentHashMap<>();*/
	private static BufferedReader reader;
	
	//gather all the files in the directory
	private static void fileHerder()
	{
	directoryListing = new File(myDirectoryPath).listFiles();
	fileCounter = directoryListing.length;
	System.out.println(fileCounter);
	/*
	nucleotideHash.put("A", 0);
	nucleotideHash.put("C", 0);
	nucleotideHash.put("G", 0);
	nucleotideHash.put("T", 0);
	nucleotideHash.put("N", 0);
	*/
	}
	
	private static class nucleotideWrangler implements Callable<List<Integer>>
	{
		private final File file;
		//private final Semaphore semaphore;
		

		nucleotideWrangler(File file)//, Semaphore semaphore)// int a, int c, int g, int t, int n)
		{
			this.file = file;
			//this.semaphore = semaphore;
			/*this.a = a;
			this.c = c;
			this.g = g;
			this.t = t;
			this.n = n;*/
			
		}
		public List<Integer> call() {
			int a = 0;
			int c = 0;
			int g = 0;
			int t = 0;
			int n = 0;
			try {		
				reader = new BufferedReader( new FileReader( file ));
				//System.out.println(reader);
				for (String nextLine = reader.readLine().trim(); nextLine != null; 
						nextLine = reader.readLine()) 
				{
					if (nextLine.charAt(0) != '>')
					{
						for (Character ch: nextLine.toUpperCase().toCharArray()) {
							if (ch == 'A') {
								a+=1;
							}
							else if (ch == 'C') {
								c+=1;
							}
							else if (ch == 'G') {
								g+=1;
							}
							else if (ch == 'T') {
								t+=1;
							}
							else{
								n+=1;
							}
								
						}
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("exit");
				System.exit(1);
			}
			finally{
				//semaphore.release();
			}
			
			List<Integer>result = Arrays.asList(a,c,g,t,n);
			
			return result;
			
		}
	}
	
	
	public static void main(String[] args) throws ExecutionException
	{
		System.out.println("Multi-Threaded");
		long start = System.currentTimeMillis();
		fileHerder();
		
		//Semaphore s = new Semaphore(4);
		
		ExecutorService executor = Executors.newFixedThreadPool(4);
        List <Future<List<Integer>>> list = new ArrayList<Future<List<Integer>>>();
        
        //System.out.println(fileCounter);
        
		for(int i = 0; i < fileCounter; i++)
		{
			//System.out.println(directoryListing[i]);
			Future<List<Integer>> future = executor.submit(new nucleotideWrangler(directoryListing[i]));
			list.add(future);
		}
		System.out.println(list.size());
		executor.shutdown();
		try {
			executor.awaitTermination(100000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			  System.out.println(e);
		}
		
		for (Future<List<Integer>> fut : list)
        {
			System.out.println(fut);
                    try {
                    		List<Integer> glob = fut.get();
                    		System.out.println(glob);
                    		a_glob += glob.get(0);
                    		c_glob += glob.get(1);
                    		g_glob += glob.get(2);
                    		t_glob += glob.get(3);
                    		n_glob += glob.get(4);
                    		
                    } catch (InterruptedException e) {                                               
                                e.printStackTrace();
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                    }
        }                       
		
		
		
		System.out.printf("%d %d %d %d %d\n", a_glob, c_glob, g_glob, t_glob, n_glob);
		long end = System.currentTimeMillis();
		System.out.println("Time: " + String.valueOf(end-start) + " miliseconds");
		
	}
	

}

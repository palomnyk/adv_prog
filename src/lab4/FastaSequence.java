/**
 * Advanced Programming
 * 
 * This is a fasta file parser for lab4 and lab5.
 * 
 * @author aaronyerke
 * @19 Sept. 2017
 */


package lab4;

import java.io.*;
import java.util.*;

public class FastaSequence 
{
	
	private String header;
	private String sequence;
	private static Map<String,Integer> innerMap;
	
	
	public FastaSequence(String header, String sequence) {
		this.header = header;
		this.sequence = sequence;
	}
	

	// returns the header of this sequence without the “>”
	public String getHeader()
	{
		return this.header;
	}
	
	public static void seqCountTable(File inFile, File outFile ) throws Exception
	{
		//fasta sequences to be processed
		List<FastaSequence> input = readFastaFile(inFile.getAbsolutePath());
		
		//hashmap nested in hashmap to hold samples and sequence counts
		Map<String, Map<String,Integer>> samples = new HashMap<>();
		
		//hack to make the code work
	   	LinkedHashSet<String> seqsInOrder = new LinkedHashSet<>();
		
		for(int i = 0; i < input.size(); i++)
		{
			FastaSequence query = input.get(i);
			String seq = query.getSequence();
			StringTokenizer sToken = new StringTokenizer(query.getHeader());
		   	sToken.nextToken();
		   	String sample = sToken.nextToken();
		   	
		   	//add seq to set of seqsInOrder
		   	seqsInOrder.add(seq);
		   	
		   	//holds sequences and counts
		   	innerMap = samples.get(sample);
		   	
		   	if( innerMap == null)
		   	{
		   		innerMap = new HashMap<>();
		   		//make a new innermap store it in innerMap
		   		innerMap.put(seq, 1);
		   		//and add it to sequences with key samples
		   		samples.put(sample, innerMap);
		   	}else 
		   	{
		   		// get the counts for the sample from the innermap
		   		Integer count = innerMap.get(seq);
		   		
		   		// (if it is null set it to zero)
		   		if (count == null)
		   		{
		   			innerMap.put(seq, 1);
		   		}else
		   		{
		   		// increment the counts by 1
		   			count += 1;
		   		// add it back to the innermaps
		   			innerMap.put(seq, count);
		   		}

		   	}
		}  	
		//make a sorted set of samples
		Set<String> sortedSamples = new TreeSet<>(samples.keySet());
		
		//list to hold sequences after hashmap is made; will become set of seqs
		Set<String> seqs = new TreeSet<>(); 
		
		for (String x: sortedSamples)
		{
			Set<String> singleSamp = samples.get(x).keySet();
			
			for (String j: singleSamp)
				{
					seqs.add(j);
				}
		}
		
	
		PrintWriter writer = new PrintWriter(outFile.getAbsolutePath(), "UTF-8");
		
		writer.print("sample\t");
		
		for (String x : seqsInOrder)
		{
			writer.print(x + "\t");
		}
		writer.print('\n');
		
		for (String x: sortedSamples)
		{
			writer.print(x + "\t");
			for (String k: seqsInOrder)
			{
				Integer count = (samples.get(x)).get(k);
				if (count != null)
				{
					writer.print(count.toString() + '\t');
				}
				else 
				{
					writer.print("0\t");
				}
				
			}
			writer.print('\n');
		}
		writer.close();
		}
	
	
	// returns the Dna sequence of this FastaSequence
	public String getSequence() 
	{
		return this.sequence;
	}
	
	// returns the number of G’s and C’s divided by the length of this sequence
	public float getGCRatio()
	{
		sequence = sequence.toUpperCase();
		
		Double gcCount = new Double(0);
		

		
		for (int i = 0; i < sequence.length(); i++) 
		{
			char m = sequence.charAt(i);
			
			if (m == 'G' || m == 'C') 
			{
				gcCount += 1;
			}
		}
		Double ratio = new Double(gcCount/sequence.length());
		return ratio.floatValue();
	}
	
	//Counts every seq and returns the seq and the count
	//For lab 5
	public static void writeUnique(File inFile, File outFile ) throws Exception
	{
		//fasta sequences to be processed
		List<FastaSequence> input = readFastaFile(inFile.getAbsolutePath());
		
		//empty hashmap to hold seq and count
		Map<String, Integer> dict = new HashMap<>();
		
		for(int i = 0; i < input.size(); i++)
		{
			FastaSequence query = input.get(i);
			String seq = query.getSequence();
			
			Integer test = dict.get(seq);
			
			//check if there is already a sequence at this slot
			if (test != null)
			{
				dict.put(seq, test + 1);
			}
			else
			{
				dict.put(seq, 1);
			}
		}
		
		//make a sorted list of values
		Set<Integer> mySet = new TreeSet<>( dict.values() );
		
		PrintWriter writer = new PrintWriter(outFile.getAbsolutePath(), "UTF-8");
        
        for( Integer x: mySet )
        {
              writer.println( ">" + x );
              for( String val: dict.keySet() )
              {
                    if( dict.get( val ).equals( x ) )
                    {
                          writer.println( val ); 
                    }
              }
        }
		writer.close();
	}
 
	
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception
	{
		String currentHeader = null;
		StringBuffer currentSeq = new StringBuffer();
		
		List<FastaSequence> fasta = new ArrayList<FastaSequence>();
		
		BufferedReader reader = new BufferedReader( new FileReader( new File( filepath )));
				
			for (String nextLine = reader.readLine(); nextLine != null; 
					nextLine = reader.readLine()) 
			{
				if (nextLine.charAt(0) == '>')
				{
					if (currentHeader != null) 
					{
						FastaSequence fastaEntry = new FastaSequence(currentHeader, currentSeq.toString());
						
						fasta.add(fastaEntry);
						
						//reset header and sequence
						currentHeader = null;
						currentSeq = new StringBuffer();
						
					}
					currentHeader = nextLine;
				}
				else
				{
					currentSeq.append(nextLine);
				}
			}
			//unload the last sequence
			FastaSequence fastaEntry = new FastaSequence(currentHeader, currentSeq.toString());
			fasta.add(fastaEntry);
			
			//close reader
			reader.close();
			
			//output list
			return fasta;
		
	}
	
	public static void main(String[] args) throws Exception
	{
	
	List<FastaSequence> fastaList = FastaSequence.readFastaFile("/Users/aaronyerke/git/sample.fasta");

	for( FastaSequence fs : fastaList)
	{
	//System.out.println(fs.getHeader());
	//System.out.println(fs.getSequence());
	//System.out.println(fs.getGCRatio());
	
	}
	seqCountTable(new File("/Users/aaronyerke/git/seqsIn.txt"), new File("/Users/aaronyerke/git/output.txt"));
	}


}

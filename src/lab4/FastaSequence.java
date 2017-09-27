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
	
	
	
	public FastaSequence(String header, String sequence) {
		this.header = header;
		this.sequence = sequence;
	}
	

	// returns the header of this sequence without the “>”
	public String getHeader()
	{
		return this.header;
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
		List<FastaSequence> input = readFastaFile(inFile.getAbsolutePath());
		
		Map<String, Integer> dict = new HashMap<>();
		
		for(int i = 0; i < input.size(); i++)
		{
			FastaSequence query = input.get(i);
			String seq = query.getSequence();
			
			Integer test = dict.get(seq);
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
	writeUnique(new File("/Users/aaronyerke/git/sample.fasta"), new File("/Users/aaronyerke/git/output.txt"));
	}


}

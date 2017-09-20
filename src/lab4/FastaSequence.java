/**
 * Advanced Programing
 * 
 * This is a fasta file parser for lab4.
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
	
	public static List<FastaSequence> readFastaFile(String filepath) throws Exception
	{
		String currentHeader = "";
		String currentSeq = "";
		
		List fasta = new ArrayList<FastaSequence>();
		
		BufferedReader reader = new BufferedReader( new FileReader( new File( filepath )));
				
			for (String nextLine = reader.readLine(); nextLine != null; 
					nextLine = reader.readLine()) 
			{
				if (nextLine.charAt(0) == '>')
				{
					if (currentHeader != "") 
					{
						FastaSequence fastaEntry = new FastaSequence(currentHeader, currentSeq);
						
						fasta.add(fastaEntry);
						
						//reset header and sequence
						currentHeader = "";
						currentSeq = "";
						
					}
					currentHeader = nextLine;
				}
				else
				{
					currentSeq += nextLine;
				}
			}
			//unload the last sequence
			FastaSequence fastaEntry = new FastaSequence(currentHeader, currentSeq);
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
	System.out.println(fs.getHeader());
	System.out.println(fs.getSequence());
	System.out.println(fs.getGCRatio());
	}
	}


}

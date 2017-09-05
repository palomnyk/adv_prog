package lab2;

import java.util.Random;

public class RandomSequencesEC
{
	public static void main (String[] args) 
	{
		String nuc = null;
		
		int aaaCounter = 0;
		
		int aCounter = 0;
		
		Random random = new Random();
		
		for(int i = 0; i<1000; i++)
		{
			int ranNum = random.nextInt(100);
			
			if (ranNum <= 12)
			{
				nuc = "A";
			}
			else if (ranNum > 12 && ranNum <= 50)
			{
				nuc = "C";
			}
			else if (ranNum > 50 && ranNum <= 89)
			{
				nuc = "G";
			}
			else
			{
				nuc = "T";
			}
			
			if (nuc == "A")
			{
				aCounter += 1;
			} 
			else
			{
				aCounter = 0;
			}
			
			if (aCounter == 3)
			{
				aaaCounter += 1;
				aCounter = 0;
			}
			
			System.out.println(nuc);
		}
		
		System.out.println("AAA is expected to appear 1.728 times (.12*.12*.12*1000).  In this program, it appeared " + aaaCounter + " times.");

	}
	

}

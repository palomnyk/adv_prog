package lab2;

import java.util.Random;

public class RandomSequences 
{
	public static void main (String[] args) 
	{
		String nuc = null;
		
		int aaaCounter = 0;
		
		int aCounter = 0;
		
		Random random = new Random();
		
		for(int i = 0; i<1000; i++)
		{
			int ranNum= random.nextInt(4);
			
			switch (ranNum)
			{
			case 0: nuc = "A";	
				break;
			case 1: nuc = "C";
				break;
			case 2: nuc = "G";
				break;
			case 3: nuc = "T";
				break;
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
		
		System.out.println("AAA is expected to appear 15.65 times (0.25*0.25*0.25*1000).  In this program, it appeared " + aaaCounter + " times.");

	}
	

}

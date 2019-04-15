package lab3;

import java.util.Calendar;
import java.util.Random;

public class Game 
{
	public static String[] SHORT_NAMES = 
	{
		"A","R", "N", "D", "C", "Q", "E", 
		"G",  "H", "I", "L", "K", "M", "F", 
		"P", "S", "T", "W", "Y", "V" 
	};

	public static String[] FULL_NAMES = 
	{
		"alanine","arginine", "asparagine", 
		"aspartic acid", "cysteine",
		"glutamine",  "glutamic acid",
		"glycine" ,"histidine","isoleucine",
		"leucine",  "lysine", "methionine", 
		"phenylalanine", "proline", 
		"serine","threonine","tryptophan", 
		"tyrosine", "valine"
	};

	public static void main (String[] args) 
	{
		//instantiate timer and get ending time
		Calendar cal = Calendar.getInstance();
		long endTime = cal.getTimeInMillis() + 30000;
		
		//instancaiate string for later use
		String aString = null;
		
		int correct = 0;
		
		int count = 0;
		
		while (true)
		{
			Random random = new Random();
			//find current time (now)
			cal = Calendar.getInstance();
			long now =  cal.getTimeInMillis();
			
			//check if time is up
			if (now < endTime)
			{
				int amino = random.nextInt(20);
				System.out.println("What is the single letter code for " + FULL_NAMES[amino] + "?");
				aString = System.console().readLine().toUpperCase();
				if (aString.equals(SHORT_NAMES[amino])) 				
				{
					correct += 1;
					System.out.println("Correct!");
				}
				else {
					System.out.println("Incorrect! It's " + SHORT_NAMES[amino] + ".");
				} 
				count += 1;
			}
			else 
			{
				System.out.println("Time's up! You answered " + correct + " out of " + count + " correctly.");
				break;
			}
		}
	}
}

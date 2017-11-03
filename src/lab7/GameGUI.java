package lab7;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Random;

public class GameGUI extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JTextField aTextField = new JTextField();
	private JButton startGame = new JButton("Start the game");
	private JButton cancel = new JButton("Cancel");
	private JTextField answerBox = new JTextField(20);
	private JTextField counter = new JTextField();
	private JTextField timer = new JTextField(20);
	private JTextField feedback = new JTextField();
	private static final Random random = new Random();
	private volatile boolean Continue = true;
	String aminoFull;
	String aminoLet;
	String input = null;
	int correct = 0;
	int count = 0;
	
	
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

	
	public GameGUI() 
	{
		super("Learn your amino acids!");
		setLocationRelativeTo(null);
		setSize(400,200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(aTextField, BorderLayout.EAST);
		getContentPane().add(startGame, BorderLayout.NORTH);
		getContentPane().add(cancel, BorderLayout.WEST);
		//getContentPane().add(feedback, BorderLayout.WEST);
		aTextField.setText("Learn the amino acids!");
		getContentPane().add(answerBox, BorderLayout.SOUTH);
		startGame.addActionListener(new StartGameListener());
		//getContentPane().add(counter, BorderLayout.CENTER);

		answerBox.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e){
				String input = answerBox.getText().toUpperCase();
				
				if (input.equals(aminoLet)) 				
				{
					correct += 1;
					updateStartGame("Correct! Correct answer number: " + correct);
					updateCounter();
				}
				else {
					updateStartGame("Incorrect! It's " + aminoLet + ".");
				} 
				count += 1;
				
				makeQuestion();
				}
			});
		
		setVisible(true);
	}
	
	private void updateCounter() 
	{
		counter.setText(Integer.valueOf(correct).toString());
		validate();
	}
		
	
	private void updateTextField(String message)
	{
		aTextField.setText(message);
		validate();
	}
	
	private void updateStartGame(String message)
	{
		startGame.setText(message);
		validate();
	}
	
	private void updateFeedback(String message)
	{
		feedback.setText(message);
		validate();
	}
	
	private class StartGameListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			runGame();
		}
	}
	
	public void makeQuestion() {
		if (Continue == true){
		int amino = random.nextInt(20);
		System.out.println(amino);
		aminoFull = FULL_NAMES[amino];
		System.out.println(amino);
		aminoLet = SHORT_NAMES[amino];
		System.out.println(correct);
		
		updateTextField("What is the single letter code for " + aminoFull + "?");
		System.out.println(aminoFull);
		}else {
			System.out.println("ovr");
		}
		
		
		
		
	}
	
	public void runGame()
	{
		//instantiate timer and get ending time
		Calendar cal = Calendar.getInstance();
		long endTime = cal.getTimeInMillis() + 30000;
		long now =  cal.getTimeInMillis();
		
		/*correct = 0;		
		count = 0;*/
		
		//boolean continue = True
		//while (now < endTime)
		{		
			//System.out.println(amino);
			
			
			
			
			//int amino = random.nextInt(20);
			//updateTextField("What is the single letter code for " + FULL_NAMES[amino] + "?");
			makeQuestion();
			
			//find current time (now)
			cal = Calendar.getInstance();
			now =  cal.getTimeInMillis();
			}
	//	updateTextField("Time's up! You answered " + correct + " out of " + count + " correctly.");
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(
				new Runnable() {
					public void run() {
						new GameGUI();
					}	
			});
	}

}


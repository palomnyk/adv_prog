/**
 * Author: Aaron Y
 * lab 7 for Adv progr
 * This is a GUI for the amino acid quiz.
 */

package lab7;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class GameGUI extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static JTextField aTextField = new JTextField();
	private static JButton startGame = new JButton("Start the game");
	private static JButton cancel = new JButton("Cancel");
	private static JTextField answerBox = new JTextField(20);
	private static JTextField timerDisplay = new JTextField();
	private static final Random random = new Random();
	private volatile static boolean Continue = true;
	String aminoFull;
	String aminoLet;
	String input = null;
	static int correct = 0;
	int count = 0;
	private static final int SECONDS = 10;
	
	
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
		getContentPane().add(timerDisplay, BorderLayout.EAST);
		timerDisplay.setText("Timer");
		getContentPane().add(startGame, BorderLayout.NORTH);
		getContentPane().add(cancel, BorderLayout.WEST);
		getContentPane().add(aTextField, BorderLayout.CENTER);
		aTextField.setText("Learn the amino acids!");
		getContentPane().add(answerBox, BorderLayout.SOUTH);
		startGame.addActionListener(new StartGameListener());
		//getContentPane().add(counter, BorderLayout.CENTER);

		answerBox.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e){
				String input = answerBox.getText().toUpperCase();
				
				if (Continue == true) {
				if (input.equals(aminoLet)) 				
				{
					correct += 1;
					updateStartGame("Correct! Correct answer count: " + correct);
				}
				else {
					updateStartGame("Incorrect! It's " + aminoLet + ".");
				} 
				count += 1;
				
				answerBox.setText("");
				
				makeQuestion();
				}
			}
			});
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				answerBox.setEditable(false);
				startGame.setEnabled(true);
				cancel.setEnabled(false);
				updateTextField("Thanks for playing!  You correctly got ".concat(Integer.valueOf(correct).toString().concat(" correct!")));
				updateStartGame("Start the game");
				Continue = false;
				try{  
				Timer.interrupt();  
				}catch(Exception e1){System.out.println("Exception handled "+e1);}  
				
			}
		});
		
		setVisible(true);
	}
	
	private class StartGameListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			(new Thread(new Timer())).start();
			startGame.setEnabled(false);
			answerBox.setEnabled(true);
			cancel.setEnabled(true);
			Continue = true;
			correct = 0;		
			count = 0;
			runGame();
		}
	}
	
	private static void updateTimer(String message) 
	{
		timerDisplay.setText(message);
	}
		
	
	private static void updateTextField(String message)
	{
		aTextField.setText(message);
	}
	
	private static void updateStartGame(String message)
	{
		startGame.setText(message);
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
			System.out.println("over");
		}
	}
	
	public void runGame()
	{
		makeQuestion();
		answerBox.setEditable(true);
	}
	
	private static class Timer implements Runnable
	{	
		@Override
		public void run() {
		try{		
			for (int i = SECONDS; i > 0; i--) {
				System.out.println("sleeping");
				Thread.sleep(1000);
				updateTimer(Integer.valueOf(i).toString());
			}
			updateTextField("Time's up! You correctly got ".concat(Integer.valueOf(correct).toString().concat(" correct!")));
			answerBox.setEditable(false);
			startGame.setEnabled(true);
			cancel.setEnabled(false);
			Continue = false;
			updateStartGame("Start the game");
			}catch(InterruptedException e){  
			throw new RuntimeException("Thread interrupted..."+e);  
			}
			
		}

		public static void interrupt() {
			System.out.println("interrupted");
			
		}  
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


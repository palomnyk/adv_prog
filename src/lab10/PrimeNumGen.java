package lab10;
/**A GUI that uses multithreading to calculate prime numbers and display
 * them on the screen
 * Authors: Anthony Fodor and Aaron Yerke
 */

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class PrimeNumGen extends JFrame
{
	
	private final JTextArea aTextField = new JTextArea();
	private final JButton primeButton = new JButton("Start");
	private final JButton cancelButton = new JButton("Cancel");
	private volatile boolean cancel = false;
	private final PrimeNumGen thisFrame;
	private AtomicInteger primeCount = new AtomicInteger(0);
	private volatile int maxInt;
	private volatile long lastUpdate;
	private volatile StringBuffer buff = new StringBuffer();
	private volatile HashSet<Thread> primeThreads = new HashSet<Thread>();
	private volatile long numAvailableThreads;
	private volatile boolean semUsed;
	
	public static void main(String[] args)
	{
		PrimeNumGen png = new PrimeNumGen("Primer Number Generator");
		
		// don't add the action listener from the constructor
		png.addActionListeners();
		png.setVisible(true);
		
	}
	
	private PrimeNumGen(String title)
	{
		super(title);
		this.thisFrame = this;
		cancelButton.setEnabled(false);
		aTextField.setEditable(false);
		setSize(200, 200);
		setLocationRelativeTo(null);
		//kill java VM on exit
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(primeButton,  BorderLayout.SOUTH);
		getContentPane().add(cancelButton,  BorderLayout.EAST);
		getContentPane().add( new JScrollPane(aTextField),  BorderLayout.CENTER);
	}
	
	private void addActionListeners()
	{
		cancelButton.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				System.out.println("canceled");
				cancel = true;
				for (Thread t : primeThreads) {
					try {
						System.out.println(t);
						t.interrupt();
					}catch(Exception e1)
					{
						System.out.println("Exception handled "+e1);
						
					}
				}
				primeButton.setEnabled(true);
				cancelButton.setEnabled(false);
				aTextField.setText("Request canceled by user");
				//set window to blank
			}
		});
			
		primeButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e)
				{
					
					String num = JOptionPane.showInputDialog("Enter a large integer");
					Integer max =null;
					
					try
					{
						max = Integer.parseInt(num);
						System.out.println(max);
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(
								thisFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
					
					if( max != null)
					{
						maxInt = max;
						aTextField.setText("");
						primeButton.setEnabled(false);
						cancelButton.setEnabled(true);
						cancel = false;
						new Thread(new UserInput()).start();

					}
				}});
		}
	
	private boolean isPrime( int i)
	{
		for( int x=2; x < i -1; x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	private class PrimeProducer implements Runnable {
		private final Set<Integer> set;
		private final int start;
		private Semaphore sem;
		
		private PrimeProducer(Set<Integer> set, int start, Semaphore sem) {
			this.set = set;;
			this.start = start;
			this.sem = sem;
		}
		public void run() {
			//System.out.println(numAvailableThreads);
			//System.out.println(maxInt);
			//System.out.println(start);
			//System.out.println(cancel);
			/** This for loop should allow for each thread to iterate through the 
			 * numbers from different starts to max without ever hitting on the same
			 * number.
			 */
			try {
				//System.out.println("Before acquire");
				sem.acquire();
				semUsed = true;
				//System.out.println("After acquire");
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				System.out.println("Revise your semaphore math");
				e1.printStackTrace();
			}
			for (int i = start; i < maxInt && ! cancel; i = (int) (i + numAvailableThreads)) {
				//System.out.println(i);
			if (isPrime(i)){
					set.add(i);
					primeCount.getAndIncrement();
								}
			}
			System.out.println("dumping semaphore");
			sem.release();
		}
	}
	
	//Make consumer class
	private class PrimeConsumer implements Runnable {
		private final Set<Integer> set;
		private Semaphore sem;
		
		
		private PrimeConsumer(Set<Integer> set, Semaphore sem) {
		this.set = set;
		this.sem = sem;
		}
		
		public void run() {
			
	        try {
	            while (sem.availablePermits() + 1 < numAvailableThreads ) {
	            		//System.out.println("time is " + System.currentTimeMillis());
	                //System.out.println(number);
	                //System.out.println(System.currentTimeMillis() - lastUpdate);
	                if( System.currentTimeMillis() - lastUpdate >= 500)
					{
						final String outString= "Found " + primeCount.get() + 
								 "so far in " + maxInt;
						System.out.println("gooot innnnnnnnn");
						Thread.sleep(100);
						
						SwingUtilities.invokeLater( new Runnable()
						{
							@Override
							public void run()
							{
								aTextField.setText(outString);
							}
						});
						
						lastUpdate = System.currentTimeMillis();	
					}
	                //System.out.println("out of loop");
	                //System.out.println(done);               
	            }
                		System.out.println("out of loop");
	                //next thing
	                //final StringBuffer buff = new StringBuffer();
                		
                		if (sem.availablePermits() ==numAvailableThreads -1) {
                			
                			buff = new StringBuffer();
                			
                			for( Integer i : set)
                			{
                				buff.append(i + "\n");
                			}
                			
	    				if( cancel == true)
	    				buff.append("cancelled");
	    				System.out.println("answer = " + buff);
	    				SwingUtilities.invokeLater( new Runnable()
	    				{
	    				@Override
	    				public void run()
	    				{
	    					cancel = false;
	    					primeButton.setEnabled(true);
	    					cancelButton.setEnabled(false);
	    					aTextField.setText( (cancel ? "cancelled " : "") +  buff.toString());
	    				}
	    				});
                		}
	            System.out.println("passed while loop");
	        } catch (InterruptedException e) {
	            //Thread.currentThread().interrupt();
	        }
	    }
		
	}
	
	
	
	private class UserInput implements Runnable
	{
		private UserInput()
		{
		}
		
		public void run()
		{	
			cancel = false;
			
			lastUpdate = System.currentTimeMillis();
			
			semUsed = false;
			
			numAvailableThreads =  Runtime.getRuntime().availableProcessors();
			
			//leave one thread for consumer
			Semaphore sem = new Semaphore((int) (numAvailableThreads - 1));
			
			final Set<Integer> set = Collections.synchronizedSet(new TreeSet<Integer>());
			
			for (int i = 1; i < numAvailableThreads; i++) {
				System.out.println("creating producer thread");
				Thread t = new Thread(new PrimeProducer(set, i, sem));
				primeThreads.add(t);
				t.start();
			}
			
			/**
			 * The semUsed boolean will be tripped to true when the sem is acquired.  This should
			 * prevent the possibility of the consumer thread starting finishing before any semaphores are 
			 * acquired because the while loop will 
			 */
			
			while (semUsed == false) {
				System.out.println("Prevented race condition");
				Thread.yield();
			}
			
			System.out.println("creating consumer thread");
			Thread u = new Thread(new PrimeConsumer(set, sem));
			u.start();
			primeThreads.add(u);

			
			
		}// end run
		
	}  // end UserInput
}
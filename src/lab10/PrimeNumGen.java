package lab10;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
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
	private volatile int done = 0;
	private final PrimeNumGen thisFrame;
	private AtomicInteger primeCount = new AtomicInteger(0);
	private volatile int maxInt;
	private volatile long lastUpdate;
	private volatile StringBuffer buff = new StringBuffer();
	private volatile HashSet<Thread> primeThreads = new HashSet<Thread>();
	private volatile long numProducerThreads;
	
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
	
	private class CancelOption implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0)
		{
			System.out.println("canceled");
			cancel = true;
			for (Thread t : primeThreads) {
				try {
					t.interrupt();
				}catch(Exception e1)
				{
					System.out.println("Exception handled "+e1);
					
				}
			}
			primeButton.setEnabled(true);
			cancelButton.setEnabled(false);
		}
	}
	
	private void addActionListeners()
	{
		cancelButton.addActionListener(new CancelOption());
	
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
		int num = (int) Math.sqrt(i);
		for( int x=2; x < num; x+=1)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	private class PrimeProducer implements Runnable {
		private BlockingQueue<Integer> queue;
		private final int start;
		
		private PrimeProducer(BlockingQueue<Integer> queue, int start) {
			this.queue = queue;
			this.start = start;
		}
		public void run() {
			//System.out.println(numProducerThreads);
			//System.out.println(maxInt);
			//System.out.println(start);
			//System.out.println(cancel);
			for (int i = start; i < maxInt && ! cancel; i = (int) (i + numProducerThreads)) {
				System.out.println(i);
			if (isPrime(i)){
				try {
					queue.put(i);
					primeCount.getAndIncrement();
					//System.out.println("adding queue");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			done += 1;
		}
	}
	
	//Make consumer class
	private class PrimeConsumer implements Runnable {
		private BlockingQueue<Integer> queue;
		
		private PrimeConsumer(BlockingQueue<Integer> queue) {
		this.queue = queue;
		}
		
		public void run() {
	        try {
	            while (done < numProducerThreads) {
	            		System.out.println("time is " + System.currentTimeMillis());
	                Integer number = queue.take();
	                System.out.println(number);
	                if( System.currentTimeMillis() - lastUpdate > 500)
					{
						final String outString= "Found " + primeCount.get() + " in " + number + " of " + maxInt;
						System.out.println("gooot innnnnnnnn");
						Thread.sleep(10000);
						
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
	                //next thing
	                //final StringBuffer buff = new StringBuffer();
	    				buff.append(number + "\n");
	    			
	    				if( cancel == true)
	    				buff.append("cancelled");
	    			
	    				SwingUtilities.invokeLater( new Runnable()
	    				{
	    				@Override
	    				public void run()
	    				{
	    					cancel = false;
	    					primeButton.setEnabled(true);
	    					cancelButton.setEnabled(true);
	    					aTextField.setText( (cancel ? "cancelled " : "") +  buff.toString());
	    				}
	    				});
	                
	            }
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
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
			done = 0;
			
			lastUpdate = System.currentTimeMillis();
			
			numProducerThreads =  Runtime.getRuntime().availableProcessors();
			
			final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(500);
			
			//1+4 2+4 3+4 4+4
			
			for (int i = 1; i < numProducerThreads; i++) {
				System.out.println("creating producer thread");
				Thread t = new Thread(new PrimeProducer(queue, i));
				primeThreads.add(t);
				t.start();
			}
			System.out.println("creating consumer thread");
			Thread u = new Thread(new PrimeConsumer(queue));
			u.start();
			primeThreads.add(u);

			
			
		}// end run
		
	}  // end UserInput
}
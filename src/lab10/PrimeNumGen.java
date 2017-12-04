package lab10;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	private volatile int max;
	private volatile long lastUpdate;
	
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
			cancel = true;
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
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(
								thisFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
						ex.printStackTrace();
					}
					
					if( max != null)
					{
						aTextField.setText("");
						primeButton.setEnabled(false);
						cancelButton.setEnabled(true);
						cancel = false;
						new Thread(new UserInput(max)).start();

					}
				}});
		}
	
	private boolean isPrime( int i)
	{
		for( int x=2; x < Math.sqrt(i); x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	//Make producer class
	private class PrimeProducer implements Runnable {
		private BlockingQueue<Integer> queue;
		private final int start;
		//private final int max;
		
		private PrimeProducer(BlockingQueue<Integer> queue, int start) {
			this.queue = queue;
			this.start = start;
			//this.max = max;
		}
		public void run() {
			for (int i = start; i < max && ! cancel; i+=4) {
			if (isPrime(i) == true){
				try {
					queue.put(i);
					primeCount.getAndIncrement();
					System.out.println("adding queue");
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
	            while (done < 4) {
	            		System.out.println("time is " + System.currentTimeMillis());
	                Integer number = queue.take();
	                System.out.println(number);
	                if( System.currentTimeMillis() - lastUpdate > 500)
					{
						final String outString= "Found " + primeCount.get() + " in " + number + " of " + max;
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
	                final StringBuffer buff = new StringBuffer();
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
	    					cancelButton.setEnabled(false);
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
		private UserInput(int num)
		{
			max = num;
		}
		
		public void run()
		{	
			cancel = false;
			done = 0;
			
			lastUpdate = System.currentTimeMillis();
			
			final BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(500);
			
			for (int i = 0; i < 4; i++) {
				System.out.println("creating producer thread");
			    new Thread(new PrimeProducer(queue, i)).start();
			}
			 
			for (int j = 0; j < Runtime.getRuntime().availableProcessors(); j++) {
				System.out.println("creating consumer thread");
				new Thread(new PrimeConsumer(queue)).start();
			}
			
			
		}// end run
		
	}  // end UserInput
}
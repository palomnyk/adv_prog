package lab10;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
	//private Long lastUpdate;
	
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
	
	private static boolean isPrime( int i)
	{
		for( int x=2; x < i -1; x++)
			if( i % x == 0  )
				return false;
		
		return true;
	}
	
	private static class PrimeWorker implements Callable<Integer>
	{
		private final Integer factor;
	
		private PrimeWorker(int factor){
			this.factor = factor;
		}
		public Integer call() {
		
			Integer result;
				if (!isPrime(factor)) {
					result = factor;
				}else {
					result = null;
			}
			return result;
		}
	}					
	
	
	
	private class UserInput implements Runnable 
	{
		private final int max;
		
		private UserInput(int num)
		{
			this.max = num;
		}
		
		public void run()
		{
			long lastUpdate = System.currentTimeMillis();
			//List<Integer> list = new ArrayList<Integer>();
			
			System.out.println(Runtime.getRuntime().availableProcessors());
			
			ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	        List <Future<Integer>> list = new ArrayList<Future<Integer>>();
			
			for (int i = 1; i < max && ! cancel; i++) 
			{
				//add PrimeWorker here
				Future<Integer> future = executor.submit(new PrimeWorker(i));
				list.add(future);
			}
			executor.shutdown();
			try {
				executor.awaitTermination(100000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				  System.out.println(e);
			}
			for (Future<Integer> fut : list) {
				Integer i = null;
				try {
					i = fut.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if( System.currentTimeMillis() - lastUpdate > 500)
				{
					final String outString= "Found " + list.size() + " in " + i + " of " + max;
					
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
			final StringBuffer buff = new StringBuffer();
			
			buff.append(i + "\n");
			
			if( cancel) {
				buff.append("cancelled");
			}
			
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
			
			
		}// end run
		
	}  // end UserInput
}
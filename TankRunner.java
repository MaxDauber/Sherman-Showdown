/**
 * TankRunner.java prompts the user for the number of players and sets
 * up the JFrame in which the TankComponent is housed.
 * @author Grant Fischer
 * @author Max Dauber
 * Period: 2
 * Date: 5/19/2017
 */

//Import necessary packages
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class TankRunner
{
	public static void main(String[] args)
	{
		//Gets the number of players
		int numPlayers = getPositiveInt("Enter number of players:");
		
		//Creates the frame
		JFrame frame = new JFrame();
		frame.setSize(820, 920);
		frame.setTitle("Sherman Showdown");
		frame.setLocation(0, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Adds the game component to the frame
		TankComponent component = new TankComponent(numPlayers);
		frame.add(component);
		frame.setVisible(true);    
	}
	
	/**
	 * 
	 * @param prompt a question asking the user to input the number of players
	 * @return the number of players about to play
	 */
	public static int getPositiveInt(String prompt) 
	{
		String input = "";
		int posInt = 0;
		while (true) 
		{
			try 
			{
				input = JOptionPane.showInputDialog(null, prompt);
				posInt = Integer.parseInt(input);
				if (posInt < 1) 
				{
					throw new Exception("Must be positive value.  Try again");
				}
				if (posInt < 2 || posInt > 4) 
				{
					throw new Exception("Must be between 2 and 4 players. "
							+ "Try again.");
				}
				return posInt;
			} 
			catch (NumberFormatException e) 
			{
				JOptionPane.showMessageDialog(null, "Numbers only.  "
						+ "Try again.");
			} 
			catch (Exception e) 
			{
				JOptionPane.showMessageDialog(null, e.getMessage());
			}
		}
	}
}

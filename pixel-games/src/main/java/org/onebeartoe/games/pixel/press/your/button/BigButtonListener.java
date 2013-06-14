
package org.onebeartoe.games.pixel.press.your.button;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rmarquez
 */
class BigButtonListener implements Runnable 
{
    private int pressCount = 0;
    private volatile PressYourButton plugin;

    BigButtonListener(final PressYourButton plugin) 
    {
	this.plugin = plugin;
    }

    public void run() 
    {
	while (true) 
	{
	    try 
	    {
		if (plugin.analogInput1 == null) 
		{
		    System.out.println("\nAnalog 1: " + plugin.analogInput1);
		}
		else 
		{
		    float a1 = plugin.analogInput1.readBuffered();
		    int signal = (int) a1;
		    if (signal == 1 && plugin.gameState == GameStates.PLAYERS_TURN)
		    {
			plugin.endCurrentPlayersTurn();
									
			pressCount++;			
			System.out.println("Analog 1: " + a1 + " - press count: " + pressCount);
		    }
		}
		Thread.sleep(60);
	    } 
	    catch (Exception ex) 
	    {
		Logger.getLogger(PressYourButton.class.getName()).log(Level.SEVERE, null, ex);
	    }
	}
    }
    
}

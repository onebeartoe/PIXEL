
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author rmarquez
 */
public class PressYourButtonWorker implements ActionListener 
{
    private final PressYourButton plugin;

    public PressYourButtonWorker(final PressYourButton plugin) 
    {
	this.plugin = plugin;
    }

    /**
     * Every time the application communicates with the PIXEL/IOIO, this method 
     * is called.
     * 
     * The frequency can be adjusted with calls to ...
     * 
     * @param e 
     */
    @Override
    public void actionPerformed(ActionEvent e) 
    {
	switch (plugin.gameState) 
	{
	    case NEXT_PLAYERS_TURN:
	    {
		plugin.nextPlayersTurn();
		break;
	    }
	    case END_OF_GAME:
	    {
		plugin.endOfGame();
		break;
	    }
	    case END_OF_TURN:
	    {
		plugin.endOfTurn();
		break;
	    }
	    case SHOW_SCORE:
	    {
		plugin.showScore();
		break;
	    }
	    default:
	    {
		plugin.newGameConfiguration();
	    }
	}
    }
    
}

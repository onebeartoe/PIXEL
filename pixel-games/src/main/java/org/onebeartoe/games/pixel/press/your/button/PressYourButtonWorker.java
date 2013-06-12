
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

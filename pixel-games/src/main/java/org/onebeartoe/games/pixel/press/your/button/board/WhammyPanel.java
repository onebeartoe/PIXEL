
package org.onebeartoe.games.pixel.press.your.button.board;

import java.awt.Color;

/**
 *
 * @author rmarquez
 */
public class WhammyPanel extends BoardPanel
{

    public WhammyPanel()
    {
	super(Color.BLACK);
	amount = -1;
    }
    
    @Override
    public String getLabel() 
    {
	return "W";
    }
    
}

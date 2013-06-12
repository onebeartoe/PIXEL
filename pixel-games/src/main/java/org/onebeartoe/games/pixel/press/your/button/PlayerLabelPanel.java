
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Color;

/**
 * @author rmarquez
 */
public class PlayerLabelPanel extends BoardPanel
{    
    private String label;
    
    public PlayerLabelPanel(String label)
    {
	super(Color.BLACK);
	
	this.label = label;
    }

    @Override
    public String getLabel() 
    {
	return label;
    }
    
}

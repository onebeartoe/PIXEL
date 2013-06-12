
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Color;

public class MoneyPanel extends BoardPanel
{
    protected int amount;    

    public MoneyPanel(Color backgroundColor, int amount) 
    {
	super(backgroundColor);

	this.amount = amount;
    }

    @Override
    public String getLabel() 
    {
	return String.valueOf(amount);
    }

}

package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

public class MoneyPanel extends BoardPanel
{
//    private int amount;

    private Font font;

    public MoneyPanel(Color backgroundColor, int amount) 
    {
	super(backgroundColor);

	this.amount = amount;

	String fontFamily = "Arial";            
	font = new Font(fontFamily, Font.PLAIN, 32);
    }


    @Override
    protected void draw(Graphics2D g2d, Point location, Color foreground) 
    {
	super.draw(g2d, location, foreground);

	g2d.setColor(foreground);

	FontMetrics fm = g2d.getFontMetrics();   

	String text = String.valueOf(amount);
	int textWidth = fm.stringWidth(text);	    
	float center = (location.x + dimension.width) / 2.0f;
	float x =  center - (textWidth / 2.0f);	
x = location.x + 1;

	int height = fm.getHeight();
	center = (location.y + dimension.height) / 2.0f;
	float y = center - (height / 2.0f);
y = location.y + 32;

//cd System.out.println("\namount: " + amount + " - x: " + x + " - y: " + y + " - width: " + dimension.width + " - height: " + dimension.height);
	
	g2d.setFont(font);	    
	g2d.drawString(text, x, y);
    }

}
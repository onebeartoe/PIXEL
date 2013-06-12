
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class BoardPanel
{
    protected Color backgroundColor;

    protected Dimension dimension;

    protected Point location;
    
    private Font font;

    public BoardPanel(Color backgroundColor)
    {
	this.backgroundColor = backgroundColor;

	this.dimension = new Dimension(42,42);
	
	String fontFamily = "Arial";            
	font = new Font(fontFamily, Font.PLAIN, 32);
    }

    protected void draw(Graphics2D g2d, Point location, Color foreground)
    {
	g2d.setColor(backgroundColor);
	g2d.fillRect(location.x, location.y, dimension.width, dimension.height);
	
	
	
	g2d.setColor(foreground);

	FontMetrics fm = g2d.getFontMetrics();   

	String text = getLabel();
	
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
    
    public abstract String getLabel();
}
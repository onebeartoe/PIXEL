
package org.onebeartoe.games.pixel.press.your.button.board;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import org.onebeartoe.games.pixel.press.your.button.PressYourButton;

public abstract class BoardPanel
{
    protected Color backgroundColor;

    protected Dimension dimension;

    protected Point location;
    
    public int amount;
    
    private Font font;

    public BoardPanel(Color backgroundColor)
    {
	this.backgroundColor = backgroundColor;

        int panelWidth = PressYourButton.gamePanelWidth;
        
	this.dimension = new Dimension(panelWidth, panelWidth);
	
	String fontFamily = "Arial";            
	font = new Font(fontFamily, Font.PLAIN, 32);
    }

    public void draw(Graphics2D g2d, Point location, Color foreground)
    {
	g2d.setColor(backgroundColor);
	g2d.fillRect(location.x, location.y, dimension.width, dimension.height);	
	g2d.setColor(foreground);

	String text = getLabel();
	
	float x = location.x + 1;
	float y = location.y + 32;
	
	g2d.setFont(font);	    
	g2d.drawString(text, x, y);
    }
    
    public abstract String getLabel();
}
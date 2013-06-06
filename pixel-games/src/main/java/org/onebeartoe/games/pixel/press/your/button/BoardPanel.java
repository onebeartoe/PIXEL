
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

public abstract class BoardPanel
{
    protected Color backgroundColor;

    protected Dimension dimension;

    protected Point location;

    public BoardPanel(Color backgroundColor)
    {
	this.backgroundColor = backgroundColor;

	this.dimension = new Dimension(42,42);
//	this.dimension = new Dimension(32,32);
    }

    protected void draw(Graphics2D g2d, Point location)
    {
	g2d.setColor(backgroundColor);
	g2d.fillRect(location.x, location.y, dimension.width, dimension.height);
    }
}
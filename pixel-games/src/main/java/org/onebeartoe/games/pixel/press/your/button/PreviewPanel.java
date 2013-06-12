
package org.onebeartoe.games.pixel.press.your.button;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 * @author rmarquez
 */
public class PreviewPanel extends JPanel 
{
    
    private Image image;
    
    private final PressYourButton plugin;

    public PreviewPanel(final PressYourButton plugin) 
    {
	this.plugin = plugin;
	setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    @Override
    public void paintComponent(Graphics g) 
    {	
	super.paintComponent(g);
	
	setAlignmentX(Component.CENTER_ALIGNMENT);
	
	if(image != null) 
	{
	    g.drawImage(image, 0, 0, plugin);
	}
    }

    public void setImage(Image image) 
    {
	this.image = image;
    }
    
}

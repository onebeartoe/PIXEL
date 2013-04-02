
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.plugins.PixelPlugin;
import javax.swing.JPanel;

/**
 * @author rmarquez
 */
public class ZeroThreadedPixelPanel extends JPanel implements PixelPlugin
{
    
    protected boolean pixelFound;

    @Override
    public void setPixelFound(boolean found) 
    {
	this.pixelFound = found;
    }    
	
    @Override
    public void startPixelActivity()
    {
        System.out.println("Starting PIXEL activity 0 in " + getClass().getSimpleName() + ".");
    }
    
    @Override
    public void stopPixelActivity()
    {
        
    }

}

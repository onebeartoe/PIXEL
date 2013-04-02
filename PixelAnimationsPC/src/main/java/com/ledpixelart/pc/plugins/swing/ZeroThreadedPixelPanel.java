
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
    public void stopPixelActivity()
    {
        
    }

    @Override
    public void setPixelFound(boolean found) 
    {
	this.pixelFound = found;
    }    
}

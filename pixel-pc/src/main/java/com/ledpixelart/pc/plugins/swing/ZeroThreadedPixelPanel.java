
package com.ledpixelart.pc.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import org.onebeartoe.pixel.plugins.swing.PixelPanel;

/**
 * @author rmarquez
 */
public class ZeroThreadedPixelPanel extends PixelPanel
//public class ZeroThreadedPixelPanel extends JPanel implements PixelPlugin
{
    
    public ZeroThreadedPixelPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
    }
    
    protected boolean pixelFound;

    @Override
    public void setPixelFound(boolean found) 
    {
	this.pixelFound = found;
    }    
	
    @Override
    public void startPixelActivity()
    {
        System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");
    }
    
    @Override
    public void stopPixelActivity()
    {
        
    }

}


package com.ledpixelart.pc.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public abstract class SingleThreadedPixelPanel extends PixelPanel
{
    
    private Timer timer;
    
    protected boolean pixelFound;
    
    public SingleThreadedPixelPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
    }

    @Override
    public void setPixelFound(boolean found) 
    {
	this.pixelFound = found;
    }    
	
    @Override
    public void startPixelActivity()
    {
	System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");		
	ActionListener listener = getActionListener();
	timer = new Timer(150, listener);
	timer.start();
    }
   
    @Override
    public void stopPixelActivity()
    {	
        if(timer != null && timer.isRunning() )
        {            
	    System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }
    }
    
    abstract ActionListener getActionListener();
}

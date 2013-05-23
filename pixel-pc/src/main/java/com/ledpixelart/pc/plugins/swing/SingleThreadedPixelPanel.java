
package com.ledpixelart.pc.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public abstract class SingleThreadedPixelPanel extends PixelPanel
{
    
    protected Timer timer;
    
    protected boolean pixelFound;
    
//    protected volatile int tickDelay;
    
    public SingleThreadedPixelPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
	
	// set the IOIO loop delay to half a second, by default  (was 150)
//	tickDelay = 150;
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
	int defaultDelay = 150;
	timer = new Timer(defaultDelay, listener);
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
    
    //TODO: this needs a better name
    protected abstract ActionListener getActionListener();
}

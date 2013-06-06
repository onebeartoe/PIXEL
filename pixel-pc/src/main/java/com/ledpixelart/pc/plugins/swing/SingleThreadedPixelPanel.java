
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
    
    protected int tickDelay;
    
    public SingleThreadedPixelPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
	
	// set the IOIO loop delay to half a second, by default  (was 150)
	tickDelay = 150;
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
	timer = new Timer(tickDelay, listener);
	timer.start();
    }
   
    @Override
    public void stopPixelActivity()
    {	
	String className = getClass().getSimpleName();
	System.out.println("Preparing to stoping PIXEL activity in " + className + ".");
        if(timer != null)// && timer.isRunning() )
        {            
	    System.out.println("Stoping PIXEL activity in " + className + ".");
            timer.stop();
        }
    }
    
//TODO: this needs a better name
    protected abstract ActionListener getActionListener();
}


package org.onebeartoe.pixel.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * @author rmarquez
 */
public abstract class SingleThreadedPixelPanel extends PixelPanel
{
    
    volatile protected Timer timer;
    
    protected boolean pixelFound;

    public SingleThreadedPixelPanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
	
    }        
	
    @Override
    public void startPixelActivity()
    {
	System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");		
	ActionListener listener = getActionListener();
	
	 if (pixel !=null) pixel.interactiveMode();  //AL added this in the event we were in localplayback mode
	 
	// set the IOIO loop delay to half a second, by default
	int delay = 500; // milliseconds
	timer = new Timer(delay, listener);
	
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

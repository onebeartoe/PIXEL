
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
		
		if (getClass().getSimpleName().equals("ScrollingTextPanel") && ScrollingTextPanel.twitterTextCheckBox.isSelected()) {
			 System.out.println("Starting Twitter search timer to go off every " + ScrollingTextPanel.twitterSearchInterval.getSelectedItem().toString());
			 ScrollingTextPanel.twitterTimer = new Timer(ScrollingTextPanel.twitterSearchDelayValue, ScrollingTextPanel.TwitterTimer);
			 ScrollingTextPanel.twitterTimer.start();
		}
		
    }
   
    @Override
    public void stopPixelActivity()
    {	
		String className = getClass().getSimpleName();
		System.out.println("Preparing to stop PIXEL activity in " + className + ".");
	        if (timer != null && timer.isRunning() )
	        {            
		    System.out.println("Stopping PIXEL activity in " + className + ".");
	            timer.stop();
	        }
	        
	        if (ScrollingTextPanel.twitterTimer != null && ScrollingTextPanel.twitterTimer.isRunning() )
	        {
	            System.out.println("Stopping Twitter Search Timer");
	            ScrollingTextPanel.twitterTimer.stop();
	        }        
    }
    
//TODO: this needs a better name
    protected abstract ActionListener getActionListener();
}

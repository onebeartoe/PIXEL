
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class AnimationsHttpHandler extends ImageResourceHttpHandler
{
    public AnimationsHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        basePath = "animations/";
        defaultImageClassPath = "arrows.png";
        modeName = "animation";
    }
    
    @Override
    protected void writeImageResource(String imageClassPath) throws IOException, ConnectionLostException
    {
        logger.log(Level.INFO, "animation handler is writing " + imageClassPath);
        
        // the writeAnimation() method just takes the name of the file
        int i = imageClassPath.lastIndexOf("/") + 1;
        String animationName = imageClassPath.substring(i);
            
        boolean saveAnimation = false;
        
        if( imageClassPath.contains("/save/") )
        {
            saveAnimation = true;
        }
        
        if( animationName.equals("animations") )
        {
            animationName = defaultImageClassPath;
        }   
        
        Pixel pixel = application.getPixel();
        pixel.stopExistingTimer();
        pixel.writeAnimation(animationName, saveAnimation);
        
        // we should wait a bit before going back to interactive mode, or it jitters
        long fifteenSeconds = Duration.ofSeconds(15).toMillis();
        Sleeper.sleepo(100);
        
        pixel.interactiveMode();
    }
}

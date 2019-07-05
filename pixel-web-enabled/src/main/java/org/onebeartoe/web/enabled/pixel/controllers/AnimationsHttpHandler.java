
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.CliPixel;
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
        defaultImageClassPath = "0rain";
        modeName = "animation";
    }
    
    @Override
    protected void writeImageResource(String imageClassPath) throws IOException, ConnectionLostException
    {
        //logger.log(Level.INFO, "animation handler is writing " + imageClassPath);
        LogMe logMe = LogMe.getInstance();
        if (!CliPixel.getSilentMode()) {
                System.out.println("animation handler is writing " + imageClassPath);
                logMe.aLogger.info("animation handler is writing " + imageClassPath);
        }
        
        // the writeAnimation() method just takes the name of the file
        int i = imageClassPath.lastIndexOf("/") + 1;
        String animationName = imageClassPath.substring(i);
        String arcadeNameOnly = "";
        
        //System.out.println("string: " + imageClassPath);  //animation/0pacgosts.png or //animation/save/0pacghosts
        //System.out.println("animationName: " + animationName);  //0pacghosts.png
        
        boolean saveAnimation = false;
        
        if( imageClassPath.contains("/save/") )
        {
            saveAnimation = true;
        }
        
        arcadeNameOnly = FilenameUtils.getBaseName(imageClassPath); //get the name only WITHOUT extension as we'll add .gif later
        
        if( animationName.equals("animations") )  //this is the default when the mode has switched here
        {
            arcadeNameOnly = defaultImageClassPath;
        } 
        
        if (!CliPixel.getSilentMode()) {
                System.out.println("Animation Handler sending GIF: " + arcadeNameOnly + ".gif");
                logMe.aLogger.info("Animation Handler sending GIF: " + arcadeNameOnly + ".gif");
        }
        
         Pixel pixel = application.getPixel();
        
        try {
            // pixel.writeAnimation(animationName, saveAnimation); //old code, this only worked for gifs that were in the original jar
            pixel.writeAnimationFilePath("animations", arcadeNameOnly + ".gif", saveAnimation);  
        } catch (NoSuchAlgorithmException ex) {
            //Logger.getLogger(AnimationsHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

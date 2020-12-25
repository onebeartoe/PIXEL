
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class RandomModeHttpHandler extends ImageResourceHttpHandler
{
    public RandomModeHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        basePath = "animations/";
        defaultImageClassPath = "0rain";
        modeName = "animation";
    }
    
    @Override
    
    protected void writeImageResource(String urlParams) throws IOException, ConnectionLostException
    {
        
        String text_ = "";
        int loop_ = 0;
        String color_ = "";
        String streamOrWrite = null ;
 	String consoleName = null ;
 	String arcadeName = null ;
        String arcadeNameExtension = null; 
        String arcadeNameOnly = null;
        boolean saveAnimation = false;
        boolean randomGIF = false;

        //logger.log(Level.INFO, "animation handler is writing " + imageClassPath);
        LogMe logMe = LogMe.getInstance();
        if (!CliPixel.getSilentMode()) {
                System.out.println("random mode handler is writing " + urlParams);
                logMe.aLogger.info("random handler is writing " + urlParams);
        }
        
        List<NameValuePair> params = null;
        try {
                params = URLEncodedUtils.parse(new URI(urlParams), "UTF-8");
            } catch (URISyntaxException ex) {
                Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (NameValuePair param : params) {
           
             switch (param.getName()) {

                    case "t": //scrolling text value
                        text_ = param.getValue();
                        break;
                    case "text": //scrolling speed
                        text_ = param.getValue();
                        break;
                    case "l": //how many times to loop
                        loop_ = Integer.valueOf(param.getValue());
                        // Long speed = Long.valueOf(s); //to do for integer
                        break;
                    case "loop": //loop
                       loop_ = Integer.valueOf(param.getValue());
                        break;
                    case "c": //color
                       color_ = param.getValue();
                       break;
                    case "r": //random   
                        randomGIF = true;
                        break;
                    }
        }
        
        logMe = LogMe.getInstance();
        if (!CliPixel.getSilentMode()) {
            System.out.println("random mode handler received: " + urlParams);
            logMe.aLogger.info("random mode handler received: " + urlParams);
        }
       
        Pixel pixel = application.getPixel();
        
        List<String> animationrandom = application.loadAnimationList();
        
        for (int i = 0; i < animationrandom.size(); i++) { //let's load up the Q randomly
        //let's set a timer and play each gif for x seconds where x comes from the url
            
            Random rand = new Random();
            String randomAnimationName = animationrandom.get(new Random().nextInt(animationrandom.size()));
            System.out.println("Random Animation: " + randomAnimationName );
            logMe.aLogger.info("Random Animation:: " + randomAnimationName);
            arcadeNameOnly = FilenameUtils.getBaseName(randomAnimationName); //stripping out the extension
            
            //try {
            //    Thread.sleep(10);
            //} catch (InterruptedException ex) {
            //    Logger.getLogger(RandomModeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
           // }
           
           //to do add a check to see how long the gif is and set the loop accordingly

            try {
                pixel.writeAnimationFilePath("animations", arcadeNameOnly + ".gif", saveAnimation,1,WebEnabledPixel.pixelConnected);  
            } catch (NoSuchAlgorithmException ex) {
               
            }
        
        }
        
        
        
    }
}


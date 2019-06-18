
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.system.Sleeper;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class ArcadeHttpHandler extends ImageResourceHttpHandler
{
    public ArcadeHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        basePath = "arcade/";
        defaultImageClassPath = "arrows.png"; //to do change this
        modeName = "arcade";
    }
    
    @Override
    protected void writeImageResource(String imageClassPath) throws IOException, ConnectionLostException
    {
        
    	String streamOrWrite = null ;
 	String platformName = null ;
 	String arcadeName = null ;
 	boolean saveAnimation = false;
 	String [] arcadeURLarray = imageClassPath.split("/"); 
    	
    	logger.log(Level.INFO, "arcade handler received " + imageClassPath);
        
        /* for (int i=0; i < arcadeURLarray.length; i++) { 
            System.out.println("Str["+i+"]:"+arcadeURLarray[i]); 
        } 
        System.out.println(arcadeURLarray.length); //should be 5
        */
        
        if (arcadeURLarray.length == 5) {
        	
        	    streamOrWrite = arcadeURLarray[2];
        	    platformName = arcadeURLarray[3];
        	    arcadeName = arcadeURLarray[4];
			
             if (streamOrWrite.toLowerCase().equals("write")) { saveAnimation = true; }
			 
        	     
             /*logger.log(Level.INFO, streamOrWrite + " Mode");
             logger.log(Level.INFO, "Platform: " + " platformName");
             logger.log(Level.INFO, "Game: " + " arcadeName");
             */
             
             System.out.println(streamOrWrite.toUpperCase() + " MODE");
             System.out.println("Platform: " + platformName);
             System.out.println("Game: " +  arcadeName);
             
             //now let's check if we have a PNG or GIF and handle accordingly 
             String extension = "";

             int i = arcadeName.lastIndexOf('.');
                if (i > 0) {
                    extension = arcadeName.substring(i+1);
                }
                
             System.out.println("Marquee Type: " +  extension.toLowerCase()); 
             
             if (extension.toLowerCase().equals("gif")) {
                 
                  Pixel pixel = application.getPixel();
                  pixel.stopExistingTimer();

                  pixel.writeArcadeAnimation(platformName, arcadeName, saveAnimation);

                    // we should wait a bit before going back to interactive mode, or it jitters
                  long fifteenSeconds = Duration.ofSeconds(15).toMillis();
                  Sleeper.sleepo(fifteenSeconds);

                  pixel.interactiveMode();
                 
             }
             else if (extension.toLowerCase().equals("png")) {
                 
                   URL url = null; 
                   /*System.out.println("loading new classpath URL for still: " + imageClassPath);
                    URL url = getClass().getClassLoader().getResource(imageClassPath);
                    System.out.println("URL for " + modeName + " loaded");*/

                    BufferedImage image;
                    if(url == null)
                    {
                        // image is not in the JAR/classpath
                       // String path = application.getPixel().getPixelHome() + imageClassPath;
                         String path = application.getPixel().getPixelHome() + "arcade/" + platformName + "/" + arcadeName;
                         System.out.println(path);
                       //  gifFilePath = pixelHome + "arcade/" + selectedPlatformName + "/" + selectedFileName; //user home/pixel/arcade/mame/digdug.gif
        //gifSourcePath = "animations/gifsource/";
                        
                        File file = new File(path);
                        
                     //   if (file.exists(path)) {
                            
                      //  }
                        
                        url = file.toURI().toURL();
                    }

                    image = ImageIO.read(url);

                    System.out.println("buffered image created for: " + url.toString());

                   Pixel pixel = application.getPixel();
                    pixel.stopExistingTimer();
                    pixel.writeImagetoMatrix(image, pixel.KIND.width, pixel.KIND.height);
                 
             }
             else {
                 System.out.println("Invalid extension, only gif or png is allowed");
             }
                     
            
        }
        else {
             System.out.println("*** ERROR **** URL format incorect, use http://localhost:8080/arcade/<stream or write>/<platform name>/<game name .gif or .png>");
             System.out.println("Example: http://localhost:8080/arcade/write/mame/pacman.png or http://localhost:8080/arcade/stream/atari2600/digdug.gif");
        }
        
        
		/*
		 * // the writeAnimation() method just takes the name of the file int i =
		 * imageClassPath.lastIndexOf("/") + 1; String arcadeName =
		 * imageClassPath.substring(i); String platformName =
		 * imageClassPath.substring(i-1); String streamOrWrite =
		 * imageClassPath.substring(i-2);
		 * 
		 * System.out.println("url: " + imageClassPath);
		 * 
		 * System.out.println("stream or write: " + streamOrWrite + "platform: " +
		 * platformName + "arcade name:" + arcadeName);
		 * 
		 * boolean saveAnimation = false;
		 * 
		 * if( imageClassPath.contains("/save/") ) { saveAnimation = true; }
		 * 
		 * if( arcadeName.equals("arcade") ) { arcadeName = defaultImageClassPath; }
		 */
        
      
    }
}

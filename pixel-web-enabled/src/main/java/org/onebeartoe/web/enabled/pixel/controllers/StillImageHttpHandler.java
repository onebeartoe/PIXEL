
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class StillImageHttpHandler extends TextHttpHandler
{

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
// jfdsalkjds        
        String basePath = "images/";
        String defaultImageClassPath = basePath + "Robot.png";
        
        String imageClassPath;
        
        try
        {
            URI requestURI = exchange.getRequestURI();
            String path = requestURI.getPath();
            int i = path.lastIndexOf("/") + 1;
            String name = path.substring(i);
            
            if(name.equals("still"))
            {
                // this is just a request change to still image mode
                imageClassPath = defaultImageClassPath;
            }
            else
            {
                imageClassPath = basePath + name;
            }
        }
        catch(Exception e)
        {
            imageClassPath = defaultImageClassPath;
            
            String message = "An error occured while determining the image from the request.  " +
                             "The default is used now.";
            
            logger.log(Level.SEVERE, message, e);
        }
        
        System.out.println("loading new classpath URL for still: " + imageClassPath);
        URL url = getClass().getClassLoader().getResource(imageClassPath);
        System.out.println("URL for still loaded");
        
        BufferedImage originalImage;
        try
        {
            System.out.println("loading still image");
            originalImage = ImageIO.read(url);
            System.out.println("still image loaded");
            
            Pixel pixel = app.getPixel();
            try
            {
                System.out.println("writing image to the Pixel");
                
                pixel.stopExistingTimer();
                pixel.writeImagetoMatrix(originalImage, pixel.KIND.width, pixel.KIND.height);
                
                System.out.println("image wrote to the Pixel");
            } 
            catch (ConnectionLostException ex)
            {
                String message = "connection lost";
                logger.log(Level.SEVERE, message, ex);
            }
        }
        catch (IOException ex)
        {
            String message = "connection lost";
            logger.log(Level.SEVERE, message, ex);
        }
        finally
        {
            return "request received for " + imageClassPath;
        }
    }
    
}

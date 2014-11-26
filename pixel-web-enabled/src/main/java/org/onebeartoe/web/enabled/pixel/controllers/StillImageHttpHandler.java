
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class StillImageHttpHandler extends PixelHttpHandler
{

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        String imageClassPath = "stills/Robot.png";
        System.out.println("loading new URL for still");
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
            return "all about that bases";
        }
    }
    
}

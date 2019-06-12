
package org.onebeartoe.web.enabled.pixel.controllers;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class StillImageHttpHandler extends ImageResourceHttpHandler
{
    public StillImageHttpHandler(WebEnabledPixel application)
    {
        super(application);
        
        basePath = "images/";
        defaultImageClassPath = basePath + "Robot.png";
        modeName = "still";
    }
    
    @Override
    protected void writeImageResource(String imageClassPath) throws IOException, ConnectionLostException
    {
        System.out.println("loading new classpath URL for still: " + imageClassPath);
        URL url = getClass().getClassLoader().getResource(imageClassPath);
        System.out.println("URL for " + modeName + " loaded");

        BufferedImage image;
        if(url == null)
        {
            // image is not in the JAR/classpath
            String path = application.getPixel().getPixelHome() + imageClassPath;
            File file = new File(path);
            url = file.toURI().toURL();
        }
        
        image = ImageIO.read(url);
        
        System.out.println("buffered image created for: " + url.toString());
        
        Pixel pixel = application.getPixel();
        pixel.stopExistingTimer();
        pixel.writeImagetoMatrix(image, pixel.KIND.width, pixel.KIND.height);
    }        
}

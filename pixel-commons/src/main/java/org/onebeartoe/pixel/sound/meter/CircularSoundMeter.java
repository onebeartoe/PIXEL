/*
 */
package org.onebeartoe.pixel.sound.meter;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 *
 * @author Roberto Marquez
 */
public class CircularSoundMeter extends ButtonUpSoundMeter
{

    public CircularSoundMeter(int width, int height)
    {
        super(width, height);
    }
    
    @Override
    public void displaySoundData(Pixel pixel, List<SoundReading> microphoneValues) 
    {	    
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color textColor = null;

        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(textColor);

        int x = width / 2;
        int y = height / 2;
        for(SoundReading f : microphoneValues)
        {
            g2d.setColor(f.color);                            
            g2d.drawOval(x, y, f.height, f.height);                        
        }

        g2d.dispose();

        if (pixel != null)
        {
            try 
            {
                int w = pixel.KIND.width;
                int h = pixel.KIND.height;
                pixel.writeImagetoMatrix(img, w, h); //TO DO need to find out how to reference PixelApp class from here
            } 
            catch (ConnectionLostException ex) 
            {
                String name = getClass().getName();
                Logger.getLogger(name).log(Level.SEVERE, null, ex);
            }                
        }
    }    
}

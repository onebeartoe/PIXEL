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
public class AllOffSoundMeter implements SoundMeter
{
    protected int width;
    
    protected int height;
    
    protected final int COLUMN_WIDTH = 2;
    
    public AllOffSoundMeter(int width, int height)
    {
        this.width = width;
        this.height = height;                
    }
    
    @Override
    public void displaySoundData(Pixel pixel, List<SoundReading> microphoneValues)
    {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color textColor = null;

        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(textColor);

        drawSoundData(g2d, microphoneValues);

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
    
    /**
     * Override this method to use a basic drawing template when it is called by #displaySoundData()
     */
    public void drawSoundData(Graphics2D g2d, List<SoundReading> microphoneValues)
    {
        // do nothing, to simulate the Pixel being off.
    }
    
}

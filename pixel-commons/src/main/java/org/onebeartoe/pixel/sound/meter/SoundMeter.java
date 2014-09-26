
package org.onebeartoe.pixel.sound.meter;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Roberto Marquez
 */
public class SoundMeter
{
    private int width;
    
    private int height;
    
    private final int COLUMN_WIDTH;
    
    public SoundMeter(int width, int height, int columnWidth)
    {
        this.width = width;
        this.height = height;
        
        this.COLUMN_WIDTH = columnWidth;
    }

/*    
    public void displaySoundData(Pixel pixel, List<SoundReading> microphoneValues) 
    {	    
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        Color textColor = null;

        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(textColor);

        int x = 0;
        for(SoundReading f : microphoneValues)
        {
            g2d.setColor(f.color);
                            
            int y = height - f.height;
                    
            g2d.fillRect(x, y, COLUMN_WIDTH, f.height);
//            g2d.fillRect(x, 0, COLUMN_WIDTH, f.height);
            
            x += COLUMN_WIDTH;
        }

        g2d.dispose();

        if (pixel != null)
        {
            try 
            {  
                pixel.writeImagetoMatrix(img, KIND.width,KIND.height); //TO DO need to find out how to reference PixelApp class from here
            } 
            catch (ConnectionLostException ex) 
            {
                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
            }                
        }
    } 
*/    
    
}

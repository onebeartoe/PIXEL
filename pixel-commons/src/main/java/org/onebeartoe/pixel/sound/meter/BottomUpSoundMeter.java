
package org.onebeartoe.pixel.sound.meter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import java.util.Random;

/**
 * @author Roberto Marquez
 */
public class BottomUpSoundMeter extends AllOffSoundMeter
{   
    public BottomUpSoundMeter(int width, int height)
    {
        super(width, height);
    }

    @Override
    public void drawSoundData(Graphics2D g2d, List<SoundReading> microphoneValues)
    {	    
        int x = 0;
        for(SoundReading f : microphoneValues)
        {
            g2d.setColor(f.color);
                            
            int y = height - f.height;
                    
            g2d.fillRect(x, y, COLUMN_WIDTH, f.height);
            
            x += COLUMN_WIDTH;
        }
    }

    public static Color randomcolor()
    {
        Random random = new Random();
        
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        int alpha = random.nextInt(256);
        Color c = new Color(r, g, b, alpha);
        
        return c;
    }
}

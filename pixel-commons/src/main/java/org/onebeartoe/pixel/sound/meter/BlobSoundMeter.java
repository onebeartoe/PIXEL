
package org.onebeartoe.pixel.sound.meter;

import java.awt.Graphics2D;
import java.util.List;

/**
 * @author Roberto Marquez
 */
public class BlobSoundMeter extends BottomUpSoundMeter
{

    public BlobSoundMeter(int width, int height)
    {
        super(width, height);
    }
    
    @Override
    public void drawSoundData(Graphics2D g2d, List<SoundReading> microphoneValues)
    {
        int x = width / 3;
        int y = height / 3;
        for(SoundReading f : microphoneValues)
        {
            g2d.setColor(f.color);                            
            g2d.drawOval(x, y, f.height, f.height);                        
        }
    }    
}

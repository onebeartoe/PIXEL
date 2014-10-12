
package org.onebeartoe.pixel.sound.meter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto Marquez
 */
public class RectangularSoundMeter extends ButtonUpSoundMeter
{

    public RectangularSoundMeter(int width, int height)
    {
        super(width, height);
    }
    
    @Override
    public void drawSoundData(Graphics2D g2d, List<SoundReading> microphoneValues)
    {
        // we don't care about the order of the buffer values.  Will represent each
        // value with a rectangle on the screen.
        Set<Integer> readings = new HashSet();
        
        for(SoundReading f : microphoneValues)
        {
            readings.add(f.height);
        }
        
        Integer[] array = readings.toArray( new Integer[0] );        
        List<Integer> list = Arrays.asList(array);        
        Collections.sort(list);
        Collections.reverse(list);  // draw the bigger rectangle first
        
        // two seems to be the width/height of a single pixel on the Pixel
        g2d.setStroke( new BasicStroke(2) );
        
        for(Integer i : list)
        {
            // iterate over every unique value in the buffer
            
            Color c = ButtonUpSoundMeter.randomcolor();
            g2d.setColor(c);
            
            float heightRatio = (float) i / height;
            float widthRatio = (float) i / width;
            
            float fw = width * widthRatio;
            int w = (int) fw;
            
            float fh = height * heightRatio;
            int h = (int) fh;
            
            float fx = (width / 2f) - (w / 2f);
            int x = (int) fx;
            
            float fy = (height / 2f) - (h / 2f);
            int y = (int) fy;
         
            System.out.println("x: " + x + " | y: " + y + " | width ratio:" + widthRatio + " | height ratio: " + heightRatio + " | ");
            
            g2d.drawRect(x, y, w, h);
        }       
    }
    
}

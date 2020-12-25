
package org.onebeartoe.pixel.sound.meter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Roberto Marquez
 */
public class CircleSoundMeter extends BottomUpSoundMeter
{

    public CircleSoundMeter(int width, int height)
    {
        super(width, height);
    }
    
    @Override
    public void drawSoundData(Graphics2D g2d, List<SoundReading> microphoneValues)
    {
        List<Integer> readings = new ArrayList();
        
        for(SoundReading f : microphoneValues)
        {
//            System.out.print(f.height + " - ");
            readings.add(f.height);
        }
//System.out.println("");        
        
        Collections.sort(readings);
        int i = readings.size() - 1;
        Integer max = readings.get(i);
        
            
        g2d.setColor(Color.GREEN);                            
        
        int x = 12; //14//width / 4;
        int y = 6;  // 8//height / 4;
        
        int startAngle = 0;
        float ratio = (float) max / height;
        float angle = -360 * ratio;
        int arcAngle = (int) angle;
        System.out.println("x: " + x + " | y: " + y + " | max: " + max + " | ratio:" + ratio + " | angle: " + angle + " | arcAngle: " + arcAngle);

        int arcWidth = 28;
        int arcHeight = 28;
        
        g2d.fillArc(x, y, arcWidth, arcHeight, startAngle, arcAngle);    
    }
    
}

/*
 */
package org.onebeartoe.pixel.sound.meter;

import java.util.List;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 *
 * @author Roberto Marquez
 */
public class AllOffSoundMeter extends ButtonUpSoundMeter
{

    public AllOffSoundMeter(int width, int height)
    {
        super(width, height);
    }
    
    @Override
    public void displaySoundData(Pixel pixel, List<SoundReading> microphoneValues)
    {
        // do nothing, to simulate the Pixel being off.
    }
    
}

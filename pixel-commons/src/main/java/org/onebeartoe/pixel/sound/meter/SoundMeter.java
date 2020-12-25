
package org.onebeartoe.pixel.sound.meter;

import java.util.List;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 *
 * @author Roberto Marquez
 */
public interface SoundMeter
{
    void displaySoundData(Pixel pixel, List<SoundReading> microphoneValues);

//    void setRedrawDelay();
    
//    void setReadingsDelay();
}

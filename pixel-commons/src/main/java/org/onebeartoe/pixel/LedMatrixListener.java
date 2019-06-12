
package org.onebeartoe.pixel;

import ioio.lib.api.RgbLedMatrix;
import org.onebeartoe.pixel.hardware.Pixel;

//TODO: WE MAY NEED TO REFACTOR THE CLASS NAME TO PixelListener
/**
 * @author Roberto Marquez
 */
public interface LedMatrixListener
{
    Pixel getPixel();
    
    void ledMatrixReady(RgbLedMatrix matrix);
}

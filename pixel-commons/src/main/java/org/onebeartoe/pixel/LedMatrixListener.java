
package org.onebeartoe.pixel;

import ioio.lib.api.RgbLedMatrix;

/**
 * @author Roberto Marquez
 */
public class LedMatrixListener 
{
    RgbLedMatrix matrix;
    
    public void ledMatrixReady(RgbLedMatrix matrix)
    {
        this.matrix = matrix;
        
        
    }
    
    
}

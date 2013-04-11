
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.plugins.PixelPlugin;
import ioio.lib.api.RgbLedMatrix;
import javax.swing.JPanel;

/**
 * @author Administrator
 */
public abstract class PixelPanel extends JPanel implements PixelPlugin 
{
    protected RgbLedMatrix matrix_;
    
    protected RgbLedMatrix.Matrix KIND;    
    
    public PixelPanel(RgbLedMatrix.Matrix KIND)
    {
        this.KIND = KIND;
    }
}

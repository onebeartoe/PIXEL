
package com.ledpixelart.pc;

import ioio.lib.api.RgbLedMatrix;
import java.awt.event.ActionEvent;

/**
 * @author rmarquez
 */
public class AnimationsPanel extends ImageTilePanel
{
    
    public AnimationsPanel(RgbLedMatrix matrix, RgbLedMatrix.Matrix KIND)
    {
	super(matrix, KIND);
	imageListPath = "/animations.text";
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
	String command = e.getActionCommand();
	System.out.println("animation comamand: " + command);
    }

    @Override
    protected String imagePath() 
    {
	return "/animations";
    }
    
}

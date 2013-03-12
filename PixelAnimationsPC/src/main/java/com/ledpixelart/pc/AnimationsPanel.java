
package com.ledpixelart.pc;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * @author rmarquez
 */
public class AnimationsPanel extends ImageTilePanel
{
    
    public AnimationsPanel()
    {
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


package com.ledpixelart.pc;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author rmarquez
 */
public abstract class PixelTilePanel extends JPanel implements ActionListener
{
    protected List<JButton> buttons;
    
    public PixelTilePanel()
    {
	GridLayout experimentLayout = new GridLayout(0, 5);
	experimentLayout.setHgap(5);	
	experimentLayout.setVgap(5);
	
	setLayout(experimentLayout);
	
	buttons = new ArrayList();
    }
    
    /**
     * This method needs calling to place the icons on the panel.
     */
    public void populate()
    {
	List<String> filenames;
	try 
	{
	    filenames = imageNames();
	    for(String file : filenames)
	    {
		ImageIcon icon = getImageIcon(file);
		JButton button = new JButton(file, icon);
		add(button);
		buttons.add(button);
	    }
	} 
	catch (Exception ex) 
	{
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	}
	
    }
    
    protected abstract ImageIcon getImageIcon(String path);
    
    protected abstract List<String> imageNames() throws Exception;
    
    protected abstract String imagePath() ;
	    
}

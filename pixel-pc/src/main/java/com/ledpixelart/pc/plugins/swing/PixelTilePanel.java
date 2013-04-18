
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.plugins.swing.ZeroThreadedPixelPanel;
import ioio.lib.api.RgbLedMatrix;
import java.awt.BorderLayout;
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
public abstract class PixelTilePanel extends ZeroThreadedPixelPanel implements ActionListener
{    
    protected JPanel buttonsPanel;
    
    protected List<JButton> buttons;    
     
    public PixelTilePanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
        
	GridLayout experimentLayout = new GridLayout(0, 5);
	experimentLayout.setHgap(5);	
	experimentLayout.setVgap(5);
	buttonsPanel = new JPanel();
	buttonsPanel.setLayout(experimentLayout);
	
	setLayout(new BorderLayout() );
	add(buttonsPanel, BorderLayout.CENTER);
	
	buttons = new ArrayList();					
    }    
    
    /**
     * This method needs calling to place the icons on the panel.
     */
    public void populate()
    {
	try 
	{
	    List<String> filenames = imageNames();
	    for(String file : filenames)
	    {
		ImageIcon icon = getImageIcon(file);
		JButton button = new JButton(icon);
		button.setActionCommand(file);
//		int i = file.lastIndexOf(".");
//		String command = file.substring(0, i);
//		button.setActionCommand(command);
		button.addActionListener(this);
		buttonsPanel.add(button);
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
    
    protected abstract String imagePath();    
	    
}

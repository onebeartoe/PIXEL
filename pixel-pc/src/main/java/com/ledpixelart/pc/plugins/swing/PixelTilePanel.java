
package com.ledpixelart.pc.plugins.swing;

import com.ledpixelart.pc.plugins.swing.ZeroThreadedPixelPanel;
import ioio.lib.api.RgbLedMatrix;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author rmarquez
 */
//public abstract class PixelTilePanel extends ZeroThreadedPixelPanel implements ActionListener
public abstract class PixelTilePanel extends ZeroThreadedPixelPanel implements MouseListener
{    
    protected JPanel buttonsPanel;
    
    protected List<JButton> buttons; 
    
  //  public static String imagePathArray[];
    
    public static ArrayList<String> imagePathArray = new ArrayList<String>();
    
    public int position[];
     
    public PixelTilePanel(RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
        
	GridLayout experimentLayout = new GridLayout(0, 5);
	experimentLayout.setHgap(5);	
	experimentLayout.setVgap(5);
	buttonsPanel = new JPanel();
	buttonsPanel.setLayout(experimentLayout);
	
	JScrollPane scrollPane = new JScrollPane(buttonsPanel);
	
	setLayout(new BorderLayout() );
	add(scrollPane, BorderLayout.CENTER);
	
	buttons = new ArrayList();					
    }    
    
    /**
     * This method needs calling to place the icons on the panel.
     */
    public void populate()
    {
	
    	int i = 0;
    	
    	try 
	{
	    List<String> filenames = imageNames();
	    for(String file : filenames)
	    {
		ImageIcon icon = getImageIcon(file);
		JButton button = new JButton(icon);
		button.setActionCommand(file);
		
		
		//imagePathArray[i] = file;  //here we have all the image paths in an array, we just need to find out which index of the button was clicked now to get it back
//		int i = file.lastIndexOf(".");
//		String command = file.substring(0, i);
//		button.setActionCommand(command);
		//button.addActionListener(this);
		
		//position[i].addMouseListener(this);
		//position[i].addMouseListener(new IndexedMouseListener(i));
		
		
		
		imagePathArray.add(file);
		
		i++;
		button.addMouseListener(this);
		buttonsPanel.add(button);
		buttons.add(button);
	
	    }
	} 
	catch (Exception ex) 
	{
	    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	}
    }    
    
    private class IndexedMouseListener extends MouseAdapter {
        private final int index;

        public IndexedMouseListener(int index) {
            this.index = index;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            System.out.println("Mouse entered for rating " + index);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            System.out.println("Mouse exited for rating " + index);
        }
    }
	
    protected abstract ImageIcon getImageIcon(String path);
    
    protected abstract List<String> imageNames() throws Exception;
    
    protected abstract String imagePath();    
	    
}


package org.onebeartoe.pixel.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import org.apache.commons.lang3.ArrayUtils;



//import com.ledpixelart.pc.PixelApp;

/**
 * @author rmarquez
 */
public class ScrollingTextPanel extends SingleThreadedPixelPanel
{
    private static final long serialVersionUID = 1L;
    
    private JTextField textField;
    
    private JComboBox<String> fontFamilyChooser;
    
    protected JPanel textPanel;
    
    final JPanel colorPanel;
    
    private JSlider scrollSpeedSlider;
    
    private JColorChooser colorChooser;
    
    private HashMap<String, Font> fonts;
    
    private int x;
    
    private String message;
    
    private FontMetrics fm;
    
    private Font font;
    
    private int fontSize = 32;
    
    private String fontFamily;
    
    private int delay;
    
    private int resetX;
    
    private int messageWidth;
    
    private String pixelHardwareID;
    
    private static Preferences prefs;
    
    private static String prefFontString;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
    
    public ScrollingTextPanel(final RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
        
        if (pixel !=null) pixel.interactiveMode(); //put into interactive mode as could have been stuck in local mode from previous panel
        
       // System.out.println("PIXEL Model is: " + KIND.width); 
        
        fonts = new HashMap();
        
        x = 0;
	
        colorChooser = new JColorChooser();
        
        textField = new JTextField("Type Something Here");
        JPanel inputSubPanel = new JPanel( new BorderLayout() );        
        inputSubPanel.add(textField, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add(inputSubPanel, BorderLayout.NORTH); 
        
        String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        
        prefs = Preferences.userRoot().node(pixelPrefNode); //let's get our preferences
    	prefFontString = prefs.get("prefFont", "Arial"); //use Arial if there is no pref saved yet
        int selectedFontIndex = ArrayUtils.indexOf(fontNames, prefFontString); //let's set the default font to Arial, otherwise it would have just picked the first one
        
        JPanel fontPanel = new JPanel( new BorderLayout() );
        fontFamilyChooser = new JComboBox(fontNames);
        fontFamilyChooser.setSelectedIndex(selectedFontIndex); //set the default font to arial, add some error checking if no arial match
        
        fontFamilyChooser.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	String selectedFontName = (String) fontFamilyChooser.getSelectedItem();
    	    	prefs.put("prefFont", selectedFontName);
    	    	System.out.println("Selected Front Name is: " + selectedFontName);
            }
        });
        
        fontPanel.add(fontFamilyChooser, BorderLayout.CENTER);
        JButton writeButton = new JButton("Write");
        
    	writeButton.addActionListener( new ActionListener() 
    	{
    	    public void actionPerformed(ActionEvent e) 
    	    {
    	    	
    	    	if (pixel != null) pixelHardwareID = pixel.getHardwareVersion();
    	    	
	    	    	
    	    		stopExistingTimer();
    	    	
    	    		Float delayFPS = 1000.f / delay; 	
    	    	
    	    		pixel.interactiveMode();
			        pixel.writeMode(delayFPS); 
			        System.out.println("FPS for write mode for scrolling text is: " + delayFPS);
			        
			        x = 0;
			        
			        System.out.println("x: " + x);
			        System.out.println("resetX is: " + resetX);
    	    	
	    	    	while (x > resetX) {
	    	    	
	    	    		System.out.println("x: " + x);
	    	    		int w = KIND.width * 2;
		    	    	int h = KIND.height * 2;
		    	    
		                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		                
		                Color textColor = colorPanel.getBackground();
		    	    
		                Graphics2D g2d = img.createGraphics();
		                g2d.setPaint(textColor);
		                
		                String fontFamily = fontFamilyChooser.getSelectedItem().toString();
		                font = fonts.get(fontFamily);
		                
		                if(font == null)
		                {
		                   font = new Font(fontFamily, Font.PLAIN, fontSize * KIND.width/32);
		                    fonts.put(fontFamily, font);
		                }            
		                
		                g2d.setFont(font);
		                
		                message = getText();
		                
		                fm = g2d.getFontMetrics();
		                
		                int y = fm.getHeight();            
		
		                try 
		                {
		                    additionalBackgroundDrawing(g2d);
		                } 
		                catch (Exception ex) 
		                {
		                    Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
		                }
		                
		                g2d.drawString(message, x, y);
		                
		                try 
		                {
		                    additionalForegroundDrawing(g2d);
		                } 
		                catch (Exception ex) 
		                {
		                    Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
		                }
		                
		                g2d.dispose();
		
		                if (pixel != null)
		                {
		                    try 
		                    {  
		                        pixel.writeImagetoMatrix(img, KIND.width,KIND.height); //TO DO need to find out how to reference PixelApp class from here
		                    } 
		                    catch (ConnectionLostException ex) 
		                    {
		                        Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
		                    }                
		                }
		                            
		                messageWidth = fm.stringWidth(message);            
		                resetX = 0 - messageWidth;
		                x--;
	    	    } 
	    	    	
	    	    pixel.playLocalMode();
	    	    
                //if(x == resetX)
              /*  if(x < resetX)  //had to change to < because of the key frame change, could skip over the ==

                {
                    //x = w;
                	x = KIND.width * 2;
                }
                else
                {
                    x--;
                    //x = x - scrollingKeyFrames;  add this later to skip frames to speed up as a preference
                }
     */
    	    	
    	    	
    			
	/*   			 if (pixelHardwareID != null && pixelHardwareID.substring(0,4).equals("PIXL")) { //only add the write button if its a PIXEL V2 unit
	   			
		    	    	stopExistingTimer();
		    	    	
		    	    	x = 0;
		    	    	
		    	    	while (x > resetX) {
		    	    	
		    	    	message = getText();
		    	    	
		    	    	messageWidth = fm.stringWidth(message);            
		                resetX = 0 - messageWidth;
		    	    	
		    	    	// int delay = scrollSpeedSlider.getValue();	//may not need this here since we already have it elsewhere
						//    delay = 14 - delay;                            // al linke: added this so the higher slider value means faster scrolling
						    
						 //   ScrollingTextPanel.this.timer.setDelay(delay);
						    
					            //int w = 64;
					            //int h = 64;
						    
						    	int w = KIND.width * 2;
						    	int h = KIND.height * 2;
						    	
						    	 BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		 			            
		 			            Color textColor = colorPanel.getBackground();
		 				    
		 			            Graphics2D g2d = img.createGraphics();
		 			            g2d.setPaint(textColor);
		 			            
		 			           // String fontFamily = fontFamilyChooser.getSelectedItem().toString();
		 			            
		 			           // font = fonts.get(fontFamily);
		 			            
		 			            
		 			           //don't need the fonts here because we already set it before
		 			            
		 			            if(font == null)
		 			            {
		 			               // font = new Font(fontFamily, Font.PLAIN, fontSize);
		 			                //font = new Font(fontFamily, Font.PLAIN, fontSize * KIND.width/32); //64 / 32 = 2 means twice as large font for the twice as high display
		 			                font = new Font ("Arial",Font.PLAIN, fontSize * KIND.width/32);
		 			                fonts.put(fontFamily, font);
		 			            }            
		 			            
		 			            g2d.setFont(font);
		 			            System.out.println("x before while loop is: " + x);       
		  			            System.out.println("resetX before while loop is: " + resetX);  
		 			            pixel.interactiveMode();
		 			            pixel.writeMode(delay); //need to tell PIXEL the frames per second to use, how fast to play the animations
		 			            System.out.println("Now writing to PIXEL's SD card, the screen will go blank until writing has been completed..."); 
		   				  
		   				
		 			           
		    				
		    			            message = getText();
		    			            
		    			            fm = g2d.getFontMetrics();
		    			            
		    			            int y = fm.getHeight();            
		
		    			            try 
		    			            {
		    			                additionalBackgroundDrawing(g2d);
		    			            } 
		    			            catch (Exception ex) 
		    			            {
		    			                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
		    			            }
		    			            
		    			            g2d.drawString(message, x, y);
		    			            
		    			            try 
		    			            {
		    			                additionalForegroundDrawing(g2d);
		    			            } 
		    			            catch (Exception ex) 
		    			            {
		    			                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
		    			            }
		    			            
		    			            g2d.dispose();
		    				
				    				try 
				      	            {	
				      	               
				    					pixel.writeImagetoMatrix(img, KIND.width,KIND.height); 
				      	                //x = x - scrollingKeyFrames_ ;  //controls the smoothness / number of keyframes
				    					x--;
				    					System.out.println("writing frame" + " " + x);
				      	                
				      	            } 
				      	            catch (ConnectionLostException ex) 
				      	            {
		      	               
				      	            }
		    	    	}  //the while loop is done
		 			           
		    	    	pixel.playLocalMode(); //now tell PIXEL to play locally
    		}  
		
	   	else {
	   		System.out.println("Sorry, this is not a PIXEL V2 frame and cannot write...");
		 }	*/
					
    	    }
    	});	
	
	colorPanel = new JPanel();
	colorPanel.setBackground(Color.GREEN);
	JButton colorButton = new JButton("change color");
	colorButton.addActionListener( new ActionListener() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
			Color color = colorChooser.showDialog(ScrollingTextPanel.this, "Select the text color.", Color.yellow);
			colorPanel.setBackground(color);
		    }
			});	
			JPanel colorComponents = new JPanel( new BorderLayout() );
			colorComponents.add(colorPanel, BorderLayout.CENTER);
			colorComponents.add(colorButton, BorderLayout.EAST);
			
		        
		    JPanel configurationPanel = new JPanel( new GridLayout(4, 1));
		    configurationPanel.add(inputSubPanel);
		    configurationPanel.add(fontPanel);
			configurationPanel.add(colorComponents);
			
			textPanel = new JPanel( new BorderLayout());
			textPanel.add(configurationPanel, BorderLayout.NORTH);
			textPanel.setBorder( BorderFactory.createTitledBorder("Text") );	
			
			//scrollSpeedSlider = new JSlider(200, 709);
			scrollSpeedSlider = new JSlider(1, 10);
			JPanel speedPanel = new JPanel();
			speedPanel.add(scrollSpeedSlider);
			speedPanel.setBorder( BorderFactory.createTitledBorder("Scroll Speed") );
			speedPanel.add(writeButton);
		        
		    JPanel propertiesPanel = new JPanel( new GridLayout(2,1, 10,10) );
		    propertiesPanel.add(textPanel);
			propertiesPanel.add(speedPanel);
		        
		    setLayout(new BorderLayout());
			add(propertiesPanel, BorderLayout.SOUTH);
    }
    
    private void stopExistingTimer()
    {
        if(timer != null && timer.isRunning() )
        {
            System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }        
    }
    
    /**
     * Override this to perform any additional background drawing on the image that get sent to the PIXEL
     * @param g2d 
     */
    protected void additionalBackgroundDrawing(Graphics2D g2d) throws Exception
    {
        
    }    
    
    /**
     * Override this to perform any additional foreground drawing on the image that get sent to the PIXEL
     * @param g2d 
     */
    protected void additionalForegroundDrawing(Graphics2D g2d) throws Exception
    {
        
    }

    @Override
    public ActionListener getActionListener() 
    {
        ActionListener listener = new TextScroller();
        
        return listener;
    }
    
    public String getText()
    {
	return textField.getText();
    }
    
    private class TextScroller implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
	    delay = scrollSpeedSlider.getValue();	
	    delay = 14 - delay;                            // 20 is the max slider, so 21 - 20 = 1 ms delay
	    
	    ScrollingTextPanel.this.timer.setDelay(delay);
	    
            //int w = 64;
            //int h = 64;
	    
	    	int w = KIND.width * 2;
	    	int h = KIND.height * 2;
	    
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
            Color textColor = colorPanel.getBackground();
	    
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(textColor);
            
            fontFamily = fontFamilyChooser.getSelectedItem().toString();
            font = fonts.get(fontFamily);
            
            if(font == null)
            {
               font = new Font(fontFamily, Font.PLAIN, fontSize * KIND.width/32);
               // font = new Font ("Arial",Font.PLAIN, fontSize * KIND.width/32);
                fonts.put(fontFamily, font);
            }            
            
            g2d.setFont(font);
            
            String message = getText();
            
            fm = g2d.getFontMetrics();
            
            int y = fm.getHeight();            

            try 
            {
                additionalBackgroundDrawing(g2d);
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            g2d.drawString(message, x, y);
            
            try 
            {
                additionalForegroundDrawing(g2d);
            } 
            catch (Exception ex) 
            {
                Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            g2d.dispose();

            if (pixel != null)
            {
                try 
                {  
                    pixel.writeImagetoMatrix(img, KIND.width,KIND.height); //TO DO need to find out how to reference PixelApp class from here
                } 
                catch (ConnectionLostException ex) 
                {
                    Logger.getLogger(ScrollingTextPanel.class.getName()).log(Level.SEVERE, null, ex);
                }                
            }
                        
            messageWidth = fm.stringWidth(message);            
            resetX = 0 - messageWidth;
            
            //if(x == resetX)
            if(x < resetX)  //had to change to < because of the key frame change, could skip over the ==

            {
                //x = w;
            	x = KIND.width * 2;
            }
            else
            {
                x--;
                //x = x - scrollingKeyFrames;  add this later to skip frames to speed up as a preference
            }
        }        
    }
    
}

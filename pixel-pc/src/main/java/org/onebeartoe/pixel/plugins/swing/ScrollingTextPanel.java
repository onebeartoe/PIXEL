
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
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.ArrayUtils;

import com.ledpixelart.pc.PixelApp;



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
    
    private JSlider fontSizeSlider;
    
    private JSlider textVerticalSlider;
    
    private JColorChooser colorChooser;
    
    private JCheckBox doubleSpeed;
    
    private JCheckBox tripleSpeed;
    
    private HashMap<String, Font> fonts;
    
    private int x;
    
    private String message;
    
    private FontMetrics fm;
    
    private Font font;
    
    private int fontSizeBase = 32;
    
    private int yOffset = 0;
    
    private String fontFamily;
    
    private int delay;
    
    private int resetX;
    
    private int y;
    
    private int messageWidth;
    
    private String pixelHardwareID;
    
    private static Preferences prefs;
    
    private static String prefFontString;
    
    private int prefFontSizeSliderPosition;
    
    private int prefFontYOffset;
    
    private Color prefColor;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
    
    private static String prefSavedText;
    
    private static Integer prefRadioSpeedButton;
    
    private boolean writeMode = false;
    
    private int scrollingKeyFrames = 1;
    
    public ScrollingTextPanel(final RgbLedMatrix.Matrix KIND)
    {
        super(KIND);
        
        prefs = Preferences.userRoot().node(pixelPrefNode); //let's get our preferences
        
        fonts = new HashMap();
        
        x = 0;
	
        colorChooser = new JColorChooser();
        
        prefRadioSpeedButton = prefs.getInt("prefRadioSpeedButton", 1);  //preferences for the speed accelerator radio buttons
        
        prefSavedText = prefs.get("prefSavedText", "Type Something Here");
        textField = new JTextField(prefSavedText);
        
        textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				CheckAndStartTimer();
				prefs.put("prefSavedText", getText());
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				CheckAndStartTimer();
				// TODO Auto-generated method stub
				prefs.put("prefSavedText", getText());
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
            // implement the methods
        });
        
        
        
        JPanel inputSubPanel = new JPanel( new BorderLayout() );        
        inputSubPanel.add(textField, BorderLayout.CENTER);
        JPanel inputPanel = new JPanel( new BorderLayout() );
        inputPanel.add(inputSubPanel, BorderLayout.NORTH); 
        
        String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
       
    	
        prefFontString = prefs.get("prefFont", "Arial"); //use Arial if there is no pref saved yet
        int selectedFontIndex = ArrayUtils.indexOf(fontNames, prefFontString); //let's set the default font to Arial, otherwise it would have just picked the first one
        
        
        prefFontSizeSliderPosition = prefs.getInt("prefFontSize", 0); //pref for the font size, default to 32 if not there
        
        yOffset = prefs.getInt("prefFontYOffset", 0); //pref for the y offset
        
        JPanel fontPanel = new JPanel( new BorderLayout() );
        fontFamilyChooser = new JComboBox(fontNames);
        fontFamilyChooser.setSelectedIndex(selectedFontIndex); //set the default font to arial, add some error checking if no arial match
        
        fontFamilyChooser.addActionListener (new ActionListener () {
            public void actionPerformed(ActionEvent e) {
            	CheckAndStartTimer();
            	String selectedFontName = (String) fontFamilyChooser.getSelectedItem();
    	    	prefs.put("prefFont", selectedFontName);
    	    	System.out.println("Selected Front Name is: " + selectedFontName);
    	    	prefs.put("prefSavedText", getText());
            }
        });
        
        fontPanel.add(fontFamilyChooser, BorderLayout.CENTER);
        JButton writeButton = new JButton("Write");
        
    	writeButton.addActionListener( new ActionListener() 
    	{
    	    public void actionPerformed(ActionEvent e) 
    	    {
    	    	
    	    	if (pixel != null) pixelHardwareID = pixel.getHardwareVersion();
	    	    	
    	    		stopExistingTimer(); //this doesn't seem to do anything?
    	    		
    	    		writeMode = true;
    	    		prefs.put("prefSavedText", getText());
    	    	
    	    		Float delayFPS = (float) Math.round(1000 / delay); 	
    	    		pixel.interactiveMode();
			        pixel.writeMode(delayFPS); 
			        System.out.println("FPS for write mode for scrolling text is: " + delayFPS);
			        
			       // x = 0;
			        x = KIND.width * 2;
			        
			        System.out.println("x: " + x);
			        System.out.println("resetX is: " + resetX);
			        
			        new writeScrollingText().execute(); //we'll do this background
			        
    	    	
	    	    	/*while (x >= resetX) {
	    	    	
	    	    		System.out.println("Writing frame: " + Math.abs(x) + " of " + Math.abs(resetX));
	    	    		
	    	    		int w = KIND.width * 2;
		    	    	int h = KIND.height * 2;
		    	    
		                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		                
		                Color textColor = colorPanel.getBackground();
		    	    
		                Graphics2D g2d = img.createGraphics();
		                g2d.setPaint(textColor);
		                
		                String fontFamily = fontFamilyChooser.getSelectedItem().toString();
		                font = fonts.get(fontFamily);
		                
		               // if(font == null)
		              //  {
		               // font = new Font(fontFamily, Font.PLAIN, fontSize);  
		                   font = new Font(fontFamily, Font.PLAIN, (fontSizeBase * KIND.width/32) + fontSizeSlider.getValue());
		                   fonts.put(fontFamily, font);
		              //  }            
		                
		                g2d.setFont(font);
		                
		                message = getText();
		                
		                fm = g2d.getFontMetrics();
		                
		                y = fm.getHeight() + yOffset;          
		
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
	    	    } */
	    	    	
	    	    //pixel.playLocalMode();
	    	    writeMode = false;
    	    }
    	});	
	
	colorPanel = new JPanel();
	
	Color colorPrefs = getColor(prefs, "prefTextColor", Color.GREEN);
	//colorPanel.setBackground(Color.GREEN); //this is the default color, let's instead get it from prefs
	colorPanel.setBackground(colorPrefs);
	
	
	JButton colorButton = new JButton("change color");
	colorButton.addActionListener( new ActionListener() 
	{
	    public void actionPerformed(ActionEvent e) 
	    {
	    	//check if the timer is not running and if not, start it and then put back into interactive mode
	    	 CheckAndStartTimer();
	    	
	    	Color color = colorChooser.showDialog(ScrollingTextPanel.this, "Select the text color.", Color.yellow);
			
			//now let's store into preferences
	    	putColor(prefs, "prefTextColor", color);
	    	prefs.put("prefSavedText", getText());
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
			
			fontSizeSlider = new JSlider(-36, 36);
			JPanel fontSizePanel = new JPanel();
			fontSizePanel.add(fontSizeSlider);
			fontSizePanel.setBorder( BorderFactory.createTitledBorder("Text Size") );
			fontSizeSlider.setValue(prefFontSizeSliderPosition); //from preferences
			fontSizeSlider.addChangeListener(new fontSizeChanged());
			
			
			textVerticalSlider = new JSlider(-36, 36);
			JPanel textVerticalPanel = new JPanel();
			textVerticalPanel.add(textVerticalSlider);
			textVerticalPanel.setBorder( BorderFactory.createTitledBorder("Text Vertical Position") );
			textVerticalSlider.setValue(yOffset); //from preferences
			textVerticalSlider.addChangeListener(new textVerticalPositionChanged());
			
			//scrollSpeedSlider = new JSlider(200, 709);
			scrollSpeedSlider = new JSlider(1, 10);
			JPanel speedPanel = new JPanel();
			speedPanel.add(scrollSpeedSlider);
			speedPanel.setBorder( BorderFactory.createTitledBorder("Scroll Speed"));
			
			JRadioButton singleSpeedRadio = new JRadioButton("1X");
			singleSpeedRadio.setMnemonic(KeyEvent.VK_B);
			singleSpeedRadio.addActionListener(new ActionListener(){
		        public void actionPerformed(ActionEvent e) {
		        	CheckAndStartTimer();
		        	scrollingKeyFrames = 1;
		        	prefs.putInt("prefRadioSpeedButton", 1);
		        	prefs.put("prefSavedText", getText());
		        }
		    });
			
			
			JRadioButton doubleSpeedRadio = new JRadioButton("2X");
			doubleSpeedRadio.setMnemonic(KeyEvent.VK_B);
			doubleSpeedRadio.addActionListener(new ActionListener(){
		        public void actionPerformed(ActionEvent e) {
		        	CheckAndStartTimer();
		        	scrollingKeyFrames = 2;
		        	prefs.putInt("prefRadioSpeedButton", 2);
		        	prefs.put("prefSavedText", getText());
		        }
		    });

			JRadioButton threeSpeedRadio = new JRadioButton("3X");
			threeSpeedRadio.setMnemonic(KeyEvent.VK_B);
			threeSpeedRadio.addActionListener(new ActionListener(){
		        public void actionPerformed(ActionEvent e) {
		        	CheckAndStartTimer();
		        	scrollingKeyFrames = 3;
		        	prefs.putInt("prefRadioSpeedButton", 3);
		        	prefs.put("prefSavedText", getText());
		        }
		    });

			JRadioButton fourSpeedRadio = new JRadioButton("4X");
			fourSpeedRadio.setMnemonic(KeyEvent.VK_B);
			fourSpeedRadio.addActionListener(new ActionListener(){
		        public void actionPerformed(ActionEvent e) {
		        	CheckAndStartTimer();
		        	scrollingKeyFrames = 4;
		        	prefs.putInt("prefRadioSpeedButton", 4);
		        	prefs.put("prefSavedText", getText());
		        }
		    });

		    //Group the radio buttons.
		    ButtonGroup RadioButtonGroup = new ButtonGroup();
		    RadioButtonGroup.add(singleSpeedRadio);
		    RadioButtonGroup.add(doubleSpeedRadio);
		    RadioButtonGroup.add(threeSpeedRadio);
		    RadioButtonGroup.add(fourSpeedRadio);
		    
		    switch (prefRadioSpeedButton) {
            case 1:  singleSpeedRadio.setSelected(true);
                     break;
            case 2:  doubleSpeedRadio.setSelected(true);
                     break;
            case 3:  threeSpeedRadio.setSelected(true);
                     break;
            case 4:  fourSpeedRadio.setSelected(true);
                     break;
            default: singleSpeedRadio.setSelected(true);
                     break;
		    }
			
		    speedPanel.add(singleSpeedRadio);
			speedPanel.add(doubleSpeedRadio);
			speedPanel.add(threeSpeedRadio);
			speedPanel.add(fourSpeedRadio);
			speedPanel.add(writeButton);
		        
		    JPanel propertiesPanel = new JPanel( new GridLayout(4,1, 10,10) );
		    propertiesPanel.add(textPanel);
		    propertiesPanel.add(fontSizePanel);
		    propertiesPanel.add(textVerticalPanel);
			propertiesPanel.add(speedPanel);
		        
		    setLayout(new BorderLayout());
			add(propertiesPanel, BorderLayout.SOUTH);
    }
    
    class writeScrollingText extends SwingWorker<Boolean, Integer> {
    		   @Override
    		   protected Boolean doInBackground() throws Exception {
    			   
    			   while (x >= resetX) {
   	    	    	
	    	    		System.out.println("Writing frame: " + Math.abs(x) + " of " + Math.abs(resetX));
	    	    		
	    	    		int w = KIND.width * 2;
		    	    	int h = KIND.height * 2;
		    	    
		                BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		                
		                Color textColor = colorPanel.getBackground();
		    	    
		                Graphics2D g2d = img.createGraphics();
		                g2d.setPaint(textColor);
		                
		                String fontFamily = fontFamilyChooser.getSelectedItem().toString();
		                font = fonts.get(fontFamily);
		                font = new Font(fontFamily, Font.PLAIN, (fontSizeBase * KIND.width/32) + fontSizeSlider.getValue());
		                fonts.put(fontFamily, font);
		                
		                g2d.setFont(font);
		                
		                message = getText();
		                
		                fm = g2d.getFontMetrics();
		                
		                y = fm.getHeight() + yOffset;          
		
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
		                x = x - scrollingKeyFrames;
		                publish(Math.abs(x)); //publish the progress of how many frames written to window
	    	    } 
    			   
    		
    		    return true;
    		   }

    		   // Can safely update the GUI from this method.
    		   protected void done() {
    		    
    		    boolean status;
    		    try {
    		     // Retrieve the return value of doInBackground.
    		     status = get();
    		     //we are done so we can now set PIXEL to local playback mode
    		 	 pixel.playLocalMode(); //now tell PIXEL to play locally
    			 System.out.println("PIXEL FOUND: Click to stream or double click to write");
    			 String message = "PIXEL FOUND: Click to stream or double click to write";
    		     PixelApp.statusLabel.setText(message);  
    		    } catch (InterruptedException e) {
    		     // This is thrown if the thread's interrupted.
    		    } catch (ExecutionException e) {
    		     // This is thrown if we throw an exception
    		     // from doInBackground.
    		    }
    		   }

    		   @Override
    		   // Can safely update the GUI from this method.
    		   protected void process(List<Integer> chunks) {
    		    // Here we receive the values that we publish().
    		    // They may come grouped in chunks.
    		    int mostRecentValue = chunks.get(chunks.size()-1);
    		    //System.out.println("DO NOT INTERRUPT: Writing frame " + Integer.toString(mostRecentValue) + " of " + Math.abs(resetX));
    		    String message = "DO NOT INTERRUPT: Writing frame " + Integer.toString(mostRecentValue) + " of " + Math.abs(resetX);
    	        PixelApp.statusLabel.setText(message);  
    		   }
    		  };
    
    public static String getColorString(Color c) {
        int i = c.getRGB() & 0xFFFFFF;
        String s = Integer.toHexString(i).toUpperCase();
        while (s.length() < 6) {
            s = "0" + s;
        }
        return s;
    }
    
    public static void putColor(Preferences node, String key, Color c) {
        node.put(key, "0x" + getColorString(c));
    }
    
    public static Color getColor(Preferences node, String key, Color default_color) {
        Color result = default_color;
        String value = node.get(key, "unknown");
        if (!value.equals("unknown")) {
            try {
                result = Color.decode(value);
            } catch (Exception e) {
                System.out.println("Couldn't decode color preference for '" + key + "' from '" + value + "'");
            }
        }
        return result;
    }
    
    private class fontSizeChanged implements ChangeListener{
    	  public void stateChanged(ChangeEvent ce){
    	  //fontSize = (fontSizeBase * KIND.width/32) + fontSizeSlider.getValue();
    	  CheckAndStartTimer();
		  
    	  prefs.putInt("prefFontSize", fontSizeSlider.getValue());
    	  prefs.put("prefSavedText", getText());
    	  }
    }
    
    private class textVerticalPositionChanged implements ChangeListener{
	  	  public void stateChanged(ChangeEvent ce){
	  		
	  	  CheckAndStartTimer();
	  	  
	  	  yOffset = textVerticalSlider.getValue();   
	  	  prefs.putInt("prefFontYOffset", textVerticalSlider.getValue());
	  	  prefs.put("prefSavedText", getText()); //dont' really want to put the text save in the timer loop as it's too frequent so sticking in these areas
	  	  }
  }
    
    
    private void CheckAndStartTimer() {
    	if (pixel != null && !timer.isRunning()) {
	  		  pixel.interactiveMode(); //put into interactive mode as could have been stuck in local mode after a write
	  		  timer.start();
	  	  }
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
	    //as a hack, add if in write mode flag?
        	
        delay = scrollSpeedSlider.getValue();	
	    delay = 14 - delay;                            // 20 is the max slider, so 21 - 20 = 1 ms delay
	    
	    ScrollingTextPanel.this.timer.setDelay(delay);
	    
	    	int w = KIND.width * 2;
	    	int h = KIND.height * 2;
	    
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
            Color textColor = colorPanel.getBackground();
	    
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(textColor);
            
            //get prefs for fontSize, change it from the font size slider
            
            fontFamily = fontFamilyChooser.getSelectedItem().toString();
            font = fonts.get(fontFamily);
            font = new Font(fontFamily, Font.PLAIN, (fontSizeBase * KIND.width/32) + fontSizeSlider.getValue());
            //font = new Font(fontFamily, Font.PLAIN, fontSize);
            fonts.put(fontFamily, font);
            
            g2d.setFont(font);
            
            String message = getText();
            
            fm = g2d.getFontMetrics();
            
            //y = fm.getHeight() + textVerticalSlider.getValue();     
            y = fm.getHeight() + yOffset;            

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
                //x--;
                x = x - scrollingKeyFrames;  
            }
         } 
    }



	@Override
	public void setPixelFound(boolean found) {
		// TODO Auto-generated method stub
		
	}
    
}

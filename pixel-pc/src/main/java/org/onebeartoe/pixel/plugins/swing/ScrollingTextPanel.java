
package org.onebeartoe.pixel.plugins.swing;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang3.ArrayUtils;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

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
    
    public static JCheckBox twitterTextCheckBox;
    
    private static JCheckBox filterTweetsCheckBox;
    
    private JTextField twitterSearchTerm;
    
    private JLabel twitterSearchLabel;
    
    private JLabel twitterTimerDelayLabel;
    
    private JLabel twitterSecondsLabel;
    
    public static JComboBox twitterSearchInterval;
    
    public static Integer twitterSearchDelayValue;
    
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
    
    private static int INDEX_NOT_FOUND = -1;
    
    private int prefFontSizeSliderPosition;
    
    private int prefFontYOffset;
    
    private Color prefColor;

    private static String pixelPrefNode = "/com/ledpixelart/pc";
    
    private static String prefSavedText;
    
    private static Integer prefSpeedScrollPosition_;
    
    private static Integer prefRadioSpeedButton;
    
    private boolean writeMode = false;
    
    private int scrollingKeyFrames = 3;
    
    private int selectedFontIndex;
    
    public static ActionListener TwitterTimer;
    
    public volatile static Timer twitterTimer;
    
    private Twitter twitter;
    
    private TwitterFactory tf;
    
    private Query query;
    
    private QueryResult result = null;
    
    private Status status;
    
    private String lastTweet;
    
    private Integer tweetCount = 0;
	
    
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
        
    
     
   	 
        
        TwitterTimer = new ActionListener() 
	  	{
	  	    public void actionPerformed(ActionEvent evt) 
	  	    {
	  	    	
		  	  	//twitter = TwitterFactory.getSingleton();
		        query = new Query(twitterSearchTerm.getText());
		        
				try {
					result = twitter.search(query);
					System.out.println("Number of matched tweets: " + result.getCount());
				} catch (TwitterException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (Status status : result.getTweets()) {
					
					if (filterTweetsCheckBox.isSelected()) { // then we don't want @ mentions or http:// tweets
						if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							System.out.println(status.getText());
							setText(status.getText()); 
						}
					}
					
					else {
						if (!status.getText().contains("RT")) {
							
							//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
							System.out.println(status.getText());
							setText(status.getText()); //it's the last one so let's display it
						}
					}
					
		        }
	  	    }
	  	};
	  	
        
	    ConfigurationBuilder cb = new ConfigurationBuilder();
	   	 cb.setDebugEnabled(true)
	   	   .setOAuthConsumerKey("Ax6lCfg9Yf2Niab22e9SsY75b")
	   	   .setOAuthConsumerSecret("3isp024VgehfZ60HwbEcBt1ZZzPyoXseaWYmO4NXxoxefKY65A")
	   	   .setOAuthAccessToken("") // we don't need these right now as we are just calling public twitter searches
	   	   .setOAuthAccessTokenSecret("");
	   	 tf = new TwitterFactory(cb.build());
	   	 twitter = tf.getInstance();
	   	 
	  	
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
        selectedFontIndex = ArrayUtils.indexOf(fontNames, prefFontString); //let's set the default font to Arial, otherwise it would have just picked the first one
        if (selectedFontIndex == INDEX_NOT_FOUND) {  //returns -1 if not found
        	selectedFontIndex = 1; //then we'll just pick the first font in the list
        }
        
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
	    	    	
    	    		stopExistingTimer(); 
    	    		stopTwitterTimer(); //we need to stop the twitter timer if it's running and then restart after the write
    	    		
    	    		//writeMode = true;
    	    		prefs.put("prefSavedText", getText());
    	    	
    	    		//Float delayFPS = (float) Math.round(1000 / delay); 	
    	    		pixel.interactiveMode();
			        //pixel.writeMode(delayFPS); 
			        pixel.writeMode(10); //10 fps or 100ms 
			       // System.out.println("FPS for write mode for scrolling text is: " + delayFPS);
			        
			       // x = 0;
			        x = KIND.width * 2;
			        scrollingKeyFrames = scrollSpeedSlider.getValue();
			        
			        System.out.println("x: " + x);
			        System.out.println("resetX is: " + resetX);
			        
			        Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
			        setCursor(hourglassCursor);
			        
			        PixelApp.frame.setEnabled(false); // we don't want the user clicking somewhere else during the write
			        
			        new writeScrollingText().execute(); //we'll do this background
    	    
			        //writeMode = false;
    	    }
    	});	
	
	colorPanel = new JPanel();
	
	Color colorPrefs = getColor(prefs, "prefTextColor", Color.GREEN);
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
        
    JPanel configurationPanel = new JPanel( new GridLayout(5, 1));
    configurationPanel.add(inputSubPanel);
    configurationPanel.add(fontPanel);
	configurationPanel.add(colorComponents);
	
	textPanel = new JPanel( new BorderLayout());
	textPanel.add(configurationPanel, BorderLayout.NORTH);
	textPanel.setBorder( BorderFactory.createTitledBorder("Text") );	
	
	twitterTextCheckBox = new JCheckBox("Enable Twitter Feed");
	twitterSearchTerm = new JTextField(prefs.get("prefSavedTwitterSearchTerm", "Enter Search Term"));
	twitterSearchLabel = new JLabel("Twitter Search Term:");
	
	String twitterIntervals[] = { "10s", "30s", "1 min", "2 min", "5 min",
			"10 min", "30 min","1 hour","2 hours",
			"4 hours","8 hours", "16 hours", "24 hours"};

	twitterSearchInterval = new JComboBox(twitterIntervals);
	twitterSearchInterval.setSelectedIndex(prefs.getInt("preftwitterSearchDelayValue", 2));
	twitterTimerComboUpdate();
	
	twitterSearchInterval.addActionListener (new ActionListener () {
	    public void actionPerformed(ActionEvent e) {
	    	
	    	twitterTimerComboUpdate();
	    	prefs.putInt("preftwitterSearchDelayValue", twitterSearchInterval.getSelectedIndex());
	    	
	    	//the interveral changed so we need to stop and restart the twitter timer
	    	 stopTwitterTimer();
	    	 System.out.println("Starting Twitter search timer to go off every " + twitterSearchInterval.getSelectedItem().toString());
			 twitterTimer = new Timer(twitterSearchDelayValue, TwitterTimer);
			 twitterTimer.start();
	    
	    }
	});
	
	filterTweetsCheckBox = new JCheckBox("Filter Tweets containing http:// or @");
	filterTweetsCheckBox.setSelected(prefs.getBoolean("prefFilterTweets",true));
	filterTweetsCheckBox.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	
        	if (filterTweetsCheckBox.isSelected()) {
        		prefs.putBoolean("prefFilterTweets",true);
        	}
        	else {
        		prefs.putBoolean("prefFilterTweets",false); 
        	}
        	
        }
    });
	
	twitterTimerDelayLabel = new JLabel("Twitter Refresh Interval:");
	
	//JPanel twitterPanel = new JPanel();
	JPanel twitterPanel = new JPanel(new GridBagLayout());
	GridBagConstraints c = new GridBagConstraints();
	
	c.fill = GridBagConstraints.HORIZONTAL;
	c.weightx = 0.5;

    c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 0;
	c.gridy = 0;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(twitterTextCheckBox, c);
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 1;
	c.gridy = 0;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(filterTweetsCheckBox,c);
	
	///******* new row
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 0;
	c.gridy = 1;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(twitterSearchLabel, c);
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 1;
	c.gridy = 1;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(twitterSearchTerm, c);
	
	//****** next row
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 0;
	c.gridy = 2;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(twitterTimerDelayLabel,c);
	
	c.gridwidth = 1; //for the first row, each component should take up 1 column
	c.gridx = 1;
	c.gridy = 2;
	c.insets = new Insets(0,0,0,0);  //left and right padding
	twitterPanel.add(twitterSearchInterval,c);
	
	
	
	twitterPanel.setBorder( BorderFactory.createTitledBorder("Twitter") );
	
	twitterTextCheckBox.setSelected(prefs.getBoolean("prefTwitterTextCheckBox",false));
	
	if (twitterTextCheckBox.isSelected()) {
		
  		//first time run on startup, then we switch to the timer
		//twitter = TwitterFactory.getSingleton();
        query = new Query(twitterSearchTerm.getText());
        int z = 0;
        
		try {
			result = twitter.search(query);
			tweetCount = result.getCount();
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		for (Status status : result.getTweets()) {
			
			if (filterTweetsCheckBox.isSelected()) { // then we don't want @ mentions or http:// tweets
				if (!status.getText().contains("RT") && !status.getText().contains("http://") && !status.getText().contains("@")) {   //retweets have "RT" in them, we don't want retweets in this case
					
					//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
					System.out.println(status.getText());
					setText(status.getText()); //it's the last one so let's display it
				}
			}
			
			else {
				if (!status.getText().contains("RT")) {
					
					//System.out.println("@" + status.getUser().getScreenName() + ":" + status.getText());
					System.out.println(status.getText());
					setText(status.getText()); //it's the last one so let's display it
				}
			}
			
        }

		 stopTwitterTimer();
		 System.out.println("Starting Twitter search timer to go off every " + twitterSearchInterval.getSelectedItem().toString());
		 twitterTimer = new Timer(twitterSearchDelayValue, TwitterTimer);
		 twitterTimer.start();

		 //TO DO test this if Internet is down
	} 
	
	twitterTextCheckBox.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
        	
        	if (twitterTextCheckBox.isSelected()) {
        		prefs.putBoolean("prefTwitterTextCheckBox",true);
        		stopTwitterTimer();
        		System.out.println("Starting Twitter search timer to go off every " + twitterSearchInterval.getSelectedItem().toString());
				twitterTimer = new Timer(twitterSearchDelayValue, TwitterTimer);
				twitterTimer.start();
        	}
        	else {
        		prefs.putBoolean("prefTwitterTextCheckBox",false); 
        		stopTwitterTimer();
        	}
        	
        }
    });
	
	twitterSearchTerm.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				//CheckAndStartTimer();
				prefs.put("prefSavedTwitterSearchTerm", twitterSearchTerm.getText()); 
				
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				//CheckAndStartTimer();
				// TODO Auto-generated method stub
				prefs.put("prefSavedTwitterSearchTerm", twitterSearchTerm.getText()); 
				
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				
			}
            // implement the methods
        });
	

	
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
	
	  
	prefSpeedScrollPosition_ = prefs.getInt("prefSpeedScrollPosition", 3);  
	//scrollSpeedSlider = new JSlider(200, 709);
	scrollSpeedSlider = new JSlider(1, 10);
	JPanel speedPanel = new JPanel();
	speedPanel.add(scrollSpeedSlider);
	speedPanel.setBorder( BorderFactory.createTitledBorder("Scroll Speed"));
	scrollSpeedSlider.setValue(prefSpeedScrollPosition_);
	
	scrollSpeedSlider.addChangeListener(new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent ce) {
            //System.out.println(((JSlider) ce.getSource()).getValue());
        	CheckAndStartTimer();
        	prefs.putInt("prefSpeedScrollPosition",scrollSpeedSlider.getValue());
        	//TO DO save prefs here
        }
    });
	
	speedPanel.add(writeButton);
        
    JPanel propertiesPanel = new JPanel( new GridLayout(0,1, 0,0) );
    propertiesPanel.add(textPanel);
    propertiesPanel.add(twitterPanel);
    propertiesPanel.add(fontSizePanel);
    propertiesPanel.add(textVerticalPanel);
	propertiesPanel.add(speedPanel);
        
    setLayout(new BorderLayout());
	add(propertiesPanel, BorderLayout.CENTER);
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
    		     
    		     Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    		     setCursor(normalCursor);
    		     
    		     PixelApp.frame.setEnabled(true);
    		     
    		    } catch (InterruptedException e) {
    		     // This is thrown if the thread's interrupted.
    		    } catch (ExecutionException e) {
    		     // This is thrown if we throw an exception
    		     // from doInBackground.
    		    }
    		    
    		    //lastly let's make restart the twitter timer is the check box was enabled
    		    
    		    if (twitterTextCheckBox.isSelected()) {
    				 stopTwitterTimer();
    				 System.out.println("Starting Twitter search timer to go off every " + twitterSearchInterval.getSelectedItem().toString());
    				 twitterTimer = new Timer(twitterSearchDelayValue, TwitterTimer);
    				 twitterTimer.start();
    				 //TO DO test this if Internet is down
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
    
    
    private void twitterTimerComboUpdate() {
    	switch (twitterSearchInterval.getSelectedIndex()) { 
	     case 0:
	    	 twitterSearchDelayValue = 10000;
	    	 break;
	     case 1:
	    	 twitterSearchDelayValue = 30000;
	    	 break;
	     case 2:
	    	 twitterSearchDelayValue = 60000;
	    	 break;
	     case 3:
	    	 twitterSearchDelayValue = 120000;
	    	 break;
	     case 4:
	    	 twitterSearchDelayValue = 300000;
	    	 break;
	     case 5:
	    	 twitterSearchDelayValue = 600000;
	    	 break;	 
	     case 6:
	    	 twitterSearchDelayValue = 1800000;
	    	 break;	 	 
	     case 7:
	    	 twitterSearchDelayValue = 3600000;
	    	 break;	 	 
	     case 8:
	    	 twitterSearchDelayValue = 7200000;
	    	 break;	 	 
	     case 9:
	    	 twitterSearchDelayValue = 14400000;
	    	 break;	 	 
	     case 10:
	    	 twitterSearchDelayValue = 28800000;
	    	 break;	 	
	     case 11:
	    	 twitterSearchDelayValue = 57600000;
	    	 break;	 	 	
	     case 12:
	    	 twitterSearchDelayValue = 86400000;
	    	 break;	 	 	
	     default:	    		 
	    	 twitterSearchDelayValue = 60000;
	    	 prefs.putInt("preftwitterSearchDelayValue", 2);
	     
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
            System.out.println("Stopping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }        
    }
    
    private void stopTwitterTimer()
    {
        if(twitterTimer != null && twitterTimer.isRunning() )
        {
            System.out.println("Stopping Twitter Search Timer...");
            twitterTimer.stop();
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
        	
      //  delay = scrollSpeedSlider.getValue();	
	  //  delay = 14 - delay;  
        	// 20 is the max slider, so 21 - 20 = 1 ms delay
        	
        scrollingKeyFrames = scrollSpeedSlider.getValue();
	    
	    //ScrollingTextPanel.this.timer.setDelay(delay);
	    ScrollingTextPanel.this.timer.setDelay(100); //we need to hard code this to 100 ms for bluetooth
	    
	    	int w = KIND.width * 2;
	    	int h = KIND.height * 2;
	    
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
            Color textColor = colorPanel.getBackground();
	    
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(textColor);
            
            //get prefs for fontSize, change it from the font size slider
            
            fontFamily = fontFamilyChooser.getSelectedItem().toString(); //this line was crashing on linux and raspberry pi
            
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
	
	public void setText(final String message) {
		
		SwingUtilities.invokeLater(new Runnable() {
		      public void run() {
		    	  textField.setText(message);
		      }
		    });
		
		//textField.setText(message);
	}
    
}

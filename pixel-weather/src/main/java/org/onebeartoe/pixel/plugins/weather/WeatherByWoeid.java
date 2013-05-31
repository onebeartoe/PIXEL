
//package com.ledpixelart.pc.plugins.swing;
package org.onebeartoe.pixel.plugins.weather;

import com.ledpixelart.pc.plugins.swing.SingleThreadedPixelPanel;
import ioio.lib.api.RgbLedMatrix.Matrix;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;


/**
 *
 */
public class WeatherByWoeid extends SingleThreadedPixelPanel
{
    private ActionListener worker = new WeatherWorker();
    
    public WeatherByWoeid(Matrix m)
    {
	super(m);
	
	tickDelay = 5000;
	
	worker = new WeatherWorker();
	
	setLayout( new BorderLayout() );
	
	JLabel label = new JLabel("Weather Panel");
	
	JEditorPane webView = new JEditorPane();
	webView.setContentType("text/html");
	String uri = "http://weather.yahooapis.com/forecastrss?w=615702";
	int start = -1;
	int end = -1;
	try 
	{
/*	    
	    URL url = new URL(uri);
	    InputStream instream = url.openStream();
	    InputStreamReader inputStreamReader = new InputStreamReader(instream);
	    BufferedReader in = new BufferedReader(inputStreamReader);
	    StringBuilder feed = new StringBuilder();
	    String inputLine = in.readLine();
	    while (inputLine != null)
	    {
		feed.append(inputLine);
		inputLine = in.readLine();
	    }
	    in.close();	    	    	    
	    
	    System.out.println("Here is the feed: " + feed.toString() );
	    
	    start = feed.indexOf("<![CDATA[");
	    
	    end = feed.indexOf("]]>");
	    String description = feed.substring(start, end);
	    
	    webView.setText(description);
*/
	    InputStream dataIn = new WeatherService().retrieve( uri );
	    
	
	    // Parse Data
	    Weather weather = new WeatherService().parse( dataIn );
	    
	    String description = new WeatherService().format( weather );
	    webView.setText(description);
	} 
	catch (Exception ex) 
	{
	    String message = "start: " + start + "  -   end: " + end;
	    Logger.getLogger(WeatherByWoeid.class.getName()).log(Level.SEVERE, message, ex);
	}
	
	add(label, BorderLayout.NORTH);
	add(webView, BorderLayout.CENTER);
    }
    
    @Override
    public ActionListener getActionListener() 
    {        
        return worker;
    }
    
    @Override
    public ImageIcon getTabIcon()
    {
	System.out.println("\n\n\nusing a custom tab\n");
	String path = "tab-icon.png";
	URL url = getClass().getResource(path);
        ImageIcon imagesTabIcon = new ImageIcon(url);
	
	return imagesTabIcon;
    }
    
    public class WeatherWorker implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	    System.out.println("weather works great");
	}
    }
    
}




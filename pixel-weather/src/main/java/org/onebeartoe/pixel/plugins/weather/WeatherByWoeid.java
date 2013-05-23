
//package com.ledpixelart.pc.plugins.swing;
package org.onebeartoe.pixel.plugins.weather;

import com.ledpixelart.pc.plugins.swing.SingleThreadedPixelPanel;
import ioio.lib.api.RgbLedMatrix.Matrix;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.ImageIcon;
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
	
	worker = new WeatherWorker();
	
	setLayout( new BorderLayout() );
	
	JLabel label = new JLabel("Weather Panel");
	
	add(label);
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




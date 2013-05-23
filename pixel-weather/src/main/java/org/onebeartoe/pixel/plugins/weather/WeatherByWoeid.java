
package com.ledpixelart.pc.plugins.swing;
//package org.onebeartoe.pixel.plugins.weather;

import ioio.lib.api.RgbLedMatrix.Matrix;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	JLabel label = new JLabel("Weather");
	
	add(label);
    }
    
    @Override
    public ActionListener getActionListener() 
    {        
        return worker;
    }
    
    public class WeatherWorker implements ActionListener
    {
	@Override
	public void actionPerformed(ActionEvent e) 
	{
	    System.out.println("weather work");
	}
    }
    
}




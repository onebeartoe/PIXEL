package org.onebeartoe.pixel.plugins.swing;

import com.ledpixelart.pc.plugins.swing.*;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;

/**
 * This was intended for the functionality seen in the Android PIXEL app for
 * interactive images.
 *
 * @author Roberto Marquez
 */
public class VuMeterPanel extends ImageTilePanel
{
    private AnalogInput proximitySensor;

    private Timer timer;

    private ProximityListener proximityListener;

    private volatile List<VuReading> microphoneValues;
    
    private final int VALUES_MAX = 32;
    
    private final int COLUMN_WIDTH = 2;
  
    private final int SENSOR_READ_DELAY = 300;
    
    private int w = KIND.width * 2;
    
    private int h = KIND.height * 2;

Random random = new Random();
    
    public VuMeterPanel(RgbLedMatrix.Matrix KIND) 
    {
        super(KIND);

        w = KIND.width * 2;
        h = KIND.height * 2;
        
        proximityListener = new ProximityListener();
        
        microphoneValues = new ArrayList();
    }
    
    @Override
    public void startPixelActivity() 
    {
        System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");

        proximitySensor = pixel.getAnalogInput1();

        timer = new Timer(SENSOR_READ_DELAY, proximityListener);
        timer.start();
    }

    @Override
    public void stopPixelActivity() 
    {
        if (timer != null && timer.isRunning()) 
        {
            System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
            timer.stop();
        }
    }

    private void writeVuData() 
    {	    
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        Color textColor = null;

        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(textColor);

        int x = 0;
        for(VuReading f : microphoneValues)
        {
            g2d.setColor(f.color);
                            
            int y = h - f.height;
                    
            g2d.fillRect(x, y, COLUMN_WIDTH, f.height);
//            g2d.fillRect(x, 0, COLUMN_WIDTH, f.height);
            
            x += COLUMN_WIDTH;
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
    }

    private class ProximityListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e) 
        {
            if (proximitySensor == null) 
            {
                System.out.println("The proximity sensor is not initialized");
            } 
            else 
            {
                try 
                {
                    float p = proximitySensor.read();
p = random.nextFloat();
                    
//                    System.out.println("proximity sensor: " + p);

                    float r = h * p;
                    int height = (int) r;

                    Color c = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256), random.nextInt(256));
                    
                    VuReading reading = new VuReading();
                    reading.height = height;
                    reading.color = c;
                    
                    microphoneValues.add(reading);
                    
                    if(microphoneValues.size() > VALUES_MAX)
                    {
                        microphoneValues.remove(0);
                    }
                    
                    writeVuData();
                } 
                catch (Exception ex) 
                {
                    Logger.getLogger(VuMeterPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private class VuReading
    {
        public int height;
        
        public Color color;
    }

}

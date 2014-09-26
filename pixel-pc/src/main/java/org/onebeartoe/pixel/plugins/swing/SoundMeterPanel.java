package org.onebeartoe.pixel.plugins.swing;

import com.ledpixelart.pc.plugins.swing.*;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import org.onebeartoe.pixel.sound.meter.SoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundReading;

/**
 * This was intended for the functionality seen in the Android PIXEL app for
 * interactive images.
 *
 * @author Roberto Marquez
 */
public class SoundMeterPanel extends ImageTilePanel
{
    private AnalogInput proximitySensor;

    private Timer timer;

    private ProximityListener proximityListener;

    private volatile List<SoundReading> microphoneValues;
    
    private final int VALUES_MAX = 32;
    
    private final int COLUMN_WIDTH = 2;
  
    private final int SENSOR_READ_DELAY = 300;
    
    private int w = KIND.width * 2;
    
    private int h = KIND.height * 2;

    private Random random = new Random();
    
    private SoundMeter soundMeter;
    
    public SoundMeterPanel(RgbLedMatrix.Matrix KIND) 
    {
        super(KIND);

        w = KIND.width * 2;
        h = KIND.height * 2;
        
        proximityListener = new ProximityListener();
        
        microphoneValues = new ArrayList();
        
        soundMeter = new SoundMeter(w, h, COLUMN_WIDTH);
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

                    float ratio = h * p;
                    int height = (int) ratio;

                    int r = random.nextInt(256);
                    int g = random.nextInt(256);
                    int b = random.nextInt(256);
                    int alpha = random.nextInt(256);
                    Color c = new Color(r, g, b, alpha);
                    
                    SoundReading reading = new SoundReading();
                    reading.height = height;
                    reading.color = c;
                    
                    microphoneValues.add(reading);
                    
                    if(microphoneValues.size() > VALUES_MAX)
                    {
                        microphoneValues.remove(0);
                    }

// PICK UP HERE                    
//                    soundMeter.displaySoundData();
                } 
                catch (Exception ex) 
                {
                    Logger.getLogger(SoundMeterPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}

package org.onebeartoe.pixel.plugins.swing;

import com.ledpixelart.pc.plugins.swing.*;
import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.onebeartoe.pixel.sound.meter.AllOffSoundMeter;
import org.onebeartoe.pixel.sound.meter.BlobSoundMeter;
import org.onebeartoe.pixel.sound.meter.BottomUpSoundMeter;
import org.onebeartoe.pixel.sound.meter.CircleSoundMeter;
import org.onebeartoe.pixel.sound.meter.RectangularSoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundMeter;
import org.onebeartoe.pixel.sound.meter.SoundReading;
import org.onebeartoe.pixel.sound.meter.WaveSoundMeter;

/**
 * This tab uses a microphone sensor to visualize the sound data it reads.
 *
 * @author Roberto Marquez
 */
public class SoundMeterPanel extends ImageTilePanel
{
    private AnalogInput microphoneSensor;

    private Timer sensorReadingsTimer;
    
    private RedrawListener redrawListener;
    
    private Timer redrawTimer;

    private MicrophoneListener microphoneListener;
    
    private volatile List<SoundReading> microphoneValues;
    
    private final int VALUES_MAX = 32;        

// rename to DEFAULT_*    
    private final int SENSOR_READ_DELAY = 300;
    
    private final int DEFAULT_REDRAW_DELAY = 300;
    
    private int w = KIND.width * 2;
    
    private int h = KIND.height * 2;
    
    private Random random;
    
    private SoundMeter soundMeter;
    
    public SoundMeterPanel(RgbLedMatrix.Matrix KIND) 
    {
        super(KIND);

        w = KIND.width * 2;
        h = KIND.height * 2;
        
        random = new Random();
        
        microphoneListener = new MicrophoneListener();                
        
        microphoneValues = new ArrayList();
        
        redrawListener = new RedrawListener();
        
        soundMeter = new BottomUpSoundMeter(w, h);
        
        Vector v = new Vector();
        v.add("Off");
        v.add("Rectangle");
        v.add("Blob");
        v.add("Circle");
        v.add("Bottom Up");
        v.add("Wave Graph");
        JComboBox modesDropdown = new JComboBox(v);
        modesDropdown.addActionListener( new DropdownListener() );
        modesDropdown.setBorder( new TitledBorder("Mode"));
        
        JSlider readingsSlider = new JSlider(1, 1000);
        readingsSlider.addChangeListener( new ReadingsSliderListener() );
        readingsSlider.setBorder( new TitledBorder("Sensor Delay"));
        readingsSlider.setValue(3);
        
        JSlider redrawSlider = new JSlider(1, 1000);
        redrawSlider.addChangeListener( new RedrawSliderListener() );
        redrawSlider.setBorder( new TitledBorder("Redraw Delay"));
        redrawSlider.setPaintLabels(true);
        redrawSlider.setValue(60);
        
//        GridLayout layout = new GridLayout(3, 1);
        BoxLayout layout = new BoxLayout(this, BoxLayout.Y_AXIS);
        setLayout(layout);                
        add(modesDropdown);
        add(readingsSlider);
        add(redrawSlider);
    }
    
    private void resetTimer(Timer timer, ActionListener listener, int value)
    {
        if (timer != null)// && timer.isRunning()) // for some reason the check on isRunning() was returning false
        {
            System.out.println("Resetting timer in " + listener.getClass().getSimpleName() + " to " + value + ".");
            timer.stop();

            timer = new Timer(value, listener);
            timer.start();
        }        
    }
    
    @Override
    public void startPixelActivity() 
    {
        System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");

        microphoneSensor = pixel.getAnalogInput1();

        sensorReadingsTimer = new Timer(SENSOR_READ_DELAY, microphoneListener);
        sensorReadingsTimer.start();
        
        redrawTimer = new Timer(DEFAULT_REDRAW_DELAY, redrawListener);
        redrawTimer.start();
    }

    @Override
    public void stopPixelActivity() 
    {
        System.out.println("Stoping PIXEL activity in " + getClass().getSimpleName() + ".");
        
        if (sensorReadingsTimer != null && sensorReadingsTimer.isRunning()) 
        {
            sensorReadingsTimer.stop();
        }
        
        if(redrawTimer != null && redrawTimer.isRunning() )
        {
            redrawTimer.stop();
        }
    }

    private class DropdownListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            JComboBox source = (JComboBox) e.getSource();
            String selectedItem = (String) source.getSelectedItem();

            switch(selectedItem)
            {
                case "Rectangle":
                {
                    soundMeter = new RectangularSoundMeter(w,h);
                    break;
                }
                case "Blob":
                {
                    soundMeter = new BlobSoundMeter(w, h);
                    break;
                }
                case "Circle":
                {
                    soundMeter = new CircleSoundMeter(w, h);
                    break;
                }
                case "Bottom Up":
                {
                    soundMeter = new BottomUpSoundMeter(w, h);
                    break;
                }
                case "Wave Graph":
                {
                    soundMeter = new WaveSoundMeter(w, h);
                    break;
                }
                default:
                {
                    // "Off"
                    soundMeter = new AllOffSoundMeter(w, h);
                }
            };
            System.out.println("moe dropdown changes");
        }
    }
    
    private class MicrophoneListener implements ActionListener 
    {
        public void actionPerformed(ActionEvent e) 
        {
            if (microphoneSensor == null) 
            {
                System.out.println("The proximity sensor is not initialized");
            } 
            else 
            {
                try 
                {
                    float p = microphoneSensor.read();

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
                } 
                catch (Exception ex) 
                {
                    Logger.getLogger(SoundMeterPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private class RedrawListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            List<SoundReading> values = new ArrayList();
            values.addAll(microphoneValues); 
            soundMeter.displaySoundData(pixel, values);
        }
    }
    
    private class ReadingsSliderListener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e) 
        {
            JSlider slider = (JSlider) e.getSource();
            if( slider.getValueIsAdjusting() )
            {
                System.out.println("The sensor readings slider is adjusting.");
            }
            else
            {
                int value = slider.getValue();
                resetTimer(sensorReadingsTimer, microphoneListener, value);
            }
        }
    }
    
    private class RedrawSliderListener implements ChangeListener
    {
        @Override
        public void stateChanged(ChangeEvent e) 
        {
System.out.println("redraw slider changing");
            JSlider slider = (JSlider) e.getSource();
//            if( slider.getValueIsAdjusting() )
//            {
//                System.out.println("The redraw slider is adjusting.");
//            }
//            else
            {
                int value = slider.getValue();
                resetTimer(redrawTimer, redrawListener, value);
            }
System.out.println("redraw slider CHANGED");            
        }
    }
}

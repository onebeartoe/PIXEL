
package org.onebeartoe.pixel.hardware;

import java.awt.Color;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * The trigonometry is Originally from:
 * http://www.javatpoint.com/Analog-clock-in-applet
 */
public class AnalogClock
{
    private Color background = Color.orange;

    private int width;
    private int height;
    
    Thread t = null;

    private int hours = 0;
    private int minutes = 0;
    private int seconds = 0;
    
    private String timeString = "";

    public AnalogClock(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    private void drawHand(double angle, int radius, Graphics g)
    {
        angle -= 0.5 * Math.PI;
        int x = (int) (radius * Math.cos(angle));
        int y = (int) (radius * Math.sin(angle));
        g.drawLine(width / 2, height / 2, width / 2 + x, height / 2 + y);
    }

    private void drawWedge(double angle, int radius, Graphics g)
    {
        angle -= 0.5 * Math.PI;
        int x = (int) (radius * Math.cos(angle));
        int y = (int) (radius * Math.sin(angle));
        angle += 2 * Math.PI / 3;
        int x2 = (int) (5 * Math.cos(angle));
        int y2 = (int) (5 * Math.sin(angle));
        angle += 2 * Math.PI / 3;
        int x3 = (int) (5 * Math.cos(angle));
        int y3 = (int) (5 * Math.sin(angle));
        g.drawLine(width / 2 + x2, height / 2 + y2, width / 2 + x, height / 2 + y);
        g.drawLine(width / 2 + x3, height / 2 + y3, width / 2 + x, height / 2 + y);
        g.drawLine(width / 2 + x2, height / 2 + y2, width / 2 + x3, height / 2 + y3);
    }

    public void paint(Graphics g)
    {
        Calendar cal = Calendar.getInstance();
        hours = cal.get(Calendar.HOUR_OF_DAY);
        if (hours > 12)
        {
            hours -= 12;
        }
        minutes = cal.get(Calendar.MINUTE);
        seconds = cal.get(Calendar.SECOND);

        SimpleDateFormat formatter
                = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        Date date = cal.getTime();
        timeString = formatter.format(date);
                
        g.setColor(background);
        g.fillRect(0,0, width, height);
        
        // change the color for the clock hands
        g.setColor(Color.gray);
        
        // draw the clock hands
        drawWedge(2 * Math.PI * hours / 12, width / 5, g);
        drawWedge(2 * Math.PI * minutes / 60, width / 3, g);
        drawHand(2 * Math.PI * seconds / 60, width / 2, g);
        
        g.setColor(Color.white);
        g.drawString(timeString, 10, height - 100);
    }
}

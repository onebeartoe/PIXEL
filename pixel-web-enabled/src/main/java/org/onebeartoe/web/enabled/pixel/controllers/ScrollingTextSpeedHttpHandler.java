
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextSpeedHttpHandler extends TextHttpHandler
{

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        int i = path.lastIndexOf("/") + 1;
        String s = path.substring(i);
        
        Long speed = Long.valueOf(s);
        
        if(speed < 100)
        {
            speed = 100L;
        }
        if(600 < speed)
        {
            speed = 600L;
        }
        
        Pixel pixel = app.getPixel();
        pixel.stopExistingTimer();
        pixel.setScrollDelay(speed);
        pixel.scrollText();
        
        return "scrolling text speed update received:" + speed;
    }

}



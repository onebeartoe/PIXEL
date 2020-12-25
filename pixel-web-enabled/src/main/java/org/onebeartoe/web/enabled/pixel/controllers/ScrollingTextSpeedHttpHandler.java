
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.net.URI;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextSpeedHttpHandler extends TextHttpHandler
{
    protected WebEnabledPixel application;
    
    public ScrollingTextSpeedHttpHandler(WebEnabledPixel application)
    {
        this.application = application;
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        LogMe logMe = LogMe.getInstance();
        
        URI requestURI = exchange.getRequestURI();
        String path = requestURI.getPath();
        int i = path.lastIndexOf("/") + 1;
        String s = path.substring(i);
        
        Long speed = Long.valueOf(s);
        
        if(speed == 0) 
        {
            speed = 10L;
        }
        
        Pixel pixel = application.getPixel();
        pixel.setScrollDelay(speed);
        
        if (!CliPixel.getSilentMode()) {
            System.out.println("scrolling text speed update received:" + speed);
            logMe.aLogger.info("scrolling text speed update received:" + speed);
         }
        
        return "scrolling text speed update received:" + speed;
    }

}




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
public class ScrollingTextScrollSmoothHttpHandler extends TextHttpHandler
{
    protected WebEnabledPixel application;
    
    public ScrollingTextScrollSmoothHttpHandler(WebEnabledPixel application)
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
        
        int scrollsmooth = Integer.valueOf(s);
        
        Pixel pixel = application.getPixel();
        pixel.setScrollSmooth(scrollsmooth);
        
        if (!CliPixel.getSilentMode()) {
            System.out.println("scrolling smooth factor received:" + scrollsmooth);
            logMe.aLogger.info("scrolling smooth factor received:" + scrollsmooth);
         }
        
        return "scrolling smooth factor received:" + scrollsmooth;
    }

}



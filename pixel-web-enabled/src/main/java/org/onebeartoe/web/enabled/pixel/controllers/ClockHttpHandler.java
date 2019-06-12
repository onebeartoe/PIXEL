
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.util.logging.Logger;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class ClockHttpHandler extends TextHttpHandler
{
    private Logger logger;
    
    protected WebEnabledPixel app;
    
    public ClockHttpHandler(WebEnabledPixel application)
    {
        String name = getClass().getName();
        logger = Logger.getLogger(name);
        
        this.app = application;
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        Pixel.ClockModes mode = Pixel.ClockModes.ANALOG;
        
        Pixel pixel = app.getPixel();
        pixel.displayClock(mode);
        
        return "changed to clock mode request recieved";
    }
}

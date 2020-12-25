
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpHandler;
import java.util.logging.Logger;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public abstract class PixelHttpHandler implements HttpHandler
{
    protected WebEnabledPixel app;
    
    protected Logger logger;    
    
    public PixelHttpHandler()
    {
        String name = getClass().getName();
        logger = Logger.getLogger(name);
    }

    public WebEnabledPixel getApp()
    {
        return app;
    }

    public void setApp(WebEnabledPixel app)
    {
        this.app = app;
    }
}


package org.onebeartoe.web.enabled.pixel.controllers;

import org.onebeartoe.net.http.TextHttpHandler;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public abstract class PixelHttpHandler extends TextHttpHandler
{
    protected WebEnabledPixel app;

    public WebEnabledPixel getApp()
    {
        return app;
    }

    public void setApp(WebEnabledPixel app)
    {
        this.app = app;
    }
}

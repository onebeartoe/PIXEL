/*
 */
package org.onebeartoe.web.enabled.pixel.controllers;

import java.util.List;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 *
 * @author Roberto Marquez
 */
public class ArcadeListHttpHandler extends ListHttpHandler
{

    public ArcadeListHttpHandler(WebEnabledPixel application)
    {
        super(application);
    }
    @Override
    protected List<String> getList()
    {
        List<String> arcade = application.loadArcadeList();
        
        return arcade;
    }
}

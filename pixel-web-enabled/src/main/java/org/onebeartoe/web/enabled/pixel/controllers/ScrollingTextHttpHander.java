
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;

import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import static org.onebeartoe.web.enabled.pixel.WebEnabledPixel.MATRIX_TYPE;
//import static org.onebeartoe.web.enabled.pixel.WebEnabledPixel.fontNames;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextHttpHander extends PixelHttpHandler
{
    
//TODO: delete?        
    public ScrollingTextHttpHander()
    {
        
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        logger.log(Level.INFO, "hi from scroller");

        URI requestURI = exchange.getRequestURI();

        String encodedQuery = requestURI.getQuery();
        String query;
        try
        {
            query = URLDecoder.decode(encodedQuery, "UTF-8");
            String[] parameters = query.split("&");
            if(parameters != null && parameters.length > 0)
            {
                String command = parameters[0];
                String [] strs = command.split("=");
                String t = strs[0];
                String text = strs[1];

                app.getPixel().setScrollingText(text);
            }
        } 
        catch (UnsupportedEncodingException ex)
        {
            logger.log(Level.SEVERE, "The scrolling text parameters could not be decoded.", ex);
        }

        app.getPixel().scrollText();

        return "scrolling text request received";
    }
}

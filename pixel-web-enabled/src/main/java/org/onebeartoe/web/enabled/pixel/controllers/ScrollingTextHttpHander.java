
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.logging.Level;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextHttpHander extends TextHttpHandler
{
    
//TODO: delete?        
    public ScrollingTextHttpHander()
    {
        
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        URI requestURI = exchange.getRequestURI();
        
        logger.log(Level.INFO, "Scrolling text handler received a request: " + requestURI);

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
                app.getPixel().scrollText();
            }
        } 
        catch (UnsupportedEncodingException ex)
        {
            logger.log(Level.SEVERE, "The scrolling text parameters could not be decoded.", ex);
        }
        finally
        {
            return "scrolling text request received";
        }
    }
}


package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.web.enabled.pixel.CliPixel;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextHttpHander extends TextHttpHandler
{
    //private Logger logger;
    
    protected WebEnabledPixel app;
    
    public ScrollingTextHttpHander(WebEnabledPixel application)
    {
        String name = getClass().getName();
       // logger = Logger.getLogger(name);
        
        this.app = application;
    }

    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        
        LogMe logMe = LogMe.getInstance();
        URI requestURI = exchange.getRequestURI();
        
        
         if (!CliPixel.getSilentMode()) {
             logMe.aLogger.info("Scrolling text handler received a request: " + requestURI);
             System.out.println("Scrolling text handler received a request: " + requestURI);
         }
        
        
        String encodedQuery = requestURI.getQuery();

        String text = null;
        
        if(encodedQuery == null)
        {
            text = "scrolling text";

            //logger.log(Level.INFO, "scrolling default text"); 
             if (!CliPixel.getSilentMode()) {
                logMe.aLogger.info("scrolling default text");
                System.out.println("scrolling default text");
            }
        }
        else
        {
            try
            {
                String query = URLDecoder.decode(encodedQuery, "UTF-8");
                String[] parameters = query.split("&");
                //logger.log(Level.INFO, "parameters: " + parameters);
                //if (!CliPixel.getSilentMode()) {
                //    logMe.aLogger.info("parameters: " + parameters);
                //    System.out.println("parameters: " + parameters);
                //}
                if(parameters != null && parameters.length > 0)
                {
                    String command = parameters[0];
                    String [] strs = command.split("=");
                    String t = strs[0];
                    text = strs[1];

                    //logger.log(Level.INFO, "scrolling custom message:" + text);
                    if (!CliPixel.getSilentMode()) {
                        logMe.aLogger.info("scrolling custom message:" + text);
                        System.out.println("scrolling custom message:" + text);
                    }
                }
                else
                {
                    text = "error processing request";
                }
            } 
            catch (UnsupportedEncodingException ex)
            {
                //logger.log(Level.SEVERE, "The scrolling text parameters could not be decoded.", ex);
                logMe.aLogger.log(Level.SEVERE, "The scrolling text parameters could not be decoded.", ex);
            }
        }
        
        
        app.getPixel().interactiveMode(); //few cases text wasn't working, think it was because this was missing
        
        app.getPixel().setScrollingText(text);
        app.getPixel().scrollText();
        
        return "scrolling text request received";
    }
}

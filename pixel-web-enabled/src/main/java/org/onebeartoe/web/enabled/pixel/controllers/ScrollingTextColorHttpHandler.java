
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.awt.Color;
import java.net.URI;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.CliPixel;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextColorHttpHandler extends TextHttpHandler
{
    protected WebEnabledPixel application;
    
    public ScrollingTextColorHttpHandler(WebEnabledPixel application)
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
        //String hex = path.substring(i);
        String color_ = path.substring(i);
        Color color = Color.red;
        Boolean colorTextMatch = false;
        
        switch (color_) {
            
            case "red":
                color = Color.RED;
                colorTextMatch = true;
                break;
            case "blue":
                color = Color.BLUE;
                colorTextMatch = true;
                break;
             case "cyan":
                color = Color.CYAN;
                colorTextMatch = true;
                break;
             case "gray":
                color = Color.GRAY;
                colorTextMatch = true;
                break;
             case "darkgray":
                color = Color.DARK_GRAY;
                colorTextMatch = true;
                break;
            case "green":
                color = Color.GREEN;
                colorTextMatch = true;
                break;
             case "lightgray":
                color = Color.LIGHT_GRAY;
                colorTextMatch = true;
                break;
            case "magenta":
                color = Color.MAGENTA;
                colorTextMatch = true;
                break;
            case "orange":
                color = Color.ORANGE;
                colorTextMatch = true;
                break;
             case "pink":
                color = Color.PINK;
                colorTextMatch = true;
                break;
            case "yellow":
                color = Color.YELLOW;
                colorTextMatch = true;
                break;
            case "white":
                color = Color.WHITE;
                colorTextMatch = true;
                break;
            default: 
                color = Color.RED;
                colorTextMatch = false;
        }
        
        if (!colorTextMatch) {           //this means we have a hex code vs. color string text
            color = hex2Rgb(color_);
        }
        
        //Color color = hex2Rgb(hex); //roberto had a hex code, changed to color to make it simpler for the user
     
        
// I think in head less environment decode() did not work        
//        Color color = Color.decode(hex);
        
        Pixel pixel = application.getPixel();
        //pixel.stopExistingTimer();  //should not need this here
        pixel.setScrollTextColor(color);
        //pixel.scrollText(0); //setting to 0 as we would not be looping from here
        
        //return "scrolling text color update received:" + hex;
        if (!CliPixel.getSilentMode()) {
             System.out.println("scrolling text color update received:" + color_);
             logMe.aLogger.info("scrolling text color update received:" + color_);
         }
        return "scrolling text color update received:" + color_;
    }
    
    /**
     * 
     * @param colorStr e.g. "FFFFFF"
     * @return 
     */
    public static Color hex2Rgb(String colorStr) 
    {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    }    
}

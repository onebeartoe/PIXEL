
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.awt.Color;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
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
        String colorString = path.substring(i);
        Color color = Color.red; //default
        
        
        if (colorString != null) color = ArcadeHttpHandler.getColorFromHexOrName(colorString);
        
// I think in head less environment decode() did not work        
//        Color color = Color.decode(hex);
        
        Pixel pixel = application.getPixel();
        //pixel.stopExistingTimer();  //should not need this here
        pixel.setScrollTextColor(color);
        //pixel.scrollText(0); //setting to 0 as we would not be looping from here
        
        //return "scrolling text color update received:" + hex;
        if (!CliPixel.getSilentMode()) {
             System.out.println("scrolling text color update received:" + colorString);
             logMe.aLogger.info("scrolling text color update received:" + colorString);
         }
        return "scrolling text color update received:" + colorString;
    }
    
    /**
     * 
     * @param colorStr e.g. "FFFFFF"
     * @return 
     */
    
    /*
    public static Color hex2Rgb(String colorStr) 
    {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    } 
    
     private boolean isHexadecimal(String input) {
        
        final Pattern HEXADECIMAL_PATTERN = compile("\\p{XDigit}+");
        final Matcher matcher = HEXADECIMAL_PATTERN.matcher(input);
        return matcher.matches();
        
    }*/
}

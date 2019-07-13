
package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.awt.Color;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.compile;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.onebeartoe.network.TextHttpHandler;
import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
import org.onebeartoe.pixel.LogMe;
import org.onebeartoe.web.enabled.pixel.CliPixel;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextHttpHander extends TextHttpHandler  //TO DO have TextHttpHandler send a return
        
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
        
        String text_ = null;
        String color_ = null;
        Color color = null;
        String speed_ = null;
        Long speed = null;
        String loop_ = null;
        int loop = 0;
        
        
        LogMe logMe = LogMe.getInstance();
        URI requestURI = exchange.getRequestURI();
        
         if (!CliPixel.getSilentMode()) {
             logMe.aLogger.info("Scrolling text handler received a request: " + requestURI);
             System.out.println("Scrolling text handler received a request: " + requestURI);
         }
        
        String encodedQuery = requestURI.getQuery();
        
        if(encodedQuery == null)
        {
            text_ = "scrolling text";
            
             if (!CliPixel.getSilentMode()) {
                logMe.aLogger.info("scrolling default text");
                System.out.println("scrolling default text");
            }
        }
        else  {
            
            //we'll have something /text?t=hello world&c=red&s=100&l=5
            List<NameValuePair> params = null;
            try {
                    params = URLEncodedUtils.parse(new URI(requestURI.toString()), "UTF-8");
            } catch (URISyntaxException ex) {
                    //Logger.getLogger(ArcadeHttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            }

            for (NameValuePair param : params) {

                switch (param.getName()) {

                    case "t": //scrolling text value
                        text_ = param.getValue();
                        break;
                    case "c": //text color
                        color_ = param.getValue();
                        break;
                    case "s": //scrolling speed
                        speed_ = param.getValue();
                        break;
                    case "l": //loop
                        loop_ = param.getValue();
                        break;
                    case "text": //scrolling text value
                        text_ = param.getValue();
                        break;
                    case "color": //text color
                        color_ = param.getValue();
                        break;
                    case "speed": //scrolling speed
                        speed_ = param.getValue();
                        break;
                    case "loop": //loop
                        loop_ = param.getValue();
                        break;
                    }
            }

            /* TO DO catch this wrong URL format as I made this mistake of ? instead of & after the first one!!!!
            Scrolling text handler received a request: /text/?t=hello%20world?c=red?s=10?l=2
            t : hello world?c=red?s=10?l=2
            */
           
            }

        if (!CliPixel.getSilentMode()) {

            System.out.println("scrolling text: " + text_);
            if (color_ != null) System.out.println("text color: " + color_);
            if (speed_ != null) System.out.println("scrolling speed: " + speed_);
            System.out.println("# times to loop: " + loop_);
            logMe.aLogger.info("scrolling text: " + text_);
            if (color_ != null) logMe.aLogger.info("text color: " + color_);
            if (speed_ != null) logMe.aLogger.info("scrolling speed: " + speed_);
            logMe.aLogger.info("# times to loop: " + loop_);
        }
            if (color_ != null) {  //some color was entered, either red, green, blue, etc. or a hex value with the #
           
                //let's first check if we have a hex string color
                if (isHexadecimal(color_)) {
                     color = hex2Rgb(color_);
                     System.out.println("Hex color value detected");
                }        
                else {   //and if not then color text was entered so let's look for a match
                
                    switch (color_) {

                        case "red":
                            color = Color.RED;
                            break;
                        case "blue":
                            color = Color.BLUE;
                            break;
                        case "cyan":
                            color = Color.CYAN;
                            break;
                        case "gray":
                            color = Color.GRAY;
                            break;
                        case "darkgray":
                            color = Color.DARK_GRAY;
                            break;
                        case "green":
                            color = Color.GREEN;
                            break;
                        case "lightgray":
                            color = Color.LIGHT_GRAY;
                            break;
                        case "magenta":
                            color = Color.MAGENTA;
                            break;
                        case "orange":
                            color = Color.ORANGE;
                            break;
                        case "pink":
                            color = Color.PINK;
                            break;
                        case "yellow":
                            color = Color.YELLOW;
                            break;
                        case "white":
                            color = Color.WHITE;
                            break;
                        default:
                            color = Color.RED;
                    }
                }
            }

            /*
            if (speed_ != null) {

                 speed = Long.valueOf(speed_);

                if(speed == 0) //Roberto originally had if less than 100 here but the scrolling is too slow
                {
                    //speed = 100L;
                    speed = 1L;
                }

                if(600 < speed)
                {
                    speed = 600L;
                }
            }
            */          
        
        //app.getPixel().interactiveMode(); //to do: we shouldn't need this but add back if so
        
        if (loop_ != null) loop = Integer.valueOf(loop_);
              
        if (color_ == null)  color = Color.RED;

        //if (speed_ == null)  speed = 10L;
       
        if (speed_ == null) { //speed was not specified so let's get default per panel type
            
            int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
            int yTextOffset = -4;
            int fontSize_ = 22;
            speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset and font
        }
        
        app.getPixel().scrollText(text_, loop, speed, color);
        
        return "scrolling text request received: " + text_ ;
    }
    
    
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
        
    }
    
}


  
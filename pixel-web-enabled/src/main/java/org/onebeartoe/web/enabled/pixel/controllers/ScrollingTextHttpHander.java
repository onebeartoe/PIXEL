
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
        boolean colorTextMatch = false;
        
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

                /*
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
            if (color_ != null) {

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
            }

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
        
        //app.getPixel().interactiveMode(); //to do: we shouldn't need this but add back if so
        
        if (loop_ != null) loop = Integer.valueOf(loop_);
        
        if (app.getPixel().getLoopStatus() && loop != 0) {  //let's check if we are looping. If yes, we don't want to set the text but instead write to the Q
            
              System.out.println("we are looping and received a new loop command so writing to Q");
              System.out.println("color before: " + color);
              
              if (color_ == null)  color = Color.RED;

              if (speed_ == null)  speed_ = "10";
              
             //if (scrollingTextColor == null) scrollingTextColor = Color.RED;
             // app.getPixel().addtoQueue("text",text_,Long.toString(speed),loop,false,color);
             
               try { 
            
                    app.getPixel().PixelQueue.add("text" + "," + text_ + "," + speed_ + "," + Integer.toString(loop) + "," + "false" + "," + color.toString());
                } 
                catch (Exception e) { 
                    System.out.println("Queue Exception: " + e); 
                } 
               
                System.out.println("Queue Contents : " + app.getPixel().PixelQueue);
            
        } else {
        
            app.getPixel().setScrollingText(text_);

            if (color_ != null)  app.getPixel().setScrollTextColor(color);

            if (speed_ != null)  app.getPixel().setScrollDelay(speed);

            app.getPixel().scrollText(loop);
        
        }
        
        
        
        return "scrolling text request received: " + text_ ;
    }
    
    
    public static Color hex2Rgb(String colorStr) 
    {
        return new Color(
                Integer.valueOf( colorStr.substring( 0, 2 ), 16 ),
                Integer.valueOf( colorStr.substring( 2, 4 ), 16 ),
                Integer.valueOf( colorStr.substring( 4, 6 ), 16 ) );
    }    
    
    
    
    
}


  
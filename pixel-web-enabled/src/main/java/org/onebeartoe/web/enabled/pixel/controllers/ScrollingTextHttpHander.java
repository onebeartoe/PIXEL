
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
import org.onebeartoe.pixel.hardware.Pixel;
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
        int scrollsmooth_ = 1;
        Long speeddelay_ = 10L;
        
        
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
            }

            for (NameValuePair param : params) {

                switch (param.getName()) {

                    case "t": //scrolling text value
                        text_ = param.getValue();
                        break;
                    case "c": //text color
                        color_ = param.getValue();
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
                    case "ss": //scroll smooth
                        scrollsmooth_ = Integer.valueOf(param.getValue());
                        break;
                    case "scrollsmooth": //scroll smooth
                        scrollsmooth_ = Integer.valueOf(param.getValue());
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
            //if (scrollsmooth_ != 1) System.out.println("scrolling smooth factor: " + scrollsmooth_);
            System.out.println("# times to loop: " + loop_);
            logMe.aLogger.info("scrolling text: " + text_);
            if (color_ != null) logMe.aLogger.info("text color: " + color_);
            if (speed_ != null) logMe.aLogger.info("scrolling speed: " + speed_);
            //if (scrollsmooth_ != 1) logMe.aLogger.info("scrolling smooth factor: " + scrollsmooth_);
            logMe.aLogger.info("# times to loop: " + loop_);
        }
            
        if (color_ != null) color = ArcadeHttpHandler.getColorFromHexOrName(color_);
            
            if (speed_ != null) {

                speed = Long.valueOf(speed_);

                if(speed == 0) //Roberto originally had if less than 100 here but the scrolling is too slow
                {
                    speed = 10L;
                }
            }      
        
        //app.getPixel().interactiveMode(); //to do: we shouldn't need this but add can try adding back if scrolling text failing
        
        if (loop_ != null) loop = Integer.valueOf(loop_);
              
        if (color_ == null)  color = Color.RED;
       
        //if (speed_ == null) { //speed was not specified so let's get default per panel type
        //  let's set the defaults 
            int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
            int yTextOffset = -4;
            int fontSize_ = 22;
            speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset and font
        //}
        
        // and if speed was entered in a param, let's override with that
         if (speed_ != null) { //speed is not null so it was specified so let's override with that
             speed = Long.valueOf(speed_);
         } //TO DO this is bad code, clean up later
        
        app.getPixel().scrollText(text_, loop, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
        
        return "scrolling text request received: " + text_ ;
    }
}


  

package org.onebeartoe.web.enabled.pixel.controllers;

import com.sun.net.httpserver.HttpExchange;
import java.awt.Color;
import java.awt.Font;

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
    protected LCDPixelcade lcdDisplay = null;
    protected WebEnabledPixel app;
    
    public ScrollingTextHttpHander(WebEnabledPixel application)
    {
        //super(application);
    
        if(WebEnabledPixel.getLCDMarquee().equals("yes"))
            lcdDisplay = new LCDPixelcade(); //bombing out on windows here
    
        String name = getClass().getName();
        
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
        int scrollsmooth_ = 0;
        Long speeddelay_ = 10L;
        int fontSize_ = 0;
        int yOffset_ = 0;
        int lines_ = 1;
        String font_ = null;
        LogMe logMe = LogMe.getInstance();
        URI requestURI = exchange.getRequestURI();
        Font font = null;
        
        
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
                    case "font":
                        font_ = param.getValue();
                        break;
                    case "size":
                        fontSize_ = Integer.valueOf(param.getValue()).intValue();
                        break;
                    case "yoffset":
                        yOffset_ = Integer.valueOf(param.getValue()).intValue();
                        break;
                     case "lines":
                        lines_ = Integer.valueOf(param.getValue()).intValue(); 
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
        
    if (color_ == null) {
      if (WebEnabledPixel.getTextColor().equals("random")) {
        color = WebEnabledPixel.getRandomColor();
      } else {
        color = WebEnabledPixel.getColorFromHexOrName(WebEnabledPixel.getTextColor());
      } 
    } else {
      color = WebEnabledPixel.getColorFromHexOrName(color_);
    } 
    if (loop_ != null)
      loop = Integer.valueOf(loop_).intValue(); 
    
    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
    speed = Long.valueOf(WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID));
    
    if (speed_ != null) {
      speed = Long.valueOf(speed_);
      if (speed.longValue() == 0L)
        speed = Long.valueOf(10L); 
    } 
    
    if (scrollsmooth_ == 0) {
      String scrollSpeedSettings = WebEnabledPixel.getTextScrollSpeed();
      scrollsmooth_ = WebEnabledPixel.getScrollingSmoothSpeed(scrollSpeedSettings);
    } 
    
    if (font_ == null)
      font_ = WebEnabledPixel.getDefaultFont(); 
    
    Pixel.setFontFamily(font_);
    
    if (yOffset_ == 0)
      yOffset_ = WebEnabledPixel.getDefaultyTextOffset(); 
    
    Pixel.setYOffset(yOffset_);
    
    if (fontSize_ == 0)
      fontSize_ = WebEnabledPixel.getDefaultFontSize(); 
    
    Pixel.setFontSize(fontSize_);
    
    if (lines_ == 2) 
        Pixel.setDoubleLine(true);
    else if (lines_ == 4)
         Pixel.setFourLine(true);
    else {
        Pixel.setDoubleLine(false); //don't forget to set it back
        Pixel.setFourLine(false); //don't forget to set it back
    }
        
    app.getPixel().scrollText(text_, loop, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
    
    if (Pixel.isWindows() && WebEnabledPixel.getLCDMarquee().equals("yes")) {
                if(lcdDisplay == null)
                   lcdDisplay = new LCDPixelcade();
                
            lcdDisplay.setNumLoops(loop);    
            lcdDisplay.scrollText(text_, new Font(font_, Font.PLAIN, 288), color, 5); //int speed
    }
        
    return "scrolling text request received: " + text_ ;
    
    }
}


  

//package org.onebeartoe.web.enabled.pixel.controllers;
//
//import com.sun.net.httpserver.HttpExchange;
//import java.awt.Color;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.onebeartoe.network.TextHttpHandler;
//import org.onebeartoe.pixel.LogMe;
//import org.onebeartoe.pixel.hardware.Pixel;
//import org.onebeartoe.web.enabled.pixel.CliPixel;
//import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
//
//public class ScrollingTextHttpHander extends TextHttpHandler {
//  protected WebEnabledPixel app;
//  
//  public ScrollingTextHttpHander(WebEnabledPixel application) {
//    String name = getClass().getName();
//    this.app = application;
//  }
//  
//  protected String getHttpText(HttpExchange exchange) {
//    String text_ = null;
//    String color_ = null;
//    Color color = null;
//    String speed_ = null;
//    Long speed = null;
//    String loop_ = null;
//    int loop = 0;
//    int scrollsmooth_ = 0;
//    Long speeddelay_ = Long.valueOf(10L);
//    int fontSize_ = 0;
//    int yOffset_ = 0;
//    int lines_ = 1;
//    String font_ = null;
//    LogMe logMe = LogMe.getInstance();
//    URI requestURI = exchange.getRequestURI();
//    
//    if (!CliPixel.getSilentMode()) {
//      LogMe.aLogger.info("Scrolling text handler received a request: " + requestURI);
//      System.out.println("Scrolling text handler received a request: " + requestURI);
//    } 
//    
//    String encodedQuery = requestURI.getQuery();
//    
//    if (encodedQuery == null) {
//      text_ = "scrolling text";
//      if (!CliPixel.getSilentMode()) {
//        LogMe.aLogger.info("scrolling default text");
//        System.out.println("scrolling default text");
//      } 
//    } else {
//      List<NameValuePair> params = null;
//      try {
//        params = URLEncodedUtils.parse(new URI(requestURI.toString()), "UTF-8");
//      } catch (URISyntaxException uRISyntaxException) {}
//      for (NameValuePair param : params) {
//        switch (param.getName()) {
//          case "t":
//            text_ = param.getValue();
//          case "c":
//            color_ = param.getValue();
//          case "l":
//            loop_ = param.getValue();
//          case "text":
//            text_ = param.getValue();
//          case "color":
//            color_ = param.getValue();
//          case "speed":
//            speed_ = param.getValue();
//          case "loop":
//            loop_ = param.getValue();
//          case "font":
//            font_ = param.getValue();
//          case "size":
//            fontSize_ = Integer.valueOf(param.getValue()).intValue();
//          case "yoffset":
//            yOffset_ = Integer.valueOf(param.getValue()).intValue();
//          case "ss":
//            scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
//          case "lines":
//            lines_ = Integer.valueOf(param.getValue()).intValue();
//          case "scrollsmooth":
//            scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
//        } 
//      } 
//    } 
//    if (!CliPixel.getSilentMode()) {
//      System.out.println("scrolling text: " + text_);
//      if (color_ != null)
//        System.out.println("text color: " + color_); 
//      if (speed_ != null)
//        System.out.println("scrolling speed: " + speed_); 
//      System.out.println("# times to loop: " + loop_);
//      LogMe.aLogger.info("scrolling text: " + text_);
//      if (color_ != null)
//        LogMe.aLogger.info("text color: " + color_); 
//      if (speed_ != null)
//        LogMe.aLogger.info("scrolling speed: " + speed_); 
//      LogMe.aLogger.info("# times to loop: " + loop_);
//    } 
//    if (color_ == null) {
//      if (WebEnabledPixel.getTextColor().equals("random")) {
//        color = WebEnabledPixel.getRandomColor();
//      } else {
//        color = WebEnabledPixel.getColorFromHexOrName(WebEnabledPixel.getTextColor());
//      } 
//    } else {
//      color = WebEnabledPixel.getColorFromHexOrName(color_);
//    } 
//    if (loop_ != null)
//      loop = Integer.valueOf(loop_).intValue(); 
//    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//    speed = Long.valueOf(WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID));
//    if (speed_ != null) {
//      speed = Long.valueOf(speed_);
//      if (speed.longValue() == 0L)
//        speed = Long.valueOf(10L); 
//    } 
//    if (scrollsmooth_ == 0) {
//      String scrollSpeedSettings = WebEnabledPixel.getTextScrollSpeed();
//      scrollsmooth_ = WebEnabledPixel.getScrollingSmoothSpeed(scrollSpeedSettings);
//    } 
//    if (font_ == null)
//      font_ = WebEnabledPixel.getDefaultFont(); 
//    this.app.getPixel();
//    Pixel.setFontFamily(font_);
//    
//    if (yOffset_ == 0)
//      yOffset_ = WebEnabledPixel.getDefaultyTextOffset(); 
//    this.app.getPixel();
//    Pixel.setYOffset(yOffset_);
//    if (fontSize_ == 0)
//      fontSize_ = WebEnabledPixel.getDefaultFontSize(); 
//    this.app.getPixel();
//    Pixel.setFontSize(fontSize_);
//    
//    
//    if (lines_ == 2) 
//        app.getPixel().setDoubleLine(true);
//     else
//        app.getPixel().setDoubleLine(false); //don't forget to set it back
//
//     if (lines_ == 4) 
//        app.getPixel().setFourLine(true);
//     else
//        app.getPixel().setFourLine(false); //don't forget to set it back
//    
//    
//    
//    app.getPixel().scrollText(text_, loop, speed.longValue(), color, WebEnabledPixel.pixelConnected, scrollsmooth_);
//    
//    return "scrolling text request received: " + text_;
//  }
//}


//package org.onebeartoe.web.enabled.pixel.controllers;
//
//import com.sun.net.httpserver.HttpExchange;
//import java.awt.Color;
//
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URLDecoder;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import static java.util.regex.Pattern.compile;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.onebeartoe.network.TextHttpHandler;
//import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
//import org.onebeartoe.pixel.LogMe;
//import org.onebeartoe.pixel.hardware.Pixel;
//import org.onebeartoe.web.enabled.pixel.CliPixel;
//
///**
// * @author Roberto Marquez
// */
//public class ScrollingTextHttpHander extends TextHttpHandler  //TO DO have TextHttpHandler send a return
//        
//{
//    //private Logger logger;
//    
//    protected WebEnabledPixel app;
//    
//    public ScrollingTextHttpHander(WebEnabledPixel application)
//    {
//        String name = getClass().getName();
//       // logger = Logger.getLogger(name);
//        
//        this.app = application;
//    }
//
//    @Override
//    protected String getHttpText(HttpExchange exchange)
//    {
//        
//        String text_ = null;
//        String color_ = null;
//        Color color = null;
//        String speed_ = null;
//        Long speed = null;
//        String loop_ = null;
//        int loop = 0;
//        int scrollsmooth_ = 1;
//        Long speeddelay_ = 10L;
//        
//        int fontSize_ = WebEnabledPixel.MATRIX_TYPE.height;
//        int yOffset_ = 0;
//        int lines_ = 1;
//        //boolean smallFont = false;
//        String font_ = "Arial";
//        
//        
//        LogMe logMe = LogMe.getInstance();
//        URI requestURI = exchange.getRequestURI();
//        
//         if (!CliPixel.getSilentMode()) {
//             logMe.aLogger.info("Scrolling text handler received a request: " + requestURI);
//             System.out.println("Scrolling text handler received a request: " + requestURI);
//         }
//        
//        String encodedQuery = requestURI.getQuery();
//        
//        if(encodedQuery == null)
//        {
//            text_ = "scrolling text";
//            
//             if (!CliPixel.getSilentMode()) {
//                logMe.aLogger.info("scrolling default text");
//                System.out.println("scrolling default text");
//            }
//        }
//        else  {
//            
//            //we'll have something /text?t=hello world&c=red&s=100&l=5
//            List<NameValuePair> params = null;
//            try {
//                    params = URLEncodedUtils.parse(new URI(requestURI.toString()), "UTF-8");
//            } catch (URISyntaxException ex) {
//            }
//
//            for (NameValuePair param : params) {
//
//                switch (param.getName()) {
//
//                    case "t": //scrolling text value
//                        text_ = param.getValue();
//                        break;
//                    case "c": //text color
//                        color_ = param.getValue();
//                        break;
//                    case "l": //loop
//                        loop_ = param.getValue();
//                        break;
//                    case "text": //scrolling text value
//                        text_ = param.getValue();
//                        break;
//                    case "color": //text color
//                        color_ = param.getValue();
//                        break;
//                    case "speed": //scrolling speed
//                        speed_ = param.getValue();
//                        break;
//                    case "loop": //loop
//                        loop_ = param.getValue();
//                        break;
//                    case "font": //font family
//                        font_ = param.getValue();
//                        break;
//                    case "size": //font size
//                        fontSize_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "yoffset": //font size
//                        yOffset_ = Integer.valueOf(param.getValue());
//                        break;    
//                    case "ss": //scroll smooth
//                        scrollsmooth_ = Integer.valueOf(param.getValue());
//                        break;
//                    case "lines": //number of lines
//                        lines_ = Integer.valueOf(param.getValue());
//                        break;    
//                    case "scrollsmooth": //scroll smooth
//                        scrollsmooth_ = Integer.valueOf(param.getValue());
//                        break;
//                }
//            }
//            
//            
//
//            /* TO DO catch this wrong URL format as I made this mistake of ? instead of & after the first one!!!!
//            Scrolling text handler received a request: /text/?t=hello%20world?c=red?s=10?l=2
//            t : hello world?c=red?s=10?l=2
//            */
//           
//            }
//
//        if (!CliPixel.getSilentMode()) {
//
//            System.out.println("scrolling text: " + text_);
//            if (color_ != null) System.out.println("text color: " + color_);
//            if (speed_ != null) System.out.println("scrolling speed: " + speed_);
//            //if (scrollsmooth_ != 1) System.out.println("scrolling smooth factor: " + scrollsmooth_);
//            System.out.println("# times to loop: " + loop_);
//            logMe.aLogger.info("scrolling text: " + text_);
//            if (color_ != null) logMe.aLogger.info("text color: " + color_);
//            if (speed_ != null) logMe.aLogger.info("scrolling speed: " + speed_);
//            //if (scrollsmooth_ != 1) logMe.aLogger.info("scrolling smooth factor: " + scrollsmooth_);
//            logMe.aLogger.info("# times to loop: " + loop_);
//        }
//            
//        if (color_ != null) color = ArcadeHttpHandler.getColorFromHexOrName(color_);
//            
//            if (speed_ != null) {
//
//                speed = Long.valueOf(speed_);
//
//                if(speed == 0) //Roberto originally had if less than 100 here but the scrolling is too slow
//                {
//                    speed = 10L;
//                }
//            }      
//        
//        //app.getPixel().interactiveMode(); //to do: we shouldn't need this but add can try adding back if scrolling text failing
//        
//        if (loop_ != null) loop = Integer.valueOf(loop_);
//              
//        if (color_ == null)  color = Color.RED;
//       
//        //if (speed_ == null) { //speed was not specified so let's get default per panel type
//        //  let's set the defaults 
//        int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//        //int yTextOffset = -4;
//        //fontSize_ = 22;
//        
//        
//        speed = WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID);  //this method also sets the yoffset and font
//        //to do we can set this dynamically 
//        
//       
//             app.getPixel().setFontSize(fontSize_); //this comes from the param
//             app.getPixel().setFontFamily(font_);
//             app.getPixel().setYOffset(yOffset_);
//             
//             if (lines_ == 2) 
//                app.getPixel().setDoubleLine(true);
//             else
//                app.getPixel().setDoubleLine(false); //don't forget to set it back
//             
//             if (lines_ == 4) 
//                app.getPixel().setFourLine(true);
//             else
//                app.getPixel().setFourLine(false); //don't forget to set it back
//        
//       
//        // and if speed was entered in a param, let's override with that
//         if (speed_ != null) { //speed is not null so it was specified so let's override with that
//             speed = Long.valueOf(speed_);
//         } //TO DO this is bad code, clean up later
//        
//        app.getPixel().scrollText(text_, loop, speed, color,WebEnabledPixel.pixelConnected,scrollsmooth_);
//        
//        return "scrolling text request received: " + text_ ;
//    }
//}

//
//package org.onebeartoe.web.enabled.pixel.controllers;
//
//import com.sun.net.httpserver.HttpExchange;
//import java.awt.Color;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.util.List;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.utils.URLEncodedUtils;
//import org.onebeartoe.network.TextHttpHandler;
//import org.onebeartoe.pixel.LogMe;
//import org.onebeartoe.pixel.hardware.Pixel;
//import org.onebeartoe.web.enabled.pixel.CliPixel;
//import org.onebeartoe.web.enabled.pixel.WebEnabledPixel;
//
//public class ScrollingTextHttpHander extends TextHttpHandler {
//  protected WebEnabledPixel app;
//  
//  public ScrollingTextHttpHander(WebEnabledPixel application) {
//    String name = getClass().getName();
//    this.app = application;
//  }
//  
//  protected String getHttpText(HttpExchange exchange) {
//    String text_ = null;
//    String color_ = null;
//    Color color = null;
//    String speed_ = null;
//    Long speed = null;
//    String loop_ = null;
//    int loop = 0;
//    int scrollsmooth_ = 0;
//    Long speeddelay_ = Long.valueOf(10L);
//    int fontSize_ = 0;
//    int yOffset_ = 0;
//    int lines_ = 1;
//    String font_ = null;
//    LogMe logMe = LogMe.getInstance();
//    URI requestURI = exchange.getRequestURI();
//    if (!CliPixel.getSilentMode()) {
//      LogMe.aLogger.info("Scrolling text handler received a request: " + requestURI);
//      System.out.println("Scrolling text handler received a request: " + requestURI);
//    } 
//    String encodedQuery = requestURI.getQuery();
//    if (encodedQuery == null) {
//      text_ = "scrolling text";
//      if (!CliPixel.getSilentMode()) {
//        LogMe.aLogger.info("scrolling default text");
//        System.out.println("scrolling default text");
//      } 
//    } else {
//      List<NameValuePair> params = null;
//      try {
//        params = URLEncodedUtils.parse(new URI(requestURI.toString()), "UTF-8");
//      } catch (URISyntaxException uRISyntaxException) {}
//      for (NameValuePair param : params) {
//        switch (param.getName()) {
//          case "t":
//            text_ = param.getValue();
//          case "c":
//            color_ = param.getValue();
//          case "l":
//            loop_ = param.getValue();
//          case "text":
//            text_ = param.getValue();
//          case "color":
//            color_ = param.getValue();
//          case "speed":
//            speed_ = param.getValue();
//          case "loop":
//            loop_ = param.getValue();
//          case "font":
//            font_ = param.getValue();
//          case "size":
//            fontSize_ = Integer.valueOf(param.getValue()).intValue();
//          case "yoffset":
//            yOffset_ = Integer.valueOf(param.getValue()).intValue();
//          case "ss":
//            scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
//          case "lines":
//            lines_ = Integer.valueOf(param.getValue()).intValue();
//          case "scrollsmooth":
//            scrollsmooth_ = Integer.valueOf(param.getValue()).intValue();
//        } 
//      } 
//    } 
//    if (!CliPixel.getSilentMode()) {
//      System.out.println("scrolling text: " + text_);
//      if (color_ != null)
//        System.out.println("text color: " + color_); 
//      if (speed_ != null)
//        System.out.println("scrolling speed: " + speed_); 
//      System.out.println("# times to loop: " + loop_);
//      LogMe.aLogger.info("scrolling text: " + text_);
//      if (color_ != null)
//        LogMe.aLogger.info("text color: " + color_); 
//      if (speed_ != null)
//        LogMe.aLogger.info("scrolling speed: " + speed_); 
//      LogMe.aLogger.info("# times to loop: " + loop_);
//    } 
//    if (color_ == null) {
//      if (WebEnabledPixel.getTextColor().equals("random")) {
//        color = WebEnabledPixel.getRandomColor();
//      } else {
//        color = WebEnabledPixel.getColorFromHexOrName(WebEnabledPixel.getTextColor());
//      } 
//    } else {
//      color = WebEnabledPixel.getColorFromHexOrName(color_);
//    } 
//    if (loop_ != null)
//      loop = Integer.valueOf(loop_).intValue(); 
//    int LED_MATRIX_ID = WebEnabledPixel.getMatrixID();
//    speed = Long.valueOf(WebEnabledPixel.getScrollingTextSpeed(LED_MATRIX_ID));
//    if (speed_ != null) {
//      speed = Long.valueOf(speed_);
//      if (speed.longValue() == 0L)
//        speed = Long.valueOf(10L); 
//    } 
//    if (scrollsmooth_ == 0) {
//      String scrollSpeedSettings = WebEnabledPixel.getTextScrollSpeed();
//      scrollsmooth_ = WebEnabledPixel.getScrollingSmoothSpeed(scrollSpeedSettings);
//    } 
//    if (font_ == null)
//      font_ = WebEnabledPixel.getDefaultFont(); 
//    this.app.getPixel();
//    Pixel.setFontFamily(font_);
//    if (yOffset_ == 0)
//      yOffset_ = WebEnabledPixel.getDefaultyTextOffset(); 
//    this.app.getPixel();
//    Pixel.setYOffset(yOffset_);
//    if (fontSize_ == 0)
//      fontSize_ = WebEnabledPixel.getDefaultFontSize(); 
//    this.app.getPixel();
//    Pixel.setFontSize(fontSize_);
//    if (lines_ == 2) {
//      this.app.getPixel();
//      Pixel.setDoubleLine(true);
//    } else {
//      this.app.getPixel();
//      Pixel.setDoubleLine(false);
//    } 
//    if (lines_ == 4) {
//      this.app.getPixel();
//      Pixel.setFourLine(true);
//    } else {
//      this.app.getPixel();
//      Pixel.setFourLine(false);
//    } 
//    this.app.getPixel().scrollText(text_, loop, speed.longValue(), color, WebEnabledPixel.pixelConnected, scrollsmooth_);
//    return "scrolling text request received: " + text_;
//  }
//}
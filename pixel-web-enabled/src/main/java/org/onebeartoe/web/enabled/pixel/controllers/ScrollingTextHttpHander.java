
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
import static org.onebeartoe.web.enabled.pixel.WebEnabledPixel.fontNames;

/**
 * @author Roberto Marquez
 */
public class ScrollingTextHttpHander extends PixelHttpHandler
{
    private Timer timer;
    
    private HashMap<String, Font> fonts;

    private String scrollingText;
    
    private int x;    
        
    public ScrollingTextHttpHander()
    {
        x = 0;
                
        fonts = new HashMap();
    }
    
    /**
     * Override this to perform any additional background drawing on the image that get sent to the PIXEL
     * @param g2d 
     */
    protected void additionalBackgroundDrawing(Graphics2D g2d) throws Exception
    {
        
    }    
    
    /**
     * Override this to perform any additional foreground drawing on the image that get sent to the PIXEL
     * @param g2d 
     */
    protected void additionalForegroundDrawing(Graphics2D g2d) throws Exception
    {
        
    }
    
    @Override
    protected String getHttpText(HttpExchange exchange)
    {
        logger.log(Level.INFO, "hi from scroller");
        
        String requestMethod = exchange.getRequestMethod();
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

                setScrollingText(text);
            }
        } 
        catch (UnsupportedEncodingException ex)
        {
            logger.log(Level.SEVERE, "The scrolling text parameters could not be decoded.", ex);
        }


        if(timer == null)
        {
            System.out.println("time is null");
        }
        else
        {
            timer.cancel();

        }

        timer = new Timer();

        int refreshDelay = 500;//1000 * 12;  // in twelve seconds


        TimerTask drawTask = new TextScroller();

        Date firstTime = new Date();

        timer.schedule(drawTask, firstTime, refreshDelay);

        return "was it frech?!?";
    }
    
    public String getScrollingText()
    {
        return scrollingText;
    }

    public void setScrollingText(String scrollingText)
    {
        this.scrollingText = scrollingText;
    }
    
    @Deprecated
    public class TextScroller extends TimerTask//implements ActionListener
    {
        @Override
        public void run()
        {
	    int delay = 200;//scrollSpeedSlider.getValue();	
	    delay = 710 - delay;                            // al linke: added this so the higher slider value means faster scrolling
	    
//	    ChangeModeServlet.this.timer.setDelay(refreshDelay);
	    
            int w = 64;
            int h = 64;
	    
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            
	    Color textColor = Color.GREEN;//colorPanel.getBackground();
	    
            Graphics2D g2d = img.createGraphics();
            g2d.setPaint(textColor);
                      
            String fontFamily = fontNames[0];
//            String fontFamily = fontFamilyChooser.getSelectedItem().toString();
            
            Font font = fonts.get(fontFamily);
            if(font == null)
            {
                font = new Font(fontFamily, Font.PLAIN, 32);
                fonts.put(fontFamily, font);
            }            
            
            g2d.setFont(font);
            
            String message = getScrollingText();
            
            FontMetrics fm = g2d.getFontMetrics();
            
            int y = fm.getHeight();            

            try 
            {
                additionalBackgroundDrawing(g2d);
            } 
            catch (Exception ex) 
            {
                logger.log(Level.SEVERE, null, ex);
            }
            
            g2d.drawString(message, x, y);
            
            try 
            {
                additionalForegroundDrawing(g2d);
            } 
            catch (Exception ex) 
            {
                logger.log(Level.SEVERE, null, ex);
            }
            
            g2d.dispose();

            System.out.println(".");

            if(app.getPixel() != null)
            {
                try 
                {  
                    app.getPixel().writeImagetoMatrix(img, MATRIX_TYPE.width, MATRIX_TYPE.height);
                } 
                catch (ConnectionLostException ex) 
                {
                    logger.log(Level.SEVERE, null, ex);
                }                
            }
            else
            {
                logger.log(Level.INFO, "There is no pixel for the text scrolller.");
            }
                        
            int messageWidth = fm.stringWidth(message);            
            int resetX = 0 - messageWidth;
            
            if(x == resetX)
            {
                x = w;
            }
            else
            {
                x--;
            }
        }
    }    
}
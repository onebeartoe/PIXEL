
package org.onebeartoe.pixel.enterpirse.edition;

import ioio.lib.api.exception.ConnectionLostException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
//import java.util.Timer;
//import java.util.TimerTask;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.onebeartoe.pixel.enterpirse.edition.InitializationServlet.MATRIX_TYPE;
import static org.onebeartoe.pixel.enterpirse.edition.InitializationServlet.PIXEL_KEY;
import static org.onebeartoe.pixel.enterpirse.edition.InitializationServlet.PIXEL_TIMER_KEY;
import static org.onebeartoe.pixel.enterpirse.edition.InitializationServlet.fontNames;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
//@WebServlet(urlPatterns = {"/mode/"}, loadOnStartup=1)
@WebServlet(value = "/mode/", loadOnStartup=1)

public class ChangeModeServlet extends HttpServlet
{
    private Logger logger;

//    volatile protected Timer timer;
        
    private HashMap<String, Font> fonts;

    private int x;
        
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
    public void destroy()
    {
//        if(timer == null)
//        {
//            System.out.println("The init servlet stopped with not timer set.");
//        }
//        else
//        {
//            timer.cancel();
//            System.out.println("The init servlet stopped the Pixel timer.");
//        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException 
    {
        StringBuilder textEntries = new StringBuilder();
        
        textEntries.append("<br/>" + "The change mode servlet received a request.");
        
        // remove the leading forward slash
        String pi = request.getPathInfo();
        String mode;
        if(pi == null)
        {
            mode = "/";
        }
        else
        {
            mode = pi.substring(1);
        }
        
//  /      TimerTask drawTask = null;
        
        String forward;
        switch(mode)
        {
            case "/":
            {
                forward = "";

                textEntries.append("<br/>" + "Slash was selected.");
                
                break;
            }
            default:
            {
  //              drawTask = new TextScroller();
                
                textEntries.append("<br/>" + "Scrolling text draw task was created.");
                
                // scrolling text
                forward = "scrolling-text";
            }
        }

        
        ServletContext servletContext = getServletContext();     
//        Timer timer = (Timer) servletContext.getAttribute(PIXEL_TIMER_KEY);     
//        if(timer == null)
//        {
//            textEntries.append("<br/>" + "The timer was null on mode change.");
//        }
//        else
//        {
//            timer.cancel();
//        }
        
//        if(drawTask == null)
        {
            textEntries.append("<br/>" + "The drawTask was null on mode change.");
        }
//        else
        {
            textEntries.append("<br/>" + "Starting Pixel worker");
            Date now = new Date();
//            timer = new Timer();//now, startAction);        
            long refreshDelay = 500;
//            timer.schedule(drawTask, now, refreshDelay);
            textEntries.append("<br/>" + "The Pixel worker was scheduled.");
        }
        
        forward = "/mode/" + forward + "/index.jsp";
//        forward = request.getContextPath() + "/mode/" + forward + "/index.jsp";
        
        textEntries.append("<br/>" + "change mode servlet forwarding to " + forward);
        
        
        request.setAttribute("stexts", textEntries.toString());
        
        ServletContext context = getServletContext();
        RequestDispatcher rd = context.getRequestDispatcher(forward);
//        response.sendRedirect(forward);
        
        rd.forward(request, response);
    }
    
    public String getText()
    {
	return "some text text text text";
    }
    
    @Override
    public void init() throws ServletException 
    {
        super.init();
        
        logger = Logger.getLogger(getClass().getName());
        
        System.out.println("<br/>" + "The change mode servlet started.");
        
        fonts = new HashMap();

        x = 0;
    }

//    @Deprecated
//    public class TextScroller extends TimerTask//implements ActionListener
//    {
//        @Override
//        public void run()
//        {
//	    int delay = 200;//scrollSpeedSlider.getValue();	
//	    delay = 710 - delay;                            // al linke: added this so the higher slider value means faster scrolling
//	    	    
//            int w = 64;
//            int h = 64;
//	    
//            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
//            
//	    Color textColor = Color.GREEN;//colorPanel.getBackground();
//	    
//            Graphics2D g2d = img.createGraphics();
//            g2d.setPaint(textColor);
//                      
//            String fontFamily = fontNames[0];
//            
//            Font font = fonts.get(fontFamily);
//            if(font == null)
//            {
//                font = new Font(fontFamily, Font.PLAIN, 32);
//                fonts.put(fontFamily, font);
//            }            
//            
//            g2d.setFont(font);
//            
//            String message = getText();
//            
//            FontMetrics fm = g2d.getFontMetrics();
//            
//            int y = fm.getHeight();            
//
//            try 
//            {
//                additionalBackgroundDrawing(g2d);
//            } 
//            catch (Exception ex) 
//            {
//                ex.printStackTrace();
//            }
//            
//            g2d.drawString(message, x, y);
//            
//            try 
//            {
//                additionalForegroundDrawing(g2d);
//            } 
//            catch (Exception ex) 
//            {
//                ex.printStackTrace();
//            }
//            
//            g2d.dispose();
//System.out.println("<br/>" + ".");
//
//            ServletContext servletContext = getServletContext();     
//            Pixel pixel = (Pixel) servletContext.getAttribute(PIXEL_KEY);
//            if(pixel != null)
//            {
//                try 
//                {  
//                    pixel.writeImagetoMatrix(img, MATRIX_TYPE.width, MATRIX_TYPE.height);
//                } 
//                catch (ConnectionLostException ex) 
//                {
//                    ex.printStackTrace();
//                }                
//            }
//                        
//            int messageWidth = fm.stringWidth(message);            
//            int resetX = 0 - messageWidth;
//            
//            if(x == resetX)
//            {
//                x = w;
//            }
//            else
//            {
//                x--;
//            }
//        }
//    }
}

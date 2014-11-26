
package org.onebeartoe.pixel.web.enabled;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
public class WebEnabledPixel
{
    private Logger logger;

    private HttpServer server;

    private Timer searchTimer;
    
    private Pixel pixel;
    
    public static RgbLedMatrix.Matrix MATRIX_TYPE = RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;

    private int x;

    public static final String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    
    private HashMap<String, Font> fonts;
    
    private String scrollingText;
    
    public WebEnabledPixel()
    {
        String name = getClass().getName();
        logger = Logger.getLogger(name);
        
        try
        {
            InetSocketAddress anyhost = new InetSocketAddress(2007);
            server = HttpServer.create(anyhost, 0);
            HttpContext createContext = server.createContext("/",     new IndexHttpHandler() );
            HttpContext   textContext = server.createContext("/text", new ScrollingTextHttpHander() );
                                        server.createContext("/still", new StillImageHttpHandler() );
        } 
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, null, ex);
        }
        
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
    
    public String getScrollingText()
    {
        return scrollingText;
    }
    
    public static void main(String[] args)
    {
        WebEnabledPixel app = new WebEnabledPixel();
        app.startServer();
    }

    public void setScrollingText(String scrollingText)
    {
        this.scrollingText = scrollingText;
    }    

    private void startSearchTimer()
    {
// CHAGN ETH ENAME OF THIS        
	int refreshDelay = 1000 * 12;  // in twelve seconds
        searchTimer = new Timer();
        
        TimerTask task = new SearchTimerTask();
       
	searchTimer.schedule(task, refreshDelay);
    }
        
    private void startServer()
    {
        startSearchTimer();
        
        server.start();
        
        
        
        PixelIntegration pi = new PixelIntegration();
    }
    
    private class IndexHttpHandler extends TextHttpHandler
    {
        protected String getHttpText(HttpExchange t)
        {
            String response = "Hello, Pixel Worlds!\n";
            
            return response;
        }
    }
    
// MOVE ME
    private abstract class TextHttpHandler implements HttpHandler
    {
        protected abstract String getHttpText(HttpExchange exchange);
        
        public void handle(HttpExchange exchange) throws IOException
        {            
            String response = getHttpText(exchange);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }        
    }
    
    private class PixelIntegration extends IOIOConsoleApp
    {
        public PixelIntegration()
        {
            try
            {
                System.out.println("CALLING GO");
                go(null);
            } 
            catch (Exception ex)
            {
                String message = "Could not initialize Pixel: " + ex.getMessage();
                logger.log(Level.INFO, message);
            }
        }
        
        /**
         * can you belive this was what was not letting the app connect to the PIXEL?
         * @param args
         * @throws IOException 
         */
        @Override
        protected void run(String[] args) throws IOException 
        {
            System.out.println("now it begins!");
            
            InputStreamReader isr = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(isr);
            boolean abort = false;
            String line;
            while (!abort && (line = reader.readLine()) != null) 
            {
                if (line.equals("t")) 
                {
                    //ledOn_ = !ledOn_;
                } 
                else if (line.equals("q")) {
                    abort = true;
                    System.exit(1);
                } 
                else 
                {
                    System.out.println("Unknown input. q=quit.");
                }
            }
        }

        @Override
        public IOIOLooper createIOIOLooper(String connectionType, Object extra)
        {
            IOIOLooper looper = new BaseIOIOLooper() 
            {
            
                @Override
                public void disconnected() 
                {
                    String message = "PIXEL was Disconnected";
                    System.out.println(message);
                }

                @Override
                public void incompatible() 
                {
                    String message = "Incompatible Firmware Detected";
                    System.out.println(message);
                }

                @Override
                protected void setup() throws ConnectionLostException, InterruptedException
                {
                    pixel = new Pixel(MATRIX_TYPE);
                    pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
                    pixel.ioiO = ioio_;

                    StringBuilder message = new StringBuilder();
                    
                    if(pixel.matrix == null)
                    {
                        message.append("wtffff" + "\n");
                    }
                    else
                    {
                        message.append("Found PIXEL: " + pixel.matrix + "\n");
                    }
                    
                    
                    
                    message.append("You may now interact with the PIXEL!\n");

    //TODO: Load something on startup

                    searchTimer.cancel(); //need to stop the timer so we don't still display the pixel searching message
                    
                    message.append("PIXEL Status: Connected");

                    logger.log(Level.INFO, message.toString());
                }
            };
                    
            return looper;
        }        
    }
    
    private class ScrollingTextHttpHander extends TextHttpHandler //implements HttpHandler 
    {
        private Timer timer;
        
        @Override
        protected String getHttpText(HttpExchange exchange)
        {
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
                System.out.println("time is not null");
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
    }
        
    private class SearchTimerTask extends TimerTask
    {
	final long searchPeriodLength = 45 * 1000;
	
	final long periodStart;
	
	final long periodEnd;
	
	private int dotCount = 0;
	
	String message = "Searching for PIXEL";
	
	StringBuilder label = new StringBuilder(message);
	
	public SearchTimerTask()
	{
	    label.insert(0, "<html><body><h2>");
	    
	    Date d = new Date();
	    periodStart = d.getTime();
	    periodEnd = periodStart + searchPeriodLength;
	}
	
	public void run()
	{	    	    	    
	    if(dotCount > 10)
	    {
		label = new StringBuilder(message);
		label.insert(0, "<html><body><h2>");
		
		dotCount = 0;
	    }
	    else
	    {
		label.append('.');
	    }
	    dotCount++;
	    
	    Date d = new Date();
	    long now = d.getTime();
	    if(now > periodEnd)
	    {
		searchTimer.cancel();//stop();
                
		if(pixel == null || pixel.matrix == null)
		{
		    message = "A connection to PIXEL could not be established.";
		    String title = "PIXEL Connection Unsuccessful: ";
                    message = title + message;
                    logger.log(Level.SEVERE, message);
		}
                else
                {
                    logger.log(Level.INFO, "Looks like we have a PIXEL connection!");
                }
	    }
	}        
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

            if(pixel != null)
            {
                try 
                {  
                    pixel.writeImagetoMatrix(img, MATRIX_TYPE.width, MATRIX_TYPE.height);
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


package org.onebeartoe.web.enabled.pixel;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;
import org.onebeartoe.web.enabled.pixel.controllers.AnimationsHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.IndexHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.PixelHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ScrollingTextHttpHander;
import org.onebeartoe.web.enabled.pixel.controllers.StillImageHttpHandler;

/**
 * @author Roberto Marquez
 */
public class WebEnabledPixel
{
    protected Logger logger;

    private HttpServer server;

    private Timer searchTimer;
    
    private Pixel pixel;

    public static final String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    
    // 3 translates to RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
    private final static int LED_MATRIX_ID = 3;
    
    private static final PixelEnvironment pixelEnvironment = new PixelEnvironment(LED_MATRIX_ID);
    
    public final static RgbLedMatrix.Matrix MATRIX_TYPE = pixelEnvironment.KIND;
    
    public WebEnabledPixel()
    {
        String name = getClass().getName();
        logger = Logger.getLogger(name);
        
        createControllers();
    }
    
    private void createControllers()
    {
        try
        {
            InetSocketAddress anyhost = new InetSocketAddress(2007);
            server = HttpServer.create(anyhost, 0);
            
            PixelHttpHandler indexHttpHandler = new IndexHttpHandler();
            PixelHttpHandler scrollingTextHttpHander = new ScrollingTextHttpHander();
            PixelHttpHandler stillImageHttpHandler = new StillImageHttpHandler() ;
            PixelHttpHandler animationsHttpHandler = new AnimationsHttpHandler();

// ARE WE GONNA DO ANYTHING WITH THE HttpContext OBJECTS?            
            HttpContext createContext = server.createContext("/",     indexHttpHandler);
            HttpContext   textContext = server.createContext("/text", scrollingTextHttpHander);
            HttpContext  stillContext = server.createContext("/still", stillImageHttpHandler);
            HttpContext animationsContext = server.createContext("/animations", animationsHttpHandler);
                                        
            indexHttpHandler.setApp(this);
            scrollingTextHttpHander.setApp(this);
            stillImageHttpHandler.setApp(this);
            animationsHttpHandler.setApp(this);
        } 
        catch (IOException ex)
        {
            String message = "An error occured while creating the controllers";
            logger.log(Level.SEVERE, message, ex);
        }
    }

    public Pixel getPixel()
    {
        return pixel;
    }
    
    public static void main(String[] args)
    {
        WebEnabledPixel app = new WebEnabledPixel();
        app.startServer();
    }

    public void setPixel(Pixel pixel)
    {
        this.pixel = pixel;
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
                    pixel = new Pixel(pixelEnvironment.KIND, pixelEnvironment.currentResolution);
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
    
}

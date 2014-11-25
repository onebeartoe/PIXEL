
package org.onebeartoe.pixel.enterpirse.edition;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.onebeartoe.pixel.hardware.Pixel;

/**
 * @author Roberto Marquez
 */
@WebServlet(value = "/init", loadOnStartup=1)
public class InitializationServlet extends HttpServlet
{   
    public static RgbLedMatrix.Matrix MATRIX_TYPE = RgbLedMatrix.Matrix.ADAFRUIT_32x16;
//    public static RgbLedMatrix.Matrix MATRIX_TYPE = RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
    
    private Timer searchTimer;
    
    private String statusLabel;
    
    private Logger logger;
    
    private boolean stayConnected = true;
    
    public static final String [] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
    
    public static final String PIXEL_KEY = "PIXEL_KEY";
    
    public static final String PIXEL_TIMER_KEY = "PIXEL_TIMER_KEY";
    
    @Override
    public void init()
    {
        String className = InitializationServlet.class.getName();
        logger = Logger.getLogger(className);
        logger.log(Level.INFO, "The initialization servlet started!.!");
        
        Pixel pixel = new Pixel(MATRIX_TYPE);
                
        // save the pixel to appliction scope
        ServletContext servletContext = getServletContext();     
        servletContext.setAttribute(PIXEL_KEY, pixel);
        
        // save teh timer to application scope
        Timer timer = null;
        servletContext.setAttribute(PIXEL_TIMER_KEY, timer);
        
	startSearchTimer();
            
        statusLabel = "Initializing";
        TimerTask initializeTask = new TimerTask()
        {

            @Override
            public void run()
            {
                final String [] args = {};
                try 
                {
                    initializePixel(args);
                } 
                catch (Exception ex) 
                {
                    statusLabel = "An error occureds while initializing";
                    logger.log(Level.SEVERE, statusLabel, ex);
                }                    
            }
        };
        Date now  = new Date();    
        Timer initTimer = new Timer();
        initTimer.schedule(initializeTask, now);
    }
    
    @Override
    public void destroy()
    {
        searchTimer.cancel();
    }
        
    private void initializePixel(String[] args) throws Exception 
    {
        PixelIntegration pi = new PixelIntegration(args);
//        pi.
    }
    
//    @Deprecated
//    private void initializePixelOld(String[] args) throws Exception 
//    {
//        System.out.println("initializing the IOIO application helper");
//        
//        IOIOPcApplicationHelper helper = new IOIOPcApplicationHelper(this);
////        helper.start();
//        try
//        {
//            helper.start();
//        }
//        catch(UnsatisfiedLinkError e)
//        {
//            // we are not ont he Raspberry Pi or something went real bad
//            e.printStackTrace();
//        }
//        
//        try 
//        {
//            
//            run(args);
//        } 
//        catch (Exception e) 
//        {                
//            throw e;
//        } 
//        finally 
//        {                
//            helper.stop();
//        }
//    }
    
    private void run(String[] argssss) throws Exception
    {
// DONT RUN THE SCROLLING TEXT HERE        
//        System.out.println("Starting PIXEL activity in " + getClass().getSimpleName() + ".");		
//	ActionListener listener = new TextScroller();
//	
//	// set the IOIO loop delay to half a second, by default
//	int delay = 500; // milliseconds
//        
//	timer = new Timer(delay, listener);	
//	timer.start();
    }
    
    private void startSearchTimer()
    {
//	int delay = 1000;
	SearchTimer worker = new SearchTimer();
	searchTimer = new Timer();//delay, worker);
	searchTimer.schedule(worker, new Date());
    }
    
    private class PixelIntegration extends IOIOConsoleApp
    {
        public PixelIntegration(String [] args)
        {
            try
            {
                System.out.println("CALLING GO");
                go(args);
            } 
            catch (Exception ex)
            {
                String message = "Could not initialize Pixel: " + ex.getMessage();
                logger.log(Level.INFO, message);
            }
        }
        
        @Override
        protected void run(String[] args) throws IOException 
        {
            System.out.println("Pixel integration with delayted run() via sleep.");
            
            while(stayConnected)
            {
                long duration = 1000 * 60 * 1;
                try
                {
                    Thread.sleep(duration);
                } 
                catch (InterruptedException ex)
                {
                    String message = "Error sleeping for Pixel initialization: " + ex.getMessage();
                    logger.log(Level.INFO, message, ex);
                }
            }
            

            
            
//            InputStreamReader isr = new InputStreamReader(System.in);
//            BufferedReader reader = new BufferedReader(isr);
//            boolean abort = false;
//            String line;
//            while (!abort && (line = reader.readLine()) != null) 
//            {
//                if (line.equals("t")) 
//                {
//                    //ledOn_ = !ledOn_;
//                } 
//                else if (line.equals("q")) {
//                    abort = true;
//                    System.exit(1);
//                } 
//                else 
//                {
//                    System.out.println("Unknown input. q=quit.");
//                }
//            }            
            
            logger.log(Level.INFO, "Pixel integration: end of run");
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
                    ServletContext servletContext = getServletContext();     
                    Pixel pixel = (Pixel) servletContext.getAttribute(PIXEL_KEY);
                    pixel.matrix = ioio_.openRgbLedMatrix(pixel.KIND);
                    pixel.ioiO = ioio_;

                    StringBuilder message = new StringBuilder();
                    message.append("Found PIXEL: " + pixel.matrix + "\n");
                    message.append("You may now interact with the PIXEL\n");

    //TODO: Load something on startup

                    searchTimer.cancel();//stop(); //need to stop the timer so we don't still display the pixel searching message
                    message.append("PIXEL Status: Connected");
                    statusLabel = message.toString();

                    logger.log(Level.INFO, statusLabel);
                }
            };
                    
            return looper;
        }        
    }
    
    private class SearchTimer extends TimerTask//implements ActionListener 
    {
	final long searchPeriodLength = 45 * 1000;
	
	final long periodStart;
	
	final long periodEnd;
	
	private int dotCount = 0;
	
	String message = "Searching for PIXEL";
	
	StringBuilder label = new StringBuilder(message);
	
	public SearchTimer()
	{
	    label.insert(0, "<html><body><h2>");
	    
	    Date d = new Date();
	    periodStart = d.getTime();
	    periodEnd = periodStart + searchPeriodLength;
	}

        @Override
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

	    statusLabel = label.toString();
	    
	    Date d = new Date();
	    long now = d.getTime();
	    if(now > periodEnd)
	    {
		searchTimer.cancel();
                
                ServletContext servletContext = getServletContext();     
                Pixel pixel = (Pixel) servletContext.getAttribute(PIXEL_KEY);
		if(pixel.matrix == null)
		{
		    message = "A connection to PIXEL could not be established.";
		    String title = "PIXEL Connection Unsuccessful: ";
                    statusLabel = title + message;
                    logger.log(Level.SEVERE, statusLabel);
		}
	    }
        }
    }
}


package org.onebeartoe.web.enabled.pixel;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.pc.SerialPortIOIOConnectionBootstrap;
//import static ioio.lib.pc.SerialPortIOIOConnectionBootstrap.ledResolution_;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;
import java.awt.Color;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import org.onebeartoe.io.TextFileReader;
import org.onebeartoe.io.buffered.BufferedTextFileReader;

import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;

import org.onebeartoe.web.enabled.pixel.controllers.AnimationsHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.AnimationsListHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ArcadeListHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ClockHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.IndexHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ScrollingTextColorHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ScrollingTextHttpHander;
import org.onebeartoe.web.enabled.pixel.controllers.ScrollingTextSpeedHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.StaticFileHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.StillImageHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.StillImageListHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.UploadHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.UploadOriginHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ArcadeHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.ConsoleHttpHandler;
import org.onebeartoe.web.enabled.pixel.controllers.QuitHttpHandler;

import org.onebeartoe.pixel.LogMe;
/**
 * @author Roberto Marquez
 */
public class WebEnabledPixel
{
    //public static final Logger logger = null;
    
    public static LogMe logMe = null;

    private HttpServer server;

    private int httpPort;

    private CliPixel cli;
    
    private Timer searchTimer;
    
    private Pixel pixel;
    
    private String ledResolution_ = "";

    private static int LED_MATRIX_ID = 15;
//TODO: We shoudl invert this and have teh user specicy the matrix label 
//      (SEEEDSTUDIO_64x64, Matrix.SEEEDSTUDIO_32x32, etc...) instead of an
//      integer ID.
//      The lable makes sense if user's are copying and pasting the commands, if not then
//      integer IDs makes sense, but is harder to maintain.
    
    private static PixelEnvironment pixelEnvironment;
    
    public static RgbLedMatrix.Matrix MATRIX_TYPE ;
    
    public static boolean silentMode_ = false;
    
//TODO: MAKE THIS PRIVATE    
    public List<String> stillImageNames;

//TODO: MAKE THIS PRIVATE    
    public List<String> animationImageNames;
    
    public List<String> arcadeImageNames;
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    public static String port_ = null;
    
    private static String alreadyRunningErrorMsg = "";
    
    private static int yTextOffset = 0;
    
    private static int fontSize_ = 32;
    
    private static int speed_ = 10;
    
    private static long speed = 10L;
    
    public static String pixelwebVersion = "2.0.8";
    
    private static boolean backgroundMode_ = false;
    
    private static boolean stayConnected = true;
    
    public static boolean pixelConnected = false;
    
    public WebEnabledPixel(String[] args)
    {
        cli = new CliPixel(args);
        cli.parse();
        httpPort = cli.getWebPort();
        silentMode_ = cli.getSilentMode();
        backgroundMode_ = cli.getBackgroundMode();
        
        //Using our common logger across multiple classes
        //LogMe logMe = LogMe.getInstance();
        logMe = LogMe.getInstance();
        if (!silentMode_) {
            logMe.aLogger.info( "Pixelcade Listender (pixelweb) Version " + pixelwebVersion);
            System.out.println( "Pixelcade Listender (pixelweb) Version " + pixelwebVersion);
        }

        yTextOffset = cli.getyTextOffset();
        
        LED_MATRIX_ID = cli.getLEDMatrixType(); //let's get this from the command line class (CliPixel.java) and if there is no command line entered, we'll take the default of 3
        
        if (isWindows()) {
        
                alreadyRunningErrorMsg = "*** ERROR *** \n"
                        + "Pixel Listener (pixelweb.exe) is already running\n"
                        + "You don't need to launch it again\n"
                        + "You may also want to add the Pixel Listener to your Windows Startup Folder";
        } else {
                alreadyRunningErrorMsg = "*** ERROR *** \n"
                        + "Pixel Listener (pixelweb.jar) is already running\n"
                        + "You don't need to launch it again\n"
                        + "You may also want to add the Pixel Listener to your init.d startup";
        }
        
        //we can use the led matrix from the command line but let's override it if there is a settings.ini
         File file = new File("settings.ini");
             if (file.exists() && !file.isDirectory()) { 
                 
                Ini ini = null;
                try {
                   ini = new Ini(new File("settings.ini"));  //uses the ini4j lib
                } catch (IOException ex) {
                   logMe.aLogger.log(Level.SEVERE, "could not load settings.ini", ex);
                    if (!silentMode_) logMe.aLogger.severe("Could not open settings.ini" + ex);
                }
                //only go here if settings.ini exists
                
                ledResolution_=ini.get("PIXELCADE SETTINGS", "ledResolution"); 
                
                if (ledResolution_.equals("128x32")) {
                    if (!silentMode_) {
                        System.out.println("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                        logMe.aLogger.info("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                    }
                    LED_MATRIX_ID = 15;
                } 
                
                if (ledResolution_.equals("64x32")) {
                     if (!silentMode_) {
                        System.out.println("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                        logMe.aLogger.info("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                     }
                    LED_MATRIX_ID = 13;
                } 
                
                 if (ledResolution_.equals("32x32")) {
                     if (!silentMode_) {
                        System.out.println("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                        logMe.aLogger.info("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                     }
                    LED_MATRIX_ID = 11;
                } 
         }
        
        pixelEnvironment = new PixelEnvironment(LED_MATRIX_ID);
        
        MATRIX_TYPE = pixelEnvironment.LED_MATRIX;
        
        pixel = new Pixel(pixelEnvironment.LED_MATRIX, pixelEnvironment.currentResolution);
        
        //for the y positioning, font size, and speed on scrolling text, looks like arial works out best so we'll stick with it
        switch (LED_MATRIX_ID) {
            
            case 11: //32x32
                yTextOffset = -4;
                fontSize_ = 22;
                speed_ = 38;
                break;
            case 13: //64x32
                yTextOffset = -12;
                fontSize_ = 32;
                speed_ = 18;       //smaller the frame, faster the scrolling so slowing it down relative to 128x32
                break;
            case 15: //128x32
                yTextOffset = -12;
                fontSize_ = 32;
                speed_ = 10;
                break;
            default: 
                yTextOffset = -4;  
                fontSize_ = 22;
                speed_ = 38;
        }
        
        pixel.setyScrollingTextOffset(yTextOffset);
        pixel.setFontSize(fontSize_);
        pixel.setScrollDelay(speed_);
        pixel.setScrollTextColor(Color.red);
        
        if (!silentMode_) logMe.aLogger.info( "Pixelcade HOME DIRECTORY: " +    pixel.getPixelHome());
        
            extractDefaultContent();  //to do :  keep this file smaller, moved this to pixelcade-installer?

            loadImageLists();

            loadAnimationList();

            loadArcadeList();

            createControllers();
        }
        
    private void createControllers()
    {
        try
        {
            InetSocketAddress anyhost = new InetSocketAddress(httpPort);
            server = HttpServer.create(anyhost, 0);
            
            HttpHandler indexHttpHandler = new IndexHttpHandler();
            
            HttpHandler scrollingTextHttpHander = new ScrollingTextHttpHander(this);
            
            HttpHandler scrollingTextSpeedHttpHander = new ScrollingTextSpeedHttpHandler(this);
            
            HttpHandler scrollingTextColorHttpHandler = new ScrollingTextColorHttpHandler(this);
            
            HttpHandler staticFileHttpHandler = new StaticFileHttpHandler(this);
            
            HttpHandler stillImageHttpHandler = new StillImageHttpHandler(this) ;
            
            HttpHandler stillImageListHttpHandler = new StillImageListHttpHandler(this);
            
            HttpHandler animationsHttpHandler = new AnimationsHttpHandler(this);
            
            HttpHandler animationsListHttpHandler = new AnimationsListHttpHandler(this);
            
            HttpHandler arcadeListHttpHandler = new ArcadeListHttpHandler(this);
            
            HttpHandler consoleListHttpHandler = new ConsoleHttpHandler(this);

            HttpHandler uploadHttpHandler = new UploadHttpHandler(this);
            
            HttpHandler uploadOriginHttpHandler = new UploadOriginHttpHandler( (UploadHttpHandler) uploadHttpHandler);
            
            HttpHandler clockHttpHandler = new ClockHttpHandler(this);
            
            HttpHandler arcadeHttpHandler = new ArcadeHttpHandler(this);
            
             HttpHandler quitHttpHandler = new QuitHttpHandler(this);
            
            
// ARE WE GONNA DO ANYTHING WITH THE HttpContext OBJECTS?   
            
            HttpContext createContext =     server.createContext("/", indexHttpHandler);
            
            HttpContext animationsContext = server.createContext("/animations", animationsHttpHandler);
                                            server.createContext("/animations/list", animationsListHttpHandler);
                                            server.createContext("/animations/save", animationsListHttpHandler);
                                            
            HttpContext arcadeContext =     server.createContext("/arcade", arcadeHttpHandler);
                                            server.createContext("/quit", quitHttpHandler);
                                            server.createContext("/shutdown", quitHttpHandler);
                                            server.createContext("/arcade/list", arcadeListHttpHandler);
                                            server.createContext("/console", consoleListHttpHandler);

            HttpContext staticContent =     server.createContext("/files", staticFileHttpHandler);
            
            HttpContext  stillContext =     server.createContext("/still", stillImageHttpHandler);
                                            server.createContext("/still/list", stillImageListHttpHandler);
                                            
                                            
            HttpContext   textContext =     server.createContext("/text", scrollingTextHttpHander);
                                            server.createContext("/text/speed", scrollingTextSpeedHttpHander);
                                            server.createContext("/text/color", scrollingTextColorHttpHandler);
                                            
            HttpContext uploadContext =     server.createContext("/upload", uploadHttpHandler);
                                            server.createContext("/upload/origin", uploadOriginHttpHandler);
            
            HttpContext clockContext =      server.createContext("/clock", clockHttpHandler);
                                            
        } 
        catch (IOException ex)
        {
            //if we got here, most likely the pixel listener was already running so let's give a message and then exit gracefully
            
             System.out.println(alreadyRunningErrorMsg);
             System.out.println("Exiting...");
             
             if (isWindows() || isMac()) {  //we won't have xwindows on the Pi so skip this for the Pi
            
                JFrame frame = new JFrame("JOptionPane showMessageDialog example");  //let's show a pop up too so the user doesn't miss it
                JOptionPane.showMessageDialog(frame,
                   alreadyRunningErrorMsg,
                   "Pixelcade Listener Already Running",
                   JOptionPane.ERROR_MESSAGE);
             }
        
             System.exit(1);    //we can't continue because the pixel listener is already running
        }
    }
    
    public void extractAnimationImages() throws IOException
    {
        String animationsListFilesystemPath = pixel.getPixelHome() + "animations.text";
        File animationsListFile = new File(animationsListFilesystemPath);
        
        String pathPrefix = "animations/";
        String animationsListClasspath = "/animations.text";
        
        extractClasspathResourcesList(animationsListFile, animationsListClasspath, pathPrefix);        
    }
    
     public void extractGifSourceAnimationImages() throws IOException
    {
        String animationsListFilesystemPath = pixel.getPixelHome() + "gifsource.text";
        File animationsListFile = new File(animationsListFilesystemPath);
        
        String pathPrefix = "animations/gifsource/";
        String animationsListClasspath = "/gifsource.text";
        
        extractClasspathResourcesList(animationsListFile, animationsListClasspath, pathPrefix);        
    }
     
     public void extractArcadeConsoleGIFs() throws IOException
    {
        String animationsListFilesystemPath = pixel.getPixelHome() + "consoles.text";
        File animationsListFile = new File(animationsListFilesystemPath);
        
        //String pathPrefix = "arcade/console/";
        String pathPrefix = "console/";
        String animationsListClasspath = "/consoles.text";
        
        extractClasspathResourcesList(animationsListFile, animationsListClasspath, pathPrefix);        
    }
     
      public void extractArcadeMAMEGIFs() throws IOException
    {
        String animationsListFilesystemPath = pixel.getPixelHome() + "mame.text";
        File animationsListFile = new File(animationsListFilesystemPath);
        
        //String pathPrefix = "arcade/mame/";
        String pathPrefix = "mame/";
        String animationsListClasspath = "/mame.text";
        
        String mamePath = pixel.getPixelHome() + "mame"; //home/pixelcade/mame
        File mameDirectory = new File(mamePath);
        
        if (!mameDirectory.exists()) {  //let's skip if the mame folder is already there, it will be there on Windows because of the installer but won't be there for Pi users
            extractClasspathResourcesList(animationsListFile, animationsListClasspath, pathPrefix);   
        } else {
            String message = "Pixel app will not extract the contents of " + mamePath
                        + ".  The folder already exists";
            System.out.println(message);
        }
    }
      
       public void extractRetroPie() throws IOException
    {
        String contentClasspath = "/retropie/";
        String inpath = contentClasspath + "mame.csv";

        String pixelHomePath = pixel.getPixelHome();
        File pixelHomeDirectory = new File(pixelHomePath);
            
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "pixel-logo.txt";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "pixelc.jar";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "pixelcade.jar";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "retrogame.cfg";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "runcommand-onend.sh";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "runcommand-onstart.sh";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "shutdown_button.py";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "shutdown_button.service";
        extractClasspathResource(inpath, pixelHomeDirectory);
        
        inpath = contentClasspath + "settings.ini";
        extractClasspathResource(inpath, pixelHomeDirectory);
    }
      
  
     
       public void createArcadeDirs() throws IOException
    {
        String animationsListFilesystemPath = pixel.getPixelHome() + "arcadedirs.text";
        File animationsListFile = new File(animationsListFilesystemPath);
        
        //String pathPrefix = "arcade/";
        String pathPrefix = "";
        String animationsListClasspath = "/arcadedirs.text";
        
        extractArcadeDirs(animationsListFile, animationsListClasspath, pathPrefix);        
    }
    
    private void extractDefaultContent()
    {
        try
        {
 
            extractHtmlAndJavascript();  //web server basic files
                        
            extractStillImages();      //for the web server functions
            
            extractAnimationImages();  //for the web server functions
            
            extractGifSourceAnimationImages(); //the gifsource directory for animations
            
            if (isMac() || isUnix())  {       //extract RetroPie files if mac or Pi, we don't do this for windows as the installer takes care of these files already
                
                File settings = new File(pixel.getPixelHome() + "settings.ini");
                if (!settings.exists()) {
                    extractRetroPie();
                } 
                
                extractArcadeConsoleGIFs();
            
                extractArcadeMAMEGIFs();          //we skip this is the arcade/mame folder is already there
                
                createArcadeDirs();
            }
        } 
        catch (IOException ex)
        {
            //logger.log(Level.SEVERE, "could not extract all default content", ex);
            logMe.aLogger.log(Level.SEVERE, "could not extract all default content", ex);
        }
    }
    
// AND CSS OR RENAME!    
    private void extractHtmlAndJavascript() throws IOException
    {
        File indexHTMLFile = new File(pixel.getPixelHome() + "index.html");
        if (!indexHTMLFile.exists()) {
             String contentClasspath = "/web-content/";
            String inpath = contentClasspath + "index.html";

            String pixelHomePath = pixel.getPixelHome();
            File pixelHomeDirectory = new File(pixelHomePath);

            extractClasspathResource(inpath, pixelHomeDirectory);

            inpath = contentClasspath + "pixel.js";
            extractClasspathResource(inpath, pixelHomeDirectory);

            inpath = contentClasspath + "images.css";
            extractClasspathResource(inpath, pixelHomeDirectory);
        }
    }
    
    private void extractStillImages() throws IOException
    {
        String imagesListFilesystemPath = pixel.getPixelHome() + "images.text";
        File imagesListFile = new File(imagesListFilesystemPath);
        
        String pathPrefix = "images/";
        String imagesListClasspath = "/images.text";
        
        extractClasspathResourcesList(imagesListFile, imagesListClasspath, pathPrefix);
    }

    private void extractClasspathResource(String classpath, File parentDirectory) throws IOException
    {
        InputStream instream = getClass().getResourceAsStream(classpath);

        if( !parentDirectory.exists() )
        {
            parentDirectory.mkdirs();
        }

        int i = classpath.lastIndexOf("/") + 1;
        String outname = classpath.substring(i);
        String outpath = parentDirectory.getAbsolutePath() + File.separator + outname;
        File outfile = new File(outpath);
        
//TODO: UNCOMMENT THIS IF/ELSE WHEN YOU ARE DONE TESTING, as is, the code extracts 
//      all files every run to make Web development faster.  That is, the existing 
//      files don't need not be removed to get the latest changes extracted.
//        if( outfile.exists() )
//        {
//            logger.log(Level.INFO, "Pixel app will not extract " + classpath + ".  It already exists.");
//        }
//        else
        {
            //logger.log(Level.INFO, "Pixel app is extracting " + classpath);
            //System.out.println("Pixel is extracting " + classpath);

            FileOutputStream fos = new FileOutputStream(outfile);
            IOUtils.copy(instream, fos);
        }
    }
    
    /**
     * This method only extracts the resources to the file system if the list file
     * does not exist on the filesystem.  This is to keep from extracting the default 
     * content every time the application runs.
     */
    private void extractClasspathResourcesList(File resourceListFile, 
                                                 String resourceListClasspath,
                                                 String pathPrefix) throws IOException
    {
        if(resourceListFile.exists() )
        {
            String message = "Pixel app will not extract the contents of " + resourceListClasspath
                        + ".  The list already exists at " + resourceListFile.getAbsolutePath();
             if (!silentMode_) System.out.println(message);
        }
        else
        {
            // extract the list so on next run the app knows not to extract the default content
            extractClasspathResource(resourceListClasspath, resourceListFile);
            
            String outputDirectoryPath = "";
            
            if (pathPrefix == "") {
                outputDirectoryPath = pixel.getPixelHome();  //this means we're copying into the root of pixelcade
            } else {
                outputDirectoryPath = pixel.getPixelHome() + pathPrefix;
            }
            
            //String outputDirectoryPath = pixel.getPixelHome() + pathPrefix;
            File outputDirectory = new File(outputDirectoryPath);
            
            //to do add check if this dir already exists and skip if so
            
            TextFileReader tfr = new BufferedTextFileReader();
            List<String> imageNames = tfr.readTextLinesFromClasspath(resourceListClasspath);
            
            for(String name : imageNames)
            {
                String classpath = "/" + pathPrefix + name;
                
                 if (!silentMode_) System.out.println("Extracting " + classpath);
                
                extractClasspathResource(classpath, outputDirectory);
            }
        }
    }
    
    private void extractClasspathResourcesListRoot (File resourceListFile, 
                                                 String resourceListClasspath
                                                 ) throws IOException
    {
        if(resourceListFile.exists() )
        {
            String message = "Pixel app will not extract the contents of " + resourceListClasspath
                        + ".  The list already exists at " + resourceListFile.getAbsolutePath();
                        if (!silentMode_) System.out.println(message);
        }
        else
        {
            // extract the list so on next run the app knows not to extract the default content
            extractClasspathResource(resourceListClasspath, resourceListFile);
            
            String outputDirectoryPath = pixel.getPixelHome() ; 
            File outputDirectory = new File(outputDirectoryPath);
            
            //to do add check if this dir already exists and skip if so
            
            TextFileReader tfr = new BufferedTextFileReader();
            List<String> imageNames = tfr.readTextLinesFromClasspath(resourceListClasspath);
            
            for(String name : imageNames)
            {
               
                String classpath = name;
                
                if (!silentMode_) System.out.println("Extracting " + classpath);
                
                extractClasspathResource(classpath, outputDirectory);
            }
        }
    }
    
     private void extractArcadeDirs(File resourceListFile, 
                                                 String resourceListClasspath,
                                                 String pathPrefix) throws IOException
    {
        if(resourceListFile.exists() )
        {
            String message = "Pixel app will not extract the contents of " + resourceListClasspath
                        + ".  The list already exists at " + resourceListFile.getAbsolutePath();
                        if (!silentMode_) System.out.println(message);
        }
        else
        {
            // extract the list so on next run the app knows not to extract the default content
            extractClasspathResource(resourceListClasspath, resourceListFile);
            
            TextFileReader tfr = new BufferedTextFileReader();
            List<String> imageNames = tfr.readTextLinesFromClasspath(resourceListClasspath);
            
            for(String name : imageNames)
            { 
              
                String outputArcadeDirectoryPath = pixel.getPixelHome() +  name;
                
                File outputArcadeDirectory = new File(outputArcadeDirectoryPath);
                
                if (!silentMode_) System.out.println("Creating Arcade Directory: " + outputArcadeDirectoryPath);
                
                if( !outputArcadeDirectory.exists() )
                    {
                        outputArcadeDirectory.mkdirs();
                    }
            }
        }
    }
     
     public static int getMatrixID() {
         return  LED_MATRIX_ID;
     }
     
     public static long getScrollingTextSpeed(int LED_MATRIX_ID) {
                         
            switch (LED_MATRIX_ID) {

               case 11: //32x32
                   yTextOffset = -4;
                   fontSize_ = 22;
                   speed = 38L;
                   break;
               case 13: //64x32
                   yTextOffset = -12;
                   fontSize_ = 32;
                   speed = 18L;       //smaller the frame, faster the scrolling so slowing it down relative to 128x32
                   break;
               case 15: //128x32
                   yTextOffset = -12;
                   fontSize_ = 32;
                   speed = 10L;
                   break;
               default: 
                   yTextOffset = -4;  
                   fontSize_ = 22;
                   speed = 38L;
           }

           Pixel.setyScrollingTextOffset(yTextOffset);
           Pixel.setFontSize(fontSize_);

           return speed;
                        
     }
     
     public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
		
	}

    public Pixel getPixel()
    {
        return pixel;
    }
    
    public List<String> loadAnimationList()
    {
        try
        {
            animationImageNames = loadImageList("animations");
        } 
        catch (Exception ex)
        {
            //logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
            logMe.aLogger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
        }
        
        return animationImageNames;
    }
    
    public List<String> loadArcadeList()
    {
        try
        {
            arcadeImageNames = loadImageList("mame");
            //TO DO how to concatenate or should we not do that and have separate for each console?
            //how to modify to generic the pngs?
        } 
        catch (Exception ex)
        {
            //logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
            logMe.aLogger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
        }
        
        return arcadeImageNames;
    }
    
    private List<String> loadImageList(String directoryName) throws Exception  //TO DO alphabetize this list
    {
        String dirPath = pixel.getPixelHome() + directoryName;
        File parent = new File(dirPath);
        
        List<String> namesList = new ArrayList();
        
        if( !parent.exists() || !parent.isDirectory() )
        {
            String message = "The directory is not valid:" +
                             dirPath + "\n" + 
                             "exists: " + parent.exists() + "\n" + 
                             "directory: " + parent.isDirectory();
            throw new Exception(message);
        }
        else
        {
            String [] names = parent.list( new FilenameFilter()
            {

                @Override
                public boolean accept(File dir, String name)
                {
                    return name.toLowerCase().endsWith(".png") || 
                            name.toLowerCase().endsWith(".gif");
                }
            });
            
            List<String> list = Arrays.asList(names);
            namesList.addAll(list);
        }
        
        java.util.Collections.sort(namesList); //sorting the list alphabetical
        return namesList;
    }
    
    public List<String> loadImageLists()
    {
        try
        {        
            stillImageNames = loadImageList("images");
        } 
        catch (Exception ex)
        {
            //logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
            logMe.aLogger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
        }
        
        return stillImageNames;
    }
    
    public static void main(String[] args)
    {
        
        WebEnabledPixel app = new WebEnabledPixel(args);
        app.startServer();
    }

    public void setPixel(Pixel pixel)
    {
        this.pixel = pixel;
    }  

    private void startSearchTimer()
    {
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
//TODO: ONCE USING THE PIXelINTEGRATRION FROM PIXEL-COMMONS
//      CALL ITS addXxxxxListeners() methods
//      AND then its initialize() method
    }
    
     
    /* public String getPixelResolution()
        {
            return ledResolution_;
        }
     */ 
    
       
    /**
     * @deprecated Use the version in pixel-commons from Alinke's github.com repository.
     */
    @Deprecated
    private class PixelIntegration extends IOIOConsoleApp
    {
        public PixelIntegration()
        {
            try
            {
                 if (!silentMode_) System.out.println("PixelIntegration is calling go()");
                
                go(null);
            } 
            catch (Exception ex)
            {
                String message = "Could not initialize Pixel: " + ex.getMessage();
                //logger.log(Level.INFO, message);
                logMe.aLogger.info(message);
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
            
            if (backgroundMode_) {      //if this block isn't here, java -jar pixelweb.jar & doesn't work in Linux
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
                              
                            }
                        }
		}
            else {
            
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
                        logMe.aLogger.severe(message);
                }

                @Override
                public void incompatible() 
                {
                    String message = "Incompatible Firmware Detected";
                    System.out.println(message);
                    logMe.aLogger.severe(message);
                }

                @Override
                protected void setup() throws ConnectionLostException, InterruptedException
                {
//                  pixel = new Pixel(pixelEnvironment.LED_MATRIX, pixelEnvironment.currentResolution);
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
                    
                    message.append("You may now interact with PIXEL!\n");
                    message.append("LED matrix type is: " + LED_MATRIX_ID +"\n");
                    

                    //TODO: Load something on startup

                    searchTimer.cancel(); //need to stop the timer so we don't still display the pixel searching message
                    
                    message.append("PIXEL Status: Connected");
                    pixelConnected = true;
                    
                    //we just connected so let's let's check the Q and see if anything was written to it while we were searching for the board
                    if (!pixel.PixelQueue.isEmpty()) {
                        pixel.doneLoopingCheckQueue();
                         if (!silentMode_)  {
                            System.out.println("Processing Startup Queue Items...");
                            logMe.aLogger.info("Processing Startup Queue Items...");
                        }
                    } else {
                        if (!silentMode_)  {
                            System.out.println("No Items in the Queue at Startup...");
                            logMe.aLogger.info("No Items in the Queue at Startup...");
                        }
                    }
                    
                    //we need to check if there was anything written to the Q before we connected
                   
                     if (!silentMode_)  {
                         System.out.println(message);
                         logMe.aLogger.info(message.toString());
                     }
                     
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
                    //logger.log(Level.SEVERE, message);
                    logMe.aLogger.severe(message);
		}
                else
                {
                    //logger.log(Level.INFO, "Looks like we have a PIXEL connection!");
                     if (!silentMode_) logMe.aLogger.info("Looks like we have a PIXEL connection!");
                }
	    }
	}        
    }    
}

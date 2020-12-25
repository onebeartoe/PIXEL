
package org.onebeartoe.installer.cade.pixel;


import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.pc.SerialPortIOIOConnectionBootstrap;
//import static ioio.lib.pc.SerialPortIOIOConnectionBootstrap.ledResolution_;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOConsoleApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.InetSocketAddress;
import java.net.MalformedURLException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import org.onebeartoe.io.TextFileReader;
import org.onebeartoe.io.buffered.BufferedTextFileReader;

import org.onebeartoe.pixel.PixelEnvironment;
import org.onebeartoe.pixel.hardware.Pixel;


public class installer
{
    protected Logger logger;

    private int httpPort;

    private CliPixel cli;
    
    private Timer searchTimer;
    
    private Pixel pixel;
    
    private String ledResolution_ = "";

    private static int LED_MATRIX_ID = 11;
    
    private static PixelEnvironment pixelEnvironment;
    
    public  static RgbLedMatrix.Matrix MATRIX_TYPE ;
    
//TODO: MAKE THIS PRIVATE    
    public List<String> stillImageNames;

//TODO: MAKE THIS PRIVATE    
    public List<String> animationImageNames;
    
    public List<String> arcadeImageNames;
    
    public static String OS = System.getProperty("os.name").toLowerCase();
    
    public static String port_ = null;
    
    private static String alreadyRunningErrorMsg = "";
    
   
    public installer(String[] args)
    {
        cli = new CliPixel(args);
        cli.parse();
        httpPort = cli.getWebPort();

        String name = getClass().getName();
        logger = Logger.getLogger(name);

        
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
                   Logger.getLogger(SerialPortIOIOConnectionBootstrap.class.getName()).log(Level.SEVERE, null, ex);
                }
                //only go here if settings.ini exists
                
                ledResolution_=ini.get("PIXELCADE SETTINGS", "ledResolution"); 
                
                if (ledResolution_.equals("128x32")) {
                    System.out.println("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                    LED_MATRIX_ID = 15;
                } 
                
                if (ledResolution_.equals("64x32")) {
                    System.out.println("PIXEL resolution found in settings.ini: resolution=" + ledResolution_);
                    LED_MATRIX_ID = 13;
                } 
         }
        
        //extractDefaultContent();
        
          try
        {
            extractHtmlAndJavascript();  //web server basic files
                        
            extractStillImages();      //for the web server functions
            
            extractAnimationImages();  //for the web server functions
            
            extractGifSourceAnimationImages(); //the gifsource directory for animations
            
            if (isMac() || isUnix())  {       //extract RetroPie files if mac or Pi, we don't do this for windows as the installer takes care of these files already
                
                extractRetroPie();
                
                extractArcadeConsoleGIFs();
            
                extractArcadeMAMEGIFs();          //we skip this is the arcade/mame folder is already there
                
                createArcadeDirs();
            }
        } 
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "could not extract all default content", ex);
        }
        
        loadImageLists();
        
        loadAnimationList();
        
        loadArcadeList();
        
        
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
        
        inpath = contentClasspath + "testwrite.sh";
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
                
                extractRetroPie();
                
                extractArcadeConsoleGIFs();
            
                extractArcadeMAMEGIFs();          //we skip this is the arcade/mame folder is already there
                
                createArcadeDirs();
            }
        } 
        catch (IOException ex)
        {
            logger.log(Level.SEVERE, "could not extract all default content", ex);
        }
    }
    
// AND CSS OR RENAME!    
    private void extractHtmlAndJavascript() throws IOException
    {
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
            //logger.log(Level.INFO, message); //logger adds a timestamp line that we don't want
            System.out.println(message);
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
                
                System.out.println("Extracting " + classpath);
                
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
            //logger.log(Level.INFO, message); //logger adds a timestamp line that we don't want
            System.out.println(message);
        }
        else
        {
            // extract the list so on next run the app knows not to extract the default content
            extractClasspathResource(resourceListClasspath, resourceListFile);
            
       //     inpath = contentClasspath + "pixel.js";
       // extractClasspathResource(inpath, pixelHomeDirectory);
        
       // inpath = contentClasspath + "images.css";
       // extractClasspathResource(inpath, pixelHomeDirectory);
            
            String outputDirectoryPath = pixel.getPixelHome() ; 
            File outputDirectory = new File(outputDirectoryPath);
            
            //to do add check if this dir already exists and skip if so
            
            TextFileReader tfr = new BufferedTextFileReader();
            List<String> imageNames = tfr.readTextLinesFromClasspath(resourceListClasspath);
            
            for(String name : imageNames)
            {
               // String classpath = "/" + pathPrefix + name;
                
                String classpath = name;
                
                System.out.println("Extracting " + classpath);
                
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
            //logger.log(Level.INFO, message); //logger adds a timestamp line that we don't want
            System.out.println(message);
        }
        else
        {
            // extract the list so on next run the app knows not to extract the default content
            extractClasspathResource(resourceListClasspath, resourceListFile);
            
            //String outputDirectoryPath = pixel.getPixelHome() + pathPrefix; //home/arcade
            //String outputDirectoryPath = pixel.getPixelHome(); //home/arcade
            //File outputDirectory = new File(outputDirectoryPath);
            
            TextFileReader tfr = new BufferedTextFileReader();
            List<String> imageNames = tfr.readTextLinesFromClasspath(resourceListClasspath);
            
            for(String name : imageNames)
            { 
                //String outputArcadeDirectoryPath = pixel.getPixelHome() + pathPrefix + name;
                String outputArcadeDirectoryPath = pixel.getPixelHome() +  name;
                
                File outputArcadeDirectory = new File(outputArcadeDirectoryPath);
                
                System.out.println("Creating Arcade Directory: " + outputArcadeDirectoryPath);
                
                if( !outputArcadeDirectory.exists() )
                    {
                        outputArcadeDirectory.mkdirs();
                    }
                
               // new File(dirName).mkdir();
                //extractClasspathResource(classpath, outputDirectory);
            }
        }
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

    
    
    public List<String> loadAnimationList()
    {
        try
        {
            animationImageNames = loadImageList("animations");
        } 
        catch (Exception ex)
        {
            logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
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
            logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
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
            logger.log(Level.SEVERE, "could not load image resources on the filesystem", ex);
        }
        
        return stillImageNames;
    }
    
    public static void main(String[] args)
    {
        
        //installer app = new installer(args);
        //app.startServer();
    }
 
      
}

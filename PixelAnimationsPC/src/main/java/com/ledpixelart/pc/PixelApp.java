
package com.ledpixelart.pc;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.pc.IOIOSwingApp;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;
import javax.swing.JFileChooser;

import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.ImageIcon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class PixelApp extends IOIOSwingApp implements ActionListener 
{

    private static int width_original;
    
    private static int height_original;
    
    private static short[] frame_;
    
    private static byte[] BitmapBytes;
    
    private Logger logger;
    
    private static RgbLedMatrix matrix_;
    
    private static RgbLedMatrix.Matrix KIND;
    
    private static InputStream BitmapInputStream;

    private static ActionListener animateTimer = null;

    private JFileChooser userDirectoryChooser;
    
    private JPanel userPanel;
    
    private PixelTilePanel userTilePanel;
    
    private static BufferedImage originalImage;

    private static BufferedImage ResizedImage;

    private static int i = 0;
    private static boolean pixelFound = false;
    private static int numFrames = 0;
    private static String framestring;
    protected JButton b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16, b17, b18, b19, b20, b21, b22, b23,
	    b24, b25, b26, b27, b28, b29, b30, b31, b32, b33, b34, b35, b36, b37, b38, b39, b40;
    private static URL buttonImageURL;
    private static String animation_name;


    private int selectedFileTotalFrames;
    private int selectedFileDelay;

    private Timer timer;
    private static String decodedDirPath;
    private static String selectedFileName;
    
    private static InputStream decodedFile;
    
    private static BufferedReader br;
    
    private static String line;
    private static String fdelim;
    private static String fileAttribs;
    private static String[] fileAttribs2;
    
    public PixelApp()
    {
	logger = Logger.getLogger(PixelApp.class.getName());//.log(Level.SEVERE, message, ex);	
        
        String path = System.getProperty("user.home");
//        File homeDir = new File(path);
//        userDirectoryChooser = new JFileChooser(homeDir);
        userDirectoryChooser = new JFileChooser();
        userDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public static void main(String[] args) throws Exception 
    {
	new PixelApp().go(args); //ui stuff

	KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
	frame_ = new short[KIND.width * KIND.height];
	BitmapBytes = new byte[KIND.width * KIND.height * 2]; //512 * 2 = 1024 or 1024 * 2 = 2048

	//************ this part of code writes to the LED matrix in code without any external file *********
	//  writeTest(); //this just writes a test pattern to the LEDs in code without using any external file, uncomment out this line if you want to see that and then comment out the next two lines
	//***************************************************************************************************

	///************ this set of code loads a raw RGB565 image *****************
	// BitmapInputStream = PixelAlbumPC.class.getClassLoader().getResourceAsStream("images/gumball.rgb565"); //loads in a raw file in rgb565 format, use the windows program paint.net with the rgb565 plug-in to convert png, jpg, etc to this format
	//BitmapInputStream = PixelAlbumPC.class.getClassLoader().getResourceAsStream("images/ginseng.rgb565"); 
	// loadRGB565();
	//**************************************************************************

	animateTimer = new ActionListener() 
	{
	    public void actionPerformed(ActionEvent evt) 
	    {
		if (!pixelFound) 
		{  
		    //only go here if PIXEL wa found, other leave the timer
		    return;
		}
		// System.out.println("animate");

		i++;

		if (i >= numFrames - 1) 
		{
		    i = 0;
		}

		// framestring = "animations/decoded/boat/boat" + i + ".rgb565";
		framestring = "animations/decoded/" + animation_name + "/" + animation_name + i + ".rgb565";
		try 
		{
		    loadRGB565(framestring);
		} 
		catch (ConnectionLostException e1) 
		{
		    // TODO Auto-generated catch block
		    e1.printStackTrace();
		}

		// if (i == numFrames - 1) {
		// Animate.restart();
		//  }

	    }
	};
    }

    private static void loadRGB565(String raw565ImagePath) throws ConnectionLostException {

	BitmapInputStream = PixelApp.class.getClassLoader().getResourceAsStream(raw565ImagePath);

	try 
	{   
	    int n = BitmapInputStream.read(BitmapBytes, 0, BitmapBytes.length); // reads
	    // the
	    // input
	    // stream
	    // into
	    // a
	    // byte
	    // array
	    Arrays.fill(BitmapBytes, n, BitmapBytes.length, (byte) 0);
	} 
	catch (IOException e) 
	{
	    e.printStackTrace();
	}

	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}

	matrix_.frame(frame_);

    }

    private static void loadRGB565PNG() throws ConnectionLostException 
    {
	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}

	matrix_.frame(frame_);
    }

    private static void WriteImagetoMatrix(String imagePath) throws ConnectionLostException 
    {  
	//here we'll take a PNG, BMP, or whatever and convert it to RGB565 via a canvas, also we'll re-size the image if necessary
	
	URL url = PixelApp.class.getClassLoader().getResource(imagePath);

	try 
	{
	    originalImage = ImageIO.read(url);
	    width_original = originalImage.getWidth();
	    height_original = originalImage.getHeight();

	    if (width_original != KIND.width || height_original != KIND.height) 
	    {  
		//the image is not the right dimensions, ie, 32px by 32px				
		ResizedImage = new BufferedImage(KIND.width, KIND.height, originalImage.getType());
		Graphics2D g = ResizedImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(originalImage, 0, 0, KIND.width, KIND.height, 0, 0, originalImage.getWidth(), originalImage.getHeight(), null);
		g.dispose();
		originalImage = ResizedImage;		
	    }

	    int numByte = 0;
	    int i = 0;
	    int j = 0;

	    for (i = 0; i < KIND.height; i++) 
	    {
		for (j = 0; j < KIND.width; j++) 
		{
		    Color c = new Color(originalImage.getRGB(j, i));  //i and j were reversed which was rotationg the image by 90 degrees
		    int aRGBpix = originalImage.getRGB(j, i);  //i and j were reversed which was rotationg the image by 90 degrees
		    int alpha;
		    int red = c.getRed();
		    int green = c.getGreen();
		    int blue = c.getBlue();

		    //RGB565
		    red = red >> 3;
		    green = green >> 2;
		    blue = blue >> 3;
		    //A pixel is represented by a 4-byte (32 bit) integer, like so:
		    //00000000 00000000 00000000 11111111
		    //^ Alpha  ^Red     ^Green   ^Blue
		    //Converting to RGB565

		    short pixel_to_send = 0;
		    int pixel_to_send_int = 0;
		    pixel_to_send_int = (red << 11) | (green << 5) | (blue);
		    pixel_to_send = (short) pixel_to_send_int;

		    //dividing into bytes
		    byte byteH = (byte) ((pixel_to_send >> 8) & 0x0FF);
		    byte byteL = (byte) (pixel_to_send & 0x0FF);

		    //Writing it to array - High-byte is the first

		    BitmapBytes[numByte + 1] = byteH;
		    BitmapBytes[numByte] = byteL;
		    numByte += 2;
		}
	    }
	} 
	catch (IOException e) 
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	loadRGB565PNG();
    }

    private static byte[] Image2Byte(BufferedImage image) 
    {
	WritableRaster raster = image.getRaster();
	DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
	
	return buffer.getData();
    }

    public static byte[] extractBytes(BufferedImage image) throws IOException 
    {

	// get DataBufferBytes from Raster
	WritableRaster raster = image.getRaster();
	DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

	return (data.getData());
    }

    //return file (image) names in the chosen directory
    public ArrayList<String> getFileNames() 
    {
	//widget to let users select a directory or file
	JFileChooser chooser = new JFileChooser();
	//holds all file (image) names in the chosen directory
	ArrayList<String> myArr = new ArrayList<String>();

	//only allow directory selection
	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	//current directory is set
	chooser.setCurrentDirectory(new java.io.File("."));

	//pops up file chooser dialog, user chooses a directory
	int returnVal = chooser.showOpenDialog(null);

	//if the selected option was approved
	if (returnVal == JFileChooser.APPROVE_OPTION) 
	{
	    //directory object
	    File folder = chooser.getSelectedFile();
	    //directory string
	    String directory = chooser.getSelectedFile() + "\\";
	    //list files objects in the directory object
	    File[] listOfFiles = folder.listFiles();

	    //put all the names of the file objects into myArr
	    for (int i = 0; i < listOfFiles.length; i++) 
	    {
		if (listOfFiles[i].isFile()) {
		    myArr.add(directory + listOfFiles[i].getName());
		}//end inner if
	    }//end for         
	}//end outer if
	//else no selection was made
	else {
	    System.out.println("No Selection ");
	}//end else

	return myArr;
    }

    private static void writeTest() 
    {
	for (int i = 0; i < frame_.length; i++) 
	{
	    //	frame_[i] = (short) (((short) 0x00000000 & 0xFF) | (((short) (short) 0x00000000 & 0xFF) << 8));  //all black
	    frame_[i] = (short) (((short) 0xFFF5FFB0 & 0xFF) | (((short) (short) 0xFFF5FFB0 & 0xFF) << 8));  //pink
	    //frame_[i] = (short) (((short) 0xFFFFFFFF & 0xFF) | (((short) (short) 0xFFFFFFFF & 0xFF) << 8));  //all white
	}
    }    

    @Override
    protected Window createMainWindow(String args[]) 
    {
	try 
	{
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} 
	catch (Exception ex) 
	{
	    String message = "An error occured while setting the native look and feel.";
	    logger.log(Level.SEVERE, message, ex);	
	}	

	JFrame frame = new JFrame("Click an Animation");
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);	
	
	GridLayout experimentLayout = new GridLayout(0, 5);
	
	Container imagesPanel = new JPanel();
	imagesPanel.setLayout(experimentLayout);

	experimentLayout.setHgap(5);	
	experimentLayout.setVgap(5);		

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/0rain.png");
	ImageIcon irain = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/arrows.png");
	ImageIcon iarrows = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/boat.png");
	ImageIcon iboat = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/bubbles.png");
	ImageIcon ibubbles = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/colortiles.png");
	ImageIcon icolortiles = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/crosshatch.png");
	ImageIcon icrosshatch = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/earth.png");
	ImageIcon iearth = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/farmer.png");
	ImageIcon ifarmer = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/fire.png");
	ImageIcon ifire = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/fliptile.png");
	ImageIcon ifliptile = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/flow.png");
	ImageIcon iflow = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/float.png");
	ImageIcon ifloat = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/fuji.png");
	ImageIcon ifuji = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/lines.png");
	ImageIcon ilines = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/orangeball.png");
	ImageIcon iorangeball = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/pacman.png");
	ImageIcon ipacman = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/paoloworm.png");
	ImageIcon ipaoloworm = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/pattern.png");
	ImageIcon ipattern = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rainfast.png");
	ImageIcon irainfast = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rshifter.png");
	ImageIcon irshifter = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rspray.png");
	ImageIcon irspray = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rstarburst.png");
	ImageIcon irstarburst = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rstarfield.png");
	ImageIcon irstarfield = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rwaterflow.png");
	ImageIcon irwaterflow = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/rwhiteball.png");
	ImageIcon irwhiteball = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sboxergreen.png");
	ImageIcon isboxergreen = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sboxerpink.png");
	ImageIcon isboxerpink = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/scmakeout.png");
	ImageIcon iscmakeout = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/screddance.png");
	ImageIcon iscreddance = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/scrowd.png");
	ImageIcon iscrowd = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sfighting.png");
	ImageIcon isfighting = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sgorangedancer.png");
	ImageIcon isgorangedancer = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sgreendancer.png");
	ImageIcon isgreendancer = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sjumpblue.png");
	ImageIcon isjumpblue = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sjumppink.png");
	ImageIcon isjumppink = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/sponytail.png");
	ImageIcon isponytail = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/spraying.png");
	ImageIcon ispraying = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/ybikini.png");
	ImageIcon iybikini = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/zaquarium.png");
	ImageIcon izaquarium = new ImageIcon(buttonImageURL);

	buttonImageURL = PixelApp.class.getClassLoader().getResource("images/zarcade.png");
	ImageIcon izarcade = new ImageIcon(buttonImageURL);

	b1 = new JButton("", irain);	
	b1.setMnemonic(KeyEvent.VK_A);
	b1.setActionCommand("0rain");

	b2 = new JButton("", iarrows);	
	b2.setMnemonic(KeyEvent.VK_B);
	b2.setActionCommand("arrows");

	b3 = new JButton("", iboat);
	b3.setMnemonic(KeyEvent.VK_C);
	b3.setActionCommand("boat");

	b4 = new JButton("", ibubbles);
	b4.setMnemonic(KeyEvent.VK_C);
	b4.setActionCommand("bubbles");

	b5 = new JButton("", icolortiles);
	b5.setMnemonic(KeyEvent.VK_C);
	b5.setActionCommand("colortiles");

	b6 = new JButton("", icrosshatch);
	b6.setMnemonic(KeyEvent.VK_C);
	b6.setActionCommand("crosshatch");

	b7 = new JButton("", iearth);
	b7.setMnemonic(KeyEvent.VK_C);
	b7.setActionCommand("earth");

	b8 = new JButton("", ifarmer);
	b8.setMnemonic(KeyEvent.VK_C);
	b8.setActionCommand("farmer");

	b9 = new JButton("", ifire);
	b9.setMnemonic(KeyEvent.VK_C);
	b9.setActionCommand("fire");

	b10 = new JButton("", ifliptile);
	b10.setMnemonic(KeyEvent.VK_C);
	b10.setActionCommand("fliptile");

	b11 = new JButton("", ifloat);
	b11.setMnemonic(KeyEvent.VK_C);
	b11.setActionCommand("float");

	b12 = new JButton("", iflow);
	b12.setMnemonic(KeyEvent.VK_C);
	b12.setActionCommand("flow");

	b13 = new JButton("", ifuji);
	b13.setMnemonic(KeyEvent.VK_C);
	b13.setActionCommand("fuji");

	b14 = new JButton("", ilines);
	b14.setMnemonic(KeyEvent.VK_C);
	b14.setActionCommand("lines");

	b15 = new JButton("", iorangeball);
	b15.setMnemonic(KeyEvent.VK_C);
	b15.setActionCommand("orangeball");

	b16 = new JButton("", ipacman);
	b16.setMnemonic(KeyEvent.VK_C);
	b16.setActionCommand("pacman");

	b17 = new JButton("", ipaoloworm);
	b17.setMnemonic(KeyEvent.VK_C);
	b17.setActionCommand("paoloworm");

	b18 = new JButton("", ipattern);
	b18.setMnemonic(KeyEvent.VK_C);
	b18.setActionCommand("pattern");

	b19 = new JButton("", irainfast);
	b19.setMnemonic(KeyEvent.VK_C);
	b19.setActionCommand("rainfast");

	b20 = new JButton("", irshifter);
	b20.setMnemonic(KeyEvent.VK_C);
	b20.setActionCommand("rshifter");

	b21 = new JButton("", irspray);
	b21.setMnemonic(KeyEvent.VK_C);
	b21.setActionCommand("rspray");

	b22 = new JButton("", irstarburst);
	b22.setMnemonic(KeyEvent.VK_C);
	b22.setActionCommand("rstarburst");

	b23 = new JButton("", irstarfield);
	b23.setMnemonic(KeyEvent.VK_C);
	b23.setActionCommand("rstarfield");

	b24 = new JButton("", irwaterflow);
	b24.setMnemonic(KeyEvent.VK_C);
	b24.setActionCommand("rwaterflow");

	b25 = new JButton("", irwhiteball);
	b25.setMnemonic(KeyEvent.VK_C);
	b25.setActionCommand("rwhiteball");

	b26 = new JButton("", isboxergreen);
	b26.setMnemonic(KeyEvent.VK_C);
	b26.setActionCommand("sboxergreen");

	b27 = new JButton("", isboxerpink);
	b27.setMnemonic(KeyEvent.VK_C);
	b27.setActionCommand("sboxerpink");

	b28 = new JButton("", iscmakeout);
	b28.setMnemonic(KeyEvent.VK_C);
	b28.setActionCommand("scmakeout");

	b29 = new JButton("", iscreddance);
	b29.setMnemonic(KeyEvent.VK_C);
	b29.setActionCommand("screddance");

	b30 = new JButton("", iscrowd);
	b30.setMnemonic(KeyEvent.VK_C);
	b30.setActionCommand("scrowd");

	b31 = new JButton("", isfighting);
	b31.setMnemonic(KeyEvent.VK_C);
	b31.setActionCommand("sfighting");

	b32 = new JButton("", isgorangedancer);
	b32.setMnemonic(KeyEvent.VK_C);
	b32.setActionCommand("sgorangedancer");

	b33 = new JButton("", isgreendancer);
	b33.setMnemonic(KeyEvent.VK_C);
	b33.setActionCommand("sgreendancer");

	b34 = new JButton("", isjumpblue);
	b34.setMnemonic(KeyEvent.VK_C);
	b34.setActionCommand("sjumpblue");

	b35 = new JButton("", isjumppink);
	b35.setMnemonic(KeyEvent.VK_C);
	b35.setActionCommand("sjumppink");

	b36 = new JButton("", isponytail);
	b36.setMnemonic(KeyEvent.VK_C);
	b36.setActionCommand("sponytail");

	b37 = new JButton("", ispraying);
	b37.setMnemonic(KeyEvent.VK_C);
	b37.setActionCommand("spraying");

	b38 = new JButton("", iybikini);
	b38.setMnemonic(KeyEvent.VK_C);
	b38.setActionCommand("ybikini");

	b39 = new JButton("", izaquarium);
	b39.setMnemonic(KeyEvent.VK_C);
	b39.setActionCommand("zaquarium");

	b40 = new JButton("", izarcade);
	b40.setMnemonic(KeyEvent.VK_C);
	b40.setActionCommand("zarcade");
/*
	b1.setEnabled(false);
	b1.setEnabled(false);
	b2.setEnabled(false);
	b3.setEnabled(false);
	b4.setEnabled(false);
	b5.setEnabled(false);
	b6.setEnabled(false);
	b7.setEnabled(false);
	b8.setEnabled(false);
	b9.setEnabled(false);
	b10.setEnabled(false);
	b11.setEnabled(false);
	b12.setEnabled(false);
	b13.setEnabled(false);
	b14.setEnabled(false);
	b15.setEnabled(false);
	b16.setEnabled(false);
	b17.setEnabled(false);
	b18.setEnabled(false);
	b19.setEnabled(false);
	b20.setEnabled(false);
	b21.setEnabled(false);
	b22.setEnabled(false);
	b23.setEnabled(false);
	b24.setEnabled(false);
	b25.setEnabled(false);
	b26.setEnabled(false);
	b27.setEnabled(false);
	b28.setEnabled(false);
	b29.setEnabled(false);
	b30.setEnabled(false);
	b31.setEnabled(false);
	b32.setEnabled(false);
	b33.setEnabled(false);
	b34.setEnabled(false);
	b35.setEnabled(false);
	b36.setEnabled(false);
	b37.setEnabled(false);
	b38.setEnabled(false);
	b39.setEnabled(false);
	b40.setEnabled(false);
*/
	b1.addActionListener(this);
	b2.addActionListener(this);
	b3.addActionListener(this);
	b4.addActionListener(this);
	b5.addActionListener(this);
	b6.addActionListener(this);
	b7.addActionListener(this);
	b8.addActionListener(this);
	b9.addActionListener(this);
	b10.addActionListener(this);
	b11.addActionListener(this);
	b12.addActionListener(this);
	b13.addActionListener(this);
	b14.addActionListener(this);
	b15.addActionListener(this);
	b16.addActionListener(this);
	b17.addActionListener(this);
	b18.addActionListener(this);
	b19.addActionListener(this);
	b20.addActionListener(this);
	b21.addActionListener(this);
	b22.addActionListener(this);
	b23.addActionListener(this);
	b24.addActionListener(this);
	b25.addActionListener(this);
	b26.addActionListener(this);
	b27.addActionListener(this);
	b28.addActionListener(this);
	b29.addActionListener(this);
	b30.addActionListener(this);
	b31.addActionListener(this);
	b32.addActionListener(this);
	b33.addActionListener(this);
	b34.addActionListener(this);
	b35.addActionListener(this);
	b36.addActionListener(this);
	b37.addActionListener(this);
	b38.addActionListener(this);
	b39.addActionListener(this);
	b40.addActionListener(this);

	imagesPanel.add(b1);
	imagesPanel.add(b2);
	imagesPanel.add(b3);
	imagesPanel.add(b4);
	imagesPanel.add(b5);
	imagesPanel.add(b6);
	imagesPanel.add(b7);
	imagesPanel.add(b8);
	imagesPanel.add(b9);
	imagesPanel.add(b10);
	imagesPanel.add(b11);
	imagesPanel.add(b12);
	imagesPanel.add(b13);
	imagesPanel.add(b14);
	imagesPanel.add(b15);
	imagesPanel.add(b16);
	imagesPanel.add(b17);
	imagesPanel.add(b18);
	imagesPanel.add(b19);
	imagesPanel.add(b20);
	imagesPanel.add(b21);
	imagesPanel.add(b22);
	imagesPanel.add(b23);
	imagesPanel.add(b24);
	imagesPanel.add(b25);
	imagesPanel.add(b26);
	imagesPanel.add(b27);
	imagesPanel.add(b28);

	imagesPanel.add(b29);
	imagesPanel.add(b30);
	imagesPanel.add(b31);
	imagesPanel.add(b32);
	imagesPanel.add(b33);
	imagesPanel.add(b34);
	imagesPanel.add(b35);
	imagesPanel.add(b36);
	imagesPanel.add(b37);
	imagesPanel.add(b38);
	imagesPanel.add(b39);
	imagesPanel.add(b40);	
	
	JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");
        
        tabbedPane.addTab("Images", icon, imagesPanel, "Load built-in images.");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
	
	PixelTilePanel imagesPanelReal = new ImageTilePanel();
	imagesPanelReal.populate();
	tabbedPane.addTab("Images - real", icon, imagesPanelReal, "Load built-in images.");
        
	PixelTilePanel animationsPanel = new AnimationsPanel();
	animationsPanel.populate();;
        tabbedPane.addTab("Animations", icon, animationsPanel, "Does twice as much nothing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("Interactive", icon, panel3, "Still does nothing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
                
        userPanel = new JPanel();	
	userPanel.setLayout( new BorderLayout() );
	JButton userButton = new JButton("Browse");
        userButton.addActionListener( new UserButtonListener() );
        userPanel.add(userButton, BorderLayout.NORTH);
	String path = System.getProperty("user.home");
        File homeDirectory = new File(path);
	userTilePanel = new UserProvidedPanel(homeDirectory);
	userTilePanel.populate();
	userPanel.add(userTilePanel, BorderLayout.CENTER);
        tabbedPane.addTab("User Defined", icon, userPanel, "Does nothing at all");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);        
        
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	frame.add(tabbedPane, BorderLayout.CENTER);	
	frame.setSize(500, 450);
	
	// center it
	frame.setLocationRelativeTo(null); 
	
	frame.setVisible(true);
	
	return frame;
    }

/* delte */    
     protected JPanel makeTextPanel(String text) 
     {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = PixelApp.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
    
    private void enableButtons() 
    {
	b1.setEnabled(true);
	b1.setEnabled(true);
	b2.setEnabled(true);
	b3.setEnabled(true);
	b4.setEnabled(true);
	b5.setEnabled(true);
	b6.setEnabled(true);
	b7.setEnabled(true);
	b8.setEnabled(true);
	b9.setEnabled(true);
	b10.setEnabled(true);
	b11.setEnabled(true);
	b12.setEnabled(true);
	b13.setEnabled(true);
	b14.setEnabled(true);
	b15.setEnabled(true);
	b16.setEnabled(true);
	b17.setEnabled(true);
	b18.setEnabled(true);
	b19.setEnabled(true);
	b20.setEnabled(true);
	b21.setEnabled(true);
	b22.setEnabled(true);
	b23.setEnabled(true);
	b24.setEnabled(true);
	b25.setEnabled(true);
	b26.setEnabled(true);
	b27.setEnabled(true);
	b28.setEnabled(true);
	b29.setEnabled(true);
	b30.setEnabled(true);
	b31.setEnabled(true);
	b32.setEnabled(true);
	b33.setEnabled(true);
	b34.setEnabled(true);
	b35.setEnabled(true);
	b36.setEnabled(true);
	b37.setEnabled(true);
	b38.setEnabled(true);
	b39.setEnabled(true);
	b40.setEnabled(true);
    }

    @Override
    public IOIOLooper createIOIOLooper(String connectionType, Object extra) 
    {
	return new BaseIOIOLooper() 
	{
	    private DigitalOutput led_;

	    @Override
	    protected void setup() throws ConnectionLostException,
		    InterruptedException 
	    {
		led_ = ioio_.openDigitalOutput(IOIO.LED_PIN, true);
		matrix_ = ioio_.openRgbLedMatrix(KIND);
		pixelFound = true;
		System.out.println("Found PIXEL\n");
		System.out.println("You may now select one of the animations\n");
		enableButtons();
		loadRGB565("images/selectpic32.rgb565"); //show select picture on PIXEL	
	    }	    
	};
    }

    @Override
    public void actionPerformed(ActionEvent event) 
    {
	selectedFileName = event.getActionCommand();
	decodedDirPath = "animations/decoded";

	//System.out.println("selected file name: " + selectedFileName);

	decodedFile = PixelApp.class.getClassLoader().getResourceAsStream(decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + ".txt"); //decoded/rain/rain.text
	//note can't use file operator here as you can't reference files from a jar file

	if (decodedFile != null) 
	{
	    // ok good, now let's read it, we need to get the total numbers of frames and the frame speed

	    try {
		//  BufferedReader br = new BufferedReader(new FileReader(decodedFile));
		br = new BufferedReader(
			new InputStreamReader(decodedFile));

		line = br.readLine();

		// while ((line = br.readLine()) != null) {
		//     text.append(line);
		//    text.append('\n');	   	             
		// }
	    } catch (IOException e) {
		//You'll need to add proper error handling here
	    }

	    fileAttribs = line.toString();  //now convert to a string	 
	    //System.out.println(fileAttribs);
	    fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
	    fileAttribs2 = fileAttribs.split(fdelim);
	    selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());

	    //System.out.println("total frames: " + selectedFileTotalFrames);
	    //System.out.println(fileAttribs2[0] + " " + fileAttribs2[1] + fileAttribs2[2]);

	    selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());
	    //selectedFileResolution = 32;
	    //selectedFileResolution = Integer.parseInt(fileAttribs2[2].trim());
	}

	// System.out.println(fileAttribs);

	//****** Now let's setup the animation ******
	i = 0;
	animation_name = event.getActionCommand();
	numFrames = selectedFileTotalFrames;
	// System.out.println("file delay: " + selectedFileDelay);

	timer = new Timer(selectedFileDelay, animateTimer);

	if (timer.isRunning() == true) 
        {
	    timer.stop();
	}
	timer.start();
    }
    
    private class UserButtonListener implements ActionListener    
    {
        public void actionPerformed(ActionEvent ae) 
        {
            int result = userDirectoryChooser.showOpenDialog(null);
            if(result == JFileChooser.APPROVE_OPTION)
            {
                File directory = userDirectoryChooser.getSelectedFile();
                if( directory == null )
                {
                    System.out.println("laters");   
                }
                else
                {
                    
                    userPanel.remove(userTilePanel);
                    userTilePanel = new UserProvidedPanel(directory);
		    userTilePanel.populate();
                    userPanel.add(userTilePanel, BorderLayout.CENTER);
                }
            }            
        }        
    }
    
}

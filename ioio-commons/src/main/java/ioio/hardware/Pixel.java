
package ioio.hardware;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.awt.Canvas;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;


/**
 * @author rmarquez
 * @deprecated use the version in org.onebeartoe.pixel.hardware
 */
public class Pixel 
{

    public RgbLedMatrix matrix;
    
    public final RgbLedMatrix.Matrix KIND;
    
    public AnalogInput analogInput1;
    
    protected byte[] BitmapBytes;
    
    protected InputStream BitmapInputStream;
    
    protected short[] frame_;
       
    public Pixel(RgbLedMatrix matrix, RgbLedMatrix.Matrix KIND)
    {
    	this.matrix = matrix;
    	
	this.KIND = KIND;
	
	BitmapBytes = new byte[KIND.width * KIND.height * 2]; //512 * 2 = 1024 or 1024 * 2 = 2048
	
	frame_ = new short[KIND.width * KIND.height];
    }

    /**
     * Read the input stream into a byte array
     * @param raw565ImagePath
     * @throws ConnectionLostException 
     */
    public void loadRGB565(String raw565ImagePath) throws ConnectionLostException 
    {
	BitmapInputStream = getClass().getClassLoader().getResourceAsStream(raw565ImagePath);

	try 
	{   
	    int n = BitmapInputStream.read(BitmapBytes, 0, BitmapBytes.length);
	    Arrays.fill(BitmapBytes, n, BitmapBytes.length, (byte) 0);
	} 
	catch (IOException e) 
	{
	    System.err.println("An error occured while trying to load " + raw565ImagePath + ".");
	    System.err.println("Make sure " + raw565ImagePath + "is included in the executable JAR.");
	    e.printStackTrace();
	}

	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}
//        matrix = PixelApp.getMatrix();
	matrix.frame(frame_);
    }
    
 
    
    private void loadRGB565PNG() throws ConnectionLostException 
    {
	int y = 0;
	for (int f = 0; f < frame_.length; f++) 
	{   
	    frame_[f] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
	    y = y + 2;
	}

//        matrix = PixelApp.getMatrix();
	matrix.frame(frame_);
    }
    
    
    /**          
     * this part of code writes to the LED matrix in code without any external file
     * this just writes a test pattern to the LEDs in code without using any external 
     * file	
     */
    private void writeTest() 
    {
	for (int i = 0; i < frame_.length; i++) 
	{
	    //	frame_[i] = (short) (((short) 0x00000000 & 0xFF) | (((short) (short) 0x00000000 & 0xFF) << 8));  //all black
	    frame_[i] = (short) (((short) 0xFFF5FFB0 & 0xFF) | (((short) (short) 0xFFF5FFB0 & 0xFF) << 8));  //pink
	    //frame_[i] = (short) (((short) 0xFFFFFFFF & 0xFF) | (((short) (short) 0xFFFFFFFF & 0xFF) << 8));  //all white
	}
    }
    
}

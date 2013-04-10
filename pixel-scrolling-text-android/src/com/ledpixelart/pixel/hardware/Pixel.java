
package com.ledpixelart.pixel.hardware;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;

import java.io.InputStream;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;


/**
 * @author rmarquez
 */
public class Pixel 
{

    public RgbLedMatrix matrix;
    
    public final RgbLedMatrix.Matrix KIND;
    
    public AnalogInput analogInput1;
    
    protected byte[] BitmapBytes;
    
    protected InputStream BitmapInputStream;
    
    protected short[] frame_;
    
    private Bitmap canvasBitmap;
    
    private Bitmap originalImage;
    
    int resizedFlag = 0;
    
    private Bitmap resizedBitmap;
    
    public Pixel(RgbLedMatrix matrix, RgbLedMatrix.Matrix KIND)
    {
    	this.matrix = matrix;
    	
		this.KIND = KIND;
		
		BitmapBytes = new byte[KIND.width * KIND.height * 2]; //512 * 2 = 1024 or 1024 * 2 = 2048
		
		frame_ = new short[KIND.width * KIND.height];
    }
    
    public void writeImagetoMatrix(float x, String text, Paint paint) throws ConnectionLostException 
    {  
    	//here we'll take a PNG, BMP, or whatever and convert it to RGB565 via a canvas, also we'll re-size the image if necessary
    	
    	originalImage = Bitmap.createBitmap(64,  64, Bitmap.Config.RGB_565);
    	Canvas canvas = new Canvas(originalImage);    	
    	
    	float y = 25;    	    
    	
    	canvas.drawText(text, x, y, paint);
    	
    	int width_original = originalImage.getWidth();
    	int height_original = originalImage.getHeight();
		 
    	if (width_original != KIND.width || height_original != KIND.height) 
    	{
    		resizedFlag = 1;
    		
			 //the iamge is not the right dimensions, so we need to re-size
    		float scaleWidth = ((float) KIND.width) / width_original;
    		float scaleHeight = ((float) KIND.height) / height_original;
   		 	 
			//int x = 30;
			//int y = 30;
			 
			//scaleWidth = ((float) x) / width_original;
	   		 	//scaleHeight = ((float) y) / height_original;
   		 	 
   		 	 
	   		 // create matrix for the manipulation
	   		 Matrix matrix2 = new Matrix();
	   		 // resize the bit map
	   		 matrix2.postScale(scaleWidth, scaleHeight);
	   		 resizedBitmap = Bitmap.createBitmap(originalImage, 0, 0, width_original, height_original, matrix2, true);
	   		 canvasBitmap = Bitmap.createBitmap(KIND.width, KIND.height, Config.RGB_565); 
	   		 canvas = new Canvas(canvasBitmap);
	   		 canvas.drawRGB(0,0,0); //a black background
	   	   	 canvas.drawBitmap(resizedBitmap, 0, 0, null);
	   		 ByteBuffer buffer = ByteBuffer.allocate(KIND.width * KIND.height *2); //Create a new buffer
	   		 canvasBitmap.copyPixelsToBuffer(buffer); //copy the bitmap 565 to the buffer		
	   		 BitmapBytes = buffer.array(); //copy the buffer into the type array
		 }
		 else 
		 {
			// then the image is already the right dimensions, no need to waste resources resizing
			 resizedFlag = 0;
			 canvasBitmap = Bitmap.createBitmap(KIND.width, KIND.height, Config.RGB_565); 
	   		 canvas = new Canvas(canvasBitmap);
	   	   	 canvas.drawBitmap(originalImage, 0, 0, null);
	   		 ByteBuffer buffer = ByteBuffer.allocate(KIND.width * KIND.height *2); //Create a new buffer
	   		 canvasBitmap.copyPixelsToBuffer(buffer); //copy the bitmap 565 to the buffer		
	   		 BitmapBytes = buffer.array(); //copy the buffer into the type array
		 }	   		
		 
		loadImage();  
		matrix.frame(frame_);  //write to the matrix   
    }    
    
    public void loadImage() 
    {
  		int y = 0;
  		for (int i = 0; i < frame_.length; i++) 
  		{
  			frame_[i] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
  			y = y + 2;
  		}
  		
  		//we're done with the images so let's recycle them to save memory
	    canvasBitmap.recycle();
	    originalImage.recycle(); 
	    
	    if ( resizedFlag == 1) 
	    {
	    	resizedBitmap.recycle(); //only there if we had to resize an image
	    }	   		
  	}    
        
}

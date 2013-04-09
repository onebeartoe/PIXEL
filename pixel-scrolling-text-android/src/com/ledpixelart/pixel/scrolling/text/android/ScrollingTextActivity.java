
package com.ledpixelart.pixel.scrolling.text.android;

import ioio.lib.api.RgbLedMatrix;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

/*
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
*/

import java.util.logging.Level;
import java.util.logging.Logger;

import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.ledpixelart.pixel.hardware.Pixel;

public class ScrollingTextActivity extends IOIOActivity 
{
	private TextView textView_;
	
	private SeekBar seekBar_;
	
	private ToggleButton toggleButton_;
	
	private int x;

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView_ = (TextView)findViewById(R.id.TextView);
        seekBar_ = (SeekBar)findViewById(R.id.SeekBar);
        toggleButton_ = (ToggleButton)findViewById(R.id.ToggleButton);

        enableUi(false);
    }
	
	class Looper extends BaseIOIOLooper 
	{		
		private RgbLedMatrix ledMatrix;
		
		private Pixel pixel;

		@Override
		public void setup() throws ConnectionLostException 
		{
			try 
			{
				RgbLedMatrix.Matrix type = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32;
				ledMatrix = ioio_.openRgbLedMatrix(type);
				
//				Toast toast = Toast.makeText(getApplicationContext() , "matrix obtained", Toast.LENGTH_SHORT);
//				toast.show();
				
				pixel = new Pixel(ledMatrix, type);
				
//				toast.setText("PIXEL obtained.");
				System.out.println("PIXEL obtained");
				
				enableUi(true);
			} 
			catch (ConnectionLostException e) 
			{
				enableUi(false);
				throw e;
			}
		}
		
		@Override
		public void loop() throws ConnectionLostException 
		{ 
			{
				int w = 64;
	            int h = 64;
/*	            
	            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	            
	            Graphics2D g2d = img.createGraphics();
	            g2d.setPaint(Color.orange);
	            
	            String fontFamily = "Arial";
	            
	            Font font = new Font(fontFamily, Font.PLAIN, 32);
	                	            
	            g2d.setFont(font);
	            
	            String message = "Hello, world!";
	            
	            FontMetrics fm = g2d.getFontMetrics();
	            
	            int y = fm.getHeight();            

	            g2d.drawString(message, x, y);
	            g2d.dispose();
*/
	            try 
	            {              
	                pixel.writeImagetoMatrix();
	                
	            } 
	            catch (ConnectionLostException ex) 
	            {
	                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
	            }
	            
	            try 
	            {
					Thread.sleep(1200);
				} 
	            catch (InterruptedException e) 
				{
					System.out.println("coudl not sleep in " + getClass().getName() );
				}
	            
/*	                        
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
*/	            
			}	
		}
	}

	@Override
	protected IOIOLooper createIOIOLooper() 
	{
		return new Looper();
	}

	private void enableUi(final boolean enable) 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				seekBar_.setEnabled(enable);
				toggleButton_.setEnabled(enable);
			}
		});
	}
	
	private void setText(final String str) 
	{
		runOnUiThread(new Runnable() 
		{
			@Override
			public void run() 
			{
				textView_.setText(str);
			}
		});
	}
	
}

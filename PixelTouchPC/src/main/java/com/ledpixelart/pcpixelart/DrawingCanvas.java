package com.ledpixelart.pcpixelart;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import ioio.lib.api.RgbLedMatrix;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

public class DrawingCanvas extends JPanel implements MouseListener, MouseMotionListener 
{
    private static final long serialVersionUID = 1L;
    
    private final int CANVAS_WIDTH = 200;
    private final int CANVAS_HEIGHT = 200;
    
    // last mouse position
    private int lastX;
    private int lastY;   
    
    private ioio.lib.api.RgbLedMatrix.Matrix KIND;
    
    private ioio.lib.api.RgbLedMatrix matrix;
    
    private short [] frame;
          
    public DrawingCanvas(RgbLedMatrix matrix, short [] frame, RgbLedMatrix.Matrix KIND) 
    {
	this.matrix = matrix;
	this.frame = frame;

	addMouseListener(this);         
	addMouseMotionListener(this);   

	this.KIND = KIND;

	setBackground(new Color(255, 255, 160)); // pale yellow

	Dimension d = new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
	setPreferredSize(d);
    }

    /**
     * Determines the starting point of the next line
     * @param event 
     */
    public void mousePressed(MouseEvent event) 
    {
	Point first = event.getPoint(); // where's the mouse?
	lastX = first.x; // save the mouse position
	lastY = first.y;
    }

    /**
     * Draws a line from the last point to the current point.
     * @param event 
     */    
    public void mouseDragged(MouseEvent event) 
    {
	// get the mouse position
	Point current = event.getPoint(); 

	Graphics page = getGraphics(); 
	
	page.drawLine(lastX, lastY, current.x, current.y);

	lastX = current.x; 
	lastY = current.y; 

	
	Dimension d = new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT);
	Rectangle canvas = new Rectangle(getLocation(), d);
	try 
	{
	    BufferedImage screencapture = new Robot().createScreenCapture(canvas);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
	    ImageIO.write(screencapture, "jpeg", baos);
	    baos.flush();
		
/* I assume these bytes can be loaded into a ByteBuffer, but 
 * am not sure how to do it without Android's Bitmap, Canvas, and Matrix classes. */		
	    byte[] imageAsRawBytes = baos.toByteArray();

	    baos.close();

	    int width_original = CANVAS_WIDTH;
	    int height_original = CANVAS_HEIGHT;
	    float scaleWidth = ((float) KIND.width) / width_original;
	    float scaleHeight = ((float) KIND.height) / height_original;
		
/*
 * I am not sure how to port this commented Android graphics specific part of the code to Java desktop APIs.
	    // create matrix for the manipulation
	    ioio.lib.api.RgbLedMatrix.Matrix matrix2 = new ioio.lib.api.RgbLedMatrix.Matrix();
	    // resize the bit map
	    matrix2.postScale(scaleWidth, scaleHeight);
	    resizedBitmap = Bitmap.createBitmap(mBitmap, 0, 0, width_original, height_original, matrix2, true);
	    canvasBitmap = Bitmap.createBitmap(KIND.width, KIND.height, Config.RGB_565);
	    canvas = new Canvas(canvasBitmap);
	    //canvas.drawRGB(0,0,0); //a black background
	    canvas.drawBitmap(resizedBitmap, 0, 0, null);
	    canvas.rotate(90);
	    ByteBuffer buffer = ByteBuffer.allocate(KIND.width * KIND.height * 2); //Create a new buffer
	    canvasBitmap.copyPixelsToBuffer(buffer); //copy the bitmap 565 to the buffer		
	    BitmapBytes = buffer.array(); //copy the buffer into the type array

	    loadImage();

	    if (appAlreadyStarted == 1) 
	    {
		try {
		    matrix_.frame(frame_);    //this was caushing a crash so switched to a timer
		} catch (ConnectionLostException e) {
		    e.printStackTrace();
		}
	    }
*/		
	    } 
	    catch (Exception ex) 
	    {
		Logger.getLogger(DrawingCanvas.class.getName()).log(Level.SEVERE, null, ex);
	    }
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseMoved(MouseEvent event) {
    }
    
}

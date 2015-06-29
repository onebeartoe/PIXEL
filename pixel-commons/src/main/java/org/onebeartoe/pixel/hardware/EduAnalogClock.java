
package org.onebeartoe.pixel.hardware;

import java.util.Date;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * originally from clock.java: 
 * 
 *      http://groups.engin.umd.umich.edu/CIS/course.des/cis525/java/f00/stella/
 *      and
 *      http://groups.engin.umd.umich.edu/CIS/course.des/cis525/java/f00/stella/cis525_hw3_source.html
 * 
 */
public class EduAnalogClock implements ImageObserver
{
	Thread runthread;		//Thread variable

	int htmlradius;			//PARAM - radius of clock face
	int horizoffset;		//PARAM - horizontal number offset
	int verticalOffset;			//PARAM - vertical number offset

	double clockDiameter;	//Diameter of the face of the clock

	double hour; 			//System hour, 0-23
	double minute;			//System minute, 0-59
	double second; 			//System second, 0-59
	double dotdiameter = 8;	//Diameter of a dot on the clock
	double dx;				//X coordinates of number on the clock
	double dy;				//Y coordinates of number on the clock
	double xcenter; 		//X coordinate of the center of the clock
	double ycenter;			//Y coordinate of the center of the clock
	double hradius; 		//Length of the hour hand
	double mradius;			//Length of the minute hand
	double sradius;			//Length of the second hand

	Font f = new Font("TimesRoman", Font.BOLD, 14);	//Font of numbers

	int[] xAxisSecondPoints = new int[5];	//X coordinates for each point of the second hand polygon
	int[] yAxisSecondsPoints = new int[5];	//Y coordinates for each point of the second hand polygon
	
	int[] mxpts = new int[5];	//X coordinates for each point of the minute hand polygon
	int[] mypts = new int[5];	//Y coordinates for each point of the minute hand polygon
	
	int[] xAxisHourPoints = new int[5];	//X coordinates for each point of the hour hand polygon
	int[] yAxisHourPoints = new int[5];	//Y coordinates for each point of the hour hand polygon
	
	int pts = 5;

	double lastsecond = -1; 	//Last second drawn

	Image offscreenImage; 		//Used for double buffering
	Graphics offscreenGraphics; //Used for double buffering
	int iwidth;					//Image width
	int iheight;				//Image height
	double newheight;			//Scaled image height
	double newwidth;			//Scaled image width
	float scalefactor;			//Scale factor
	double imageboxlength;		//Size of image bounding box
	int imagex;					//Image x coord
	int imagey;					//Image y coord
        
        public EduAnalogClock(int OFFSCREEN_IMAGE_WIDTH, int OFFSCREEN_IMAGE_HEIGHT)
        {
            iwidth = OFFSCREEN_IMAGE_WIDTH;
                    
            iheight = OFFSCREEN_IMAGE_HEIGHT;
        }
        
//	public void run() 
//        {
//	
//		while (true) {
//			this.getDate();
//			if (second != lastsecond) {
//				this.calculatePoints();
////				repaint();
//			}
//			try { Thread.sleep(480); }
//			catch (InterruptedException e) {}
//		}
//	}

        /**
         * call this before you start the clock
         */
	public void init() 
        {
//	<PARAM name=diameter value="400">
//	<PARAM name=image value="hat.jpg">
//	<PARAM name=hoff value="0">
//	<PARAM name=voff value="-5">
	
		//Get applet PARAM's
//		clockDiameter = 400;
                clockDiameter = iwidth - 1;                
//		clockDiameter = Integer.parseInt((getParameter("diameter")));
                
//		image = "hat.jpg";
//                image = getParameter("image");
                
		horizoffset = 0;
//		horizoffset = Integer.parseInt((getParameter("hoff")));
                
                verticalOffset = -5;
//		verticalOffset = Integer.parseInt((getParameter("voff")));

		//Calculate center & hand radius
		xcenter = clockDiameter/2;
		ycenter = clockDiameter/2;
		hradius = 0.5*(clockDiameter/2);
		mradius = 0.7*(clockDiameter/2);

		//Initializa image object
                offscreenImage = new BufferedImage(iwidth, iheight, BufferedImage.TYPE_INT_ARGB);
                
		offscreenGraphics = offscreenImage.getGraphics();

		sradius = 0.7*(clockDiameter/2);
	}
	
        /**
         * Calculates the points which make up each corner of the polygon (hand) based upon
         *   the current time.
         *   x endpoint = xcenter + radius*sin(2PI*time/60) for minutes and seconds
         *   y endpoint = ycenter - radius*cos(2PI*time/60) for minutes and seconds
         *   The other two midpoints are half the length of the hand offset by 0.1 radians
         */
	public void calculatePoints() 
        {
            // points for the seconds hand    
		xAxisSecondPoints[0] = xAxisSecondPoints[4] = (int) xcenter;
		yAxisSecondsPoints[0] = yAxisSecondsPoints[4] = (int) ycenter;

		xAxisSecondPoints[2] = (int)(xcenter + (sradius*(Math.sin(2*Math.PI*(second/60)))));
		yAxisSecondsPoints[2] = (int)(ycenter + (-1.5*sradius*(Math.cos(2*Math.PI*(second/60)))));

		xAxisSecondPoints[1] = (int)(xcenter + (0.3*sradius*(Math.sin(2*Math.PI*(second/60) - 0.1))));
		yAxisSecondsPoints[1] = (int)(ycenter + (-0.3*sradius*(Math.cos(2*Math.PI*(second/60) - 0.1))));

		xAxisSecondPoints[3] = (int)(xcenter + (0.3*sradius*(Math.sin(2*Math.PI*(second/60) + 0.1))));
		yAxisSecondsPoints[3] = (int)(ycenter + (-0.3*sradius*(Math.cos(2*Math.PI*(second/60) + 0.1))));

                
                // points for the minutes hand
		mxpts[0] = mxpts[4] = (int) xcenter;
		mypts[0] = mypts[4] = (int) ycenter;

		mxpts[2] = (int)(xcenter + (mradius*(Math.sin(2*Math.PI*(minute/60)))));
		mypts[2] = (int)(ycenter + (-1.4*mradius*(Math.cos(2*Math.PI*(minute/60)))));

		mxpts[1] = (int)(xcenter + (0.5*mradius*(Math.sin(2*Math.PI*(minute/60) - 0.2))));
		mypts[1] = (int)(ycenter + (-0.5*mradius*(Math.cos(2*Math.PI*(minute/60) - 0.2))));

		mxpts[3] = (int)(xcenter + (0.5*mradius*(Math.sin(2*Math.PI*(minute/60) + 0.2))));
		mypts[3] = (int)(ycenter + (-0.5*mradius*(Math.cos(2*Math.PI*(minute/60) + 0.2))));

                                
		//To gradually move the hour hand so that it's position reflects the 
		//position of the minute hand, we need to calculate how many seconds 
		//out of 12 hours (43,200s) have passed and substitute that value for 
		//s/60. Otherwise, the hour hand would operate as a 'step' function 
		//portraying a misleading time.
		double totalSeconds = calculateSeconds();

		xAxisHourPoints[0] = xAxisHourPoints[4] = (int) xcenter;
		yAxisHourPoints[0] = yAxisHourPoints[4] = (int) ycenter;
                
		xAxisHourPoints[2] = (int)(xcenter + (hradius*(Math.sin(2*Math.PI*totalSeconds))));
		yAxisHourPoints[2] = (int)(ycenter + (-1.25*hradius*(Math.cos(2*Math.PI*totalSeconds))));

		xAxisHourPoints[1] = (int)(xcenter + (0.7*hradius*(Math.sin(2*Math.PI*totalSeconds - 0.2))));
		yAxisHourPoints[1] = (int)(ycenter + (-0.7*hradius*(Math.cos(2*Math.PI*totalSeconds - 0.2))));

		xAxisHourPoints[3] = (int)(xcenter + (0.7*hradius*(Math.sin(2*Math.PI*totalSeconds + 0.2))));
		yAxisHourPoints[3] = (int)(ycenter + (-0.7*hradius*(Math.cos(2*Math.PI*totalSeconds + 0.2))));
	}

        /**
         * 
         * @return total # of elapsed seconds
         */
	public double calculateSeconds() 
        {
		//Get the total number of seconds elapsed in this 12-hour period
		return( ( (3600*hour + 60*minute + second) / 43200) );
	}


        /**
         * updates drawing area
         * @param g 
         */
	public void paint(Graphics g) 
        {
            getDate();
            calculatePoints();
		
            
		// set background color
                g.setColor(Color.black);
                g.fillRect(0,0, iwidth, iheight);
		
		//Set font
		g.setFont(f);
		
		//Set draw color
		offscreenGraphics.setColor(Color.black);
		
		//Draw Oval face and background rectangle
		offscreenGraphics.fillOval(0,0,(int)clockDiameter, (int)clockDiameter); //Draw an oval
		offscreenGraphics.fillRect(0,0, (int)clockDiameter+1, (int)clockDiameter+1);
		
		offscreenGraphics.setColor(Color.white);
		offscreenGraphics.fillOval(0,0,(int)clockDiameter-1, (int)clockDiameter-1);
                
                offscreenGraphics.setColor(Color.black);
                offscreenGraphics.fillOval(20,20,(int)clockDiameter-40, (int)clockDiameter-40);

		//Each number is first calculated, then converted to a string. The string's height and
		//width are calculated using font metrics. The string can then be centered at it's 
		//appropriate position using a formula similar to the one that determines where each hand
		//should be drawn.		
		for (double i=0; i<60; i+=5) 
                {			
			double number;		//hour

			number = i/5;
			
			if (number == 0) {
				number = 12;
			}
			
			//Convert to string and remove the ".0"
			String s = Double.toString(number);
			String t = s.substring(0, s.length() - 2);		
		}

		//Get the size of the bounding box
		imageboxlength = (double)(clockDiameter*(Math.sin(Math.PI/4))*0.8);

		//Scale the image
		if (iwidth > iheight) 
                {
			scalefactor = (float)(imageboxlength/iwidth);
		}	
		else 
                {
			scalefactor = (float)(imageboxlength/iheight);
		}
		
		newwidth = iwidth*scalefactor;
		newheight = iheight*scalefactor;

		imagex = (int)(xcenter - (newwidth/2));
		imagey = (int)(ycenter - (newheight/2));

		//Draw each hand
		offscreenGraphics.setColor(Color.red); 

		offscreenGraphics.fillPolygon(xAxisSecondPoints, yAxisSecondsPoints, pts);

		offscreenGraphics.setColor(Color.blue);
		offscreenGraphics.fillPolygon(mxpts, mypts, pts);

		offscreenGraphics.setColor(Color.green);
		offscreenGraphics.fillPolygon(xAxisHourPoints, yAxisHourPoints, pts);

		lastsecond = second;

		//Swap the offscreen image with the onscreen image
		g.drawImage(offscreenImage, 0, 0, this);
	}

//	public void update(Graphics g) 
//        {
//		//Call the paint routine without clearing the canvas
//		paint(g);
//	}

	//=============================
	//Function:	getDate()
	//Input:	none
	//Output:	none
	//			updates hour, minute, second
	//=============================
	
	public void getDate()
        {
		//Get the current date/time
		Date theDate = new Date();				//Get current system time
	
		//Parse out the hour, minute and second
		hour = (double) theDate.getHours();		//Get hours
		minute = (double) theDate.getMinutes(); //Get minutes
		second = (double) theDate.getSeconds(); //Get seconds
	}

	//=============================
	//Function:	start()
	//Input:	none
	//Output:	none
	//			updates runthread
	//=============================
	
	public void start() {
	
		//Start as a thread
		if (runthread == null) {
//			runthread = new Thread(this);
			runthread.start();
		}
	}

	//=============================
	//Function:	stop()
	//Input:	none
	//Output:	none
	//			updates runthread
	//=============================	
//	public void stop() 
//        {
//	
//		//Stop the thread
//		if (runthread != null) {
//			runthread.stop();
//			runthread = null;
//		}
//	}

    @Override
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
    {
        return true;
    }
}
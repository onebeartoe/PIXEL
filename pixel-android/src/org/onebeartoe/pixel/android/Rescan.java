
package org.onebeartoe.pixel.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.widget.TextView;

public class Rescan extends Activity  
{

   	private final String tag = "LEDAlbum";	
	private RescanTimer rescanTimer; 
	private boolean scanAllPics;
	private int countdownCounter;
	private static final int countdownDuration = 30;
	private TextView countdown_;
       
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //force only portrait mode
        setContentView(R.layout.rescan);         
        countdown_ = (TextView)findViewById(R.id.countdown); 
        
        //launch the media scanner here
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,Uri.parse("file//" + Environment.getExternalStorageDirectory()))); //this triggers a media scan on the whole sd card
        
        countdownCounter = countdownDuration -1; //reset the counter
               
        rescanTimer = new RescanTimer(countdownDuration*1000,1000); //pop up a message if it's not connected by this timer
 		rescanTimer.start(); //this timer will pop up a message box if the device is not found      
    }
    
    public class RescanTimer extends CountDownTimer
	{

		public RescanTimer(long startTime, long interval)
			{
				super(startTime, interval);
			}

		@Override
		public void onFinish()
			{
			
			finish();
				
			}

		@Override
		public void onTick(long millisUntilFinished)				{
	
			setCountdown(Integer.toString(countdownCounter));
			countdownCounter--;
		}
	}
    
    
    
    private void setCountdown(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				countdown_.setText(str);
			}
		});
	}
    
}

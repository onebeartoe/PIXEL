
package org.onebeartoe.pixel.android;

import java.util.HashMap;
import java.util.Map;

import org.onebeartoe.pixel.android.art.PixelArtActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class PixelActivity extends Activity 
{
	private Map<Integer, Class<? extends Activity> > activitiesMap;
	
	public PixelActivity()
	{
		popluateActivitiesMap();
	}
	
	public void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.pixel);

	    GridView gridview = (GridView) findViewById(R.id.gridview);
	    gridview.setAdapter( new ImageAdapter(this) );
	    gridview.setOnItemClickListener( new ThumbnailListener() );
	}
	
	private void popluateActivitiesMap()
	{		
		activitiesMap = new HashMap();
	
		Integer i = 0;
		activitiesMap.put(i, PixelArtActivity.class);
	}
	
	public class ImageAdapter extends BaseAdapter 
	{
	    private Context mContext;

	    public ImageAdapter(Context c) 
	    {
	        mContext = c;
	    }

	    public int getCount() 
	    {
	        return thumbnailIds.length;
	    }

	    public Object getItem(int position) {
	        return null;
	    }

	    public long getItemId(int position) {
	        return 0;
	    }

	    // create a new ImageView for each item referenced by the Adapter
	    public View getView(int position, View convertView, ViewGroup parent) {
	        ImageView imageView;
	        if (convertView == null) {  // if it's not recycled, initialize some attributes
	            imageView = new ImageView(mContext);
	            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
	            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
	            imageView.setPadding(8, 8, 8, 8);
	        } else {
	            imageView = (ImageView) convertView;
	        }

	        imageView.setImageResource(thumbnailIds[position]);
	        return imageView;
	    }

	    // references to our images
	    private Integer[] thumbnailIds = 
	    	{
	            R.drawable.stills_tile, 
	            R.drawable.animations_tile,
	            R.drawable.interactive_tile, 
	            R.drawable.scrolling_text_tile     
	    };
	}
	
	private class ThumbnailListener implements OnItemClickListener 
    {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
        {
        	if(position < activitiesMap.size() )
        	{
        		Intent mainIntent = new Intent(PixelActivity.this, PixelArtActivity.class);
				PixelActivity.this.startActivity(mainIntent);
        	}
        	else
        	{
        		String message = "No activity is definded for position " + position + ".";
        		Toast.makeText(PixelActivity.this, message, Toast.LENGTH_SHORT).show();
        	}
        }
    }
}

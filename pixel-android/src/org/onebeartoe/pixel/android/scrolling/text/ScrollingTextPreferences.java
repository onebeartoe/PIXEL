
package org.onebeartoe.pixel.android.scrolling.text;

import org.onebeartoe.pixel.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ScrollingTextPreferences extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.scrolling_text_preferences);    
      
    }
}

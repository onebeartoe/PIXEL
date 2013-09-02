package org.onebeartoe.pixel.android.animations;

import org.onebeartoe.pixel.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AnimationsPreferences extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.animations_preferences);    
      
    }
}




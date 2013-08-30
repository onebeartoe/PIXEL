
package org.onebeartoe.pixel.android.art;

import org.onebeartoe.pixel.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ArtPreferences extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.art_preferences);
    }
}

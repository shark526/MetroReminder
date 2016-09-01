package com.shark.metroreminder;
import android.app.*;
import android.os.*;


public class SettingsActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//addPreferencesFromResource(R.xml.preferences);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new PrefsFragment()).commit();
    }
	
}

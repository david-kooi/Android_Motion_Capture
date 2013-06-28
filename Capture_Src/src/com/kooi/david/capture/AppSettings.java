package com.kooi.david.capture;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.example.cameratextv1.R;

public class AppSettings extends PreferenceActivity  {

	//TODO: Notification dialog for each settings

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencefile);
		//Settings activity only to be used with android 3.0 and lower
		//Create SettingsFragment for 3.0 and higher
	}
		
}
	



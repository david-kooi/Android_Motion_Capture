package com.kooi.david.capture;

import com.example.cameratextv1.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AppSettings extends ListActivity {

	//TODO: Notification dialog for each settings
	
	EditText delayPicker;
	ToggleButton toggleButton;
	Button setButton;
	EditText frameEditText;
	ListView settingsList;
	static boolean motionToggle = true;
	static int frameSpeed = 0;
	String[] settings = {"Motion Detection", "Delay", "Capture Speed"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_settings);
	    
		settingsList = (ListView) findViewById(android.R.id.list);
	    
	    setListAdapter(new ArrayAdapter<String>(
			      this,
			      android.R.layout.simple_list_item_1,
			      settings));
	
		

	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
	super.onListItemClick(l, v, position, id);
	//Position 0: Motion Detection
	//Position 2: Delay
	//Position 3: Capture Speed
	
		if(position == 0){
			//Dialog
		}
		else if (position == 1){
			//Dialog
		}
		else if (position == 2){
			//Dialog
		}
	}

	
	
	public void onPause(){

		//Set Delay
		String text2 = delayPicker.getText().toString();
		if(text2 != "''"){
			int delay = Integer.valueOf(text2)*1000;
			CameraInterface.delayTime = delay;
		}
		
		super.onPause();
	}
	public void onResume(){
		super.onResume();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void toggleButtonOnClick(View v){
		
		if(toggleButton.isChecked()){
			motionToggle = true;
			Log.d("Process", "Motion Detection: "+motionToggle);
			Toast.makeText(this, "Motion Detection Activated", Toast.LENGTH_SHORT).show();
		}
		else if(!toggleButton.isChecked()){
			motionToggle = false;
			Log.d("Process", "Motion Detection: " + motionToggle);
			Toast.makeText(this, "Motion Detection Deactivated", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void setFrameSpeedOnClick(View v){
		String text1 = frameEditText.getText().toString();
		frameSpeed = Integer.valueOf(text1);
		CameraPreview.frameCaptureSpeed = 30/frameSpeed;
		Toast.makeText(this, "Frame Capture Speed: "+frameSpeed, Toast.LENGTH_SHORT).show();
		
	}

}

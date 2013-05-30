package com.kooi.david.capture;

import com.example.cameratextv1.R;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AppSettings extends Activity {

	ToggleButton toggleButton;
	Button setButton;
	EditText frameEditText;
	static boolean motionToggle = true;
	static int frameSpeed = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_settings);
		
		toggleButton = (ToggleButton) findViewById(R.id.toggleButton1);
		frameEditText = (EditText) findViewById(R.id.frameCaptureSpeedTextEdit);
		setButton = (Button) findViewById(R.id.setFrameSpeedButton);
		
		toggleButton.setChecked(true);
	}

	public void onPause(){
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
		
		frameSpeed = Integer.valueOf(frameEditText.getText().toString());
		
		CameraPreview.frameCaptureSpeed = 30/frameSpeed;
		
		Toast.makeText(this, "Frame Capture Speed: "+frameSpeed, Toast.LENGTH_SHORT).show();
		
	}

}

package com.kooi.david.capture;

import com.example.cameratextv1.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
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
	    

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//public void toggleButtonOnClick(View v){
//		
//		if(toggleButton.isChecked()){
//			motionToggle = true;
//			Log.d("Process", "Motion Detection: "+motionToggle);
//			Toast.makeText(this, "Motion Detection Activated", Toast.LENGTH_SHORT).show();
//		}
//		else if(!toggleButton.isChecked()){
//			motionToggle = false;
//			Log.d("Process", "Motion Detection: " + motionToggle);
//			Toast.makeText(this, "Motion Detection Deactivated", Toast.LENGTH_SHORT).show();
//		}
//	}
//	
//	public void setFrameSpeedOnClick(View v){
//		String text1 = frameEditText.getText().toString();
//		frameSpeed = Integer.valueOf(text1);
//		CameraPreview.frameCaptureSpeed = 30/frameSpeed;
//		Toast.makeText(this, "Frame Capture Speed: "+frameSpeed, Toast.LENGTH_SHORT).show();
//		
//	}

}

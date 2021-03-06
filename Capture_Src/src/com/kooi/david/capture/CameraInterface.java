package com.kooi.david.capture;

import com.example.cameratextv1.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class CameraInterface extends Activity {

	//TODO: Create another method of motion notification
	
	// Camera Values---------------->
	PreviewCallback previewCallback;
	Button captureButton;
	Camera deviceCamera;
	CameraPreview cameraPreview;
	FrameLayout preview;
	LayoutInflater previewInflater = null;

	// Boolean Values---------------------------->
	public static boolean PREVIEWRUNNING = false;
	public static boolean CAPTUREFRAME = false;
	public static boolean BUTTONPRESSED = false;

	// Other Values------------------->
	public static int BUTTONCOUNT = 0;
	//Preference Values--------------->
	SharedPreferences sharedPref;
	public static long delayTime = 0;
	public static boolean detectionBoolean;
	public static int detectionInt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_interface);
		
		
		 //Set preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPref.getBoolean("motion_detection", true))
        {
        	detectionInt = 0;
        }else{
        	detectionInt = 1;
        }
	
        if(sharedPref.getBoolean("delay", false)){ //If Delay is on
        	delayTime = sharedPref.getInt("edit_delay", 0);
        }
        else{
        	delayTime = 0;
        }	
		
		captureButton = (Button) findViewById(R.id.captureButton);
		//Camera and Preview setup--------------------------------->
		deviceCamera = getCameraInstance();
		cameraPreview = new CameraPreview(this, deviceCamera, detectionInt);
		preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(cameraPreview);
		//----------------------------------------------------------^
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Keep screen running
        
        Log.d("Preferences", "Motion Detection: " + detectionInt);
        Log.d("Preferences", "Delay: " + delayTime);
	}
	
	public void onPause(){
		super.onPause();
		BUTTONPRESSED = false;
		PREVIEWRUNNING = false;
		preview.removeAllViews();
		this.releaseCamera();
	}
	public void onResume(){
		super.onResume();
		if(deviceCamera == null){
			
		deviceCamera = Camera.open();
		Log.d("Test", "Interface Camera: " + deviceCamera);
		cameraPreview = new CameraPreview(this, deviceCamera, detectionInt);
		preview = (FrameLayout) findViewById(R.id.cameraPreview);
		preview.addView(cameraPreview);
		
		}
		deviceCamera.startPreview();

	}
	public void onDestory(){
		super.onDestroy();
		finish();
		this.releaseCamera();

	}

	// Button that starts the recording
	public void captureButtonOnClick(View v) throws InterruptedException {
		// See CameraPreview.frameIncrement()
		Log.d("Process", "ButtonPress");

		if (BUTTONPRESSED == false) {
			ImageAnalysis.runIndex = 0;
			Thread.sleep(delayTime);			//Delay Before Capture Start
			BUTTONPRESSED = true;
			Toast.makeText(this, "Capture Started", Toast.LENGTH_SHORT).show();
		} else if (BUTTONPRESSED == true) {
			Toast.makeText(this, "Capture Stopped", Toast.LENGTH_SHORT).show();
			BUTTONPRESSED = false;
			cameraPreview.extractFromBuffer();
			addImagesToGallery();
			//
			Log.d("Process",
					"All pictures compressed. Pictures send to gallery.");
		
		}

	}
	

	private void addImagesToGallery() {
		sendBroadcast(new Intent(
				Intent.ACTION_MEDIA_MOUNTED,
				Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	}

	public static Camera getCameraInstance() {
		Camera c = null;
		try {
			c = Camera.open(); // attempt to get a Camera instance
		} catch (Exception e) {
			Log.e("Error", "Camera Error: " + e);
		}
		return c; // returns null if camera is unavailable
	}

	public void releaseCamera() {
		if (deviceCamera != null) {
			deviceCamera.stopPreview();      
			deviceCamera.setPreviewCallback(null);    
            deviceCamera.release();     
            deviceCamera = null;  
		}
	}

}

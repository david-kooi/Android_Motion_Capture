package com.kooi.david.capture;

import com.example.cameratextv1.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

	Button startCamera;
	Button startSettings;
	ImageView captureImage;
	Drawable image;

	//TODO: Create a gallery 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		startCamera = (Button) findViewById(R.id.startCameraButton);
		startSettings = (Button) findViewById(R.id.settingsButton);

	}

	public void startCameraOnClick(View v) {
		Intent i = new Intent(this, CameraInterface.class);
		startActivity(i);
	}

	public void settingsButtonOnClick(View v) {
		Intent i = new Intent(this, AppSettings.class);
		startActivity(i);
	}

}

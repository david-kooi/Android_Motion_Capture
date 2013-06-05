package com.kooi.david.capture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.PreviewCallback;

@SuppressLint("NewApi")
public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {

	// Camera Variables---------------->  
	private int frame_number = 0;
	private byte[] frame = new byte[1];
	private Camera deviceCamera;
	Camera.Parameters cameraParams;
	static int frameCaptureSpeed = 5; // Speed for motion Detection is 7
										// (4.2fps)
										// Speed for rapid capture is 5 (6fps)

	// Preview Variables------------->
	private SurfaceHolder thisHolder;
	private int previewHeight;
	private int previewWidth;

	// Save Variables------------->
	File pictureFile;
	int imageCount = 0;
	FileOutputStream fos = null;
	boolean check;
	ArrayList<byte[]> byteBuffer;

	// Process values--------------->

	static int detectOrCapture = 0;   // 0: Motion Detection
									// 1: Rapid Frame Save
	boolean captureFrame = false;
	int frameCount = 0;
	int loopCount = 0;
	boolean extractFromBuffer = false;
	boolean motionTrigger = false;
	static boolean optimizedDecode = false; 


	@SuppressWarnings("deprecation")
	public CameraPreview(Context whatContext, Camera whatCamera) {
		super(whatContext);

		byteBuffer = new ArrayList<byte[]>(); // Stores Image data for later
											  // extraction

		deviceCamera = whatCamera;
		cameraParams = deviceCamera.getParameters();

		thisHolder = getHolder();
		thisHolder.addCallback(this);
		thisHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		motionSettings();
		frameCaptureSettings();

	}

	private void motionSettings() {
		if (AppSettings.motionToggle) {
			detectOrCapture = 0;
		} else {
			detectOrCapture = 1;
		}
	}

	private void frameCaptureSettings() {
		if (AppSettings.frameSpeed != 0) {
			frameCaptureSpeed = AppSettings.frameSpeed;
		}
		Log.d("Process", "Frame Speed:" + frameCaptureSpeed);

	}

	// Controls the flow of the program
	private void processControler() {
		if (CameraInterface.BUTTONPRESSED) {
			if (frameCount == 0 || frameCount == frameCaptureSpeed) {
				captureFrame = true;
				if (frameCount == frameCaptureSpeed) {
					frameCount = 0;
				}
			} else {
				captureFrame = false;
			}
			frameCount++;
		} else {
			captureFrame = false;
		}

	}

	PreviewCallback previewCallback = new PreviewCallback() {
		public void onPreviewFrame(byte[] data, Camera camera) {
			processControler();
			if (captureFrame) {
				deviceCamera.startPreview();
				Log.d("Process", "*****FRAME_CAPTURE*****");
				frame[0] = (byte) frame_number;

				new FrameHandler().execute(data, frame);
				frame_number++;
			}
		}
	};

	// Courtesy of globetrotter
	public class FrameHandler extends AsyncTask<byte[], Boolean, Boolean> {

		Toast toast;

		// Final Variables
		private final int WIDTH = previewWidth;
		private final int HEIGHT = previewHeight;
		private final int ARRAY_LENGTH = previewWidth * previewWidth * 3 / 2;

		// Pre-allocated working arrays
		private int[] argb8888 = new int[ARRAY_LENGTH];

		@Override
		protected Boolean doInBackground(byte[]... data) {
			// Log.d("Process", "FrameHandler is Go!");
//
			/* If detectOrCapture = 0: image analysis
			   If detectOrCaoture = 1: Saves byte data to a buffer...byte data will
			 be compressed into a jpeg post capture
			 */
			switch (detectOrCapture) {
			case 0:
				Log.d("Data", "***Analysis Start***");
				if(!optimizedDecode){ //Initial Decode
					Log.d("Process","Standard Decode");
					decodeYUV(argb8888, data[0], WIDTH, HEIGHT);//1. Decode data
					final Bitmap bitmap = Bitmap.createBitmap(argb8888, WIDTH, //2. Create bitmap from data
							HEIGHT, Config.ARGB_8888);
					ImageAnalysis.setBitmapSpecs(bitmap);//Set variables in ImageAnalysis
					optimizedDecode = true;
				}
				else{ //Optimized Decode
					Log.d("Process", "Optimized Decode");
					// Analyze Bitmap
					decodeYUVForMotion(argb8888,data[0],WIDTH,HEIGHT); //1. Decode Data
					final Bitmap bitmap = Bitmap.createBitmap(argb8888, ImageAnalysis.gridWidth, //2. Create bitmap from data
							ImageAnalysis.gridHeight, Config.ARGB_8888);
					Log.d("Check", "Optimized Height: " + bitmap.getHeight());
					Log.d("Check", "Optimized Height: " + bitmap.getWidth());
					ImageAnalysis.setBitmap(bitmap);
					ImageAnalysis.extractValuesFromBitmap();							//3. Analyze bitmap
					motionTrigger = ImageAnalysis.statisticalAnalysis();   //4. Returns boolean based on analysis
					ImageAnalysis.runIndex++;
					if (motionTrigger) { 
						Log.d("Data", "*****MOTION*****");
						Log.d("Process", "*****MOTION*****");
						detectOrCapture = 1;
						return true;
					}
				}
				break;
			case 1:
				addToByteBuffer(data[0]);
				Log.d("Process", "Byte[] added to buffer");
				break;

			}
			return false;

		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				Toast.makeText(getContext(), "Motion Detected",
						Toast.LENGTH_SHORT).show();
			}
		}

	}

	private void addToByteBuffer(byte[] in) {
		byteBuffer.add(in);
	}

	public void extractFromBuffer() {
		Log.d("Process", "Post capture extraction started");
		final int WIDTH = previewWidth;
		final int HEIGHT = previewHeight;
		final int ARRAY_LENGTH = previewWidth * previewWidth * 3 / 2;
		byte[] tempByteArray;

		int[] dataArray = new int[ARRAY_LENGTH];

		// Decodes byte array into bitmap
		for (int i = 0; i < byteBuffer.size(); i++) {
			tempByteArray = byteBuffer.get(i);
			decodeYUV(dataArray, tempByteArray, WIDTH, HEIGHT);// 1. Decode
			final Bitmap bitmap = Bitmap.createBitmap(dataArray, WIDTH, HEIGHT,
					Config.ARGB_8888); //2. Create Bitmap

			setJpegSaveLocation(); // 3. Compresses and saves bitmap to SD card
			try {
				fos = new FileOutputStream(pictureFile);
				// Log.d("Process", "FileOutputStream Created");
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				
				// Log.d("Process",
				// "Bitmap Compressed To: "
				// + pictureFile.getAbsolutePath());
				// Log.d("Process", "Compression: Finished");

				fos.flush();
				fos.close();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	// Sets the location to save on the SD card
	private void setJpegSaveLocation() {
		String root;
		File dir;
		String image;
		Long mili = System.currentTimeMillis();

		root = Environment.getExternalStorageDirectory().getPath()
				+ "/Capture.";
		dir = new File(root);
		dir.mkdir();

		image = "Image" + imageCount + mili + ".jpeg";

		pictureFile = new File(dir, image);
		try {
			pictureFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("Process",
				"Save Location Set To: " + pictureFile.getAbsolutePath());
		imageCount++;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			// deviceCamera.setPreviewDisplay(thisHolder);
			// deviceCamera.startPreview();
		} catch (Exception e) {
			Log.d("Process:", "Error setting camera preview: " + e.getMessage());

		}

	}

	private void releaseCamera() {
		if (deviceCamera != null) {
			deviceCamera.release(); // release the camera for other applications
			deviceCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		releaseCamera();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (thisHolder.getSurface() == null) {
			return;
		}
		if (CameraInterface.PREVIEWRUNNING) {
			deviceCamera.stopPreview();
		}

		// Set camera parameters---------------------------------------------->
		cameraParams = deviceCamera.getParameters();
		List<Camera.Size> list = cameraParams.getSupportedPreviewSizes();
		previewHeight = getMaxPreviewHeight(list);
		previewWidth = getMaxPreviewWidth(list);
		int exposure = cameraParams.getExposureCompensation();
		int exposureMax = cameraParams.getMaxExposureCompensation();
		int exposureMin = cameraParams.getMinExposureCompensation();
		Log.d("Process", "Exposure: " + exposure);
		Log.d("Process", "Max Exposure: " + exposureMax);
		Log.d("Process", "Min Exposure: " + exposureMin);
		cameraParams.setExposureCompensation(exposureMin);
		cameraParams.setPreviewSize(previewWidth, previewHeight);
		deviceCamera.setParameters(cameraParams);
		Log.d("Process: ", "PreviewWidth: " + previewWidth);
		Log.d("Process: ", "PreviewHeight:" + previewHeight);
		// ---------------------------------------------------------------^

		try {
			deviceCamera.setPreviewDisplay(thisHolder);
		} catch (Exception e) {
			Log.d("Process:",
					"Error starting camera preview: " + e.getMessage());
		}
		deviceCamera.setPreviewCallback(previewCallback);
		deviceCamera.startPreview();
		CameraInterface.PREVIEWRUNNING = true;
	}

	private int getMaxPreviewWidth(List<Camera.Size> whatList) {
		int width;
		int tempWidth;

		width = whatList.get(0).width;
		for (int i = 0; i < whatList.size(); i++) {
			tempWidth = whatList.get(i).width;
			if (tempWidth > width) {
				width = tempWidth;
			} else {
				continue;
			}
		}
		return width;
	}

	private int getMaxPreviewHeight(List<Camera.Size> whatList) {
		int height;
		int tempHeight;

		height = whatList.get(0).height;
		for (int i = 0; i < whatList.size(); i++) {
			tempHeight = whatList.get(0).height;
			if (tempHeight > height) {
				height = tempHeight;
			} else {
				continue;
			}
		}
		return height;
	}

	
	// David Manpearl 081201 
	public void decodeYUV(int[] out, byte[] fg, int width, int height)
			throws NullPointerException, IllegalArgumentException {
		int sz = width * height;
		if (out == null)
			throw new NullPointerException("buffer out is null");
		if (out.length < sz)
			throw new IllegalArgumentException("buffer out size " + out.length
					+ " < minimum " + sz);
		if (fg == null)
			throw new NullPointerException("buffer 'fg' is null");
		if (fg.length < sz)
			throw new IllegalArgumentException("buffer fg size " + fg.length
					+ " < minimum " + sz * 3 / 2);
		int i, j;
		int Y, Cr = 0, Cb = 0;
		for (j = 0; j < height; j++) {
			int pixPtr = j * width;
			final int jDiv2 = j >> 1;
			for (i = 0; i < width; i++) {
				Y = fg[pixPtr];
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
					Cb = fg[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = fg[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;
				out[pixPtr++] = 0xff000000 + (B << 16) + (G << 8) + R;
				//Log.d("Pix", "pixPtr: " + pixPtr);

			}
		}

	}
	
	public void decodeYUVForMotion(int[] out, byte[] fg, int width, int height)
			throws NullPointerException, IllegalArgumentException {
		int sz = width * height;
		if (out == null)
			throw new NullPointerException("buffer out is null");
		if (out.length < sz)
			throw new IllegalArgumentException("buffer out size " + out.length
					+ " < minimum " + sz);
		if (fg == null)
			throw new NullPointerException("buffer 'fg' is null");
		if (fg.length < sz)
			throw new IllegalArgumentException("buffer fg size " + fg.length
					+ " < minimum " + sz * 3 / 2);
		int i, j;
		int count = 0;
		int Y, Cr = 0, Cb = 0;
		for (j = 0; j <= height; j= j + ImageAnalysis.verticalInc) {
			if(j == height){
				j = height - 1;
			}
			int pixPtr = j * width;
			int outIndex = count * ImageAnalysis.gridWidth;
			count++;
			final int jDiv2 = j >> 1;
			for (i = 0; i < ImageAnalysis.gridWidth; i++) {
				Y = fg[pixPtr];
				//Log.d("Data", "Y: " + Y);
				if (Y < 0)
					Y += 255;
				if ((i & 0x1) != 1) {
					final int cOff = sz + jDiv2 * width + (i >> 1) * 2;
					Cb = fg[cOff];
					if (Cb < 0)
						Cb += 127;
					else
						Cb -= 128;
					Cr = fg[cOff + 1];
					if (Cr < 0)
						Cr += 127;
					else
						Cr -= 128;
				}
				int R = Y + Cr + (Cr >> 2) + (Cr >> 3) + (Cr >> 5);
				if (R < 0)
					R = 0;
				else if (R > 255)
					R = 255;
				int G = Y - (Cb >> 2) + (Cb >> 4) + (Cb >> 5) - (Cr >> 1)
						+ (Cr >> 3) + (Cr >> 4) + (Cr >> 5);
				if (G < 0)
					G = 0;
				else if (G > 255)
					G = 255;
				int B = Y + Cb + (Cb >> 1) + (Cb >> 2) + (Cb >> 6);
				if (B < 0)
					B = 0;
				else if (B > 255)
					B = 255;
				out[outIndex++] = 0xff000000 + (B << 16) + (G << 8) + R;
				//Log.d("Index", "j: " + j);
				//Log.d("Index", "i: " + i);
				//Log.d("Index", "outIndex: " + outIndex);
				
			}
		}

	}

}

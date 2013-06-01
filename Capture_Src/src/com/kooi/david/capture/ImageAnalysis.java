package com.kooi.david.capture;

import android.graphics.Color;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.lang.Math;

public class ImageAnalysis {
	
	//Test Values
	static double meanSD;
	static int count = 1;
	static double sumSD;
	
	//Bitmap
	static Bitmap bitmapToScan;

	// Image values------->
	static int imgHeight;
	static int imgWidth;
	static int gridHeight;
	static int gridWidth;
	static int horizontalInc;
	static int verticalInc;
	static int baseValue;
	static int blueValue;
	static int redValue;
	static int greenValue;
	static Color c;

	// Arrays------------------------>
	static int rgbArray[][];
	static int resultantRgbArray[][];

	// Control Values----------------->
	static int controlRgbArray[][];
	static double TEMPMEAN = 0;
	static double controlMean = 0;
	static int runIndex = 0; // How many times ImageAnalysis has been run

	// Sets the gridHeight, gridWidth, and increments...etc
	// Prepares Bitmap for analysis
	public static void setBitmapSpecs(Bitmap whatBitmap) {
		imgHeight = whatBitmap.getHeight();
		imgWidth = whatBitmap.getWidth();

		// TODO: Create algorithm for incrementation
		// Horizontal and Vertical increments set for a 480X720 image
		verticalInc = 80;
		horizontalInc = 72;
		gridHeight = (imgHeight / verticalInc) + 1; // +1 to include borders
		gridWidth = (imgWidth / horizontalInc) + 1;
		//Initialize Arrays------------------------------------>								
			controlRgbArray = new int[gridHeight][gridWidth];
			rgbArray = new int[gridHeight][gridWidth]; 	
			resultantRgbArray = new int[gridHeight][gridWidth];
			Log.d("Process", "Arrays initialized");
		//Initialize Arrays--------------------------------------^
		controlMean = calculateMean(controlRgbArray); //For testing purposes

		

	}
	public static void setBitmap(Bitmap whatBitmap){
		bitmapToScan = whatBitmap;
		// Control image is always the previous frame
		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				controlRgbArray[i][j] = rgbArray[i][j];
			}
		}
	}

	//TODO: Create a better pixel grid
	// Extract RGB values from bitmap
	public static void analyzeBitmap() {
		int i;
		int j;
		int rowIndex = 0;
		int colIndex = 0;
		//Log.d("Process", "Bitmap Extraction is Go!");

		rgbArray = new int[gridHeight][gridWidth];

		for (i = 0; i <= imgHeight; i = (i + verticalInc)) {
			if (i == imgHeight) {
				i = imgHeight - 1;
			}
			colIndex = 0;
			for (j = 0; j <= imgWidth; j = (j + horizontalInc)) {
				if (j == imgWidth) {
					j = imgWidth - 1;
				}

				baseValue = bitmapToScan.getPixel(j, i);
				redValue = Color.red(baseValue);
				greenValue = Color.green(baseValue);
				blueValue = Color.blue(baseValue);

				rgbArray[rowIndex][colIndex] = redValue;

				//Log.d("Process: ", "Pixel: "+j+","+i);
				//Log.d("Process","Value: "+rgbArray[rowIndex][colIndex]);

				colIndex++;
			}

			rowIndex++;
		}
		Log.d("Process", "Analysis: Data Extraction Finished");

	}

	public static boolean statisticalAnalysis() {
		//Log.d("Process", "Analysis is Go!");

		// Values
		double mean;
		double standardDev;

		// 1. Set control image
		if (runIndex == 0) {
			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					controlRgbArray[i][j] = rgbArray[i][j];
					// Log.d("Process",
					// "Analysis: controlVal:"+controlRgbArray[i][j]);

				}
			}
			return false;
		}
		// 2. Subtract next image from control
		else {
			for (int i = 0; i < gridHeight; i++) {
				for (int j = 0; j < gridWidth; j++) {
					int controlVal = controlRgbArray[i][j];
					int imageVal = rgbArray[i][j];
					// Log.d("Process", "Analysis: controlVal:"+controlVal);
					// Log.d("Process","Analysis: val: "+imageVal);
					resultantRgbArray[i][j] = (controlVal - imageVal);
					// Log.d("Process", "resultant:"+resultantRgbArray[i][j]);
				}
			}

			// 3. Find Standard Deviation of resultantRgbArray
			double frameMean = calculateMean(rgbArray);
			mean = calculateMean(resultantRgbArray);
			Log.d("Data", "----------");
			Log.d("Data", "Analysis: Mean of Control: " + controlMean);
			Log.d("Data", "Mean of Frame: " + frameMean);
			// 4. Find Standard Deviation of the resultantRgbArray
			standardDev = calculateStandardDeviation(mean, resultantRgbArray);
			Log.d("Data", "Analysis: Standard Dev:" + standardDev);
			Log.d("Data", "----------");
			Log.d("Process", "Statistics Finished");
			
			//Previous value: 25
			//Current value: 15
			if(standardDev >= 25){
				return true;
			}
			else{
				return false;
			}
		}
	}

	private static double calculateMean(int[][] whatArray) {
		int sum = 0;
		int total = 0;
		double mean;

		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				sum = sum + whatArray[i][j];
				total++;
			}
		}
		mean = (double) sum / (double) total;
		return mean;

	}

	private static double calculateStandardDeviation(double whatMean,
			int[][] whatArray) {
		double mean = whatMean;
		double tempValue;
		int total = gridWidth * gridHeight;

		// Squared Difference Values
		double meanOfSD;
		double squaredDifference;
		double sumOfSD = 0;

		double standardDeviation;

		// Gets the squared difference of mean & value
		// (mean - value)^2
		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				tempValue = whatArray[i][j];

				squaredDifference = Math.pow(mean - tempValue, 2);
				sumOfSD = squaredDifference + sumOfSD;
			}
		}

		// mean of squared differences
		meanOfSD = sumOfSD / total;
		standardDeviation = Math.sqrt(meanOfSD);
		//Find the mean SD 
		sumSD = standardDeviation + sumSD;
		meanSD = sumSD/count;
		count++;
		Log.d("Data","Mean SD: "+meanSD);
		
		return standardDeviation;

	}

}

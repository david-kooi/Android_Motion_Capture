package com.kooi.david.capture;

import android.graphics.Color;

import android.graphics.Bitmap;
import android.util.Log;

import java.lang.Math;

public class ImageAnalysis {
	
	//Test Values-------->
	static double meanSD;
	static int count = 1;
	static double sumSD;
	
	//Bitmap------------------->
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
	static double rgbArray[][];
	static double resultantRgbArray[][];
	static double controlRgbArray[][];

	// Control Values----------------->
	static double TEMPMEAN = 0;
	static double controlMean = 0;
	static int runIndex = 0; // How many times ImageAnalysis has been run

	public static void setBitmapSpecs(Bitmap whatBitmap) {
		imgHeight = whatBitmap.getHeight();
		imgWidth = whatBitmap.getWidth();

		// TODO: Create algorithm for incrementation
		// Horizontal and Vertical increments set for a 480X720 image
		//Grid: 8X12
		verticalInc = 60;
		horizontalInc = 60;
		gridHeight = (imgHeight / verticalInc) + 1;
		gridWidth = (imgWidth / horizontalInc) + 1;
		//Initialize Arrays------------------------------------>								
		controlRgbArray = new double[gridHeight][gridWidth];
		rgbArray = new double[gridHeight][gridWidth]; 	
		resultantRgbArray = new double[gridHeight][gridWidth];
		Log.d("Process", "Arrays initialized");
		//Initialize Arrays--------------------------------------^

	}
	//Sets what bitmap is to be analyzed
	public static void setBitmap(Bitmap whatBitmap){
		bitmapToScan = whatBitmap;
		// Control image is always the previous frame
		for (int i = 0; i < gridHeight; i++) {
			for (int j = 0; j < gridWidth; j++) {
				controlRgbArray[i][j] = rgbArray[i][j];
			}
		}
		controlMean = calculateMean(controlRgbArray); //For testing purposes

	}

	//TODO: Create a better pixel grid
	// Extract RGB values from bitmap
	public static void extractDataFromBitmap() {
		int i;
		int j;

		//Log.d("Process", "Bitmap Extraction is Go!");

		rgbArray = new double[gridHeight][gridWidth];

		for (i = 0; i < gridHeight; i++) {
			for (j = 0; j < gridWidth; j++) {


				baseValue = bitmapToScan.getPixel(j, i);
				redValue = Color.red(baseValue);
				greenValue = Color.green(baseValue);
				blueValue = Color.blue(baseValue);

				rgbArray[i][j] = redValue;

				// Log.d("Process: ", "Pixel: "+j+","+i);
				// Log.d("Process","Value: "+rgbArray[rowIndex][colIndex]);

			}

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
					double controlVal = controlRgbArray[i][j];
					double imageVal = rgbArray[i][j];
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
			if(standardDev >= 15){
				return true;
			}
			else{
				return false;
			}
		}
	}

	private static double calculateMean(double[][] whatArray) {
		double sum = 0;
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
			double[][] whatArray) {
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

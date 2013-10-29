package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class DawnImagerSun {
	private static final String DAWN_VESTA_URL = "http://neo.jpl.nasa.gov/orbits/fullview1.jpg";
	private static final String FILE_SAVE_FORMAT = "yyyy-MM-dd__HHmm";
	private static final String STATUS_FILE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static String SAVE_DIRECTORY = "C:/Users/Adam/Desktop/"; 
	private static String STATUS_FILE = "status-sun.txt";

	private static final int SLEEP_TIME_BETWEEN_ATTEMPTS = 1000 * 60 * 60; //Sleep for 1 hour
	
	public static void main(String[] args) {
		if(args.length > 1){
			System.out.println("USAGE: dawnimager [savedirectory]");
			System.exit(0);
		} else if(args.length == 1){
			SAVE_DIRECTORY = args[0];
		}
		
		BufferedImage vestaImg = null;
		BufferedImage prevImg = null;
		while(true){
			URL dawnVestaURL = null;
			try {
				dawnVestaURL = new URL(DAWN_VESTA_URL);
			} catch (MalformedURLException e1) {
//				System.err.println("Error with URL");
			}
			if(dawnVestaURL != null){
				try {
					vestaImg = ImageRetriever.retrieve(dawnVestaURL);
				} catch (IOException e) {
//					System.err.println("Error reading file");
				}
				
				if(vestaImg != null){
					if(prevImg == null || !ImageUtil.equals(vestaImg, prevImg)){
						try {
							ImageUtil.saveImage(vestaImg, SAVE_DIRECTORY, FILE_SAVE_FORMAT);
//							System.out.println("Saved image!");
						} catch (IOException e) {
//							System.err.println("Error saving file");
						}
					} else {
//						System.out.println("Skipped image!");
					}
				}
			}
			
			prevImg = vestaImg;
			
			try{
				FileWriter status = new FileWriter(new File(STATUS_FILE));
				String timeStamp = ImageUtil.getUTCTimeStamp(STATUS_FILE_FORMAT);
				status.write("Sun: last updated " + timeStamp + System.getProperty("line.separator"));
				status.close();
			} catch(Exception e){
//				e.printStackTrace();
			}
			
			long now = System.currentTimeMillis();
			long tryAgainTime = now + SLEEP_TIME_BETWEEN_ATTEMPTS;
			
			while(System.currentTimeMillis() < tryAgainTime){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
//					System.err.println("Thread interrupted");
				}
			}
		}
	}
}

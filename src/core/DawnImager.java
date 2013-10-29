package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class DawnImager {
	private static final String DAWN_TRAJ_URL = "http://neo.jpl.nasa.gov/orbits/fulltraj.jpg";
	private static final String DAWN_SUN_URL = "http://neo.jpl.nasa.gov/orbits/fullview1.jpg";
	private static final String DAWN_CERES_URL = "http://neo.jpl.nasa.gov/orbits/fullview2.jpg";
	private static final String DAWN_EARTH_URL = "http://neo.jpl.nasa.gov/orbits/fullview3.jpg";
	private static final String DAWN_VESTA_URL = "http://neo.jpl.nasa.gov/orbits/fullview4.jpg";
	
	private static final HashMap<String, Boolean> checkedLast = new HashMap<String, Boolean>(5);
	
	private static String CELESTIAL_BODY = "all";
	
	private static final String FILE_SAVE_FORMAT = "yyyy-MM-dd__HHmm";
	private static final String STATUS_FILE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static String SAVE_DIRECTORY_BASE = "/home/adam/Dropbox/Java_Projects/DawnImager/"; 

	private static final int SLEEP_TIME_BETWEEN_ATTEMPTS = 1000 * 60 * 60; //Sleep for 1 hour
	
	public static void main(String[] args) {
		if(args.length > 2){
			System.out.println("USAGE: dawnimager [celestial body] [savedirectory]");
			System.exit(0);
		} else if(args.length == 1){	
			if(!(args[0].equals("vesta") || args[0].equals("ceres") || args[0].equals("sun") 
						|| args[0].equals("earth") || args[0].equals("traj") || args[0].equals("all"))){
				System.out.println("Invalid celestial body: " + args[0]);
				System.exit(0);
			}
			
			CELESTIAL_BODY = args[0];
		} else if(args.length == 2){
			if(!(args[0].equals("vesta") || args[0].equals("ceres") || args[0].equals("sun")
					|| args[0].equals("earth") || args[0].equals("traj") || args[0].equals("all"))){
				System.out.println("Invalid celestial body: " + args[0]);
				System.exit(0);
			}
			
			SAVE_DIRECTORY_BASE = args[1];
			CELESTIAL_BODY = args[0];
		}
		
		String[] names = {"vesta", "earth", "sun", "ceres", "traj"};
		for(String s : names){
			checkedLast.put(s, false);
		}
		
		BufferedImage vestaImg = null;
		BufferedImage prevVestaImg = null;
		BufferedImage ceresImg = null;
		BufferedImage prevCeresImg = null;
		BufferedImage sunImg = null;
		BufferedImage prevSunImg = null;
		BufferedImage earthImg = null;
		BufferedImage prevEarthImg = null;
		BufferedImage trajImg = null;
		BufferedImage prevTrajImg = null;
		while(true){
			URL dawnVestaURL = null;
			URL dawnCeresURL = null;
			URL dawnSunURL = null;
			URL dawnEarthURL = null;
			URL dawnTrajURL = null;
			try {
				dawnVestaURL = new URL(DAWN_VESTA_URL);
				dawnCeresURL = new URL(DAWN_CERES_URL);
				dawnSunURL = new URL(DAWN_SUN_URL);
				dawnEarthURL = new URL(DAWN_EARTH_URL);
				dawnTrajURL = new URL(DAWN_TRAJ_URL);
			} catch (MalformedURLException e1) {}
			
			if(CELESTIAL_BODY.equals("vesta") || CELESTIAL_BODY.equals("all")){
				vestaImg = doBody(dawnVestaURL, prevVestaImg, "vesta");
				prevVestaImg = vestaImg;
			} if(CELESTIAL_BODY.equals("ceres") || CELESTIAL_BODY.equals("all")){
				ceresImg = doBody(dawnCeresURL, prevCeresImg, "ceres");
				prevCeresImg = ceresImg;
			} if(CELESTIAL_BODY.equals("sun") || CELESTIAL_BODY.equals("all")){
				sunImg = doBody(dawnSunURL, prevSunImg, "sun");
				prevSunImg = sunImg;
			} if(CELESTIAL_BODY.equals("earth") || CELESTIAL_BODY.equals("all")){
				earthImg = doBody(dawnEarthURL, prevEarthImg, "earth");
				prevEarthImg = earthImg;
			} if(CELESTIAL_BODY.equals("traj") || CELESTIAL_BODY.equals("all")){
				trajImg = doBody(dawnTrajURL, prevTrajImg, "traj");
				prevTrajImg = trajImg;
			}
				
			
			try{
				FileWriter status = new FileWriter(new File("status-" + CELESTIAL_BODY + ".txt"));
				String timeStamp = ImageUtil.getUTCTimeStamp(STATUS_FILE_FORMAT);
				status.write(CELESTIAL_BODY + ": last updated " + timeStamp + System.getProperty("line.separator"));
				status.close();
			} catch(Exception e){}
			
			long now = System.currentTimeMillis();
			long tryAgainTime = now + SLEEP_TIME_BETWEEN_ATTEMPTS;
			
			while(System.currentTimeMillis() < tryAgainTime){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	public static String removeNewLine(String str){
		String tmp = str.replaceAll(System.getProperty("line.separator"), "");
		for(int i=0; i < tmp.length(); i++){
			char c = tmp.charAt(i);
			if(c == 0){
				return tmp.substring(0, i);
			}
		} 
		
		return null;
	}
	
	public static BufferedImage getLastImg(String name){
		BufferedImage lastImg = null;
		try {
			Process prc = null;
			prc = Runtime.getRuntime().exec("sh " + SAVE_DIRECTORY_BASE + "Images/getLastImage.sh " + name);
	
			if(prc != null){
				byte[] bytes = new byte[500];
				prc.getInputStream().read(bytes);
				String lastImgName = new String(bytes);
				
				lastImgName = removeNewLine(lastImgName);
				String imgPath = SAVE_DIRECTORY_BASE + "Images/" + name + "/" + lastImgName;
				File f = new File(imgPath);
				lastImg = ImageRetriever.retrieve(f);
			}
		} catch (IOException e1) {}
		
		return lastImg;
	}
	
	public static BufferedImage doBody(URL url, BufferedImage prevImg, String name){
		BufferedImage img = null;
		if(url != null){
			try {
				img = ImageRetriever.retrieve(url);
			} catch (IOException e) {}
			
			if(img != null){
				if(prevImg == null || !ImageUtil.equals(img, prevImg)){
					if(!checkedLast.get(name)){
						BufferedImage lastImg = getLastImg(name);
						checkedLast.put(name, true);
						
						if(lastImg == null || !ImageUtil.equals(img, lastImg)){
							try {
								ImageUtil.saveImage(img, SAVE_DIRECTORY_BASE + "Images/" + name + "/", FILE_SAVE_FORMAT);
							} catch (IOException e) {}
						} else{
							System.out.println("Already have latest image for " + name);
						}
					} else{
						try {
							ImageUtil.saveImage(img, SAVE_DIRECTORY_BASE + "Images/" + name + "/", FILE_SAVE_FORMAT);
						} catch (IOException e) {}
					}
				}
			}
		}
		
		return img;
	}
}

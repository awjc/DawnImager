package core;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.TimeZone;

import javax.imageio.ImageIO;

public class ImageUtil {
	public static boolean equals(BufferedImage b1, BufferedImage b2) {
		if (b1 == b2) {
			return true;
		} // true if both are null
		if (b1 == null || b2 == null) {
			return false;
		}
		if (b1.getWidth() != b2.getWidth()) {
			return false;
		}
		if (b1.getHeight() != b2.getHeight()) {
			return false;
		}
		for (int i = 0; i < b1.getWidth(); i++) {
			for (int j = 0; j < b1.getHeight(); j++) {
				if (b1.getRGB(i, j) != b2.getRGB(i, j)) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void saveImage(BufferedImage img, String directory, String format) throws IOException{
		String timeStamp = getUTCTimeStamp(format);
		
		File outFile = new File(directory + timeStamp + ".png");
		ImageIO.write(img, "PNG", outFile);
	}
	
	public static void saveImage(BufferedImage img, String filename) throws IOException{
		File outFile = new File(filename);
		ImageIO.write(img, "PNG", outFile);
	}
	
	public static String getUTCTimeStamp(String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(new Date());
	}

	public static boolean equals(BufferedInputStream s1, BufferedInputStream s2) throws IOException{
		byte[] buffer1 = new byte[1024];
		byte[] buffer2 = new byte[1024];
		int numRead1, numRead2;

		do{
			numRead1 = s1.read(buffer1);
			numRead2 = s2.read(buffer2);
			
			if(!Arrays.equals(buffer1, buffer2)){
				return false;
			}
		} while(numRead1 != -1 && numRead2 != -1);
		
		return true;
	}
}

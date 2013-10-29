package core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImageRetriever {
	public static BufferedImage retrieve(URL url) throws IOException {
		return ImageIO.read(url);
	}
	
	public static BufferedImage retrieve(File file) throws IOException {
		return ImageIO.read(file);
	}
}

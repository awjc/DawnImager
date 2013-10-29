package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class DawnSync {
	private static String srcDir = "/mnt/csdrive/Dawn/Images/";
	private static String destDir = "/home/adam/Dropbox/Java_Projects/DawnImager/Images/";
	//private static String srcDir = "../../../../../../../z/Dawn/Images/";
	//private static String destDir = "../Images/";

	private static ArrayList<String> checksums;
	
	public static void main(String[] args){
		List<?> names = Arrays.asList(new String[]{ "sun", "earth", "ceres", "traj", "vesta" });
		
		Options options = new Options();
		options.addOption("d", true, "Destination directory");
		options.addOption("s", true, "Source directory");
		
		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try{
			cmd = parser.parse(options, args);
			if(cmd.hasOption("s")){
				srcDir = cmd.getOptionValue("s");
			}
			if(cmd.hasOption("d")){
				destDir = cmd.getOptionValue("d");
			}
			
			if(cmd.getArgList().size() > 0){
				names = cmd.getArgList();
			}
		} catch(ParseException e){
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("BODIES: " + names);
		System.out.println("DEST: " + destDir);
		System.out.println("SRC: " + srcDir);
		System.out.println();
		
		for(Object name : names){
			doBody(name.toString());
		}
	}

	public static void doBody(String name){
		System.out.println("For body: " + name);
		List<String> srcFiles = null;
		List<String> destFiles = null;
		try{
			readCheckSums(name);
			
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".png") || name.endsWith(".jpg");
				}
			};
			srcFiles = Arrays.asList(new File(srcDir + name).list(filter));
			destFiles = Arrays.asList(new File(destDir + name).list(filter));
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("Error indexing files");
			return;
		}

		if(srcFiles == null){
			System.err.println("Error indexing source files from: " + srcDir);
			return;
		} else if(destFiles == null){
			System.err.println("Error indexing destination files from: " + destDir);
			return;
		}

		System.out.println("Source: " + srcFiles.size() + ", Dest: " + destFiles.size());
		int nFiles = 0;
		try{
			for(String srcFile : srcFiles){

				String srcName = srcDir + name + "/" + srcFile;
//				for(String destFile : destFiles){
//					String destName = destDir + name + "/" + destFile;

//					BufferedInputStream srcImg = new BufferedInputStream(new FileInputStream(new File(srcName)));
//					BufferedInputStream destImg = new BufferedInputStream(new FileInputStream(new File(destName)));

//					if(ImageUtil.equals(srcImg, destImg)){
						// if(getMD5Checksum(srcName).equals(getMD5Checksum(destName))){
//					}

//					srcImg.close();
//					destImg.close();
//				}

				if(!alreadyHaveCheckSum(srcName)){
					String destName = destDir + name + "/" + srcFile;
					System.out.println("Syncing: " + destName + "...");
					ImageUtil.saveImage(ImageIO.read(new File(srcName)), destName);
					writeMostRecentCheckSum(destDir + name + "/checksums.txt");
					nFiles++;
				} else {
					System.out.println("Checking: " + srcName + "...");
				}
			}
		} catch(Exception e){
			e.printStackTrace();
			System.err.println("Error reading files");
			return;
		}

		if(nFiles > 0)
			System.out.println("Successfully synced " + nFiles + (nFiles == 1 ? " file." : " files.") + System.getProperty("line.separator"));
		else
			System.out.println("All files up to date!" + System.getProperty("line.separator"));
	}
	
	private static void readCheckSums(String name) throws IOException{
		checksums = new ArrayList<String>();
		File file = new File(destDir + name + "/checksums.txt");
//		System.out.println("FILE: " + destDir + name + "/checksums.txt");
		if(!file.exists()){
			file.createNewFile();
		}
		Scanner in = new Scanner(file);
		while(in.hasNext()){
			checksums.add(in.next());
		}
		in.close();
	}
	
	private static boolean writeMostRecentCheckSum(String filename) throws IOException{
		if(checksums != null && checksums.size() > 0){
			FileWriter out = new FileWriter(new File(filename), true);
			out.write(checksums.get(checksums.size() - 1) + System.getProperty("line.separator"));
			out.close();
			return true;
		} 
		return false;
	}
	
	private static boolean alreadyHaveCheckSum(String filename) throws Exception{
		String checksum = getMD5Checksum(filename);
		boolean alreadyHaveIt = checksums.contains(checksum);
		if(!alreadyHaveIt){
			checksums.add(checksum);
		}
		
		return alreadyHaveIt;
	}

	public static byte[] createChecksum(String filename) throws Exception{
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("MD5");
		int numRead;

		do{
			numRead = fis.read(buffer);
			if(numRead > 0){
				complete.update(buffer, 0, numRead);
			}
		} while(numRead != -1);

		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(String filename) throws Exception{
		byte[] b = createChecksum(filename);
		String result = "";

		for(int i = 0; i < b.length; i++){
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	public static ArrayList<String> parseFileList(String s, String delimiter){
		Scanner in = new Scanner(s);
		in.useDelimiter(delimiter);

		ArrayList<String> files = new ArrayList<String>();
		while(in.hasNext()){
			String ss = in.next();
			if(ss.length() < 1000 && ss.endsWith(".png")){
				files.add(ss);
			}
		}

		in.close();
		return files;
	}
}

package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;

public class DawnGrapher {
//	private static String graphdir = "/home/adam/Dropbox/Matlab/DawnGrapher/";
//	private static String[] imgdirs = { "/home/adam/Dropbox/Java_Projects/DawnImager/Images_Before_November/",
//			"/home/adam/Dropbox/Java_Projects/DawnImager/Images/" };
	
	private static String graphdir = "C:/Users/Adam/Dropbox/Matlab/DawnGrapher/";
	private static String[] imgdirs = { "C:/Users/Adam/Dropbox/Java_Projects/DawnImager/Images_Before_November/",
			"C:/Users/Adam/Dropbox/Java_Projects/DawnImager/Images/" };

	private static boolean USE_MILES = true;

	private static double prevDist = -1;
	private static double prevSpeed = -1;
	private static final double OUTLIER_TOLERANCE = 0.05;
	private static final double FINAL_TOLERANCE = 0.17;
	private static final double FINAL_BIG_TOLERANCE = 0.10;
	private static String prevStr = "2010";

	public static void main(String[] args){
		String[] bodies = { "sun", "earth", "ceres", "vesta" };
		if(args.length > 0){
			bodies = args;
		}

		for(String body : bodies){
			if(body.equals("vesta"))
				USE_MILES = true;
			else
				USE_MILES = false;

			boolean firstDir = true;
			int k = 1;
			
			for(String imgdir : imgdirs){
				System.out.println("From directory: " + imgdir);
				prevStr = "2010";
				List<String> files = null;
				try{
					FilenameFilter filter = new FilenameFilter() {
						@Override
						public boolean accept(File dir, String name) {
							return name.endsWith(".png") || name.endsWith(".jpg");
						}
					};
					files = Arrays.asList(new File(imgdir + body).list(filter));
				} catch(Exception e){
					System.err.println("Error getting file list!");
					e.printStackTrace();
				}

				BufferedWriter out = null;
				try{
					String OUTFILE = graphdir + "dawn-" + body + ".txt";
					out = new BufferedWriter(new FileWriter(new File(OUTFILE), !firstDir));
				} catch(Exception e){
					System.err.println("Error creating output file!");
					e.printStackTrace();
				}

				if(out != null){
					prevDist = -1;
					prevSpeed = -1;
					if(files != null && files.size() > 0){
						for(int i = 0; i < files.size(); i++){
							String toWrite = processFile(imgdir + body + "/" + files.get(i));
							if(toWrite != null){
								try{
									System.out.println("Writing " + body + " #" + String.format("%03d", k++) + ": "
											+ toWrite);
									if(firstDir){
										out.write(toWrite + System.getProperty("line.separator"));
									} else{
										out.append(toWrite + System.getProperty("line.separator"));
									}
								} catch(Exception e){
									System.err.println("Error writing to output file!");
									e.printStackTrace();
								}
							}
						}
					}

					try{
						out.close();
					} catch(Exception e){
						System.err.println("Error closing output file!");
						e.printStackTrace();
					}
				}
				
				firstDir = false;
			}

			System.out.println("===================================================");
		}
	}

	private static int getClosest(String s, String[] list){
		int closest = -1;
		int prevScore = 0;
		for(int j = 0; j < list.length; j++){
			int score = 0;
			String listitem = list[j];
			for(int i = 0; i < listitem.length() && i < s.length(); i++){
				char c = s.charAt(i);
				score += c == listitem.charAt(i) ? 1 : 0;
				score += listitem.contains("" + c) ? 1 : 0;
			}

			if(score > prevScore){
				closest = j;
				prevScore = score;
			}
		}

		return closest;
	}

	private static int matchMonth(String date){
		String[] months = { "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
		HashMap<String, Integer> mistakes = new HashMap<String, Integer>();
		mistakes.put("001", 10);
		mistakes.put("0ct", 10);

		date = date.toLowerCase();
		for(int i = 0; i < months.length; i++){
			if(date.contains(months[i])){
				return i + 1;
			}
		}

		for(String key : mistakes.keySet()){
			if(date.startsWith(key)){
				return mistakes.get(key);
			}
		}

		String monthstr = date.substring(0, 3);
		return getClosest(monthstr, months) + 1;
	}

	private static int matchDay(String date){
		int idx = date.indexOf('.');
		int toReturn = -1;
		if(idx != -1){
			try{
				int nDigits = 0;
				char chars = '\0';
				for(int i = idx + 1; i < date.length(); i++){
					char c = date.charAt(i);
					if(Character.isDigit(c)){
						nDigits++;
						if(nDigits == 2){
							toReturn = Integer.parseInt(new String(new char[]{ chars, c }));
						} else{
							chars = c;
						}
					}
				}
			} catch(Exception e){
				System.out.println(date + " : " + idx);
			}
		}

		return toReturn;
	}

	private static int matchYear(String date){
		if(date.contains("2012")){
			return 2012;
		} else if(date.contains("2013")){
			return 2013;
		} else{
			return -1;
		}
	}

	private static int matchHour(String date){
		int idx = date.indexOf("2012");
		if(idx < 0){
			idx = date.indexOf("2013");
		} if(idx < 0){
			idx = date.indexOf("2014");
		} if(idx < 0){
			idx = date.indexOf("2015");
		}

		if(idx != 0){
			int nDigits = 0;
			char chars = '\0';
			for(int i = idx + 4; i < date.length(); i++){
				char c = date.charAt(i);
				if(Character.isDigit(c)){
					nDigits++;
					if(nDigits == 2){
						return Integer.parseInt(new String(new char[]{ chars, c }));
					} else{
						chars = c;
					}
				}
			}
		}

		return -1;
	}

	private static int matchMinute(String date){
		int idx = date.indexOf(':');
		if(idx != -1){
			int nDigits = 0;
			char chars = '\0';
			for(int i = idx + 1; i < date.length(); i++){
				char c = date.charAt(i);
				if(Character.isDigit(c)){
					nDigits++;
					if(nDigits == 2){
						return Integer.parseInt(new String(new char[]{ chars, c }));
					} else{
						chars = c;
					}
				}
			}
		}

		return -1;
	}

	private static int matchSecond(String date){
		int idx = date.lastIndexOf(':');
		if(idx != -1){
			int nDigits = 0;
			char chars = '\0';
			for(int i = idx + 1; i < date.length(); i++){
				char c = date.charAt(i);
				if(Character.isDigit(c)){
					nDigits++;
					if(nDigits == 2){
						return Integer.parseInt(new String(new char[]{ chars, c }));
					} else{
						chars = c;
					}
				}
			}
		}

		return -1;
	}

	private static Date filterDate(String date){
		int month = matchMonth(date);
		int day = matchDay(date);
		int year = matchYear(date);

		int hour = matchHour(date);
		int minute = matchMinute(date);
		int second = matchSecond(date);

		// String datefmt = String.format("%02d/%02d/%4d", month, day, year);
		// String time = String.format("%02d:%02d:%02d", hour, minute, second);
		// System.out.print(datefmt + " " + time + " ");

		if(month == -1 || day == -1 || year == -1 || hour == -1 || minute == -1 || second == -1){
			// System.out.println("HOUSTON WE HAVE A PROBLEM: " + date);
			return null;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(TimeZone.getTimeZone("UTC"));
		cal.set(year, month - 1, day, hour, minute, second);
		return cal.getTime();
	}

	private static double filterDist(String dist){
		double distance = -1;
		Scanner scan = new Scanner(dist);
		if(scan.hasNextDouble()){
			distance = scan.nextDouble();
		} else{
			int idx = dist.indexOf('.');
			if(idx != -1){
				try{
					distance = Double.parseDouble(dist.substring(0, idx + 2));
				} catch(NumberFormatException e){
					distance = -1;
				}
			}
		}

		scan.close();

		int power = -1;
		String[] nums = { "th0usand", "milli0n" };
		if(distance > 0){
			int tidx = dist.indexOf('t');
			int midx = dist.indexOf('m');
			if(tidx != -1 || midx != -1){
				if(midx != -1 && (midx < tidx || tidx == -1)){
					power = getClosest(dist.substring(midx), nums);
				} else if(tidx != -1){
					power = getClosest(dist.substring(tidx), nums);
				}
			}
		}

		if(power != -1){
			distance = distance * (power == 0 ? 1000 : 1000000);
		}

		// System.out.print("=== Distance: " + String.format("%09.0f", distance)
		// + " km ");
		// if(distance < 0){
		// System.out.println("HOUSTON WE HAVE A PROBLEM: " + dist);
		// }

		return distance;
	}

	private static double filterSpeed(String speed){
		speed = speed.replace(':', '.');
		// speed = speed.replaceAll("-", "");
		// speed = speed.replaceAll("'", "");
		// speed = speed.replaceAll("’", "");
		// speed = speed.replaceAll("mph", "");
		// speed = speed.replaceAll("\"", "");
		// speed = speed.replaceAll("‘", "");
		speed = speed.replaceAll("[^0-9\\.]", "");

		Scanner scan = new Scanner(speed);
		double speedval = -1;
		if(scan.hasNextDouble()){
			speedval = scan.nextDouble();
		} else{
			int idx = speed.indexOf('.');
			if(idx != -1){
				try{
					speedval = Double.parseDouble(speed.substring(0, idx + 3));
				} catch(Exception e){

				}
			}
		}

		scan.close();

		// System.out.println("=== Speed: " + String.format("%.2f", speedval) +
		// " km/s");
		// if(speedval < 0){
		// System.out.println("HOUSTON WE HAVE A PROBLEM: " + speed);
		// }

		// if(speedval < 0){
		// System.out.println("OMG " + speed);
		// }
		return speedval;
	}

	private static String processFile(String wholeFileName){
		try{
			String date = runScript("sh", graphdir + "date.sh", wholeFileName);
			String dist = runScript("sh", graphdir + "objdist" + (USE_MILES ? "miles.sh" : ".sh"), wholeFileName);
			String speed = runScript("sh", graphdir + "objspeed.sh", wholeFileName);

			Date d = filterDate(date);
			double dst = filterDist(dist);
			double spd = filterSpeed(speed);

			if(d == null || dst < 0 || spd < 0){
				if(d != null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
					String toWrite = sdf.format(d) + "-" + String.format("%.0f", dst) + "-"
							+ String.format("%.1f", spd);
					System.out.println("SKIPPING:           " + toWrite);
				}
				return null;
			}

			if(prevDist < 0){
				prevDist = dst;
			}

			if(prevSpeed < 0){
				prevSpeed = spd;
			}

			if(isOutlier(dst, prevDist) || isOutlier(spd, prevSpeed)){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
				String toWrite = sdf.format(d) + "-" + String.format("%.0f", dst) + "-" + String.format("%.1f", spd);
				// System.out.println(dst + " : " + prevDist);
				System.out.println("SKIPPING:           " + toWrite);
				return null;
			}

			prevDist = dst;
			prevSpeed = spd;

			// CONVERT TO microAU
			if(dst > 0)
				dst /= (USE_MILES ? 92.9558073 : 149.597871);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			// System.out.println(sdf.format(d) + " === Distance: " +
			// String.format("%09.0f km", dst)
			// + " === Speed: " + String.format("%2.2f km/s", spd));
			String dateStr = sdf.format(d);
			String toWrite = dateStr + "-" + String.format("%.0f", dst) + "-" + String.format("%.1f", spd);

			if(dateStr.compareTo(prevStr) < 0){
				System.out.println("SKIPPING:           " + toWrite);
				return null;
			}

			prevStr = dateStr;

			return toWrite;
		} catch(IOException e){
			System.err.println("Error running image reading scripts!");
			e.printStackTrace();
		}

		return null;
	}

	private static boolean isOutlier(double val, double val2){
		double tol = 0;
		if(val < 0.55){
			tol = FINAL_TOLERANCE;
		} else if(val2 != prevSpeed && val > 20000 && val < 10000000){
			tol = FINAL_BIG_TOLERANCE;
		} else{
			tol = OUTLIER_TOLERANCE;
		}

		return val == 0 || val2 == 0 || Math.abs((val2 - val)) / (val) > tol;
	}

	private static String runScript(String... cmd) throws IOException{
		ProcessBuilder builder = new ProcessBuilder();
		builder.command(cmd);
		Process prc = builder.start();

		byte[] bytes = new byte[1024];
		prc.getInputStream().read(bytes);

		return new String(bytes);
	}
}

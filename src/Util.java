import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Util {

	private static final Random rand = new Random(System.currentTimeMillis());

	public static final String ANSI_RESET = "\033[0m";
	public static final String ANSI_BLACK = "\033[0;30m";
	public static final String ANSI_RED = "\033[0;31m";
    public static final String ANSI_GREEN = "\033[0;32m";
    public static final String ANSI_YELLOW = "\033[0;33m";
    public static final String ANSI_BLUE = "\033[0;34m";
    public static final String ANSI_MAGENTA = "\033[0;35m";
    public static final String ANSI_CYAN = "\033[0;36m";
    public static final String ANSI_WHITE = "\033[0;37m";

	public static final void setSeed(long seed) {
		rand.setSeed(seed);
	}

	//inclusive min, exclusive max
	public static final int randomInt(int min, int max) {
		return rand.nextInt(max - min) + min;
	}

	public static final float randomFloat(float min, float max) {
		return rand.nextFloat() * (max - min) + min;
	}

	public static final float lerp(float a, float b, float t) {
		return (b - a) * t + a;
	}

	public static boolean isColorSupported() {
		return !System.getProperty("os.name").toLowerCase().startsWith("win");
	}

	public static String colorText(String text, String color) {
		if(isColorSupported())
			return color + text + ANSI_RESET;
		else
			return text;
	}

	public static final void pause(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException ex){}
	}
	
	public static final void log(String line) {
		log("debug.log",line);
	}

	public static final void log(String path, String line) {
		try {	
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(path,true)));
			String timeStamp = new SimpleDateFormat("[yyyy.MM.dd.HH.mm.ss]").format(new Date());

			writer.println(timeStamp + " " + line);
			writer.close();
		}
		catch (IOException ex){}	
	}
}

// Import the Java classes
import java.io.*;

/**
 * Implements console-based log file tailing, or more specifically, tail
 * following: it is somewhat equivalent to the unix command "tail -f"
 */
public class Tail implements LogFileTailerListener {
	/**
	 * The log file tailer
	 */
	private SetLogFileTailer tailer;

	// New instance of reconstruct class
	Reconstruct reconstruct = new Reconstruct();
	
	/**
	 * Creates a new Tail instance to follow the specified file
	 */
	public Tail(File filename) {
		tailer = new SetLogFileTailer(filename, 1000, false);
		tailer.addLogFileTailerListener(this);
		tailer.start();
	}

	/**
	 * A new line has been added to the tailed log file
	 * 
	 * @param line
	 *            The new line that has been added to the tailed log file
	 */
	public void newLogFileLine(String line) {
		//reconstruct.rebuildLog(line);
	}
}
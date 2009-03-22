/**
 * RightLog is designed to read and parse Essbase server or application logs
 * (all versions from Essbase from v.5 until Essbase 11) and to generate clean
 * and readable output as spreadsheet or database ready output for detailed
 * analysis. In order to enhance analysis, options are available on date
 * formating, or better filtering.
 * 
 * @author Sebastien Roux
 */

public class JRightLog {

	/**
	 * Main class
	 * <p>
	 * 
	 * Conmmand line launcher. Redirection to command line parser.
	 * 
	 * @author Sebastien Roux
	 */
	public static void main(String[] args) {

		CLI verifyArguments = new CLI();
		verifyArguments.setArgs(args);
		verifyArguments.parseArgs();
	}
}
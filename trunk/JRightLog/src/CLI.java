
import java.io.File;
import org.apache.commons.cli.*;

/**
 * CLI
 * <p>
 * 
 * Parse and verify command line arguments, displays help content on error
 * 
 * @author Sebastien Roux
 * @mail roux.sebastien@gmail.com
 */
public class CLI {

    public static boolean runGUI;
    private String[] arguments;

    public String[] getArgs() {
        return arguments;
    }

    public void setArgs(String[] arguments) {
        this.arguments = arguments;
    }

    /**
     * Define and parse command line arguments Jakarta CLI library
     */
    @SuppressWarnings("static-access")
    public void parseArgs() {

        // Define options

        // create Options object
        Options options = new Options();

        // add log file option
        @SuppressWarnings("static-access")
        Option logFile = OptionBuilder.withArgName("file[;file2;...]").withLongOpt("input").hasArgs().withValueSeparator(';').withDescription("use specified log(s)").create("i");
        options.addOption(logFile);

        // add output file option
        @SuppressWarnings("static-access")
        Option outputFile = OptionBuilder.withArgName("file").withLongOpt(
                "output").hasArg(true).withDescription(
                "use specified file for output\n" + "if not specified stdout is used").create("o");
        options.addOption(outputFile);

        // add header option
        options.addOption("h", "headers", false, "display headers on top");

        // add date format option - require an argument (iso, eur, us)
        @SuppressWarnings("static-access")
        Option dateFormat = OptionBuilder.withArgName("date format").withLongOpt("date").hasOptionalArgs().withDescription(
                "apply specified date format\n" + "arguments are: EUR, ISO or US").create("d");
        options.addOption(dateFormat);

        // add separator option - require an argument
        @SuppressWarnings("static-access")
        Option separator = OptionBuilder.withArgName("separator").withLongOpt(
                "separator").hasOptionalArg().withDescription(
                "use specified separator for output (use \t or tab for tabulation)").create("s");
        options.addOption(separator);

        // add filter option - require an argument
        @SuppressWarnings("static-access")
        Option filter = OptionBuilder.withArgName("filter").withLongOpt(
                "filter").hasOptionalArg().withDescription(
                "use specified filter").create("f");
        options.addOption(filter);

        // add categories option
        options.addOption("c", "categories", false,
                "display message categories");

        // add help option
        options.addOption("help", false, "display this help");

        // Parse commande line arguments
        CommandLine cmd;

        try {
            Reconstruct reconstruct = new Reconstruct();

            CommandLineParser parser = new GnuParser();

            cmd = parser.parse(options, arguments);

            // input file argument (log file)
            if (cmd.hasOption("i") && getArgs().length >= 2) {

                String[] strInputFile = cmd.getOptionValues("i");

                File[] fInputFile = new File[cmd.getOptionValues("i").length];

                // for each specified file check if it exists and if it is a file
                for (int counter = 0; counter < cmd.getOptionValues("i").length; counter++) {
                    if (!new File(strInputFile[counter]).exists() || !new File(strInputFile[counter]).isFile()) {
                        System.out.println(strInputFile[counter].toString() + " : specified file is not a file or does not exist!\n");
                        displayHelp(options);
                    } else {
                        fInputFile[counter] = new File(strInputFile[counter]);
                    }
                }
                reconstruct.setInputFile(fInputFile);
            } else if (getArgs().length == 0) {
                runGUI = true;
            } else {
                displayHelp(options);
            }

            // output file
            if (cmd.hasOption("o")) {
                String OutputFile = cmd.getOptionValue("o");
                reconstruct.setOutputFile(OutputFile);
            } 

            // categories
            if (cmd.hasOption("c")) {
                reconstruct.setCategories(true);
            }

            // filter
            if (cmd.hasOption("f")) {
                String filterArg = cmd.getOptionValue("f");
                reconstruct.setFilter(filterArg);
            }

            // date format
            if (cmd.hasOption("d")) {
                if (cmd.getOptionValue("d") != null) {
                    String dateFormatArg = cmd.getOptionValue("d").toLowerCase();
                    if (dateFormatArg.equals("eur") || dateFormatArg.equals("iso") || dateFormatArg.equals("us")) {
                        reconstruct.setDateFormat(dateFormatArg);
                    }
                }
            }

            // headers
            if (cmd.hasOption("h")) {
                reconstruct.setHeader(true);
            }

            // delimiter
            if (cmd.hasOption("s")) {
                String separatorArg = cmd.getOptionValue("s");
                reconstruct.setOutputDelimiter(separatorArg);
            }

            // help
            if (cmd.hasOption("help")) {
                displayHelp(options);
            }

            if (runGUI == true) {
                GUI gui = new GUI();
                gui.go();
            } else {
                reconstruct.go();
            }
            
        } // if arguments missing for specified options (-i, -o, -d, -s, -f ...)
        catch (ParseException e) {
            displayHelp(options);
        }
    }

    /**
     * Display usage and help
     */
    private final void displayHelp(Options options) {

        final String HELP_DESC = "DESCRIPTION:\n" + "Parse ANY Essbase (v.5-v.11) server or application logs\n" + "and generate a full custom delimited spreadsheet or database ready output\n" + "for detailed analysis.\n" + "Options available for further analysis: date formating,\n" + "headers, detailed message categories\n";

        final String HELP_USAGESAMPLE = "USAGE SAMPLE:\n" + "";

        final String HELP_REQU = "REQUIREMENTS:\n" + "JRE 1.6 or higher\nCurrent JRE version: " + JavaVersionDisplayApplet() + "\n";

        final String HELP_VERS = "VERSION:\n" + "version 1.0\n";

        final String HELP_AUTH = "AUTHOR:\n" + "Proudly coded & released for the Essbase community by Sebastien Roux <roux.sebastien@gmail.com>\n";

        final String HELP_SITE = "SITE:\n" + "http://code.google.com/p/jrightlog/\n";

        final String HELP_LICE = "LICENCE:\n" + "GNU General Public License version 3 (GPLv3)\n";

        final String HELP_NOTE = "NOTES:\n" + "Use at your own risk!\n" + "You will be solely responsible for any damage\n" + "to your computer system or loss of data\n" + "that may result from the download\n" + "or the use of the following application.\n";

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("<jRightLog -i <file[;file2;...]> [OPTIONS]",
                HELP_DESC + "OPTIONS:\n", options, HELP_REQU + HELP_VERS + HELP_AUTH + HELP_SITE + HELP_LICE + HELP_NOTE);
        System.exit(0);
    }

    public static String JavaVersionDisplayApplet() {
        String jVersion = System.getProperty("java.version");
        /*
        String jVersion = System.getProperty("java.specification.version");

        BigDecimal version = new BigDecimal(System.getProperty("java.specification.version"));

        if (version.compareTo(new BigDecimal("1.5")) < 0) {
        System.out.println("Your Java version is too old. Please install Java 5 or newer.");
        }
         */
        return jVersion;
    }
}
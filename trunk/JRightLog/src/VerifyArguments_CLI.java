
import java.io.File;
import org.apache.commons.cli.*;

/**
 * VerifyArguments_CLI
 * <p>
 * 
 * Verify command line arguments, displays help content on error
 * 
 * @author Sebastien Roux
 */
public class VerifyArguments_CLI {

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
                "use specified separator for ouput").create("s");
        options.addOption(separator);

        // add help option
        options.addOption("help", false, "display this help");

        // add tailer option
        options.addOption("t", "tailer", false, "tail specified log file");

        // add categories option
        options.addOption("c", "categories", false,
                "display message categories");

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

                // for each specified file check if it exist and if it is a file
                // (could be a directory...)
                for (int counter = 0; counter < cmd.getOptionValues("i").length; counter++) {
                    if (new File(strInputFile[counter]).exists() == false || !new File(strInputFile[counter]).isFile()) {
                        System.out.println(strInputFile[counter].toString() + " : specified file is not a file or does not exist!\n");
                        displayHelp(options);
                    } else {
                        fInputFile[counter] = new File(strInputFile[counter]);
                    }
                }
                reconstruct.setInputFile(fInputFile);
            } else if (getArgs().length == 0) {
                displayHelp(options);
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

            // categories
            if (cmd.hasOption("t")) {
                reconstruct.setTailer(true);
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

            // help
            if (cmd.hasOption("help")) {
                displayHelp(options);
            }

            // separator/delimiter
            if (cmd.hasOption("s")) {
                String separatorArg = cmd.getOptionValue("s");
                reconstruct.setOutputDelimiter(separatorArg);
            }

            reconstruct.go();
        } // if arguments missing for specified options (-i, -o, -d, -s ...)
        catch (ParseException e) {
            displayHelp(options);
        }
    }

    /**
     * Display usage and help
     */
    private final void displayHelp(Options options) {

        final String HELP_DESC = "DESCRIPTION:\n" + "Parse ANY Essbase (v.5-v.11) server or application logs\n" + "and generate a full custom delimited spreadsheet or database ready output\n" + "for detailed analysis.\n" + "Options available for further analysis: date formating,\n" + "headers, detailed message categories, tailer...\n";

        final String HELP_REQU = "REQUIREMENTS:\n" + "JRE 1.6 or higher\n";

        final String HELP_VERS = "VERSION:\n" + "version 0.x.b\n";

        final String HELP_AUTH = "AUTHOR:\n" + "Written by Sebastien Roux <sebastien.roux@partake.com>\n" + "PARTAKE CONSULTING FRANCE - http://www.partake.com/ - 2007\n";

        final String HELP_NOTE = "NOTES:\n" + "Use at your own risk!\n" + "You will be solely responsible for any damage\n" + "to your computer system or loss of data\n" + "that may result from the download\n" + "or the use of the following application.\n";

        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("<jRightLog -i <file[;file2;...]> [OPTIONS]",
                HELP_DESC + "OPTIONS:\n", options, HELP_REQU + HELP_VERS + HELP_AUTH + HELP_NOTE);
        System.exit(0);
    }
}
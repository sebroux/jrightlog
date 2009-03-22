
import java.io.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Parse and rebuild Essbase logs
 * 
 * @author Sebastien Roux
 */
public class Reconstruct {

    private static final String FILESEPARATOR = ";";
    /**
     * Input file delimiter constant
     */
    private static final String LOG_DELIMITER = "~^*&:";
    /**
     * Output file delimiter
     */
    private static String delimiter = "tab";
    /**
     * Output file name
     */
    private static String outputFile = "";
    /**
     * Categories
     */
    private static boolean categories = false;
    /**
     *  Filter
     */
    private static String sfilter = "";
    /**
     * Date format
     */
    private static String dateFormat = "default";
    /**
     * Headers
     */
    private static boolean header = false;
    /**
     * Essbase log file(s)
     */
    private static File[] inputFile = null;

    public static void setOutputDelimiter(String output_delimiter) {
        if (!output_delimiter.equals("")) {
            if (output_delimiter.equals("\t")
                    || output_delimiter.equals("\\t")
                    || output_delimiter.equals("tab")) { // handle tab string
                Reconstruct.delimiter = "\t";
            } else {
                Reconstruct.delimiter = output_delimiter;
            }
        }
    }

    public static String getOutputDelimiter() {
        return delimiter;
    }

    public static void setOutputFile(String outputFileName) {
        Reconstruct.outputFile = outputFileName;
    }

    public static String getOutputFile() {
        return outputFile;
    }

    public static boolean getCategories() {
        return categories;
    }

    public static void setCategories(boolean categories) {
        Reconstruct.categories = categories;
    }

    public static String getFilter() {
        return sfilter;
    }

    public static void setFilter(String filter) {
        Reconstruct.sfilter = filter;
    }

    public static String getDateFormat() {
        return dateFormat;
    }

    public static void setDateFormat(String dateFormat) {
        if (dateFormat != null) {
            Reconstruct.dateFormat = dateFormat;
        }
    }

    public static boolean getHeader() {
        return header;
    }

    public static void setHeader(boolean header) {
        Reconstruct.header = header;
    }

    // Convert inputFile array to delimited string
    public static String getInputFile(String delimiter) {

        if (delimiter.equals("")) {
            delimiter = FILESEPARATOR;
        }

        StringBuffer result = new StringBuffer();

        if (inputFile.length > 0) {
            result.append(inputFile[0]);
            for (int counter = 1; counter < inputFile.length; counter++) {
                result.append(delimiter);
                result.append(inputFile[counter]);
            }
        }
        return result.toString();
    }

    public static void setInputFile(File[] inputFile) {
        Reconstruct.inputFile = inputFile;
    }

    public static String getLOG_DELIMITER() {
        return LOG_DELIMITER;
    }

    /**
     * Main class
     * <p>
     * Handle the following cases: - tailer enabled (-t), - no output file
     * defined in command line, - output file defined
     *
     * @author Sebastien Roux
     */
    public void go() {

        String line = null;
        String delim = getOutputDelimiter();
        String filter = getFilter();

        int counter = 0;

        // if no output file defined
        if (getOutputFile().equals("")) {

            // for (File file : inputFile) {
            for (int fileCounter = 0; fileCounter < inputFile.length; fileCounter++) {
                try {
                    // File input
                    FileReader reader = new FileReader(inputFile[fileCounter]);
                    BufferedReader buffer = new BufferedReader(reader);
                    // header
                    if (counter == 0) {
                        setHeader(delim);
                    }
                    // rebuild log (main)
                    rebuildLog(line, buffer, delim, filter);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                counter++;
            }
        } // if output file defined
        else {

            for (int fileCounter = 0; fileCounter < inputFile.length; fileCounter++) {
                try {
                    // File input
                    FileReader reader = new FileReader(inputFile[fileCounter]);
                    BufferedReader buffer = new BufferedReader(reader);

                    // File output
                    File myOutputFile = new File(getOutputFile());
                    FileWriter out;

                    // if first file creates output
                    if (counter == 0) {
                        out = new FileWriter(myOutputFile);
                        // header
                        setHeader(out, delim);
                    } // else append to output
                    else {
                        out = new FileWriter(myOutputFile, true);
                    }

                    // rebuild log (main)
                    rebuildLog(line, buffer, out, delim, filter);

                    buffer.close();
                    out.flush();
                    out.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                counter++;
            }
        }
    }

    /**
     * Parse from specified log file to file output
     *
     * @param line
     *            current line being rode in log file
     * @param buffer
     *            input file buffer
     * @param out
     *            output file
     *
     * @author Sebastien Roux
     */
    private void rebuildLog(String line, BufferedReader buffer, FileWriter out, String delim, String filter) {

        Perl5Util regEx = new Perl5Util();

        // Read input file
        try {

            while ((line = buffer.readLine()) != null) {

                // Skip empty rows
                if (line.equals("")) {
                    continue;
                } else if (regEx.match("/^\\[(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/",
                        line)) {
                    // Replace 1st opening bracket ([) by nothing
                    line = regEx.substitute("s/^\\[//", line);
                    // Replace 1st closing bracket (]) with delimiter
                    line = regEx.substitute("s/\\]/" + delim + "/",
                            line);

                    // Replace 4 1st whitespaces with delimiter
                    for (int counter = 0; counter <= 3; counter++) {
                        line = regEx.substitute("s/ /" + delim + "/",
                                line);
                    }
                    // Replace slash delimiters (/) with delimiter
                    line = regEx.substitute("s/\\//" + delim + "/g",
                            line);

                    // Date
                    if (!dateFormat.equals("default")) {
                        line = changeDateFormat(line, delim);
                    }

                    // Categories
                    if (categories == true) {
                        line = setEssMsgCat(line, delim);
                    }

                    // Join with next line if not empty
                    String nextLine;
                    while ((nextLine = buffer.readLine()) != null) {
                        line = line + nextLine;
                        break;
                    }
                } else if (regEx.match("/^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/", line)) {

                    List list = new ArrayList();

                    // Split with delimiter
                    regEx.split(list, "/[" + LOG_DELIMITER + "]/", line);

                    StringBuffer sb = new StringBuffer();
                    line = sb.append((String) list.get(0)).append(
                            (String) delimiter).append(
                            (String) list.get(1)).append(
                            (String) delimiter).append(
                            (String) list.get(2)).append(
                            (String) delimiter).append(
                            (String) list.get(3)).append((String) ":").append(
                            (String) list.get(4)).append((String) ":").append(
                            (String) list.get(5)).append(
                            (String) delimiter).append(
                            (String) list.get(6)).append(
                            (String) delimiter).append(
                            (String) list.get(7)).append(
                            (String) delimiter).append(
                            (String) list.get(8)).append(
                            (String) delimiter).append(
                            (String) list.get(9)).append(
                            (String) delimiter).append(
                            (String) list.get(10)).append(
                            (String) delimiter).append(
                            (String) list.get(11)).append((String) "").append(
                            (String) list.get(12)).append((String) "").append(
                            (String) list.get(13)).toString();

                    for (int counter = 14; counter <= list.size() - 1; counter++) {
                        line = sb.append((String) " ").append(
                                (String) list.get(counter)).toString();
                    }

                    // Date
                    if (!getDateFormat().equals("default")) {
                        line = changeDateFormat(line, delim);
                    }

                    // Categories
                    if (getCategories() == true) {
                        line = setEssMsgCat(line, delim);
                    }
                } else {
                    StringBuffer sb = new StringBuffer();

                    if (getCategories() == true) {
                        if (getDateFormat().equals("default")) {
                            for (int counter = 1; counter <= 12; counter++) {
                                sb.append((String) delimiter);
                            }
                        } else {
                            for (int counter = 1; counter <= 9; counter++) {
                                sb.append((String) delimiter);
                            }
                        }
                    } else {
                        if (getDateFormat().equals("default")) {
                            for (int counter = 1; counter <= 11; counter++) {
                                sb.append((String) delimiter);
                            }
                        } else {
                            for (int counter = 1; counter <= 8; counter++) {
                                sb.append((String) delimiter);
                            }
                        }
                    }
                    line = sb.append((String) line).toString();
                }

                // Common cases
                {
                    // Replace 2 first brackets ((,)) with delimiter
                    for (int counter = 1; counter <= 2; counter++) {
                        line = regEx.substitute("s/[()]/" + delimiter + "/", line);
                    }
                    // Replacing multi-space by single space
                    line = regEx.substitute("s/ +/ /g", line);
                    // Deleting eol tab/space
                    line = regEx.substitute("s/[ \t]+$//g", line);
                    // Replacing single quote by double qsuotes
                    line = regEx.substitute("s/'/\"/g", line);
                }

                // Write output
                if (!line.equals("") && regEx.match("/^.*" + filter + ".*$/", line)) {
                    out.write(line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Parse from specified log file to console output (stdout)
     *
     * @param line
     *            current line being rode in log file
     * @param buffer
     *            input file buffer
     *
     * @author Sebastien Roux
     */
    private void rebuildLog(String line, BufferedReader buffer, String delim, String filter) {

        Perl5Util regEx = new Perl5Util();

        // Read input file
        try {

            while ((line = buffer.readLine()) != null) {

                // Skip empty rows
                if (line.equals("")) {
                    continue;
                } else if (regEx.match("/^\\[(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/",
                        line)) {
                    // Replace 1st opening bracket ([) by nothing
                    line = regEx.substitute("s/^\\[//", line);
                    // Replace 1st closing bracket (]) with delimiter
                    line = regEx.substitute("s/\\]/" + delimiter + "/",
                            line);
                    // Replace 4 1st whitespaces with delimiter
                    for (int counter = 0; counter <= 3; counter++) {
                        line = regEx.substitute("s/ /" + delimiter + "/",
                                line);
                    }
                    // Replace slash delimiters (/) with delimiter
                    line = regEx.substitute("s/\\//" + delimiter + "/g",
                            line);

                    // Date
                    if (!dateFormat.equals("default")) {
                        line = changeDateFormat(line, delim);
                    }

                    // Categories
                    if (categories == true) {
                        line = setEssMsgCat(line, delim);
                    }

                    // Join with next line if not empty
                    String nextLine;
                    while ((nextLine = buffer.readLine()) != null) {
                        line = line + nextLine;
                        break;
                    }
                } else if (regEx.match("/^(Mon|Tue|Wed|Thu|Fri|Sat|Sun)/", line)) {

                    List list = new ArrayList();

                    // Split with delimiter
                    regEx.split(list, "/[" + LOG_DELIMITER + "]/", line);

                    StringBuffer sb = new StringBuffer();
                    line = sb.append((String) list.get(0)).append(
                            (String) delim).append(
                            (String) list.get(1)).append(
                            (String) delim).append(
                            (String) list.get(2)).append(
                            (String) delim).append(
                            (String) list.get(3)).append((String) ":").append(
                            (String) list.get(4)).append((String) ":").append(
                            (String) list.get(5)).append(
                            (String) delim).append(
                            (String) list.get(6)).append(
                            (String) delim).append(
                            (String) list.get(7)).append(
                            (String) delim).append(
                            (String) list.get(8)).append(
                            (String) delim).append(
                            (String) list.get(9)).append(
                            (String) delim).append(
                            (String) list.get(10)).append(
                            (String) delim).append(
                            (String) list.get(11)).append((String) "").append(
                            (String) list.get(12)).append((String) "").append(
                            (String) list.get(13)).toString();

                    for (int counter = 14; counter <= list.size() - 1; counter++) {
                        line = sb.append((String) " ").append(
                                (String) list.get(counter)).toString();
                    }

                    // Date
                    if (!getDateFormat().equals("default")) {
                        line = changeDateFormat(line, getOutputDelimiter());
                    }

                    // Categories
                    if (getCategories() == true) {
                        line = setEssMsgCat(line, getOutputDelimiter());
                    }
                } else {
                    StringBuffer sb = new StringBuffer();

                    if (getCategories() == true) {
                        if (getDateFormat().equals("default")) {
                            for (int counter = 1; counter <= 12; counter++) {
                                sb.append((String) delim);
                            }
                        } else {
                            for (int counter = 1; counter <= 9; counter++) {
                                sb.append((String) delim);
                            }
                        }
                    } else {
                        if (getDateFormat().equals("default")) {
                            for (int counter = 1; counter <= 11; counter++) {
                                sb.append((String) delim);
                            }
                        } else {
                            for (int counter = 1; counter <= 8; counter++) {
                                sb.append((String) delim);
                            }
                        }
                    }
                    line = sb.append((String) line).toString();
                }

                // Common cases
                {
                    // Replace 2 first brackets ((,)) with delimiter
                    for (int counter = 1; counter <= 2; counter++) {
                        line = regEx.substitute("s/[()]/" + delim + "/", line);
                    }
                    // Replacing multi-space by single space
                    line = regEx.substitute("s/ +/ /g", line);
                    // Deleting eol tab/space
                    line = regEx.substitute("s/[ \t]+$//g", line);
                }

                // Write output
                if (!line.equals("") && regEx.match("/^.*" + filter + ".*$/", line)) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change month format from MMM to MM
     *
     * @param line
     *            current line being rode in log file
     *
     * @author Sebastien Roux
     */
    private String changeMonthFormat(String line) {
        Perl5Util regEx = new Perl5Util();

        if (regEx.match("/(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)/",
                line)) {

            String matchResult = regEx.getMatch().toString();

            String MonthListMMM[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            String MonthListMM[] = {"01", "02", "03", "04", "05", "06", "07",
                "08", "09", "10", "11", "12"};

            for (int counter = 0; counter < MonthListMMM.length; counter++) {
                if (matchResult.equals(MonthListMMM[counter])) {
                    line = regEx.substitute("s/" + matchResult + "/" + MonthListMM[counter] + "/", line);
                    break;
                }
            }
        }
        return line;
    }

    /**
     * Change date format from default to specified format: - European,
     * DD/MM/YYYY - ISO 8601, YYYY-MM-DD - American (US), MM/DD/YYYY
     *
     * @param line
     *            current line being rode in log file
     *
     * @author Sebastien Roux
     */
    private String changeDateFormat(String line, String delim) {

        line = changeMonthFormat(line);

        Perl5Util regEx = new Perl5Util();

        List list = new ArrayList();

        // Split according delimiter
        regEx.split(list, "/[" + delim + "]/", line);

        StringBuffer sb = new StringBuffer();

        // Set date format to ISO 8601 extended style (YYYY-MM-DD)
        if (getDateFormat().equals("iso")) {
            line = sb.append((String) list.get(4)).append((String) "/").append(
                    (String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) delim).toString();
        } // Set date format to US style (MM/DD/YYYY)
        else if (dateFormat.equals("us")) {
            line = sb.append((String) list.get(1)).append((String) "/").append(
                    (String) list.get(2)).append((String) "/").append(
                    (String) list.get(4)).append((String) delim).toString();
        } // Set date format to European style (DD/MM/YYYY)
        else if (dateFormat.equals("eur")) {
            line = sb.append((String) list.get(2)).append((String) "/").append(
                    (String) list.get(1)).append((String) "/").append(
                    (String) list.get(4)).append((String) delim).toString();
        }

        line = sb.append((String) list.get(3)).toString();

        for (int counter = 5; counter <= list.size() - 1; counter++) {
            line = sb.append((String) delim).append(
                    (String) list.get(counter)).toString();
        }

        return line;
    }

    /**
     * Insert information message defined as Error Message Categories in Essbase
     * Database Administrator Guide source: $ARBORPATH\Docs\en\dbag\dlogs.htm
     *
     * @param line
     *            current line being rode in log file
     *
     * @author Sebastien Roux
     */
    // Add info/error message category
    private String setEssMsgCat(String line, String delim) {

        Perl5Util regEx = new Perl5Util();

        if (regEx.match("m/\\d{7,7}/", line)) {

            int matchResult = Integer.parseInt(regEx.getMatch().toString());

            int rangeMin[] = {1001000, 1002000, 1003000, 1004000, 1005000,
                1006000, 1007000, 1008000, 1009000, 1010000, 1011000,
                1012000, 1013000, 1014000, 1015000, 1016000, 1017000,
                1018000, 1019000, 1020000, 1021000, 1022000, 1023000,
                1024000, 1030000, 1040000, 1041000, 1042000, 1043000,
                1050000, 1056000, 1060000, 1061000, 1070000, 1071000,
                1080000, 1081000, 1090000, 1010000, 1100000, 1110000,
                1120000, 1130000, 1140000, 1150000, 1160000, 1170000,
                1180000, 1190000, 1200000, 1201000};

            int rangeMax[] = {1001999, 1002999, 1003999, 1004999, 1005999,
                1006999, 1007999, 1008999, 1009999, 1010999, 1011999,
                1012999, 1013999, 1014999, 1015999, 1016999, 1017999,
                1018999, 1019999, 1020999, 1021999, 1022999, 1023999,
                1024999, 1030999, 1040999, 1041999, 1042999, 1049999,
                1055999, 1059999, 1060999, 1069999, 1070999, 1079999,
                1080099, 1089999, 1099999, 1019999, 1100999, 1119999,
                1129999, 1139999, 1149999, 1159999, 1169999, 1179999,
                1189999, 1199999, 1200999, 9999999};

            String errorComponent[] = {"Report Writer", "General server",
                "Data load", "General server",
                "Backup, export, or validate", "Data cache",
                "Outline restructure",
                "System calls, portable layer, ASD, or Agent",
                "Restoring ASCII data", "Internal (block numbering)",
                "Internal (utilities)", "Calculator", "Requestor",
                "Lock manager", "Alias table", "Report Writer", "Currency",
                "Not currently used", "Database artifacts",
                "Spreadsheet extractor", "SQL Interface", "Security",
                "Partitioning", "Query Extractor", "API",
                "General network", "Network-Named Pipes", "Network-TCP",
                "Not currently used", "Agent", "Not currently used",
                "Outline API", "Not currently used", "Index manager",
                "Not currently used", "Transaction manager",
                "Not currently used", "Rules file processing",
                "Not currently used", "Not currently used",
                "Web Analysis", "Grid API", "Miscellaneous",
                "LRO", "Outline synchronization", "Outline change records",
                "Attributes", "Showcase",
                "Enterprise Integration Services", "Calculator framework",
                "Other"};

            for (int counter = 0; counter < rangeMin.length; counter++) {
                if (matchResult >= rangeMin[counter] && matchResult <= rangeMax[counter]) {
                    line = regEx.substitute("s/" + matchResult + "/" + matchResult + delim + errorComponent[counter] + "/", line);
                    break;
                }
            }
        }
        return line;
    }

    /**
     * Insert header on top of output file
     *
     * @param out
     *            output file
     *
     * @author Sebastien Roux
     */
    private void setHeader(FileWriter out, String delim) {

        String line;

        if (getHeader() == true) {
            if (getCategories() == true) {
                if (!getDateFormat().equals("default")) {
                    line = "date" + delim + "time" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                } else {
                    line = "day" + delim + "month" + delim + "daynum" + delim + "time" + delim + "year" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                }

            } else {
                if (!getDateFormat().equals("default")) {
                    line = "date" + delim + "time" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "description";

                } else {
                    line = "day" + delim + "month" + delim + "daynum" + delim + "time" + delim + "year" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                }
            }
            // Write output
            try {
                // Write to specified file
                out.write(line + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Insert header on top of console output (stdout)
     *
     * @author Sebastien Roux
     */
    // Header insertion at the beginning of the file
    private void setHeader(String delim) {

        String line;

        if (getHeader() == true) {
            if (getCategories() == true) {
                if (!getDateFormat().equals("default")) {
                    line = "date" + delim + "time" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                } else {
                    line = "day" + delim + "month" + delim + "daynum" + delim + "time" + delim + "year" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                }

            } else {
                if (!getDateFormat().equals("default")) {
                    line = "date" + delim + "time" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "description";

                } else {
                    line = "day" + delim + "month" + delim + "daynum" + delim + "time" + delim + "year" + delim + "server" + delim + "application" + delim + "database" + delim + "user" + delim + "level" + delim + "msgcode" + delim + "category" + delim + "description";
                }
            }
            // Write output to console
            System.out.println(line);
        }
    }
}

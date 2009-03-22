
import java.awt.Component;
import java.io.*;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class Config {

    private static String cfgFile = "config.properties";
    private static final String FILESEPARATOR = ";";
    private static Component frame;

    @SuppressWarnings("static-access")
    // Record current parameter in config file
    public static void setConfig() {

        try {
            Properties properties = new Properties();
            FileOutputStream configFile = new FileOutputStream(cfgFile);
            Reconstruct reconstruct = new Reconstruct();

            properties.setProperty("input_file", reconstruct.getInputFile(""));
            properties.setProperty("output_file", reconstruct.getOutputFile().toString());
            properties.setProperty("filter_string", reconstruct.getFilter().toString());
            properties.setProperty("date_format", reconstruct.getDateFormat().toString());
            properties.setProperty("error_message_categories", new Boolean(reconstruct.getCategories()).toString());
            properties.setProperty("header", new Boolean(reconstruct.getHeader()).toString());
            properties.setProperty("output_delimiter", reconstruct.getOutputDelimiter().toString());

            properties.store(configFile, "Configuration file for JRightLog utility");

            configFile.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }

    @SuppressWarnings("static-access")
    public static void getConfig() {

        File file = new File(cfgFile);

        if (file.exists()) {

            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(cfgFile));
                Reconstruct reconstruct = new Reconstruct();

                String[] strInputFile = properties.getProperty("input_file", "").toString().split(FILESEPARATOR);
                File[] fInputFile = new File[strInputFile.length];

                for (int counter = 0; counter < strInputFile.length; counter++) {
                    if (!new File(strInputFile[counter]).exists() || !new File(strInputFile[counter]).isFile()) {

                        JOptionPane.showMessageDialog(frame,
                                strInputFile[counter].toString() + " : specified file is not a file or does not exist!",
                                "Input file(s)",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    } else {
                        fInputFile[counter] = new File(strInputFile[counter]);
                    }
                }
                reconstruct.setInputFile(fInputFile);

                reconstruct.setOutputFile(properties.getProperty("output_file", "").toString());
                reconstruct.setFilter(properties.getProperty("filter_string", "").toString());
                reconstruct.setDateFormat(properties.getProperty("date_format", "default").toString());
                reconstruct.setCategories(Boolean.parseBoolean(properties.getProperty("error_message_categories", "false")));
                reconstruct.setHeader(Boolean.parseBoolean(properties.getProperty("header", "false")));
                reconstruct.setOutputDelimiter(properties.getProperty("output_delimiter", "").toString());

            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(0);
            }
        } else {
            setConfig();
        }
    }
}

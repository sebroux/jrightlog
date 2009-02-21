package jrightlog;

import java.io.*;
import java.util.Properties;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class Config {

    private static String cfgFile = "config.properties";

    @SuppressWarnings("static-access")
    public static void setConfig() {

        try {
            Properties properties = new Properties();
            FileOutputStream configFile = new FileOutputStream(cfgFile);
            Core core = new Core();

            properties.setProperty("input_directory", core.getInputDir().toString());
            properties.setProperty("output_file", core.getOutputFile().toString());
            properties.setProperty("filter_string", core.getFilter().toString());
            properties.setProperty("date_format", core.getDateFormat().toString());
            properties.setProperty("sort_order", core.getSortOrder().toString());
            properties.setProperty("compression", core.getZipOption().toString());

            properties.store(configFile, "Configuration file for JSSAM utility");

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
                Core core = new Core();

                core.setInputDir(properties.getProperty("input_directory", ""));
                core.setOutputFile(properties.getProperty("output_file", ""));
                core.setFilter(properties.getProperty("filter_string", ""));
                core.setDateFormat(properties.getProperty("date_format", "default"));
                core.setSortOrder(properties.getProperty("sort_order", "default"));
                core.setZipOption(Boolean.parseBoolean(properties.getProperty("compression", "false")));
            } catch (IOException ioe) {
                ioe.printStackTrace();
                System.exit(0);
            }
        } else {
            setConfig();
        }
    }
}


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author sebastien roux
 * @mail roux.sebastien@gmail.com
 *
 */
public class Zip {

    private static final String ZIP_EXTENSION = "zip";

    public static void zipOutputFile() throws IOException {
        Reconstruct reconstruct = new Reconstruct();

        @SuppressWarnings("static-access")
        File source = new File(reconstruct.getOutputFile());
        //String sSource = source.getAbsoluteFile().toString();
        String sSource = source.toString();

        // Create a buffer for reading the files
        byte[] buffer = new byte[1024];

        // Create the ZIP file
        @SuppressWarnings("static-access")
        String target = reconstruct.getOutputFile().toString().substring(0, reconstruct.getOutputFile().length() - 3) + ZIP_EXTENSION;
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(target));
            // Compress the files
            FileInputStream in = new FileInputStream(source);

            // Add ZIP entry to output stream.
            out.setLevel(Deflater.BEST_COMPRESSION);
            out.putNextEntry(new ZipEntry(sSource));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }

            out.closeEntry();
            in.close();
            out.close();
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
            System.exit(0);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
            System.exit(0);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(0);
        }
    }
}

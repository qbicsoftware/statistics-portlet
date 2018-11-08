package life.qbic.portal.io;

import life.qbic.portal.presenter.utils.CustomNotification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.ui.Upload;

import java.io.*;

/**
 * @author fhanssen
 * Upload a file to use it as config
 */
public  class MyReceiver implements Upload.Receiver {

    private static final Logger logger = LogManager.getLogger(MyReceiver.class);

    private File file;
    private String filename;

    public OutputStream receiveUpload(String filename, String mimeType) {

        // Create upload stream
        FileOutputStream fileOutputStream; // Stream to write to

        try {

            // Open the file for writing.
            file = new File(filename);
            this.filename = filename;
            fileOutputStream = new FileOutputStream(file);

            logger.info("Successfully uploaded file " + filename);

        } catch (FileNotFoundException e) {

            logger.error("Could not upload file: " + e.toString());
            CustomNotification.error("Error", e.toString());

            return null;
        }
        return fileOutputStream;
    }

    public File getFile(){
        return file;
    }

    public String getFilename() {
        return filename;
    }
}

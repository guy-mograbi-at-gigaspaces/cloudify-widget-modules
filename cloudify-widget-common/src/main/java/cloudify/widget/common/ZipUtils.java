package cloudify.widget.common;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/10/14
 * Time: 9:23 PM
 */
public class ZipUtils {

    private static Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    static public void unzipArchive(File archive, File outputDir)
            throws ZipException {
        try {
            ZipFile zipfile = new ZipFile(archive);
            for (Enumeration<? extends ZipEntry> e = zipfile.entries(); e.hasMoreElements(); ) {
                ZipEntry entry = e.nextElement();
                unzipEntry(zipfile, entry, outputDir);
            }
        } catch (Exception e) {
            String msg = "Failed to extract archive: " + archive;
            logger.error(msg, e);
            throw new ZipException(msg);
        }
    }

    static private void unzipEntry(ZipFile zipfile, ZipEntry entry, File outputDir)
            throws IOException {

        if (entry.isDirectory()) {
            new File(outputDir, entry.getName()).mkdir();
            return;
        }

        File outputFile = new File(outputDir, entry.getName());
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdir();
        }

        logger.debug("Extracting: [{}]", entry);
        BufferedInputStream inputStream = new BufferedInputStream(zipfile.getInputStream(entry));
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.close();
            inputStream.close();
        }
    }

}

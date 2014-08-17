package cloudify.widget.common;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipException;

/**
 *
 *
 * This class is responsible for download+extracting and copying widget resources.
 *
 * The required flow is the following:
 *
 *  - Check if resource already downloaded
 *  - if not: download the resource using a unique ID to identify it. (widget id for example)
 *  - extract it (we assume resources are zipped).
 *
 * When it is time to use the resource:
 *
 *  - copy the downloaded resource to a temporary folder with a UUID attached to it
 *  - delete the copy once we're done with it.

 *
 *
 *
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/10/14
 * Time: 9:14 PM
 */
public class WidgetResourcesUtils {


    static public void download( String url , File dest ){
        try {
            FileUtils.copyURLToFile(new URL(url), dest);
        }catch(Exception e){
            throw new RuntimeException("unable to download url :" + url + " :: " + dest ,e);
        }
    }

    static public void unzip( File archive, File dest ){
        try {
            ZipUtils.unzipArchive( archive, dest );
        } catch (ZipException e) {
            throw new RuntimeException("unable to unzip :: " + archive + " :: " + dest ,e);
        }
    }

    static public void copyDir( File src, File dest ){
        try {
            FileUtils.copyDirectory(src, dest);
        } catch (IOException e) {
            throw new RuntimeException("unable to copy directory:: " + src + " :: " + dest,e);
        }
    }

    static public void deleteDir( File dir ){
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new RuntimeException("unable to delete directory",e);
        }
    }

    static public void deleteFile( File file ){
        FileUtils.deleteQuietly(file);
    }


    static public String getUrlName( String url ){
        try {
            return new File(new URL(url).getFile()).getName();
        } catch (MalformedURLException e) {
            throw new RuntimeException("unable to get name from url :: " + url ,e);
        }
    }


    // takes care of a recipe
    public static class ResourceManager{
        public String uid;
        public String url;
        public String baseDir;
        private static Logger logger = LoggerFactory.getLogger(ResourceManager.class);
        // the dest dir (base + uid )
        public File getDestDir(){

            return new File(baseDir, uid);
        }

        // downloads an archive (zip file) to dest dir
        public void download(){
            logger.info("download [{}] to [{}]", url, getArchiveFile());
            WidgetResourcesUtils.download(url, getArchiveFile());
        }

        // the downloaded archive file
        private File getArchiveFile() {
            return new File( getDestDir(), getUrlName(url));
        }

        // extract the archive to the dest dir
        public void extract(){
            logger.info("extracting [{}] to [{}]", getArchiveFile(), getDestDir());
            WidgetResourcesUtils.unzip( getArchiveFile(), getDestDir() );
        }

        public void deleteArchiveFile(){
            logger.info("deleting [{}]", getArchiveFile());
            WidgetResourcesUtils.deleteFile(getArchiveFile());
        }

        // copies dest dir to target
        public void copy( File target ){
            logger.info("copying [{}] to [{}]", getDestDir(), target);
            WidgetResourcesUtils.copyDir( getDestDir(), target );
        }

        // tells me if the resource already downloaded
        public boolean isExtracted( ){
            return getDestDir().exists();
        }

        /**
         * A complex function that checks if cache exists or not,
         * if cache exists, simply copies to target. otherwise downloads to cache, extracts and copies.
         *
         * @param target
         *
         * @return true iff download was performed
         */
        public boolean copyFromCache( File target ){
            boolean performedDownload = false;
            if ( ! isExtracted()  ) {
                performedDownload = true;
                download();
                extract();
            }
            copy(target);
            return performedDownload;
        }

        /**
         * This function ignores the cache altogether.
         * it downloads a new copy of the resource every single time.
         *
         * eventually it will delete the temp folder
         *
         *
         * use if resource is really small
         *
         * @param target
         */
        public void copyFresh(File target) {
            baseDir = new File(System.getProperty("java.io.tmpdir"), "cloudifyWidget" + System.currentTimeMillis() ).getAbsolutePath();
            download();
            extract();
            copy(target);
            deleteDir(new File(baseDir));
        }

        // deletes the dest dir
        public void delete(){
            logger.info("deleting [{}]", getDestDir());
            WidgetResourcesUtils.deleteDir( getDestDir() );
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public void setBaseDir(String baseDir) {
            this.baseDir = baseDir;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }
    }

}

package cloudify.widget.common;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 8/10/14
 * Time: 9:40 PM
 */
public class TestWidgetResourceUtils {

    private static Logger logger = LoggerFactory.getLogger(TestWidgetResourceUtils.class);

    @Test
    public void test() throws IOException {
        File temp = new File(System.getProperty("java.io.tmpdir"));
        WidgetResourcesUtils.ResourceManager manager = new WidgetResourcesUtils.ResourceManager();
        String baseDir = new File(temp , "resources_test").getAbsolutePath();
        manager.setBaseDir( baseDir );
        manager.setUrl("https://github.com/guy-mograbi-at-gigaspaces/gs-ui-infra/archive/master.zip");
        String resourceUuid = UUID.randomUUID().toString();
        manager.setUid(resourceUuid);


        manager.download();
        File extractDest = new File(baseDir, resourceUuid);
        Assert.assertTrue( "expected dest dir to exist", extractDest.exists());
        Assert.assertTrue( "expected archive zip to exist",new File(extractDest, "master.zip").exists());



        manager.extract();
        Assert.assertTrue("expected extracted folder to exist", new File(extractDest, "gs-ui-infra-master").exists());


        File tempDir = new File(temp, "resources_test/" + UUID.randomUUID().toString());
        manager.copy(tempDir);
        Assert.assertTrue("expected temp dir to exist", tempDir.exists());
        Assert.assertTrue("expected README file to exist", new File(new File(tempDir,"gs-ui-infra-master"), "README.md").exists());

        manager.delete();
        Assert.assertTrue("extractDest should not exist", !extractDest.exists());

        FileUtils.deleteDirectory(tempDir);

    }

    @Test
    public void testFreshCopy() throws IOException {

        File tempFolder = new File(System.getProperty("java.io.tmpdir"),"testFreshCopyDir");
        File tempTarget = new File(tempFolder, "testFreshCopy" );
        FileUtils.deleteDirectory( tempFolder );

        WidgetResourcesUtils.ResourceManager manager = new WidgetResourcesUtils.ResourceManager();


        manager.setUrl("https://github.com/guy-mograbi-at-gigaspaces/gs-ui-infra/archive/master.zip");
        String resourceUuid = UUID.randomUUID().toString();
        manager.setUid(resourceUuid);

        manager.copyFresh(tempTarget );
        String absolutePath = new File(tempTarget, "master.zip").getAbsolutePath();
        Assert.assertTrue("expecting [" + absolutePath + "] to exist", new File(absolutePath).exists());
        Assert.assertTrue( new File( tempTarget, "gs-ui-infra-master").exists() );
    }


    @Test
    public void testCopyFromCache() throws IOException {

        File tempFolder = new File(System.getProperty("java.io.tmpdir"),"testFreshCopyDir");
        File tempTarget = new File(tempFolder, "testFreshCopy" );
        FileUtils.deleteDirectory( tempFolder );

        WidgetResourcesUtils.ResourceManager manager = new WidgetResourcesUtils.ResourceManager();


        manager.setUrl("https://github.com/guy-mograbi-at-gigaspaces/gs-ui-infra/archive/master.zip");
        String resourceUuid = UUID.randomUUID().toString();
        manager.setUid(resourceUuid);

        manager.copyFromCache(tempTarget);
        Assert.assertFalse("expecting download will not happen again", manager.copyFromCache(tempTarget));
        String absolutePath = new File(tempTarget, "master.zip").getAbsolutePath();
        Assert.assertTrue( "expecting [" + absolutePath + "] to exist" , new File(absolutePath).exists() );
        Assert.assertTrue( new File( tempTarget, "gs-ui-infra-master").exists() );
    }


    @Test
    public void testLastModified() throws IOException {
        File tempFolder = new File(System.getProperty("java.io.tmpdir"),"testFreshCopyDir");
        File tempTarget = new File(tempFolder, "testFreshCopy" );
        FileUtils.deleteDirectory( tempFolder );

        WidgetResourcesUtils.ResourceManager manager = new WidgetResourcesUtils.ResourceManager();


        manager.setUrl("https://github.com/guy-mograbi-at-gigaspaces/gs-ui-infra/archive/master.zip");
        String resourceUuid = UUID.randomUUID().toString();
        manager.setUid(resourceUuid);

        manager.copyFromCache(tempTarget );

        logger.info("last modified [{}] , [{}] ", manager.lastModified(), new Date(manager.lastModified()));

        // the assertion is that no error is thrown.
    }
}

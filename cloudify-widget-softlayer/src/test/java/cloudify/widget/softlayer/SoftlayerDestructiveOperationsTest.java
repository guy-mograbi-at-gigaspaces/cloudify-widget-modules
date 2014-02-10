package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;


/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/4/14
 * Time: 1:09 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:softlayer-context.xml"})
public class SoftlayerDestructiveOperationsTest {

    private static Logger logger = LoggerFactory.getLogger(SoftlayerDestructiveOperationsTest.class);
    private ComputeService computeService;
    private ComputeServiceContext context;

    @Autowired
    private SoftlayerCloudCredentials softlayerCloudCredentials;

    @Before
    public void bootstrap() {
        logger.info("before test setup...");
        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials, true);
        logger.info("created context [{}]", context);
        computeService = context.getComputeService();
        logger.info("created compute service [{}]", computeService);
        logger.info("test setup finished");
    }

    @Test
    public void testCreateMachine() {
        logger.info("starting test - create softlayer machine");

        final Collection<CloudServerCreated> cloudServerCreatedCollection = TestUtils.createCloudServer(computeService, "testsoft");
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        int i = 0;
        for( CloudServerCreated cloudServerCreated : cloudServerCreatedCollection ){
            logger.info( "machine created, [{}] - [{}]", i++, ( ( SoftlayerCloudServerCreated )cloudServerCreated ).getNewNode() );
        }

        logger.info("completed test - create softlayer machine");
    }

    @After
    public void teardown() {

    }
}
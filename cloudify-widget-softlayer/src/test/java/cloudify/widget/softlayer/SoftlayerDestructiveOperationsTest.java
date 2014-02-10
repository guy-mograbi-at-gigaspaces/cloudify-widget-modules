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
        logger.info("before setup...");
        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials.getUser(), softlayerCloudCredentials.getApiKey(), true);
        computeService = context.getComputeService();
        logger.info("setup finished: \n\tcontext is [{}] \n\tcompute service is [{}]", context, computeService);
    }

    @Test
    public void testCreateMachine() {

        logger.info("Start test create softlayer machine");
        SoftlayerMachineOptions machineOptions = new SoftlayerMachineOptions( "testsoft" );
        machineOptions.hardwareId( "1640,2238,13899" ).locationId( "37473" );
        logger.info("machine options created");
        SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        logger.info("softlayerCloudServerApi created");
        Collection<CloudServerCreated> cloudServerCreatedCollection = softlayerCloudServerApi.create( machineOptions );
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        int i = 0;
        for( CloudServerCreated cloudServerCreated : cloudServerCreatedCollection ){
            logger.info( "machine created, [{}] ", i++, ( ( SoftlayerCloudServerCreated )cloudServerCreated ).getNewNode() );
        }

        logger.info("Start test create softlayer machine, completed");
    }

    @After
    public void teardown() {

    }
}
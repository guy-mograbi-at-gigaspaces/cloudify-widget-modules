package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.assertNotNull;

/**
 * User: eliranm
 * Date: 2/9/14
 * Time: 7:55 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:softlayer-context.xml"})
public class SoftlayerNonDestructiveOperationsTest {

    private static Logger logger = LoggerFactory.getLogger(SoftlayerNonDestructiveOperationsTest.class);
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

        // TODO use this after implementing deleteMachine()
//        createNewMachine();

    }

    private void createNewMachine() {
        logger.info("creating new machine");
        SoftlayerMachineOptions machineOptions = new SoftlayerMachineOptions( "testsoft" )
                .hardwareId("1640,2238,13899")
                .locationId("37473");
        Collection<CloudServerCreated> cloudServerCreatedCollection = new SoftlayerCloudServerApi(computeService, null).create(machineOptions);
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        int i = 0;
        for( CloudServerCreated cloudServerCreated : cloudServerCreatedCollection ){
            logger.info( "machine created, [{}] ", i++, ( (SoftlayerCloudServerCreated)cloudServerCreated ).getNewNode() );
        }
    }

    @Test
    public void testContext() {
        logger.info("testing context [{}]", context);
        assertNotNull("context is null!", context);
    }

    @Test
    public void testComputeService() {
        logger.info("testing compute service [{}]", computeService);
        assertNotNull("compute service is null!", computeService);
    }

    @Ignore
    public void getMachine() {
        // TODO test softlayer cloud server get status
    }

    @Test
    public void testGetAllMachinesWithTag() {

        SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        Collection<CloudServer> machinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag("testsoft-4");
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }
    }

    @After
    public void teardown() {

        // TODO teardown machines created during bootstrap
    }

}
package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.common.CloudExecResponseImpl;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
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
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void runScriptOnNodeTest(){

        final String echoString = "hello world";

        Set<? extends ComputeMetadata> computeMetadatas = computeService.listNodes();
        for( ComputeMetadata computeMetadata : computeMetadatas ){
            NodeMetadata nodeMetadata = (NodeMetadata)computeMetadata;
            Set<String> publicAddresses = nodeMetadata.getPublicAddresses();
            if( !publicAddresses.isEmpty() ){
                SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
                String publicAddress = publicAddresses.iterator().next();
                CloudExecResponseImpl cloudExecResponse =
                        (CloudExecResponseImpl)softlayerCloudServerApi.runScriptOnMachine("echo " + echoString, publicAddress, null);
                logger.info("run Script on machine, completed, response [{}]" , cloudExecResponse );
                assertTrue( "Script must have [" + echoString + "]" , cloudExecResponse.getOutput().contains( echoString ) );
                break;
            }
        }
    }

    @After
    public void teardown() {

        // TODO teardown machines created during bootstrap
    }

}
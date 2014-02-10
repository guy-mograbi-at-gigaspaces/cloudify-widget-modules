package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.common.CloudExecResponseImpl;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

import static org.junit.Assert.*;

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
    private String tagMask = "test-machine-";
    private String otherName = "other-test-machine";
    private SortedSet<String> machineCommonNames = new TreeSet<String>(Arrays.asList(
            tagMask + "1" ,tagMask + "2"
    ));
    private SortedSet<String> machineNames;

    @Autowired
    private SoftlayerCloudCredentials softlayerCloudCredentials;
    private Collection<Collection<? extends CloudServerCreated>> machines;

    public SoftlayerNonDestructiveOperationsTest() {
        machineNames = new TreeSet<String>(machineCommonNames);
        machineNames.add(otherName);
    }

    // TODO create machines BeforeClass

    @Before
    public void bootstrap() {
        logger.info("starting test setup...");

        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials, true);
        logger.info("created context [{}]", context);
        computeService = context.getComputeService();
        logger.info("created compute service [{}]", computeService);
        // TODO uncomment for prod
        machines = createMachines();
        logger.info("test setup finished");
    }

    private Collection<Collection<? extends CloudServerCreated>> createMachines() {
        final Collection<Collection<? extends CloudServerCreated>> cloudServerCreatedsCollection = new ArrayList<Collection<? extends CloudServerCreated>>();
        for (String name : machineNames) {
            cloudServerCreatedsCollection.add(TestUtils.createCloudServer(computeService, name));
        }
        return cloudServerCreatedsCollection;
    }

    // TODO test softlayer cloud server get status

    @Test
    public void testGetAllMachinesWithTag() {


        SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        Collection<CloudServer> machinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag(tagMask);
        assertNotNull(machinesWithTag);
        logger.info("machines returned, size is [{}]", machinesWithTag.size());

        // assert that we got the desired number of machines
        assertEquals("expecting machines size to be 2", 2, machinesWithTag.size());

        for (CloudServer server : machinesWithTag) {
            final String name = server.getName();
            // assert that we got the desired machines
            assertTrue(String.format("expecting machine [%s] to be contained in machine names collection", name),
                    machineNames.contains(name));
            assertNotSame(String.format("expecting machine name [%s] not to be one that was not requested [%s]", name, otherName), otherName, name);
        }
    }

    @Test
    public void testGetMachineById() {

        SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        Collection<CloudServer> cloudServers = softlayerCloudServerApi.getAllMachinesWithTag(tagMask);
        for (CloudServer cloudServer : cloudServers) {
            logger.info("cloud server found with id [{}]", cloudServer.getId());
            CloudServer cs = softlayerCloudServerApi.get(cloudServer.getId());
            assertNotNull("expecting machine not to be null", cs);
        }
    }

    @Test
    public void runScriptOnNodeTest(){

        final String echoString = "hello world";

        Set<? extends ComputeMetadata> computeMetadatas = computeService.listNodes();
        for( ComputeMetadata computeMetadata : computeMetadatas ){
            NodeMetadata nodeMetadata = (NodeMetadata)computeMetadata;
            logger.info( "Machine " + nodeMetadata.getHostname() + " user name:" + nodeMetadata.getCredentials().getUser() );
            //prevent using Windows machines , comparing user name since OS is not initialized
            if( !nodeMetadata.getCredentials().getUser().contains( "Administrator" ) ){
                logger.info( "Proceeding machine..." );
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
    }

    @After
    public void teardown() {
        logger.info("tearing down...");
        // TODO teardown machines created during bootstrap
    }

}
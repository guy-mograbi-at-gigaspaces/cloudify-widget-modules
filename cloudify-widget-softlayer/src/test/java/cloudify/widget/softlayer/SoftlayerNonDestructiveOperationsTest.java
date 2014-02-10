package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.junit.*;
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

    public SoftlayerNonDestructiveOperationsTest() {
        machineNames = new TreeSet<String>(machineCommonNames);
        machineNames.add(otherName);
    }

    // TODO create machines BeforeClass

    @Before
    public void bootstrapClass() {
        logger.info("starting test setup...");

        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials, true);
        logger.info("created context [{}]", context);
        computeService = context.getComputeService();
        logger.info("created compute service [{}]", computeService);
        // TODO uncomment for prod
//        createMachines();
        logger.info("test setup finished");
    }

    private void createMachines() {
        for (String name : machineNames) {
            TestUtils.createCloudServer(computeService, name);
        }
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

    @After
    public void teardown() {
        logger.info("tearing down...");
        // TODO teardown machines created during bootstrap
    }

}
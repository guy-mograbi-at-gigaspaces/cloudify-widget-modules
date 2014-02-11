package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.CloudServerStatus;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.junit.Assert.*;


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
    private SoftlayerCloudServerApi softlayerCloudServerApi;

    @Before
    public void bootstrap() {
        logger.info("starting test setup...");
        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials, true);
        logger.info("created context [{}]", context);
        computeService = context.getComputeService();
        logger.info("created compute service [{}]", computeService);
        softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        logger.info("created cloud server api [{}]", softlayerCloudServerApi);
        logger.info("test setup finished");
    }

    @Test
    public void testCreateMachine() {
        logger.info("starting test - create softlayer machine");

        final Collection<? extends CloudServerCreated> cloudServerCreatedsCollection = TestUtils.createCloudServer("testsoft", softlayerCloudServerApi);
        assertNotNull("cloud server created collection should not be null", cloudServerCreatedsCollection);
        logger.info("machine(s) created, count=" + cloudServerCreatedsCollection.size());
        assertFalse("cloud server created collection should not be empty", cloudServerCreatedsCollection.isEmpty());
        int i = 0;
        for (CloudServerCreated cloudServerCreated : cloudServerCreatedsCollection) {
            final NodeMetadata newNode = ((SoftlayerCloudServerCreated) cloudServerCreated).getNewNode();
            logger.info("machine created, [{}] - [{}]", i++, newNode);
            assertNotNull("node metadata should not be null", newNode);
        }

        logger.info("completed test - create softlayer machine");
    }

    @Test
    public void testDeleteMachine() {

        String deleteCandidateName = "testsoft-delete";
//        String deleteCandidateName = "testsoft-5d4";

        // TODO uncomment in prod
        logger.info("creating machine for deletion, with name [{}]", deleteCandidateName);
        final Collection<? extends CloudServerCreated> cloudServerCreatedsCollection = TestUtils.createCloudServer(deleteCandidateName, softlayerCloudServerApi);
        assertNotNull("cloud server created objects cannot be null", cloudServerCreatedsCollection);
        assertFalse("cloud server created objects cannot be empty", cloudServerCreatedsCollection.isEmpty());
        final String id = cloudServerCreatedsCollection.iterator().next().getId();

        // TODO comment in prod
/*
        logger.info("getting machine id, fetching by name [{}]...", deleteCandidateName);
        final Collection<CloudServer> allMachinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag(deleteCandidateName);
        assertFalse("machines returned should not be empty", allMachinesWithTag.isEmpty());
        final String id = allMachinesWithTag.iterator().next().getId();
*/

        final CloudServer cloudServer = softlayerCloudServerApi.get(id);
        assertNotNull("cloud server can't be null", cloudServer);

        logger.info("waiting for status [{}] before deletion attempt", CloudServerStatus.RUNNING);
        TestUtils.waitForStatus(cloudServer, CloudServerStatus.RUNNING, 60 * 2);
//        logger.info("cloud server status is [{}]", cloudServer.getStatus());


        logger.info("deleting machine with id [{}]...", id);
        softlayerCloudServerApi.delete(id);

        logger.info("waiting for status [{}] before deletion attempt", CloudServerStatus.UNRECOGNIZED);
        TestUtils.waitForStatus(cloudServer, CloudServerStatus.UNRECOGNIZED, 60 * 2);

        logger.info("deletion seems successful, testing delete attempt on non-existing node...");
        RuntimeException expectedException = null;
        try {
            softlayerCloudServerApi.delete(id);
        } catch (RuntimeException e) {
            logger.info("exception thrown:\n [{}]", e);
            expectedException = e;
        } finally {
            assertNotNull("exception should have been thrown on delete attempt failure", expectedException);
            boolean assignableFrom = SoftlayerCloudServerApiOperationFailureException.class.isAssignableFrom(expectedException.getClass());
            if (!assignableFrom) {
                logger.info("exception thrown is not expected. stack trace is:\n[{}]", expectedException.getStackTrace());
            }
            assertTrue(String.format("[%s] should be assignable from exception type thrown on delete attempt failure [%s]", SoftlayerCloudServerApiOperationFailureException.class, expectedException.getClass()),
                    assignableFrom);
        }
    }

    @After
    public void teardown() {
        logger.info("tearing down...");
        // TODO remove all machines
    }
}
package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.api.clouds.MachineOptions;
import cloudify.widget.common.CloudExecResponseImpl;
import cloudify.widget.common.CollectionUtils;
import junit.framework.Assert;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
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

    @Autowired
    private CloudServerApi cloudServerApi;

    private String tagMask = "test-machine-";

    @Autowired
    private IConnectDetails connectDetails;

    @Autowired
    private SoftlayerMachineOptions machineOptions;


    @Test
    public void testSoftlayerDriver() {


        logger.info("Start test create softlayer machine");

        logger.info("softlayerCloudServerApi created");
        Collection<? extends CloudServerCreated> cloudServerCreatedCollection = cloudServerApi.create( machineOptions );
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        Assert.assertEquals( "should create number of machines specified", machineOptions.machinesCount(), CollectionUtils.size(cloudServerCreatedCollection) );


        logger.info("Start test create softlayer machine, completed");
        cloudServerApi.connect( connectDetails );
        Collection<CloudServer> machinesWithTag = cloudServerApi.getAllMachinesWithTag("testsoft-4");
        Assert.assertEquals( "should list machines that were created", machineOptions.machinesCount(), CollectionUtils.size(machinesWithTag));
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }

        /** get machine by id **/
        Collection<CloudServer> cloudServers = cloudServerApi.getAllMachinesWithTag(tagMask);
        for (CloudServer cloudServer : cloudServers) {
            logger.info("cloud server found with id [{}]", cloudServer.getId());
            CloudServer cs = cloudServerApi.get(cloudServer.getId());
            assertNotNull("expecting server not to be null", cs);
        }

        /** run script on machine **/ 
        final String echoString = "hello world";
        Collection<CloudServer> machines = cloudServerApi.getAllMachinesWithTag("testsoft-4");

        for (CloudServer machine : machines) {
            String publicIp = machine.getServerIp().publicIp;
            CloudExecResponse cloudExecResponse = cloudServerApi.runScriptOnMachine("echo " + echoString, publicIp, null);
            logger.info("run Script on machine, completed, response [{}]" , cloudExecResponse );
            assertTrue( "Script must have [" + echoString + "]" , cloudExecResponse.getOutput().contains( echoString ) );
        }
    }


    public void setCloudServer(CloudServerApi cloudServer) {
        this.cloudServerApi = cloudServer;
    }
}
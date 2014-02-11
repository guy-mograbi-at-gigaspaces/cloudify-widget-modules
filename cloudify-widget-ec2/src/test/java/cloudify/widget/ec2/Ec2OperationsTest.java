package cloudify.widget.ec2;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CloudExecResponseImpl;
import cloudify.widget.common.CollectionUtils;
import cloudify.widget.common.WaitTimeout;
import junit.framework.Assert;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.ec2.domain.InstanceType;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:ec2-context.xml"})
public class Ec2OperationsTest {

    private static Logger logger = LoggerFactory.getLogger(Ec2OperationsTest.class);
    private final String[] TAGS = { "ec2TestTag1", "ec2TestTag2" };

//    @Autowired
//    private Ec2ConnectDetails ec2ConnectDetails;

    @Autowired
    private CloudServerApi cloudServerApi;

    @Autowired
    private IConnectDetails connectDetails;

    @Autowired
    private Ec2MachineOptions machineOptions;

    @Autowired
    public WaitTimeout waitMachineIsRunningTimeout;

    @Autowired
    public WaitTimeout waitMachineIsStoppedTimeout;

    @Test
    public void testSoftlayerDriver() {

        logger.info("Start test, before connect");

        cloudServerApi.connect( connectDetails );

        logger.info("Start test create ec2 machine");

        Collection<? extends CloudServerCreated> cloudServerCreatedCollection = cloudServerApi.create( machineOptions );
        logger.info("ec2CloudServerApi created");
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        Assert.assertEquals("should create number of machines specified", machineOptions.machinesCount(), CollectionUtils.size(cloudServerCreatedCollection));

        logger.info("Start test create ec2 machine, completed");

        Collection<CloudServer> machinesWithTag = cloudServerApi.getAllMachinesWithTag("testsoft-4");
        Assert.assertEquals( "should list machines that were created", machineOptions.machinesCount(), CollectionUtils.size(machinesWithTag));
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }

        /** get machine by id **/
        Collection<CloudServer> cloudServers = cloudServerApi.getAllMachinesWithTag("testtag1");
        for (CloudServer cloudServer : cloudServers) {
            logger.info("cloud server found with id [{}]", cloudServer.getId());
            CloudServer cs = cloudServerApi.get(cloudServer.getId());
            assertNotNull("expecting server not to be null", cs);
        }
    }
                                                               /*
    @Test
    public void createNewMachine() {

        Ec2CloudServerApi softlayerCloudServerApi = new Ec2CloudServerApi(computeService, null);
        Ec2MachineOptions machineOptions = new Ec2MachineOptions( "evgenyec2test", 1 ).
        tags( Arrays.asList( TAGS ) ).hardwareId(InstanceType.M1_SMALL).osFamily(OsFamily.CENTOS);
        Collection<? extends CloudServerCreated> cloudServers = softlayerCloudServerApi.create(machineOptions);
        logger.info("machines returned, size is [{}]", cloudServers.size());
        for (CloudServerCreated cloudServerCreated : cloudServers) {
            logger.info("EC2 cloud server id [{}]", cloudServerCreated.getId());
        }
    }                                                        */
                   /*

    @Test
    public void testGetAllMachinesWithTag() {

        Ec2CloudServerApi softlayerCloudServerApi = new Ec2CloudServerApi(computeService, null);
        Collection<CloudServer> machinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag(TAGS[0]);
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }
    }
                 */
    /*
    @Ignore
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
                    Ec2CloudServerApi ec2CloudServerApi = new Ec2CloudServerApi(computeService, null);
                    String publicAddress = publicAddresses.iterator().next();
                    CloudExecResponseImpl cloudExecResponse =
                        (CloudExecResponseImpl)ec2CloudServerApi.runScriptOnMachine("echo " + echoString, publicAddress, null);
                    logger.info("run Script on machine, completed, response [{}]" , cloudExecResponse );
                    assertTrue( "Script must have [" + echoString + "]" , cloudExecResponse.getOutput().contains( echoString ) );
                    break;
                }
            }
        }
    }    */
}
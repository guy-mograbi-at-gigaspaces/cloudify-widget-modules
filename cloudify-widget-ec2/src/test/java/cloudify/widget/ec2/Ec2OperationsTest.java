package cloudify.widget.ec2;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.MachineOptions;
import cloudify.widget.common.CloudExecResponseImpl;
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
    private ComputeService computeService;
    private ComputeServiceContext context;

    @Autowired
    private Ec2CloudCredentials ec2CloudCredentials;

    @Before
    public void bootstrap() {
        logger.info("before setup...");
        context = Ec2CloudUtils.computeServiceContext( ec2CloudCredentials );
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

    @Test
    public void createNewMachine() {

        Ec2CloudServerApi softlayerCloudServerApi = new Ec2CloudServerApi(computeService, null);
        Ec2MachineOptions machineOptions = new Ec2MachineOptions( "myEc2Test", 1 ).
        tags( Arrays.asList( "ec2TestTag1", "ec2TestTag2" ) ).hardwareId(InstanceType.M1_SMALL).osFamily(OsFamily.CENTOS);
        Collection<? extends CloudServerCreated> cloudServers = softlayerCloudServerApi.create(machineOptions);
        logger.info("machines returned, size is [{}]", cloudServers.size());
        for (CloudServerCreated cloudServerCreated : cloudServers) {
            logger.info("EC2 cloud server id [{}]", cloudServerCreated.getId());
        }
    }


    @Test
    public void testGetAllMachinesWithTag() {

        Ec2CloudServerApi softlayerCloudServerApi = new Ec2CloudServerApi(computeService, null);
        Collection<CloudServer> machinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag(null);
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }
    }

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
    }

    @After
    public void teardown() {

        // TODO teardown machines created during bootstrap
    }

}
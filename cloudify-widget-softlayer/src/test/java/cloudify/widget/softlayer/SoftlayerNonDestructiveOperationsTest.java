package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.IConnectDetails;
import cloudify.widget.common.CollectionUtils;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

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

    @Autowired
    public WaitTimeout waitMachineIsRunningTimeout;

    @Autowired
    public WaitTimeout waitMachineIsStoppedTimeout;



    @Test
    public void testSoftlayerDriver() {


        logger.info("Start test create softlayer machine");

        logger.info("softlayerCloudServerApi created");
        Collection<? extends CloudServerCreated> cloudServerCreatedCollection = cloudServerApi.create( machineOptions );
        assertNotNull("cloud server created collection should not be null", cloudServerCreatedCollection);
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


        /**
         * teardown
         */

         logger.info("deleting all machines");

        for (CloudServer machine : machines) {
            logger.info("waiting for machine to run");
            MachineIsRunningCondition runCondition = new MachineIsRunningCondition();
            runCondition.setMachine(machine);

            waitMachineIsRunningTimeout.setCondition(runCondition);
            waitMachineIsRunningTimeout.waitFor();

            logger.info("deleting machine with id [{}]...", machine.getId());
            cloudServerApi.delete(machine.getId());

            logger.info("waiting for machine to stop");
            MachineIsStoppedCondition stopCondition = new MachineIsStoppedCondition();
            stopCondition.setMachine(machine);


            waitMachineIsStoppedTimeout.setCondition( stopCondition );
            waitMachineIsStoppedTimeout.waitFor();

            Exception expectedException= null;
            try {
                cloudServerApi.delete(machine.getId());
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
    }

    public void setCloudServer(CloudServerApi cloudServer) {
        this.cloudServerApi = cloudServer;
    }


    public void setWaitMachineIsRunningTimeout( WaitTimeout waitTimeout ){
        this.waitMachineIsRunningTimeout = waitTimeout;
    }

    public static class MachineIsRunningCondition implements WaitTimeout.Condition {
        public CloudServer machine;

        @Override
        public boolean apply() {
            return machine.isRunning();
        }

        public void setMachine(CloudServer machine) {
            this.machine = machine;
        }

        @Override
        public String toString() {
            return "MachineIsRunningCondition{" +
                    "machine=" + machine +
                    '}';
        }
    }

    public static class MachineIsStoppedCondition implements WaitTimeout.Condition{
        public CloudServer machine;

        @Override
        public boolean apply() {
            return machine.isStopped();
        }

        public void setMachine(CloudServer machine) {
            this.machine = machine;
        }

        @Override
        public String toString() {
            return "MachineIsStoppedCondition{" +
                    "machine=" + machine +
                    '}';
        }
    }
}
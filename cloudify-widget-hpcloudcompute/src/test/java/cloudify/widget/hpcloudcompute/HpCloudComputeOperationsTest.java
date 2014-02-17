package cloudify.widget.hpcloudcompute;

import cloudify.widget.api.clouds.*;
import cloudify.widget.common.CollectionUtils;
import cloudify.widget.common.MachineIsNotRunningCondition;
import cloudify.widget.common.MachineIsRunningCondition;
import cloudify.widget.common.WaitTimeout;
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
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:hpcloudcompute-context.xml"})
public class HpCloudComputeOperationsTest {

    private static Logger logger = LoggerFactory.getLogger(HpCloudComputeOperationsTest.class);
    private final String[] TAGS = { "hpCloudTestTag1", "hpCloudTestTag2" };

    private final String echoString = "hello world";

    @Autowired
    private CloudServerApi cloudServerApi;

    @Autowired
    private IConnectDetails connectDetails;

    @Autowired
    private HpCloudComputeMachineOptions machineOptions;

    @Autowired
    public WaitTimeout waitMachineIsRunningTimeout;

    @Autowired
    public WaitTimeout waitMachineIsNotRunning;

    @Test
    public void testHpCloudComputeDriver() {

        logger.info("Start test, before connect");

        cloudServerApi.connect( connectDetails );

        logger.info("Start test create hp cloud machine");

        Collection<? extends CloudServerCreated> cloudServerCreatedCollection = cloudServerApi.create( machineOptions );
        logger.info("hpCloudCloudServerApi created");
        logger.info( "machine(s) created, count=" + cloudServerCreatedCollection.size() );
        Assert.assertEquals("should create number of machines specified", machineOptions.machinesCount(), CollectionUtils.size(cloudServerCreatedCollection));

        logger.info("Start test create HP cloud machine, completed");

        Collection<CloudServer> machinesWithTag = cloudServerApi.getAllMachinesWithTag("hpCloudTestTag1");
        Assert.assertEquals( "should list machines that were created", machineOptions.machinesCount(), CollectionUtils.size(machinesWithTag));
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server name [{}]", cloudServer.getName());
        }

        /** get machine by id **/
        machinesWithTag = cloudServerApi.getAllMachinesWithTag("hpCloudTestTag2");
        Assert.assertEquals( "should list machines that were created", machineOptions.machinesCount(), CollectionUtils.size(machinesWithTag));
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server found with id [{}]", cloudServer.getId());
            CloudServer cs = cloudServerApi.get(cloudServer.getId());
            assertNotNull("expecting server not to be null", cs);
        }

        logger.info("Running script");

        /** run script on machine **/
        for (CloudServer machine : machinesWithTag) {
            String publicIp = machine.getServerIp().publicIp;
            CloudExecResponse cloudExecResponse = cloudServerApi.runScriptOnMachine("echo " + echoString, publicIp, null);
            logger.info("run Script on machine, completed, response [{}]" , cloudExecResponse );
            assertTrue( "Script must have [" + echoString + "]" , cloudExecResponse.getOutput().contains( echoString ) );
        }

        logger.info("rebuild machines...");
        for (CloudServer machine : machinesWithTag) {
            logger.info("rebuild machine, id [{}] ",machine.getId());
            cloudServerApi.rebuild(machine.getId());
        }

        logger.info("deleting all machines");

        for( CloudServer machine : machinesWithTag ) {
            logger.info("waiting for machine to run");
            MachineIsRunningCondition runCondition = new MachineIsRunningCondition();
            runCondition.setMachine(machine);

            waitMachineIsRunningTimeout.setCondition(runCondition);
            waitMachineIsRunningTimeout.waitFor();

            logger.info("deleting machine with id [{}]...", machine.getId());
            cloudServerApi.delete(machine.getId());

            logger.info("waiting for machine to stop");
            MachineIsNotRunningCondition notRunningCondition = new MachineIsNotRunningCondition();
            notRunningCondition.setMachine(machine);

            waitMachineIsNotRunning.setCondition( notRunningCondition );
            waitMachineIsNotRunning.waitFor();

            //in the case of HP cloud any exception is not thrown in the case of passed wrong id to destroyNode method
        }

    }
}
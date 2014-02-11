package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.CloudServerStatus;
import org.jclouds.compute.domain.OsFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * User: eliranm
 * Date: 2/10/14
 * Time: 5:16 PM
 */
public class TestUtils {

    private static Logger logger = LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
    }

    public static Collection<? extends CloudServerCreated> createCloudServer(String namePrefix, SoftlayerCloudServerApi softlayerCloudServerApi) {
        logger.info("creating new machine with name prefix [{}]", namePrefix);
        SoftlayerMachineOptions machineOptions = new SoftlayerMachineOptions(namePrefix)
                .hardwareId("1640,2238,13899")
                .locationId("37473")
                .osFamily(OsFamily.CENTOS);
        return softlayerCloudServerApi.create(machineOptions);
    }

    public static void waitForStatus(CloudServer cloudServer, CloudServerStatus cloudServerStatus, int timeoutInSeconds) {
        int secondsElapsed = 0;
        CloudServerStatus status = null;
        while (secondsElapsed < timeoutInSeconds && !cloudServerStatus.equals(status)) {
            try {
                Thread.sleep(1000);
                status = cloudServer.getStatus();
                logger.info("> waiting for status [{}], status is [{}]", cloudServerStatus, status);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                secondsElapsed += 1;
            }
        }
    }
}

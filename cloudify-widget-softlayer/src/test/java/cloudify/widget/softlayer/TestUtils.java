package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.compute.ComputeService;
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

    public static Collection<? extends CloudServerCreated> createCloudServer(ComputeService computeService, String name) {
        logger.info("creating new machine with name [{}]", name);
        SoftlayerMachineOptions machineOptions = new SoftlayerMachineOptions(name)
                .hardwareId("1640,2238,13899")
                .locationId("37473")
                .osFamily(OsFamily.CENTOS);
        return new SoftlayerCloudServerApi(computeService, null).create(machineOptions);
    }

}

package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import com.google.common.collect.Iterables;
import org.hamcrest.CoreMatchers;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.softlayer.SoftLayerApi;
import org.jclouds.softlayer.SoftLayerClient;
import org.jclouds.softlayer.features.VirtualGuestClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/4/14
 * Time: 1:09 PM
 */
public class TestSoftlayer {

    private static Logger logger = LoggerFactory.getLogger(TestSoftlayer.class);
    private ComputeService computeService;
    private ComputeServiceContext context;


    @Before
    public void setup() {
        // TODO to external config!
//        computeService = ContextBuilder.newBuilder("stub").buildView(ComputeServiceContext.class).getComputeService();
//        context = SoftlayerCloudUtils.computeServiceContext("stub", "", "", true);
        context = SoftlayerCloudUtils.computeServiceContext("softlayer", "USER", "API_KEY", true);
        computeService = context.getComputeService();
    }

    @Ignore
    public void testImageId() {
        Image first = Iterables.get(computeService.listImages(), 0);
        assert ImagePredicates.idEquals(first.getId()).apply(first);
        Image second = Iterables.get(computeService.listImages(), 1);
        assert !ImagePredicates.idEquals(first.getId()).apply(second);
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
    public void testGetAllMachinesWithTag() {

//        SoftLayerClient softlayerClient = ContextBuilder.newBuilder("softlayer").buildApi(SoftLayerClient.class);

        SoftlayerCloudServerApi softlayerCloudServerApi = new SoftlayerCloudServerApi(computeService, null);
        Collection<CloudServer> machinesWithTag = softlayerCloudServerApi.getAllMachinesWithTag("");
//        assertThat("this string", is("this string"));
        logger.info("machines returned, size is [{}]", machinesWithTag.size());
        for (CloudServer cloudServer : machinesWithTag) {
            logger.info("cloud server provider ip is [{}]", cloudServer.getServerIp().privateIp);
        }
    }

}

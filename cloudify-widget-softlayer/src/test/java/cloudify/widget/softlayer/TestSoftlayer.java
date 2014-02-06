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
import org.junit.*;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/4/14
 * Time: 1:09 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:softlayer-context.xml"})
public class TestSoftlayer {

    private static Logger logger = LoggerFactory.getLogger(TestSoftlayer.class);
    private ComputeService computeService;
    private ComputeServiceContext context;

    @Autowired
    private SoftlayerCloudCredentials softlayerCloudCredentials;

    @Before
    public void setup() {
        logger.info("before setup...");
        context = SoftlayerCloudUtils.computeServiceContext(softlayerCloudCredentials.getUser(), softlayerCloudCredentials.getApiKey(), true);
        computeService = context.getComputeService();
        logger.info("setup finished: \n\tcontext is [{}] \n\tcompute service is [{}]", context, computeService);
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

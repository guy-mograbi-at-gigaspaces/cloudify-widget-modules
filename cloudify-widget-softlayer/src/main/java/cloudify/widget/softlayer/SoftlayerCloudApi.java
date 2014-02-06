package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudApi;
import cloudify.widget.api.clouds.CloudServerApi;
import org.jclouds.compute.ComputeService;
import org.jclouds.softlayer.SoftLayerApi;

/**
 * User: eliranm
 * Date: 2/4/14
 * Time: 4:10 PM
 */
public class SoftlayerCloudApi implements CloudApi {

    private final ComputeService computeService;
    private final SoftLayerApi softLayerApi;

    public SoftlayerCloudApi(ComputeService computeService, SoftLayerApi SoftLayerApi) {
        this.computeService = computeService;
        this.softLayerApi = SoftLayerApi;
    }

    @Override
    public CloudServerApi getServerApiForZone(String zone) {
        // TODO by zone?
        return new SoftlayerCloudServerApi(computeService, softLayerApi);
    }

}

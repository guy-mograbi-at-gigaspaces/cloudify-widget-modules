package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.ec2.Ec2CloudServerApi;
import cloudify.widget.hpcloudcompute.HpCloudComputeCloudServerApi;
import cloudify.widget.pool.manager.settings.dto.ProviderSettings;
import cloudify.widget.softlayer.SoftlayerCloudServerApi;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 3:10 PM
 */
public class CloudServerApiFactory {

    private CloudServerApiFactory() {
    }

    public static CloudServerApi create(ProviderSettings.ProviderName providerName) {
        if (ProviderSettings.ProviderName.hp == providerName) {
            return new HpCloudComputeCloudServerApi();
        }
        if (ProviderSettings.ProviderName.softlayer == providerName) {
            return new SoftlayerCloudServerApi();
        }
        if (ProviderSettings.ProviderName.ec2 == providerName) {
            return new Ec2CloudServerApi();
        }
        return null;
    }
}

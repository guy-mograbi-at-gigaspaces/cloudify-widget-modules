package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.ec2.Ec2CloudServerApi;
import cloudify.widget.hpcloudcompute.HpCloudComputeCloudServerApi;
import cloudify.widget.pool.manager.dto.ProviderSettings;
import cloudify.widget.softlayer.SoftlayerCloudServerApi;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 3:10 PM
 */
public class CloudServerApiFactory {

    private CloudServerApiFactory() {
    }

    /**
     * Creates a cloud server API according to provider name.
     *
     * @param providerName The desired provider name.
     * @return A concrete API using the desired provider, or {@code null} if no such provider found.
     */
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

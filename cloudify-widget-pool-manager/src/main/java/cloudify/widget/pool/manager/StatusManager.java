package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.dto.ProviderSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 8:05 PM
 */
public class StatusManager {

    private static Logger logger = LoggerFactory.getLogger(StatusManager.class);

    public void updateStatus(PoolStatus poolStatus) {
    }

    public void setStatus(PoolStatus poolStatus) {
    }

    public PoolStatus getStatus(PoolSettings poolSettings) {

        ProviderSettings provider = poolSettings.getProvider();
        if (provider == null) {
            throw new RuntimeException("provider not found in pool settings");
        }

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(provider.getName());

        logger.debug("connecting to cloud server api [{}] using connect details [{}]", cloudServerApi, provider.getConnectDetails());
        cloudServerApi.connect(provider.getConnectDetails());

        String mask = provider.getMachineOptions().getMask();
        logger.debug("returning status with mask [{}]", mask);

        return new PoolStatus()
                .minNodes(poolSettings.getMinNodes())
                .maxNodes(poolSettings.getMaxNodes())
                .currentSize(cloudServerApi.getAllMachinesWithTag(mask).size());
    }

}

package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.settings.dto.PoolSettings;
import cloudify.widget.pool.manager.settings.dto.ProviderSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:49 PM
 */
public class PoolManager {

    private static Logger logger = LoggerFactory.getLogger(PoolManager.class);
    
    public PoolStatus getStatus(PoolSettings poolSettings) {

        ProviderSettings provider = poolSettings.provider;
        if (provider == null) {
            throw new RuntimeException("provider not found in pool settings");
        }

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(provider.name);

        if (cloudServerApi == null) {
            throw new RuntimeException("cloud server api could not be instantiated");
        }
        
        logger.info("connecting to cloud server api [{}] using connect details [{}]", cloudServerApi, provider.connectDetails);

        cloudServerApi.connect(provider.connectDetails);

        String mask = provider.machineOptions.getMask();

        return new PoolStatus()
                .setMinNodes(poolSettings.minNodes)
                .setMaxNodes(poolSettings.maxNodes)
                .setCurrentSize(cloudServerApi.getAllMachinesWithTag(mask).size());
    }

}

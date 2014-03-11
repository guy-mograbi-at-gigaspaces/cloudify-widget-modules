package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.dto.ProviderSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 8:05 PM
 */
public class StatusManager {

    private static Logger logger = LoggerFactory.getLogger(StatusManager.class);

    @Autowired
    private NodesDataAccessManager nodesDataAccessManager;

    public void updateStatus(PoolStatus poolStatus) {
    }

    public void setStatus(PoolStatus poolStatus) {
    }

    public PoolStatus getStatus(PoolSettings poolSettings) {

        ProviderSettings provider = poolSettings.getProvider();
        if (provider == null) {
            logger.error("provider not found in pool settings");
        }

        return new PoolStatus()
                .minNodes(poolSettings.getMinNodes())
                .maxNodes(poolSettings.getMaxNodes())
                .currentSize(nodesDataAccessManager.listNodes(poolSettings).size());
    }

}

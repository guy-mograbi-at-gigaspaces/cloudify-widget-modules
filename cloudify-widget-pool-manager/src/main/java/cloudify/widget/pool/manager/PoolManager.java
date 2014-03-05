package cloudify.widget.pool.manager;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:49 PM
 */
public class PoolManager {

    private static Logger logger = LoggerFactory.getLogger(PoolManager.class);

    @Autowired
    private PoolDao poolDao;

    @Autowired
    private ManagerSettingsHandler managerSettingsHandler;

    public PoolStatus getStatus(PoolSettings poolSettings) {

        ProviderSettings provider = poolSettings.getProvider();
        if (provider == null) {
            throw new RuntimeException("provider not found in pool settings");
        }

        CloudServerApi cloudServerApi = getCloudServerApi(provider);

        logger.debug("connecting to cloud server api [{}] using connect details [{}]", cloudServerApi, provider.getConnectDetails());
        cloudServerApi.connect(provider.getConnectDetails());

        String mask = provider.getMachineOptions().getMask();
        logger.debug("returning status with mask [{}]", mask);

        return new PoolStatus()
                .minNodes(poolSettings.getMinNodes())
                .maxNodes(poolSettings.getMaxNodes())
                .currentSize(cloudServerApi.getAllMachinesWithTag(mask).size());
    }

    public ManagerSettings getSettings() {
        return managerSettingsHandler.read();
    }

    public List<NodeModel> listNodes(PoolSettings poolSettings) {
        return poolDao.readAll(poolSettings.getId());
    }

    public NodeModel getNode(long nodeId) {
        return poolDao.read(nodeId);
    }

    public boolean addNode(NodeModel nodeModel) {
        return poolDao.create(nodeModel);
    }

    public int removeNode(long nodeId) {
        return poolDao.delete(nodeId);
    }

    public int updateNode(NodeModel nodeModel) {
        return poolDao.update(nodeModel);
    }


    private CloudServerApi getCloudServerApi(ProviderSettings provider) {
        CloudServerApi cloudServerApi = CloudServerApiFactory.create(provider.getName());
        if (cloudServerApi == null) {
            throw new RuntimeException("cloud server api could not be instantiated");
        }
        return cloudServerApi;
    }


    public void setPoolDao(PoolDao poolDao) {
        this.poolDao = poolDao;
    }

    public void setManagerSettingsHandler(ManagerSettingsHandler managerSettingsHandler) {
        this.managerSettingsHandler = managerSettingsHandler;
    }

}

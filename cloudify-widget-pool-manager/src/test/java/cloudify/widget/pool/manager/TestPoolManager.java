package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.settings.ManagerSettingsHandler;
import cloudify.widget.pool.manager.settings.dto.ManagerSettings;
import cloudify.widget.pool.manager.settings.dto.PoolSettings;
import cloudify.widget.pool.manager.settings.dto.ProviderSettings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 4:42 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:app-context.xml", "classpath:pool-manager-context.xml"})
@ActiveProfiles({"softlayer","ibmprod"})
public class TestPoolManager {

    @Autowired
    private PoolDao poolDao;

    @Autowired
    private PoolManager poolManager;

    @Autowired
    private ManagerSettingsHandler managerSettingsHandler;

    private static Logger logger = LoggerFactory.getLogger(TestPoolManager.class);

    @Test
    public void testJdbConnection(){
        List<MachineModel> machines = poolDao.getMachines();
        logger.info("I have [{}] machines", machines);
    }

    @Test
    public void testManagerApi() {

        ManagerSettings managerSettings = managerSettingsHandler.read();
        PoolSettings poolSettings = null;
        for (PoolSettings ps : managerSettings.pools) {
            // get softlayer pool settings
            if (ps.provider.name == ProviderSettings.ProviderName.softlayer) {
                poolSettings = ps;
                break;
            }
        }

        Assert.notNull(poolSettings, "pool settings should not be null");

        PoolStatus poolStatus = poolManager.getStatus(poolSettings);

        Assert.notNull(poolStatus, "pool status should not be null");

        logger.info("poolStatus.minNodes [{}]", poolStatus.minNodes);
        logger.info("poolStatus.maxNodes [{}]", poolStatus.maxNodes);
        logger.info("poolStatus.currentSize [{}]", poolStatus.currentSize);

        Assert.isTrue(poolStatus.currentSize >= poolStatus.minNodes && poolStatus.currentSize <= poolStatus.maxNodes);

    }

    public void setManagerSettingsHandler(ManagerSettingsHandler managerSettingsHandler) {
        this.managerSettingsHandler = managerSettingsHandler;
    }

    public PoolDao getPoolDao() {
        return poolDao;
    }

    public void setPoolDao(PoolDao poolDao) {
        this.poolDao = poolDao;
    }
}

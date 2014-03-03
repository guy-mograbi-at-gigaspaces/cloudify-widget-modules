package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.dto.ManagerSettings;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.ProviderSettings;
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

    private static Logger logger = LoggerFactory.getLogger(TestPoolManager.class);

    @Autowired
    private PoolManager poolManager;

    @Test
    public void testManagerApi() {

        ManagerSettings managerSettings = poolManager.getSettings();

        // get softlayer pool settings
        PoolSettings softlayerPoolSettings = null;
        for (PoolSettings ps : managerSettings.pools) {
            if (ps.provider.name == ProviderSettings.ProviderName.softlayer) {
                softlayerPoolSettings = ps;
                break;
            }
        }

        Assert.notNull(softlayerPoolSettings, "pool settings should not be null");

        PoolStatus poolStatus = poolManager.getStatus(softlayerPoolSettings);

        Assert.notNull(poolStatus, "pool status should not be null");

        Assert.isTrue(poolStatus.currentSize >= poolStatus.minNodes && poolStatus.currentSize <= poolStatus.maxNodes,
                String.format("current size [%s] must be greater than or equal to min size [%s] and less than or equal to max size [%s]",
                        poolStatus.currentSize, poolStatus.minNodes, poolStatus.maxNodes));

    }

}

package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.*;
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

/*
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
*/

    @Before
    public void init() {

/*
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader("/init.sql"));
        } catch (FileNotFoundException e) {
            logger.error("failed to find sql file", e);
        }

        LineNumberReader fileReader = new LineNumberReader(in);
        String statement = null;
        try {
            statement = JdbcTestUtils.readScript(fileReader);
        } catch (IOException e) {
            logger.error("failed to read script from file");
        }

        jdbcTemplate.update(statement);
*/

    }

    @After
    public void destroy() {

    }

    @Test
    public void test() {

    }


    @Test
    public void testPoolStatus() {

        ManagerSettings managerSettings = poolManager.getSettings();

        PoolSettings softlayerPoolSettings = getSoftlayerPoolSettings(managerSettings);

        Assert.notNull(softlayerPoolSettings, "pool settings should not be null");

        logger.info("getting pool status...");
        PoolStatus poolStatus = poolManager.getStatus(softlayerPoolSettings);

        Assert.notNull(poolStatus, "pool status should not be null");

        Assert.isTrue(poolStatus.currentSize >= poolStatus.minNodes && poolStatus.currentSize <= poolStatus.maxNodes,
                String.format("current size [%s] must be greater than or equal to min size [%s] and less than or equal to max size [%s]",
                        poolStatus.currentSize, poolStatus.minNodes, poolStatus.maxNodes));

    }



    @Test
    public void testPoolCrud() {

        ManagerSettings managerSettings = poolManager.getSettings();

        PoolSettings softlayerPoolSettings = getSoftlayerPoolSettings(managerSettings);

        logger.info("creating node...");
        NodeModel nodeModel = new NodeModel()
                .setPoolId(softlayerPoolSettings.id)
                .setNodeStatus(NodeModel.NodeStatus.CREATING)
                .setMachineId("test_machine_id")
                .setCloudifyVersion("1.1.0");

        logger.info("adding node [{}] to pool manager", nodeModel);
        boolean added = poolManager.addNode(nodeModel);
        logger.info("added [{}]", added);

        Assert.isTrue(added, "add node should return true if any rows were created in the table");

        logger.info("listing nodes...");
        List<NodeModel> softlayerNodeModels = poolManager.listNodes(softlayerPoolSettings);
        logger.info("got nodes [{}]", softlayerNodeModels);

        Assert.notNull(softlayerNodeModels, String.format("failed to read nodes of pool with id [%s]", softlayerPoolSettings.id));
        Assert.notEmpty(softlayerNodeModels, "nodes in softlayer pool should not be empty");

        logger.info("updating node status from [{}] to [{}]", nodeModel.nodeStatus, NodeModel.NodeStatus.BOOTSTRAPPING);
        nodeModel.setNodeStatus(NodeModel.NodeStatus.BOOTSTRAPPING);
        int affectedByUpdate = poolManager.updateNode(nodeModel);
        logger.info("affectedByUpdate [{}]", affectedByUpdate);

        Assert.isTrue(affectedByUpdate == 1, "exactly one node should be updated");

        logger.info("reading node after update... using id [{}]", nodeModel.id);
        NodeModel node = poolManager.getNode(nodeModel.id);
        logger.info("got node [{}]", node);

        Assert.notNull(node, "failed to read a single node");

        Assert.isTrue(node.nodeStatus == NodeModel.NodeStatus.BOOTSTRAPPING,
                String.format("node status should be updated to [%s], but is [%s]", NodeModel.NodeStatus.BOOTSTRAPPING.name(), node.nodeStatus));

        // TODO test removeNode()

    }

    private PoolSettings getSoftlayerPoolSettings(ManagerSettings managerSettings) {
        logger.info("looking for softlayer pool settings in manager settings [{}]", managerSettings);
        PoolSettings softlayerPoolSettings = null;
        for (PoolSettings ps : managerSettings.pools) {
            if (ps.provider.name == ProviderSettings.ProviderName.softlayer) {
                logger.info("found softlayer pool settings [{}]", ps);
                softlayerPoolSettings = ps;
                break;
            }
        }
        return softlayerPoolSettings;
    }

}














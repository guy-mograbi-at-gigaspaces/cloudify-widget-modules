package cloudify.widget.pool.manager;

import cloudify.widget.common.FileUtils;
import cloudify.widget.pool.manager.dto.*;
import cloudify.widget.pool.manager.tasks.*;
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

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 4:42 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pool-manager-test-context.xml"})
@ActiveProfiles({"softlayer"})
public class TestPoolManager {

    private static Logger logger = LoggerFactory.getLogger(TestPoolManager.class);

    private static final String SCHEMA = "pool_manager_test";

    @Autowired
    private PoolManagerApi poolManagerApi;

    @Autowired
    private NodesDataAccessManager nodesDataAccessManager;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private Integer nExecutions;

    @Autowired
    private String bootstrapScriptResourcePath;


    @Before
    public void init() {

        // initializing test schema
        jdbcTemplate.update("create schema " + SCHEMA);
        jdbcTemplate.update("use " + SCHEMA);
        buildDatabase();

    }

    @After
    public void destroy() {
        jdbcTemplate.update("drop schema " + SCHEMA);
    }


    @Test
    public void test() {
        Assert.notNull(jdbcTemplate);
    }


    @Test(timeout = 10 * 60 * 1000)
    public void testTaskExecutor() {
        logger.info("testing manager task executor");


        PoolSettings softlayerPoolSettings = poolManagerApi.getSettings().getPools().getByProviderName(ProviderSettings.ProviderName.softlayer);
        logger.info("got pool settings with id [{}]", softlayerPoolSettings.getId());

        // delete - (should fail)

        logger.info("executing delete machine task that's bound to fail...");
        taskExecutor.execute(DeleteMachineTask.class, null, softlayerPoolSettings);

        List<TaskErrorModel> taskErrorModels = poolManagerApi.listTaskErrors(softlayerPoolSettings);
        TaskErrorModel taskErrorModel = null;
        for (TaskErrorModel model : taskErrorModels) {
            taskErrorModel = model;
            break;
        }
        Assert.notNull(taskErrorModel, "task error should not be null");
        logger.info("task message is [{}]", taskErrorModel.message);
        Assert.isTrue(taskErrorModel.taskName == TaskName.DELETE_MACHINE, "task name should be " + TaskName.DELETE_MACHINE);

        // create

        logger.info("executing create machine task [{}] times...", nExecutions);
        for (int i = 0; i < nExecutions; i++) {
            taskExecutor.execute(CreateMachineTask.class, null, softlayerPoolSettings);
        }

        logger.info("checking table for added node...");
        NodeModel nodeModel = null;
        List<NodeModel> softlayerNodeModels = poolManagerApi.listNodes(softlayerPoolSettings);
        for (NodeModel softlayerNodeModel : softlayerNodeModels) {
            nodeModel = softlayerNodeModel;
            logger.info("found node [{}]", nodeModel);
            break;
        }

        Assert.isTrue(nodeModel != null, "node cannot be null after machine is created");

        logger.info("node status is [{}]", nodeModel.nodeStatus);
        Assert.isTrue(nodeModel.nodeStatus == NodeModel.NodeStatus.CREATED,
                String.format("node status should be [%s]", NodeModel.NodeStatus.CREATED));

        final NodeModel finalNodeModel = nodeModel;

        // bootstrap

        taskExecutor.execute(BootstrapMachineTask.class, new BootstrapMachineTaskConfig() {
            @Override
            public String getBootstrapScriptResourcePath() {
                return bootstrapScriptResourcePath;
            }

            @Override
            public NodeModel getNodeModel() {
                return finalNodeModel;
            }
        }, softlayerPoolSettings);

        // delete

        taskExecutor.execute(DeleteMachineTask.class, new DeleteMachineTaskConfig() {
            @Override
            public NodeModel getNodeModel() {
                return finalNodeModel;
            }
        }, softlayerPoolSettings);

        Assert.isNull(poolManagerApi.getNode(nodeModel.id), "node should not be found after deletion");
    }

    @Test
    public void testPoolStatus() {

        ManagerSettings managerSettings = poolManagerApi.getSettings();

        logger.info("looking for softlayer pool settings in manager settings [{}]", managerSettings);
        PoolSettings softlayerPoolSettings = managerSettings.getPools().getByProviderName(ProviderSettings.ProviderName.softlayer);

        Assert.notNull(softlayerPoolSettings, "pool settings should not be null");

        logger.info("getting pool status...");
        PoolStatus poolStatus = poolManagerApi.getStatus(softlayerPoolSettings);

        Assert.notNull(poolStatus, "pool status should not be null");

        Assert.isTrue(poolStatus.currentSize >= poolStatus.minNodes && poolStatus.currentSize <= poolStatus.maxNodes,
                String.format("current size [%s] must be greater than or equal to min size [%s] and less than or equal to max size [%s]",
                        poolStatus.currentSize, poolStatus.minNodes, poolStatus.maxNodes));

    }


    @Test
    public void testPoolCrud() {

        ManagerSettings managerSettings = poolManagerApi.getSettings();

        logger.info("looking for softlayer pool settings in manager settings [{}]", managerSettings);
        PoolSettings softlayerPoolSettings = managerSettings.getPools().getByProviderName(ProviderSettings.ProviderName.softlayer);

        int nodesSize = 3;
        List<NodeModel> nodes = new ArrayList<NodeModel>(nodesSize);

        logger.info("creating [{}] nodes for pool with provider [{}]...", nodesSize, softlayerPoolSettings.getProvider().getName());
        for (int i = 0; i < nodesSize; i++) {
            nodes.add(new NodeModel()
                    .setPoolId(softlayerPoolSettings.getId())
                    .setNodeStatus(NodeModel.NodeStatus.CREATED)
                    .setMachineId("test_machine_id")
                    .setCloudifyVersion("1.1.0"));
        }

        NodeModel firstNode = nodes.get(0);

        // create

        for (NodeModel node : nodes) {
            logger.info("adding node [{}] to pool...", node);
            boolean added = nodesDataAccessManager.addNode(node);
            logger.info("added [{}]", added);
            Assert.isTrue(added, "add node should return true if it was successfully added in the pool");
            logger.info("node id [{}], initial id [{}]", node.id, NodeModel.INITIAL_ID);
            Assert.isTrue(node.id != NodeModel.INITIAL_ID, "node id should be updated after added to the pool.");
        }

        // read

        logger.info("reading node with id [{}]...", firstNode.id);
        NodeModel readNode = nodesDataAccessManager.getNode(firstNode.id);
        logger.info("returned node [{}]", readNode);
        Assert.notNull(readNode, String.format("failed to read node with id [%s]", firstNode.id));

        logger.info("reading none existing node...");
        readNode = nodesDataAccessManager.getNode(-2);
        logger.info("returned node [{}]", readNode);
        Assert.isNull(readNode,
                String.format("non existing node should be null when read from the pool, but instead returned [%s]", readNode));

        logger.info("listing nodes for pool with provider [{}]...", softlayerPoolSettings.getProvider().getName());
        List<NodeModel> softlayerNodeModels = nodesDataAccessManager.listNodes(softlayerPoolSettings);
        logger.info("returned nodes [{}]", softlayerNodeModels);

        Assert.notNull(softlayerNodeModels, String.format("failed to read nodes of pool with id [%s]", softlayerPoolSettings.getId()));
        Assert.notEmpty(softlayerNodeModels, "nodes in softlayer pool should not be empty");
        Assert.isTrue(softlayerNodeModels.size() == nodesSize,
                String.format("amount of nodes in softlayer pool should be [%s], but instead is [%s]", nodesSize, softlayerNodeModels.size()));

        // update

        logger.info("updating first node status from [{}] to [{}]", firstNode.nodeStatus, NodeModel.NodeStatus.BOOTSTRAPPED);
        firstNode.setNodeStatus(NodeModel.NodeStatus.BOOTSTRAPPED);
        int affectedByUpdate = nodesDataAccessManager.updateNode(firstNode);
        logger.info("affectedByUpdate [{}]", affectedByUpdate);

        Assert.isTrue(affectedByUpdate == 1,
                String.format("exactly ONE node should be updated, but instead the amount affected by the update is [%s]", affectedByUpdate));

        logger.info("reading first node after update, using id [{}]...", firstNode.id);
        NodeModel node = nodesDataAccessManager.getNode(firstNode.id);
        logger.info("returned node [{}]", node);

        Assert.notNull(node, "failed to read a single node");

        Assert.isTrue(node.nodeStatus == NodeModel.NodeStatus.BOOTSTRAPPED,
                String.format("node status should be updated to [%s], but is [%s]", NodeModel.NodeStatus.BOOTSTRAPPED.name(), node.nodeStatus));

        // delete

        logger.info("removing node with id [{}]...", node.id);
        nodesDataAccessManager.removeNode(node.id);

        logger.info("reading node after remove, using id [{}]...", node.id);
        node = nodesDataAccessManager.getNode(node.id);
        logger.info("returned node [{}]", node);

        Assert.isNull(node, String.format("node should be null after it is removed, but instead returned [%s]", node));
    }


    private void buildDatabase() {
        // going through all files under the 'sql' folder, and executing all of them.
        Iterator<File> sqlFileIterator = org.apache.commons.io.FileUtils.iterateFiles(
                FileUtils.getFileInClasspath("sql"), new String[]{"sql"}, false);
        while (sqlFileIterator.hasNext()) {
            File file = sqlFileIterator.next();
            List<String> statements = readSqlStatementsFromFile(file);
            for (String stmt : statements) {
                jdbcTemplate.update(stmt);
            }
        }
    }

    private String readSqlStatementFromFile(File file) {

        String script = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            LineNumberReader fileReader = new LineNumberReader(in);
            script = JdbcTestUtils.readScript(fileReader);
        } catch (IOException e) {
            logger.error("failed to read sql script from file", e);
        }
        return script;
    }

    private List<String> readSqlStatementsFromFile(File file) {
        String script = readSqlStatementFromFile(file);
        List<String> statements = new ArrayList<String>();
        JdbcTestUtils.splitSqlScript(script, ';', statements);
        return statements;
    }

}













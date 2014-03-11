package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.TaskErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class CreateMachineTask implements ITask {

    private static Logger logger = LoggerFactory.getLogger(CreateMachineTask.class);

    private PoolSettings poolSettings;

    private NodesDataAccessManager nodesDataAccessManager;

    private TaskErrorsDataAccessManager taskErrorsDataAccessManager;

    private StatusManager statusManager;

    private static final TaskName TASK_NAME = TaskName.CREATE_MACHINE;

    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
        this.nodesDataAccessManager = nodesDataAccessManager;
    }

    @Override
    public void setTaskErrorsDataAccessManager(TaskErrorsDataAccessManager taskErrorsDataAccessManager) {
        this.taskErrorsDataAccessManager = taskErrorsDataAccessManager;
    }

    @Override
    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    @Override
    public void setPoolSettings(PoolSettings poolSettings) {
        this.poolSettings = poolSettings;
    }

    @Override
    public void setTaskConfig(TaskConfig taskConfig) {
        // no need for task data for this task
    }

    @Override
    public Object call() throws Exception {
        logger.info("creating machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            logger.error("failed to obtain an API object using provider [{}]", providerSettings.getName());
            return null;
        }

        PoolStatus status = statusManager.getStatus(poolSettings);
        if (status.currentSize >= status.maxNodes) {
            String message = "failed to create machine, pool has reached its maximum capacity as defined in the pool settings";
            logger.error(message);
            taskErrorsDataAccessManager.addTaskError(new TaskErrorModel()
                    .setPoolId(poolSettings.getId())
                    .setTaskName(TASK_NAME)
                    .setMessage(message)
            );
            return null;
        }

        cloudServerApi.connect(providerSettings.getConnectDetails());

        Collection<? extends CloudServerCreated> cloudServerCreateds = cloudServerApi.create(providerSettings.getMachineOptions());
        for (CloudServerCreated created : cloudServerCreateds) {
            NodeModel nodeModel = new NodeModel()
                    .setMachineId(created.getId())
                    .setPoolId(poolSettings.getId())
                    .setNodeStatus(NodeModel.NodeStatus.CREATED);
            logger.debug("machine created, adding node to database [{}]", nodeModel);
            nodesDataAccessManager.addNode(nodeModel);
        }

        return cloudServerCreateds;
    }

}

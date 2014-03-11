package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.TaskErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.dto.ProviderSettings;
import cloudify.widget.pool.manager.dto.TaskErrorModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class DeleteMachineTask implements ITask<DeleteMachineTaskConfig, Void> {

    private static Logger logger = LoggerFactory.getLogger(DeleteMachineTask.class);

    private NodesDataAccessManager nodesDataAccessManager;

    private StatusManager statusManager;

    private TaskErrorsDataAccessManager taskErrorsDataAccessManager;

    private PoolSettings poolSettings;

    private DeleteMachineTaskConfig taskConfig;

    private static final TaskName TASK_NAME = TaskName.DELETE_MACHINE;

    @Override
    public Void call() throws Exception {
        logger.info("deleting machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            logger.error("failed to obtain an API object using provider [{}]", providerSettings.getName());
            return null;
        }

        PoolStatus status = statusManager.getStatus(poolSettings);
        if (status.currentSize <= status.minNodes) {
            String message = "failed to remove machine: pool has reached its minimum capacity as defined in the pool settings";
            logger.error(message);
            taskErrorsDataAccessManager.addTaskError(new TaskErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getId())
                    .setMessage(message)
            );
            return null;
        }

        cloudServerApi.connect(providerSettings.getConnectDetails());
        cloudServerApi.delete(taskConfig.getNodeModel().machineId);

        logger.info("machine deleted, removing node model in the database [{}]");
        nodesDataAccessManager.removeNode(taskConfig.getNodeModel().id);

        return null;
    }


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
    public void setTaskConfig(DeleteMachineTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}

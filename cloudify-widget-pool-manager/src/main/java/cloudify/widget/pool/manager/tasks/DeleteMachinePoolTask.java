package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.TaskErrorsManager;
import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class DeleteMachinePoolTask implements PoolTask {

    private static Logger logger = LoggerFactory.getLogger(DeleteMachinePoolTask.class);

    private PoolManager poolManager;

    private TaskErrorsManager taskErrorsManager;

    private PoolSettings poolSettings;

    private TaskData taskData;

    private static final TaskName TASK_NAME = TaskName.DELETE_MACHINE;


    @Override
    public void run() {
        logger.info("deleting machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            logger.error("failed to obtain an API object using provider [{}]", providerSettings.getName());
            return;
        }

        // TODO ponder: is this really a job for the task?
        PoolStatus status = poolManager.getStatus(poolSettings);
        if (status.currentSize <= status.minNodes) {
            String message = "failed to remove machine: pool has reached its minimum capacity as defined in the pool settings";
            logger.error(message);
            taskErrorsManager.addTaskError(new TaskErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getId())
                    .setMessage(message)
            );
            return;
        }

        logger.info("connecting to cloud server api with details [{}]", providerSettings.getConnectDetails());
        cloudServerApi.connect(providerSettings.getConnectDetails());
        logger.info("deleting machine with id [{}]", taskData.getNodeModel().machineId);
        cloudServerApi.delete(taskData.getNodeModel().machineId);

        logger.info("machine deleted, removing node model in the database [{}]");
        poolManager.removeNode(taskData.getNodeModel().id);

    }


    @Override
    public TaskName getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    @Override
    public void setTaskErrorsManager(TaskErrorsManager taskErrorsManager) {
        this.taskErrorsManager = taskErrorsManager;
    }

    @Override
    public void setPoolSettings(PoolSettings poolSettings) {
        this.poolSettings = poolSettings;
    }

    @Override
    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }
}

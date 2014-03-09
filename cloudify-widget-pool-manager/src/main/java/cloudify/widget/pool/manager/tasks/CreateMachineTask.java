package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.TaskErrorsManager;
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

    private PoolManager poolManager;

    private TaskErrorsManager taskErrorsManager;

    private static final TaskName TASK_NAME = TaskName.CREATE_MACHINE;

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
    public void setTaskConfig(TaskConfig taskConfig) {
        // no need for task data for this task
    }

    @Override
    public void run() {
        logger.info("creating machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            logger.error("failed to obtain an API object using provider [{}]", providerSettings.getName());
            return;
        }


        // TODO ponder: is this really a job for the task?
        PoolStatus status = poolManager.getStatus(poolSettings);
        if (status.currentSize >= status.maxNodes) {
            String message = "failed to create machine, pool has reached its maximum capacity as defined in the pool settings";
            logger.error(message);
            taskErrorsManager.addTaskError(new TaskErrorModel()
                    .setPoolId(poolSettings.getId())
                    .setTaskName(TASK_NAME)
                    .setMessage(message)
            );
            return;
        }


        logger.info("connecting to cloud server api with details [{}]", providerSettings.getConnectDetails());
        cloudServerApi.connect(providerSettings.getConnectDetails());
        logger.info("creating machine(s) with options [{}]", providerSettings.getMachineOptions());
        Collection<? extends CloudServerCreated> cloudServerCreateds = cloudServerApi.create(providerSettings.getMachineOptions());

        for (CloudServerCreated created : cloudServerCreateds) {
            NodeModel nodeModel = new NodeModel()
                    .setMachineId(created.getId())
                    .setPoolId(poolSettings.getId())
                    .setNodeStatus(NodeModel.NodeStatus.CREATED);
//                    .setCloudifyVersion();
            logger.info("machine created, adding node to database [{}]", nodeModel);
            poolManager.addNode(nodeModel);
        }
    }

}

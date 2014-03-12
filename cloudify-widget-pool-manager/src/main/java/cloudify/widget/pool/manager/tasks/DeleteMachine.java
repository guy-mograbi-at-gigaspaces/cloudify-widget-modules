package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.ErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class DeleteMachine implements Task<DeleteMachineConfig, Void> {

    private static Logger logger = LoggerFactory.getLogger(DeleteMachine.class);

    private NodesDataAccessManager nodesDataAccessManager;

    private StatusManager statusManager;

    private ErrorsDataAccessManager errorsDataAccessManager;

    private PoolSettings poolSettings;

    private DeleteMachineConfig taskConfig;

    private static final TaskName TASK_NAME = TaskName.DELETE_MACHINE;

    @Override
    public Void call() throws Exception {
        logger.info("deleting machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            String message = String.format("failed to obtain an API object using provider [%s]", providerSettings.getName());
            logger.error(message);
            throw new RuntimeException(message);
        }

        PoolStatus status = statusManager.getPoolStatus(poolSettings);
        if (status.getCurrentSize() <= poolSettings.getMinNodes()) {
            String message = "pool has reached its minimum capacity as defined in the pool settings";
            logger.error(message);
            errorsDataAccessManager.addError(new ErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getId())
                    .setMessage(message)
            );
            throw new RuntimeException(message);
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
    public void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager) {
        this.errorsDataAccessManager = errorsDataAccessManager;
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
    public void setTaskConfig(DeleteMachineConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}

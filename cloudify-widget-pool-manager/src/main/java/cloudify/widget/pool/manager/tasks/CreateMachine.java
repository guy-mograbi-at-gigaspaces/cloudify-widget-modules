package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.ErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class CreateMachine implements Task<TaskConfig, Collection<NodeModel>> {

    private static Logger logger = LoggerFactory.getLogger(CreateMachine.class);

    private PoolSettings poolSettings;

    private NodesDataAccessManager nodesDataAccessManager;

    private ErrorsDataAccessManager errorsDataAccessManager;

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
    public void setTaskConfig(TaskConfig taskConfig) {
        // no need for task data for this task
    }

    @Override
    public Collection<NodeModel> call() throws Exception {
        logger.info("creating machine with pool settings [{}]", poolSettings);

        ProviderSettings providerSettings = poolSettings.getProvider();

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(providerSettings.getName());
        if (cloudServerApi == null) {
            String message = String.format("failed to obtain cloud server API using provider [%s]", providerSettings.getName());
            logger.error(message);
            throw new RuntimeException(message);
        }



        logger.debug("connecting to provider [{}]", providerSettings.getName());
        cloudServerApi.connect(providerSettings.getConnectDetails());

        Collection<NodeModel> nodeModelsCreated = new ArrayList<NodeModel>();

        Collection<? extends CloudServerCreated> cloudServerCreateds = cloudServerApi.create(providerSettings.getMachineOptions());
        for (CloudServerCreated created : cloudServerCreateds) {
            NodeModel nodeModel = new NodeModel()
                    .setMachineId(created.getId())
                    .setPoolId(poolSettings.getId())
                    .setNodeStatus(NodeStatus.CREATED);
            logger.debug("machine created, adding node to database. node model is [{}]", nodeModel);
            nodesDataAccessManager.addNode(nodeModel);
            nodeModelsCreated.add(nodeModel);
        }

        // TODO ponder: do we really need to pass this back?
        return nodeModelsCreated;
    }

}

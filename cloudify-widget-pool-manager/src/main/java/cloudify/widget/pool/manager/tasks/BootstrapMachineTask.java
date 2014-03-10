package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.TaskErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.BootstrapProperties;
import cloudify.widget.pool.manager.dto.NodeModel;
import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.TaskErrorModel;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 6:00 PM
 */
public class BootstrapMachineTask implements ITask<BootstrapMachineTaskConfig> {

    private static Logger logger = LoggerFactory.getLogger(CreateMachineTask.class);

    private static final TaskName TASK_NAME = TaskName.BOOTSTRAP_MACHINE;

    private NodesDataAccessManager nodesDataAccessManager;

    private TaskErrorsDataAccessManager taskErrorsDataAccessManager;

    private PoolSettings poolSettings;

    private BootstrapMachineTaskConfig taskConfig;

    @Override
    public void run() {

        if (taskConfig.getNodeModel().nodeStatus == NodeModel.NodeStatus.BOOTSTRAPPED) {
            logger.info("node is already bootstrapped, aborting bootstrap task");
            return;
        }

        File scriptFile = null;
        try {
            scriptFile = ResourceUtils.getFile(taskConfig.getBootstrapScriptResourcePath());
        } catch (FileNotFoundException e) {
            logger.error("failed to get resource for bootstrap script", e);
        }

        logger.info("bootstrap script file is [{}]", scriptFile);

        String script = null;
        try {
            script = FileUtils.readFileToString(scriptFile);
        } catch (IOException e) {
            logger.error("failed to read bootstrap script file to string");
        }

        logger.info("script file read to string\n\n{}", script);

        BootstrapProperties bootstrapProperties = poolSettings.getBootstrapProperties();
        script.replaceAll("##publicip##", bootstrapProperties.getPublicIp());
        script.replaceAll("##privateip##", bootstrapProperties.getPrivateIp());
        script.replaceAll("##cloudifyUrl##", bootstrapProperties.getCloudifyUrl());
        script.replaceAll("##prebootstrapScript##", bootstrapProperties.getPreBootstrapScript());
        script.replaceAll("##recipeRelativePath##", bootstrapProperties.getRecipeRelativePath());
        script.replaceAll("##recipeUrl##", bootstrapProperties.getRecipeUrl());

        logger.info("script file updated with bootstrap properties\n\n{}", script);


        CloudServerApi cloudServerApi = CloudServerApiFactory.create(poolSettings.getProvider().getName());
        cloudServerApi.connect();

        String machineId = taskConfig.getNodeModel().machineId;

        CloudServer cloudServer = cloudServerApi.get(machineId);
        if (cloudServer == null) {
            String message = String.format("node with id [%s] was not found", machineId);
            logger.error(message);
            taskErrorsDataAccessManager.addTaskError(new TaskErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getId())
                    .setMessage(message));
            return;
        }

        cloudServerApi.runScriptOnMachine(script, cloudServer.getServerIp().publicIp); // TODO ponder why public ip ?

        NodeModel updatedNodeModel = nodesDataAccessManager.getNode(taskConfig.getNodeModel().id);
        logger.info("bootstrap was run on the machine, updating node status in the database [{}]", updatedNodeModel);
        updatedNodeModel.setNodeStatus(NodeModel.NodeStatus.BOOTSTRAPPED);
        nodesDataAccessManager.updateNode(updatedNodeModel);
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
    }

    @Override
    public void setPoolSettings(PoolSettings poolSettings) {
        this.poolSettings = poolSettings;
    }

    @Override
    public void setTaskConfig(BootstrapMachineTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}

package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.TaskErrorsManager;
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

    private PoolManager poolManager;

    private TaskErrorsManager taskErrorsManager;

    private PoolSettings poolSettings;

    private BootstrapMachineTaskConfig taskConfig;

    @Override
    public void run() {

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

        logger.info("script file read to string [{}]", script);

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(poolSettings.getProvider().getName());
        cloudServerApi.connect();

        String machineId = taskConfig.getNodeModel().machineId;

        CloudServer cloudServer = cloudServerApi.get(machineId);
        if (cloudServer == null) {
            String message = String.format("node with id [%s] was not found", machineId);
            logger.error(message);
            taskErrorsManager.addTaskError(new TaskErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getId())
                    .setMessage(message));
            return;
        }

        cloudServerApi.runScriptOnMachine(script, cloudServer.getServerIp().privateIp); // TODO private ?

        NodeModel updatedNodeModel = poolManager.getNode(taskConfig.getNodeModel().id);
        logger.info("bootstrap was run on the machine, updating node status in the database [{}]", updatedNodeModel);
        updatedNodeModel.setNodeStatus(NodeModel.NodeStatus.BOOTSTRAPPED);
        poolManager.updateNode(updatedNodeModel);
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
    public void setTaskConfig(BootstrapMachineTaskConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}

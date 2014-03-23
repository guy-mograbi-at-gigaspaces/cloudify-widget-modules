package cloudify.widget.pool.manager.tasks;

import cloudify.widget.api.clouds.CloudExecResponse;
import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerApi;
import cloudify.widget.pool.manager.CloudServerApiFactory;
import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.ErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 6:00 PM
 */
public class BootstrapMachine implements Task<BootstrapMachineConfig, Void> {

    private static Logger logger = LoggerFactory.getLogger(CreateMachine.class);

    private static final TaskName TASK_NAME = TaskName.BOOTSTRAP_MACHINE;

    private NodesDataAccessManager nodesDataAccessManager;

    private ErrorsDataAccessManager errorsDataAccessManager;

    private PoolSettings poolSettings;

    private BootstrapMachineConfig taskConfig;

    @Override
    public Void call() throws Exception {

        if (taskConfig.getNodeModel().nodeStatus == NodeStatus.BOOTSTRAPPED ||
                taskConfig.getNodeModel().nodeStatus == NodeStatus.BOOTSTRAPPING) {
            String message = String.format("node with id [%s] is bootstrapping or is already bootstrapped, aborting bootstrap task", taskConfig.getNodeModel().id);
            logger.info(message);
            throw new RuntimeException(message);
        }

        String script = getBootstrapScript();

        script = injectBootstrapProperties(script);

        CloudServerApi cloudServerApi = CloudServerApiFactory.create(poolSettings.getProvider().getName());
        cloudServerApi.connect(poolSettings.getProvider().getConnectDetails());

        CloudServer cloudServer = getCloudServer(cloudServerApi);

        runBootstrapScriptOnMachine(script, cloudServerApi, cloudServer);

        return null;
    }

    private String getBootstrapScript() {
        return readScriptFromFile(getScriptFile());
    }

    private File getScriptFile() {
        File scriptFile;
        try {
            scriptFile = ResourceUtils.getFile(taskConfig.getBootstrapScriptResourcePath());
            logger.debug("bootstrap script file is [{}]", scriptFile);
        } catch (FileNotFoundException e) {
            String message = "failed to get resource for bootstrap script";
            logger.error(message, e);
            errorsDataAccessManager.addError(new ErrorModel()
                    .setPoolId(poolSettings.getUuid())
                    .setTaskName(TASK_NAME)
                    .setMessage(message)
            );
            throw new RuntimeException(message);
        }
        return scriptFile;
    }

    private String readScriptFromFile(File scriptFile) {
        String script;
        try {
            script = FileUtils.readFileToString(scriptFile);
            logger.debug("script file read to string\n\n[{}]...", script.substring(0, 20));
        } catch (IOException e) {
            String message = "failed to read bootstrap script file to string";
            logger.error(message, e);
            errorsDataAccessManager.addError(new ErrorModel()
                    .setPoolId(poolSettings.getUuid())
                    .setTaskName(TASK_NAME)
                    .setMessage(message)
            );
            throw new RuntimeException(message);
        }
        return script;
    }

    private String injectBootstrapProperties(String script) {
        BootstrapProperties bootstrapProperties = poolSettings.getBootstrapProperties();
        return script
                .replaceAll("##publicip##", bootstrapProperties.getPublicIp())
                .replaceAll("##privateip##", bootstrapProperties.getPrivateIp())
                .replaceAll("##cloudifyUrl##", bootstrapProperties.getCloudifyUrl())
                .replaceAll("##prebootstrapScript##", bootstrapProperties.getPreBootstrapScript())
                .replaceAll("##recipeRelativePath##", bootstrapProperties.getRecipeRelativePath())
                .replaceAll("##recipeUrl##", bootstrapProperties.getRecipeUrl());
    }

    private CloudServer getCloudServer(CloudServerApi cloudServerApi) {
        String machineId = taskConfig.getNodeModel().machineId;
        CloudServer cloudServer = cloudServerApi.get(machineId);
        if (cloudServer == null) {
            String message = String.format("machine with id [%s] was not found", machineId);
            logger.error(message);
            errorsDataAccessManager.addError(new ErrorModel()
                    .setTaskName(TASK_NAME)
                    .setPoolId(poolSettings.getUuid())
                    .setMessage(message));
            throw new RuntimeException(message);
        }
        return cloudServer;
    }

    private void runBootstrapScriptOnMachine(String script, CloudServerApi cloudServerApi, CloudServer cloudServer) {
        updateNodeModelStatus(NodeStatus.BOOTSTRAPPING);
        CloudExecResponse cloudExecResponse = cloudServerApi.runScriptOnMachine(script, cloudServer.getServerIp().publicIp);
        int exitStatus = cloudExecResponse.getExitStatus();
        logger.debug("bootstrap was run on the machine, node id [{}]", taskConfig.getNodeModel().id);
        if (exitStatus == 0) {
            updateNodeModelStatus(NodeStatus.BOOTSTRAPPED);
        } else {
            updateNodeModelStatus(NodeStatus.CREATED);
            String message = "bootstrap execution failed";
            logger.error(message);
            HashMap<String, Object> infoMap = new HashMap<String, Object>();
            infoMap.put("exitStatus", exitStatus);
            infoMap.put("output", cloudExecResponse.getOutput());
            errorsDataAccessManager.addError(new ErrorModel()
                    .setPoolId(poolSettings.getUuid())
                    .setTaskName(TASK_NAME)
                    .setMessage(message)
                    .setInfoFromMap(infoMap)
            );
            throw new RuntimeException(message);
        }
    }

    private void updateNodeModelStatus(NodeStatus nodeStatus) {
        logger.debug("bootstrap was run on the machine, updating node status to [{}]", nodeStatus);
        NodeModel updatedNodeModel = nodesDataAccessManager.getNode(taskConfig.getNodeModel().id);
        updatedNodeModel.setNodeStatus(nodeStatus);
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
    public void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager) {
        this.errorsDataAccessManager = errorsDataAccessManager;
    }

    @Override
    public void setStatusManager(StatusManager statusManager) {
    }

    @Override
    public void setPoolSettings(PoolSettings poolSettings) {
        this.poolSettings = poolSettings;
    }

    @Override
    public void setTaskConfig(BootstrapMachineConfig taskConfig) {
        this.taskConfig = taskConfig;
    }
}

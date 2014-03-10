package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 7:50 PM
 */
public class PoolManagerApi {

    private static Logger logger = LoggerFactory.getLogger(PoolManagerApi.class);


    @Autowired
    private NodesDataAccessManager nodesDataAccessManager;

    @Autowired
    private TaskErrorsDataAccessManager taskErrorsDataAccessManager;

    @Autowired
    private SettingsDataAccessManager settingsDataAccessManager;

    @Autowired
    private StatusManager statusManager;

    public PoolStatus getStatus(PoolSettings poolSettings) {
        return statusManager.getStatus(poolSettings);
    }

    public ManagerSettings getSettings() {
        return settingsDataAccessManager.read();
    }

    // nodes api

    public List<NodeModel> listNodes(PoolSettings poolSettings) {
        return nodesDataAccessManager.listNodes(poolSettings);
    }

    public NodeModel getNode(long nodeId) {
        return nodesDataAccessManager.getNode(nodeId);
    }

    public boolean createNode(NodeModel nodeModel) {
        // TODO replace with executor logic
        return nodesDataAccessManager.addNode(nodeModel);
    }

    public int deleteNode(long nodeId) {
        // TODO replace with executor logic
        return nodesDataAccessManager.removeNode(nodeId);
    }

    // TODO externalize!
/*
    public void runScriptOnNode(NodeModel nodeModel, File script) {
        return executor.runScriptOnNode(nodeModel, script);
    }
*/

    // task errors api

    public List<TaskErrorModel> listTaskErrors(PoolSettings poolSettings) {
        return taskErrorsDataAccessManager.listTaskErrors(poolSettings);
    }

    public TaskErrorModel getTaskError(long errorId) {
        return taskErrorsDataAccessManager.getTaskError(errorId);
    }

    public int removeTaskError(long errorId) {
        return taskErrorsDataAccessManager.removeTaskError(errorId);
    }



    public void setSettingsDataAccessManager(SettingsDataAccessManager settingsDataAccessManager) {
        this.settingsDataAccessManager = settingsDataAccessManager;
    }

    public void setTaskErrorsDataAccessManager(TaskErrorsDataAccessManager taskErrorsDataAccessManager) {
        this.taskErrorsDataAccessManager = taskErrorsDataAccessManager;
    }

    public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
        this.nodesDataAccessManager = nodesDataAccessManager;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }
}

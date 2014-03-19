package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;
import cloudify.widget.pool.manager.tasks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 7:50 PM
 */
public class PoolManagerApiImpl implements PoolManagerApi {

    private static Logger logger = LoggerFactory.getLogger(PoolManagerApiImpl.class);

    @Autowired
    private NodesDataAccessManager nodesDataAccessManager;

    @Autowired
    private ErrorsDataAccessManager errorsDataAccessManager;

    @Autowired
    private TasksDataAccessManager tasksDataAccessManager;


    @Autowired
    private StatusManager statusManager;

    @Autowired
    private TaskExecutor taskExecutor;

    private String bootstrapScriptResourcePath;


    @Override
    public PoolStatus getStatus(PoolSettings poolSettings) {
        if (poolSettings == null) return null;
        return statusManager.getPoolStatus(poolSettings);
    }

    @Override
    public Collection<PoolStatus> listStatuses() {
        return statusManager.listPoolStatuses();
    }

    @Override
    public List<NodeModel> listNodes(PoolSettings poolSettings) {
        if (poolSettings == null) return null;
        return nodesDataAccessManager.listNodes(poolSettings);
    }

    @Override
    public NodeModel getNode(long nodeId) {
        return nodesDataAccessManager.getNode(nodeId);
    }

    @Override
    public NodeModel getNode(NodeModel nodeModel) {
        throw new UnsupportedOperationException("not supported yet!");
        // TODO implement - get node and mark as occupied?
    }

    @Override
    public NodeModel getAnyNode(PoolSettings poolSettings) {
        throw new UnsupportedOperationException("not supported yet!");

/*
        List<NodeModel> nodeModels = nodesDataAccessManager.listNodes(poolSettings);
        // TODO TBD run validation at this point
        for (NodeModel nodeModel : nodeModels) {
            if (nodeModel.nodeStatus != NodeStatus.OCCUPIED) {  // TODO discuss with guy: what would the tasks check (non occupied?)
                return nodeModel;
            }
        }
        return null;
*/
    }

    @Override
    public void createNode(PoolSettings poolSettings, TaskCallback<Collection<NodeModel>> taskCallback) {
        if (poolSettings == null) return;
        taskExecutor.execute(CreateMachine.class, null, poolSettings, taskCallback);
    }

    @Override
    public void deleteNode(PoolSettings poolSettings, long nodeId,  TaskCallback<Void> taskCallback) {
        final NodeModel node = _getNodeModel(nodeId);
        if (node == null) return;
        if (poolSettings == null) return;
        taskExecutor.execute(DeleteMachine.class, new DeleteMachineConfig() {
            @Override
            public NodeModel getNodeModel() {
                return node;
            }
        }, poolSettings, taskCallback);
    }

    @Override
    public void bootstrapNode(PoolSettings poolSettings, long nodeId, TaskCallback<NodeModel> taskCallback) {
        final NodeModel node = _getNodeModel(nodeId);
        taskExecutor.execute(BootstrapMachine.class, new BootstrapMachineConfig() {
            @Override
            public String getBootstrapScriptResourcePath() {
                return bootstrapScriptResourcePath;
            }
            @Override
            public NodeModel getNodeModel() {
                return node;
            }
        }, poolSettings, taskCallback);

        // TODO task should find a CREATED node for itself, and should return that node in the callback success
/*
        taskExecutor.execute(BootstrapMachine.class, new BootstrapMachineConfig() {
            @Override
            public String getBootstrapScriptResourcePath() {
                return bootstrapScriptResourcePath;
            }
//            @Override
//            public NodeModel getNodeModel() {
//                return node;
//            }
        }, poolSettings, taskCallback);
*/
    }

    @Override
    public List<ErrorModel> listTaskErrors(PoolSettings poolSettings) {
        if (poolSettings == null) return null;
        return errorsDataAccessManager.listErrors(poolSettings);
    }

    @Override
    public ErrorModel getTaskError(long errorId) {
        return errorsDataAccessManager.getError(errorId);
    }

    @Override
    public void removeTaskError(long errorId) {
        errorsDataAccessManager.removeError(errorId);
    }

    @Override
    public List<TaskModel> listRunningTasks(PoolSettings poolSettings) {
        return tasksDataAccessManager.listTasks(poolSettings);
    }

    private NodeModel _getNodeModel(long nodeId) {
        final NodeModel node = nodesDataAccessManager.getNode(nodeId);
        if (node == null) {
            logger.error("node with id [{}] not found", nodeId);
        }
        return node;
    }



    public void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager) {
        this.errorsDataAccessManager = errorsDataAccessManager;
    }

    public void setTasksDataAccessManager(TasksDataAccessManager tasksDataAccessManager) {
        this.tasksDataAccessManager = tasksDataAccessManager;
    }

    public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
        this.nodesDataAccessManager = nodesDataAccessManager;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }

    public void setTaskExecutor(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setBootstrapScriptResourcePath(String bootstrapScriptResourcePath) {
        this.bootstrapScriptResourcePath = bootstrapScriptResourcePath;
    }
}

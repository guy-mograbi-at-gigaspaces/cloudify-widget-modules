package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;
import cloudify.widget.pool.manager.tasks.TaskCallback;

import java.util.Collection;
import java.util.List;

/**
 * User: eliranm
 * Date: 3/10/14
 * Time: 5:39 PM
 */
public interface PoolManagerApi {

    PoolStatus getStatus(PoolSettings poolSettings);

    Collection<PoolStatus> listStatuses();

    ManagerSettings getSettings();

    List<NodeModel> listNodes(PoolSettings poolSettings);

    @Deprecated
    /**
     * @deprecated use {@link #getNode(cloudify.widget.pool.manager.dto.NodeModel)} instead.
     */
    NodeModel getNode(long nodeId); // TODO rename

    NodeModel getNode(NodeModel nodeModel); // TODO rename

    NodeModel getAnyNode(PoolSettings poolSettings); // TODO rename

    void createNode(PoolSettings poolSettings, TaskCallback<Collection<NodeModel>> taskCallback);

    @Deprecated
    /**
     * @deprecated use {@link #deleteNode(cloudify.widget.pool.manager.dto.NodeModel, cloudify.widget.pool.manager.tasks.TaskCallback)} instead.
     */
    void deleteNode(long nodeId, TaskCallback<Void> taskCallback);

    void deleteNode(NodeModel nodeModel, TaskCallback<Void> taskCallback);

    @Deprecated
    /**
     * @deprecated use {@link #bootstrapNode(cloudify.widget.pool.manager.dto.PoolSettings, cloudify.widget.pool.manager.tasks.TaskCallback)} instead.
     */
    void bootstrapNode(long nodeId, TaskCallback<Void> taskCallback);

    void bootstrapNode(PoolSettings poolSettings, TaskCallback<NodeModel> taskCallback);


    List<ErrorModel> listTaskErrors(PoolSettings poolSettings);

    ErrorModel getTaskError(long errorId);

    void removeTaskError(long errorId);
}

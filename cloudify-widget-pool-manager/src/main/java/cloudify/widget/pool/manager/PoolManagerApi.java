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
     * @deprecated use {@link #getNode(cloudify.widget.pool.manager.dto.PoolSettings)} instead.
     */
    NodeModel getNode(long nodeId);

    NodeModel getNode(PoolSettings poolSettings);

    void createNode(PoolSettings poolSettings, TaskCallback<Collection<NodeModel>> taskCallback);

    void deleteNode(long nodeId, TaskCallback<Void> taskCallback);

    // TODO
//    void deleteNode(NodeModel nodeModel, TaskCallback<Void> taskCallback);

    void bootstrapNode(long nodeId, TaskCallback<Void> taskCallback);

    // TODO
//    void bootstrapNode(NodeModel nodeModel, TaskCallback<Object> taskCallback);


    List<ErrorModel> listTaskErrors(PoolSettings poolSettings);

    ErrorModel getTaskError(long errorId);

    void removeTaskError(long errorId);
}

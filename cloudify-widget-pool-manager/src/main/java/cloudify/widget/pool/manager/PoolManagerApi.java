package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.*;

import java.util.Collection;
import java.util.List;

/**
 * User: eliranm
 * Date: 3/10/14
 * Time: 5:39 PM
 */
public interface PoolManagerApi {

    PoolStatus getStatus(String poolSettingsId);

    Collection<PoolStatus> listStatuses();

    ManagerSettings getSettings();

    List<NodeModel> listNodes(String poolSettingsId);

    NodeModel getNode(long nodeId);

    void createNode(String poolSettingsId);

    void deleteNode(long nodeId);

    void bootstrapNode(long nodeId);

    List<TaskErrorModel> listTaskErrors(String poolSettingsId);

    TaskErrorModel getTaskError(long errorId);

    void removeTaskError(long errorId);
}

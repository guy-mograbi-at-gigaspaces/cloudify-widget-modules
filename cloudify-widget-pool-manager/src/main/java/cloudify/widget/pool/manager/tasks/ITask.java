package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.TaskErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.PoolSettings;

/**
 * User: eliranm
 * Date: 3/6/14
 * Time: 12:39 PM
 */
public interface ITask<T extends TaskConfig> extends Runnable {

    TaskName getTaskName();

    void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager);

    void setTaskErrorsDataAccessManager(TaskErrorsDataAccessManager taskErrorsDataAccessManager);

    void setStatusManager(StatusManager statusManager);

    void setPoolSettings(PoolSettings poolSettings);

    void setTaskConfig(T taskConfig);
}

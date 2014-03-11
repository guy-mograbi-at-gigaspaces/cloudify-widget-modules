package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.TaskErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.PoolSettings;

import java.util.concurrent.Callable;

/**
 * @param <T> The task config implementation.
 * @param <R> The expected task result data.
 */
public interface Task<T extends TaskConfig, R> extends Callable<R> {

    TaskName getTaskName();

    void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager);

    void setTaskErrorsDataAccessManager(TaskErrorsDataAccessManager taskErrorsDataAccessManager);

    void setStatusManager(StatusManager statusManager);

    void setPoolSettings(PoolSettings poolSettings);

    void setTaskConfig(T taskConfig);
}

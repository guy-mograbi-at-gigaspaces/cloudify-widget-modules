package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.NodesDataAccessManager;
import cloudify.widget.pool.manager.StatusManager;
import cloudify.widget.pool.manager.ErrorsDataAccessManager;
import cloudify.widget.pool.manager.dto.PoolSettings;

import java.util.concurrent.Callable;

/**
 * @param <T> The task config implementation.
 * @param <R> The expected task result data.
 */
public interface Task<T extends TaskConfig, R> extends Callable<R> {

    TaskName getTaskName();

    void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager);

    void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager);

    void setStatusManager(StatusManager statusManager);

    void setPoolSettings(PoolSettings poolSettings);

    void setTaskConfig(T taskConfig);
}

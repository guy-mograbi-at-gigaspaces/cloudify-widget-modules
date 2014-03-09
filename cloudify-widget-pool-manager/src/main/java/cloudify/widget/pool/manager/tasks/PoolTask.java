package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.TaskErrorsManager;
import cloudify.widget.pool.manager.PoolManager;
import cloudify.widget.pool.manager.dto.PoolSettings;

/**
 * User: eliranm
 * Date: 3/6/14
 * Time: 12:39 PM
 */
public interface PoolTask extends Runnable {

    TaskName getTaskName();

    void setPoolManager(PoolManager poolManager);

    void setTaskErrorsManager(TaskErrorsManager taskErrorsManager);

    void setPoolSettings(PoolSettings poolSettings);

    void setTaskData(TaskData taskData);
}

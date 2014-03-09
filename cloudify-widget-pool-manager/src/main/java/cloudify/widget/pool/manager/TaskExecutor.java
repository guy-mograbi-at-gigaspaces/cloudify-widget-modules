package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.tasks.PoolTask;
import cloudify.widget.pool.manager.tasks.TaskData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:35 PM
 */
public class TaskExecutor {

    private static Logger logger = LoggerFactory.getLogger(TaskExecutor.class);

    private ExecutorService executorService;

    private int terminationTimeoutInSeconds = 30;

    private PoolManager poolManager;

    private TaskErrorsManager taskErrorsManager;

    public void init() {
    }

    public void destroy() {
        executorService.shutdown();
        try {
            // Wait until all threads are finish
            executorService.awaitTermination(terminationTimeoutInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("await termination interrupted", e);
        }
    }

    public <T extends PoolTask> void execute(Class<T> task, TaskData taskData, PoolSettings poolSettings) {
        assert executorService != null : "executor must not be null";
        assert poolSettings != null : "pool settings must not be null";

        T command = null;
        try {
            command = task.newInstance();
            command.setPoolSettings(poolSettings);
            command.setPoolManager(poolManager);
            command.setTaskErrorsManager(taskErrorsManager);
            command.setTaskData(taskData);
        } catch (InstantiationException e) {
            logger.error("task instantiation failed", e);
        } catch (IllegalAccessException e) {
            logger.error("task instantiation failed", e);
        }

        if (command != null) {
            executorService.execute(command);
        }
    }


    public void setTerminationTimeoutInSeconds(int terminationTimeoutInSeconds) {
        this.terminationTimeoutInSeconds = terminationTimeoutInSeconds;
    }

    public void setPoolManager(PoolManager poolManager) {
        this.poolManager = poolManager;
    }

    public void setTaskErrorsManager(TaskErrorsManager taskErrorsManager) {
        this.taskErrorsManager = taskErrorsManager;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}

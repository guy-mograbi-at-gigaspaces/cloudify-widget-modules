package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.tasks.ITask;
import cloudify.widget.pool.manager.tasks.TaskConfig;
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

    private NodesDataAccessManager nodesDataAccessManager;

    private TaskErrorsDataAccessManager taskErrorsDataAccessManager;

    private StatusManager statusManager;

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

    public <T extends ITask> void execute(Class<T> task, TaskConfig taskConfig, PoolSettings poolSettings) {
        assert executorService != null : "executor must not be null";
        assert poolSettings != null : "pool settings must not be null";

        T command = null;
        try {
            command = task.newInstance();
            command.setPoolSettings(poolSettings);
            command.setNodesDataAccessManager(nodesDataAccessManager);
            command.setTaskErrorsDataAccessManager(taskErrorsDataAccessManager);
            command.setStatusManager(statusManager);
            command.setTaskConfig(taskConfig);
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

    public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
        this.nodesDataAccessManager = nodesDataAccessManager;
    }

    public void setTaskErrorsDataAccessManager(TaskErrorsDataAccessManager taskErrorsDataAccessManager) {
        this.taskErrorsDataAccessManager = taskErrorsDataAccessManager;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }
}

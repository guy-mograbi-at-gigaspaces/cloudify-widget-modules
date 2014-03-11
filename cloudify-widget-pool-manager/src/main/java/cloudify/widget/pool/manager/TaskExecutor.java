package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.tasks.*;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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

    private ListeningExecutorService executorService;

    private int terminationTimeoutInSeconds = 30;

    private NodesDataAccessManager nodesDataAccessManager;

    private ErrorsDataAccessManager errorsDataAccessManager;

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

    public <T extends Task, R> void execute(Class<T> task, TaskConfig taskConfig, PoolSettings poolSettings, TaskCallback<R> taskCallback) {
        assert executorService != null : "executor must not be null";
        assert poolSettings != null : "pool settings must not be null";

        T worker = null;
        try {
            worker = task.newInstance();
            worker.setPoolSettings(poolSettings);
            worker.setNodesDataAccessManager(nodesDataAccessManager);
            worker.setErrorsDataAccessManager(errorsDataAccessManager);
            worker.setStatusManager(statusManager);
            worker.setTaskConfig(taskConfig);
        } catch (InstantiationException e) {
            logger.error("task instantiation failed", e);
        } catch (IllegalAccessException e) {
            logger.error("task instantiation failed", e);
        }

        if (worker != null) {
            ListenableFuture listenableFuture = executorService.submit(worker);
            if (taskCallback == null) {
                taskCallback = new NoopTaskCallback();
            }
            Futures.addCallback(listenableFuture, taskCallback);
        }
    }


    public void setTerminationTimeoutInSeconds(int terminationTimeoutInSeconds) {
        this.terminationTimeoutInSeconds = terminationTimeoutInSeconds;
    }

    public void setNodesDataAccessManager(NodesDataAccessManager nodesDataAccessManager) {
        this.nodesDataAccessManager = nodesDataAccessManager;
    }

    public void setErrorsDataAccessManager(ErrorsDataAccessManager errorsDataAccessManager) {
        this.errorsDataAccessManager = errorsDataAccessManager;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = MoreExecutors.listeningDecorator(executorService);
    }

    public void setStatusManager(StatusManager statusManager) {
        this.statusManager = statusManager;
    }
}

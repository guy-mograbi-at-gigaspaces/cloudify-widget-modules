package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.tasks.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:35 PM
 */
public class ThreadPool {

    private static Logger logger = LoggerFactory.getLogger(ThreadPool.class);

    private ExecutorService executor;

    private int poolSize = 200;

    private int corePoolSize = 10;

    private int terminationTimeoutInSeconds = 10;

    public ThreadPool() {
        executor = Executors.newFixedThreadPool(poolSize);
    }

    public void init() {
        assert corePoolSize < poolSize : "core threads size must be lower than overall threads size!";
        ((ThreadPoolExecutor) executor).setCorePoolSize(corePoolSize);
    }

    public void destroy() {
        executor.shutdown();
        try {
            // Wait until all threads are finish
            executor.awaitTermination(terminationTimeoutInSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("await termination interrupted", e);
        }
    }

    public <S extends TaskResult, T extends Callable<S>> void execute(Class<T> task) {
        T worker = null;
        try {
            worker = task.newInstance();
        } catch (InstantiationException e) {
            logger.error("instantiation failed", e);
        } catch (IllegalAccessException e) {
            logger.error("instantiation failed", e);
        }


        Future<S> future = executor.submit(worker);

        try {
            future.get();
        } catch (InterruptedException e) {
            logger.error("execution interrupted", e);
        } catch (ExecutionException e) {
            logger.error("execution failed", e);
        }

    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

}

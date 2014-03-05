package cloudify.widget.pool.manager.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:32 PM
 */
public class CreateMachine<T extends TaskResult> implements Callable<T> {

    private static Logger logger = LoggerFactory.getLogger(CreateMachine.class);

    @Override
    public T call() throws Exception {
        logger.info("creating machine...");
        return null;
    }

}

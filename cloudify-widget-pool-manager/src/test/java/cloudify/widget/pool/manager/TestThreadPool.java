package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.tasks.BootstrapMachine;
import cloudify.widget.pool.manager.tasks.CreateMachine;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * User: eliranm
 * Date: 3/5/14
 * Time: 5:23 PM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:pool-manager-test-context.xml"})
@ActiveProfiles({"softlayer"})
public class TestThreadPool {

    private static Logger logger = LoggerFactory.getLogger(TestThreadPool.class);

    @Autowired
    private ThreadPool threadPool;

    @Test
    public void testPool() {

        for (int i = 0; i < 500; i++) {
            threadPool.execute(CreateMachine.class);
            threadPool.execute(BootstrapMachine.class);
        }
    }

    public void setThreadPool(ThreadPool threadPool) {
        this.threadPool = threadPool;
    }


}

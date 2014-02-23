package cloudify.widget.pool.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 4:42 AM
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:app-context.xml"})
@ActiveProfiles({"softlayer","ibmprod"})
public class TestPoolManager {

    @Autowired
    private PoolDao poolDao;

    private static Logger logger = LoggerFactory.getLogger(TestPoolManager.class);
    @Test
    public void testJdbConnection(){
        List<MachineModel> machines = poolDao.getMachines();
        logger.info("I have [{}] machines", machines);
    }

    public PoolDao getPoolDao() {
        return poolDao;
    }

    public void setPoolDao(PoolDao poolDao) {
        this.poolDao = poolDao;
    }
}

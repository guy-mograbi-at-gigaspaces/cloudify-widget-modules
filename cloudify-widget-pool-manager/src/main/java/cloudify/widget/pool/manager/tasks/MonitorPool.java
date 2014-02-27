package cloudify.widget.pool.manager.tasks;

import cloudify.widget.pool.manager.PoolMonitor;
import cloudify.widget.pool.manager.PoolStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/21/14
 * Time: 10:33 AM
 */
public class MonitorPool implements Runnable {

    private PoolMonitor poolMonitor;

    private static Logger logger = LoggerFactory.getLogger(MonitorPool.class);

    @Override
    public void run() {
        Map<String, Object> monitReport = new LinkedHashMap<String, Object>();
        PoolStatus poolStatus = poolMonitor.getPoolStatus();
        monitReport.put("report", poolStatus);
//        monitReport.put("machineModelsWithoutStatus", )
        logger.info("[{}]",poolMonitor.getPoolStatus());
    }

    public PoolMonitor getPoolMonitor() {
        return poolMonitor;
    }

    public void setPoolMonitor(PoolMonitor poolMonitor) {
        this.poolMonitor = poolMonitor;
    }
}

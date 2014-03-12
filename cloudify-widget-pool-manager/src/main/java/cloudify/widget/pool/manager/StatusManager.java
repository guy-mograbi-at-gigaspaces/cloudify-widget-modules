package cloudify.widget.pool.manager;

import cloudify.widget.pool.manager.dto.PoolSettings;
import cloudify.widget.pool.manager.dto.PoolStatus;
import cloudify.widget.pool.manager.dto.PoolStatusCount;
import cloudify.widget.pool.manager.dto.ProviderSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * User: eliranm
 * Date: 3/9/14
 * Time: 8:05 PM
 */
public class StatusManager {

    private static Logger logger = LoggerFactory.getLogger(StatusManager.class);

    @Autowired
    private NodesDataAccessManager nodesDataAccessManager;

    public void updateStatus(PoolStatus poolStatus) {
    }

    public void setStatus(PoolStatus poolStatus) {
    }

    public Collection<PoolStatus> getPoolsStatus() {

        Map< String /* poolId */, PoolStatus> resultMap = new HashMap<String, PoolStatus>();

        List<PoolStatusCount> poolStatusCounts = nodesDataAccessManager.getPoolStatusCounts();

        for (PoolStatusCount poolStatusCount : poolStatusCounts) {
            String poolId = poolStatusCount.getPoolId();
            if ( !resultMap.containsKey(poolId) ){
                PoolStatus value = new PoolStatus();
                value.setPoolId( poolId );
                resultMap.put( poolId, value);
            }

            resultMap.get(poolId).getCountPerStatus().put( poolStatusCount.getStatus(), poolStatusCount.getCount() );
        }

        return resultMap.values();
    }

    public PoolStatus getPoolStatus( PoolSettings poolSettings ){
         //TODO
        return null;
    }

}

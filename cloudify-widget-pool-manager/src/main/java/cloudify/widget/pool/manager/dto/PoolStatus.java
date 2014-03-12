package cloudify.widget.pool.manager.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:33 PM
 */
public class PoolStatus {

    public String poolId;
    public Map<NodeStatus,Integer> countPerStatus = new HashMap<NodeStatus, Integer>();

    public int getCurrentSize() {
        int size = 0;
        Map<NodeStatus, Integer> countPerStatus = getCountPerStatus();
        for (Integer integer : countPerStatus.values()) {
            size += integer;
        }
        return size;
    }

    public Map<NodeStatus, Integer> getCountPerStatus() {
        return countPerStatus;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }
}

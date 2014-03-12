package cloudify.widget.pool.manager.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 1:33 PM
 */
public class PoolStatus {

    public int currentSize;
    public String poolId;
    public Map<NodeStatus,Integer> countPerStatus = new HashMap<NodeStatus, Integer>();

    public int getCurrentSize() {
        return currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = currentSize;
    }

    public Map<NodeStatus, Integer> getCountPerStatus() {
        return countPerStatus;
    }

    public void setCountPerStatus(Map<NodeStatus, Integer> countPerStatus) {
        this.countPerStatus = countPerStatus;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }
}

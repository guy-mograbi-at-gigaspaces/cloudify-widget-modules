package cloudify.widget.pool.manager.dto;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 3/12/14
 * Time: 1:54 PM
 */
public class PoolStatusCount{
    private NodeStatus status;
    private int count;
    private String poolId;

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPoolId() {
        return poolId;
    }

    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }
}

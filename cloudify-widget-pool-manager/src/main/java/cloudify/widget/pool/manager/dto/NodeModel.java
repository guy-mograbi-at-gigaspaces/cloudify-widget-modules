package cloudify.widget.pool.manager.dto;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 6:46 PM
 */
public class NodeModel {

    public static final int INITIAL_ID = -1;

    public static enum NodeStatus {
        CREATED, BOOTSTRAPPED, READY, OCCUPIED;
    }

    public long id = INITIAL_ID;
    public String poolId;
    public NodeStatus nodeStatus;
    public String machineId;
    public String cloudifyVersion;

    public NodeModel setId(long id) {
        this.id = id;
        return this;
    }

    public NodeModel setPoolId(String poolId) {
        this.poolId = poolId;
        return this;
    }

    public NodeModel setNodeStatus(NodeStatus nodeStatus) {
        this.nodeStatus = nodeStatus;
        return this;
    }

    public NodeModel setMachineId(String machineId) {
        this.machineId = machineId;
        return this;
    }

    public NodeModel setCloudifyVersion(String cloudifyVersion) {
        this.cloudifyVersion = cloudifyVersion;
        return this;
    }

    @Override
    public String toString() {
        return "NodeModel{" +
                "id=" + id +
                ", poolId='" + poolId + '\'' +
                ", nodeStatus=" + nodeStatus +
                ", machineId='" + machineId + '\'' +
                ", cloudifyVersion='" + cloudifyVersion + '\'' +
                '}';
    }

}

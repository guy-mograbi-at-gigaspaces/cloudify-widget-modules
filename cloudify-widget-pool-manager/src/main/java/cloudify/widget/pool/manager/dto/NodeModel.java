package cloudify.widget.pool.manager.dto;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 6:46 PM
 */
public class NodeModel {

    public static enum NodeStatus {
        CREATING,BOOTSTRAPPING,READY,OCCUPIED;
    }

    public long id;
    public String poolId;
    public NodeStatus nodeStatus;
    public String machineId;
    public String cloudifyVersion;
}

package cloudify.widget.pool.manager.dto;

import cloudify.widget.api.clouds.MachineCredentials;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * User: eliranm
 * Date: 3/2/14
 * Time: 6:46 PM
 */
public class NodeModel {

    public static final int INITIAL_ID = -1;

    public long id = INITIAL_ID;
    public String poolId;
    public NodeStatus nodeStatus;
    public String machineId;
    public String machineCredentials;

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

    public NodeModel setMachineCredentials(String machineCredentials) {
        this.machineCredentials = machineCredentials;
        return this;
    }


    /**
     * This method can't be named setCredentials, or JSON mapping will fail.
     * @param machineCredentials
     * @return
     */
    public NodeModel setCredentialsFromObject(MachineCredentials machineCredentials) {
        try {
            this.machineCredentials = new ObjectMapper().writeValueAsString(machineCredentials);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }



    @Override
    public String toString() {
        return "NodeModel{" +
                "id=" + id +
                ", poolId='" + poolId + '\'' +
                ", nodeStatus=" + nodeStatus +
                ", machineId='" + machineId + '\'' +
                ", credentials='" + machineCredentials + '\'' +
                '}';
    }

}

package cloudify.widget.ec2;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.ServerIp;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudServer implements CloudServer {

    private final ComputeMetadata computeMetadata;
    private final ComputeService computeService;

    public Ec2CloudServer(ComputeService computeService, ComputeMetadata computeMetadata) {
        this.computeService = computeService;
        this.computeMetadata = computeMetadata;
    }

    @Override
    public String getId() {
        return computeMetadata.getId();
    }

    @Override
    public String getName() {
        return computeMetadata.getName();
    }

    @Override
    public boolean isRunning(){
//        return getStatus() == Ec2CloudServerStatus.RUNNING;
        // TODO implement functionality
        return false;
    }

    @Override
    public boolean isStopped(){
//        return getStatus() == Ec2CloudServerStatus.STOPPED || getStatus() == Ec2CloudServerStatus.UNRECOGNIZED;
        // TODO implement functionality
        return false;
    }

    private Ec2CloudServerStatus getStatus() {
        NodeMetadata.Status status = computeService.getNodeMetadata(computeMetadata.getId()).getStatus();
        return Ec2CloudServerStatus.fromValue(status.toString());
    }

    @Override
    public ServerIp getServerIp() {
        ServerIp serverIp = new ServerIp();
        serverIp.privateIp = computeMetadata.getProviderId();
        return serverIp;
    }
}

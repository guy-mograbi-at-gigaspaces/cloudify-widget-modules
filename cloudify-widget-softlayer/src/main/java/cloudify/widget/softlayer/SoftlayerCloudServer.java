package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerStatus;
import cloudify.widget.api.clouds.ServerIp;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.domain.ComputeMetadata;
import org.jclouds.compute.domain.NodeMetadata;

/**
 * User: eliranm
 * Date: 2/4/14
 * Time: 4:20 PM
 */
public class SoftlayerCloudServer implements CloudServer {

    private final ComputeMetadata computeMetadata;
    private final ComputeService computeService;

    public SoftlayerCloudServer(ComputeService computeService, ComputeMetadata computeMetadata) {
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
    public CloudServerStatus getStatus() {
        NodeMetadata.Status status = computeService.getNodeMetadata(computeMetadata.getId()).getStatus();
        return CloudServerStatus.fromValue(status.toString());
    }

    @Override
    public ServerIp getServerIp() {
        ServerIp serverIp = new ServerIp();
        serverIp.privateIp = computeMetadata.getProviderId();
        return serverIp;
    }
}

package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudServer;
import cloudify.widget.api.clouds.CloudServerStatus;
import cloudify.widget.api.clouds.ServerIp;
import org.jclouds.compute.domain.ComputeMetadata;

import java.util.Map;

/**
 * User: eliranm
 * Date: 2/4/14
 * Time: 4:20 PM
 */
public class SoftlayerCloudServer implements CloudServer {

    private final ComputeMetadata computeMetadata;

    public SoftlayerCloudServer (ComputeMetadata computeMetadata) {
        this.computeMetadata = computeMetadata;
    }

//    @Override
//    public Multimap<String, CloudAddress> getAddresses() {
//        return null;
//    }

    @Override
    public String getId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public CloudServerStatus getStatus() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ServerIp getServerIp() {
        ServerIp serverIp = new ServerIp();
        serverIp.privateIp = computeMetadata.getProviderId();
        return serverIp;
    }
}

package cloudify.widget.hpcloudcompute;


import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.compute.domain.NodeMetadata;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class HpCloudComputeCloudServerCreated implements CloudServerCreated {

	private final NodeMetadata newNode;

	public HpCloudComputeCloudServerCreated(NodeMetadata newNode){
		this.newNode = newNode;
	}

	public NodeMetadata getNewNode() {
		return newNode;
	}

    @Override
    public String getId() {
        return newNode.getId();
    }

	@Override
	public String toString() {
		return "HpCloudComputeCloudServerCreated [newNode=" + newNode + "], id=" + newNode.getId();
	}
}
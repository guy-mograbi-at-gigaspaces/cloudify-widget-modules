package cloudify.widget.softlayer;


import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.compute.domain.NodeMetadata;

import java.util.Set;

/**
 * Softlayer implementation of CloudServerCreated
 * @author evgenyf
 * Date: 10/7/13
 */
public class SoftlayerCloudServerCreated implements CloudServerCreated {

	private final NodeMetadata newNode;

	public SoftlayerCloudServerCreated(NodeMetadata newNode){
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
		return "SoftlayerCloudServerCreated [newNode=" + newNode + "], id=" + newNode.getId();
	}
}
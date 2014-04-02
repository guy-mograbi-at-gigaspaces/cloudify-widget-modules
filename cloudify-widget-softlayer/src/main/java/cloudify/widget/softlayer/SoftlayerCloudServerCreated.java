package cloudify.widget.softlayer;


import cloudify.widget.api.clouds.CloudServerCreated;
import cloudify.widget.api.clouds.MachineCredentials;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.LoginCredentials;

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
    public MachineCredentials getCredentials() {
        LoginCredentials loginCredentials = newNode.getCredentials();
        if (loginCredentials != null) {
            return new MachineCredentials()
                    .setUser(loginCredentials.getUser())
                    .setPassword(loginCredentials.getPassword())
                    .setPrivateKey(loginCredentials.getPrivateKey());
        }
        return null;
    }

    @Override
	public String toString() {
		return "SoftlayerCloudServerCreated [newNode=" + newNode + "], id=" + newNode.getId();
	}
}
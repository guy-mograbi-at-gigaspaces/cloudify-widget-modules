package cloudify.widget.hpcloud;


import cloudify.widget.api.clouds.CloudServerCreated;
import org.jclouds.openstack.nova.v2_0.domain.ServerCreated;

/**
 * HP implementation of CloudServerCreated
 * @author evgenyf
 * Date: 10/7/13
 */
public class HpCloudServerCreated implements CloudServerCreated {

	private final ServerCreated serverCreated;

	public HpCloudServerCreated( ServerCreated serverCreated ){
		this.serverCreated = serverCreated;
	}

	@Override
	public String getId() {
		return serverCreated.getId();
	}

	@Override
	public String toString() {
		return "HpCloudServerCreated [serverCreated=" + serverCreated + "]";
	}
}
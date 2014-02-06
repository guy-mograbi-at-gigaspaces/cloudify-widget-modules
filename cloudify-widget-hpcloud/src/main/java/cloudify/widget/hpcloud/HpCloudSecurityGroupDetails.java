package cloudify.widget.hpcloud;

import cloudify.widget.api.clouds.ISecurityGroupDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 3:05 PM
 */
public class HpCloudSecurityGroupDetails implements ISecurityGroupDetails{
    public String securityGroup;
    public String zoneName;

    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
}

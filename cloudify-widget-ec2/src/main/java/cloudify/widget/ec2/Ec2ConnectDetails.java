package cloudify.widget.ec2;

import cloudify.widget.api.clouds.IConnectDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 10:31 AM
 */
public class Ec2ConnectDetails implements IConnectDetails {
    private String accessId;
    private String secretAccessKey;

    public Ec2ConnectDetails(String accessId, String secretAccessKey) {
        this.accessId = accessId;
        this.secretAccessKey = secretAccessKey;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getSecretAccessKey() {
        return secretAccessKey;
    }

    public void setSecretAccessKey(String secretAccessKey) {
        this.secretAccessKey = secretAccessKey;
    }
}

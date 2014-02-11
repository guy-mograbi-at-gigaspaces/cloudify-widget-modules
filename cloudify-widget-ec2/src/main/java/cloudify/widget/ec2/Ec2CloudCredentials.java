package cloudify.widget.ec2;

import cloudify.widget.api.clouds.CloudCredentials;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class Ec2CloudCredentials implements CloudCredentials {

    private String accessId;
    private String secretAccessKey;

    public Ec2CloudCredentials(String accessId, String secretAccessKey) {
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

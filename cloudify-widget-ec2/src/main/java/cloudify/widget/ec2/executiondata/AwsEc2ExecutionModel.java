package cloudify.widget.ec2.executiondata;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/15/14
 * Time: 10:25 AM
 */
public class AwsEc2ExecutionModel {

    public String imageId;
    public String endpoint;

    public String keyPairName;
    public String keyPairContent;

    public String securityGroupName;

    public String getSecurityGroupName() {
        return securityGroupName;
    }

    public void setSecurityGroupName(String securityGroupName) {
        this.securityGroupName = securityGroupName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getKeyPairName() {
        return keyPairName;
    }

    public void setKeyPairName(String keyPairName) {
        this.keyPairName = keyPairName;
    }

    public String getKeyPairContent() {
        return keyPairContent;
    }

    public void setKeyPairContent(String keyPairContent) {
        this.keyPairContent = keyPairContent;
    }
}

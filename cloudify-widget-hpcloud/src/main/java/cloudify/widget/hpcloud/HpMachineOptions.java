package cloudify.widget.hpcloud;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 3:13 PM
 *
 * Guy - here are some flags we need to have
 *
 *
 * public String serverNamePrefix="cloudify_pool_server";
 public String zoneName="az-1.region-a.geo-l";
 public String keyPair="cloudify";
 public String securityGroup="default";
 public String flavorId="102";
 public String imageId="1358";
 public SshConfiguration ssh = new SshConfiguration();
 public String apiKey="<HP cloud Password>";
 public String username="<tenant>:<user>";

 public ApiCredentials api = new ApiCredentials();
 *
 *
 *
 */
public class HpMachineOptions {
    public String name;
    public String imageRef;
    public String flavorRef;
    public String tag;
    public String keyPair;
    public String securityGroup;

    public String getSecurityGroup() {
        return securityGroup;
    }

    public void setSecurityGroup(String securityGroup) {
        this.securityGroup = securityGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getFlavorRef() {
        return flavorRef;
    }

    public void setFlavorRef(String flavorRef) {
        this.flavorRef = flavorRef;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }
}

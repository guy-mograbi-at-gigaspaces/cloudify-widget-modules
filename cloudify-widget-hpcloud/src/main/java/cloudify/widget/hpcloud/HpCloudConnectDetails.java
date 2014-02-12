package cloudify.widget.hpcloud;

import cloudify.widget.api.clouds.IConnectDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/6/14
 * Time: 3:24 PM
 */
public class HpCloudConnectDetails implements IConnectDetails{
    public String project;
    public String key;
    public String secretKey;
    public boolean apiCredentials;
    public String zone;

    public HpCloudConnectDetails() {
    }

    public HpCloudConnectDetails setProject(String project) {
        this.project = project;
        return this;
    }

    // defaults until we have better clouds support.
//    public void init(){
//        this.zone = conf.server.cloudBootstrap.zoneName; //default. this should be given in the API call evetually.
//        apiCredentials = true;
//        cloudProvider = CloudProvider.HP;
//    }

    public HpCloudConnectDetails setKey(String key) {
        this.key = key;
        return this;
    }

    public HpCloudConnectDetails setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }

    public String getProject() {
        return project;
    }

    public String getKey() {
        return key;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public HpCloudConnectDetails setApiCredentials(boolean apiCredentials) {
        this.apiCredentials = apiCredentials;
        return this;
    }

    public HpCloudConnectDetails setZone(String zone) {
        this.zone = zone;
        return this;
    }

    public String getIdentity() {
        return project + ":" + key;
    }

    public String getCredential() {
        return secretKey;
    }

    public String toString() {
        return "NovaCloudCredentials{" +
                "project='" + project + '\'' +
                ", key='" + key + '\'' +
                ", secretKey='***'" +
                ", apiCredentials=" + apiCredentials +
                ", zone='" + zone + '\'' +
                '}';
    }

}

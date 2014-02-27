package cloudify.widget.hpcloudcompute;

import cloudify.widget.api.clouds.IConnectDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 10:31 AM
 */
public class HpCloudComputeConnectDetails implements IConnectDetails {

    private String user;
    private String project;
    private String key;
    private String secretKey;
    private String password;

    public HpCloudComputeConnectDetails() {}

    public HpCloudComputeConnectDetails( String user, String project, String key, String secretKey, String password ) {
        this.user = user;
        this.project = project;
        this.key = key;
        this.secretKey = secretKey;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setKeyFile(String secretFile) {
        this.secretKey = secretKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

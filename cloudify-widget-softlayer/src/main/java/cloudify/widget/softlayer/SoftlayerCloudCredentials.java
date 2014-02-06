package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.CloudCredentials;

/**
 * User: eliranm
 * Date: 2/6/14
 * Time: 8:49 PM
 */
public class SoftlayerCloudCredentials implements CloudCredentials {

    private String user;
    private String apiKey;

    public SoftlayerCloudCredentials(String user, String apiKey) {
        this.user = user;
        this.apiKey = apiKey;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}

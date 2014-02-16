package cloudify.widget.cli.softlayer;

import cloudify.widget.cli.ICloudBootstrapDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/13/14
 * Time: 1:02 PM
 */
public class SoftlayerCloudBootstrapDetails implements ICloudBootstrapDetails{
    private String username;
    private String apiKey;



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public String toString() {
        return "SoftlayerCloudBootstrapDetails{" +
                "username='" + username + '\'' +
                ", apiKey='***'" +
                '}';
    }
}

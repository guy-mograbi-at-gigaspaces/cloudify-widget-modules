package cloudify.widget.softlayer;

import cloudify.widget.api.clouds.IConnectDetails;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/11/14
 * Time: 10:31 AM
 */
public class SoftlayerConnectDetails implements IConnectDetails {
    public String username;
    public String key;
    public boolean isApiKey;

    public String getUsername() {
        return username;
    }

    public SoftlayerConnectDetails setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getKey() {
        return key;
    }

    public SoftlayerConnectDetails setKey(String key) {
        this.key = key;
        return this;
    }

    public boolean isApiKey() {
        return isApiKey;
    }

    public SoftlayerConnectDetails setApiKey(boolean isApiKey) {
        this.isApiKey = isApiKey;
        return this;
    }
}

package cloudify.widget.cli.softlayer;


import cloudify.widget.common.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 2/13/14
 * Time: 1:02 PM
 */
public class SoftlayerCloudBootstrapDetails extends AbstractCloudBootstrapDetails{
    private String username;

    private String apiKey;


    public SoftlayerCloudBootstrapDetails() {

    }

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
    public Collection<String> getProperties() {
        Collection<String> newLines = new LinkedList<String>();
        newLines.add("user=" + StringUtils.wrapWithQuotes(username));
        newLines.add("apiKey=" + StringUtils.wrapWithQuotes(apiKey));
        return newLines;
    }

    @Override
    public String toString() {
        return "SoftlayerCloudBootstrapDetails{" +
                "username='" + username + '\'' +
                ", apiKey='" + apiKey + '\'' +
                "} " + super.toString();
    }
}

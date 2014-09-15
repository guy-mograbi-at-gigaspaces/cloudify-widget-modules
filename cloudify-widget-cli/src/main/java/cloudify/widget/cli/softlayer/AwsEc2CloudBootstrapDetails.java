package cloudify.widget.cli.softlayer;

import cloudify.widget.common.StringUtils;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: guym
 * Date: 9/10/14
 * Time: 7:00 PM
 */
public class AwsEc2CloudBootstrapDetails extends AbstractCloudBootstrapDetails {
    private String key;
    private String secretKey;
    private String keyFile;
    private String keyPair;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getKeyFile() {
        return keyFile;
    }

    public void setKeyFile(String keyFile) {
        this.keyFile = keyFile;
    }

    public String getKeyPair() {
        return keyPair;
    }

    public void setKeyPair(String keyPair) {
        this.keyPair = keyPair;
    }

    @Override
        public Collection<String> getProperties() {
            Collection<String> newLines = new LinkedList<String>();
            newLines.add("user=" + StringUtils.wrapWithQuotes(key));
            newLines.add("apiKey=" + StringUtils.wrapWithQuotes(secretKey));
            newLines.add("keyFile=" + StringUtils.wrapWithQuotes(keyFile));
            newLines.add("keyPair=" + StringUtils.wrapWithQuotes(keyPair));
            return newLines;
        }

    @Override
    public String toString() {
        return "AwsEc2CloudBootstrapDetails{" +
                "key='" + key + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", keyFile='" + keyFile + '\'' +
                ", keyPair='" + keyPair + '\'' +
                "} " + super.toString();
    }
}

package cloudify.widget.hpcloudcompute;

import cloudify.widget.api.clouds.ISshDetails;

/**
 * User: evgeny
 * Date: 2/10/14
 * Time: 6:55 PM
 */
public class HpCloudComputeSshDetails implements ISshDetails {

    private final int port;
    private final String user;
    private final String privateKey;

    public HpCloudComputeSshDetails(int port, String user, String privateKey){
        this.port = port;
        this.user = user;
        this.privateKey = privateKey;
    }

    public int port(){
        return port;
    }

    public String user(){
        return user;
    }

    public String privateKey(){
        return privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HpCloudComputeSshDetails that = (HpCloudComputeSshDetails) o;

        if (port != that.port) return false;
        if (privateKey != null ? !privateKey.equals(that.privateKey) : that.privateKey != null) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = port;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (privateKey != null ? privateKey.hashCode() : 0);
        return result;
    }
}
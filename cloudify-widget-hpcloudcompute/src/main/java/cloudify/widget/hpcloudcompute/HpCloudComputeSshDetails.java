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
}